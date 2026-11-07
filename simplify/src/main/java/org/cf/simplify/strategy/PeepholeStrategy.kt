package org.cf.simplify.strategy

import org.cf.smalivm.core.DexReferenceOperand
import org.cf.smalivm.core.InstructionTemplate
import org.cf.smalivm.core.MethodView
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.*
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference

/** Local rewrites whose prerequisites preserve initialization and runtime type checks. */
class PeepholeStrategy : OptimizationStrategy {
    override val optimizationCategories = setOf(PEEPHOLE_OPTIMIZATIONS, CONSTANTIZED_IFS)

    override fun perform(input: StrategyInput) {
        val method = input.graph.methodView
        for (address in method.instructionAddresses) {
            if (!input.structureGraph.wasReached(address)) continue
            val instruction = method.instructionAt(address)
            when {
                instruction.opcode == Opcode.INVOKE_STATIC || instruction.opcode == Opcode.INVOKE_STATIC_RANGE ->
                    peepClassForName(input, method, address, instruction)
                instruction.opcode in CONDITIONAL_BRANCHES -> {
                    val children = input.structureGraph.children(address)
                    if (children.size != 1) continue
                    val replacement = if (children[0] == method.nextAddress(address)) {
                        InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))
                    } else {
                        InstructionTemplate.Branch(ImmutableInstruction30t(Opcode.GOTO_32, 0),
                            input.transaction.pendingAnchorForCodeAddress(children[0]))
                    }
                    input.transaction.replaceInstruction(address, listOf(replacement))
                    input.recordOptimization(CONSTANTIZED_IFS)
                }
                instruction.opcode == Opcode.CHECK_CAST -> {
                    val register = (instruction as OneRegisterInstruction).registerA
                    val target = ((instruction as ReferenceInstruction).reference as TypeReference).type
                    val parents = input.structureGraph.parentAddresses(address)
                    val matches = if (parents.isEmpty()) {
                        method.parameters.any { it.register == register && it.type.descriptor == target }
                    } else {
                        parents.all { parent ->
                            val values = input.graph.registerValues(parent, register)
                            values.isNotEmpty() && values.all { it.type?.descriptor == target }
                        }
                    }
                    if (matches) {
                        input.transaction.removeInstruction(address)
                        input.recordOptimization(PEEPHOLE_OPTIMIZATIONS)
                    }
                }
            }
        }
    }

    private fun peepClassForName(input: StrategyInput, method: MethodView, address: Int, instruction: Instruction) {
        val reference = (instruction as? ReferenceInstruction)?.reference as? MethodReference ?: return
        if (reference.definingClass != "Ljava/lang/Class;" || reference.name != "forName" ||
            reference.parameterTypes.map { it.toString() } != listOf("Ljava/lang/String;") ||
            reference.returnType != "Ljava/lang/Class;") return
        val argument = when (instruction) {
            is RegisterRangeInstruction -> instruction.startRegister.takeIf { instruction.registerCount == 1 }
            is FiveRegisterInstruction -> instruction.registerC.takeIf { instruction.registerCount == 1 }
            else -> null
        } ?: return
        val preceding = onlyPrecedingParent(method, address) ?: return
        val literalInstruction = method.instructionAt(preceding)
        if (literalInstruction.opcode != Opcode.CONST_STRING ||
            (literalInstruction as OneRegisterInstruction).registerA != argument) return
        val literal = ((literalInstruction as ReferenceInstruction).reference as? StringReference)?.string ?: return
        val descriptor = method.definingClass.descriptor
        if (literal != descriptor.substring(1, descriptor.length - 1).replace('/', '.')) return
        val next = method.nextAddress(address)
        if (!method.hasAddress(next) || method.opcodeAt(next) != Opcode.MOVE_RESULT_OBJECT) return
        // The executing class is already initialized. Other Class.forName calls must retain initialization.
        val destination = (method.instructionAt(next) as OneRegisterInstruction).registerA
        val replacement = InstructionTemplate.Encoded(
            ImmutableInstruction21c(Opcode.CONST_CLASS, destination, ImmutableTypeReference(descriptor)),
            DexReferenceOperand.Staged(input.transaction.stageType(descriptor)))
        input.transaction.replaceInstruction(address, listOf(replacement))
        input.transaction.removeInstruction(next)
        input.recordOptimization(PEEPHOLE_OPTIMIZATIONS)
    }

    private fun onlyPrecedingParent(method: MethodView, address: Int): Int? {
        val index = method.instructionIndexAtCodeAddress(address) ?: return null
        if (index == 0) return null
        val preceding = method.codeAddressAtInstructionIndex(index - 1)
        if (!method.canContinue(preceding) || method.nextAddress(preceding) != address) return null
        for (candidate in method.instructionAddresses) {
            val instruction = method.instructionAt(candidate)
            if (instruction is OffsetInstruction) {
                if (candidate != preceding && candidate + instruction.codeOffset == address) return null
                if (instruction.opcode == Opcode.PACKED_SWITCH || instruction.opcode == Opcode.SPARSE_SWITCH) {
                    val payloadAddress = candidate + instruction.codeOffset
                    if (method.isPayload(payloadAddress)) {
                        val payload = method.payloadAt(payloadAddress) as? SwitchPayload
                        if (payload?.switchElements?.any { candidate + it.offset == address } == true) return null
                    }
                }
            }
        }
        if (method.tryBlocks.any { block -> block.catchAllAddress == address ||
                block.catches.any { it.handlerAddress == address } }) return null
        return preceding
    }

    companion object {
        const val PEEPHOLE_OPTIMIZATIONS = "peephole optimizations"
        const val CONSTANTIZED_IFS = "constantized ifs"
        private val CONDITIONAL_BRANCHES = setOf(
            Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE,
            Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ)
    }
}
