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
        for (address in input.graph.methodView.instructionAddresses) {
            if (!input.structureGraph.wasReached(address)) continue
            val instruction = input.graph.methodView.instructionAt(address)
            val reference = (instruction as? ReferenceInstruction)?.reference as? MethodReference ?: continue
            when (reference.toString()) {
                METHOD_INVOKE -> replaceMethodInvoke(input, address, instruction)
                FIELD_GET -> replaceFieldGet(input, address, instruction)
            }
        }
    }

    private fun replaceMethodInvoke(input: StrategyInput, address: Int, instruction: Instruction) {
        val registers = invokeRegisters(instruction)
        if (registers.size != 3) return
        val parents = input.structureGraph.parentAddresses(address)
        if (parents.isEmpty()) return
        // A completed graph is not enough: reflection wraps target exceptions, whereas a
        // direct invoke does not. Opaque/denied backend calls carry STRONG effects.
        if (!input.graph.didEveryExecutionContinueNormally(address) ||
            input.structureGraph.sideEffectLevel(address) != SideEffectLevel.NONE) return
        val metadata = input.graph.reflectedMethod(parents, registers[0]) ?: return
        val method = ParsedMethod.parse(metadata.descriptor) ?: return
        val caller = input.graph.methodView.definingClass.descriptor
        if (!canAccess(caller, method.owner, metadata.modifiers, metadata.declaringClassModifiers)) return
        val isStatic = Modifier.isStatic(metadata.modifiers)
        if (!isStatic) {
            val target = input.graph.consensus(parents, registers[1])
            if (target !is Value.Known || target.readsAsNullReference) return
        }
        val argumentsValue = input.graph.consensus(parents, registers[2]) as? Value.Known ?: return
        val rawArguments = argumentsValue.raw
        if (argumentsValue.isNull || argumentsValue.isDalvikNull) {
            if (method.parameters.isNotEmpty()) return
        } else {
            // Heap-backed arrays do not expose per-element proof here. Leave them reflective.
            val arguments = rawArguments as? Array<*> ?: return
            if (arguments.size != method.parameters.size) return
            for ((index, type) in method.parameters.withIndex()) {
                if (isPrimitive(type)) {
                    val argument = arguments[index] ?: return
                    val actual = "L${argument.javaClass.name.replace('.', '/')};"
                    // Reflection permits widening; our unbox sequence only preserves exact wrappers.
                    if (actual != wrapper(type)) return
                }
            }
        }
        val transaction = input.transaction
        val result = moveResultAfter(input.graph.methodView, address)
        val boxResult = result != null && isPrimitive(method.returnType)
        val parameterWords = method.parameters.sumOf(::width)
        val range = parameterWords + (if (isStatic) 0 else 1) > 5
        val receiverWords = if (range && !isStatic) 1 else 0
        val argumentWords = parameterWords + receiverWords
        val scratchWords = argumentWords + if (boxResult) width(method.returnType) else 0
        val scratch = if (scratchWords > 0) transaction.reserveScratchRegisters(scratchWords) else null
        val replacements = mutableListOf<InstructionTemplate>()
        var offset = receiverWords
        method.parameters.forEachIndexed { index, type ->
            val destination = requireNotNull(scratch)[offset]
            replacements += encoded(ImmutableInstruction31i(Opcode.CONST, 0, index), destination)
            replacements += encoded(ImmutableInstruction23x(Opcode.AGET_OBJECT, 0, 0, 0),
                destination, transaction.logicalRegister(registers[2]), destination)
            val castType = if (isPrimitive(type)) wrapper(type) else type
            replacements += InstructionTemplate.Encoded(
                ImmutableInstruction21c(Opcode.CHECK_CAST, 0, ImmutableTypeReference(castType)),
                DexReferenceOperand.Staged(transaction.stageType(castType)), listOf(destination))
            if (isPrimitive(type)) {
                replacements += invoke(transaction, Opcode.INVOKE_VIRTUAL, listOf(destination), unboxMethod(type))
                replacements += encoded(ImmutableInstruction11x(moveResultOpcode(type), 0), destination)
            }
            offset += width(type)
        }
        val arguments = mutableListOf<LogicalRegister>()
        if (!isStatic) {
            val receiver = transaction.logicalRegister(registers[1])
            // Reflection accepts Object; direct dispatch needs the declaring type for DEX verification.
            // Successful concrete execution above proves this cast cannot introduce a failure.
            replacements += InstructionTemplate.Encoded(
                ImmutableInstruction21c(Opcode.CHECK_CAST, 0, ImmutableTypeReference(method.owner)),
                DexReferenceOperand.Staged(transaction.stageType(method.owner)), listOf(receiver))
            if (range) {
                val scratchReceiver = requireNotNull(scratch)[0]
                replacements += encoded(ImmutableInstruction32x(Opcode.MOVE_OBJECT_16, 0, 0), scratchReceiver, receiver)
                arguments += scratchReceiver
            } else arguments += receiver
        }
        repeat(parameterWords) { arguments += requireNotNull(scratch)[receiverWords + it] }
        replacements += invoke(transaction, invokeOpcode(metadata, range), arguments, method)
        if (boxResult) {
            val resultRegisters = List(width(method.returnType)) { requireNotNull(scratch)[argumentWords + it] }
            replacements += encoded(ImmutableInstruction11x(moveResultOpcode(method.returnType), 0), resultRegisters[0])
            replacements += invoke(transaction, Opcode.INVOKE_STATIC, resultRegisters, boxMethod(method.returnType))
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
        val parents = input.structureGraph.parentAddresses(address)
        if (parents.isEmpty()) return
        val metadata = input.graph.reflectedField(parents, registers[0]) ?: return
        val field = ParsedField.parse(metadata.descriptor) ?: return
        // Other owners can initialize or fail access checks; instance reads require a receiver proof.
        if (!Modifier.isStatic(metadata.modifiers) || field.owner != input.graph.methodView.definingClass.descriptor) return
        val transaction = input.transaction
        val result = moveResultAfter(input.graph.methodView, address)
        val primitive = isPrimitive(field.type)
        val scratch = if (result == null || primitive) transaction.reserveScratchRegisters(width(field.type)) else null
        val destination = scratch?.get(0) ?: transaction.logicalRegister(requireNotNull(result).destination)
        val replacements = mutableListOf<InstructionTemplate>(InstructionTemplate.Encoded(
            ImmutableInstruction21c(getStaticGetOpcode(field.type), 0, ImmutableFieldReference(field.owner, field.name, field.type)),
            DexReferenceOperand.Staged(transaction.stageField(field.owner, field.name, field.type)), listOf(destination)))
        if (primitive) {
            replacements += invoke(transaction, Opcode.INVOKE_STATIC,
                List(width(field.type)) { requireNotNull(scratch)[it] }, boxMethod(field.type))
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

    private fun invoke(transaction: MethodEditTransaction, opcode: Opcode, registers: List<LogicalRegister>, method: ParsedMethod): InstructionTemplate {
        transaction.stagePrototype(method.parameters, method.returnType)
        val reference = DexReferenceOperand.Staged(transaction.stageMethod(method.owner, method.name, method.parameters, method.returnType))
        val placeholder = ImmutableMethodReference(method.owner, method.name, method.parameters, method.returnType)
        val instruction = if (opcode == Opcode.INVOKE_STATIC_RANGE || opcode == Opcode.INVOKE_VIRTUAL_RANGE ||
            opcode == Opcode.INVOKE_DIRECT_RANGE || opcode == Opcode.INVOKE_INTERFACE_RANGE) {
            ImmutableInstruction3rc(opcode, 0, registers.size, placeholder)
        } else ImmutableInstruction35c(opcode, registers.size, 0, 0, 0, 0, 0, placeholder)
        return InstructionTemplate.Encoded(instruction, reference, registers)
    }

    private fun encoded(instruction: Instruction, vararg registers: LogicalRegister) =
        InstructionTemplate.Encoded(instruction, registers.asList())

    private fun invokeRegisters(instruction: Instruction): List<Int> = when (instruction) {
        is RegisterRangeInstruction -> List(instruction.registerCount) { instruction.startRegister + it }
        is FiveRegisterInstruction -> listOf(instruction.registerC, instruction.registerD, instruction.registerE,
            instruction.registerF, instruction.registerG).take(instruction.registerCount)
        else -> emptyList()
    }

    private fun moveResultAfter(method: MethodView, address: Int): MoveResult? {
        val next = method.nextAddress(address)
        if (!method.hasAddress(next) || method.opcodeAt(next) != Opcode.MOVE_RESULT_OBJECT) return null
        return MoveResult(next, (method.instructionAt(next) as OneRegisterInstruction).registerA)
    }

    private data class MoveResult(val address: Int, val destination: Int)
    private data class ParsedMethod(val owner: String, val name: String, val parameters: List<String>, val returnType: String) {
        companion object {
            fun parse(descriptor: String): ParsedMethod? {
                val arrow = descriptor.indexOf("->")
                val open = descriptor.indexOf('(', arrow + 2)
                val close = descriptor.indexOf(')', open + 1)
                if (arrow <= 0 || open <= arrow + 2 || close < open || close == descriptor.lastIndex) return null
                val parameters = parseTypes(descriptor.substring(open + 1, close)) ?: return null
                val returnType = descriptor.substring(close + 1)
                if (returnType != "V" && parseTypes(returnType)?.size != 1) return null
                return ParsedMethod(descriptor.substring(0, arrow), descriptor.substring(arrow + 2, open), parameters, returnType)
            }
        }
    }
    private data class ParsedField(val owner: String, val name: String, val type: String) {
        companion object {
            fun parse(descriptor: String): ParsedField? {
                val arrow = descriptor.indexOf("->")
                val colon = descriptor.indexOf(':', arrow + 2)
                if (arrow <= 0 || colon <= arrow + 2 || colon == descriptor.lastIndex) return null
                val type = descriptor.substring(colon + 1)
                if (parseTypes(type)?.size != 1) return null
                return ParsedField(descriptor.substring(0, arrow), descriptor.substring(arrow + 2, colon), type)
            }
        }
    }

    companion object {
        const val UNREFLECTED_METHODS = "unreflected methods"
        const val UNREFLECTED_FIELDS = "unreflected fields"
        private const val METHOD_INVOKE = "Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"
        private const val FIELD_GET = "Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;"

        internal fun getStaticGetOpcode(type: String): Opcode = when (type) {
            "Z" -> Opcode.SGET_BOOLEAN
            "B" -> Opcode.SGET_BYTE
            "C" -> Opcode.SGET_CHAR
            "S" -> Opcode.SGET_SHORT
            "J", "D" -> Opcode.SGET_WIDE
            else -> if (isPrimitive(type)) Opcode.SGET else Opcode.SGET_OBJECT
        }
        private fun width(type: String) = if (type == "J" || type == "D") 2 else 1
        private fun isPrimitive(type: String) = type.length == 1 && type[0] in "ZBCSIJFD"
        private fun moveResultOpcode(type: String) = when {
            width(type) == 2 -> Opcode.MOVE_RESULT_WIDE
            isPrimitive(type) -> Opcode.MOVE_RESULT
            else -> Opcode.MOVE_RESULT_OBJECT
        }
        private fun wrapper(type: String): String = "Ljava/lang/" + when (type) {
            "Z" -> "Boolean;"
            "B" -> "Byte;"
            "C" -> "Character;"
            "S" -> "Short;"
            "I" -> "Integer;"
            "J" -> "Long;"
            "F" -> "Float;"
            "D" -> "Double;"
            else -> error("Not primitive: $type")
        }
        private fun unboxMethod(type: String): ParsedMethod {
            val name = when (type) {
                "Z" -> "booleanValue"
                "B" -> "byteValue"
                "C" -> "charValue"
                "S" -> "shortValue"
                "I" -> "intValue"
                "J" -> "longValue"
                "F" -> "floatValue"
                "D" -> "doubleValue"
                else -> error("Not primitive: $type")
            }
            return ParsedMethod(wrapper(type), name, emptyList(), type)
        }
        private fun boxMethod(type: String) = ParsedMethod(wrapper(type), "valueOf", listOf(type), wrapper(type))
        private fun parseTypes(descriptors: String): List<String>? {
            val result = mutableListOf<String>()
            var index = 0
            while (index < descriptors.length) {
                val start = index
                while (index < descriptors.length && descriptors[index] == '[') index++
                if (index == descriptors.length) return null
                if (descriptors[index] == 'L') {
                    val end = descriptors.indexOf(';', index)
                    if (end < 0) return null
                    index = end + 1
                } else if (descriptors[index] in "ZBCSIJFD") index++ else return null
                result += descriptors.substring(start, index)
            }
            return result
        }
    }
}
