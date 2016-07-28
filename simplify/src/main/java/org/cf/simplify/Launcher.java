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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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

    private static void filterSupportLibrary(Collection<VirtualMethod> localMethods) {
        for (Iterator<VirtualMethod> it = localMethods.iterator(); it.hasNext(); ) {
            String name = it.next().getSignature();
            if (SUPPORT_LIBRARY_PATTERN.matcher(name).find()) {
                it.remove();
            }
        }
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
            ch.qos.logback.classic.Logger rootLogger =
                    (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.OFF);
            return;
        }

        if (bean.getVerbosity() == 1) {
            ch.qos.logback.classic.Logger rootLogger =
                    (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.INFO);
        } else if (bean.getVerbosity() == 2) {
            ch.qos.logback.classic.Logger rootLogger =
                    (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.DEBUG);
        } else if (bean.getVerbosity() == 3) {
            // Ok, you asked for it.
            ch.qos.logback.classic.Logger rootLogger =
                    (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.TRACE);
        }
    }

    private static void updateZip(File zip, File entry, String entryName) throws IOException {
        Map<String, String> env = new HashMap<>();
        String uriPath = "jar:file:" + zip.getAbsolutePath();
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

        formatter
                .printHelp("java -jar simplify.jar <input> [options]", "deobfuscates a dalvik executable", options, "");
    }

    public void run(String[] args) throws IOException, UnhandledVirtualException {
        opts = getOptions(args);

        setLogLevel(opts);
        if (log.isInfoEnabled()) {
            log.info("Options:\n{}", opts.toString());
        }

        long startTime = System.currentTimeMillis();
        VirtualMachine vm = vmFactory.build(opts.getInFile(), opts.getOutputAPILevel(), opts.getMaxAddressVisits(),
                opts.getMaxCallDepth(), opts.getMaxMethodVisits(), opts.getMaxExecutionTime());
        ClassManager classManager = vm.getClassManager();
        Set<String> classNames = classManager.getNonFrameworkClassNames();
        int classCount = 0;
        int methodCount = 0;
        for (String className : classNames) {
            int newCount = executeClass(vm, className);
            classCount += newCount > 0 ? 1 : 0;
            methodCount += newCount;
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println(
                "Simplified " + methodCount + " methods from " + classCount + " classes in " + totalTime + " ms.");
        System.out.println(Optimizer.getTotalOptimizationCounts());

        System.out.println("Writing output to " + opts.getOutFile());
        classManager.getDexBuilder().writeTo(new FileDataStore(opts.getOutDexFile()));
        if (opts.isZip()) {
            Files.copy(opts.getInFile().toPath(), opts.getOutFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            updateZip(opts.getOutFile(), opts.getOutDexFile(), "classes.dex");
        }
    }

    private int executeClass(VirtualMachine vm, String className) {
        ClassManager classManager = vm.getClassManager();
        DexBuilder dexBuilder = classManager.getDexBuilder();
        Collection<VirtualMethod> methods = classManager.getVirtualClass(className).getMethods();
        filterMethods(methods, opts.getIncludeFilter(), opts.getExcludeFilter());
        if (!opts.includeSupportLibrary()) {
            filterSupportLibrary(methods);
        }

        for (VirtualMethod method : methods) {
            boolean executeAgain;
            do {
                System.out.println("Executing: " + method);
                ExecutionGraph graph = null;
                try {
                    graph = vm.execute(method);
                } catch (VirtualMachineException e) {
                    System.err.println("Aborting execution; exception: " + e);
                }

                if (null == graph) {
                    System.out.println("Skipping " + method);
                    break;
                }

                Optimizer optimizer = new Optimizer(graph, method, vm, dexBuilder, opts);
                optimizer.simplify(opts.getMaxOptimizationPasses());
                if (optimizer.madeChanges()) {
                    // Optimizer changed the implementation. Re-build graph to include changes.
                    vm.updateInstructionGraph(method);
                }
                System.out.println(optimizer.getOptimizationCounts());

                executeAgain = optimizer.shouldReexecute();
            } while (executeAgain);
        }

        return methods.size();
    }

}
