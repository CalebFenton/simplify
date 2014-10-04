package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.GotoOp;
import org.cf.smalivm.opcode.InvokeOp;
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

    private static boolean areAssignmentsRead(int address, TIntList assignedList, ExecutionGraph graph) {
        Deque<ExecutionNode> stack = new ArrayDeque<ExecutionNode>(getChildrenAtAddress(address, graph));
        ExecutionNode node;
        int[] assigned = assignedList.toArray();
        while ((node = stack.poll()) != null) {
            MethodState mState = node.getContext().getMethodState();
            for (int register : assigned) {
                if (mState.wasRegisterRead(register)) {
                    log.trace("r" + register + " is read after this address (" + address + ") @" + node.getAddress()
                                    + ", " + node.getOp());
                    return true;
                } else if (mState.wasRegisterAssigned(register)) {
                    log.trace("r" + register + " is reassigned without being read @" + node.getAddress() + ", "
                                    + node.getOp());
                    return false;
                }
            }
            stack.addAll(node.getChildren());
        }

        return false;
    }

    private static List<ExecutionNode> getChildrenAtAddress(int address, ExecutionGraph graph) {
        List<ExecutionNode> result = new ArrayList<ExecutionNode>();
        List<ExecutionNode> nodePile = graph.getNodePile(address);
        for (ExecutionNode node : nodePile) {
            result.addAll(node.getChildren());
        }

        return result;
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
            log.debug("Dead test @" + address + " for: " + op);

            if (!mbgraph.wasAddressReached(address)) {
                log.debug("dead: " + op);
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
                log.warn("Null execution context @" + address + ". This shouldn't happen!");
                continue;
            }

            MethodState mState = ectx.getMethodState();
            TIntList assigned = mState.getRegistersAssigned();
            if (assigned.size() == 0) {
                continue;
            }

            Op op = mbgraph.getOp(address);
            log.debug("Read assignments test @" + address + " for: " + op);
            if (areAssignmentsRead(address, assigned, mbgraph)) {
                continue;
            }

            log.debug("dead assignment: " + op + ", registers=" + assigned);
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

            if (op.getOpName().contains("-direct")) {
                // Not sure about initializers..
                continue;
            }

            log.debug("Results usage test @" + address + " for: " + op);
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

            log.info("dead result: " + op);
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

    private static TIntSet getHandlerCodeAddresses(MethodBackedGraph mbgraph) {
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
        // List<BuilderTryBlock> tryBlocks = mbgraph.getTryBlocks();
        // TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        // TIntList result = getAddressesNotInTryCatchBlocks(addressToInstruction, tryBlocks);

        TIntList result = new TIntArrayList(mbgraph.getAddresses());

        // This is a hack to prevent handlers from being removed
        // And it's probably not a good idea to remove the last op in a method because it either returns or jumps.
        result.sort();
        result.removeAt(result.size() - 1);
        result.removeAll(getHandlerCodeAddresses(mbgraph));

        for (int address : result.toArray()) {
            Op op = mbgraph.getOp(address);
            int level = op.sideEffectLevel().getValue();
            if (level > SIDE_EFFECT_THRESHOLD.getValue()) {
                result.remove(address);
                continue;
            }

        }

        return result;
    }

}
