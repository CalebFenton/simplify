package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.debug.EndLocal
import com.android.tools.smali.dexlib2.iface.debug.EpilogueBegin
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.debug.PrologueEnd
import com.android.tools.smali.dexlib2.iface.debug.RestartLocal
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31t
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableDebugItem
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEndLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEpilogueBegin
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableLineNumber
import com.android.tools.smali.dexlib2.immutable.debug.ImmutablePrologueEnd
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableRestartLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableSetSourceFile
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableStartLocal
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction20t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutablePackedSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSparseSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSwitchElement

/** Computes one complete DEX encoding from logical method positions and targets. */
internal object MethodLayoutSolver {
    fun solve(
        body: MethodBody,
        stringEncoding: StringInstructionEncoding? = null,
    ): MethodLayout {
        val records = body.instructions.filter { it.position !in body.syntheticPayloadPaddingPositions }
        val recordsByPosition = records.associateBy(EncodedInstruction::position)
        val branchStates = records.mapNotNull { record ->
            when (record.instruction.opcode) {
                in OpcodeGroups.BRANCH -> record.position to BranchState(record.instruction.opcode)
                else -> null
            }
        }.toMap().toMutableMap()
        val switchOwnersByPayload = LinkedHashMap<LogicalCodePosition, MutableList<LogicalCodePosition>>()
        records.filter { it.instruction.opcode in OpcodeGroups.SWITCH }.forEach { record ->
            val payload = requireNotNull(body.controlTargets[record.position]) {
                "Switch at ${record.codeAddress} has no payload target"
            }
            switchOwnersByPayload.getOrPut(payload, ::mutableListOf).add(record.position)
        }
        var groupsByPayload = switchOwnersByPayload.mapValues { (_, owners) ->
            listOf(owners.sortedBy(LogicalCodePosition::ordinal))
        }

        val gotoCount = branchStates.values.count { it.opcode in OpcodeGroups.GOTO }
        val conditionalCount = branchStates.values.count { it.opcode in OpcodeGroups.CONDITIONAL_BRANCH }
        val maximumRounds = 2 + (2 * gotoCount) + conditionalCount + switchOwnersByPayload.values.sumOf(List<*>::size)
        var assignment: AddressAssignment? = null
        var round = 0
        while (round++ < maximumRounds) {
            val current = assignAddresses(body, records, branchStates, groupsByPayload, stringEncoding)
            var changed = widenBranches(body, recordsByPosition, branchStates, current) > 0
            val regrouped = regroupSwitchPayloads(body, groupsByPayload, current)
            if (regrouped != groupsByPayload) {
                groupsByPayload = regrouped
                changed = true
            }
            if (!changed) {
                assignment = current
                break
            }
        }
        val finalAssignment = assignment ?: error("Method layout did not converge within its monotonic round bound")
        val instructions = emitInstructions(
            body,
            records,
            branchStates,
            groupsByPayload,
            finalAssignment,
            stringEncoding,
        )
        val instructionBoundaries = buildSet {
            instructions.forEach { add(it.codeAddress) }
            add(finalAssignment.codeUnits)
        }
        data class ProtectedRange(val start: LogicalCodePosition, val end: LogicalCodePosition)

        val handlersByRange = LinkedHashMap<ProtectedRange, MutableList<ImportedExceptionHandler>>()
        body.tryRegions.forEach { region ->
            if (region.start != region.end) {
                handlersByRange.getOrPut(ProtectedRange(region.start, region.end), ::mutableListOf)
                    .addAll(region.handlers)
            }
        }
        val tryBlocks = handlersByRange.entries
            .sortedWith(compareBy({ it.key.start.ordinal }, { it.key.end.ordinal }))
            .flatMap { (range, orderedHandlers) ->
                val start = finalAssignment.addressOf(range.start)
                val end = finalAssignment.addressOf(range.end)
                val handlers = orderedHandlers.map { handler ->
                    ImmutableExceptionHandler(handler.exceptionType, finalAssignment.addressOf(handler.target))
                }
                buildList {
                    var chunkStart = start
                    while (end - chunkStart > MAX_TRY_RANGE_CODE_UNITS) {
                        val maximumEnd = chunkStart + MAX_TRY_RANGE_CODE_UNITS
                        val chunkEnd = instructionBoundaries.asSequence()
                            .filter { it > chunkStart && it <= maximumEnd }
                            .maxOrNull()
                            ?: error("Protected range contains an instruction larger than the DEX range limit")
                        add(ImmutableTryBlock(chunkStart, chunkEnd - chunkStart, handlers))
                        chunkStart = chunkEnd
                    }
                    add(ImmutableTryBlock(chunkStart, end - chunkStart, handlers))
                }
            }
            .toPersistentList()
        val debugItems = body.debugItems.map { item ->
            relocateDebugItem(item.item, finalAssignment.addressOf(item.position))
        }.toPersistentList()
        return MethodLayout(
            body.registerCount,
            instructions.toPersistentList(),
            finalAssignment.addressesByPosition.toPersistentMap(),
            tryBlocks,
            debugItems,
        )
    }

