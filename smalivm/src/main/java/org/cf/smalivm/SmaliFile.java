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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SmaliFile {

    private static final Pattern CLASS_PATTERN = Pattern.compile(" (L[^;]+;)");

    private final String path;
    private final String className;
    private boolean isResource;

    public SmaliFile(String path, InputStream inputStream) {
        this.path = path;
        className = getClassName(new BufferedInputStream(inputStream));
    }

    public SmaliFile(File file) throws FileNotFoundException {
        this(file.getAbsolutePath(), new FileInputStream(file));
    }

    public InputStream open() throws URISyntaxException, FileNotFoundException {
        if (isResource) {
            return SmaliFile.class.getResourceAsStream(path);
        } else {
            File file = new File(path);

            return new FileInputStream(file);
        }
    }

    public String getPath() {
        return path;
    }

    public String getClassName() {
        return className;
    }

    public void setIsResource(boolean isResource) {
        this.isResource = isResource;
    }

    public boolean isResource() {
        return this.isResource;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 61).append(className).toHashCode();
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

    private String getClassName(BufferedInputStream inputStream) {
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

    @Override
    public String toString() {
        return path;
    }

}
