package org.cf.simplify.strategy

import java.lang.reflect.Modifier
import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.*
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.immutable.instruction.*
import com.android.tools.smali.dexlib2.immutable.reference.*

/** Lowers exact reflective accesses using transaction-owned references and scratch registers. */
class UnreflectionStrategy : OptimizationStrategy {
    override val optimizationCategories = setOf(UNREFLECTED_METHODS, UNREFLECTED_FIELDS)

    override fun perform(input: StrategyInput) {
        for (address in input.method.instructionAddresses) {
            if (input.graph.isDead(address)) continue
            val instruction = input.method.instructionAt(address)
            when (invokedMethod(instruction)) {
                METHOD_INVOKE -> replaceMethodInvoke(input, address, instruction)
                FIELD_GET -> replaceFieldGet(input, address, instruction)
            }
        }
    }

    private fun replaceMethodInvoke(input: StrategyInput, address: Int, instruction: Instruction) {
        val registers = invokeRegisters(instruction)
        if (registers.size != 3) return
        val parents = input.graph.parentAddresses(address)
        if (parents.isEmpty()) return
        // A completed graph is not enough: reflection wraps target exceptions, whereas a
        // direct invoke does not. Opaque/denied backend calls carry STRONG effects.
        if (!input.graph.didEveryExecutionContinueNormally(address) ||
            input.graph.sideEffectLevel(address) != SideEffectLevel.NONE) return
        val metadata = (input.graph.reflectedMethod(parents, registers[0]) as? Agreement.Agreed)?.value ?: return
        val method = metadata.method
        val parameters = method.parameterTypes.map(CharSequence::toString)
        val caller = input.method.definingClass.descriptor
        if (!canAccess(caller, method.definingClass, metadata.modifiers, metadata.declaringClassModifiers)) return
        val isStatic = Modifier.isStatic(metadata.modifiers)
        if (!isStatic) {
            val target = input.graph.consensus(parents, registers[1])
            if (target !is Value.Known || target.readsAsNullReference) return
        }
        val argumentsValue = input.graph.consensus(parents, registers[2]) as? Value.Known ?: return
        if (argumentsValue.isNull || argumentsValue.isDalvikNull) {
            if (parameters.isNotEmpty()) return
        } else {
            val classes = (input.graph.arrayElementClasses(parents, registers[2]) as? Agreement.Agreed)?.value ?: return
            if (classes.size != parameters.size) return
            for ((index, type) in parameters.withIndex()) {
                val primitive = Primitive.of(type) ?: continue
                // Reflection permits widening; the unbox sequence only preserves exact wrappers.
                if (classes[index]?.descriptor != primitive.wrapper) return
            }
        }
        val transaction = input.transaction
        val result = moveResultAfter(input.method, address)
        val boxedResult = Primitive.of(method.returnType).takeIf { result != null }
        val parameterWords = parameters.sumOf(::width)
        val range = parameterWords + (if (isStatic) 0 else 1) > 5
        val receiverWords = if (range && !isStatic) 1 else 0
        val argumentWords = parameterWords + receiverWords
        val scratchWords = argumentWords + (boxedResult?.width ?: 0)
        val scratch = if (scratchWords > 0) transaction.reserveScratchRegisters(scratchWords) else null
        val replacements = mutableListOf<InstructionTemplate>()
        var offset = receiverWords
        parameters.forEachIndexed { index, type ->
            val destination = requireNotNull(scratch)[offset]
            val primitive = Primitive.of(type)
            replacements += encoded(ImmutableInstruction31i(Opcode.CONST, 0, index), destination)
            replacements += encoded(ImmutableInstruction23x(Opcode.AGET_OBJECT, 0, 0, 0),
                destination, transaction.logicalRegister(registers[2]), destination)
            val castType = primitive?.wrapper ?: type
            replacements += InstructionTemplate.Encoded(
                ImmutableInstruction21c(Opcode.CHECK_CAST, 0, ImmutableTypeReference(castType)),
                DexReferenceOperand.Staged(transaction.stageType(castType)), listOf(destination))
            if (primitive != null) {
                replacements += invoke(transaction, Opcode.INVOKE_VIRTUAL, listOf(destination), primitive.unbox)
                replacements += encoded(ImmutableInstruction11x(primitive.moveResult, 0), destination)
            }
            offset += width(type)
        }
        val arguments = mutableListOf<LogicalRegister>()
        if (!isStatic) {
            val receiver = transaction.logicalRegister(registers[1])
            // Reflection accepts Object; direct dispatch needs the declaring type for DEX verification.
            // Successful concrete execution above proves this cast cannot introduce a failure.
            replacements += InstructionTemplate.Encoded(
                ImmutableInstruction21c(Opcode.CHECK_CAST, 0, ImmutableTypeReference(method.definingClass)),
                DexReferenceOperand.Staged(transaction.stageType(method.definingClass)), listOf(receiver))
            if (range) {
                val scratchReceiver = requireNotNull(scratch)[0]
                replacements += encoded(ImmutableInstruction32x(Opcode.MOVE_OBJECT_16, 0, 0), scratchReceiver, receiver)
                arguments += scratchReceiver
            } else arguments += receiver
        }
        repeat(parameterWords) { arguments += requireNotNull(scratch)[receiverWords + it] }
        replacements += invoke(transaction, invokeOpcode(metadata, range), arguments, method)
        if (boxedResult != null) {
            val resultRegisters = List(boxedResult.width) { requireNotNull(scratch)[argumentWords + it] }
            replacements += encoded(ImmutableInstruction11x(boxedResult.moveResult, 0), resultRegisters[0])
            replacements += invoke(transaction, Opcode.INVOKE_STATIC, resultRegisters, boxedResult.box)
        } else if (result != null && method.returnType == "V") {
            // Method.invoke returns null for void, rather than leaving the destination unchanged.
            replacements += encoded(ImmutableInstruction11n(Opcode.CONST_4, 0, 0), transaction.logicalRegister(result.destination))
            transaction.removeInstruction(result.address)
        }
        transaction.replaceInstruction(address, replacements)
        input.recordOptimization(UNREFLECTED_METHODS)
    }

