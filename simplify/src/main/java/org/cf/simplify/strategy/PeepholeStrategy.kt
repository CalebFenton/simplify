package org.cf.simplify.strategy

import org.cf.smalivm.core.DexReferenceOperand
import org.cf.smalivm.core.InstructionTemplate
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
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
        val method = input.method
        for (address in method.instructionAddresses) {
            if (input.graph.isDead(address)) continue
            val instruction = method.instructionAt(address)
            when {
                isClassForName(instruction) -> peepClassForName(input, address, instruction)
                instruction.opcode in CONDITIONAL_BRANCHES -> {
                    val children = input.graph.children(address)
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
                    val parents = input.graph.parentAddresses(address)
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

    /**
     * `Class.forName` of the executing class's own name becomes `const-class`: that class is already initialized,
     * so the call has no initialization effect to keep. The name must come from a `const-string` that is the only
     * way into the call.
     */
    private fun peepClassForName(input: StrategyInput, address: Int, instruction: Instruction) {
        val method = input.method
        val argument = invokeRegisters(instruction).singleOrNull() ?: return
        val flow = input.controlFlow ?: return
        val index = flow.indexOf(address) ?: return
        val literalInstruction = flow.fallthroughPredecessor(index)?.let(flow::instructionAt) ?: return
        if (literalInstruction.opcode != Opcode.CONST_STRING ||
            (literalInstruction as OneRegisterInstruction).registerA != argument) return
        val literal = ((literalInstruction as ReferenceInstruction).reference as StringReference).string
        val descriptor = method.definingClass.descriptor
        if (literal != descriptor.substring(1, descriptor.length - 1).replace('/', '.')) return
        val result = moveResultAfter(method, address) ?: return
        val replacement = InstructionTemplate.Encoded(
            ImmutableInstruction21c(Opcode.CONST_CLASS, result.destination, ImmutableTypeReference(descriptor)),
            DexReferenceOperand.Staged(input.transaction.stageType(descriptor)))
        input.transaction.replaceInstruction(address, listOf(replacement))
        input.transaction.removeInstruction(result.address)
        input.recordOptimization(PEEPHOLE_OPTIMIZATIONS)
    }

    companion object {
        const val PEEPHOLE_OPTIMIZATIONS = "peephole optimizations"
        const val CONSTANTIZED_IFS = "constantized ifs"
        private val CONDITIONAL_BRANCHES = setOf(
            Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE,
            Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ)
    }
}
