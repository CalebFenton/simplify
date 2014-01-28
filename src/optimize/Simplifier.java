package optimize;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.vm.ContextGraph;
import util.SparseArray;

public class Simplifier {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static boolean simplify(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        boolean madeChanges = false;

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        System.out.println("Simplifying " + methodDescriptor);

        ConstantPropigator propigator = new ConstantPropigator(dexBuilder, method, graph);
        madeChanges |= propigator.perform();

        DeadRemover remover = new DeadRemover(dexBuilder, method, graph);
        madeChanges |= remover.perform();

        System.out.println("Total optimizations: " + propigator.toString() + ", " + remover.toString());

        return madeChanges;
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
