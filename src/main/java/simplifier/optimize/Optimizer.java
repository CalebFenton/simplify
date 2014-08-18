package simplifier.optimize;

import gnu.trove.set.TIntSet;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplifier.Main;
import simplifier.vm.context.ContextGraph;
import util.SparseArray;

public class Optimizer {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static boolean simplify(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        boolean madeChanges = false;

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        System.out.println("Simplifying " + methodDescriptor);

        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        ConstantPropigator propigator = new ConstantPropigator(dexBuilder, implementation, graph);
        propigator.perform();
        System.out.println("Const optimizations: " + propigator.toString());

        /*
         * If changes were made, DeadRemover can't rely on the graph to include the changes. Pass along the updated
         * implementation and any changed addresses so they can be avoided.
         */
        if (propigator.madeChanges()) {
            return true;
        }

        implementation = propigator.getImplementation();
        TIntSet changedAddresses = propigator.getChangedAddresses();
        DeadRemover remover = new DeadRemover(implementation, graph, changedAddresses);
        remover.perform();
        System.out.println("Dead optimizations: " + remover.toString());

        return propigator.madeChanges() || remover.madeChanges();
    }

    protected static SparseArray<BuilderInstruction> buildAddressToInstruction(List<BuilderInstruction> instructions) {
        SparseArray<BuilderInstruction> result = new SparseArray<BuilderInstruction>(instructions.size());
        for (BuilderInstruction instruction : instructions) {
            int address = instruction.getLocation().getCodeAddress();
            result.put(address, instruction);
        }

        return result;
    }

}
