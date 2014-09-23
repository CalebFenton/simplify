package org.cf.simplify.strategy;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.simplify.MethodBackedGraph;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.ContextNode;
import org.cf.smalivm.context.MethodContext;
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

    private static final SideEffect.Type SIDE_EFFECT_THRESHOLD = SideEffect.Type.WEAK;

    private static boolean areAssignmentsRead(int address, TIntList assigned, ContextGraph graph) {
        Deque<ContextNode> stack = new ArrayDeque<ContextNode>(getChildrenAtAddress(address, graph));
        ContextNode node;
        while ((node = stack.poll()) != null) {
            MethodContext ctx = node.getMethodContext();
            for (int register : assigned.toArray()) {
                if (ctx.wasRegisterRead(register)) {
                    log.trace("r" + register + " is read after this address (" + address + ") @" + node.getAddress()
                                    + ", " + node.getOpHandler());
                    return true;
                } else if (ctx.wasRegisterAssigned(register)) {
                    log.trace("r" + register + " is reassigned without being read @" + node.getAddress() + ", "
                                    + node.getOpHandler());
                    return false;
                }
            }
            stack.addAll(node.getChildren());
        }

        return false;
    }

    private static TIntList getAddressesNotInCatchBlocks(TIntObjectMap<BuilderInstruction> addressToInstruction,
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
        for (int address : addressToInstruction.keys()) {
            BuilderInstruction instruction = addressToInstruction.get(address);
            if (catchAddresses.contains(address)) {
                inCatch = true;
                continue;
            }

            if (inCatch) {
                Set<Label> labels = instruction.getLocation().getLabels();
                inCatch = labels.size() == 0;
            }

            result.add(address);
        }

        return result;
    }

    private static List<ContextNode> getChildrenAtAddress(int address, ContextGraph graph) {
        List<ContextNode> result = new ArrayList<ContextNode>();
        List<ContextNode> nodePile = graph.getNodePile(address);
        for (ContextNode node : nodePile) {
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
            Op op = mbgraph.getOpHandler(address);
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

            List<ContextNode> pile = mbgraph.getNodePile(address);
            MethodContext mctx = pile.get(0).getMethodContext();
            if (mctx == null) {
                log.warn("Null method context. This shouldn't happen!");
                continue;
            }

            TIntList assigned = mctx.getRegistersAssigned();
            if (assigned.size() == 0) {
                continue;
            }

            Op op = mbgraph.getOpHandler(address);
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
            Op op = mbgraph.getOpHandler(address);
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
            Op op = mbgraph.getOpHandler(address);
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
        TIntList result = getAddressesNotInCatchBlocks(addressToInstruction, tryBlocks);
        for (int address : result.toArray()) {
            Op op = mbgraph.getOpHandler(address);
            int sideEffect = op.sideEffectType().ordinal();
            if (sideEffect > SIDE_EFFECT_THRESHOLD.ordinal()) {
                result.remove(address);
            }
        }

        return result;
    }

}
