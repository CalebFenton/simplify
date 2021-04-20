package org.cf.smalivm.dex

import org.cf.smalivm.configuration.Configuration
import org.cf.util.Utils
import org.slf4j.LoggerFactory
import java.io.File

class SmaliFileFactory {
    fun getSmaliFiles(file: File?): Set<SmaliFile> {
        return getSmaliFiles(arrayOf(file))
    }

    fun getSmaliFiles(files: Array<File?>): Set<SmaliFile> {
        val smaliFiles = smaliFiles
        for (file in files) {
            val matches = Utils.getFilesWithSmaliExtension(file)
            matches.parallelStream().forEach { match: File? ->
                val smaliFile = SmaliFile(match)
                // DalvikVM rejects classes in an APK that are already defined.
                // Framework classes take precedence over local classes.
                val className = smaliFile.className
                if (isFrameworkClass(className) && !className.startsWith("Landroid/support/")) {
                    log.warn("Input class '{}' has an earlier definition; ignoring", className)
                } else {
                    smaliFiles.add(smaliFile)
                }
            }
        }
        return smaliFiles
    }

    val smaliFiles: MutableSet<SmaliFile>
        get() {
            cacheFramework()
            val smaliFiles: MutableSet<SmaliFile> = HashSet()
            smaliFiles.addAll(frameworkClassNameToSmaliFile!!.values)
            return smaliFiles
        }

    fun isFrameworkClass(className: String): Boolean {
        return frameworkClassNameToSmaliFile!!.containsKey(className)
    }

    fun getSmaliFiles(path: String): Set<SmaliFile> {
        return getSmaliFiles(arrayOf(path))
    }

    fun getSmaliFiles(paths: Array<String>): Set<SmaliFile> {
        val files = arrayOfNulls<File>(paths.size)
        for (i in paths.indices) {
            files[i] = File(paths[i])
        }
        return getSmaliFiles(files)
    }

    fun isSafeFrameworkClass(className: String): Boolean {
        val smaliFile = frameworkClassNameToSmaliFile!![className]
        return null != smaliFile && smaliFile.isSafeFrameworkClass
    }

    companion object {
        private val log = LoggerFactory.getLogger(SmaliFileFactory::class.java.simpleName)
        private var frameworkClassNameToSmaliFile: Map<String, SmaliFile>? = null

        @Synchronized
        private fun cacheFramework() {
            if (frameworkClassNameToSmaliFile != null) {
                return
            }
            val startTime = System.currentTimeMillis()
            frameworkClassNameToSmaliFile = parseFramework()
            if (log.isDebugEnabled) {
                val endTime = System.currentTimeMillis()
                // assuming time has not gone backwards
                val totalTime = endTime - startTime
                log.debug(
                    "Cached {} framework classes in {} seconds.", frameworkClassNameToSmaliFile!!.size,
                    totalTime / 1000
                )
            }
        }

        private fun parseFramework(): Map<String, SmaliFile> {
            val frameworkFiles: MutableMap<String, SmaliFile> = HashMap()
            // framework_classes.cfg is built by FrameworkCacheBuilder
            val frameworkClassesCfg = Configuration.load(Configuration.FRAMEWORK_CLASSES_PATH)
            val safeFrameworkClasses = HashSet(Configuration.load(Configuration.SAFE_FRAMEWORK_CLASSES_PATH))
            for (line in frameworkClassesCfg) {
                val parts = line.split(":").toTypedArray()
                val className = parts[0]
                val path = parts[1]
                val smaliFile = SmaliFile(path, className)
                smaliFile.setIsResource(true)
                smaliFile.setIsSafeFramework(safeFrameworkClasses.contains(className))
                frameworkFiles[smaliFile.className] = smaliFile
            }
            return frameworkFiles
        }
    }
}
