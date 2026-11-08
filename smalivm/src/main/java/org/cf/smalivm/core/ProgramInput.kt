package org.cf.smalivm.core

import java.io.File
import java.io.IOException
import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Format
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.dexbacked.ZipDexContainer
import com.android.tools.smali.dexlib2.formatter.DexFormatter
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35mi
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35ms
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction45cc
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableReferenceFactory
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder

/** The input path does not hold a program the engine can load: it is missing, unreadable, malformed, or multi-DEX. */
public class ProgramInputException internal constructor(message: String, cause: Throwable? = null) :
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
            // A builder interns one class definition per name, so each file gets its own.
            return files.map { readSmali(it, DexBuilder(Opcodes.forApi(apiLevel)), apiLevel) }
        }

        private fun readSmali(file: Path, builder: DexBuilder, apiLevel: Int): ImmutableClassDef = try {
            Files.newInputStream(file).use { input ->
                immutableClass(SmaliParser.parse(file.toString(), input, builder, apiLevel))
            }
        } catch (failure: ProgramInputException) {
            throw failure
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
                return DexFileFactory.loadDexFile(input, opcodes).classes.map(::immutableClass)
            } catch (failure: ProgramInputException) {
                throw failure
            } catch (failure: IOException) {
                throw ProgramInputException("Cannot read DEX input $input: ${failure.message}", failure)
            } catch (failure: RuntimeException) {
                throw ProgramInputException("Cannot read DEX input $input: ${failure.message}", failure)
            }
        }

        /** [definition] as immutable values, refused as ART's DEX file verifier refuses its file. */
        private fun immutableClass(definition: ClassDef): ImmutableClassDef {
            for (field in definition.fields) {
                malformedSignature { FieldSignature.of(field) }?.let { reason ->
                    throw ProgramInputException("Field ${DexFormatter.INSTANCE.getFieldDescriptor(field)} $reason")
                }
            }
            return ImmutableClassDef(
                definition.type,
                definition.accessFlags,
                definition.superclass,
                definition.interfaces,
                definition.sourceFile,
                definition.annotations,
                definition.staticFields,
                definition.instanceFields,
                definition.directMethods.map(::immutableMethod),
                definition.virtualMethods.map(::immutableMethod),
            )
        }

        /** Why ART's DEX file verifier refuses a member id whose name or types do not parse, or null. */
        private inline fun malformedSignature(read: () -> Unit): String? = try {
            read()
            null
        } catch (failure: MalformedSignatureException) {
            "has a malformed signature: ${failure.reason}"
        }

        private fun immutableMethod(method: Method): ImmutableMethod {
            val code = method.implementation?.let {
                ImmutableMethodImplementation(
                    it.registerCount,
                    it.instructions.map(::immutableInstruction),
                    it.tryBlocks,
                    it.debugItems,
                )
            }
            dexFileVerifierError(method, code)?.let { error ->
                throw ProgramInputException("Method ${DexFormatter.INSTANCE.getMethodDescriptor(method)} $error")
            }
            return ImmutableMethod(
                method.definingClass,
                method.name,
                method.parameters,
                method.returnType,
                method.accessFlags,
                method.annotations,
                method.hiddenApiRestrictions,
                code,
            )
        }

        /**
         * Why ART's DEX file verifier (`libdexfile/dex/dex_file_verifier.cc`) refuses the file holding [method]
         * with [code], or null. A method id names a MemberName and type descriptors (`CheckInterMethodIdItem`);
         * only native and abstract methods lack code (`CheckMethodAccessFlags`); the registers hold the receiver and
         * parameter words, `ins_size`; and every try range and handler lies inside the code (`CheckIntraCodeItem`,
         * `CheckAndGetHandlerOffsets`). dexlib2 does not expose `ins_size`, so the words come from the prototype,
         * which is what a DEX writer stores; a code item whose `ins_size` disagrees with its prototype fails ART's
         * method verifier instead, which rejects only the class.
         */
        private fun dexFileVerifierError(method: Method, code: ImmutableMethodImplementation?): String? {
            malformedSignature { MethodSignature.of(method) }?.let { return it }
            val codeForbidden = Modifier.isAbstract(method.accessFlags) || Modifier.isNative(method.accessFlags)
            if (code == null) return if (codeForbidden) null else "has no code but is neither native nor abstract"
            if (codeForbidden) return "has code but is native or abstract"
            val parameterWords = (if (Modifier.isStatic(method.accessFlags)) 0 else 1) +
                method.parameterTypes.sumOf { if (it.toString() in WIDE_TYPES) 2 else 1 }
            if (parameterWords > code.registerCount) {
                return "has $parameterWords parameter words but ${code.registerCount} registers"
            }
            val codeSize = code.instructions.sumOf { it.codeUnits }
            for (block in code.tryBlocks) {
                val end = block.startCodeAddress.toLong() + block.codeUnitCount
                if (block.startCodeAddress >= codeSize || end > codeSize) {
                    return "has a try range [${block.startCodeAddress}, $end) outside its $codeSize code units"
                }
                block.exceptionHandlers.firstOrNull { it.handlerCodeAddress >= codeSize }?.let { handler ->
                    return "has a handler at ${handler.handlerCodeAddress} outside its $codeSize code units"
                }
            }
            return null
        }

        private val WIDE_TYPES = setOf("J", "D")

        private fun immutableInstruction(instruction: Instruction): ImmutableInstruction =
            if (instruction is FiveRegisterInstruction && instruction.registerCount > MAX_FIVE_REGISTER_COUNT) {
                OverCountInstruction.of(instruction)
            } else {
                ImmutableInstruction.of(instruction)
            }

        private const val SMALI_EXTENSION = ".smali"
    }
}

