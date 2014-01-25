package optimize;

import gnu.trove.list.TIntList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.Iterator;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.util.SparseArray;

import simplify.Main;
import simplify.vm.ContextGraph;
import simplify.vm.ContextNode;
import simplify.vm.MethodContext;
import simplify.vm.RegisterStore;

public class DeadRemover {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final DexBuilder dexBuilder;
    private final ContextGraph graph;
    private final MutableMethodImplementation implementation;
    private final SparseArray<BuilderInstruction> addressToInstruction;
    private final SparseArray<SparseArray<RegisterStore>> addressRegisterToStore;

    private int unreachableCount = 0;
    private int unusedCount = 0;

    DeadRemover(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        this.dexBuilder = dexBuilder;
        this.graph = graph;

        MutableMethodImplementation impl = (MutableMethodImplementation) method.getImplementation();
        implementation = impl;

        addressToInstruction = Simplifier.buildAddressToInstruction(impl.getInstructions());

        addressRegisterToStore = buildAddressRegisterToStore(graph);
    }

    boolean perform() {
        boolean madeChanges = false;

        SparseArray<TIntSet> registerToReferences = new SparseArray<TIntSet>();
        SparseArray<TIntSet> registerToUses = new SparseArray<TIntSet>();
        TIntList addresses = graph.getConnectedTerminatingAddresses();
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);

            for (ContextNode node : graph.getNodePile(address)) {
                MethodContext mctx = node.getContext();

                System.out.println(address + " - context: " + mctx);

                // TODO: write custom iterator pffsslffj, delete getregistercount
                for (int register = 0; register < mctx.getRegisterCount(); register++) {
                    TIntSet references = registerToReferences.get(register);
                    if (references == null) {
                        references = new TIntHashSet();
                        registerToReferences.put(register, references);
                    }

                    TIntSet uses = registerToUses.get(register);
                    if (uses == null) {
                        uses = new TIntHashSet();
                        registerToUses.put(register, uses);
                    }

                    RegisterStore registerStore = node.getContext().peekRegister(register);
                    references.addAll(registerStore.getReferenced());
                    uses.addAll(registerStore.getUsed());

                    System.out.println("refs=" + references + ", uses=" + uses);

                    if (registerStore.getType().equals("J")) {
                        // Longs are wide and take up two registers
                        register++;
                    }
                }
            }
        }

        System.out.println("At the end: refs=" + registerToReferences + ", uses=" + registerToUses);

        return madeChanges;
    }

    private static SparseArray<SparseArray<RegisterStore>> buildAddressRegisterToStore(ContextGraph graph) {
        SparseArray<SparseArray<RegisterStore>> result = new SparseArray<SparseArray<RegisterStore>>();

        Iterator<ContextNode> it = graph.iterator();
        while (it.hasNext()) {
            ContextNode node = it.next();

            System.out.println("iterating: " + node);
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("unreachable=").append(unreachableCount).append(", unused=").append(unusedCount);

        return sb.toString();
    }

}
