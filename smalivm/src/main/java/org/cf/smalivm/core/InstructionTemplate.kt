package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.PayloadInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction

/** Detached pre-layout instruction specification. */
sealed class InstructionTemplate private constructor() {
    private data class Snapshot(
        val instruction: Instruction?,
        val failureMessage: String?,
        val logicalRegisters: List<LogicalRegister>?,
        val referenceOperands: List<DexReferenceOperand>,
    )

    /** A detached encoded instruction with no transaction-relative control operand. */
    class Encoded private constructor(private val snapshot: Snapshot) : InstructionTemplate() {
        constructor(instruction: Instruction) : this(snapshot(instruction, logicalRegisters = null))

        constructor(instruction: Instruction, logicalRegisters: List<LogicalRegister>) :
            this(snapshot(instruction, logicalRegisters))

        constructor(instruction: Instruction, reference: DexReferenceOperand) :
            this(snapshot(instruction, logicalRegisters = null, referenceOperands = listOf(reference)))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            logicalRegisters: List<LogicalRegister>,
        ) : this(snapshot(instruction, logicalRegisters, listOf(reference)))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            reference2: DexReferenceOperand,
        ) : this(snapshot(instruction, logicalRegisters = null, referenceOperands = listOf(reference, reference2)))

        constructor(
            instruction: Instruction,
            reference: DexReferenceOperand,
            reference2: DexReferenceOperand,
            logicalRegisters: List<LogicalRegister>,
        ) : this(snapshot(instruction, logicalRegisters, listOf(reference, reference2)))

        internal val instruction: Instruction? get() = snapshot.instruction
        internal val failureMessage: String? get() = snapshot.failureMessage
        internal val logicalRegisters: List<LogicalRegister>? get() = snapshot.logicalRegisters
        internal val referenceOperands: List<DexReferenceOperand> get() = snapshot.referenceOperands

        internal fun detachedCopy(): Encoded = Encoded(snapshot)

    }

    /** A detached conditional or `goto` whose target belongs to this transaction. */
    class Branch private constructor(
        private val snapshot: Snapshot,
        internal val target: PendingCodeAnchor,
    ) : InstructionTemplate() {
        constructor(instruction: OffsetInstruction, target: PendingCodeAnchor) :
            this(snapshot(instruction, logicalRegisters = null), target)

        constructor(
            instruction: OffsetInstruction,
            target: PendingCodeAnchor,
            logicalRegisters: List<LogicalRegister>,
        ) : this(snapshot(instruction, logicalRegisters), target)

        internal val instruction: Instruction? get() = snapshot.instruction
        internal val failureMessage: String? get() = snapshot.failureMessage
        internal val logicalRegisters: List<LogicalRegister>? get() = snapshot.logicalRegisters
        internal val referenceOperands: List<DexReferenceOperand> get() = snapshot.referenceOperands

        internal fun detachedCopy(): Branch = Branch(snapshot, target)

    }

    /** A detached `fill-array-data` instruction with a logical payload target. */
    class FillArrayData private constructor(
        private val snapshot: Snapshot,
        internal val target: PendingCodeAnchor,
    ) : InstructionTemplate() {
        constructor(instruction: Instruction31t, target: PendingCodeAnchor) :
            this(snapshot(instruction, logicalRegisters = null), target)

        constructor(
            instruction: Instruction31t,
            target: PendingCodeAnchor,
            logicalRegisters: List<LogicalRegister>,
        ) : this(snapshot(instruction, logicalRegisters), target)

        internal val instruction: Instruction? get() = snapshot.instruction
        internal val failureMessage: String? get() = snapshot.failureMessage
        internal val logicalRegisters: List<LogicalRegister>? get() = snapshot.logicalRegisters
        internal val referenceOperands: List<DexReferenceOperand> get() = snapshot.referenceOperands

        internal fun detachedCopy(): FillArrayData = FillArrayData(snapshot, target)

    }

    /** A detached switch owner with owner-relative cases expressed as logical targets. */
    class Switch private constructor(
        private val snapshot: Snapshot,
        internal val payloadTarget: PendingCodeAnchor,
        switchCases: List<Case>,
    ) : InstructionTemplate() {
        constructor(
            instruction: Instruction31t,
            payloadTarget: PendingCodeAnchor,
            switchCases: List<Case>,
        ) : this(snapshot(instruction, logicalRegisters = null), payloadTarget, switchCases.toList())

        constructor(
            instruction: Instruction31t,
            payloadTarget: PendingCodeAnchor,
            switchCases: List<Case>,
            logicalRegisters: List<LogicalRegister>,
        ) : this(snapshot(instruction, logicalRegisters), payloadTarget, switchCases.toList())

        /** One switch key and its transaction-bound logical target. */
        data class Case(val key: Int, val target: PendingCodeAnchor)

        internal val instruction: Instruction? get() = snapshot.instruction
        internal val failureMessage: String? get() = snapshot.failureMessage
        internal val logicalRegisters: List<LogicalRegister>? get() = snapshot.logicalRegisters
        internal val referenceOperands: List<DexReferenceOperand> get() = snapshot.referenceOperands
        internal val switchCases: List<Case> = switchCases.toList()

        internal fun detachedCopy(): Switch = Switch(snapshot, payloadTarget, switchCases)

    }

    /** Detached array or switch payload data. Owner instructions keep all logical targets. */
    class Payload private constructor(private val snapshot: Snapshot) : InstructionTemplate() {
        constructor(instruction: PayloadInstruction) : this(snapshot(instruction, logicalRegisters = null))

        internal val instruction: Instruction? get() = snapshot.instruction
        internal val failureMessage: String? get() = snapshot.failureMessage
        internal val referenceOperands: List<DexReferenceOperand> get() = snapshot.referenceOperands

        internal fun detachedCopy(): Payload = Payload(snapshot)

    }

    private companion object {
        fun snapshot(
            instruction: Instruction,
            logicalRegisters: List<LogicalRegister>?,
            referenceOperands: List<DexReferenceOperand>? = null,
        ): Snapshot = try {
            val detachedInstruction = ImmutableInstruction.of(instruction)
            Snapshot(
                detachedInstruction,
                null,
                logicalRegisters?.toList(),
                referenceOperands?.toList() ?: operandsOf(detachedInstruction),
            )
        } catch (failure: RuntimeException) {
            Snapshot(
                null,
                failure.message ?: failure.javaClass.simpleName,
                logicalRegisters?.toList(),
                referenceOperands?.toList().orEmpty(),
            )
        }

        fun operandsOf(instruction: Instruction): List<DexReferenceOperand> = when (instruction) {
            is DualReferenceInstruction -> listOf(
                DexReferenceOperand.Existing(instruction.reference),
                DexReferenceOperand.Existing(instruction.reference2),
            )
            is ReferenceInstruction -> listOf(DexReferenceOperand.Existing(instruction.reference))
            else -> emptyList()
        }
    }
}