/** The most registers a non-range five-register instruction can name. */
internal const val MAX_FIVE_REGISTER_COUNT = 5

/**
 * A five-register instruction whose count declares more registers than its five register fields. The format's
 * four-bit count can hold fifteen and dexlib2's immutable instructions refuse more than five, so a DEX method
 * keeps such an instruction in this form. ART's verifier rejects the method ("invalid arg count"), so this form
 * carries the code from input to output and never executes.
 */
internal sealed class OverCountInstruction(
    opcode: Opcode,
    private val count: Int,
    protected val fields: IntArray,
) : ImmutableInstruction(opcode), FiveRegisterInstruction {
    override fun getRegisterCount(): Int = count
    override fun getRegisterC(): Int = fields[0]
    override fun getRegisterD(): Int = fields[1]
    override fun getRegisterE(): Int = fields[2]
    override fun getRegisterF(): Int = fields[3]
    override fun getRegisterG(): Int = fields[4]

    /** This instruction naming [references] in `referenceOperands` order; a format without references keeps none. */
    open fun rebound(references: List<Reference>): OverCountInstruction = this

    class Of35c(opcode: Opcode, count: Int, fields: IntArray, private val reference: Reference) :
        OverCountInstruction(opcode, count, fields), Instruction35c {
        override fun getFormat(): Format = Format.Format35c
        override fun getReference(): Reference = reference
        override fun getReferenceType(): Int = opcode.referenceType
        override fun rebound(references: List<Reference>) = Of35c(opcode, registerCount, fields, references[0])
    }

    class Of45cc(
        opcode: Opcode,
        count: Int,
        fields: IntArray,
        private val reference: Reference,
        private val reference2: Reference,
    ) : OverCountInstruction(opcode, count, fields), Instruction45cc {
        override fun getFormat(): Format = Format.Format45cc
        override fun getReference(): Reference = reference
        override fun getReferenceType(): Int = opcode.referenceType
        override fun getReference2(): Reference = reference2
        override fun getReferenceType2(): Int = opcode.referenceType2
        override fun rebound(references: List<Reference>) =
            Of45cc(opcode, registerCount, fields, references[0], references[1])
    }

    class Of35mi(opcode: Opcode, count: Int, fields: IntArray, private val inlineIndex: Int) :
        OverCountInstruction(opcode, count, fields), Instruction35mi {
        override fun getFormat(): Format = Format.Format35mi
        override fun getInlineIndex(): Int = inlineIndex
    }

    class Of35ms(opcode: Opcode, count: Int, fields: IntArray, private val vtableIndex: Int) :
        OverCountInstruction(opcode, count, fields), Instruction35ms {
        override fun getFormat(): Format = Format.Format35ms
        override fun getVtableIndex(): Int = vtableIndex
    }

    companion object {
        fun of(source: FiveRegisterInstruction): OverCountInstruction {
            val fields =
                intArrayOf(source.registerC, source.registerD, source.registerE, source.registerF, source.registerG)
            return when (source) {
                is Instruction35c -> Of35c(
                    source.opcode,
                    source.registerCount,
                    fields,
                    ImmutableReferenceFactory.of(source.reference),
                )
                is Instruction45cc -> Of45cc(
                    source.opcode,
                    source.registerCount,
                    fields,
                    ImmutableReferenceFactory.of(source.reference),
                    ImmutableReferenceFactory.of(source.reference2),
                )
                is Instruction35mi -> Of35mi(source.opcode, source.registerCount, fields, source.inlineIndex)
                is Instruction35ms -> Of35ms(source.opcode, source.registerCount, fields, source.vtableIndex)
                else -> error("No five-register format for ${source.opcode}")
            }
        }
    }
}
