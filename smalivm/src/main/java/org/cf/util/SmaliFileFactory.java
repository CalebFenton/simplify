package org.cf.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.SmaliFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliFileFactory {

    private static Map<String, SmaliFile> frameworkClassNameToSmaliFile;

    private static final Logger log = LoggerFactory.getLogger(SmaliFileFactory.class.getSimpleName());

    private static synchronized void cacheFramework() throws IOException {
        if (frameworkClassNameToSmaliFile != null) {
            return;
        }

        frameworkClassNameToSmaliFile = new HashMap<String, SmaliFile>();

        long startTime = System.currentTimeMillis();
        List<String> frameworkClassesCfg = ConfigurationLoader.loadConfig("framework_classes.cfg");
        Set<String> safeFrameworkClasses = new HashSet<String>(
                        ConfigurationLoader.loadConfig("safe_framework_classes.cfg"));
        for (String line : frameworkClassesCfg) {
            String[] parts = line.split(":");
            String className = parts[0];
            String path = parts[1];
            SmaliFile smaliFile = new SmaliFile(path, className);
            smaliFile.setIsResource(true);
            smaliFile.setIsSafeFramework(safeFrameworkClasses.contains(className));
            frameworkClassNameToSmaliFile.put(smaliFile.getClassName(), smaliFile);
        }

        if (log.isDebugEnabled()) {
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime; // assuming time has not gone backwards
            log.debug("Cached {} framework classes in {} ms.", frameworkClassNameToSmaliFile.size(), totalTime);
        }
    }

    public Set<SmaliFile> getSmaliFiles(File file) throws IOException {
        return getSmaliFiles(new File[] { file });
    }

    public Set<SmaliFile> getSmaliFiles(File[] files) throws IOException {
        cacheFramework();

        Set<SmaliFile> smaliFiles = new HashSet<SmaliFile>();
        smaliFiles.addAll(frameworkClassNameToSmaliFile.values());

        for (File file : files) {
            List<File> matches = Utils.getFilesWithSmaliExtension(file);
            for (File match : matches) {
                SmaliFile smaliFile = new SmaliFile(match);
                // DalvikVM rejects classes that are already defined.
                // Framework classes take precedence over local classes.
                if (isFrameworkClass(smaliFile.getClassName())) {
                    log.warn("'{}' has an earlier definition; blocking out", smaliFile.getClassName());
                } else {
                    smaliFiles.add(smaliFile);
                }
            }
        }

        return smaliFiles;
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

    public boolean isFrameworkClass(String className) {
        return frameworkClassNameToSmaliFile.containsKey(className);
    }

    public boolean isSafeFrameworkClass(String className) {
        SmaliFile smaliFile = frameworkClassNameToSmaliFile.get(className);
        if (null == smaliFile) {
            return false;
        }

        return smaliFile.isSafeFrameworkClass();
    }

}
