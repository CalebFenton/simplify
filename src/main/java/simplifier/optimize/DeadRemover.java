package simplifier.optimize;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.iface.debug.DebugItem;

import simplifier.Main;
import simplifier.vm.SideEffect;
import simplifier.vm.context.ContextGraph;
import simplifier.vm.context.ContextNode;
import simplifier.vm.context.MethodContext;
import simplifier.vm.op_handler.InvokeOp;
import simplifier.vm.op_handler.Op;
import util.SparseArray;
import util.Utils;

public class DeadRemover {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final SideEffect.Type SIDE_EFFECT_THRESHOLD = SideEffect.Type.WEAK;

    private static boolean areAssignmentsRead(int address, ContextGraph graph, TIntList assigned) {
        Deque<ContextNode> stack = new ArrayDeque<ContextNode>();
        stack.addAll(getChildrenAtAddress(address, graph));

        while (stack.peek() != null) {
            ContextNode node = stack.poll();
            MethodContext ctx = node.getContext();

            for (int i = 0; i < assigned.size(); i++) {
                int assignedRegister = assigned.get(i);
                if (ctx.wasRegisterRead(assignedRegister)) {
                    log.info("r" + assignedRegister + " is read after this address (" + address + ") @"
                                    + node.getAddress() + ", " + node.getOpHandler());
                    return true;
                } else if (ctx.wasRegisterAssigned(assignedRegister)) {
                    log.info("r" + assignedRegister + " is reassigned without being read @" + node.getAddress() + ", "
                                    + node.getOpHandler());
                    return false;
                }
            }

            stack.addAll(node.getChildren());
        }

        return false;
    }

    private static List<ContextNode> getChildrenAtAddress(int address, ContextGraph graph) {
        List<ContextNode> result = new ArrayList<ContextNode>();
        List<ContextNode> nodePile = graph.getNodePile(address);
        for (ContextNode node : nodePile) {
            result.addAll(node.getChildren());
        }

        return result;
    }

    private final SparseArray<BuilderInstruction> addressToInstruction;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private TIntArrayList noppedAddresses;
    private final TIntSet changedAddresses;
    private int deadCount = 0;
    private int unreachableCount = 0;

    DeadRemover(MutableMethodImplementation implementation, ContextGraph graph, TIntSet changedAddresses) {
        this.graph = graph;
        this.implementation = implementation;
        this.changedAddresses = changedAddresses;
        addressToInstruction = Optimizer.buildAddressToInstruction(implementation.getInstructions());
        noppedAddresses = new TIntArrayList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("unreachable=").append(unreachableCount).append(", dead=").append(deadCount);

        return sb.toString();
    }

    private TIntList getDeadAssignmentAddresses(TIntList addresses) {
        TIntList result = new TIntArrayList(0);
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);

            Op handler = graph.getOpHandler(address);
            if (handler.sideEffectType().ordinal() > SIDE_EFFECT_THRESHOLD.ordinal()) {
                continue;
            }

            // Only invokes will have > 1 assignments, all others will have <= 1
            // All executions of the same instruction should set the same registers
            List<ContextNode> pile = graph.getNodePile(address);
            if (pile.size() < 1) {
                // TODO: move-exception will have a node pile of 0, why??
                log.warning("Node pile size is 0. This could be a mistake. Skipping.");
                continue;
            }

