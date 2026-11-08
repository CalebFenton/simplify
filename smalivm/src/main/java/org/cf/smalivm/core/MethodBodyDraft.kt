package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.iface.instruction.Instruction

internal data class DraftInstructionTemplate(
    val instruction: Instruction,
    val target: PendingCodeAnchor? = null,
    val switchCases: List<InstructionTemplate.Switch.Case> = emptyList(),
    val logicalRegisters: List<LogicalRegister>? = null,
    val referenceOperands: List<DexReferenceOperand> = emptyList(),
)

/** Instructions inserted before one baseline instruction, with one anchor per inserted instruction. */
internal data class DraftInsertion(
    val instructions: List<DraftInstructionTemplate>,
    val anchors: List<PendingCodeAnchor>,
)

/** Logical instructions and metadata after every transaction anchor has been resolved. */
internal data class ResolvedMethodBodyEdit(
    val body: MethodBody,
    val positionsByPendingAnchor: Map<PendingCodeAnchor, LogicalCodePosition>,
)

/**
 * Applies indexed commands to a baseline body. Commands are keyed by baseline code address; insertions for one
 * address are in submission order. Labels, try ranges and debug items on an edited instruction move to the first
 * instruction that replaces or precedes it.
 */
internal object MethodBodyDraft {
    private class DraftRecord(val template: DraftInstructionTemplate, val baselinePosition: LogicalCodePosition?)

    /** Edited instructions in order, indexed by provisional ordinal, and where each old position now points. */
    private class Ordering(
        val records: List<DraftRecord>,
        val transferOrdinals: Map<LogicalCodePosition, Int>,
        val survivingOrdinals: Map<LogicalCodePosition, Int>,
        val anchorOrdinals: Map<PendingCodeAnchor, Int>,
    )

    /** Provisional ordinal to the ordinal of the next retained record, after unowned baseline payloads go. */
    private class Compaction(
        val retained: List<DraftRecord>,
        val finalOrdinalByProvisional: IntArray,
        val finalOrdinalAtOrAfter: IntArray,
    )

