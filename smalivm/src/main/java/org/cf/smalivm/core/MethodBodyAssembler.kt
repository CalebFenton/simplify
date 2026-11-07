package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation

/** Freezes one solved layout as a committed method body. */
internal object MethodBodyAssembler {
    fun assemble(layout: MethodLayout): MethodBody {
        val instructions = layout.instructions.map { record ->
            materializeReferenceOperands(record.instruction, record.referenceOperands)
        }
        val implementation = ImmutableMethodImplementation(
            layout.registerCount,
            instructions,
            layout.tryBlocks,
            layout.debugItems,
        )
        val syntheticPaddingAddresses = layout.instructions.mapNotNullTo(linkedSetOf()) { record ->
            record.codeAddress.takeIf { record.position == null && record.instruction.opcode == Opcode.NOP }
        }
        return MethodBody.import(implementation, syntheticPaddingAddresses)
    }

    internal fun materializeReferenceOperands(
        instruction: Instruction,
        operands: List<DexReferenceOperand>,
    ): Instruction =
        if (operands.isEmpty()) instruction else instruction.withReferences(operands.map(DexReferenceOperand::asReference))
}
