package org.cf.smalivm.core

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.ZipDexContainer
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder

/** The input path does not hold a program the engine can load: it is missing, unreadable, malformed, or multi-DEX. */
class ProgramInputException internal constructor(message: String, cause: Throwable? = null) :
    IOException(message, cause)

/**
 * Application class definitions in classpath order, read from a Smali file, a directory of Smali files, a DEX file,
 * or an archive whose only class path DEX is `classes.dex`, plus the API level the program is written for.
 */
internal class ProgramInput private constructor(
    val classes: List<ImmutableClassDef>,
    val outputApiLevel: Int,
) {
    companion object {
        fun read(input: File, outputApiLevel: Int): ProgramInput {
            if (!input.exists()) throw ProgramInputException("Input path does not exist: $input")
            val classes = when {
                input.isDirectory -> readSmaliDirectory(input.toPath(), outputApiLevel)
                input.name.endsWith(SMALI_EXTENSION) -> {
                    listOf(readSmali(input.toPath(), DexBuilder(Opcodes.forApi(outputApiLevel)), outputApiLevel))
                }
                else -> readDex(input, outputApiLevel)
            }
            return ProgramInput(classes, outputApiLevel)
        }

        /** Smali files in path order, which is the classpath order for duplicate class definitions. */
        private fun readSmaliDirectory(directory: Path, apiLevel: Int): List<ImmutableClassDef> {
            val files = try {
                Files.walk(directory).use { paths ->
                    paths.filter { it.isRegularFile() && it.name.endsWith(SMALI_EXTENSION) }.sorted().toList()
                }
            } catch (failure: IOException) {
                throw ProgramInputException("Cannot list Smali files under $directory: ${failure.message}", failure)
            }
            val builder = DexBuilder(Opcodes.forApi(apiLevel))
            return files.map { readSmali(it, builder, apiLevel) }
        }

        private fun readSmali(file: Path, builder: DexBuilder, apiLevel: Int): ImmutableClassDef = try {
            Files.newInputStream(file).use { input ->
                ImmutableClassDef.of(SmaliParser.parse(file.toString(), input, builder, apiLevel))
            }
        } catch (failure: IOException) {
            throw ProgramInputException("Cannot read Smali file $file: ${failure.message}", failure)
        } catch (failure: RuntimeException) {
            throw ProgramInputException("Cannot assemble Smali file $file: ${failure.message}", failure)
        }

        /**
         * A DEX file, or the `classes.dex` of an archive. An Android runtime also loads `classes2.dex`,
         * `classes3.dex`, ... up to the first missing number, and the engine treats an absent class as absent
         * from the device, so an archive with such entries is refused rather than read partially.
         */
        private fun readDex(input: File, apiLevel: Int): List<ImmutableClassDef> {
            val opcodes = Opcodes.forApi(apiLevel)
            try {
                val archive = ZipDexContainer(input, opcodes)
                if (archive.isZipFile) {
                    val secondaryDexEntries = generateSequence(2) { it + 1 }
                        .map { "classes$it.dex" }
                        .takeWhile { archive.getEntry(it) != null }
                        .toList()
                    if (secondaryDexEntries.isNotEmpty()) {
                        throw ProgramInputException(
                            "Multi-DEX archives are not supported: $input also has " +
                                secondaryDexEntries.joinToString(", "),
                        )
                    }
                }
                return DexFileFactory.loadDexFile(input, opcodes).classes.map(ImmutableClassDef::of)
            } catch (failure: ProgramInputException) {
                throw failure
            } catch (failure: IOException) {
                throw ProgramInputException("Cannot read DEX input $input: ${failure.message}", failure)
            } catch (failure: RuntimeException) {
                throw ProgramInputException("Cannot read DEX input $input: ${failure.message}", failure)
            }
        }

        private const val SMALI_EXTENSION = ".smali"
    }
}
