package org.cf.smalivm.dex

import org.cf.smalivm.type.ClassManager
import org.cf.util.ClassNameUtils
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URLClassLoader

class SmaliClassLoader(private val classManager: ClassManager) : URLClassLoader(arrayOf(SmaliClassLoader::class.java.getResource(FRAMEWORK_STUBS_JAR))) {
    private val cachedClasses: MutableMap<String, Class<*>> = HashMap()
    private val classBuilder = ClassBuilder(classManager)

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        if (name in BUNDLED_RUNTIME_CLASSES) {
            // These are compiled pinned AOSP implementations, not generated framework shapes.
            // Use the VM's defining loader (also under isolated test/application class loaders).
            return SmaliClassLoader::class.java.classLoader.loadClass(name)
        }
        /*
         * Framework classes remain child-first so generated Android field/method shapes are preserved
         * even when the JVM has a same-named class. The explicit bundled runtime implementations above
         * must instead retain their executable bodies and shared host class identity. Class loading
         * does not authorize reflection: callers still apply the reflection policy before execution.
         */
        val internalName = ClassNameUtils.binaryToInternal(name)
        if (!classManager.getFrameworkClassNames().contains(internalName)) {
            return super.loadClass(name, resolve)
        }
        var klazz = cachedClasses[name]
        if (klazz != null) {
            return klazz
        }
        try {
            klazz = findClass(name)
        } catch (e: ClassNotFoundException) {
            return super.loadClass(name, resolve)
        }
        cachedClasses[name] = klazz
        return klazz
    }

    @Throws(ClassNotFoundException::class)
    public override fun findClass(name: String): Class<*> {
        if (name.startsWith("java.")) {
            // TODO: fix this!
            log.warn(
                """
            Unable to build and load prohibited class name: {}
            This can happen when loading an Android-specific class from a prohibited package such as java.lang. It may be necessary to reflect or emulate any affected methods.
            """.trimIndent(),
                name
            )
            throw ClassNotFoundException(name)
        }

        var klazz = cachedClasses[name]
        if (klazz != null) {
            return klazz
        }

        val internalName = ClassNameUtils.binaryToInternal(name)
        if (!classManager.getClassNames().contains(internalName)) {
            throw ClassNotFoundException(name)
        }
        val b = loadClassData(name)
        klazz = defineClass(name, b, 0, b.size)
        cachedClasses[name] = klazz

        // No one ever tells you this is also necessary or you'll have a null package for the class.
        val packageName = getPackageName(name)
        if (packageName != null && getDefinedPackage(packageName) == null) {
            definePackage(packageName, null, null, null, null, null, null, null)
        }
        return klazz
    }

    private fun loadClassData(name: String): ByteArray {
        val internalName = ClassNameUtils.binaryToInternal(name)
        val virtualClass = classManager.getVirtualClass(internalName)
        log.trace("building class: {}", virtualClass.classDef)
        return classBuilder.build(virtualClass.classDef)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SmaliClassLoader::class.java.simpleName)
        // Vendored AOSP Base64.java supplies exactly this runtime family. Keep its helper classes with the
        // outer class; synthesizing any of them would replace executable methods with throwing stubs.
        private val BUNDLED_RUNTIME_CLASSES = setOf(
            "android.util.Base64",
            "android.util.Base64\$Coder",
            "android.util.Base64\$Decoder",
            "android.util.Base64\$Encoder",
        )

        // This jar is created by FrameworkJarBuilder. It's a cache of framework classes to avoid
        // having to dynamically generate them.
        const val FRAMEWORK_STUBS_JAR = "/framework/android-framework.jar"

        @Throws(IOException::class, ClassNotFoundException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val smaliPath = args[0] // e.g. src/test/resources/smali
            val className = args[1] // e.g. "Linvoke_static_test;"
            println("Loading $className from $smaliPath")
            val classManager = ClassManager(
                File(smaliPath),
                SmaliParser.DEX_API_LEVEL,
                DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)),
            )
            val virtualClass = classManager.getVirtualClass(className)
            val classLoader = SmaliClassLoader(classManager)
            val klazz = classLoader.loadClass(virtualClass.sourceName)
            println("Loaded class: $klazz")
            val klazz2 = classLoader.loadClass(virtualClass.sourceName)
            println("Loaded class2: $klazz2")
            val klazz3 = classLoader.loadClass(virtualClass.sourceName)
            println("Loaded class3: $klazz3")
        }

        private fun getPackageName(className: String): String? {
            val i = className.lastIndexOf('.')
            return if (i > 0) {
                className.substring(0, i)
            } else {
                // No package name, e.g. LsomeClass;
                null
            }
        }
    }
}