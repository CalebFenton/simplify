package org.cf.smalivm.dex

import com.google.common.io.Files
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.ClassManagerFactory
import org.cf.util.Utils
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.formatter.DexFormatter
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.writer.builder.DexBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.jar.JarOutputStream
import java.util.stream.Collectors
import java.util.zip.ZipEntry

/**
 * This updates framework_classes.cfg which contains a list of all framework classes and Smali paths. It's used as a cache so the paths don't have to
 * be enumerated when smalivm starts. Then, it generates Java .class files for each framework class and creates an android-##.jar. This is also used
 * as a cache so smalivm doesn't have to generate Java classes when framework class objects are instantiated.
 *
 */
object FrameworkJarBuilder {
    private const val FRAMEWORK_ROOT = "/framework/smali"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 2) {
            println("Usage: frameworkJarBuilder <framework resource path (e.g. android-25)> <output jar path>")
            System.exit(-1)
        }
        val cacheFileName = "src/main/resources/framework_classes.cfg"
        val cacheSink = Files.asCharSink(File(cacheFileName), StandardCharsets.UTF_8)
        val resPath = FRAMEWORK_ROOT + "/" + args[0]
        println("Building framework cache from $resPath")
        val cache = buildFrameworkCache(resPath)
        cacheSink.write(cache)
        println("Saved cache to " + cacheFileName + " (" + cache.toByteArray().size + " bytes)")
        val outPath = args[1]
        println("Beginning class generation")
        val classManager = ClassManagerFactory().build()
        val builder = ClassBuilder(classManager)
        val classNames = buildJar(classManager, builder, outPath)
        println("Saved " + classNames.size + " classes to " + outPath)
    }

    @Throws(Exception::class)
    private fun buildFrameworkCache(resPath: String): String {
        val resFiles: MutableList<File> = LinkedList()
        try {
            val url = FrameworkJarBuilder::class.java.getResource(resPath)
            val file = File(url.toURI())
            resFiles.addAll(Utils.getFilesWithSmaliExtension(file))
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        val resourcePaths = resFiles.parallelStream().map { obj: File -> obj.absolutePath }.map { p: String ->
            p.substring(
                p.lastIndexOf(
                    FRAMEWORK_ROOT
                )
            )
        }
            .sorted() // so they look nice in the output file
            .collect(Collectors.toList())
        val dexBuilder = DexBuilder(Opcodes.getDefault())
        val sb = StringBuilder()
        for (path in resourcePaths) {
            val inputStream = FrameworkJarBuilder::class.java.getResourceAsStream(path)
            val classDef = SmaliParser.parse(path, inputStream, dexBuilder)
            sb.append(DexFormatter.INSTANCE.getReference(classDef))
            sb.append(':').append(path.substring(1)).append('\n')
        }
        return sb.toString()
    }

    @Throws(IOException::class)
    private fun buildJar(classManager: ClassManager, builder: ClassBuilder, outPath: String): Set<String> {
        val classNames = classManager.getFrameworkClassNames()
        val out = JarOutputStream(FileOutputStream(outPath))
        for (className in classNames) {
            val virtualClass = classManager.getVirtualClass(className)
            val classDef: ClassDef = virtualClass.classDef
            val b = builder.build(classDef)
            val entryName = className.substring(1, className.length - 1)
            out.putNextEntry(ZipEntry("$entryName.class"))
            out.write(b)
            out.closeEntry()
        }
        out.close()
        return classNames
    }
}