    fun resolve(
        baselineBody: MethodBody,
        insertions: Map<Int, List<DraftInsertion>>,
        replacements: Map<Int, List<DraftInstructionTemplate>>,
        removals: Set<Int>,
        anchorsByPosition: Map<LogicalCodePosition, List<PendingCodeAnchor>>,
        submittedTryRegions: List<TryRegionSpec>?,
    ): ResolvedMethodBodyEdit {
        val ordering = order(baselineBody, insertions, replacements, removals, anchorsByPosition)
        val targets = HashMap<Int, Int>()
        val switchCases = HashMap<Int, List<Pair<Int, Int>>>()
        resolveControl(baselineBody, ordering, targets, switchCases)
        val compaction = compact(ordering.records, unownedBaselinePayloads(baselineBody, ordering.records, targets))

        val positions = LogicalCodePosition.forBody(compaction.retained.size + 1)
        fun positionAt(provisionalOrdinal: Int): LogicalCodePosition =
            positions[compaction.finalOrdinalAtOrAfter[provisionalOrdinal]]
        fun transferred(position: LogicalCodePosition): Int = ordering.transferOrdinals.getValue(position)
        fun anchored(anchor: PendingCodeAnchor): Int = ordering.anchorOrdinals.getValue(anchor)

        val encoded = ArrayList<EncodedInstruction>(compaction.retained.size)
        val positionsByCodeAddress = LinkedHashMap<Int, LogicalCodePosition>(compaction.retained.size + 1)
        val logicalRegisters = LinkedHashMap<LogicalCodePosition, List<LogicalRegister>>()
        val referenceOperands = LinkedHashMap<LogicalCodePosition, List<DexReferenceOperand>>()
        var codeAddress = 0
        compaction.retained.forEachIndexed { ordinal, record ->
            val position = positions[ordinal]
            val template = record.template
            positionsByCodeAddress[codeAddress] = position
            encoded += EncodedInstruction(position, codeAddress, template.instruction)
            codeAddress += template.instruction.codeUnits
            template.logicalRegisters?.let { logicalRegisters[position] = it }
            if (template.referenceOperands.isNotEmpty()) referenceOperands[position] = template.referenceOperands
        }
        positionsByCodeAddress[codeAddress] = positions.last()

        val controlTargets = LinkedHashMap<LogicalCodePosition, LogicalCodePosition>()
        val logicalSwitchCases = LinkedHashMap<LogicalCodePosition, List<ImportedSwitchCase>>()
        targets.forEach { (source, target) ->
            val finalSource = compaction.finalOrdinalByProvisional[source]
            if (finalSource < 0) return@forEach
            val sourcePosition = positions[finalSource]
            controlTargets[sourcePosition] = positionAt(target)
            switchCases[source]?.let { cases ->
                logicalSwitchCases[sourcePosition] = cases.map { (key, caseTarget) ->
                    ImportedSwitchCase(key, positionAt(caseTarget))
                }
            }
        }

        fun tryRegion(start: Int, end: Int, handlers: List<Pair<String?, Int>>): ImportedTryRegion? {
            val startPosition = positionAt(start)
            val endPosition = positionAt(end)
            if (startPosition == endPosition) return null
            val translatedHandlers = handlers.map { (exceptionType, target) ->
                ImportedExceptionHandler(exceptionType, positionAt(target))
            }
            return ImportedTryRegion(startPosition, endPosition, translatedHandlers.toPersistentList())
        }
        val tryRegions = submittedTryRegions?.mapNotNull { region ->
            tryRegion(
                anchored(region.start),
                anchored(region.end),
                region.catchHandlers.map { handler ->
                    (handler as? CatchHandlerSpec.Typed)?.exceptionType?.descriptor to anchored(handler.target)
                },
            )
        } ?: baselineBody.tryRegions.mapNotNull { region ->
            tryRegion(
                transferred(region.start),
                transferred(region.end),
                region.handlers.map { handler -> handler.exceptionType to transferred(handler.target) },
            )
        }
        val debugItems = baselineBody.debugItems.map { item ->
            ImportedDebugItem(positionAt(transferred(item.position)), item.item)
        }
        val logicalBody = MethodBody.logical(
            baselineBody.registerCount,
            encoded,
            positionsByCodeAddress,
            controlTargets,
            logicalSwitchCases,
            tryRegions,
            debugItems,
            logicalRegisters,
            referenceOperands,
        )
        return ResolvedMethodBodyEdit(logicalBody, ordering.anchorOrdinals.mapValues { (_, ordinal) -> positionAt(ordinal) })
    }

    private fun order(
        baselineBody: MethodBody,
        insertions: Map<Int, List<DraftInsertion>>,
        replacements: Map<Int, List<DraftInstructionTemplate>>,
        removals: Set<Int>,
        anchorsByPosition: Map<LogicalCodePosition, List<PendingCodeAnchor>>,
    ): Ordering {
        val baseline = baselineBody.instructions
        val records = ArrayList<DraftRecord>(baseline.size)
        val transferOrdinals = HashMap<LogicalCodePosition, Int>(baseline.size + 1)
        val survivingOrdinals = HashMap<LogicalCodePosition, Int>(baseline.size)
        val anchorOrdinals = LinkedHashMap<PendingCodeAnchor, Int>()

        for (baselineRecord in baseline) {
            val address = baselineRecord.codeAddress
            val position = baselineRecord.position
            val inserted = insertions[address].orEmpty()
            val firstInsertedOrdinal = records.size.takeIf { inserted.isNotEmpty() }
            for (insertion in inserted) {
                insertion.instructions.forEachIndexed { index, instruction ->
                    anchorOrdinals[insertion.anchors[index]] = records.size
                    records += DraftRecord(instruction, baselinePosition = null)
                }
            }

            val replacement = replacements[address]
            val firstReplacementOrdinal = records.size.takeIf { replacement != null }
            if (replacement != null) {
                replacement.forEach { records += DraftRecord(it, baselinePosition = null) }
            } else if (address !in removals && position !in baselineBody.syntheticPayloadPaddingPositions) {
                survivingOrdinals[position] = records.size
                records += DraftRecord(DraftInstructionTemplate(baselineRecord.instruction), position)
            }

            transferOrdinals[position] =
                firstInsertedOrdinal ?: firstReplacementOrdinal ?: survivingOrdinals[position] ?: records.size
        }
        transferOrdinals[baselineBody.endPosition] = records.size
        anchorsByPosition.forEach { (position, anchors) ->
            val ordinal = transferOrdinals.getValue(position)
            anchors.forEach { anchor -> anchorOrdinals[anchor] = ordinal }
        }
        return Ordering(records, transferOrdinals, survivingOrdinals, anchorOrdinals)
    }

