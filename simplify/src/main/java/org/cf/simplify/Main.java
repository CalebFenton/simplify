package org.cf.simplify;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final Pattern SUPPORT_LIBRARY_PATTERN = Pattern.compile("Landroid/support/(annotation|v\\d{1,2})/");

    public static void main(String[] args) throws Exception {
        OptionBean opts = new OptionBean();
        CmdLineParser parser = new CmdLineParser(opts);
        parser.parseArgument(args);
        if (opts.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        setLogLevel(opts);
        if (log.isInfoEnabled()) {
            log.info("Options:\n" + opts.toString());
        }

        long startTime = System.currentTimeMillis();

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(opts.getOutputAPILevel());
        SmaliClassManager classManager = new SmaliClassManager(opts.getInFile(), dexBuilder);
        VirtualMachine vm = new VirtualMachine(classManager, opts.getMaxAddressVisits(), opts.getMaxCallDepth(),
                        opts.getMaxMethodVisits());

        Set<String> classNames = classManager.getNonFrameworkClassNames();
        for (String className : classNames) {
            Set<String> methodDescriptors = classManager.getMethodDescriptors(className);
            filterMethods(methodDescriptors, opts.getIncludeFilter(), opts.getExcludeFilter());
            if (!opts.includeSupportLibrary()) {
                filterSupportLibrary(methodDescriptors);
            }

            for (String methodDescriptor : methodDescriptors) {
                boolean shouldExecuteAgain = false;
                do {
                    System.out.println("Executing: " + methodDescriptor);
                    ExecutionGraph graph = null;
                    try {
                        graph = vm.execute(methodDescriptor);
                    } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded e) {
                        System.err.println("Max visitation exception: " + e);
                    }

                    if (null == graph) {
                        System.out.println("Skipping " + methodDescriptor);
                        break;
                    }

                    BuilderMethod method = classManager.getMethod(methodDescriptor);
                    Optimizer optimizer = new Optimizer(graph, method, vm, dexBuilder);
                    optimizer.simplify(opts.getMaxOptimizationPasses());
                    if (optimizer.madeChanges()) {
                        // Optimizer changed the implementation. Re-build graph to include changes.
                        vm.updateInstructionGraph(methodDescriptor);
                    }
                    System.out.println(optimizer.getOptimizationCounts());

                    shouldExecuteAgain = optimizer.getShouldExecuteAgain();
                } while (shouldExecuteAgain);
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Simplified " + classNames.size() + " classes in " + totalTime + " ms.");
        System.out.println(Optimizer.getTotalOptimizationCounts());

        File outFile = opts.getOutFile();
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

    private static void filterSupportLibrary(Collection<String> methodDescriptors) {
        for (Iterator<String> it = methodDescriptors.iterator(); it.hasNext();) {
            String name = it.next();
            if (SUPPORT_LIBRARY_PATTERN.matcher(name).find()) {
                it.remove();
            }
        }
    }

    private static void setLogLevel(OptionBean bean) {
        if (bean.isQuiet()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.OFF);
            return;
        }

        if (bean.isVerbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
        } else if (bean.isVverbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.DEBUG);
        } else if (bean.isVvverbose()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.TRACE);
        }
    }

}
