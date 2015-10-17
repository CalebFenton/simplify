package org.cf.simplify;

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

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxExecutionTimeExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public class Launcher {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    private static final Pattern SUPPORT_LIBRARY_PATTERN = Pattern.compile("Landroid/support/(annotation|v\\d{1,2})/");

    private final VirtualMachineFactory vmFactory;
    private Options opts;

    public Launcher(VirtualMachineFactory vmFactory) {
        this.vmFactory = vmFactory;
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
        System.out.println("Simplified " + methodCount + " methods from " + classCount + " classes in " + totalTime + " ms.");
        System.out.println(Optimizer.getTotalOptimizationCounts());

        System.out.println("Writing output to " + opts.getOutFile());
        classManager.getDexBuilder().writeTo(new FileDataStore(opts.getOutDexFile()));
        if (opts.isApk()) {
            FileUtils.copyFile(opts.getInFile(), opts.getOutFile());
            updateZip(opts.getOutFile(), opts.getOutDexFile(), "classes.dex");
        }
    }

    private int executeClass(VirtualMachine vm, String className) throws UnhandledVirtualException {
        ClassManager classManager = vm.getClassManager();
        DexBuilder dexBuilder = classManager.getDexBuilder();
        Set<String> methodDescriptors = classManager.getMethodDescriptors(className);
        filterMethods(methodDescriptors, opts.getIncludeFilter(), opts.getExcludeFilter());
        if (!opts.includeSupportLibrary()) {
            filterSupportLibrary(methodDescriptors);
        }

        for (String methodDescriptor : methodDescriptors) {
            boolean shouldReexecute = false;
            do {
                System.out.println("Executing: " + methodDescriptor);
                ExecutionGraph graph = null;
                try {
                    graph = vm.execute(methodDescriptor);
                } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded | MaxExecutionTimeExceeded e) {
                    System.err.println("Aborting execution: " + e);
                }

                if (null == graph) {
                    System.out.println("Skipping " + methodDescriptor);
                    break;
                }

                BuilderMethod method = classManager.getMethod(methodDescriptor);
                Optimizer optimizer = new Optimizer(graph, method, vm, dexBuilder, opts);
                optimizer.simplify(opts.getMaxOptimizationPasses());
                if (optimizer.madeChanges()) {
                    // Optimizer changed the implementation. Re-build graph to include changes.
                    vm.updateInstructionGraph(methodDescriptor);
                }
                System.out.println(optimizer.getOptimizationCounts());

                shouldReexecute = optimizer.shouldReexecute();
            } while (shouldReexecute);
        }

        return methodDescriptors.size();
    }

    private static void filterMethods(Collection<String> methodDescriptors, Pattern positive, Pattern negative) {
        for (Iterator<String> it = methodDescriptors.iterator(); it.hasNext();) {
            String name = it.next();
            if ((positive != null) && !positive.matcher(name).find()) {
                it.remove();
            } else if ((negative != null) && negative.matcher(name).find()) {
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

    private static Options getOptions(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser(opts);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.out);
            System.exit(-1);
        }
        if (opts.isHelp()) {
            parser.printUsage(System.out);
            System.exit(0);
        }

        return opts;
    }

    private static void setLogLevel(Options bean) {
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
            // Ok, you asked for it.
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                            .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.TRACE);
        }
    }

    private static void updateZip(File zip, File entry, String entryName) throws IOException {
        Map<String, String> env = new HashMap<String, String>();
        String uriPath = "jar:file:" + zip.getAbsolutePath();
        URI uri = URI.create(uriPath);
        FileSystem fs = FileSystems.newFileSystem(uri, env);
        try {
            fs.provider().checkAccess(fs.getPath(entryName), AccessMode.READ);
            Path target = fs.getPath(entryName);
            Path source = entry.toPath();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw e;
        } finally {
            fs.close();
        }
    }

}