    private data class BranchState(var opcode: Opcode, var expanded: Boolean = false)

    private data class PayloadGroupId(val payload: LogicalCodePosition, val index: Int)

    private data class AddressAssignment(
        val addressesByPosition: Map<LogicalCodePosition, Int>,
        val payloadAddressesByOwner: Map<LogicalCodePosition, Int>,
        val payloadAddressesByGroup: Map<PayloadGroupId, Int>,
        val codeUnits: Int,
    ) {
        fun addressOf(position: LogicalCodePosition): Int = requireNotNull(addressesByPosition[position]) {
            "Logical position has no layout address: $position"
        }
    }

    private fun assignAddresses(
        body: MethodBody,
        records: List<EncodedInstruction>,
        branchStates: Map<LogicalCodePosition, BranchState>,
        groupsByPayload: Map<LogicalCodePosition, List<List<LogicalCodePosition>>>,
        stringEncoding: StringInstructionEncoding?,
    ): AddressAssignment {
        val addresses = LinkedHashMap<LogicalCodePosition, Int>()
        val payloadAddressesByOwner = HashMap<LogicalCodePosition, Int>()
        val payloadAddressesByGroup = HashMap<PayloadGroupId, Int>()
        var address = 0
        for (record in records) {
            if (record.instruction.opcode in SWITCH_PAYLOAD_OPCODES) {
                val groups = groupsByPayload[record.position].orEmpty().ifEmpty { listOf(emptyList()) }
                groups.forEachIndexed { index, owners ->
                    if (address and 1 != 0) address++
                    addresses.putIfAbsent(record.position, address)
                    payloadAddressesByGroup[PayloadGroupId(record.position, index)] = address
                    owners.forEach { owner -> payloadAddressesByOwner[owner] = address }
                    address += record.instruction.codeUnits
                }
            } else {
                if (record.instruction.opcode in OpcodeGroups.PAYLOAD && address and 1 != 0) address++
                addresses[record.position] = address
                address += instructionWidth(record, branchStates[record.position], stringEncoding)
            }
        }
        val end = body.positionsByCodeAddress.maxByOrNull(Map.Entry<Int, LogicalCodePosition>::key)?.value
        if (end != null) addresses[end] = address
        return AddressAssignment(addresses, payloadAddressesByOwner, payloadAddressesByGroup, address)
    }

    private fun instructionWidth(
        record: EncodedInstruction,
        state: BranchState?,
        stringEncoding: StringInstructionEncoding?,
    ): Int = when {
        state?.expanded == true -> EXPANDED_CONDITIONAL_CODE_UNITS
        state?.opcode == Opcode.GOTO -> 1
        state?.opcode == Opcode.GOTO_16 -> 2
        state?.opcode == Opcode.GOTO_32 -> 3
        stringEncoding == StringInstructionEncoding.CONST_STRING_JUMBO &&
            record.instruction.opcode == Opcode.CONST_STRING -> 3
        else -> record.instruction.codeUnits
    }

    private fun widenBranches(
        body: MethodBody,
        recordsByPosition: Map<LogicalCodePosition, EncodedInstruction>,
        branchStates: MutableMap<LogicalCodePosition, BranchState>,
        assignment: AddressAssignment,
    ): Int {
        var widenings = 0
        for ((position, state) in branchStates) {
            val target = requireNotNull(body.controlTargets[position]) { "Branch has no logical target: $position" }
            val offset = assignment.addressOf(target) - assignment.addressOf(position)
            when {
                state.opcode == Opcode.GOTO && offset !in Byte.MIN_VALUE..Byte.MAX_VALUE -> {
                    state.opcode = Opcode.GOTO_16
                    widenings++
                }
                state.opcode == Opcode.GOTO_16 && offset !in Short.MIN_VALUE..Short.MAX_VALUE -> {
                    state.opcode = Opcode.GOTO_32
                    widenings++
                }
                state.opcode in OpcodeGroups.CONDITIONAL_BRANCH && !state.expanded &&
                    offset !in Short.MIN_VALUE..Short.MAX_VALUE -> {
                    val source = requireNotNull(recordsByPosition[position]).instruction
                    check(source is OneRegisterInstruction || source is TwoRegisterInstruction) {
                        "Unsupported conditional format: ${source.opcode.format}"
                    }
                    state.expanded = true
                    widenings++
                }
            }
        }
        return widenings
    }

