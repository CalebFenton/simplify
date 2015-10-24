package org.cf.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.SmaliFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliFileFactory {

    private static Map<String, SmaliFile> frameworkCache;

    private static final Logger log = LoggerFactory.getLogger(SmaliFileFactory.class.getSimpleName());

    private Map<String, SmaliFile> frameworkClassNameToSmaliFile;

    private synchronized void cacheFramework() throws IOException {
        if (frameworkCache != null) {
            return;
        }

        frameworkCache = new HashMap<String, SmaliFile>();

        long startTime = System.currentTimeMillis();
        List<String> frameworkClassesCfg = ConfigLoader.loadConfig("framework_classes.cfg");
        Set<String> safeFrameworkClasses = new HashSet<String>(ConfigLoader.loadConfig("safe_framework_classes.cfg"));
        for (String line : frameworkClassesCfg) {
            String[] parts = line.split(":");
            String className = parts[0];
            String path = parts[1];
            SmaliFile smaliFile = new SmaliFile(path, className);
            smaliFile.setIsResource(true);
            smaliFile.setIsSafeFramework(safeFrameworkClasses.contains(className));
            frameworkCache.put(smaliFile.getClassName(), smaliFile);
        }

        if (log.isDebugEnabled()) {
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime; // assuming time has not gone backwards
            StringBuilder sb = new StringBuilder();
            sb.append("Cached ").append(frameworkCache.size()).append(" framework classes in ").append(totalTime)
                            .append(" ms.");
            log.debug(sb.toString());
        }
    }

    public Set<SmaliFile> getSmaliFiles(File file) throws IOException {
        return getSmaliFiles(new File[] { file });
    }

    public Set<SmaliFile> getSmaliFiles(File[] files) throws IOException {
        Set<SmaliFile> smaliFiles = new HashSet<SmaliFile>();
        Set<String> inputClasses = new HashSet<String>();
        for (File file : files) {
            List<File> matches = getFilesWithSmaliExtension(file);
            for (File match : matches) {
                SmaliFile smaliFile = new SmaliFile(match);
                smaliFiles.add(smaliFile);
                inputClasses.add(smaliFile.getClassName());
            }
        }

        cacheFramework();

        // Input classes take precedence over framework classes
        frameworkClassNameToSmaliFile = new HashMap<String, SmaliFile>(frameworkCache);
        for (Map.Entry<String, SmaliFile> entry : frameworkCache.entrySet()) {
            if (!inputClasses.contains(entry.getKey())) {
                frameworkClassNameToSmaliFile.put(entry.getKey(), entry.getValue());
            }
        }
        smaliFiles.addAll(frameworkClassNameToSmaliFile.values());

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

    private static List<File> getFilesWithSmaliExtension(File file) {
        List<File> files = new LinkedList<File>();
        if (file.isDirectory()) {
            files = (List<File>) FileUtils.listFiles(file, new String[] { "smali" }, true);
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".smali")) {
            files.add(file);
        }

        return files;
    }

}
