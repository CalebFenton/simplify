package org.cf.simplify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.util.Dexifier;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final Level LOG_LEVEL = Level.FINE;

    private static final int MAX_NODE_VISITS = 2000;
    private static final int MAX_CALL_DEPTH = 20;

    private static final int MAX_OPTIMIZATION_SWEEPS = 100;

    public static void main(String[] argv) throws Exception {
        setupLogger();

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(Dexifier.DEFAULT_API_LEVEL);
        List<BuilderClassDef> classDefs = Dexifier.dexifySmaliFiles(argv[0], dexBuilder);
        String methodRegex = argv.length > 1 ? ".*" + argv[1] + ".*" : "";
        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (BuilderClassDef classDef : classDefs) {
            methods.addAll(classDef.getMethods());
        }

        VirtualMachine vm = new VirtualMachine(classDefs, MAX_NODE_VISITS, MAX_CALL_DEPTH);

        // TODO: investigate sorting methods by implementation size. maybe shorter methods can be optimized more easily
        // and will speed up optimizations of dependent methods.
        Set<BuilderMethod> finishedMethods = new HashSet<BuilderMethod>();
        for (BuilderMethod method : methods) {
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

            if (finishedMethods.contains(method)) {
                continue;
            }

            ContextGraph graph = vm.execute(methodDescriptor);
            if (graph == null) {
                log.info("Skipping " + methodDescriptor);
                finishedMethods.add(method);
                continue;
            }

            Optimizer opt = new Optimizer(dexBuilder, method, graph, vm);
            boolean madeChanges = opt.simplify(MAX_OPTIMIZATION_SWEEPS);
            if (madeChanges) {
                /*
                 * All objects associated with this method (dexbuilder, implmenetation) have been updated elsewhere.
                 * Poke the VM to re-build the graph based on them.
                 */
                vm.updateInstructionGraph(methodDescriptor);
            } else {
                finishedMethods.add(method);
            }
        }

        String outputDexFile = "out_simple.dex";
        log.info("Writing result to " + outputDexFile);
        dexBuilder.writeTo(new FileDataStore(new File(outputDexFile)));
    }

    public static void setupLogger() {
        // log.setLevel(LOG_LEVEL);
        //
        // try {
        // SimpleFormatter formatter = new SimpleFormatter();
        // FileHandler fh = new FileHandler("log.txt");
        // fh.setFormatter(formatter);
        // log.addHandler(fh);
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
        // for (Handler handler : Logger.getLogger("").getHandlers()) {
        // handler.setLevel(LOG_LEVEL);
        // }
    }

}
