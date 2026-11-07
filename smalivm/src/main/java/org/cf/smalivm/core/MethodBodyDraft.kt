package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction

internal data class BaselineDraftInstruction(
    val id: BaselineInstructionId?,
    val codeAddress: Int,
    val position: LogicalCodePosition,
    val instruction: Instruction,
    val syntheticPayloadPadding: Boolean,
)

internal data class DraftSwitchCase(
    val key: Int,
    val target: PendingCodeAnchor,
)

internal data class DraftInstructionTemplate(
    val instruction: Instruction,
    val target: PendingCodeAnchor? = null,
    val switchCases: List<DraftSwitchCase> = emptyList(),
    val logicalRegisters: List<LogicalRegister>? = null,
    val referenceOperands: List<DexReferenceOperand> = emptyList(),
)

internal data class DraftInsertion(
    val instructions: List<DraftInstructionTemplate>,
    val anchors: List<PendingCodeAnchor>,
    val commandSequence: Int,
)

internal data class DraftCatchHandlerSpec(
    val exceptionType: String?,
    val target: PendingCodeAnchor,
)

internal data class DraftTryRegionSpec(
    val start: PendingCodeAnchor,
    val end: PendingCodeAnchor,
    val handlers: List<DraftCatchHandlerSpec>,
)

