package optimize;

import gnu.trove.list.TIntList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;
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
            log.fine("Liveness test for: " + handler);

            // Reachability
            List<ContextNode> nodePile = graph.getNodePile(address);
            if (nodePile.size() == 0) {
                log.fine("Nop unreachable instruction: " + handler);
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
                boolean areAnyAssignmentsRead = areAssignmentsRead(address, graph);
                if (!areAnyAssignmentsRead) {
                    log.info("Nop dead assignment: " + handler + ", assign=" + assignments);
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
                        log.info("Nop unused, no side-effect: " + handler);
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
        for (ContextNode node : graph.getNodePile(assignAddress)) {
            MethodContext mctx = node.getContext();
            TIntIntMap watchedRegisterToReadSize = new TIntIntHashMap();
            SparseArray<RegisterStore> registerToStore = mctx.getRegisterToStore();
            for (int i = 0; i < registerToStore.size(); i++) {
                int register = registerToStore.keyAt(i);
                RegisterStore rs = mctx.peekRegister(register);
                if (rs.getAssigned().contains(assignAddress)) {
                    watchedRegisterToReadSize.put(register, rs.getRead().size());
                }
            }

            if (areRegistersRead(node, assignAddress, watchedRegisterToReadSize)) {
                // At least one register is read in this node's children
                return true;
            }
        }

        return false;
    }

    private static boolean areRegistersRead(ContextNode node, int assignAddress, TIntIntMap watchedRegisterToReadSize) {
        MethodContext mctx = node.getContext();
        for (int register : watchedRegisterToReadSize.keys()) {
            RegisterStore rs = mctx.peekRegister(register);
            if (rs == null) {
                continue;
            }
            if (!rs.getAssigned().contains(assignAddress)) {
                // It's the same register number as one we're looking for, but it wasn't assigned in the same place so
                // it's not the same instance.
                continue;
            }

            if (rs.getRead().size() > watchedRegisterToReadSize.get(register)) {
                return true;
            }
        }

        for (ContextNode child : node.getChildren()) {
            return areRegistersRead(child, assignAddress, watchedRegisterToReadSize);
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
