package optimize;

import java.util.logging.Logger;

import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Main;
import simplify.vm.ContextGraph;

public class Simplifier {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void simplify(DexBuilder dexBuilder, BuilderMethod method, ContextGraph graph) {
        ConstantPropigator propigator = new ConstantPropigator(dexBuilder, method, graph);
        NoopRemover remover = new NoopRemover(dexBuilder, method, graph);

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

}
