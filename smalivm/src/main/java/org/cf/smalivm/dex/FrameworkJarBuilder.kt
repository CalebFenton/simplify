package org.cf.smalivm.dex

import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.cf.smalivm.core.FrameworkArtifact
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/** Regenerates the legacy class index and JVM shapes from the selected, generated framework catalog. */
object FrameworkJarBuilder {
    @JvmStatic
    fun main(args: Array<String>) {
        require(args.size == 2) { "Usage: frameworkJarBuilder <output class index path> <output jar path>" }
        val artifact = FrameworkArtifact.load()
        val classes = artifact.classes.values.sortedBy { it.type }
        require(classes.isNotEmpty()) { "The selected framework catalog is empty" }
        val classBuilder = ClassBuilder { type ->
            requireNotNull(artifact.classes[type]) { "Missing framework member class $type" }.accessFlags
        }
        val indexPath = Path.of(args[0]).toAbsolutePath()
        val jarPath = Path.of(args[1]).toAbsolutePath()
        Files.createDirectories(indexPath.parent)
        Files.createDirectories(jarPath.parent)
        val temporaryIndex = Files.createTempFile(indexPath.parent, "framework-classes-", ".cfg")
        val temporaryJar = Files.createTempFile(jarPath.parent, "android-framework-", ".jar")
        try {
            Files.newBufferedWriter(temporaryIndex, Charsets.UTF_8).use { index ->
                JarOutputStream(Files.newOutputStream(temporaryJar).buffered()).use { jar ->
                    for (entry in classes) {
                        try {
                            // Intern only this class. Neither ClassManager nor FrameworkCatalog may retain
                            // the entire framework's parsed bodies during reference generation.
                            val dexBuilder = DexBuilder(artifact.opcodes)
                            val source = requireNotNull(artifact.openClassSource(entry.type))
                            val definition = source.inputStream().use { input ->
                                SmaliParser.parse(entry.resourcePath, input, dexBuilder, artifact.opcodes.api)
                            }
                            check(definition.type == entry.type) { "Framework class does not match catalog" }
                            val bytes = classBuilder.build(definition)
                            val name = entry.type.substring(1, entry.type.length - 1) + ".class"
                            jar.putNextEntry(ZipEntry(name).apply { time = 0L })
                            jar.write(bytes)
                            jar.closeEntry()
                            index.append(entry.type).append(':').append(entry.resourcePath).append('\n')
                        } catch (failure: Exception) {
                            throw IllegalStateException("Failed to generate framework shape for ${entry.type}", failure)
                        }
                    }
                }
            }
            // Do not replace either tracked artifact until every selected class has been generated.
            Files.move(temporaryJar, jarPath, REPLACE_EXISTING)
            Files.move(temporaryIndex, indexPath, REPLACE_EXISTING)
        } finally {
            Files.deleteIfExists(temporaryJar)
            Files.deleteIfExists(temporaryIndex)
        }
        println("Saved ${classes.size} Android API ${artifact.androidApiLevel} framework classes (DEX ${artifact.dexVersion}) to $jarPath and $indexPath")
    }
}