/** One private logical body built from all indexed commands in one forward pass. */
internal class MethodBodyDraft private constructor(
    private val body: MethodBody,
) {
    val instructionCount: Int get() = body.instructions.count { it.instruction.opcode !in MethodView.PAYLOAD_OPCODES }

    fun toLogicalBody(): MethodBody = body

    private data class DraftRecord(
        val template: DraftInstructionTemplate,
        val baselinePosition: LogicalCodePosition?,
    )

    companion object {
        fun resolve(
            baselineBody: MethodBody,
            baseline: List<BaselineDraftInstruction>,
            insertionsByInstruction: Map<BaselineInstructionId, List<DraftInsertion>>,
            replacementsByInstruction: Map<BaselineInstructionId, List<DraftInstructionTemplate>>,
            removedInstructions: Set<BaselineInstructionId>,
            anchorsByPosition: Map<LogicalCodePosition, List<PendingCodeAnchor>>,
            submittedTryRegions: List<DraftTryRegionSpec>?,
        ): ResolvedMethodBodyEdit {
            val records = ArrayList<DraftRecord>(baseline.size)
            val transferOrdinals = LinkedHashMap<LogicalCodePosition, Int>(baseline.size + 1)
            val survivingOrdinals = HashMap<LogicalCodePosition, Int>(baseline.size)
            val anchorOrdinals = LinkedHashMap<PendingCodeAnchor, Int>()

            for (baselineInstruction in baseline) {
                val id = baselineInstruction.id
                val insertions = if (id == null) emptyList() else insertionsByInstruction[id].orEmpty()
                    .sortedBy(DraftInsertion::commandSequence)
                val firstInsertedOrdinal = records.size.takeIf { insertions.isNotEmpty() }
                for (insertion in insertions) {
                    insertion.instructions.forEachIndexed { index, instruction ->
                        anchorOrdinals[insertion.anchors[index]] = records.size
                        records += DraftRecord(instruction, baselinePosition = null)
                    }
                }

                val replacement = id?.let(replacementsByInstruction::get)
                val firstReplacementOrdinal = records.size.takeIf { replacement != null }
                if (replacement != null) {
                    replacement.forEach { records += DraftRecord(it, baselinePosition = null) }
                } else if ((id == null || id !in removedInstructions) && !baselineInstruction.syntheticPayloadPadding) {
                    survivingOrdinals[baselineInstruction.position] = records.size
                    records += DraftRecord(
                        DraftInstructionTemplate(baselineInstruction.instruction),
                        baselineInstruction.position,
                    )
                }

                transferOrdinals[baselineInstruction.position] =
                    firstInsertedOrdinal ?: firstReplacementOrdinal ?: survivingOrdinals[baselineInstruction.position] ?: records.size
            }

            val baselineEnd = requireNotNull(
                baselineBody.positionsByCodeAddress.maxByOrNull(Map.Entry<Int, LogicalCodePosition>::key)?.value,
            )
            transferOrdinals[baselineEnd] = records.size
            anchorsByPosition.forEach { (position, anchors) ->
                val ordinal = requireNotNull(transferOrdinals[position])
                anchors.forEach { anchor -> anchorOrdinals[anchor] = ordinal }
            }

            val provisionalTargets = HashMap<Int, Int>()
            val provisionalSwitchCases = HashMap<Int, List<Pair<Int, Int>>>()
            baselineBody.controlTargets.forEach { (source, target) ->
                val sourceOrdinal = survivingOrdinals[source] ?: return@forEach
                provisionalTargets[sourceOrdinal] = requireNotNull(transferOrdinals[target])
                baselineBody.switchCases[source]?.let { cases ->
                    provisionalSwitchCases[sourceOrdinal] = cases.map { case ->
                        case.key to requireNotNull(transferOrdinals[case.target])
                    }
                }
            }
            records.forEachIndexed { ordinal, record ->
                val target = record.template.target ?: return@forEachIndexed
                provisionalTargets[ordinal] = requireNotNull(anchorOrdinals[target])
                if (record.template.instruction.opcode in SWITCH_OPCODES) {
                    provisionalSwitchCases[ordinal] = record.template.switchCases.map { case ->
                        case.key to requireNotNull(anchorOrdinals[case.target])
                    }
                }
            }

            val baselineOpcodesByPosition = baseline.associate { it.position to it.instruction.opcode }
            val baselinePayloadOwners = baselineBody.controlTargets.entries
                .filter { (source) ->
                    baselineOpcodesByPosition[source] in PAYLOAD_OWNER_OPCODES
                }
                .groupBy(keySelector = Map.Entry<LogicalCodePosition, LogicalCodePosition>::value)
            val targetedPayloadOrdinals = provisionalTargets.entries.mapNotNullTo(hashSetOf()) { (source, target) ->
                target.takeIf { records[source].template.instruction.opcode in PAYLOAD_OWNER_OPCODES }
            }
            val removedPayloadOrdinals = records.mapIndexedNotNullTo(hashSetOf()) { ordinal, record ->
                val position = record.baselinePosition ?: return@mapIndexedNotNullTo null
                if (
                    record.template.instruction.opcode in MethodView.PAYLOAD_OPCODES &&
                    baselinePayloadOwners[position].orEmpty().isNotEmpty() &&
                    ordinal !in targetedPayloadOrdinals
                ) {
                    ordinal
                } else {
                    null
                }
            }

            val finalOrdinalByProvisional = IntArray(records.size) { -1 }
            val retainedRecords = ArrayList<DraftRecord>(records.size - removedPayloadOrdinals.size)
            records.forEachIndexed { ordinal, record ->
                if (ordinal !in removedPayloadOrdinals) {
                    finalOrdinalByProvisional[ordinal] = retainedRecords.size
                    retainedRecords += record
                }
            }
            val finalOrdinalAtOrAfter = IntArray(records.size + 1)
            var nextOrdinal = retainedRecords.size
            finalOrdinalAtOrAfter[records.size] = nextOrdinal
            for (ordinal in records.indices.reversed()) {
                val finalOrdinal = finalOrdinalByProvisional[ordinal]
                if (finalOrdinal >= 0) nextOrdinal = finalOrdinal
                finalOrdinalAtOrAfter[ordinal] = nextOrdinal
            }

            val positions = LogicalCodePosition.forBody(retainedRecords.size + 1)
            fun positionAt(provisionalOrdinal: Int): LogicalCodePosition =
                positions[finalOrdinalAtOrAfter[provisionalOrdinal]]

            val encoded = ArrayList<EncodedInstruction>(retainedRecords.size)
            val positionsByCodeAddress = LinkedHashMap<Int, LogicalCodePosition>(retainedRecords.size + 1)
            var codeAddress = 0
            retainedRecords.forEachIndexed { ordinal, record ->
                val position = positions[ordinal]
                positionsByCodeAddress[codeAddress] = position
                encoded += EncodedInstruction(position, codeAddress, record.template.instruction)
                codeAddress += record.template.instruction.codeUnits
            }
            positionsByCodeAddress[codeAddress] = positions.last()

            val controlTargets = LinkedHashMap<LogicalCodePosition, LogicalCodePosition>()
            val switchCases = LinkedHashMap<LogicalCodePosition, List<ImportedSwitchCase>>()
            val logicalRegisters = LinkedHashMap<LogicalCodePosition, List<LogicalRegister>>()
            val referenceOperands = LinkedHashMap<LogicalCodePosition, List<DexReferenceOperand>>()
            retainedRecords.forEachIndexed { ordinal, record ->
                record.template.logicalRegisters?.let { logicalRegisters[positions[ordinal]] = it }
                if (record.template.referenceOperands.isNotEmpty()) {
                    referenceOperands[positions[ordinal]] = record.template.referenceOperands
                }
            }
            provisionalTargets.forEach { (source, target) ->
                val finalSource = finalOrdinalByProvisional[source]
                if (finalSource < 0) return@forEach
                val sourcePosition = positions[finalSource]
                controlTargets[sourcePosition] = positionAt(target)
                provisionalSwitchCases[source]?.let { cases ->
                    switchCases[sourcePosition] = cases.map { (key, caseTarget) ->
                        ImportedSwitchCase(key, positionAt(caseTarget))
                    }
                }
            }

            val tryRegions = if (submittedTryRegions == null) {
                baselineBody.tryRegions.mapNotNull { region ->
                    val start = positionAt(requireNotNull(transferOrdinals[region.start]))
                    val end = positionAt(requireNotNull(transferOrdinals[region.end]))
                    if (start == end) {
                        null
                    } else {
                        ImportedTryRegion(
                            start,
                            end,
                            region.handlers.map { handler ->
                                ImportedExceptionHandler(
                                    handler.exceptionType,
                                    positionAt(requireNotNull(transferOrdinals[handler.target])),
                                )
                            }.toPersistentList(),
                        )
                    }
                }
            } else {
                submittedTryRegions.mapNotNull { region ->
                    val start = positionAt(requireNotNull(anchorOrdinals[region.start]))
                    val end = positionAt(requireNotNull(anchorOrdinals[region.end]))
                    if (start == end) {
                        null
                    } else {
                        ImportedTryRegion(
                            start,
                            end,
                            region.handlers.map { handler ->
                                ImportedExceptionHandler(
                                    handler.exceptionType,
                                    positionAt(requireNotNull(anchorOrdinals[handler.target])),
                                )
                            }.toPersistentList(),
                        )
                    }
                }
            }
            val debugItems = baselineBody.debugItems.map { item ->
                ImportedDebugItem(
                    positionAt(requireNotNull(transferOrdinals[item.position])),
                    item.item,
                )
            }
            val logicalBody = MethodBody.logical(
                baselineBody.registerCount,
                encoded,
                positionsByCodeAddress,
                controlTargets,
                switchCases,
                tryRegions,
                debugItems,
                logicalRegisters,
                referenceOperands,
            )
            val resolvedAnchors = anchorOrdinals.mapValues { (_, ordinal) -> positionAt(ordinal) }
            return ResolvedMethodBodyEdit(logicalBody, resolvedAnchors)
        }

        fun build(resolved: ResolvedMethodBodyEdit): MethodBodyDraftConstruction = MethodBodyDraftConstruction(
            MethodBodyDraft(resolved.body),
            resolved.positionsByPendingAnchor,
        )

        private val PAYLOAD_OWNER_OPCODES = setOf(
            Opcode.FILL_ARRAY_DATA,
            Opcode.PACKED_SWITCH,
            Opcode.SPARSE_SWITCH,
        )
        private val SWITCH_OPCODES = setOf(Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
    }
}

/** Logical instructions and metadata after every transaction anchor has been resolved. */
internal data class ResolvedMethodBodyEdit(
    val body: MethodBody,
    val positionsByPendingAnchor: Map<PendingCodeAnchor, LogicalCodePosition>,
)

/** A draft plus transaction-owned anchors resolved before the draft is constructed. */
internal data class MethodBodyDraftConstruction(
    val draft: MethodBodyDraft,
    val positionsByPendingAnchor: Map<PendingCodeAnchor, LogicalCodePosition>,
)
