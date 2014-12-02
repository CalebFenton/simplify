package org.cf.simplify;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class Main {

    public static void main(String[] args) throws Exception {
        OptionBean bean = new OptionBean();
        CmdLineParser parser = new CmdLineParser(bean);
        parser.parseArgument(args);
        if (bean.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        setLogLevel(bean);

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(bean.getOutputAPILevel());
        SmaliClassManager classManager = new SmaliClassManager(bean.getInFile(), dexBuilder);
        VirtualMachine vm = new VirtualMachine(classManager, bean.getMaxAddressVisits(), bean.getMaxCallDepth(),
                        bean.getMaxMethodVisits());

        for (String className : classManager.getNonFrameworkClassNames()) {
            Set<String> methodDescriptors = classManager.getMethodDescriptors(className);
            filterMethods(methodDescriptors, bean.getIncludeFilter(), bean.getExcludeFilter());

            for (String methodDescriptor : methodDescriptors) {
                System.out.println("Executing: " + methodDescriptor);
                ExecutionGraph graph = vm.execute(methodDescriptor);
                if (graph == null) {
                    System.out.println("Skipping " + methodDescriptor);
                    continue;
                }

                BuilderMethod method = classManager.getMethod(methodDescriptor);
                Optimizer opt = new Optimizer(graph, method, vm, dexBuilder);
                boolean madeChanges = opt.simplify(bean.getMaxOptimizationPasses());
                if (madeChanges) {
                    // Optimizer changed the implementation. Re-build graph based on changes.
                    vm.updateInstructionGraph(methodDescriptor);
                }
            }
        }

        File outFile = bean.getOutFile();
        System.out.println("Writing result to " + outFile);
        dexBuilder.writeTo(new FileDataStore(outFile));
    }

    private static void filterMethods(Collection<String> methodDescriptors, Pattern positive, Pattern negative) {
        for (Iterator<String> it = methodDescriptors.iterator(); it.hasNext();) {
            String name = it.next();
            if ((positive != null) && !positive.matcher(name).find()) {
                it.remove();
            }

            if ((negative != null) && negative.matcher(name).find()) {
                it.remove();
            }
        }
    }

    private static void setLogLevel(OptionBean bean) {
        if (bean.isVerbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("info"));
        } else if (bean.isVverbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("debug"));
        } else if (bean.isVvverbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.toLevel("trace"));
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

}
