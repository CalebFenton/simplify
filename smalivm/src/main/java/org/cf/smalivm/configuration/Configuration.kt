package org.cf.smalivm.configuration

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*

class Configuration private constructor() {
    val immutableClasses: Set<String> = HashSet(load(IMMUTABLE_CLASSES_PATH))
    val safeClasses: Set<String> = HashSet(load(SAFE_CLASSES_PATH))
    private val safeMethods: Set<String> = HashSet(load(SAFE_METHODS_PATH))
    private val unsafeMethods: Set<String> = HashSet(load(UNSAFE_METHODS_PATH))

    fun isImmutable(className: String): Boolean {
        return immutableClasses.contains(className)
    }

    fun isMutable(className: String): Boolean {
        return !isImmutable(className)
    }

    /**
     * Safe classes are Java API classes which are safe to instantiate, load, and statically initialize. Safe methods are methods of Java classes
     * which are safe to execute. Only classes and methods which can have no possible side effects outside of SmaliVM should be configured as safe.
     */
    fun isSafe(typeSignature: String): Boolean {
        val parts = typeSignature.split("->").toTypedArray()
        val className = parts[0]
        if (safeClasses.contains(className) && !isUnsafeMethod(typeSignature)) {
            return true
        }
        if (parts.size > 1) {
            // It's a method name
            if (safeMethods.contains(typeSignature)) {
                return true
            }
        }
        return false
    }

    fun isSafe(method: VirtualMethod): Boolean {
        return isSafe(method.signature)
    }

    fun isSafe(virtualClass: VirtualType): Boolean {
        return isSafe(virtualClass.toString())
    }

    fun isUnsafeMethod(methodSignature: String): Boolean {
        return unsafeMethods.contains(methodSignature)
    }

    companion object {
        private val log = LoggerFactory.getLogger(Configuration::class.java.simpleName)
        const val IMMUTABLE_CLASSES_PATH = "immutable_classes.cfg"
        const val SAFE_CLASSES_PATH = "safe_classes.cfg"
        const val SAFE_METHODS_PATH = "safe_methods.cfg"
        const val UNSAFE_METHODS_PATH = "unsafe_methods.cfg"
        const val FRAMEWORK_CLASSES_PATH = "framework_classes.cfg"
        const val SAFE_FRAMEWORK_CLASSES_PATH = "safe_framework_classes.cfg"
        var instance: Configuration? = null

        fun instance(): Configuration {
            if (instance == null) {
                instance = Configuration()
            }
            return instance!!
        }

        fun load(path: String): List<String> {
            val url = Resources.getResource(path)
            var lines: MutableList<String> = LinkedList()
            try {
                lines = Resources.readLines(url, Charsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            removeNonDefinitionLines(lines)
            return lines
        }

        private fun removeNonDefinitionLines(lines: MutableList<String>) {
            val iter = lines.iterator()
            while (iter.hasNext()) {
                val line = iter.next()
                if (line.trim { it <= ' ' }.startsWith("//") || line.trim { it <= ' ' }.isEmpty()) {
                    iter.remove()
                }
            }
        }
    }
}
