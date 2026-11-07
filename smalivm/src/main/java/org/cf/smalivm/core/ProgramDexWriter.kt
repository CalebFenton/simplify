package org.cf.smalivm.core

import java.io.File
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.base.BaseExceptionHandler
import com.android.tools.smali.dexlib2.base.BaseTryBlock
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction20bc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction22c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction31c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction3rc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction45cc
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction4rcc
import com.android.tools.smali.dexlib2.iface.ExceptionHandler
import com.android.tools.smali.dexlib2.iface.MethodImplementation
import com.android.tools.smali.dexlib2.iface.TryBlock
import com.android.tools.smali.dexlib2.iface.debug.DebugItem
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20bc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction45cc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction4rcc
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import com.android.tools.smali.dexlib2.writer.io.DexDataStore
import com.android.tools.smali.dexlib2.writer.io.FileDataStore
import com.android.tools.smali.dexlib2.writer.io.MemoryDataStore

/**
 * Writes one supplied immutable snapshot through a fresh, disposable DEX builder.
 *
 * Serialization reads no workspace state and does not mutate the snapshot or its definitions.
 */
class ProgramDexWriter {
    fun write(snapshot: ProgramSnapshot, output: DexDataStore) {
        verifyReferenceClosure(snapshot)
        val builder = DexBuilder(snapshot.outputApiPolicy.opcodes())
        internReferenceClosure(builder, snapshot.dexReferenceClosure)
        for (definition in snapshot.applicationClasses.values.sortedBy(ApplicationClassDefinition::type)) {
            val fields = (definition.staticFields + definition.instanceFields).map { field ->
                val declaration = field.declaration
                builder.internField(
                    declaration.definingClass,
                    declaration.name,
                    declaration.type,
                    declaration.accessFlags,
                    declaration.initialValue,
                    declaration.annotations,
                    declaration.hiddenApiRestrictions,
                )
            }
            val methods = definition.methods.map { method ->
                val declaration = method.declaration
                builder.internMethod(
                    declaration.definingClass,
                    declaration.name,
                    declaration.parameters,
                    declaration.returnType,
                    declaration.accessFlags,
                    declaration.annotations,
                    declaration.hiddenApiRestrictions,
                    method.body?.let { reboundImplementation(builder, it) },
                )
            }
            builder.internClassDef(
                definition.type,
                definition.accessFlags,
                definition.superclass,
                definition.interfaces,
                definition.sourceFile,
                definition.annotations,
                fields,
                methods,
            )
        }
        builder.writeTo(output)
    }

