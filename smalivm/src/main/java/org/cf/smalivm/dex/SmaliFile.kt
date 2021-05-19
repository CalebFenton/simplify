package org.cf.smalivm.dex

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

class SmaliFile internal constructor(val path: String, val className: String) {
    var isResource = false
    var isSafeFrameworkClass = false

    internal constructor(file: File) : this(file.absolutePath, getClassName(file))

    @Throws(IOException::class)
    fun open(): InputStream {
        return if (isResource) {
            SmaliFile::class.java.classLoader.getResourceAsStream(path)
        } else {
            FileInputStream(File(path))
        }
    }

    override fun toString(): String {
        return path
    }

    companion object {
        private val CLASS_PATTERN = Pattern.compile(" (L[^;]+;)")
        private fun getClassName(inputFile: File): String {
            return try {
                val myPath = Paths.get(inputFile.toURI())
                val lines = Files.lines(myPath)
                val firstClassLine = lines.filter { s: String -> s.startsWith(".class ") }.findFirst()
                lines.close()
                if (!firstClassLine.isPresent) {
                    throw RuntimeException("Missing class directive in $inputFile")
                }
                val line = firstClassLine.get()
                val m = CLASS_PATTERN.matcher(line)
                if (!m.find()) {
                    throw RuntimeException("Strange class directive: $line")
                }
                m.group(1)
            } catch (e: IOException) {
                throw RuntimeException("Unable to read class name in $inputFile", e)
            }
        }
    }
}