package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.PayloadInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction

/** Detached pre-layout instruction specification. */
sealed class InstructionTemplate private constructor(
    source: Instruction,
    logicalRegisters: List<LogicalRegister>?,
    referenceOperands: List<DexReferenceOperand>?,
) {
    internal val opcode: Opcode = source.opcode

    /** An immutable copy of the submitted instruction, or null when dexlib2 cannot encode it. */
    internal val instruction: Instruction? = try {
        ImmutableInstruction.of(source)
    } catch (unencodable: RuntimeException) {
        null
    }

    internal val logicalRegisters: List<LogicalRegister>? = logicalRegisters?.toList()
    internal val referenceOperands: List<DexReferenceOperand> = referenceOperands?.toList()
        ?: instruction?.referenceOperands().orEmpty().map(DexReferenceOperand::Existing)

    /** The transaction-bound code position this instruction's offset names. */
    internal open val target: PendingCodeAnchor? get() = null

    /** A detached encoded instruction with no transaction-relative control operand. */
    class Encoded : InstructionTemplate {
        constructor(instruction: Instruction) : super(instruction, null, null)

        constructor(instruction: Instruction, logicalRegisters: List<LogicalRegister>) :
            super(instruction, logicalRegisters, null)

        constructor(instruction: Instruction, reference: DexReferenceOperand) :
            super(instruction, null, listOf(reference))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            logicalRegisters: List<LogicalRegister>,
        ) : super(instruction, logicalRegisters, listOf(reference))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            reference2: DexReferenceOperand,
        ) : super(instruction, null, listOf(reference, reference2))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            reference2: DexReferenceOperand,
            logicalRegisters: List<LogicalRegister>,
        ) : super(instruction, logicalRegisters, listOf(reference, reference2))
    }

    /** A detached conditional or `goto` whose target belongs to this transaction. */
    class Branch(
        instruction: OffsetInstruction,
        override val target: PendingCodeAnchor,
        logicalRegisters: List<LogicalRegister>? = null,
    ) : InstructionTemplate(instruction, logicalRegisters, null)

    /** A detached `fill-array-data` instruction with a logical payload target. */
    class FillArrayData(
        instruction: Instruction31t,
        override val target: PendingCodeAnchor,
        logicalRegisters: List<LogicalRegister>? = null,
    ) : InstructionTemplate(instruction, logicalRegisters, null)

    /** A detached switch owner whose payload and owner-relative cases are logical targets. */
    class Switch(
        instruction: Instruction31t,
        payloadTarget: PendingCodeAnchor,
        switchCases: List<Case>,
        logicalRegisters: List<LogicalRegister>? = null,
    ) : InstructionTemplate(instruction, logicalRegisters, null) {
        /** One switch key and its transaction-bound logical target. */
        data class Case(val key: Int, val target: PendingCodeAnchor)

        override val target: PendingCodeAnchor = payloadTarget
        internal val switchCases: List<Case> = switchCases.toList()
    }

    /** Detached array or switch payload data. Owner instructions keep all logical targets. */
    class Payload(instruction: PayloadInstruction) : InstructionTemplate(instruction, null, null)
}
