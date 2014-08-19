package simplifier.optimize;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.Label;

import simplifier.Main;
import simplifier.vm.SideEffect;
import simplifier.vm.context.ContextGraph;
import simplifier.vm.context.ContextNode;
import simplifier.vm.context.MethodContext;
import simplifier.vm.op_handler.InvokeOp;
import simplifier.vm.op_handler.Op;

public class DeadDetector {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final SideEffect.Type SIDE_EFFECT_THRESHOLD = SideEffect.Type.WEAK;

    private static boolean areAssignmentsRead(int address, ContextGraph graph, TIntList assigned) {
        Deque<ContextNode> stack = new ArrayDeque<ContextNode>();
        stack.addAll(getChildrenAtAddress(address, graph));

        while (stack.peek() != null) {
            ContextNode node = stack.poll();
            MethodContext ctx = node.getContext();
            for (int register : assigned.toArray()) {
                if (ctx.wasRegisterRead(register)) {
                    log.info("r" + register + " is read after this address (" + address + ") @" + node.getAddress()
                                    + ", " + node.getOpHandler());
                    return true;
                } else if (ctx.wasRegisterAssigned(register)) {
                    log.info("r" + register + " is reassigned without being read @" + node.getAddress() + ", "
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

    static TIntList getDeadAddresses(TIntList addresses, ContextGraph graph) {
        TIntList result = new TIntArrayList(0);
        for (int address : addresses.toArray()) {
            Op handler = graph.getOpHandler(address);
            log.fine("Reachability test for: " + handler);

            List<ContextNode> nodePile = graph.getNodePile(address);
            if (nodePile.size() == 0) {
                log.fine("unreachable: " + handler);
                result.add(address);
                continue;
            }
        }

        return result;
    }

    static TIntList getUnusedAssignmentAddresses(TIntList addresses, ContextGraph graph) {
        TIntList result = new TIntArrayList(0);
        for (int address : addresses.toArray()) {
            List<ContextNode> pile = graph.getNodePile(address);
            if (pile.size() < 1) {
                // TODO: move-exception will have a node pile of 0, why??
                log.warning("Node pile size is 0. This could be a mistake. Skipping.");
                continue;
            }

            MethodContext mctx = pile.get(0).getContext();
            if (mctx == null) {
                // TODO: this probably broke because optimizer was stupid
                log.warning("Null method context. Maybe this is a template node?");
                continue;
            }

            TIntList assigned = mctx.getRegistersAssigned();
            if (assigned.size() == 0) {
                continue;
            }

            Op handler = graph.getOpHandler(address);
            log.info("Read assignments test for: " + handler);
            if (areAssignmentsRead(address, graph, assigned)) {
                continue;
            }

            log.info("unused assignment: " + handler + ", registers=" + assigned);
            result.add(address);
        }

        return result;
    }

    static TIntList getUnusedResultAddresses(TIntList addresses, ContextGraph graph,
                    TIntObjectMap<BuilderInstruction> addressToInstruction) {
        TIntList result = new TIntArrayList(0);
        for (int address : addresses.toArray()) {
            Op handler = graph.getOpHandler(address);
            if (!(handler instanceof InvokeOp)) {
                continue;
            }

            if (handler.getOpName().contains("-direct")) {
                // Not sure about initializers..
                continue;
            }

            log.fine("Results usage test for: " + handler);
            String returnType = ((InvokeOp) handler).getReturnType();
            boolean unusedResult = true;
            if (returnType.equals("V")) {
                continue;
            }

            BuilderInstruction instruction = addressToInstruction.get(address);
            int nextAddress = address + instruction.getCodeUnits();
            if (!addressToInstruction.containsKey(nextAddress)) {
                continue;
            }

            BuilderInstruction nextInstr = addressToInstruction.get(nextAddress);
            if (nextInstr.getOpcode().name.startsWith("move-result")) {
                unusedResult = false;
            }

            if (unusedResult) {
                log.info("unused result: " + handler);
                result.add(address);
                continue;
            }
        }

        return result;
    }

    static TIntList getValidAddresses(TIntObjectMap<BuilderInstruction> addressToInstruction,
                    List<BuilderTryBlock> tryBlocks, ContextGraph graph) {
        TIntList result = getAddressesNotInCatchBlocks(addressToInstruction, tryBlocks);
        for (int address : result.toArray()) {
            Op handler = graph.getOpHandler(address);
            int sideEffect = handler.sideEffectType().ordinal();
            if (sideEffect > SIDE_EFFECT_THRESHOLD.ordinal()) {
                result.remove(address);
            }
        }

        return result;
    }

}
