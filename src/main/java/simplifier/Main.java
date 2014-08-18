package simplifier;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;

import simplifier.optimize.Optimizer;
import simplifier.vm.VirtualMachine;
import simplifier.vm.context.ContextGraph;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.INFO;

    private static final int MAX_NODE_VISITS = 1000;
    private static final int MAX_CALL_DEPTH = 5;

    public static void main(String[] argv) throws Exception {
        setupLogger();

        List<File> smaliFiles = new ArrayList<File>();
        File f = new File(argv[0]);
        if (f.isDirectory()) {
            smaliFiles.addAll(Arrays.asList(f.listFiles()));
        } else {
            smaliFiles.add(f);
        }

        String methodRegex = argv.length > 1 ? ".*" + argv[1] + ".*" : "";

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(Dexifier.API_LEVEL);
        List<BuilderClassDef> classDefs = Dexifier.dexifySmaliFiles(smaliFiles, dexBuilder);
        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (BuilderClassDef classDef : classDefs) {
            methods.addAll(classDef.getMethods());
        }

        VirtualMachine vm = new VirtualMachine(classDefs, MAX_NODE_VISITS, MAX_CALL_DEPTH);

        // TODO: investigate sorting methods by implementation size. maybe shorter methods can be optimized more easily
        // and will speed up optimizations of dependent methods.
        outer: for (BuilderMethod method : methods) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            if (methodDescriptor.endsWith("-><clinit>()V")) {
                // Static class initialization is called elsewhere, as needed.
                // TODO: main shouldn't need to know how to do this!
                continue;
            }

            if (!methodRegex.isEmpty() && !methodDescriptor.matches(methodRegex)) {
                log.info("Skipping " + methodDescriptor + " because it doesn't match " + argv[1]);
                continue;
            }

            boolean madeChanges = false;
            int sweeps = 0;
            do {
                ContextGraph graph = vm.execute(methodDescriptor);
                if (graph == null) {
                    log.info("Skipping " + methodDescriptor);
                    continue outer;
                }

                // String methodName = method.getName();
                // FileUtils.writeStringToFile(new File("graphs/" + methodName + ".dot"), graph.toGraph());

                madeChanges = Optimizer.simplify(dexBuilder, method, graph);
                if (madeChanges) {
                    vm.updateInstructionGraph(methodDescriptor);
                }

                sweeps++;
            } while (madeChanges && (sweeps < 20));
        }

        String outputDexFile = "out_simple.dex";
        log.info("Writing result to " + outputDexFile);
        dexBuilder.writeTo(new FileDataStore(new File(outputDexFile)));
    }

    public static void setupLogger() {
        log.setLevel(LOG_LEVEL);

        try {
            SimpleFormatter formatter = new SimpleFormatter();
            FileHandler fh = new FileHandler("log.txt");
            fh.setFormatter(formatter);
            log.addHandler(fh);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (Handler handler : Logger.getLogger("").getHandlers()) {
            handler.setLevel(LOG_LEVEL);
        }
    }

}
