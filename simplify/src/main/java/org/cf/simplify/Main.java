package org.cf.simplify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ContextGraph;
import org.cf.util.Dexifier;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static void filterTypes(List<? extends Reference> refs, Pattern filterPattern) {
        for (Iterator<? extends Reference> it = refs.iterator(); it.hasNext();) {
            Reference ref = it.next();
            String name = ReferenceUtil.getReferenceString(ref);
            Matcher m = filterPattern.matcher(name);
            if (!m.find()) {
                it.remove();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        OptionBean bean = new OptionBean();
        CmdLineParser parser = new CmdLineParser(bean);
        parser.parseArgument(args);
        if (bean.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(bean.getOutputAPILevel());
        List<BuilderClassDef> classes = Dexifier.dexifySmaliFiles(bean.getInFile(), dexBuilder);
        if (bean.getClassFilter() != null) {
            filterTypes(classes, bean.getClassFilter());
        }

        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (BuilderClassDef klazz : classes) {
            methods.addAll(klazz.getMethods());
        }
        if (bean.getMethodFilter() != null) {
            filterTypes(methods, bean.getMethodFilter());
        }

        VirtualMachine vm = new VirtualMachine(classes, bean.getMaxNodeVisits(), bean.getMaxCallDepth());

        // TODO: investigate sorting methods by implementation size. maybe shorter methods can be optimized more easily
        // and will speed up optimizations of dependent methods.
        Set<String> finishedMethods = new HashSet<String>();
        for (BuilderMethod method : methods) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            if (methodDescriptor.endsWith("-><clinit>()V")) {
                // Static class initialization is handled by the VM.
                // TODO: main shouldn't need to know how to do this!
                continue;
            }

            if (finishedMethods.contains(methodDescriptor)) {
                continue;
            }

            System.out.println("Executing: " + methodDescriptor);
            ContextGraph graph = vm.execute(methodDescriptor);
            if (graph == null) {
                System.out.println("Skipping " + methodDescriptor);
                finishedMethods.add(methodDescriptor);
                continue;
            }

            Optimizer opt = new Optimizer(graph, method, vm, dexBuilder);
            boolean madeChanges = opt.simplify(bean.getMaxOptimizationPasses());
            if (madeChanges) {
                /*
                 * All objects associated with this method (dexbuilder, implementation) have been updated in the
                 * optimizer. Poke the VM to re-build the graph based on them.
                 */
                vm.updateInstructionGraph(methodDescriptor);
            } else {
                finishedMethods.add(methodDescriptor);
            }
        }

        File outFile = bean.getOutFile();
        System.out.println("Writing result to " + outFile);
        dexBuilder.writeTo(new FileDataStore(outFile));
    }

}
