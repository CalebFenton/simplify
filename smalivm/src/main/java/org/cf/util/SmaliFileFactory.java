package org.cf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.SmaliFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliFileFactory {

    private static final Logger log = LoggerFactory.getLogger(SmaliFileFactory.class.getSimpleName());

    private Map<String, SmaliFile> frameworkClassNameToSmaliFile;

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

    public Set<SmaliFile> getSmaliFiles(File file) throws IOException {
        return getSmaliFiles(new File[] { file });
    }

    public boolean isFrameworkClass(String className) {
        return frameworkClassNameToSmaliFile.containsKey(className);
    }

    private void cacheFrameworkSmaliFiles() throws IOException {
        frameworkClassNameToSmaliFile = new HashMap<String, SmaliFile>();
        final File jarFile = new File(SmaliFileFactory.class.getProtectionDomain().getCodeSource().getLocation()
                        .getPath());
        if (jarFile.isFile()) { // Run when JAR
            final JarFile jar = new JarFile(jarFile);
            final Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith("reflib/android-17/") && name.toLowerCase().endsWith(".smali")) {
                    StringBuilder sb = new StringBuilder("/");
                    sb.append(name);
                    name = sb.toString();
                    InputStream is = SmaliFileFactory.class.getResourceAsStream(name);
                    SmaliFile smaliFile = new SmaliFile(name, is);
                    frameworkClassNameToSmaliFile.put(smaliFile.getClassName(), smaliFile);
                }
            }
            jar.close();
        } else { // Run with IDE
            final URL url = SmaliFileFactory.class.getResource("/reflib/android-17");
            if (url != null) {
                try {
                    List<File> resFiles = (List<File>) FileUtils.listFiles(new File(url.toURI()),
                                    new String[] { "smali" }, true);
                    for (File resFile : resFiles) {
                        String absPath = resFile.getAbsolutePath();
                        int pos = absPath.lastIndexOf("/reflib/android-17/");
                        String path = absPath.substring(pos);
                        InputStream is = SmaliFileFactory.class.getResourceAsStream(path);
                        // Smali files that are resources expect resource paths, not absolute paths
                        SmaliFile smaliFile = new SmaliFile(path, is);
                        frameworkClassNameToSmaliFile.put(smaliFile.getClassName(), smaliFile);
                    }
                } catch (URISyntaxException ex) {
                    // never happens (famous last words, ikr?)
                }
            }
        }

        for (SmaliFile sf : frameworkClassNameToSmaliFile.values()) {
            sf.setIsResource(true);
        }

        if (log.isDebugEnabled()) {
            log.debug("Located " + frameworkClassNameToSmaliFile.size() + " framework classes.");
        }
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

        if (null == frameworkClassNameToSmaliFile) {
            cacheFrameworkSmaliFiles();
        }

        for (Entry<String, SmaliFile> entry : frameworkClassNameToSmaliFile.entrySet()) {
            String className = entry.getKey();
            if (inputClasses.contains(className)) {
                // Do not override input class with framework class
                frameworkClassNameToSmaliFile.remove(className);
            }
            smaliFiles.addAll(frameworkClassNameToSmaliFile.values());
        }

        return smaliFiles;
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
