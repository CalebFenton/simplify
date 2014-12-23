package org.cf.smalivm;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SmaliFile {

    private static final Pattern CLASS_PATTERN = Pattern.compile(" (L[^;]+;)");

    private final String path;
    private final String className;
    private boolean isResource;
    private boolean isSafeFramework;

    public SmaliFile(File file) throws FileNotFoundException {
        this(file.getAbsolutePath(), getClassName(new BufferedInputStream(new FileInputStream(file))));
    }

    public SmaliFile(String path, String className) {
        this.path = path;
        this.className = className;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SmaliFile)) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        SmaliFile rhs = (SmaliFile) obj;
        return new EqualsBuilder().append(className, rhs.className).isEquals();
    }

    public String getClassName() {
        return className;
    }

    public String getPath() {
        return path;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 61).append(className).toHashCode();
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

    private static ZipInputStream getReflibStream() {
        InputStream is = SmaliFile.class.getClassLoader().getResourceAsStream("reflib.zip");

        return new ZipInputStream(is);
    }

    public InputStream open() throws URISyntaxException, IOException {
        if (isResource) {
            ZipInputStream zis = getReflibStream();
            for (ZipEntry ze; (ze = zis.getNextEntry()) != null;) {
                if (ze.getName().equals(getPath())) {
                    return zis;
                }
            }

            return null;
        } else {
            return new FileInputStream(new File(path));
        }
    }

    public void setIsResource(boolean isResource) {
        this.isResource = isResource;
    }

    @Override
    public String toString() {
        return path;
    }

    private static String getClassName(BufferedInputStream inputStream) {
        String className = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
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
            className = m.group(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return className;
    }

}
