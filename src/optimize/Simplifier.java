package optimize;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.util.SparseArray;

import simplify.Main;
import simplify.vm.ContextGraph;

public class Simplifier {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void simplify(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        ConstantPropigator propigator = new ConstantPropigator(dexBuilder, method, graph);
        DeadRemover remover = new DeadRemover(dexBuilder, method, graph);

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        System.out.println("Simplifying " + methodDescriptor);

        boolean madeChanges = false;
        do {
            // Run the remover first since that means less work for the propigator
            madeChanges |= remover.perform();

            madeChanges |= propigator.perform();

            System.out.println("Total optimizations: " + propigator.toString() + ", " + remover.toString());
        } while (madeChanges);
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
