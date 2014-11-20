package org.cf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliClassNameParser {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassNameParser.class.getSimpleName());

    private static final Pattern CLASS_PATTERN = Pattern.compile(" (L[^;]+;)");

    public static Map<String, File> getClassNameToFile(String path) {
        return getClassNameToFile(new File(path));
    }

    public static Map<String, File> getClassNameToFile(String[] paths) {
        File[] smaliPaths = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            smaliPaths[i] = new File(paths[i]);
        }

        return getClassNameToFile(paths);
    }

    public static Map<String, File> getClassNameToFile(File path) {
        return getClassNameToFile(new File[] { path });
    }

    public static Map<String, File> getClassNameToFile(File[] paths) {
        Map<String, File> classNameToFile = new HashMap<String, File>();
        List<File> smaliFiles = getSmaliFiles(paths);
        for (File smaliFile : smaliFiles) {
            try {
                String className = getClassName(smaliFile);
                classNameToFile.put(className, smaliFile);
            } catch (IOException e) {
                log.error("Unable to parse " + smaliFile + " for class name.", e);
            }
        }

        // Classes should only be defined in up to one file.
        return classNameToFile;
    }

    private static String getClassName(File smaliFile) throws IOException {
        FileInputStream fis = new FileInputStream(smaliFile);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().startsWith(".class")) {
                continue;
            }

            break;
        }
        br.close();

        if (null == line) {
            throw new RuntimeException("Missing class directive.");
        }

        Matcher m = CLASS_PATTERN.matcher(line);
        if (!m.find()) {
            throw new RuntimeException("Strange class directive: " + line);
        }

        return m.group(1);
    }

    private static List<File> getSmaliFiles(File[] paths) {
        List<File> smaliFiles = new LinkedList<File>();
        for (File path : paths) {
            smaliFiles.addAll(getSmaliFiles(path));
        }

        return smaliFiles;
    }

    private static List<File> getSmaliFiles(File path) {
        List<File> smaliFiles;
        if (path.isDirectory()) {
            smaliFiles = (List<File>) FileUtils.listFiles(path, new String[] { "smali" }, true);
        } else {
            smaliFiles = new LinkedList<File>();
            smaliFiles.add(path);
        }

        return smaliFiles;
    }

}
