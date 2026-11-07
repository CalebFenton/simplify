package org.cf.smalivm.dex

import org.cf.smalivm.configuration.Configuration
import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import java.net.URLClassLoader

class FrameworkJarBuilderTest {
    @Test
    @DisplayName("Framework classes exist and can be loaded without exception")
    @Throws(IOException::class, ClassNotFoundException::class)
    fun canLoadFrameworkClasses() {
        // Skip loading prohibited package names to avoid error. This isn't ideal, but
        // I can't think of an easy work around. This still loads a majority of classes.
        val prohibitedPrefixes = arrayOf("Ljava/", "Lsun/security/", "Lsun/nio/ch/FileChannelImpl\$SimpleFileLockTable", "Lsun/nio/ch/SharedFileLockTable")
        val lines = Configuration.load(Configuration.FRAMEWORK_CLASSES_PATH)
        val jarURL = FrameworkJarBuilderTest::class.java.getResource(SmaliClassLoader.FRAMEWORK_STUBS_JAR)
        val jarLoader = URLClassLoader(arrayOf(jarURL))
        for (line in lines) {
            val internalName = line.substring(0, line.indexOf(':'))
            println(internalName)
            if (prohibitedPrefixes.any { internalName.startsWith(it) }) {
                break
            }
            val sourceName = ClassNameUtils.internalToSource(internalName)

            // If the class was generated badly, this should error.
            jarLoader.loadClass(sourceName)
        }
    }
}
