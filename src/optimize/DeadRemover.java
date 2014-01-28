package optimize;

import gnu.trove.list.TIntList;
import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.handlers.InvokeOpHandler;
import simplify.handlers.OpHandler;
import simplify.vm.ContextGraph;
import simplify.vm.ContextNode;
import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;
import util.SparseArray;

public class DeadRemover {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private final SparseArray<BuilderInstruction> addressToInstruction;

    private int unreachableCount = 0;
    private int deadCount = 0;

    DeadRemover(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        this.dexBuilder = dexBuilder;
        this.graph = graph;

        MutableMethodImplementation impl = (MutableMethodImplementation) method.getImplementation();
        implementation = impl;

        addressToInstruction = Simplifier.buildAddressToInstruction(impl.getInstructions());
    }

    boolean perform() {
        boolean madeChanges = false;

        TIntList addresses = graph.getAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            OpHandler handler = graph.getOpHandler(address);
            System.out.println("Liveness test for: " + handler);

            // Reachability
            List<ContextNode> nodePile = graph.getNodePile(address);
            if (nodePile.size() == 0) {
                System.out.println("Would NOP unreachable instruction: " + handler);
                nopInstruction(implementation, address, addressToInstruction);
                unreachableCount++;
                madeChanges = true;
                continue;
            }

            // Side-effects
            if (opHasSideEffects(handler)) {
                // This method has side effects, or assuming it does, so it can't be dead.
                continue;
            }

            // Dead assignments
            // Only invokes will have > 1 assignments, all others will have <= 1
            List<RegisterStore> assignments = getRegisterAssignments(nodePile, address);
            if (assignments.size() > 0) {
                boolean allAssignmentsUnused = true;

                if (areAssignmentsRead(address, graph)) {
                    // At least one assignment from this instruction is read -> instruction alive
                    allAssignmentsUnused = false;
                }

                if (allAssignmentsUnused) {
                    System.out.println("Would replace dead assignment with NOP: " + handler);
                    nopInstruction(implementation, address, addressToInstruction);
                    deadCount++;
                    madeChanges = true;
                    continue;
                }
            }

            // Non-side effect invokes with unused results
            if (handler instanceof InvokeOpHandler) {
                String returnType = ((InvokeOpHandler) handler).getReturnType();
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
                        System.out.println("Would replace no side-effect, unused result with NOP: " + handler);
                        nopInstruction(implementation, address, addressToInstruction);
                        deadCount++;
                        madeChanges = true;
                        continue;
                    }
                }
            }

        }

        return madeChanges;
    }

    private static void nopInstruction(MutableMethodImplementation implementation, int address,
                    SparseArray<BuilderInstruction> addressToInstruction) {
        int index = addressToInstruction.get(address).getLocation().getIndex();
        BuilderInstruction replacementInstruction = new BuilderInstruction10x(Opcode.NOP);
        implementation.replaceInstruction(index, replacementInstruction);
    }

    private static List<RegisterStore> getRegisterAssignments(List<ContextNode> nodePile, int address) {
        List<RegisterStore> result = new ArrayList<RegisterStore>();

        for (ContextNode node : nodePile) {
            MethodContext mctx = node.getContext();
            List<RegisterStore> registerStores = mctx.getRegisterToStore().getValues();
            for (RegisterStore rs : registerStores) {
                if (rs.getAssigned().contains(address)) {
                    result.add(rs);
                }
            }
        }

        return result;
    }

    private static boolean opHasSideEffects(OpHandler handler) {
        if (handler instanceof InvokeOpHandler) {
            if (((InvokeOpHandler) handler).hasSideEffects()) {
                return true;
            }
        }

        return false;
    }

    private static boolean areAssignmentsRead(int assignAddress, ContextGraph graph) {
        Iterator<ContextNode> it = graph.iterator();
        while (it.hasNext()) {
            ContextNode node = it.next();
            if (node.getAddress() == assignAddress) {
                continue;
            }

            List<RegisterStore> registerStores = node.getContext().getRegisterToStore().getValues();
            if (registerStores == null) {
                continue;
            }

            for (RegisterStore registerStore : registerStores) {
                if (registerStore.getAssigned().contains(assignAddress)) {
                    TIntSet read = registerStore.getRead();
                    // Assignment liveness testing happens after we skip invokes with side effects. If this has no side
                    // effects and the assignment and reading happened at the same address, it's useless.
                    if ((read.size() > 0) && !((read.size() == 1) && read.contains(assignAddress))) {
                        System.out.println("\t>=1 register assigned @" + assignAddress + " is read @"
                                        + node.getAddress() + ", it's alive!");
                        System.out.println(registerStore);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("unreachable=").append(unreachableCount).append(", dead=").append(deadCount);

        return sb.toString();
    }

}