    /** Fills provisional control targets and switch cases: surviving baseline owners, then submitted templates. */
    private fun resolveControl(
        baselineBody: MethodBody,
        ordering: Ordering,
        targets: MutableMap<Int, Int>,
        switchCases: MutableMap<Int, List<Pair<Int, Int>>>,
    ) {
        baselineBody.controlTargets.forEach { (source, target) ->
            val sourceOrdinal = ordering.survivingOrdinals[source] ?: return@forEach
            targets[sourceOrdinal] = ordering.transferOrdinals.getValue(target)
            baselineBody.switchCases[source]?.let { cases ->
                switchCases[sourceOrdinal] = cases.map { case -> case.key to ordering.transferOrdinals.getValue(case.target) }
            }
        }
        ordering.records.forEachIndexed { ordinal, record ->
            val target = record.template.target ?: return@forEachIndexed
            targets[ordinal] = ordering.anchorOrdinals.getValue(target)
            if (record.template.instruction.opcode in OpcodeGroups.SWITCH) {
                switchCases[ordinal] = record.template.switchCases.map { case ->
                    case.key to ordering.anchorOrdinals.getValue(case.target)
                }
            }
        }
    }

    /** Baseline payloads whose every baseline owner was edited away; nothing can reach them any more. */
    private fun unownedBaselinePayloads(
        baselineBody: MethodBody,
        records: List<DraftRecord>,
        targets: Map<Int, Int>,
    ): Set<Int> {
        val baselineOpcodes = baselineBody.instructions.associate { it.position to it.instruction.opcode }
        val ownedBaselinePayloads = baselineBody.controlTargets
            .filterKeys { baselineOpcodes[it] in OpcodeGroups.PAYLOAD_OWNER }
            .values
            .toSet()
        val targetedPayloadOrdinals = targets.entries.mapNotNullTo(hashSetOf()) { (source, target) ->
            target.takeIf { records[source].template.instruction.opcode in OpcodeGroups.PAYLOAD_OWNER }
        }
        return records.indices.filterTo(hashSetOf()) { ordinal ->
            val record = records[ordinal]
            record.template.instruction.opcode in OpcodeGroups.PAYLOAD &&
                record.baselinePosition in ownedBaselinePayloads &&
                ordinal !in targetedPayloadOrdinals
        }
    }

    private fun compact(records: List<DraftRecord>, removedOrdinals: Set<Int>): Compaction {
        val finalOrdinalByProvisional = IntArray(records.size) { -1 }
        val retained = ArrayList<DraftRecord>(records.size - removedOrdinals.size)
        records.forEachIndexed { ordinal, record ->
            if (ordinal !in removedOrdinals) {
                finalOrdinalByProvisional[ordinal] = retained.size
                retained += record
            }
        }
        val finalOrdinalAtOrAfter = IntArray(records.size + 1)
        var nextOrdinal = retained.size
        finalOrdinalAtOrAfter[records.size] = nextOrdinal
        for (ordinal in records.indices.reversed()) {
            val finalOrdinal = finalOrdinalByProvisional[ordinal]
            if (finalOrdinal >= 0) nextOrdinal = finalOrdinal
            finalOrdinalAtOrAfter[ordinal] = nextOrdinal
        }
        return Compaction(retained, finalOrdinalByProvisional, finalOrdinalAtOrAfter)
    }
}
