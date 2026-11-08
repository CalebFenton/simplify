package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderOffsetInstruction
import com.android.tools.smali.dexlib2.iface.MethodImplementation
import com.android.tools.smali.dexlib2.iface.debug.DebugItem
import com.android.tools.smali.dexlib2.iface.debug.EndLocal
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.debug.RestartLocal
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.formatter.DexFormatter

internal data class EncodedInstruction(
    val position: LogicalCodePosition,
    val codeAddress: Int,
    val instruction: Instruction,
)

internal data class ImportedSwitchCase(val key: Int, val target: LogicalCodePosition)
internal data class ImportedExceptionHandler(val exceptionType: String?, val target: LogicalCodePosition)
internal data class ImportedTryRegion(
    val start: LogicalCodePosition,
    val end: LogicalCodePosition,
    val handlers: PersistentList<ImportedExceptionHandler>,
)
internal data class ImportedDebugItem(val position: LogicalCodePosition, val item: DebugItem)

/**
 * A hard failure of ART's method verifier, which makes the declaring class fail verification. [codeAddress] is
 * the address the failure names: the instruction, the try range start, or the target that is not an instruction start.
 */
internal data class VerifierRejection(val reason: Reason, val codeAddress: Int) {
    enum class Reason {
        NO_INSTRUCTIONS,
        /** A try range starts off an instruction start, is empty, or ends past the code. */
        TRY_RANGE_OUTSIDE_INSTRUCTIONS,
        HANDLER_NOT_AN_INSTRUCTION,
        /** A non-range five-register instruction declares more registers than its five register fields. */
        ARGUMENT_COUNT_ABOVE_FIVE,
        /** A branch, payload or switch case target is not an instruction start inside the code. */
        TARGET_NOT_AN_INSTRUCTION,
        /** A switch names something other than a switch table of its kind. */
        SWITCH_TABLE_OF_WRONG_KIND,
    }
}