    private fun replaceFieldGet(input: StrategyInput, address: Int, instruction: Instruction) {
        val registers = invokeRegisters(instruction)
        if (registers.size != 2) return
        val parents = input.graph.parentAddresses(address)
        if (parents.isEmpty()) return
        val metadata = (input.graph.reflectedField(parents, registers[0]) as? Agreement.Agreed)?.value ?: return
        val field = metadata.field
        // Other owners can initialize or fail access checks; instance reads require a receiver proof.
        if (!Modifier.isStatic(metadata.modifiers) || field.definingClass != input.method.definingClass.descriptor) return
        val transaction = input.transaction
        val result = moveResultAfter(input.method, address)
        val primitive = Primitive.of(field.type)
        val scratch = if (result == null || primitive != null) transaction.reserveScratchRegisters(width(field.type)) else null
        val destination = scratch?.get(0) ?: transaction.logicalRegister(requireNotNull(result).destination)
        val replacements = mutableListOf<InstructionTemplate>(InstructionTemplate.Encoded(
            ImmutableInstruction21c(primitive?.staticGet ?: Opcode.SGET_OBJECT, 0, ImmutableFieldReference.of(field)),
            DexReferenceOperand.Staged(transaction.stageField(field.definingClass, field.name, field.type)),
            listOf(destination)))
        if (primitive != null) {
            replacements += invoke(transaction, Opcode.INVOKE_STATIC,
                List(primitive.width) { requireNotNull(scratch)[it] }, primitive.box)
        } else if (result != null) transaction.removeInstruction(result.address)
        transaction.replaceInstruction(address, replacements)
        input.recordOptimization(UNREFLECTED_FIELDS)
    }

    private fun invokeOpcode(metadata: ReflectedMethodMetadata, range: Boolean): Opcode = when {
        Modifier.isStatic(metadata.modifiers) -> if (range) Opcode.INVOKE_STATIC_RANGE else Opcode.INVOKE_STATIC
        Modifier.isInterface(metadata.declaringClassModifiers) -> if (range) Opcode.INVOKE_INTERFACE_RANGE else Opcode.INVOKE_INTERFACE
        Modifier.isPrivate(metadata.modifiers) -> if (range) Opcode.INVOKE_DIRECT_RANGE else Opcode.INVOKE_DIRECT
        else -> if (range) Opcode.INVOKE_VIRTUAL_RANGE else Opcode.INVOKE_VIRTUAL
    }

