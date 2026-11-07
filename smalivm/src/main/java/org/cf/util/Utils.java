package org.cf.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.LinkedList;
import java.util.List;

public class Utils {

    private static final PathMatcher SMALI_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.smali");

    public static List<File> getFilesWithSmaliExtension(File file) {
        final List<File> files = new LinkedList<>();
        if (file.isDirectory()) {
            try {
                java.nio.file.Files.walk(file.toPath()).forEach(filePath -> {
                    if (java.nio.file.Files.isRegularFile(filePath)) {
                        if (SMALI_MATCHER.matches(filePath)) {
                            files.add(filePath.toFile());
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".local")) {
            files.add(file);
        }

        return files;
    }

}
