package org.cf.smalivm.dex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inputFile));
        String className = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(".class")) {
                    break;
                }
            }
            br.close();

            if (null == line) {
                throw new RuntimeException("Missing class directive in " + inputFile);
            }

            Matcher m = CLASS_PATTERN.matcher(line);
            if (!m.find()) {
                throw new RuntimeException("Strange class directive: " + line);
            }
            className = m.group(1);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read class name in " + inputFile, e);
        }

        return className;
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

    public InputStream open() throws URISyntaxException, IOException {
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
