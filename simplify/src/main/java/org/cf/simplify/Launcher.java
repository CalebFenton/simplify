package org.cf.simplify;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.cf.smalivm.UnhandledVirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineException;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.util.ConsoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.AccessMode;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Pattern;

import ch.qos.logback.classic.Level;

public class Launcher {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final Pattern SUPPORT_LIBRARY_PATTERN = Pattern.compile("Landroid/support/(annotation|v\\d{1,2})/");

    private final VirtualMachineFactory vmFactory;
    private SimplifyOptions opts;

    public Launcher(VirtualMachineFactory vmFactory) {
        this.vmFactory = vmFactory;
    }

    private static void filterMethods(Collection<VirtualMethod> methods, Pattern positive, Pattern negative) {
        for (Iterator<VirtualMethod> it = methods.iterator(); it.hasNext(); ) {
            String name = it.next().getSignature();
            if (positive != null && !positive.matcher(name).find()) {
                it.remove();
            } else if (negative != null && negative.matcher(name).find()) {
                it.remove();
            }
        }
    }

    private static void filterSupportLibrary(Collection<String> classNames) {
        classNames.removeIf(name -> SUPPORT_LIBRARY_PATTERN.matcher(name).find());
    }

    private static SimplifyOptions getOptions(String[] args) {
        SimplifyOptions opts = null;
        try {
            opts = SimplifyOptionsParser.parse(args);
        } catch (ParseException e) {
            usage(SimplifyOptionsParser.getOptions());
            System.exit(-1);
        }

        if (opts.isHelp()) {
            usage(SimplifyOptionsParser.getOptions());
            System.exit(0);
        }

        return opts;
    }

