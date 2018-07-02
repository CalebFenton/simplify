package org.cf.simplify.strategy;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.*;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DeadRemovalStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategy.class.getSimpleName());

    private final ExecutionGraphManipulator manipulator;
    private List<Integer> addresses;
    private Set<Integer> exceptionHandlingAddresses;
    private int unusedAssignmentCount;
    private int uselessBranchCount;
    private int unvisitedCount;
    private int nopCount;
    private int unusedResultCount;
    private SideEffect.Level sideEffectThreshold = SideEffect.Level.NONE;

    public DeadRemovalStrategy(ExecutionGraphManipulator manipulator) {
        this.manipulator = manipulator;
        addresses = getValidAddresses(manipulator);
        exceptionHandlingAddresses = getExceptionHandlerAddresses(manipulator);
        unusedAssignmentCount = 0;
        uselessBranchCount = 0;
        unvisitedCount = 0;
        unusedResultCount = 0;
        nopCount = 0;
    }

    private static Set<Integer> getExceptionHandlerAddresses(ExecutionGraphManipulator manipulator) {
        int[] allAddresses = manipulator.getAddresses();
        Arrays.sort(allAddresses);
        int highestAddress = allAddresses[allAddresses.length - 1];
        Set<Integer> handlerAddresses = new HashSet<>();
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

    private static Set<Integer> getNormalRegistersAssigned(MethodState mState) {
        Set<Integer> assigned = new HashSet<>();
        for (int register : mState.getRegistersAssigned()) {
            if (register < 0) {
                continue;
            }
            assigned.add(register);
        }

        for (int i = 0; i < mState.getParameterCount(); i++) {
            int parameterRegister = mState.getParameterStart() + i;
            assigned.remove(parameterRegister);
        }

        return assigned;
    }

    private static boolean isAnyRegisterUsed(int address, Set<Integer> registers, ExecutionGraphManipulator graph) {
        List<ExecutionNode> children = graph.getChildren(address);
        for (ExecutionNode child : children) {
            Set<Integer> newRegisters = new HashSet<>(registers);
            if (isAnyRegisterUsed(address, newRegisters, graph, child)) {
                return true;
            }
        }

        return false;
    }

    /***
     * Check if the values in a set of registers is used (read) at or after a certain address.
     * @param address
     * @param usedRegisters
     * @param graph
     * @param node
     * @return
     */
    private static boolean isAnyRegisterUsed(int address, Set<Integer> usedRegisters, ExecutionGraphManipulator graph,
                                             ExecutionNode node) {
        ExecutionNode current = node;
        Set<Integer> reassignedRegisters = new HashSet<Integer>();
        for (; ; ) {
            MethodState mState = current.getContext().getMethodState();
            for (int register : usedRegisters) {
                // Need to check if read before checking if assigned because some ops may read & assign to the same register, e.g. add-int/2addr v0, v0
                if (mState.wasRegisterRead(register)) {
                    if (log.isTraceEnabled()) {
                        log.trace("r{} read after {} @{} with {}", register, address, current.getAddress(),
                                current.getOp());
                    }

                    return true;
                }
                // aput mutates an object. Assignment isn't "reassignment" like it is with other ops
                if (mState.wasRegisterAssigned(register)) {
                    if (current.getOp() instanceof APutOp) {
                        // aput* ops mutate the value at the target register, they don't reassign to a new object, so don't count it as reassignment
                        continue;
                    }

                    if (log.isTraceEnabled()) {
                        log.trace("r{} assigned after {} @{} with {}", register, address, current.getAddress(),
                                current.getOp());
                    }

                    // This register has been reassigned so the original value isn't accessible from this register anymore.
                    reassignedRegisters.add(register);
                }
            }

            usedRegisters.removeAll(reassignedRegisters);
            if (usedRegisters.isEmpty()) {
                return false;
            }

            List<ExecutionNode> children = current.getChildren();
            if (children.size() == 1) {
                current = children.get(0);
            } else {
                for (ExecutionNode child : children) {
                    Set<Integer> newRegisters = new HashSet<>(usedRegisters);
                    if (isAnyRegisterUsed(address, newRegisters, graph, child)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("dead ops removed", unvisitedCount);
        counts.put("dead assignments removed", unusedAssignmentCount);
        counts.put("dead results removed", unusedResultCount);
        counts.put("useless gotos removed", uselessBranchCount);
        counts.put("nops removed", nopCount);

        return counts;
    }

    @Override
    public boolean perform() {
        // Updated addresses each time because they change outside of this method.
        addresses = getValidAddresses(manipulator);
        exceptionHandlingAddresses = getExceptionHandlerAddresses(manipulator);

        Set<Integer> removeSet = new HashSet<>();
        List<Integer> removeAddresses;
        removeAddresses = getDeadAddresses();
        unvisitedCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getDeadAssignmentAddresses();
        unusedAssignmentCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getDeadResultAddresses();
        unusedResultCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getUselessBranchAddresses();
        uselessBranchCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getNopAddresses();
        nopCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        List<Integer> deadAddresses = new LinkedList<>(removeSet);
        if (deadAddresses.size() > 0) {
            manipulator.removeInstructions(deadAddresses);
        }

        return !removeSet.isEmpty();
    }

    public void setRemoveWeak(boolean removeWeak) {
        if (removeWeak) {
            sideEffectThreshold = SideEffect.Level.WEAK;
        }
    }

    List<Integer> getDeadAddresses() {
        return addresses.stream().filter(this::isDead).collect(Collectors.toList());
    }

    List<Integer> getDeadAssignmentAddresses() {
        return addresses.stream().filter(this::isDeadAssignment).collect(Collectors.toList());
    }

    List<Integer> getDeadResultAddresses() {
        return addresses.stream().filter(this::isDeadResult).collect(Collectors.toList());
    }

    List<Integer> getImpotentMethodInvocations() {
        return addresses.stream().filter(this::isImpotentMethodInvocation).collect(Collectors.toList());
    }

    List<Integer> getNopAddresses() {
        return addresses.stream().filter(this::isNop).collect(Collectors.toList());
    }

    List<Integer> getUselessBranchAddresses() {
        return addresses.stream().filter(this::isUselessBranch).collect(Collectors.toList());
    }

    List<Integer> getValidAddresses(ExecutionGraphManipulator manipulator) {
        List<Integer> validAddresses = IntStream.of(manipulator.getAddresses()).boxed().collect(Collectors.toList());
        List<Integer> invalidAddresses = new LinkedList<>();

        // Should never remove the last op. It's either return, goto, or an array payload.
        invalidAddresses.add(validAddresses.get(validAddresses.size() - 1));

        for (int address : validAddresses) {
            if (!manipulator.wasAddressReached(address)) {
                // Unreached code is valid for removal
                continue;
            }

            Op op = manipulator.getOp(address);
            if (isSideEffectAboveThreshold(op.getSideEffectLevel())) {
                invalidAddresses.add(address);
                continue;
            }

            if (op.getName().startsWith("invoke-direct")) {
                if (manipulator.getMethod().getSignature().contains(";-><init>(")) {
                    // Can't remove init method without breaking the object
                    ExecutionNode node = manipulator.getNodePile(address).get(0);
                    ExecutionContext context = node.getContext();
                    MethodState mState = context.getMethodState();

                    StringBuilder sb = new StringBuilder("invoke-direct {r");
                    sb.append(mState.getParameterStart() - 1); // p0 for instance method
                    if (op.toString().startsWith(sb.toString())) {
                        invalidAddresses.add(address);
                        continue;
                    }
                }
            }
        }
        validAddresses.removeAll(invalidAddresses);

        return validAddresses;
    }

    private boolean isDead(int address) {
        Op op = manipulator.getOp(address);
        log.debug("Dead test @{} for: {}", address, op);

        if (exceptionHandlingAddresses.contains(address)) {
            // If virtual execution was perfect, unvisited exception handling code could safely be removed provided
            // you also removed the try/catch but that level of accuracy is difficult. Instead, compromise by not
            // removing unvisited code, but try and remove for other reasons such as dead assignment.
            return false;
        }

        if (manipulator.wasAddressReached(address)) {
            return false;
        }

        if (op instanceof GotoOp) {
            // These are handled specifically by isUselessBranch
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

    private boolean isDeadAssignment(int address) {
        if (!manipulator.wasAddressReached(address)) {
            return false;
        }

        ExecutionNode node = manipulator.getNodePile(address).get(0);
        ExecutionContext context = node.getContext();
        if (context == null) {
            if (log.isWarnEnabled()) {
                log.warn("Null execution context @{}. This shouldn't happen!", address);
            }

            return false;
        }

        MethodState mState = context.getMethodState();
        Set<Integer> assigned = getNormalRegistersAssigned(mState);
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
        ExecutionContext context = node.getContext();
        MethodState mState = context.getMethodState();
        Set<Integer> assigned = getNormalRegistersAssigned(mState);
        if (0 < assigned.size()) {
            if (isAnyRegisterUsed(address, assigned, manipulator)) {
                // Result may not be used, but assignments *are* used
                return false;
            }
        }

        return true;
    }

    private boolean isImpotentMethodInvocation(int address) {
        return false;
    }

    private boolean isNop(int address) {
        if (!manipulator.wasAddressReached(address)) {
            return false;
        }

        Op op = manipulator.getOp(address);
        if (op instanceof NopOp) {
            return true;
        }

        return false;
    }

    private boolean isSideEffectAboveThreshold(SideEffect.Level level) {
        return level.compareTo(sideEffectThreshold) > 0;
    }

    private boolean isUselessBranch(int address) {
        Op op = manipulator.getOp(address);
        if (!(op instanceof GotoOp)) {
            return false;
        }

        // Branch is useless if it branches to the next instruction.
        OffsetInstruction instruction = (OffsetInstruction) manipulator.getInstruction(address);
        int branchOffset = instruction.getCodeOffset();
        if (branchOffset != instruction.getCodeUnits()) {
            return false;
        }

        return true;
    }

}
