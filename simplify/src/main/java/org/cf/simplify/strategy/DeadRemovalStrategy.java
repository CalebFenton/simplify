package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.APutOp;
import org.cf.smalivm.opcode.GotoOp;
import org.cf.smalivm.opcode.InvokeOp;
import org.cf.smalivm.opcode.NopOp;
import org.cf.smalivm.opcode.Op;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadRemovalStrategy implements OptimizationStrategy {

    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategy.class.getSimpleName());

    private static final SideEffect.Level SIDE_EFFECT_THRESHOLD = SideEffect.Level.WEAK;

    private static boolean areRegistersUsed(int address, TIntList registerList, MethodBackedGraph graph) {
        Deque<ExecutionNode> stack = new ArrayDeque<ExecutionNode>(graph.getChildren(address));
        ExecutionNode node;
        int[] registers = registerList.toArray();
        while ((node = stack.poll()) != null) {
            MethodState mState = node.getContext().getMethodState();
            for (int register : registers) {
                // Some ops read from and assign to the same register, e.g add-int/2addr v0, v0
                // Read check must come first because this still counts as a usage.
                if (mState.wasRegisterRead(register)) {
                    if (log.isTraceEnabled()) {
                        log.trace("r" + register + " is read after " + address + " @ " + node.getAddress() + ", "
                                        + node.getOp());
                    }

                    return true;
                }
                // aput is mutates an object. Assignment isn't "reassignment" like it is with other ops
                else if (mState.wasRegisterAssigned(register) && !(node.getOp() instanceof APutOp)) {
                    if (log.isTraceEnabled()) {
                        log.trace("r" + register + " is reassigned after " + address + " @ " + node.getAddress() + ", "
                                        + node.getOp());
                    }

                    return false;
                }
            }
            stack.addAll(node.getChildren());
        }

        return false;
    }

    private TIntList addresses;
    private int deadAssignmentCount;
    private int deadBranchCount;
    private int deadCount;
    private int deadResultCount;

    private final MethodBackedGraph mbgraph;

    public DeadRemovalStrategy(MethodBackedGraph mbgraph) {
        this.mbgraph = mbgraph;
        addresses = getValidAddresses(mbgraph);
        deadAssignmentCount = 0;
        deadBranchCount = 0;
        deadCount = 0;
        deadResultCount = 0;
    }

    @Override
    public Map<String, Integer> getOptimizationCounts() {
        Map<String, Integer> result = new HashMap<String, Integer>();
        result.put("dead", deadCount);
        result.put("deadAssignment", deadAssignmentCount);
        result.put("deadResult", deadResultCount);
        result.put("deadBranch", deadBranchCount);

        return result;
    }

    @Override
    public boolean perform() {
        // Updated addresses each time because they change outside of this method.
        addresses = getValidAddresses(mbgraph);

        TIntSet removeSet = new TIntHashSet();
        TIntList removeAddresses;
        removeAddresses = getDeadAddresses();
        deadCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getDeadAssignmentAddresses();
        deadAssignmentCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getDeadResultAddresses();
        deadResultCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = getUselessBranchAddresses();
        deadBranchCount += removeAddresses.size();
        removeSet.addAll(removeAddresses);

        removeAddresses = new TIntArrayList(removeSet.toArray());
        mbgraph.removeInstructions(removeAddresses);
        addresses.removeAll(removeAddresses);

        return removeAddresses.size() > 0;
    }

    TIntList getDeadAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            Op op = mbgraph.getOp(address);
            if (log.isDebugEnabled()) {
                log.debug("Dead test @" + address + " for: " + op);
            }

            if (!mbgraph.wasAddressReached(address)) {
                if (log.isDebugEnabled()) {
                    log.debug("dead: " + op);
                }
                result.add(address);
            }
        }

        return result;
    }

    TIntList getDeadAssignmentAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            if (!mbgraph.wasAddressReached(address)) {
                continue;
            }

            List<ExecutionNode> pile = mbgraph.getNodePile(address);
            ExecutionContext ectx = pile.get(0).getContext();
            if (ectx == null) {
                if (log.isWarnEnabled()) {
                    log.warn("Null execution context @" + address + ". This shouldn't happen!");
                }
                continue;
            }

            MethodState mState = ectx.getMethodState();
            TIntList assigned = mState.getRegistersAssigned();
            if (0 >= assigned.size()) {
                continue;
            }

            Op op = mbgraph.getOp(address);
            if (op.sideEffectLevel() != SideEffect.Level.NONE) {
                // Could have modified class state
                continue;
            }

            if (op.getName().startsWith("invoke-direct") && mbgraph.getMethodDescriptor().contains("-><init>(")) {
                // Instance initializer shouldn't be removed.
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("Read assignments test @" + address + " for: " + op);
            }

            if (areRegistersUsed(address, assigned, mbgraph)) {
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("dead assignment: " + op + ", registers=" + assigned);
            }
            result.add(address);
        }

        return result;
    }

    TIntList getDeadResultAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            Op op = mbgraph.getOp(address);
            if (!(op instanceof InvokeOp)) {
                continue;
            }

            if (op.getName().contains("-direct")) {
                // Initializers shouldn't be removed because they setup instance state.
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("Results usage test @" + address + " for: " + op);
            }
            String returnType = ((InvokeOp) op).getReturnType();
            if (returnType.equals("V")) {
                continue;
            }

            BuilderInstruction instruction = mbgraph.getInstruction(address);
            int nextAddress = address + instruction.getCodeUnits();
            BuilderInstruction nextInstr = mbgraph.getInstruction(nextAddress);
            if (nextInstr == null) {
                continue;
            }
            if (nextInstr.getOpcode().name.startsWith("move-result")) {
                continue;
            }

            if (log.isDebugEnabled()) {
                log.info("dead result: " + op);
            }
            result.add(address);
        }

        return result;
    }

    TIntList getUselessBranchAddresses() {
        TIntList result = new TIntArrayList();
        for (int address : addresses.toArray()) {
            Op op = mbgraph.getOp(address);
            if (!(op instanceof GotoOp)) {
                continue;
            }

            // Branch is useless if it branches to the next instruction.
            OffsetInstruction instruction = (OffsetInstruction) mbgraph.getInstruction(address);
            int branchOffset = instruction.getCodeOffset();
            if (branchOffset != instruction.getCodeUnits()) {
                continue;
            }

            result.add(address);
        }

        return result;
    }

    private static TIntSet getExceptionHandlerCodeAddresses(MethodBackedGraph mbgraph) {
        TIntSet result = new TIntHashSet();
        List<BuilderTryBlock> tryBlocks = mbgraph.getTryBlocks();
        for (BuilderTryBlock tryBlock : tryBlocks) {
            List<? extends BuilderExceptionHandler> handlers = tryBlock.getExceptionHandlers();
            for (BuilderExceptionHandler handler : handlers) {
                int address = handler.getHandlerCodeAddress();
                BuilderInstruction instruction = mbgraph.getInstruction(address);
                do {
                    // Add all instructions until return, goto, etc.
                    result.add(address);
                    address += instruction.getCodeUnits();
                    instruction = mbgraph.getInstruction(address);
                    if (instruction != null) {
                        result.add(address);
                    }
                } while ((instruction != null) && instruction.getOpcode().canContinue());
            }
        }

        return result;
    }

    TIntList getValidAddresses(MethodBackedGraph mbgraph) {
        TIntList validAddresses = new TIntArrayList(mbgraph.getAddresses());
        // Keep the last address. It's a hack. Last op is normally a return, goto, etc.
        // Though could be array-payload (but we don't check, hence hack)
        validAddresses.sort();
        validAddresses.removeAt(validAddresses.size() - 1);

        // For now, don't remove any exception handler code until VM actually understands them.
        validAddresses.removeAll(getExceptionHandlerCodeAddresses(mbgraph));

        for (int address : validAddresses.toArray()) {
            Op op = mbgraph.getOp(address);
            int level = op.sideEffectLevel().getValue();
            if (level > SIDE_EFFECT_THRESHOLD.getValue()) {
                validAddresses.remove(address);
                continue;
            }

            if (op instanceof NopOp) {
                // Usually, the only reason a nop exists is because it was generated by the compiler.
                // Most decompilers are smart enough to deal with them.
                validAddresses.remove(address);
            }
        }

        return validAddresses;
    }

}