/** Immutable assembled code and metadata for one concrete method. */
internal class MethodBody private constructor(
    internal val implementation: ImmutableMethodImplementation,
    internal val instructions: PersistentList<EncodedInstruction>,
    internal val positionsByCodeAddress: PersistentMap<Int, LogicalCodePosition>,
    internal val controlTargets: PersistentMap<LogicalCodePosition, LogicalCodePosition>,
    internal val switchCases: PersistentMap<LogicalCodePosition, PersistentList<ImportedSwitchCase>>,
    internal val tryRegions: PersistentList<ImportedTryRegion>,
    internal val debugItems: PersistentList<ImportedDebugItem>,
    internal val syntheticPayloadPaddingPositions: PersistentSet<LogicalCodePosition>,
    internal val logicalRegistersByPosition: PersistentMap<LogicalCodePosition, PersistentList<LogicalRegister>>,
    internal val referenceOperandsByPosition: PersistentMap<LogicalCodePosition, PersistentList<DexReferenceOperand>>,
) {
    internal fun hasSameEncodingAs(other: MethodBody): Boolean =
        registerCount == other.registerCount &&
            instructions.map { it.codeAddress to instructionKey(it.instruction) } ==
            other.instructions.map { it.codeAddress to instructionKey(it.instruction) } &&
            implementation.tryBlocks.map { block ->
                Triple(
                    block.startCodeAddress,
                    block.codeUnitCount,
                    block.exceptionHandlers.map { it.exceptionType to it.handlerCodeAddress },
                )
            } == other.implementation.tryBlocks.map { block ->
                Triple(
                    block.startCodeAddress,
                    block.codeUnitCount,
                    block.exceptionHandlers.map { it.exceptionType to it.handlerCodeAddress },
                )
            } &&
            implementation.debugItems.map(::debugItemKey) == other.implementation.debugItems.map(::debugItemKey)

    private fun instructionKey(instruction: Instruction): List<Any?> = buildList {
        add(instruction.opcode)
        when (instruction) {
            is FiveRegisterInstruction -> addAll(
                listOf(
                    instruction.registerCount,
                    instruction.registerC,
                    instruction.registerD,
                    instruction.registerE,
                    instruction.registerF,
                    instruction.registerG,
                ),
            )
            is RegisterRangeInstruction -> addAll(listOf(instruction.startRegister, instruction.registerCount))
            is ThreeRegisterInstruction -> addAll(listOf(instruction.registerA, instruction.registerB, instruction.registerC))
            is TwoRegisterInstruction -> addAll(listOf(instruction.registerA, instruction.registerB))
            is OneRegisterInstruction -> add(instruction.registerA)
        }
        if (instruction is NarrowLiteralInstruction) add(instruction.narrowLiteral)
        if (instruction is WideLiteralInstruction) add(instruction.wideLiteral)
        if (instruction is ReferenceInstruction) add(DexFormatter.INSTANCE.getReference(instruction.reference))
        if (instruction is DualReferenceInstruction) add(DexFormatter.INSTANCE.getReference(instruction.reference2))
        if (instruction is OffsetInstruction) add(instruction.codeOffset)
        if (instruction is SwitchPayload) {
            addAll(instruction.switchElements.flatMap { listOf(it.key, it.offset) })
        }
        if (instruction is ArrayPayload) {
            add(instruction.elementWidth)
            addAll(instruction.arrayElements)
        }
    }

    private fun debugItemKey(item: DebugItem): List<Any?> = when (item) {
        is LineNumber -> listOf(item.debugItemType, item.codeAddress, item.lineNumber)
        is StartLocal -> listOf(
            item.debugItemType,
            item.codeAddress,
            item.register,
            item.name,
            item.type,
            item.signature,
        )
        is EndLocal -> listOf(item.debugItemType, item.codeAddress, item.register, item.name, item.type, item.signature)
        is RestartLocal -> listOf(item.debugItemType, item.codeAddress, item.register, item.name, item.type, item.signature)
        is SetSourceFile -> listOf(item.debugItemType, item.codeAddress, item.sourceFile)
        else -> listOf(item.debugItemType, item.codeAddress)
    }

    internal val registerCount: Int get() = implementation.registerCount

    /** The position after the last instruction, where try ranges and debug items can end. */
    internal val endPosition: LogicalCodePosition =
        positionsByCodeAddress.getValue(positionsByCodeAddress.keys.max())

    internal val instructionsByCodeAddress: Map<Int, Instruction> =
        instructions.associate { it.codeAddress to it.instruction }

    internal val hasConstStringInstruction: Boolean =
        instructions.any { it.instruction.opcode == Opcode.CONST_STRING }

    internal fun controlTargetAt(codeAddress: Int): Int? {
        val source = positionsByCodeAddress[codeAddress] ?: return null
        val target = controlTargets[source] ?: return null
        return codeAddressByPosition[target]
    }

    internal fun switchCasesAt(codeAddress: Int): List<Pair<Int, Int>> {
        val source = positionsByCodeAddress[codeAddress] ?: return emptyList()
        return switchCases[source].orEmpty().map { it.key to requireNotNull(codeAddressByPosition[it.target]) }
    }

    private val codeAddressByPosition: Map<LogicalCodePosition, Int> =
        positionsByCodeAddress.entries.associate { (address, position) -> position to address }

    internal companion object {
        fun fromLinearInstructions(
            registerCount: Int,
            instructions: List<Instruction>,
        ): MethodBody = import(
            ImmutableMethodImplementation(registerCount, instructions, emptyList(), emptyList()),
        )

        /**
         * Imports code [verifierRejection] accepts. A try range may end, and a debug item may sit, inside an
         * instruction or past the code; each takes effect at the next instruction start, which is where ART's
         * verifier and its line table apply them.
         */
        fun import(
            implementation: MethodImplementation,
            syntheticPayloadPaddingAddresses: Set<Int>? = null,
        ): MethodBody {
            val sourceInstructions = implementation.instructions.toList()
            val logicalPositions = LogicalCodePosition.forBody(sourceInstructions.size + 1)
            val positionsByAddress = LinkedHashMap<Int, LogicalCodePosition>(sourceInstructions.size + 1)
            val sourceByAddress = LinkedHashMap<Int, Instruction>(sourceInstructions.size)
            var codeAddress = 0
            sourceInstructions.forEachIndexed { ordinal, instruction ->
                positionsByAddress[codeAddress] = logicalPositions[ordinal]
                sourceByAddress[codeAddress] = instruction
                codeAddress += instruction.codeUnits
            }
            positionsByAddress[codeAddress] = logicalPositions[sourceInstructions.size]
            val boundaries = positionsByAddress.keys.toIntArray()
            fun positionAtOrAfter(address: Int): LogicalCodePosition {
                val index = boundaries.binarySearch(address)
                val boundary = if (index >= 0) index else minOf(-index - 1, boundaries.lastIndex)
                return positionsByAddress.getValue(boundaries[boundary])
            }

            val immutable = requireNotNull(ImmutableMethodImplementation.of(implementation))
            val encoded = ArrayList<EncodedInstruction>(sourceInstructions.size)
            codeAddress = 0
            immutable.instructions.forEachIndexed { ordinal, instruction ->
                encoded += EncodedInstruction(logicalPositions[ordinal], codeAddress, instruction)
                codeAddress += instruction.codeUnits
            }

            val controlTargets = persistentHashMapOf<LogicalCodePosition, LogicalCodePosition>().builder()
            val switchCases = persistentHashMapOf<LogicalCodePosition, PersistentList<ImportedSwitchCase>>().builder()
            for ((address, instruction) in sourceByAddress) {
                val sourcePosition = requireNotNull(positionsByAddress[address])
                if (instruction is OffsetInstruction) {
                    val targetAddress = when (instruction) {
                        is BuilderOffsetInstruction -> instruction.target.codeAddress
                        else -> address + instruction.codeOffset
                    }
                    val targetPosition = positionsByAddress[targetAddress]
                    if (targetPosition != null) controlTargets[sourcePosition] = targetPosition

                    if (instruction.opcode in OpcodeGroups.SWITCH && targetPosition != null) {
                        val payload = sourceByAddress[targetAddress] as? SwitchPayload
                        if (payload != null) {
                            switchCases[sourcePosition] = payload.switchElements.map { element ->
                                val caseAddress = address + element.offset
                                ImportedSwitchCase(element.key, requireNotNull(positionsByAddress[caseAddress]))
                            }.toPersistentList()
                        }
                    }
                }
            }
            val tryRegions = immutable.tryBlocks.map { block ->
                ImportedTryRegion(
                    requireNotNull(positionsByAddress[block.startCodeAddress]),
                    positionAtOrAfter(block.startCodeAddress + block.codeUnitCount),
                    block.exceptionHandlers.map { handler ->
                        ImportedExceptionHandler(
                            handler.exceptionType,
                            requireNotNull(positionsByAddress[handler.handlerCodeAddress]),
                        )
                    }.toPersistentList(),
                )
            }.toPersistentList()
            val debugItems = immutable.debugItems.map { item ->
                ImportedDebugItem(positionAtOrAfter(item.codeAddress), item)
            }.toPersistentList()
            val syntheticPaddingPositions = if (syntheticPayloadPaddingAddresses == null) {
                val protectedPositions = buildSet {
                    addAll(controlTargets.values)
                    tryRegions.forEach { region ->
                        add(region.start)
                        add(region.end)
                        region.handlers.forEach { add(it.target) }
                    }
                    debugItems.forEach { add(it.position) }
                }
                encoded.mapIndexedNotNullTo(linkedSetOf()) { index, record ->
                    record.position.takeIf {
                        record.instruction.opcode == Opcode.NOP &&
                            record.codeAddress and 1 != 0 &&
                            encoded.getOrNull(index + 1)?.instruction?.opcode in OpcodeGroups.PAYLOAD &&
                            record.position !in protectedPositions
                    }
                }
            } else {
                syntheticPayloadPaddingAddresses.mapTo(linkedSetOf()) { address ->
                    requireNotNull(positionsByAddress[address])
                }
            }
            return MethodBody(
                immutable,
                encoded.toPersistentList(),
                positionsByAddress.toPersistentMap(),
                controlTargets.build(),
                switchCases.build(),
                tryRegions,
                debugItems,
                syntheticPaddingPositions.toPersistentSet(),
                persistentHashMapOf(),
                persistentHashMapOf(),
            )
        }

        /**
         * The first hard failure ART's method verifier (`runtime/verifier/method_verifier.cc`) reports for
         * [implementation] among its checks of code addresses, switch tables and five-register counts, or null.
         */
        fun verifierRejection(implementation: MethodImplementation): VerifierRejection? {
            val instructionsByAddress = LinkedHashMap<Int, Instruction>()
            var codeSize = 0
            for (instruction in implementation.instructions) {
                instructionsByAddress[codeSize] = instruction
                codeSize += instruction.codeUnits
            }
            if (instructionsByAddress.isEmpty()) return VerifierRejection(VerifierRejection.Reason.NO_INSTRUCTIONS, 0)
            for (block in implementation.tryBlocks) {
                val start = block.startCodeAddress
                val end = start.toLong() + block.codeUnitCount
                if (start !in instructionsByAddress || end <= start || end > codeSize) {
                    return VerifierRejection(VerifierRejection.Reason.TRY_RANGE_OUTSIDE_INSTRUCTIONS, start)
                }
            }
            for (handler in implementation.tryBlocks.flatMap { it.exceptionHandlers }) {
                if (handler.handlerCodeAddress !in instructionsByAddress) {
                    val address = handler.handlerCodeAddress
                    return VerifierRejection(VerifierRejection.Reason.HANDLER_NOT_AN_INSTRUCTION, address)
                }
            }
            for ((address, instruction) in instructionsByAddress) {
                if (instruction is FiveRegisterInstruction && instruction.registerCount > MAX_FIVE_REGISTER_COUNT) {
                    return VerifierRejection(VerifierRejection.Reason.ARGUMENT_COUNT_ABOVE_FIVE, address)
                }
                if (instruction !is OffsetInstruction) continue
                val target = address + instruction.codeOffset
                val targeted = instructionsByAddress[target]
                    ?: return VerifierRejection(VerifierRejection.Reason.TARGET_NOT_AN_INSTRUCTION, target)
                val tableOpcode = SWITCH_TABLE_BY_SWITCH[instruction.opcode] ?: continue
                if (targeted.opcode != tableOpcode) {
                    return VerifierRejection(VerifierRejection.Reason.SWITCH_TABLE_OF_WRONG_KIND, target)
                }
                (targeted as SwitchPayload).switchElements.map { address + it.offset }
                    .firstOrNull { it !in instructionsByAddress }
                    ?.let { case -> return VerifierRejection(VerifierRejection.Reason.TARGET_NOT_AN_INSTRUCTION, case) }
            }
            return null
        }

        private val SWITCH_TABLE_BY_SWITCH = mapOf(
            Opcode.PACKED_SWITCH to Opcode.PACKED_SWITCH_PAYLOAD,
            Opcode.SPARSE_SWITCH to Opcode.SPARSE_SWITCH_PAYLOAD,
        )

        fun logical(
            registerCount: Int,
            instructions: List<EncodedInstruction>,
            positionsByCodeAddress: Map<Int, LogicalCodePosition>,
            controlTargets: Map<LogicalCodePosition, LogicalCodePosition>,
            switchCases: Map<LogicalCodePosition, List<ImportedSwitchCase>>,
            tryRegions: List<ImportedTryRegion>,
            debugItems: List<ImportedDebugItem>,
            logicalRegistersByPosition: Map<LogicalCodePosition, List<LogicalRegister>> = emptyMap(),
            referenceOperandsByPosition: Map<LogicalCodePosition, List<DexReferenceOperand>> = emptyMap(),
        ): MethodBody = MethodBody(
            ImmutableMethodImplementation(
                registerCount,
                instructions.map(EncodedInstruction::instruction),
                emptyList(),
                emptyList(),
            ),
            instructions.toPersistentList(),
            positionsByCodeAddress.toPersistentMap(),
            controlTargets.toPersistentMap(),
            switchCases.mapValues { (_, cases) -> cases.toPersistentList() }.toPersistentMap(),
            tryRegions.toPersistentList(),
            debugItems.toPersistentList(),
            kotlinx.collections.immutable.persistentSetOf(),
            logicalRegistersByPosition
                .mapValues { (_, registers) -> registers.toPersistentList() }
                .toPersistentMap(),
            referenceOperandsByPosition
                .mapValues { (_, operands) -> operands.toPersistentList() }
                .toPersistentMap(),
        )
    }
}