    fun write(snapshot: ProgramSnapshot, output: File) {
        val destination = output.absoluteFile
        val temporary = Files.createTempFile(
            destination.parentFile.toPath(),
            "simplify-${destination.name}-",
            ".tmp",
        )
        try {
            val dataStore = FileDataStore(temporary.toFile())
            try {
                write(snapshot, dataStore)
            } finally {
                dataStore.close()
            }
            RandomAccessFile(temporary.toFile(), "rw").use { it.fd.sync() }
            Files.move(
                temporary,
                destination.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
        } catch (failure: Throwable) {
            try {
                Files.deleteIfExists(temporary)
            } catch (cleanupFailure: Throwable) {
                failure.addSuppressed(cleanupFailure)
            }
            throw failure
        }
    }

    fun writeToBytes(snapshot: ProgramSnapshot): ByteArray {
        val output = MemoryDataStore()
        return try {
            write(snapshot, output)
            output.data
        } finally {
            output.close()
        }
    }

    private fun internReferenceClosure(builder: DexBuilder, closure: DexReferenceClosure) {
        closure.strings.sorted().forEach(builder::internStringReference)
        closure.types.sorted().forEach(builder::internTypeReference)
        closure.prototypes.sortedBy { it.returnType + it.parameterTypes.joinToString() }.forEach { prototype ->
            builder.internMethodProtoReference(
                ImmutableMethodProtoReference(prototype.parameterTypes, prototype.returnType),
            )
        }
        closure.fields.sortedBy { it.definingClass + it.name + it.type }.forEach { field ->
            builder.internFieldReference(ImmutableFieldReference(field.definingClass, field.name, field.type))
        }
        closure.methods.sortedBy {
            it.definingClass + it.name + it.prototype.parameterTypes.joinToString() + it.prototype.returnType
        }.forEach { method ->
            builder.internMethodReference(
                ImmutableMethodReference(
                    method.definingClass,
                    method.name,
                    method.prototype.parameterTypes,
                    method.prototype.returnType,
                ),
            )
        }
        closure.methodHandles.sorted().forEach(builder::internMethodHandle)
        closure.callSites.sortedBy { it.name }.forEach(builder::internCallSite)
    }

    private fun verifyReferenceClosure(snapshot: ProgramSnapshot) {
        var required = DexReferenceClosure.EMPTY
        snapshot.applicationClasses.values.forEach { definition ->
            required = required.plus(DexReferenceClosure.fromApplicationClass(definition))
        }
        val supplied = snapshot.dexReferenceClosure
        check(supplied.strings == required.strings) { "Program snapshot has an incorrect string reference closure" }
        check(supplied.types == required.types) { "Program snapshot has an incorrect type reference closure" }
        check(supplied.fields == required.fields) { "Program snapshot has an incorrect field reference closure" }
        check(supplied.methods == required.methods) { "Program snapshot has an incorrect method reference closure" }
        check(supplied.prototypes == required.prototypes) { "Program snapshot has an incorrect prototype closure" }
        check(supplied.methodHandles == required.methodHandles) {
            "Program snapshot has an incorrect method-handle reference closure"
        }
        check(supplied.callSites == required.callSites) {
            "Program snapshot has an incorrect call-site reference closure"
        }
        if (snapshot.stringInstructionEncoding == StringInstructionEncoding.CONST_STRING) {
            check(supplied.strings.size <= MAX_NON_JUMBO_STRING_COUNT) {
                "Program snapshot exceeds the const-string index limit without jumbo encoding"
            }
        }
        if (snapshot.stringInstructionEncoding == StringInstructionEncoding.CONST_STRING_JUMBO) {
            check(
                snapshot.applicationClasses.values.none { definition ->
                    definition.methods.any { method ->
                        method.body?.instructions?.any { it.instruction.opcode == Opcode.CONST_STRING } == true
                    }
                },
            ) { "Program snapshot requires jumbo strings but contains a const-string instruction" }
        }
    }

    private companion object {
        const val MAX_NON_JUMBO_STRING_COUNT = 65_536
    }

    private fun reboundImplementation(builder: DexBuilder, body: MethodBody): MethodImplementation {
        val source = body.implementation
        val instructions = source.instructions.map { instruction -> reboundInstruction(builder, instruction) }
        val tryBlocks = source.tryBlocks.map { tryBlock -> reboundTryBlock(builder, tryBlock) }
        return object : MethodImplementation {
            override fun getRegisterCount(): Int = source.registerCount
            override fun getInstructions(): Iterable<Instruction> = instructions
            override fun getTryBlocks(): List<TryBlock<out ExceptionHandler>> = tryBlocks
            override fun getDebugItems(): Iterable<DebugItem> = source.debugItems.map { item ->
                reboundDebugItem(builder, item)
            }
        }
    }

    private fun reboundTryBlock(
        builder: DexBuilder,
        source: TryBlock<out ExceptionHandler>,
    ): TryBlock<out ExceptionHandler> {
        val handlers = source.exceptionHandlers.map { handler ->
            val exceptionType = handler.exceptionType
            val exceptionTypeReference = exceptionType?.let(builder::internTypeReference)
            object : BaseExceptionHandler() {
                override fun getExceptionType(): String? = exceptionType
                override fun getExceptionTypeReference(): TypeReference? = exceptionTypeReference
                override fun getHandlerCodeAddress(): Int = handler.handlerCodeAddress
                override fun equals(other: Any?): Boolean = this === other
                override fun hashCode(): Int = System.identityHashCode(this)
            }
        }
        return object : BaseTryBlock<ExceptionHandler>() {
            override fun getStartCodeAddress(): Int = source.startCodeAddress
            override fun getCodeUnitCount(): Int = source.codeUnitCount
            override fun getExceptionHandlers(): List<ExceptionHandler> = handlers
        }
    }

    private fun reboundInstruction(builder: DexBuilder, instruction: Instruction): Instruction {
        if (instruction !is ReferenceInstruction) return instruction
        val reference = builder.internReference(instruction.reference)
        return when (instruction) {
            is Instruction45cc -> BuilderInstruction45cc(
                instruction.opcode,
                instruction.registerCount,
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
                reference,
                builder.internReference(instruction.reference2),
            )
            is Instruction4rcc -> BuilderInstruction4rcc(
                instruction.opcode,
                instruction.startRegister,
                instruction.registerCount,
                reference,
                builder.internReference(instruction.reference2),
            )
            is Instruction20bc -> BuilderInstruction20bc(
                instruction.opcode, instruction.verificationError, reference,
            )
            is Instruction21c -> BuilderInstruction21c(instruction.opcode, instruction.registerA, reference)
            is Instruction22c -> BuilderInstruction22c(
                instruction.opcode, instruction.registerA, instruction.registerB, reference,
            )
            is Instruction31c -> BuilderInstruction31c(instruction.opcode, instruction.registerA, reference)
            is Instruction35c -> BuilderInstruction35c(
                instruction.opcode,
                instruction.registerCount,
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
                reference,
            )
            is Instruction3rc -> BuilderInstruction3rc(
                instruction.opcode, instruction.startRegister, instruction.registerCount, reference,
            )
            else -> error("Unsupported reference instruction format: ${instruction.opcode.format}")
        }
    }

    private fun reboundDebugItem(builder: DexBuilder, item: DebugItem): DebugItem = when (item) {
        is StartLocal -> object : StartLocal {
            override fun getCodeAddress(): Int = item.codeAddress
            override fun getDebugItemType(): Int = item.debugItemType
            override fun getRegister(): Int = item.register
            override fun getNameReference() = builder.internNullableStringReference(item.name)
            override fun getTypeReference() = builder.internNullableTypeReference(item.type)
            override fun getSignatureReference() = builder.internNullableStringReference(item.signature)
            override fun getName(): String? = item.name
            override fun getType(): String? = item.type
            override fun getSignature(): String? = item.signature
        }
        is SetSourceFile -> object : SetSourceFile {
            override fun getCodeAddress(): Int = item.codeAddress
            override fun getDebugItemType(): Int = item.debugItemType
            override fun getSourceFile(): String? = item.sourceFile
            override fun getSourceFileReference() = builder.internNullableStringReference(item.sourceFile)
        }
        else -> item
    }
}
