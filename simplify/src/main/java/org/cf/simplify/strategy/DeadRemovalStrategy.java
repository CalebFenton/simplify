package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.APutOp;
import org.cf.smalivm.opcode.GotoOp;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.NopOp;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

public class DeadRemovalStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategy.class.getSimpleName());

    private static TIntSet getExceptionHandlerAddresses(ExecutionGraphManipulator manipulator) {
        int[] allAddresses = manipulator.getAddresses();
        Arrays.sort(allAddresses);
        int highestAddress = allAddresses[allAddresses.length - 1];
        TIntSet handlerAddresses = new TIntHashSet();
        List<BuilderTryBlock> tryBlocks = manipulator.getTryBlocks();
        for (BuilderTryBlock tryBlock : tryBlocks) {
            List<? extends BuilderExceptionHandler> handlers = tryBlock.getExceptionHandlers();
            for (BuilderExceptionHandler handler : handlers) {
                int address = handler.getHandlerCodeAddress();
                BuilderInstruction instruction = manipulator.getInstruction(address);
                while (address < highestAddress) {
                    // Add all instructions until return, goto, etc.
                    handlerAddresses.add(address);
                    address += instruction.getCodeUnits();
                    instruction = manipulator.getInstruction(address);
                    if (!instruction.getOpcode().canContinue()) {
                        break;
                    }
                }
            }
        }

        return handlerAddresses;
    }

    private static TIntSet getNormalRegistersAssigned(MethodState mState) {
        TIntSet assigned = new TIntHashSet(mState.getRegistersAssigned());

        // The state of these registers can become invalid as we start to remove ops.
        // Infer if the result is used, etc. by means other than looking if the register
        // itself is read.
        assigned.removeAll(new int[] {
                        MethodState.ResultRegister, MethodState.ReturnAddress, MethodState.ReturnRegister });

        for (int i = 0; i < mState.getParameterCount(); i++) {
            int parameterRegister = mState.getParameterStart() + i;
            assigned.remove(parameterRegister);
        }

        return assigned;
    }

    private static boolean isAnyRegisterUsed(int address, TIntSet registers, ExecutionGraphManipulator graph) {
        List<ExecutionNode> children = graph.getChildren(address);
        for (ExecutionNode child : children) {
            TIntSet newRegisters = new TIntHashSet(registers);
            if (isAnyRegisterUsed(address, newRegisters, graph, child)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAnyRegisterUsed(int address, TIntSet registers, ExecutionGraphManipulator graph,
                    ExecutionNode node) {
        ExecutionNode current = node;
        for (;;) {
            MethodState mState = current.getContext().getMethodState();
            for (int register : registers.toArray()) {
                // Some ops read from and assign to the same register, e.g add-int/2addr v0, v0
                // Read check must come first because this still counts as a usage.
                if (mState.wasRegisterRead(register)) {
                    if (log.isTraceEnabled()) {
                        log.trace("r{} read after {} @{}, {}", register, address, current.getAddress(), current.getOp());
                    }

                    return true;
                }
                // aput is mutates an object. Assignment isn't "reassignment" like it is with other ops
                else if (mState.wasRegisterAssigned(register) && !(current.getOp() instanceof APutOp)) {
                    if (log.isTraceEnabled()) {
                        log.trace("r{} assigned after {} @{}, {}", register, address, current.getAddress(),
                                        current.getOp());
                    }

                    registers.remove(register);
                }
            }

            if (registers.isEmpty()) {
                return false;
            }

            List<ExecutionNode> children = current.getChildren();
            if (children.isEmpty()) {
                return false;
            }

            if (children.size() == 1) {
                current = children.get(0);
            } else {
                for (ExecutionNode child : children) {
                    TIntSet newRegisters = new TIntHashSet(registers);
                    if (isAnyRegisterUsed(address, newRegisters, graph, child)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private TIntList addresses;
    private int unusedAssignmentCount;
    private int uselessBranchCount;
    private int unvisitedCount;
    private int nopCount;

    private int unusedResultCount;

    private SideEffect.Level sideEffectThreshold = SideEffect.Level.NONE;

    private final ExecutionGraphManipulator manipulator;

    public DeadRemovalStrategy(ExecutionGraphManipulator manipulator) {
        this.manipulator = manipulator;
        addresses = getValidAddresses(manipulator);
        unusedAssignmentCount = 0;
        uselessBranchCount = 0;
        unvisitedCount = 0;
        unusedResultCount = 0;
        nopCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("unvisited", unvisitedCount);
        result.put("unusedAssignment", unusedAssignmentCount);
        result.put("unusedResult", unusedResultCount);
        result.put("uselessBranch", uselessBranchCount);
        result.put("nops", nopCount);

        return result;
    }

    @Override
    public boolean perform() {
        // Updated addresses each time because they change outside of this method.
        addresses = getValidAddresses(manipulator);

        TIntSet removeSet = new TIntHashSet();
        TIntList removeAddresses;
        removeAddresses = getUnusedAddresses();
        unvisitedCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getUnusedAssignmentAddresses();
        unusedAssignmentCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getUnusedResultAddresses();
        unusedResultCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getUselessBranchAddresses();
        uselessBranchCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getNopAddresses();
        nopCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = new TIntArrayList(removeSet.toArray());
        manipulator.removeInstructions(Ints.asList(removeSet.toArray()));

        return removeAddresses.size() > 0;
    }

    public void setRemoveWeak(boolean removeWeak) {
        if (removeWeak) {
            sideEffectThreshold = SideEffect.Level.WEAK;
        }
    }

    private boolean isDeadAssignment(int address) {
        if (!manipulator.wasAddressReached(address)) {
            return false;
        }

        ExecutionNode node = manipulator.getNodePile(address).get(0);
        ExecutionContext ectx = node.getContext();
        if (ectx == null) {
            if (log.isWarnEnabled()) {
                log.warn("Null execution context @" + address + ". This shouldn't happen!");
            }

            return false;
        }

        MethodState mState = ectx.getMethodState();
        TIntSet assigned = getNormalRegistersAssigned(mState);
        if (assigned.isEmpty()) {
            // Has no assignments at all
            return false;
        }

        Op op = manipulator.getOp(address);
        if (isSideEffectAboveThreshold(op.getSideEffectLevel())) {
            return false;
        }

        if (op instanceof InvokeOp) {
            String returnType = ((InvokeOp) op).getReturnType();
            if (!"V".equals(returnType)) {
                // Handled by dead result
                return false;
            }
        }

        log.debug("Dead assignments test @{} for: {}", address, op);
        if (isAnyRegisterUsed(address, assigned, manipulator)) {
            return false;
        }

        return true;
    }

    private boolean isDeadResult(int address) {
        if (!manipulator.wasAddressReached(address)) {
            return false;
        }

        Op op = manipulator.getOp(address);
        if (!(op instanceof InvokeOp)) {
            return false;
        }

        log.debug("Dead result test @{} for: {}", address, op);
        if (isSideEffectAboveThreshold(op.getSideEffectLevel())) {
            return false;
        }

        String returnType = ((InvokeOp) op).getReturnType();
        if ("V".equals(returnType)) {
            return false;
        }

        BuilderInstruction instruction = manipulator.getInstruction(address);
        int nextAddress = address + instruction.getCodeUnits();
        BuilderInstruction nextInstr = manipulator.getInstruction(nextAddress);
        if (nextInstr == null) {
            return false;
        }

        if (nextInstr.getOpcode().name.startsWith("move-result")) {
            // The result is at least mapped to a normal register
            return false;
        }

        ExecutionNode node = manipulator.getNodePile(address).get(0);
        ExecutionContext ectx = node.getContext();
        MethodState mState = ectx.getMethodState();
        TIntSet assigned = getNormalRegistersAssigned(mState);
        if (0 < assigned.size()) {
            if (isAnyRegisterUsed(address, assigned, manipulator)) {
                // Result may not be used, but assignments *are* used
                return false;
            }
        }

        return true;
    }

    private boolean isSideEffectAboveThreshold(SideEffect.Level level) {
        return level.compareTo(sideEffectThreshold) > 0;
    }

    boolean isDead(int address) {
        Op op = manipulator.getOp(address);
        log.debug("Dead test @{} for: {}", address, op);

        if (manipulator.wasAddressReached(address)) {
            return false;
        }

        if (op instanceof GotoOp) {
            // Let dead branch logic handle these.
            // Getting weird errors about unplaced labels if these are removed.
            // In that case, it was jumping to a exception handler.
            return false;
        }

        if (op instanceof NopOp) {
            int nextAddress = address + op.getLocation().getInstruction().getCodeUnits();
            Opcode nextOp = manipulator.getLocation(nextAddress).getInstruction().getOpcode();
            if (nextOp == Opcode.ARRAY_PAYLOAD) {
                // Necessary nop padding
                return false;
            }
        }

        return true;
    }

    TIntList getUnusedAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (isDead(address)) {
                result.add(address);
            }
        }

        return result;
    }

    TIntList getUnusedAssignmentAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (isDeadAssignment(address)) {
                result.add(address);
            }
        }

        return result;
    }

    TIntList getUnusedResultAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (isDeadResult(address)) {
                result.add(address);
            }
        }

        return result;
    }

    TIntList getUselessBranchAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            Op op = manipulator.getOp(address);
            if (!(op instanceof GotoOp)) {
                continue;
            }

            // Branch is useless if it branches to the next instruction.
            OffsetInstruction instruction = (OffsetInstruction) manipulator.getInstruction(address);
            int branchOffset = instruction.getCodeOffset();
            if (branchOffset != instruction.getCodeUnits()) {
                continue;
            }

            result.add(address);
        }

        return result;
    }

    TIntList getNopAddresses() {
        TIntList nopAddresses = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (!manipulator.wasAddressReached(address)) {
                continue;
            }
            Op op = manipulator.getOp(address);
            if (op instanceof NopOp) {
                nopAddresses.add(address);
            }
        }

        return nopAddresses;
    }

    TIntList getValidAddresses(ExecutionGraphManipulator manipulator) {
        TIntList validAddresses = new TIntArrayList(manipulator.getAddresses());
        // Keep the last address. It's a hack. Last op is normally a return, goto, etc.
        // Though could be array-payload (but we don't check, hence hack)
        validAddresses.sort();
        validAddresses.removeAt(validAddresses.size() - 1);

        // For now, don't remove any exception handler code until VM actually understands them.
        validAddresses.removeAll(getExceptionHandlerAddresses(manipulator));

        for (int address : validAddresses.toArray()) {
            if (!manipulator.wasAddressReached(address)) {
                // DEAD, totally valid
                continue;
            }

            Op op = manipulator.getOp(address);
            if (isSideEffectAboveThreshold(op.getSideEffectLevel())) {
                validAddresses.remove(address);
                continue;
            }

            if (op instanceof NopOp) {
                // Usually, the only reason a nop exists is because it was generated by the compiler.
                // Most decompilers are smart enough to deal with them.
                if (!manipulator.wasAddressReached(address)) {
                    // Wasn't reached, so must be a padding op. Dexlib will remove this if necessary.
                    validAddresses.remove(address);
                    continue;
                }
            }

            if (op.getName().startsWith("invoke-direct")) {
                // Initializers shouldn't be removed because they setup instance state.
                if (manipulator.getMethodDescriptor().contains(";-><init>(")) {
                    ExecutionNode node = manipulator.getNodePile(address).get(0);
                    ExecutionContext ectx = node.getContext();
                    MethodState mState = ectx.getMethodState();

                    StringBuilder sb = new StringBuilder("invoke-direct {r");
                    sb.append(mState.getParameterStart() - 1); // p0 for instance method
                    if (op.toString().startsWith(sb.toString())) {
                        validAddresses.remove(address);
                        continue;
                    }
                }
            }
        }

        return validAddresses;
    }

}