            TIntList assigned = pile.get(0).getContext().getRegistersAssigned();
            if (assigned.size() > 0) {
                log.fine("Read assignments test for: " + handler);
                if (areAssignmentsRead(address, graph, assigned)) {
                    continue;
                }

                log.info("Nop dead assignment: " + handler + ", assign=" + assigned);
                result.add(address);
            }
        }

        deadCount = result.size();

        return result;
    }

    private TIntList getUnreachedCodeAddresses(TIntList addresses) {
        TIntList nopAddresses = new TIntArrayList(0);
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            System.out.println("get op handler: " + address + " " + addressToInstruction.get(address).getOpcode().name);
            Op handler = graph.getOpHandler(address);

            if (handler.sideEffectType().ordinal() > SIDE_EFFECT_THRESHOLD.ordinal()) {
                continue;
            }

            log.fine("Reachability test for: " + handler);

            List<ContextNode> nodePile = graph.getNodePile(address);
            if (nodePile.size() == 0) {
                log.fine("Nop unreachable instruction: " + handler);
                nopAddresses.add(address);
                continue;
            }
        }

        unreachableCount = nopAddresses.size();

        return nopAddresses;
    }

    private TIntList getUnusedResultAddresses(TIntList addresses) {
        TIntList nopAddresses = new TIntArrayList(0);
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            Op handler = graph.getOpHandler(address);
            if (!(handler instanceof InvokeOp)) {
                continue;
            }

            if (handler.sideEffectType().ordinal() > SIDE_EFFECT_THRESHOLD.ordinal()) {
                continue;
            }

            if (handler.getOpName().contains("-direct")) {
                // Not sure about initializers..
                continue;
            }

            log.fine("Results usage test for: " + handler);

            String returnType = ((InvokeOp) handler).getReturnType();
            boolean unusedResult = true;
            if (!returnType.equals("V")) {
                if ((i + 1) < addresses.size()) {
                    int nextAddress = addresses.get(i + 1);
                    BuilderInstruction nextInstr = addressToInstruction.get(nextAddress);
                    if (nextInstr.getOpcode().name.startsWith("move-result")) {
                        unusedResult = false;
                    }
                }
            }

            if (unusedResult) {
                log.info("Nop unused, no side-effect op: " + handler);
                nopAddresses.add(address);
                continue;
            }

        }

        deadCount = nopAddresses.size();

        return nopAddresses;
    }

    private void nopInstruction(int address) {
        noppedAddresses.add(address);

        int index = addressToInstruction.get(address).getLocation().getIndex();
        BuilderInstruction replacementInstruction = new BuilderInstruction10x(Opcode.NOP);
        implementation.replaceInstruction(index, replacementInstruction);
    }

    private void nopInstructions(TIntList addresses) {
        Utils.deDuplicate(addresses);

        addresses.sort();

        // Need to go in reverse order with regards to address to not
        // disturb instruction addresses after this address
        for (int i = addresses.size() - 1; i >= 0; i--) {
            int address = addresses.get(i);
            nopInstruction(address);
        }
    }

    private void nopSweep() {
        // Turn any pair of nops to one nop
        Collection<? extends DebugItem> d = Utils.makeCollection(implementation.getDebugItems());
        List<BuilderInstruction> instructions = implementation.getInstructions();
        for (int i = 0; i < (instructions.size() - 1); i++) {
            BuilderInstruction current = instructions.get(i);
            BuilderInstruction next = instructions.get(i + 1);

            if ((current.getOpcode() == next.getOpcode()) && (current.getOpcode() == Opcode.NOP)) {
                MethodLocation location = current.getLocation();
                location.getDebugItems().clear();
                implementation.removeInstruction(location.getIndex());
            }
        }
    }

    private void removeInstructions(TIntList addresses) {
        // Kind of an ugly hack because everything else is built on addresses.
        TIntList indexes = new TIntArrayList();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        for (BuilderInstruction instruction : instructions) {
            MethodLocation location = instruction.getLocation();
            int address = location.getCodeAddress();
            if (addresses.contains(address)) {
                if (!indexes.contains(location.getIndex())) {
                    indexes.add(location.getIndex());
                    location.getDebugItems().clear();
                }
            }
        }

        indexes.sort();
        for (int i = indexes.size() - 1; i >= 0; i--) {
            int index = indexes.get(i);
            implementation.removeInstruction(index);
        }
    }

    boolean perform() {
        TIntList validAddresses = getAddressesNotInCatchBlocks();
        validAddresses.removeAll(changedAddresses.toArray());

        TIntList nopAddresses = new TIntArrayList();
        nopAddresses.addAll(getUnreachedCodeAddresses(validAddresses));
        nopAddresses.addAll(getDeadAssignmentAddresses(validAddresses));
        nopAddresses.addAll(getUnusedResultAddresses(validAddresses));

        removeInstructions(nopAddresses);
        // nopInstructions(nopAddresses);
        // nopSweep();

        return nopAddresses.size() > 0;
    }

    TIntList getAddressesNotInCatchBlocks() {
        TIntList catchAddresses = new TIntArrayList();
        List<BuilderTryBlock> tryBlocks = implementation.getTryBlocks();
        for (BuilderTryBlock tryBlock : tryBlocks) {
            List<? extends BuilderExceptionHandler> exceptionHandlers = tryBlock.getExceptionHandlers();
            for (BuilderExceptionHandler handler : exceptionHandlers) {
                int address = handler.getHandlerCodeAddress();
                catchAddresses.add(address);
            }
        }

        TIntList result = new TIntArrayList();
        boolean inCatch = false;
        for (int idx = 0; idx < addressToInstruction.size(); idx++) {
            int address = addressToInstruction.keyAt(idx);
            BuilderInstruction instruction = addressToInstruction.get(address);
            if (catchAddresses.contains(address)) {
                inCatch = true;
                continue;
            }

            if (inCatch) {
                if (instruction.getLocation().getLabels().size() > 0) {
                    inCatch = false;
                }
            }

            result.add(address);
        }

        return result;
    }

    public boolean madeChanges() {
        return (deadCount > 0) || (unreachableCount > 0);
    }
}