    private fun regroupSwitchPayloads(
        body: MethodBody,
        groupsByPayload: Map<LogicalCodePosition, List<List<LogicalCodePosition>>>,
        assignment: AddressAssignment,
    ): Map<LogicalCodePosition, List<List<LogicalCodePosition>>> = groupsByPayload.mapValues { (_, groups) ->
        groups.flatMap { owners ->
            owners.groupBy { owner ->
                val ownerAddress = assignment.addressOf(owner)
                body.switchCases[owner].orEmpty().map { case ->
                    case.key to (assignment.addressOf(case.target) - ownerAddress)
                }
            }.values.map { group -> group.sortedBy(LogicalCodePosition::ordinal) }
        }.sortedBy { group -> group.first().ordinal }
    }

    private fun emitInstructions(
        body: MethodBody,
        records: List<EncodedInstruction>,
        branchStates: Map<LogicalCodePosition, BranchState>,
        groupsByPayload: Map<LogicalCodePosition, List<List<LogicalCodePosition>>>,
        assignment: AddressAssignment,
        stringEncoding: StringInstructionEncoding?,
    ): List<LaidOutInstruction> {
        val result = ArrayList<LaidOutInstruction>(records.size)
        var address = 0
        for (record in records) {
            if (record.instruction.opcode in SWITCH_PAYLOAD_OPCODES) {
                val groups = groupsByPayload[record.position].orEmpty().ifEmpty { listOf(emptyList()) }
                groups.forEachIndexed { index, owners ->
                    if (address and 1 != 0) {
                        result += LaidOutInstruction(null, address, ImmutableInstruction10x(Opcode.NOP))
                        address++
                    }
                    check(address == assignment.payloadAddressesByGroup[PayloadGroupId(record.position, index)])
                    val payload = if (owners.isEmpty()) {
                        record.instruction
                    } else {
                        val owner = owners.first()
                        val ownerAddress = assignment.addressOf(owner)
                        val elements = body.switchCases[owner].orEmpty().map { case ->
                            ImmutableSwitchElement(case.key, assignment.addressOf(case.target) - ownerAddress)
                        }
                        when (record.instruction.opcode) {
                            Opcode.PACKED_SWITCH_PAYLOAD -> ImmutablePackedSwitchPayload(elements)
                            Opcode.SPARSE_SWITCH_PAYLOAD -> ImmutableSparseSwitchPayload(elements)
                            else -> error("Unsupported switch payload ${record.instruction.opcode}")
                        }
                    }
                    result += LaidOutInstruction(if (index == 0) record.position else null, address, payload)
                    address += payload.codeUnits
                }
                continue
            }
            if (record.instruction.opcode in OpcodeGroups.PAYLOAD && address and 1 != 0) {
                result += LaidOutInstruction(null, address, ImmutableInstruction10x(Opcode.NOP))
                address++
            }
            check(address == assignment.addressOf(record.position))
            val emitted = emitOrdinaryInstruction(body, record, branchStates[record.position], assignment, stringEncoding)
            emitted.forEachIndexed { index, instruction ->
                result += LaidOutInstruction(
                    if (index == 0) record.position else null,
                    address,
                    instruction,
                    if (index == 0) body.referenceOperandsByPosition[record.position].orEmpty() else emptyList(),
                )
                address += instruction.codeUnits
            }
        }
        check(address == assignment.codeUnits) { "Emitted method size differs from solved layout" }
        return result
    }

