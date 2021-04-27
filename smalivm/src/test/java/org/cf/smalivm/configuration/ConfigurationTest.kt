package org.cf.smalivm.configuration

import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.stream.Collectors

class ConfigurationTest {
    private var configuration: Configuration? = null
    @BeforeEach
    fun setUp() {
        configuration = Configuration.instance()
    }

    @Test
    fun stringIsImmutable() {
        Assertions.assertTrue(configuration!!.isImmutable("Ljava/lang/String;"))
    }

    @Test
    fun stringArrayIsImmutable() {
        Assertions.assertFalse(configuration!!.isImmutable("[Ljava/lang/String;"))
    }

    @Test
    fun stringBuilderIsImmutable() {
        Assertions.assertFalse(configuration!!.isImmutable("Ljava/lang/StringBuilder;"))
    }

    @Test
    @Throws(IOException::class, ClassNotFoundException::class)
    fun safeClassesCanBeLoaded() {
        ensureClassesExist(Configuration.SAFE_CLASSES_PATH)
    }

    @Test
    @Throws(IOException::class)
    fun immutableClassesCanBeLoaded() {
        val lines = Configuration.load(Configuration.IMMUTABLE_CLASSES_PATH)
        // Ignore primitives and inner classes
        val classNames = lines.stream().filter { l: String -> l.startsWith("L") && !l.contains("$") }.collect(Collectors.toList())
        ensureClassesExist(classNames, Configuration.IMMUTABLE_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR)
    }

    @Test
    @Throws(IOException::class)
    fun safeFrameworkClassesExist() {
        val lines = Configuration.load(Configuration.SAFE_FRAMEWORK_CLASSES_PATH)
        ensureClassesExist(lines, Configuration.SAFE_FRAMEWORK_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR)
    }

    @Test
    @Throws(IOException::class)
    fun frameworkClassesExist() {
        val lines = Configuration.load(Configuration.FRAMEWORK_CLASSES_PATH)
        val configClasses = lines.stream().map { l: String -> l.substring(0, l.indexOf(':')) }.collect(Collectors.toList())
        ensureClassesExist(configClasses, Configuration.FRAMEWORK_CLASSES_PATH, SmaliClassLoader.FRAMEWORK_STUBS_JAR)
    }

    @Throws(ClassNotFoundException::class)
    private fun ensureClassesExist(configResPath: String) {
        val classNames = Configuration.load(configResPath)
        for (className in classNames) {
            val sourceName = ClassNameUtils.internalToSource(className)
            ConfigurationTest::class.java.classLoader.loadClass(sourceName)
        }
    }

    @Throws(IOException::class)
    private fun ensureClassesExist(classNames: List<String>, configResPath: String, jarResourcePath: String) {
        val inputStream = ConfigurationTest::class.java.getResourceAsStream(jarResourcePath)
        val jis = JarInputStream(inputStream)
        var je: JarEntry?
        val jarClasses: MutableSet<String> = HashSet()
        while (true) {
            je = jis.nextJarEntry
            if (je == null) {
                break
            }
            val entryName = je.name
            if (entryName.endsWith(".class")) {
                val className = "L" + entryName.substring(0, entryName.indexOf('.')) + ";"
                jarClasses.add(className)
            }
        }
        val nonExistentClasses = classNames.stream().filter { l: String -> !jarClasses.contains(l) }.collect(Collectors.toList())
        val msg = "Classes in $configResPath don't exist: ${nonExistentClasses.toTypedArray().contentToString()}"
        Assertions.assertEquals(0, nonExistentClasses.size, msg)
    }
}
