package org.cf.smalivm.dex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SmaliFile {

    private static final Pattern CLASS_PATTERN = Pattern.compile(" (L[^;]+;)");

    private final String path;
    private final String className;
    private boolean isResource;
    private boolean isSafeFramework;

    SmaliFile(File file) throws FileNotFoundException {
        this(file.getAbsolutePath(), getClassName(file));
    }

    SmaliFile(String path, String className) {
        this.path = path;
        this.className = className;
    }

    private static String getClassName(File inputFile) throws FileNotFoundException {
        try {
            Path myPath = Paths.get(inputFile.toURI());
            Stream<String> lines = Files.lines(myPath);
            Optional<String> firstClassLine = lines.filter(s -> s.startsWith(".class ")).findFirst();
            lines.close();

            if (!firstClassLine.isPresent()) {
                throw new RuntimeException("Missing class directive in " + inputFile);
            }

            String line = firstClassLine.get();
            Matcher m = CLASS_PATTERN.matcher(line);
            if (!m.find()) {
                throw new RuntimeException("Strange class directive: " + line);
            }

            return m.group(1);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read class name in " + inputFile, e);
        }
    }

    public String getClassName() {
        return className;
    }

    public boolean isResource() {
        return isResource;
    }

    public void setIsSafeFramework(boolean isSafeFramework) {
        this.isSafeFramework = isSafeFramework;
    }

    public boolean isSafeFrameworkClass() {
        return isSafeFramework;
    }

    public InputStream open() throws IOException {
        if (isResource) {
            return SmaliFile.class.getClassLoader().getResourceAsStream(getPath());
        } else {
            return new FileInputStream(new File(path));
        }
    }

    public String getPath() {
        return path;
    }

    public void setIsResource(boolean isResource) {
        this.isResource = isResource;
    }

    @Override
    public String toString() {
        return path;
    }
}