    private fun canAccess(caller: String, owner: String, memberModifiers: Int, ownerModifiers: Int): Boolean {
        if (caller == owner) return true
        val samePackage = caller.substringBeforeLast('/', "") == owner.substringBeforeLast('/', "")
        if (!Modifier.isPublic(ownerModifiers) && !samePackage) return false
        return Modifier.isPublic(memberModifiers) || (!Modifier.isPrivate(memberModifiers) && samePackage)
    }

    private fun invoke(
        transaction: MethodEditTransaction,
        opcode: Opcode,
        registers: List<LogicalRegister>,
        method: MethodReference,
    ): InstructionTemplate {
        val parameters = method.parameterTypes.map(CharSequence::toString)
        transaction.stagePrototype(parameters, method.returnType)
        val reference = DexReferenceOperand.Staged(
            transaction.stageMethod(method.definingClass, method.name, parameters, method.returnType))
        val placeholder = ImmutableMethodReference.of(method)
        val instruction = if (opcode in RANGE_INVOKES) {
            ImmutableInstruction3rc(opcode, 0, registers.size, placeholder)
        } else ImmutableInstruction35c(opcode, registers.size, 0, 0, 0, 0, 0, placeholder)
        return InstructionTemplate.Encoded(instruction, reference, registers)
    }

    private fun encoded(instruction: Instruction, vararg registers: LogicalRegister) =
        InstructionTemplate.Encoded(instruction, registers.asList())

    /** A primitive type's boxing, unboxing, register width and static-read opcode. */
    private enum class Primitive(descriptor: Char, wrapperName: String, unboxName: String, val staticGet: Opcode) {
        BOOLEAN('Z', "Boolean", "booleanValue", Opcode.SGET_BOOLEAN),
        BYTE('B', "Byte", "byteValue", Opcode.SGET_BYTE),
        CHAR('C', "Character", "charValue", Opcode.SGET_CHAR),
        SHORT('S', "Short", "shortValue", Opcode.SGET_SHORT),
        INT('I', "Integer", "intValue", Opcode.SGET),
        LONG('J', "Long", "longValue", Opcode.SGET_WIDE),
        FLOAT('F', "Float", "floatValue", Opcode.SGET),
        DOUBLE('D', "Double", "doubleValue", Opcode.SGET_WIDE),
        ;

        val type = descriptor.toString()
        val wrapper = "Ljava/lang/$wrapperName;"
        val width = if (staticGet == Opcode.SGET_WIDE) 2 else 1
        val moveResult = if (width == 2) Opcode.MOVE_RESULT_WIDE else Opcode.MOVE_RESULT
        val unbox: MethodReference = ImmutableMethodReference(wrapper, unboxName, emptyList<String>(), type)
        val box: MethodReference = ImmutableMethodReference(wrapper, "valueOf", listOf(type), wrapper)

        companion object {
            fun of(type: CharSequence): Primitive? = entries.firstOrNull { it.type.contentEquals(type) }
        }
    }

    companion object {
        const val UNREFLECTED_METHODS = "unreflected methods"
        const val UNREFLECTED_FIELDS = "unreflected fields"
        private val METHOD_INVOKE = ImmutableMethodReference("Ljava/lang/reflect/Method;", "invoke",
            listOf("Ljava/lang/Object;", "[Ljava/lang/Object;"), "Ljava/lang/Object;")
        private val FIELD_GET = ImmutableMethodReference("Ljava/lang/reflect/Field;", "get",
            listOf("Ljava/lang/Object;"), "Ljava/lang/Object;")
        private val RANGE_INVOKES = setOf(Opcode.INVOKE_STATIC_RANGE, Opcode.INVOKE_VIRTUAL_RANGE,
            Opcode.INVOKE_DIRECT_RANGE, Opcode.INVOKE_INTERFACE_RANGE)

        private fun width(type: CharSequence): Int = Primitive.of(type)?.width ?: 1
    }
}
