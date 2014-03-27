package simplify.optimize;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.iface.debug.DebugItem;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.Utils;
import simplify.vm.ContextGraph;
import simplify.vm.ContextNode;
import simplify.vm.MethodContext;
import simplify.vm.ops.InvokeOp;
import simplify.vm.ops.Op;
import util.SparseArray;

public class DeadRemover {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

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
                                    + node.getAddress() + ", " + node.getHandler());
                    return true;
                } else if (ctx.wasRegisterAssigned(assignedRegister)) {
                    log.info("r" + assignedRegister + " is reassigned without being read @" + node.getAddress() + ", "
                                    + node.getHandler());
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

    private static boolean opHasSideEffects(Op handler) {
        if (handler instanceof InvokeOp) {
            if (((InvokeOp) handler).hasSideEffects()) {
                return true;
            }
        }

        return false;
    }

    private final SparseArray<BuilderInstruction> addressToInstruction;

    private int deadCount = 0;
    private final DexBuilder dexBuilder;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;

    private TIntArrayList noppedAddresses;
    private int unreachableCount = 0;

    DeadRemover(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        this.dexBuilder = dexBuilder;
        this.graph = graph;

        MutableMethodImplementation impl = (MutableMethodImplementation) method.getImplementation();
        implementation = impl;

        addressToInstruction = Simplifier.buildAddressToInstruction(impl.getInstructions());
        noppedAddresses = new TIntArrayList();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("unreachable=").append(unreachableCount).append(", dead=").append(deadCount);

        return sb.toString();
    }

    private TIntList getDeadAssignmentAddresses() {
        TIntList nopAddresses = new TIntArrayList(0);
        TIntList addresses = graph.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            Op handler = graph.getOpHandler(address);

            // Even if assignments are made and never used, it's almost impossible to know if a method invoke can be
            // removed because it may have side-effects. E.g. write to network, disk, etc.
            if (opHasSideEffects(handler)) {
                continue;
            }

            // Only invokes will have > 1 assignments, all others will have <= 1
            // All executions of the same instruction should set the same registers
            TIntList assigned = graph.getNodePile(address).get(0).getContext().getRegistersAssigned();
            if (assigned.size() > 0) {
                log.fine("Read assignments test for: " + handler);

                if (!areAssignmentsRead(address, graph, assigned)) {
                    log.info("Nop dead assignment: " + handler + ", assign=" + assigned);
                    nopAddresses.add(address);
                    continue;
                }
            }
        }

        deadCount = nopAddresses.size();

        return nopAddresses;
    }

    private TIntList getUnreachedCodeAddresses() {
        TIntList nopAddresses = new TIntArrayList(0);
        TIntList addresses = graph.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            Op handler = graph.getOpHandler(address);

            if (opHasSideEffects(handler)) {
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

    private TIntList getUnusedResultAddresses() {
        TIntList nopAddresses = new TIntArrayList(0);
        TIntList addresses = graph.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            Op handler = graph.getOpHandler(address);

            if (opHasSideEffects(handler)) {
                continue;
            }

            if (handler instanceof InvokeOp) {
                log.fine("Results usage test for: " + handler);

                String returnType = ((InvokeOp) handler).getReturnType();
                if (!returnType.equals("V")) {
                    boolean unusedResult = true;
                    if ((i + 1) < addresses.size()) {
                        int nextAddress = addresses.get(i + 1);
                        BuilderInstruction nextInstr = addressToInstruction.get(nextAddress);
                        if (nextInstr.getOpcode().name.startsWith("move-result")) {
                            unusedResult = false;
                        }
                    }

                    if (unusedResult) {
                        log.info("Nop unused, no side-effect op: " + handler);
                        nopAddresses.add(address);
                        continue;
                    }
                }
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
                indexes.add(location.getIndex());
                location.getDebugItems().clear();
            }
        }

        indexes.sort();
        for (int i = indexes.size() - 1; i >= 0; i--) {
            int index = indexes.get(i);
            implementation.removeInstruction(index);
        }
    }

    boolean perform() {
        TIntList nopAddresses = new TIntArrayList(0);

        nopAddresses.addAll(getUnreachedCodeAddresses());

        nopAddresses.addAll(getDeadAssignmentAddresses());

        nopAddresses.addAll(getUnusedResultAddresses());

        removeInstructions(nopAddresses);
        // nopInstructions(nopAddresses);
        // nopSweep();

        return nopAddresses.size() > 0;
    }
}
