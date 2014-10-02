package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jf.dexlib2.builder.Label;
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

    private static TIntList getAddressesNotInTryCatchBlocks(TIntObjectMap<BuilderInstruction> addressToInstruction,
                    List<BuilderTryBlock> tryBlocks) {
        TIntList catchAddresses = new TIntArrayList();
        for (BuilderTryBlock tryBlock : tryBlocks) {
            List<? extends BuilderExceptionHandler> exceptionHandlers = tryBlock.getExceptionHandlers();
            for (BuilderExceptionHandler handler : exceptionHandlers) {
                int address = handler.getHandlerCodeAddress();
                catchAddresses.add(address);
            }
        }

        TIntList result = new TIntArrayList();
        boolean inCatch = false;
        int[] addresses = addressToInstruction.keys();
        Arrays.sort(addresses);
        for (int address : addresses) {
            BuilderInstruction instruction = addressToInstruction.get(address);
            if (catchAddresses.contains(address)) {
                inCatch = true;
                continue;
            }

            if (inCatch) {
                Set<Label> labels = instruction.getLocation().getLabels();
                inCatch = 0 == labels.size();
                continue;
            }

            result.add(address);
        }

        return result;
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

    private int deadAssignmentCount = 0;
    private int deadBranchCount = 0;
    private int deadCount = 0;
    private int deadResultCount = 0;

    private final MethodBackedGraph mbgraph;

    public DeadRemovalStrategy(MethodBackedGraph mbgraph) {
        this.mbgraph = mbgraph;
        addresses = getValidAddresses(mbgraph);
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

    TIntList getValidAddresses(MethodBackedGraph mbgraph) {
        List<BuilderTryBlock> tryBlocks = mbgraph.getTryBlocks();
        TIntObjectMap<BuilderInstruction> addressToInstruction = mbgraph.getAddressToInstruction();
        TIntList result = getAddressesNotInTryCatchBlocks(addressToInstruction, tryBlocks);
        // TIntList result = new TIntArrayList(mbgraph.getAddresses());
        for (int address : result.toArray()) {
            Op op = mbgraph.getOp(address);
            int level = op.sideEffectLevel().getValue();
            if (level > SIDE_EFFECT_THRESHOLD.getValue()) {
                result.remove(address);
            }
        }

        return result;
    }

}
