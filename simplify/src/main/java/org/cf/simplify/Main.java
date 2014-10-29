package org.cf.simplify;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
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

import ch.qos.logback.classic.Level;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        OptionBean bean = new OptionBean();
        CmdLineParser parser = new CmdLineParser(bean);
        parser.parseArgument(args);
        if (bean.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        if (bean.isVerbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("debug"));
        } else if (bean.isVverbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("trace"));
        }
        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(bean.getOutputAPILevel());
        List<BuilderClassDef> classes = Dexifier.dexifySmaliFiles(bean.getInFile(), dexBuilder);

        List<BuilderMethod> methods = new ArrayList<BuilderMethod>();
        for (BuilderClassDef klazz : classes) {
            methods.addAll(klazz.getMethods());
        }
        filterTypes(methods, bean.getIncludeFilter(), bean.getExcludeFilter());

        VirtualMachine vm = new VirtualMachine(classes, bean.getMaxAddressVisits(), bean.getMaxCallDepth(),
                        bean.getMaxMethodVisits());

        // TODO: investigate sorting methods by implementation size. maybe shorter methods can be optimized more easily
        // and will speed up optimizations of dependent methods.
        Set<String> finishedMethods = new HashSet<String>();
        for (BuilderMethod method : methods) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            if (finishedMethods.contains(methodDescriptor)) {
                continue;
            }

            System.out.println("Executing: " + methodDescriptor);
            ExecutionGraph graph = vm.execute(methodDescriptor);
            if (graph == null) {
                System.out.println("Skipping " + methodDescriptor);
                finishedMethods.add(methodDescriptor);
                continue;
            }

            Optimizer opt = new Optimizer(graph, method, vm, dexBuilder);
            boolean madeChanges = opt.simplify(bean.getMaxOptimizationPasses());
            if (madeChanges) {
                // Optimizer changed the implementation. Re-build graph based on changes.
                vm.updateInstructionGraph(methodDescriptor);
            } else {
                finishedMethods.add(methodDescriptor);
            }
        }

        File outFile = bean.getOutFile();
        System.out.println("Writing result to " + outFile);
        dexBuilder.writeTo(new FileDataStore(outFile));
    }

    private static void filterTypes(List<? extends Reference> refs, Pattern positive, Pattern negative) {
        for (Iterator<? extends Reference> it = refs.iterator(); it.hasNext();) {
            Reference ref = it.next();
            String name = ReferenceUtil.getReferenceString(ref);
            if ((positive != null) && !positive.matcher(name).find()) {
                it.remove();
            }

            if ((negative != null) && negative.matcher(name).find()) {
                it.remove();
            }
        }
    }

}