    private static void setLogLevel(SimplifyOptions bean) {
        if (bean.isQuiet()) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.OFF);
            return;
        }

        if (bean.getVerbosity() == 1) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
        } else if (bean.getVerbosity() == 2) {
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.DEBUG);
        } else if (bean.getVerbosity() == 3) {
            // Ok, you asked for it.
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.TRACE);
        }
    }

    private static void updateZip(File zip, File entry, String entryName) throws IOException {
        Map<String, String> env = new HashMap<>();
        String uriPath = "jar:" + zip.toURI().toString();
        URI uri = URI.create(uriPath);
        try (FileSystem fs = FileSystems.newFileSystem(uri, env)) {
            fs.provider().checkAccess(fs.getPath(entryName), AccessMode.READ);
            Path target = fs.getPath(entryName);
            Path source = entry.toPath();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw e;
        }
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        int consoleWidth = ConsoleUtil.getConsoleWidth();
        if (consoleWidth <= 0) {
            consoleWidth = 80;
        }
        formatter.setWidth(consoleWidth);

        formatter.printHelp("java -jar simplify.jar <input> [options]", "deobfuscates a dalvik executable", options, "");
    }

    private VirtualMachine vm;

    public void run(String[] args) throws IOException, UnhandledVirtualException {
        opts = getOptions(args);

        setLogLevel(opts);
        if (log.isInfoEnabled()) {
            log.info("Options:\n{}", opts.toString());
        }

        RuntimeStats stats = new RuntimeStats();
        stats.begin();

        vm = vmFactory
                .build(opts.getInFile(), opts.getOutputAPILevel(), opts.getMaxAddressVisits(), opts.getMaxCallDepth(), opts.getMaxMethodVisits(),
                        opts.getMaxExecutionTime());
        ClassManager classManager = vm.getClassManager();
        Set<String> classNames = classManager.getNonFrameworkClassNames();
        if (!opts.includeSupportLibrary()) {
            filterSupportLibrary(classNames);
        }
        stats.intakeClasses(classNames);

        for (String className : classNames) {
            stats.incrementCurrentClassIndex();
            System.out.println("[" + stats.getCurrentClassIndex() + " / " + stats.getTotalClasses() + "] Processing top level class " + className);
            executeClass(className, stats);
        }

        stats.end();
        System.out.println("Simplification complete:\n" + stats.getStats());
        System.out.println(Optimizer.getTotalOptimizationCounts());

        System.out.println("Writing output to " + opts.getOutFile());
        classManager.getDexBuilder().writeTo(new FileDataStore(opts.getOutDexFile()));
        if (opts.isZip()) {
            Files.copy(opts.getInFile().toPath(), opts.getOutFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            updateZip(opts.getOutFile(), opts.getOutDexFile(), "classes.dex");
        }
    }

    private void executeClass(String className, RuntimeStats stats) {
        ClassManager classManager = vm.getClassManager();
        DexBuilder dexBuilder = classManager.getDexBuilder();
        Collection<VirtualMethod> methods = classManager.getVirtualClass(className).getMethods();
        filterMethods(methods, opts.getIncludeFilter(), opts.getExcludeFilter());

        stats.intakeMethods(methods);
        for (VirtualMethod method : methods) {
            stats.incrementCurrentMethodIndex();

            if (!method.hasImplementation()) {
                System.out.println("[" + stats.getCurrentMethodIndex() + " / " + stats
                        .getTotalMethods() + "] Skipping top level method without implementation: " + method);
                continue;
            }

            boolean executeAgain;
            do {
                System.out.println("(" + stats.getCurrentMethodIndex() + " / " + stats.getTotalMethods() + ") Executing top level method: " + method);
                ExecutionGraph graph = null;
                try {
                    graph = vm.execute(method);
                } catch (VirtualMachineException e) {
                    System.err.println("Aborting execution; exception: " + e);
                } catch (Throwable e1) {
                    if (opts.ignoreErrors()) {
                        System.err.println("Unexpected, non-virtual exception executing " + method + ", skipping");
                        e1.printStackTrace();
                        stats.incrementFailedMethodCount();
                        vm = vmFactory
                                .build(classManager, opts.getMaxAddressVisits(), opts.getMaxCallDepth(), opts.getMaxMethodVisits(),
                                        opts.getMaxExecutionTime());
                        break;
                    } else {
                        throw e1;
                    }
                }

                if (null == graph) {
                    System.out.println("Skipping optimization of " + method + "; null execution graph");
                    stats.incrementFailedMethodCount();
                    break;
                }

                Optimizer optimizer = new Optimizer(graph, method, vm, dexBuilder, opts);
                try {
                    optimizer.simplify(opts.getMaxOptimizationPasses());
                } catch (Throwable e1) {
                    if (opts.ignoreErrors()) {
                        System.err.println("Exception optimizing " + method + ", skipping");
                        e1.printStackTrace();
                        stats.incrementFailedMethodCount();
                        vm = vmFactory
                                .build(classManager, opts.getMaxAddressVisits(), opts.getMaxCallDepth(), opts.getMaxMethodVisits(),
                                        opts.getMaxExecutionTime());
                        break;
                    } else {
                        throw e1;
                    }
                }
                if (optimizer.madeChanges()) {
                    // Optimizer changed the implementation. Re-build graph to include changes.
                    vm.updateInstructionGraph(method);
                }
                System.out.println(optimizer.getOptimizationCounts());

                executeAgain = optimizer.shouldReexecute();
            } while (executeAgain);
            stats.incrementOptimizedMethodCount();
        }
    }

    private class RuntimeStats {
        int totalClasses;
        int totalMethods;
        int currentClassIndex;
        int currentMethodIndex;
        int failedMethodCount;
        int optimizedMethodCount;
        long startTime;
        long endTime;

        RuntimeStats() {
        }

        void intakeClasses(Collection classes) {
            totalClasses = classes.size();
        }

        void intakeMethods(Collection methods) {
            totalMethods = methods.size();
        }

        void incrementCurrentClassIndex() {
            currentClassIndex++;
        }

        void incrementCurrentMethodIndex() {
            currentMethodIndex++;
        }

        void incrementOptimizedMethodCount() {
            optimizedMethodCount++;
        }

        void incrementFailedMethodCount() {
            failedMethodCount++;
        }

        void begin() {
            startTime = System.currentTimeMillis();
        }

        void end() {
            endTime = System.currentTimeMillis();
        }

        String getStats() {
            long totalTime = endTime - startTime;
            return "\ttotal classes = " + totalClasses + "\n" +
                    "\ttotal methods = " + totalMethods + "\n" +
                    "\toptimized methods = " + optimizedMethodCount + "\n" +
                    "\tfailed methods = " + failedMethodCount + "\n" +
                    "\trun time = " + totalTime + " ms";
        }

        int getTotalClasses() {
            return totalClasses;
        }

        int getTotalMethods() {
            return totalMethods;
        }

        int getCurrentClassIndex() {
            return currentClassIndex;
        }

        int getCurrentMethodIndex() {
            return currentMethodIndex;
        }

        int getFailedMethodCount() {
            return failedMethodCount;
        }

        int getOptimizedMethodCount() {
            return optimizedMethodCount;
        }
    }
}
