package org.cf.smalivm.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cf.smalivm.dex.SmaliClassLoader;
import org.cf.util.ClassNameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConfigurationTest {

    private Configuration configuration;

    @BeforeEach
    public void setUp() {
        configuration = Configuration.instance();
    }

    @Test
    public void stringIsImmutable() {
        assertTrue(configuration.isImmutable("Ljava/lang/String;"));
    }

    @Test
    public void stringArrayIsImmutable() {
        assertFalse(configuration.isImmutable("[Ljava/lang/String;"));
    }

    @Test
    public void stringBuilderIsImmutable() {
        assertFalse(configuration.isImmutable("Ljava/lang/StringBuilder;"));
    }

    @Test
    public void safeClassesCanBeLoaded() throws IOException, ClassNotFoundException {
        ensureClassesExist(Configuration.SAFE_CLASSES_PATH);
    }

    @Test
    public void immutableClassesCanBeLoaded() throws IOException, ClassNotFoundException {
        List<String> lines = Configuration.load(Configuration.IMMUTABLE_CLASSES_PATH);
        // Ignore primitives and inner classes
        List<String> classNames = lines.stream().filter(l -> l.startsWith("L") && !l.contains("$")).collect(Collectors.toList());
        ensureClassesExist(classNames, Configuration.IMMUTABLE_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR);
    }

    @Test
    public void safeFrameworkClassesExist() throws IOException, ClassNotFoundException {
        List<String> lines = Configuration.load(Configuration.SAFE_FRAMEWORK_CLASSES_PATH);
        ensureClassesExist(lines, Configuration.SAFE_FRAMEWORK_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR);
    }

    @Test
    public void frameworkClassesExist() throws IOException, ClassNotFoundException {
        List<String> lines = Configuration.load(Configuration.FRAMEWORK_CLASSES_PATH);
        List<String> configClasses = lines.stream().map(l -> l.substring(0, l.indexOf(':'))).collect(Collectors.toList());
        ensureClassesExist(configClasses, Configuration.FRAMEWORK_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR);
    }

    private void ensureClassesExist(String configResPath) throws ClassNotFoundException {
        List<String> classNames = Configuration.load(configResPath);
        for (String className : classNames) {
            String sourceName = ClassNameUtils.internalToSource(className);
            ConfigurationTest.class.getClassLoader().loadClass(sourceName);
        }
    }

    private void ensureClassesExist(List<String> classNames, String configResPath, String jarResourcePath) throws IOException {
        InputStream is = ConfigurationTest.class.getResourceAsStream(jarResourcePath);
        JarInputStream jis = new JarInputStream(is);
        JarEntry je;

        Set<String> jarClasses = new HashSet<String>();
        while ((je = jis.getNextJarEntry()) != null) {
            String entryName = je.getName();
            if (entryName.endsWith(".class")) {
                String className = "L" + entryName.substring(0, entryName.indexOf('.')) + ";";
                jarClasses.add(className);
            }
        }

        List<String> nonExistentClasses = classNames.stream().filter(l -> !jarClasses.contains(l)).collect(Collectors.toList());

        String msg = "Classes in " + configResPath + " don't exist: " + Arrays.toString(nonExistentClasses.toArray());
        assertEquals(0, nonExistentClasses.size(), msg);
    }
}
