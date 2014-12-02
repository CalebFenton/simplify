package org.cf.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.SmaliFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliFileFactory {

    private static final Logger log = LoggerFactory.getLogger(SmaliFileFactory.class.getSimpleName());

    private static Set<SmaliFile> frameworkSmaliFiles;
    private static Set<String> frameworkClasses;

    public static Set<SmaliFile> getSmaliFiles(String path) throws IOException {
        return getSmaliFiles(new String[] { path });
    }

    public static Set<SmaliFile> getSmaliFiles(String[] paths) throws IOException {
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = new File(paths[i]);
        }

        return getSmaliFiles(files);
    }

    public static Set<SmaliFile> getSmaliFiles(File file) throws IOException {
        return getSmaliFiles(new File[] { file });
    }

    public static boolean isFrameworkClass(String className) {
        return frameworkClasses.contains(className);
    }

    private static void cacheFrameworkSmaliFiles() throws IOException {
        SmaliFileFactory.frameworkSmaliFiles = new HashSet<SmaliFile>();
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
                    frameworkSmaliFiles.add(smaliFile);
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
                        frameworkSmaliFiles.add(smaliFile);
                    }
                } catch (URISyntaxException ex) {
                    // never happens (famous last words, ikr?)
                }
            }
        }

        frameworkClasses = new HashSet<String>();
        for (SmaliFile sf : frameworkSmaliFiles) {
            sf.setIsResource(true);
            frameworkClasses.add(sf.getClassName());
        }

        if (log.isDebugEnabled()) {
            log.debug("Located " + frameworkSmaliFiles.size() + " framework classes.");
        }

    }

    public static Set<SmaliFile> getSmaliFiles(File[] files) throws IOException {
        Set<SmaliFile> smaliFiles = new HashSet<SmaliFile>();
        if (null == SmaliFileFactory.frameworkSmaliFiles) {
            cacheFrameworkSmaliFiles();
        }
        smaliFiles.addAll(frameworkSmaliFiles);

        for (File file : files) {
            List<File> matches = getFilesWithSmaliExtension(file);
            for (File match : matches) {
                SmaliFile smaliFile = new SmaliFile(match);
                smaliFiles.add(smaliFile);
            }
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