    private fun emitOrdinaryInstruction(
        body: MethodBody,
        record: EncodedInstruction,
        state: BranchState?,
        assignment: AddressAssignment,
        stringEncoding: StringInstructionEncoding?,
    ): List<Instruction> {
        val source = record.instruction
        if (stringEncoding == StringInstructionEncoding.CONST_STRING_JUMBO && source.opcode == Opcode.CONST_STRING) {
            return listOf(
                ImmutableInstruction31c(
                    Opcode.CONST_STRING_JUMBO,
                    (source as OneRegisterInstruction).registerA,
                    (source as ReferenceInstruction).reference,
                ),
            )
        }
        if (source.opcode in OpcodeGroups.SWITCH) {
            val target = requireNotNull(assignment.payloadAddressesByOwner[record.position])
            return listOf(
                ImmutableInstruction31t(
                    source.opcode,
                    (source as Instruction31t).registerA,
                    target - assignment.addressOf(record.position),
                ),
            )
        }
        if (source.opcode == Opcode.FILL_ARRAY_DATA) {
            val target = requireNotNull(body.controlTargets[record.position])
            return listOf(
                ImmutableInstruction31t(
                    source.opcode,
                    (source as Instruction31t).registerA,
                    assignment.addressOf(target) - assignment.addressOf(record.position),
                ),
            )
        }
        val target = body.controlTargets[record.position]
        if (state == null || target == null) return listOf(source)
        val sourceAddress = assignment.addressOf(record.position)
        val targetAddress = assignment.addressOf(target)
        if (state.expanded) {
            val inverse = requireNotNull(INVERSE_CONDITIONALS[state.opcode])
            val conditional = when (source) {
                is TwoRegisterInstruction -> ImmutableInstruction22t(
                    inverse,
                    source.registerA,
                    source.registerB,
                    EXPANDED_CONDITIONAL_CODE_UNITS,
                )
                is OneRegisterInstruction -> ImmutableInstruction21t(
                    inverse,
                    source.registerA,
                    EXPANDED_CONDITIONAL_CODE_UNITS,
                )
                else -> error("Unsupported conditional format: ${source.opcode.format}")
            }
            return listOf(conditional, ImmutableInstruction30t(Opcode.GOTO_32, targetAddress - (sourceAddress + 2)))
        }
        val offset = targetAddress - sourceAddress
        return listOf(
            when (state.opcode) {
                Opcode.GOTO -> ImmutableInstruction10t(Opcode.GOTO, offset)
                Opcode.GOTO_16 -> ImmutableInstruction20t(Opcode.GOTO_16, offset)
                Opcode.GOTO_32 -> ImmutableInstruction30t(Opcode.GOTO_32, offset)
                in OpcodeGroups.CONDITIONAL_BRANCH -> when (source) {
                    is TwoRegisterInstruction -> ImmutableInstruction22t(
                        state.opcode,
                        source.registerA,
                        source.registerB,
                        offset,
                    )
                    is OneRegisterInstruction -> ImmutableInstruction21t(state.opcode, source.registerA, offset)
                    else -> error("Unsupported conditional format: ${source.opcode.format}")
                }
                else -> source
            },
        )
    }

    private fun relocateDebugItem(item: com.android.tools.smali.dexlib2.iface.debug.DebugItem, address: Int): ImmutableDebugItem =
        when (item) {
            is LineNumber -> ImmutableLineNumber(address, item.lineNumber)
            is StartLocal -> ImmutableStartLocal(address, item.register, item.name, item.type, item.signature)
            is EndLocal -> ImmutableEndLocal(address, item.register, item.name, item.type, item.signature)
            is RestartLocal -> ImmutableRestartLocal(address, item.register, item.name, item.type, item.signature)
            is PrologueEnd -> ImmutablePrologueEnd(address)
            is EpilogueBegin -> ImmutableEpilogueBegin(address)
            is SetSourceFile -> ImmutableSetSourceFile(address, item.sourceFile)
            else -> error("Unsupported debug item type: ${item.javaClass.name}")
        }

    private val SWITCH_PAYLOAD_OPCODES = OpcodeGroups.PAYLOAD - Opcode.ARRAY_PAYLOAD
    private val INVERSE_CONDITIONALS = mapOf(
        Opcode.IF_EQ to Opcode.IF_NE,
        Opcode.IF_NE to Opcode.IF_EQ,
        Opcode.IF_LT to Opcode.IF_GE,
        Opcode.IF_GE to Opcode.IF_LT,
        Opcode.IF_GT to Opcode.IF_LE,
        Opcode.IF_LE to Opcode.IF_GT,
        Opcode.IF_EQZ to Opcode.IF_NEZ,
        Opcode.IF_NEZ to Opcode.IF_EQZ,
        Opcode.IF_LTZ to Opcode.IF_GEZ,
        Opcode.IF_GEZ to Opcode.IF_LTZ,
        Opcode.IF_GTZ to Opcode.IF_LEZ,
        Opcode.IF_LEZ to Opcode.IF_GTZ,
    )
    private const val EXPANDED_CONDITIONAL_CODE_UNITS = 5
    private const val MAX_TRY_RANGE_CODE_UNITS = 65_535
}
