package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20bc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction45cc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction4rcc
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction20bc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction45cc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction4rcc

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
    ): Instruction {
        if (operands.isEmpty()) return instruction
        val references = operands.map(DexReferenceOperand::asReference)
        return when (instruction) {
            is Instruction20bc -> ImmutableInstruction20bc(
                instruction.opcode,
                instruction.verificationError,
                references.single(),
            )
            is Instruction21c -> ImmutableInstruction21c(
                instruction.opcode,
                instruction.registerA,
                references.single(),
            )
            is Instruction22c -> ImmutableInstruction22c(
                instruction.opcode,
                instruction.registerA,
                instruction.registerB,
                references.single(),
            )
            is Instruction31c -> ImmutableInstruction31c(
                instruction.opcode,
                instruction.registerA,
                references.single(),
            )
            is Instruction35c -> ImmutableInstruction35c(
                instruction.opcode,
                instruction.registerCount,
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
                references.single(),
            )
            is Instruction3rc -> ImmutableInstruction3rc(
                instruction.opcode,
                instruction.startRegister,
                instruction.registerCount,
                references.single(),
            )
            is Instruction45cc -> ImmutableInstruction45cc(
                instruction.opcode,
                instruction.registerCount,
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
                references[0],
                references[1],
            )
            is Instruction4rcc -> ImmutableInstruction4rcc(
                instruction.opcode,
                instruction.startRegister,
                instruction.registerCount,
                references[0],
                references[1],
            )
            else -> error("Unsupported staged-reference instruction format: ${instruction.opcode.format}")
        }
    }
}
