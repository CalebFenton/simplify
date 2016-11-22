package org.cf.smalivm.dex;

import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.configuration.ConfigurationLoader;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmaliFileFactory {

    private static final Logger log = LoggerFactory.getLogger(SmaliFileFactory.class.getSimpleName());
    private static Map<String, SmaliFile> frameworkClassNameToSmaliFile;

    private static synchronized void cacheFramework() {
        if (frameworkClassNameToSmaliFile != null) {
            return;
        }

        long startTime = System.currentTimeMillis();
        frameworkClassNameToSmaliFile = parseFramework();

        if (log.isDebugEnabled()) {
            long endTime = System.currentTimeMillis();
            // assuming time has not gone backwards
            long totalTime = endTime - startTime;
            log.debug("Cached {} framework classes in {} seconds.", frameworkClassNameToSmaliFile.size(),
                    totalTime / 1000);
        }
    }

    private static Map<String, SmaliFile> parseFramework() {
        Map<String, SmaliFile> frameworkFiles = new HashMap<>();
        // framework_classes.cfg is built by FrameworkCacheBuilder
        List<String> frameworkClassesCfg = ConfigurationLoader.load(Configuration.FRAMEWORK_CLASSES_PATH);
        Set<String> safeFrameworkClasses = new HashSet<>(ConfigurationLoader.load(Configuration.SAFE_FRAMEWORK_CLASSES_PATH));
        for (String line : frameworkClassesCfg) {
            String[] parts = line.split(":");
            String className = parts[0];
            String path = parts[1];
            SmaliFile smaliFile = new SmaliFile(path, className);
            smaliFile.setIsResource(true);
            smaliFile.setIsSafeFramework(safeFrameworkClasses.contains(className));
            frameworkFiles.put(smaliFile.getClassName(), smaliFile);
        }

        return frameworkFiles;
    }

    public Set<SmaliFile> getSmaliFiles(File file) throws IOException {
        return getSmaliFiles(new File[] { file });
    }

    public Set<SmaliFile> getSmaliFiles(File[] files) throws IOException {
        Set<SmaliFile> smaliFiles = getSmaliFiles();
        for (File file : files) {
            List<File> matches = Utils.getFilesWithSmaliExtension(file);
            for (File match : matches) {
                SmaliFile smaliFile = new SmaliFile(match);
                // DalvikVM rejects classes in an APK that are already defined.
                // Framework classes take precedence over local classes.
                String className = smaliFile.getClassName();
                if (isFrameworkClass(className) && !className.startsWith("Landroid/support/")) {
                    log.warn("Input class '{}' has an earlier definition; ignoring", className);
                } else {
                    smaliFiles.add(smaliFile);
                }
            }
        }

        return smaliFiles;
    }

    public Set<SmaliFile> getSmaliFiles() {
        cacheFramework();

        Set<SmaliFile> smaliFiles = new HashSet<>();
        smaliFiles.addAll(frameworkClassNameToSmaliFile.values());

        return smaliFiles;
    }

    public boolean isFrameworkClass(String className) {
        return frameworkClassNameToSmaliFile.containsKey(className);
    }

    public Set<SmaliFile> getSmaliFiles(String path) throws IOException {
        return getSmaliFiles(new String[] { path });
    }

    public Set<SmaliFile> getSmaliFiles(String[] paths) throws IOException {
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = new File(paths[i]);
        }

        return getSmaliFiles(files);
    }

    public boolean isSafeFrameworkClass(String className) {
        SmaliFile smaliFile = frameworkClassNameToSmaliFile.get(className);

        return null != smaliFile && smaliFile.isSafeFrameworkClass();
    }

}
