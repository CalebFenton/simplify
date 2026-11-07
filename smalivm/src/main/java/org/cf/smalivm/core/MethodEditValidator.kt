package org.cf.smalivm.core

import java.lang.reflect.Modifier
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.debug.EndLocal
import com.android.tools.smali.dexlib2.iface.debug.EpilogueBegin
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.debug.PrologueEnd
import com.android.tools.smali.dexlib2.iface.debug.RestartLocal
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.VariableRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/** Expected structural validation for one edited method candidate. */
internal object MethodEditValidator {
    fun validateInheritedMetadata(body: MethodBody): MethodEditRejectionReason? =
        validateLogicalBody(body, inherited = true)

    fun validateLogicalBody(body: MethodBody): MethodEditRejectionReason? =
        validateLogicalBody(body, inherited = false)

    private fun validateLogicalBody(
        body: MethodBody,
        inherited: Boolean,
    ): MethodEditRejectionReason? {
        val recordsByPosition = body.instructions.associateBy(EncodedInstruction::position)
        val endPosition = body.positionsByCodeAddress.maxByOrNull(Map.Entry<Int, LogicalCodePosition>::key)?.value
            ?: return MethodEditRejectionReason.StructuralValidationFailure("The method has no end position")
        val payloadPositions = recordsByPosition.filterValues {
            it.instruction.opcode in OpcodeGroups.PAYLOAD
        }.keys
        val payloadOwners = HashMap<LogicalCodePosition, MutableList<EncodedInstruction>>()

        for (record in body.instructions) {
            val instruction = record.instruction
            val target = body.controlTargets[record.position]
            if (instruction is OffsetInstruction && target == null) {
                return MethodEditRejectionReason.InvalidControlTarget(
                    "${instruction.opcode} at ${record.codeAddress} has no logical target",
                )
            }
            if (target == null) continue
            val targetRecord = recordsByPosition[target]
            when (instruction.opcode) {
                in OpcodeGroups.BRANCH -> if (targetRecord == null || target in payloadPositions) {
                    return MethodEditRejectionReason.InvalidControlTarget(
                        "${instruction.opcode} at ${record.codeAddress} does not target executable code",
                    )
                }
                Opcode.FILL_ARRAY_DATA -> if (targetRecord?.instruction !is ArrayPayload) {
                    return MethodEditRejectionReason.InvalidControlTarget(
                        "fill-array-data at ${record.codeAddress} does not target an array payload",
                    )
                }
                in OpcodeGroups.SWITCH -> {
                    val expectedPayload = if (instruction.opcode == Opcode.PACKED_SWITCH) {
                        Opcode.PACKED_SWITCH_PAYLOAD
                    } else {
                        Opcode.SPARSE_SWITCH_PAYLOAD
                    }
                    if (targetRecord?.instruction?.opcode != expectedPayload) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} targets the wrong payload kind",
                        )
                    }
                    val cases = body.switchCases[record.position]
                        ?: return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} has no owner-specific cases",
                        )
                    val payloadKeys = (targetRecord.instruction as SwitchPayload).switchElements.map { it.key }
                    if (cases.map(ImportedSwitchCase::key) != payloadKeys) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} has cases that differ from its payload keys",
                        )
                    }
                    for (case in cases) {
                        if (case.target == endPosition || case.target in payloadPositions || case.target !in recordsByPosition) {
                            return MethodEditRejectionReason.InvalidControlTarget(
                                "${instruction.opcode} case ${case.key} does not target executable code",
                            )
                        }
                    }
                }
                else -> if (targetRecord == null || target in payloadPositions) {
                    return MethodEditRejectionReason.InvalidControlTarget(
                        "${instruction.opcode} at ${record.codeAddress} has an invalid logical target",
                    )
                }
            }
            if (instruction.opcode in OpcodeGroups.PAYLOAD_OWNER) {
                payloadOwners.getOrPut(target, ::mutableListOf).add(record)
            }
        }

        for (payloadPosition in payloadPositions) {
            if (payloadOwners[payloadPosition].isNullOrEmpty()) {
                return MethodEditRejectionReason.InvalidControlTarget("Payload data has no owning instruction")
            }
            val payload = requireNotNull(recordsByPosition[payloadPosition]).instruction
            validatePayloadKeys(payload)?.let { return it }
        }

        val paddingFreeRecords = body.instructions.filter { it.position !in body.syntheticPayloadPaddingPositions }
        if (paddingFreeRecords.firstOrNull()?.position in payloadPositions) {
            return MethodEditRejectionReason.InvalidControlTarget("Method entry cannot contain payload data")
        }
        paddingFreeRecords.zipWithNext().forEach { (previous, next) ->
            if (next.position in payloadPositions && previous.instruction.opcode.canContinue()) {
                return MethodEditRejectionReason.InvalidControlTarget(
                    "Ordinary control flow falls into payload data at ${next.codeAddress}",
                )
            }
            if (next.instruction.opcode == Opcode.MOVE_EXCEPTION && previous.instruction.opcode.canContinue()) {
                return MethodEditRejectionReason.InvalidControlTarget(
                    "Ordinary control flow enters move-exception at ${next.codeAddress}",
                )
            }
        }
        val moveExceptionPositions = body.instructions.filter {
            it.instruction.opcode == Opcode.MOVE_EXCEPTION
        }.mapTo(hashSetOf(), EncodedInstruction::position)
        if (body.controlTargets.values.any { it in moveExceptionPositions } ||
            body.switchCases.values.flatten().any { it.target in moveExceptionPositions }
        ) {
            return MethodEditRejectionReason.InvalidControlTarget("Ordinary branch enters move-exception")
        }

        validateTryRegions(body, recordsByPosition, payloadPositions, inherited)?.let { return it }
        for (item in body.debugItems) {
            if (item.position != endPosition && (item.position !in recordsByPosition || item.position in payloadPositions)) {
                return MethodEditRejectionReason.StructuralValidationFailure(
                    "Debug metadata does not target an executable boundary",
                )
            }
            val register = debugRegister(item.item)
            if (register != null && register !in 0 until body.registerCount) {
                return MethodEditRejectionReason.RegisterOverflow(register)
            }
            if (item.item !is LineNumber && item.item !is StartLocal && item.item !is EndLocal &&
                item.item !is RestartLocal && item.item !is PrologueEnd && item.item !is EpilogueBegin &&
                item.item !is SetSourceFile
            ) {
                return MethodEditRejectionReason.StructuralValidationFailure(
                    "Unsupported debug item type: ${item.item.javaClass.name}",
                )
            }
        }
        return null
    }

    fun validateLayout(
        layout: MethodLayout,
        snapshot: ProgramSnapshot,
        projectedReferences: DexReferenceClosure = snapshot.dexReferenceClosure,
    ): MethodEditRejectionReason? {
        if (layout.registerCount > MAX_REGISTER_COUNT) {
            return MethodEditRejectionReason.RegisterOverflow(layout.registerCount - 1)
        }
        val opcodes = snapshot.outputApiPolicy.opcodes()
        var codeUnits = 0L
        for (record in layout.instructions) {
            val instruction = record.instruction
            if (opcodes.getOpcodeValue(instruction.opcode) == null) {
                return MethodEditRejectionReason.StructuralValidationFailure(
                    "Opcode ${instruction.opcode.name} is unavailable at output API ${snapshot.outputApiPolicy.apiLevel}",
                )
            }
            invalidRegister(instruction, layout.registerCount)?.let {
                return MethodEditRejectionReason.RegisterOverflow(it)
            }
            invalidWideRegister(instruction, layout.registerCount)?.let {
                return MethodEditRejectionReason.RegisterOverflow(it)
            }
            codeUnits += instruction.codeUnits
            if (codeUnits > Int.MAX_VALUE) return MethodEditRejectionReason.CodeSizeOverflow(codeUnits)
        }
        val recordsByAddress = layout.instructions.associateBy(LaidOutInstruction::codeAddress)
        val payloadAddresses = layout.instructions.filter {
            it.instruction.opcode in OpcodeGroups.PAYLOAD
        }.mapTo(hashSetOf(), LaidOutInstruction::codeAddress)
        val payloadOwners = HashMap<Int, MutableList<LaidOutInstruction>>()
        for (record in layout.instructions) {
            val instruction = record.instruction
            if (instruction.opcode in OpcodeGroups.PAYLOAD && record.codeAddress and 1 != 0) {
                return MethodEditRejectionReason.StructuralValidationFailure(
                    "Payload at ${record.codeAddress} is not aligned to an even code-unit address",
                )
            }
            if (instruction !is OffsetInstruction) continue
            val targetAddress = record.codeAddress + instruction.codeOffset
            val target = recordsByAddress[targetAddress]
                ?: return MethodEditRejectionReason.InvalidControlTarget(
                    "${instruction.opcode} at ${record.codeAddress} does not target an instruction boundary",
                )
            when (instruction.opcode) {
                in OpcodeGroups.BRANCH -> {
                    if (instruction.codeOffset == 0 && instruction.opcode != Opcode.GOTO_32) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} has an invalid zero offset",
                        )
                    }
                    if (targetAddress in payloadAddresses) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} targets payload data",
                        )
                    }
                    if (target.instruction.opcode == Opcode.MOVE_EXCEPTION) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} targets move-exception",
                        )
                    }
                }
                Opcode.FILL_ARRAY_DATA -> if (target.instruction !is ArrayPayload) {
                    return MethodEditRejectionReason.InvalidControlTarget(
                        "fill-array-data at ${record.codeAddress} does not target an array payload",
                    )
                }
                in OpcodeGroups.SWITCH -> {
                    val expected = if (instruction.opcode == Opcode.PACKED_SWITCH) {
                        Opcode.PACKED_SWITCH_PAYLOAD
                    } else {
                        Opcode.SPARSE_SWITCH_PAYLOAD
                    }
                    if (target.instruction.opcode != expected) {
                        return MethodEditRejectionReason.InvalidControlTarget(
                            "${instruction.opcode} at ${record.codeAddress} targets the wrong payload kind",
                        )
                    }
                    val payload = target.instruction as SwitchPayload
                    for (element in payload.switchElements) {
                        val caseAddress = record.codeAddress + element.offset
                        if (caseAddress !in recordsByAddress || caseAddress in payloadAddresses) {
                            return MethodEditRejectionReason.InvalidControlTarget(
                                "${instruction.opcode} case ${element.key} does not target executable code",
                            )
                        }
                        if (recordsByAddress[caseAddress]?.instruction?.opcode == Opcode.MOVE_EXCEPTION) {
                            return MethodEditRejectionReason.InvalidControlTarget(
                                "${instruction.opcode} case ${element.key} targets move-exception",
                            )
                        }
                    }
                }
                else -> Unit
            }
            if (instruction.opcode in OpcodeGroups.PAYLOAD_OWNER) {
                payloadOwners.getOrPut(targetAddress, ::mutableListOf).add(record)
            }
        }
        if (payloadAddresses.any { payloadOwners[it].isNullOrEmpty() }) {
            return MethodEditRejectionReason.InvalidControlTarget("Payload data has no owning instruction")
        }
        val semanticRecords = layout.instructions.filterNot {
            it.position == null && it.instruction.opcode == Opcode.NOP
        }
        semanticRecords.zipWithNext().forEach { (previous, next) ->
            if (next.codeAddress in payloadAddresses && previous.instruction.opcode.canContinue()) {
                return MethodEditRejectionReason.InvalidControlTarget(
                    "Ordinary control flow falls into payload data at ${next.codeAddress}",
                )
            }
            if (next.instruction.opcode == Opcode.MOVE_EXCEPTION && previous.instruction.opcode.canContinue()) {
                return MethodEditRejectionReason.InvalidControlTarget(
                    "Ordinary control flow enters move-exception at ${next.codeAddress}",
                )
            }
        }
        val executableAddresses = recordsByAddress.keys - payloadAddresses
        for (tryBlock in layout.tryBlocks) {
            if (tryBlock.codeUnitCount <= 0) {
                return MethodEditRejectionReason.InvalidTryRegion("A protected range is empty or reversed")
            }
            if (tryBlock.startCodeAddress !in executableAddresses) {
                return MethodEditRejectionReason.InvalidTryRegion("A protected range starts outside executable code")
            }
            for (handler in tryBlock.exceptionHandlers) {
                if (handler.handlerCodeAddress !in executableAddresses) {
                    return MethodEditRejectionReason.InvalidTryRegion(
                        "An exception handler does not target executable code",
                    )
                }
            }
            validateCatchAllOrder(tryBlock.exceptionHandlers.map { it.exceptionType })?.let { return it }
        }
        for (item in layout.debugItems) {
            if (item.codeAddress !in executableAddresses && item.codeAddress != codeUnits.toInt()) {
                return MethodEditRejectionReason.StructuralValidationFailure(
                    "Debug metadata does not target an executable boundary",
                )
            }
            val register = debugRegister(item)
            if (register != null && register !in 0 until layout.registerCount) {
                return MethodEditRejectionReason.RegisterOverflow(register)
            }
        }
        validateEncodedTryStructure(layout, projectedReferences)?.let { return it }
        return null
    }

    fun validateApplicationMethodDefinitionCandidate(
        candidate: ApplicationMethodDefinitionCandidate,
        snapshot: ProgramSnapshot,
        stagedReferences: DexReferenceClosure = DexReferenceClosure.EMPTY,
    ): MethodEditRejectionReason? {
        val body = candidate.body
        if (body.registerCount < candidate.baselineRegisterCount) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "An edit cannot reduce the method register count",
            )
        }
        if (body.registerCount > MAX_REGISTER_COUNT) {
            return MethodEditRejectionReason.RegisterOverflow(body.registerCount - 1)
        }
        val executableInstructions = body.instructions.filter {
            it.instruction.opcode !in OpcodeGroups.PAYLOAD &&
                it.position !in body.syntheticPayloadPaddingPositions
        }
        if (executableInstructions.isEmpty()) {
            return MethodEditRejectionReason.StructuralValidationFailure("A concrete method needs executable code")
        }
        if (executableInstructions.last().instruction.opcode.canContinue()) {
            return MethodEditRejectionReason.StructuralValidationFailure("Control flow falls past the method end")
        }
        val handlerAddresses = body.implementation.tryBlocks.flatMap { block ->
            block.exceptionHandlers.map { it.handlerCodeAddress }
        }.toSet()
        body.instructions.forEachIndexed { index, record ->
            if (record.instruction.opcode in MOVE_RESULT_OPCODES) {
                validateMoveResult(body.instructions.getOrNull(index - 1)?.instruction, record.instruction.opcode)
                    ?.let { return it }
            }
            if (record.instruction.opcode == Opcode.MOVE_EXCEPTION) {
                if (record.codeAddress !in handlerAddresses) {
                    return MethodEditRejectionReason.InvalidTryRegion(
                        "move-exception is not the first instruction of an exception handler",
                    )
                }
            }
            validateInvoke(record.instruction)?.let { return it }
            validateReturnOpcode(candidate.declaration.returnType, record.instruction.opcode)?.let { return it }
        }
        validateRegisterFlow(candidate)?.let { return it }
        val allowedReferences = snapshot.dexReferenceClosure.plus(stagedReferences)
        val candidateReferences = candidate.dexReferenceClosure
        if (!allowedReferences.containsAll(candidateReferences)) {
            return MethodEditRejectionReason.InvalidInstructionTemplate(
                "This edit contains a reference that was not staged and is not reachable in the current snapshot",
            )
        }
        return null
    }

    fun validateStagedReference(reference: StagedDexReference): MethodEditRejectionReason? = when (reference) {
        is StagedDexReference.Type -> if (isValidReturnTypeDescriptor(reference.descriptor)) {
            null
        } else {
            MethodEditRejectionReason.InvalidDexReference("A staged type has a malformed descriptor")
        }
        is StagedDexReference.String -> null
        is StagedDexReference.Field -> when {
            !isValidClassDescriptor(reference.definingClass) -> invalidReference("field owner")
            !isValidMemberName(reference.name) -> invalidReference("field name")
            !isValidNonVoidTypeDescriptor(reference.type) -> invalidReference("field type")
            else -> null
        }
        is StagedDexReference.Method -> when {
            !isValidReferenceTypeDescriptor(reference.definingClass) -> invalidReference("method owner")
            !isValidMethodName(reference.name) -> invalidReference("method name")
            reference.parameterTypes.any { !isValidNonVoidTypeDescriptor(it) } ->
                invalidReference("method parameter type")
            !isValidReturnTypeDescriptor(reference.returnType) -> invalidReference("method return type")
            else -> null
        }
        is StagedDexReference.Prototype -> when {
            reference.parameterTypes.any { !isValidNonVoidTypeDescriptor(it) } ->
                invalidReference("prototype parameter type")
            !isValidReturnTypeDescriptor(reference.returnType) -> invalidReference("prototype return type")
            else -> null
        }
    }

    fun validateCatchTypeDescriptor(descriptor: String): MethodEditRejectionReason? {
        if (!isValidClassDescriptor(descriptor)) {
            return MethodEditRejectionReason.InvalidTryRegion("A typed catch requires a class descriptor")
        }
        return null
    }

    fun validateTypeOperand(opcode: Opcode, descriptor: String): MethodEditRejectionReason? {
        val valid = when (opcode) {
            Opcode.NEW_INSTANCE -> isValidClassDescriptor(descriptor)
            Opcode.NEW_ARRAY, Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE ->
                descriptor.startsWith('[') && isValidNonVoidTypeDescriptor(descriptor)
            Opcode.CHECK_CAST, Opcode.INSTANCE_OF -> isValidReferenceTypeDescriptor(descriptor)
            Opcode.CONST_CLASS -> isValidReturnTypeDescriptor(descriptor)
            else -> true
        }
        return if (valid) null else invalidReference("type operand for ${opcode.name.lowercase()}")
    }

    private fun invalidReference(part: String): MethodEditRejectionReason =
        MethodEditRejectionReason.InvalidDexReference("A staged $part is malformed")

    private fun isValidReturnTypeDescriptor(descriptor: String): Boolean =
        descriptor == "V" || isValidNonVoidTypeDescriptor(descriptor)

    private fun isValidNonVoidTypeDescriptor(descriptor: String): Boolean {
        if (descriptor.length == 1) return descriptor[0] in NON_VOID_PRIMITIVE_TYPES
        if (descriptor.startsWith('L')) return isValidClassDescriptor(descriptor)
        if (!descriptor.startsWith('[')) return false
        val dimensions = descriptor.indexOfFirst { it != '[' }.let { if (it < 0) descriptor.length else it }
        if (dimensions !in 1..MAX_ARRAY_DIMENSIONS) return false
        val component = descriptor.substring(dimensions)
        return (component.length == 1 && component[0] in NON_VOID_PRIMITIVE_TYPES) ||
            isValidClassDescriptor(component)
    }

    private fun isValidReferenceTypeDescriptor(descriptor: String): Boolean =
        isValidClassDescriptor(descriptor) ||
            (descriptor.startsWith('[') && isValidNonVoidTypeDescriptor(descriptor))

    private fun isValidClassDescriptor(descriptor: String): Boolean {
        if (descriptor.length < 3 || descriptor.firstOrNull() != 'L' || descriptor.lastOrNull() != ';') return false
        val name = descriptor.substring(1, descriptor.lastIndex)
        return name.isNotEmpty() && name.split('/').all { it.isNotEmpty() && isValidSimpleName(it) }
    }

    private fun isValidMethodName(name: String): Boolean = isValidMemberName(name)

    private fun isValidMemberName(name: String): Boolean = when {
        name.isEmpty() -> false
        isValidSimpleName(name) -> true
        name.length > 2 && name.first() == '<' && name.last() == '>' ->
            isValidSimpleName(name.substring(1, name.lastIndex))
        else -> false
    }

    private fun isValidSimpleName(name: String): Boolean = name.codePoints().allMatch { codePoint ->
        codePoint in 'A'.code..'Z'.code ||
            codePoint in 'a'.code..'z'.code ||
            codePoint in '0'.code..'9'.code ||
            codePoint == '$'.code || codePoint == '-'.code || codePoint == '_'.code ||
            codePoint in 0x00a1..0x1fff ||
            codePoint in 0x2010..0x2027 ||
            codePoint in 0x2030..0xd7ff ||
            codePoint in 0xe000..0xffef ||
            codePoint in 0x10000..0x10ffff
    }

    private data class EncodedHandlerClause(val exceptionType: String?, val handlerCodeAddress: Int)

    private fun validateEncodedTryStructure(
        layout: MethodLayout,
        projectedReferences: DexReferenceClosure,
    ): MethodEditRejectionReason? {
        if (layout.tryBlocks.size > MAX_TRY_ITEMS) {
            return MethodEditRejectionReason.InvalidTryRegion("The method exceeds the DEX try-item limit")
        }
        if (layout.tryBlocks.isEmpty()) return null

        val typeIndexes = projectedReferences.types.sorted().withIndex().associate { (index, type) -> type to index }
        val handlerLists = LinkedHashSet<List<EncodedHandlerClause>>()
        layout.tryBlocks.forEach { block ->
            handlerLists += block.exceptionHandlers.map { handler ->
                EncodedHandlerClause(handler.exceptionType, handler.handlerCodeAddress)
            }
        }

        var offset = unsignedLeb128Size(handlerLists.size).toLong()
        for (handlers in handlerLists) {
            if (offset > MAX_HANDLER_OFFSET) {
                return MethodEditRejectionReason.InvalidTryRegion(
                    "An encoded exception-handler offset exceeds the DEX unsigned 16-bit limit",
                )
            }
            val typedCount = handlers.count { it.exceptionType != null }
            val hasCatchAll = handlers.any { it.exceptionType == null }
            val encodedCount = if (hasCatchAll) -typedCount else typedCount
            offset += signedLeb128Size(encodedCount)
            handlers.forEach { handler ->
                val exceptionType = handler.exceptionType
                if (exceptionType == null) {
                    offset += unsignedLeb128Size(handler.handlerCodeAddress)
                } else {
                    val typeIndex = typeIndexes[exceptionType]
                        ?: return MethodEditRejectionReason.InvalidTryRegion(
                            "An exception handler type is absent from the projected DEX closure",
                        )
                    offset += unsignedLeb128Size(typeIndex)
                    offset += unsignedLeb128Size(handler.handlerCodeAddress)
                }
            }
        }
        return null
    }

    private fun unsignedLeb128Size(value: Int): Int {
        require(value >= 0) { "ULEB128 values cannot be negative" }
        var remaining = value
        var size = 0
        do {
            size++
            remaining = remaining ushr 7
        } while (remaining != 0)
        return size
    }

    private fun signedLeb128Size(value: Int): Int {
        var remaining = value
        var size = 0
        var hasMore: Boolean
        do {
            val byte = remaining and 0x7f
            remaining = remaining shr 7
            size++
            hasMore = !((remaining == 0 && byte and 0x40 == 0) || (remaining == -1 && byte and 0x40 != 0))
        } while (hasMore)
        return size
    }

    private fun validateTryRegions(
        body: MethodBody,
        recordsByPosition: Map<LogicalCodePosition, EncodedInstruction>,
        payloadPositions: Set<LogicalCodePosition>,
        inherited: Boolean,
    ): MethodEditRejectionReason? {
        data class Range(val start: Int, val end: Int)

        val ranges = ArrayList<Range>()
        val handlerTypesByRange = LinkedHashMap<Range, MutableList<String?>>()
        for (region in body.tryRegions) {
            val start = region.start.ordinal
            val end = region.end.ordinal
            val range = Range(start, end)
            if (start > end) {
                return MethodEditRejectionReason.InvalidTryRegion("A protected range is reversed")
            }
            if (start < end && (region.start !in recordsByPosition || region.start in payloadPositions)) {
                return MethodEditRejectionReason.InvalidTryRegion("A protected range starts outside executable code")
            }
            if (region.handlers.isEmpty()) {
                return MethodEditRejectionReason.InvalidTryRegion("A protected range has no exception handler")
            }
            validateCatchAllOrder(region.handlers.map(ImportedExceptionHandler::exceptionType))?.let { return it }
            region.handlers.mapTo(handlerTypesByRange.getOrPut(range, ::mutableListOf)) { it.exceptionType }
            for (handler in region.handlers) {
                val target = recordsByPosition[handler.target]
                if (target == null || handler.target in payloadPositions) {
                    return MethodEditRejectionReason.InvalidTryRegion(
                        "An exception handler does not target executable code",
                    )
                }
            }
            if (start == end) {
                if (inherited) continue
                return MethodEditRejectionReason.InvalidTryRegion("A protected range is empty")
            }
            ranges += range
        }
        handlerTypesByRange.values.forEach { exceptionTypes ->
            validateCatchAllOrder(exceptionTypes)?.let { return it }
        }
        val distinctRanges = ranges.distinct().sortedWith(compareBy(Range::start, Range::end))
        distinctRanges.zipWithNext().forEach { (previous, next) ->
            if (next.start < previous.end) {
                return MethodEditRejectionReason.InvalidTryRegion("Distinct protected ranges overlap")
            }
        }
        return null
    }

    private fun validateCatchAllOrder(exceptionTypes: List<String?>): MethodEditRejectionReason? {
        val catchAllIndex = exceptionTypes.indexOfFirst { it == null }
        if (catchAllIndex >= 0 && (catchAllIndex != exceptionTypes.lastIndex || exceptionTypes.count { it == null } != 1)) {
            return MethodEditRejectionReason.InvalidTryRegion("A catch-all handler must be unique and last")
        }
        return null
    }

    private fun validatePayloadKeys(payload: Instruction): MethodEditRejectionReason? {
        if (payload is ArrayPayload && payload.elementWidth !in setOf(1, 2, 4, 8)) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "Array payload element width ${payload.elementWidth} is invalid",
            )
        }
        if (payload !is SwitchPayload) return null
        if (payload.switchElements.size > MAX_SWITCH_ELEMENTS) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "A switch payload cannot contain more than $MAX_SWITCH_ELEMENTS elements",
            )
        }
        val keys = payload.switchElements.map { it.key }
        when (payload.opcode) {
            Opcode.PACKED_SWITCH_PAYLOAD -> keys.zipWithNext().forEach { (previous, next) ->
                if (next.toLong() != previous.toLong() + 1L) {
                    return MethodEditRejectionReason.StructuralValidationFailure(
                        "Packed-switch keys are not consecutive",
                    )
                }
            }
            Opcode.SPARSE_SWITCH_PAYLOAD -> keys.zipWithNext().forEach { (previous, next) ->
                if (next <= previous) {
                    return MethodEditRejectionReason.StructuralValidationFailure(
                        "Sparse-switch keys are not unique and sorted",
                    )
                }
            }
            else -> Unit
        }
        return null
    }

    private fun debugRegister(item: com.android.tools.smali.dexlib2.iface.debug.DebugItem): Int? = when (item) {
        is StartLocal -> item.register
        is EndLocal -> item.register
        is RestartLocal -> item.register
        else -> null
    }

    private enum class RegisterKind { UNDEFINED, UNKNOWN, NARROW, ZERO, REFERENCE, WIDE_LOW, WIDE_HIGH }

    private fun validateRegisterFlow(
        candidate: ApplicationMethodDefinitionCandidate,
    ): MethodEditRejectionReason? {
        val body = candidate.body
        val executableRecords = body.instructions.filter { it.instruction.opcode !in OpcodeGroups.PAYLOAD }
        if (executableRecords.isEmpty()) return null
        val recordsByPosition = executableRecords.associateBy(EncodedInstruction::position)
        val nextByPosition = executableRecords.zipWithNext().associate { (current, next) -> current.position to next.position }
        val exceptionTargetsByPosition = exceptionTargetsByPosition(body, executableRecords)
        val entryRegisters = MutableList(body.registerCount) { RegisterKind.UNDEFINED }
        val parameterTypes = candidate.declaration.parameterTypes.map(CharSequence::toString)
        val parameterWords = parameterTypes.sumOf { if (isWideType(it)) 2 else 1 } +
            if (Modifier.isStatic(candidate.declaration.accessFlags)) 0 else 1
        var parameterRegister = body.registerCount - parameterWords
        if (!Modifier.isStatic(candidate.declaration.accessFlags)) {
            entryRegisters[parameterRegister++] = RegisterKind.REFERENCE
        }
        parameterTypes.forEach { type ->
            if (isWideType(type)) {
                entryRegisters[parameterRegister] = RegisterKind.WIDE_LOW
                entryRegisters[parameterRegister + 1] = RegisterKind.WIDE_HIGH
                parameterRegister += 2
            } else {
                entryRegisters[parameterRegister++] = if (type.startsWith('L') || type.startsWith('[')) {
                    RegisterKind.REFERENCE
                } else {
                    RegisterKind.NARROW
                }
            }
        }

        val incoming = HashMap<LogicalCodePosition, PersistentList<RegisterKind>>()
        val pending = ArrayDeque<LogicalCodePosition>()
        incoming[executableRecords.first().position] = entryRegisters.toPersistentList()
        pending.addLast(executableRecords.first().position)
        while (pending.isNotEmpty()) {
            val position = pending.removeFirst()
            val record = requireNotNull(recordsByPosition[position])
            var registers = requireNotNull(incoming[position])
            val instruction = record.instruction
            readRegisters(instruction).forEach { register ->
                if (registers[register] == RegisterKind.UNDEFINED) {
                    return MethodEditRejectionReason.StructuralValidationFailure(
                        "Register v$register is read before assignment",
                    )
                }
            }
            validateReturnRegister(instruction, registers)?.let { return it }

            val exceptionRegisters = registers
            registers = applyRegisterWrites(instruction, registers)
            val successors = buildList {
                val target = body.controlTargets[position]
                when (instruction.opcode) {
                    in OpcodeGroups.GOTO -> target?.let(::add)
                    in OpcodeGroups.CONDITIONAL_BRANCH -> {
                        target?.let(::add)
                        nextByPosition[position]?.let(::add)
                    }
                    in OpcodeGroups.SWITCH -> {
                        body.switchCases[position].orEmpty().mapTo(this) { it.target }
                        nextByPosition[position]?.let(::add)
                    }
                    else -> if (instruction.opcode.canContinue()) nextByPosition[position]?.let(::add)
                }
            }
            for (successor in successors.distinct()) {
                enqueueRegisterState(successor, registers, recordsByPosition, incoming, pending)
            }
            if (instruction.opcode.canThrow()) {
                for (successor in exceptionTargetsByPosition[position].orEmpty()) {
                    enqueueRegisterState(successor, exceptionRegisters, recordsByPosition, incoming, pending)
                }
            }
        }
        return null
    }

    private fun exceptionTargetsByPosition(
        body: MethodBody,
        records: List<EncodedInstruction>,
    ): Map<LogicalCodePosition, Set<LogicalCodePosition>> {
        val ranges = body.tryRegions.groupBy { it.start.ordinal to it.end.ordinal }
            .map { (range, regions) ->
                Triple(
                    range.first,
                    range.second,
                    regions.flatMap { region -> region.handlers.map(ImportedExceptionHandler::target) }.toSet(),
                )
            }
            .sortedBy { it.first }
        if (ranges.isEmpty()) return emptyMap()
        val result = HashMap<LogicalCodePosition, Set<LogicalCodePosition>>()
        var rangeIndex = 0
        for (record in records) {
            while (rangeIndex < ranges.size && record.position.ordinal >= ranges[rangeIndex].second) rangeIndex++
            if (rangeIndex >= ranges.size) break
            val range = ranges[rangeIndex]
            if (record.position.ordinal >= range.first) result[record.position] = range.third
        }
        return result
    }

    private fun enqueueRegisterState(
        successor: LogicalCodePosition,
        state: PersistentList<RegisterKind>,
        recordsByPosition: Map<LogicalCodePosition, EncodedInstruction>,
        incoming: MutableMap<LogicalCodePosition, PersistentList<RegisterKind>>,
        pending: ArrayDeque<LogicalCodePosition>,
    ) {
        if (successor !in recordsByPosition) return
        val previous = incoming[successor]
        val merged = if (previous == null) state else mergeRegisterStates(previous, state)
        if (previous == null || previous != merged) {
            incoming[successor] = merged
            pending.addLast(successor)
        }
    }

    private fun validateReturnRegister(
        instruction: Instruction,
        registers: PersistentList<RegisterKind>,
    ): MethodEditRejectionReason? {
        if (instruction.opcode !in OpcodeGroups.RETURN || instruction.opcode == Opcode.RETURN_VOID) return null
        val register = (instruction as OneRegisterInstruction).registerA
        val kind = registers[register]
        return when {
            instruction.opcode == Opcode.RETURN_OBJECT && kind == RegisterKind.NARROW ->
                MethodEditRejectionReason.StructuralValidationFailure(
                    "return-object reads a definitely non-reference value from v$register",
                )
            instruction.opcode == Opcode.RETURN && kind == RegisterKind.REFERENCE ->
                MethodEditRejectionReason.StructuralValidationFailure(
                    "return reads a definitely reference value from v$register",
                )
            else -> null
        }
    }

    private fun applyRegisterWrites(
        instruction: Instruction,
        input: PersistentList<RegisterKind>,
    ): PersistentList<RegisterKind> {
        var registers = input
        when (instruction.opcode) {
            Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16 -> {
                val register = (instruction as OneRegisterInstruction).registerA
                registers = registers.replacingAt(register, if ((instruction as NarrowLiteralInstruction).narrowLiteral == 0) {
                    RegisterKind.ZERO
                } else {
                    RegisterKind.NARROW
                })
            }
            Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO, Opcode.CONST_CLASS, Opcode.NEW_INSTANCE,
            -> registers = registers.replacingAt((instruction as OneRegisterInstruction).registerA, RegisterKind.REFERENCE)
            Opcode.NEW_ARRAY -> registers = registers.replacingAt(
                (instruction as TwoRegisterInstruction).registerA,
                RegisterKind.REFERENCE,
            )
            Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
            Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16,
            -> {
                instruction as TwoRegisterInstruction
                registers = registers.replacingAt(instruction.registerA, registers[instruction.registerB])
            }
            Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16 -> {
                instruction as TwoRegisterInstruction
                registers = registers.replacingAt(instruction.registerA, RegisterKind.WIDE_LOW)
                    .replacingAt(instruction.registerA + 1, RegisterKind.WIDE_HIGH)
            }
            Opcode.MOVE_RESULT_OBJECT -> registers = registers.replacingAt(
                (instruction as OneRegisterInstruction).registerA,
                RegisterKind.REFERENCE,
            )
            Opcode.MOVE_RESULT_WIDE -> {
                val register = (instruction as OneRegisterInstruction).registerA
                registers = registers.replacingAt(register, RegisterKind.WIDE_LOW)
                    .replacingAt(register + 1, RegisterKind.WIDE_HIGH)
            }
            Opcode.MOVE_RESULT -> registers = registers.replacingAt(
                (instruction as OneRegisterInstruction).registerA,
                RegisterKind.UNKNOWN,
            )
            Opcode.MOVE_EXCEPTION -> registers = registers.replacingAt(
                (instruction as OneRegisterInstruction).registerA,
                RegisterKind.REFERENCE,
            )
            else -> if (instruction.opcode.setsRegister()) {
                val destination = when (instruction) {
                    is OneRegisterInstruction -> instruction.registerA
                    is TwoRegisterInstruction -> instruction.registerA
                    is ThreeRegisterInstruction -> instruction.registerA
                    else -> null
                } ?: return registers
                registers = registers.replacingAt(destination, if (instruction.opcode.setsWideRegister()) {
                    RegisterKind.WIDE_LOW
                } else {
                    RegisterKind.UNKNOWN
                })
                if (instruction.opcode.setsWideRegister()) {
                    registers = registers.replacingAt(destination + 1, RegisterKind.WIDE_HIGH)
                }
            }
        }
        return registers
    }

    private fun readRegisters(instruction: Instruction): List<Int> {
        if (instruction.opcode in ORDINARY_INVOKE_OPCODES) return invocationRegisters(instruction)
        return when {
            instruction.opcode in OpcodeGroups.CONDITIONAL_BRANCH -> when (instruction) {
                is TwoRegisterInstruction -> listOf(instruction.registerA, instruction.registerB)
                is OneRegisterInstruction -> listOf(instruction.registerA)
                else -> emptyList()
            }
            instruction.opcode in OpcodeGroups.RETURN && instruction.opcode != Opcode.RETURN_VOID ->
                listOf((instruction as OneRegisterInstruction).registerA)
            instruction.opcode in setOf(Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
                Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16) ->
                listOf((instruction as TwoRegisterInstruction).registerB)
            instruction.opcode in setOf(Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16) -> {
                instruction as TwoRegisterInstruction
                listOf(instruction.registerB, instruction.registerB + 1)
            }
            instruction.opcode.name.endsWith("/2addr") && instruction is TwoRegisterInstruction ->
                listOf(instruction.registerA, instruction.registerB)
            instruction.opcode.setsRegister() -> when (instruction) {
                is ThreeRegisterInstruction -> listOf(instruction.registerB, instruction.registerC)
                is TwoRegisterInstruction -> listOf(instruction.registerB)
                else -> emptyList()
            }
            else -> when (instruction) {
                is ThreeRegisterInstruction -> listOf(instruction.registerA, instruction.registerB, instruction.registerC)
                is TwoRegisterInstruction -> listOf(instruction.registerA, instruction.registerB)
                is OneRegisterInstruction -> listOf(instruction.registerA)
                else -> emptyList()
            }
        }
    }

    private fun mergeRegisterStates(
        left: PersistentList<RegisterKind>,
        right: PersistentList<RegisterKind>,
    ): PersistentList<RegisterKind> = List(left.size) { index ->
        val a = left[index]
        val b = right[index]
        when {
            a == b -> a
            a == RegisterKind.UNDEFINED || b == RegisterKind.UNDEFINED -> RegisterKind.UNDEFINED
            a == RegisterKind.ZERO && b == RegisterKind.REFERENCE -> RegisterKind.REFERENCE
            b == RegisterKind.ZERO && a == RegisterKind.REFERENCE -> RegisterKind.REFERENCE
            a == RegisterKind.ZERO && b == RegisterKind.NARROW -> RegisterKind.NARROW
            b == RegisterKind.ZERO && a == RegisterKind.NARROW -> RegisterKind.NARROW
            else -> RegisterKind.UNKNOWN
        }
    }.toPersistentList()

    private fun invocationRegisters(instruction: Instruction): List<Int> = when (instruction) {
        is FiveRegisterInstruction -> listOf(
            instruction.registerC,
            instruction.registerD,
            instruction.registerE,
            instruction.registerF,
            instruction.registerG,
        ).take(instruction.registerCount)
        is RegisterRangeInstruction -> List(instruction.registerCount) { instruction.startRegister + it }
        else -> emptyList()
    }

    private fun invalidRegister(instruction: Instruction, registerCount: Int): Int? {
        fun invalid(register: Int): Int? = register.takeIf { it !in 0 until registerCount }

        when (instruction) {
            is ThreeRegisterInstruction -> {
                invalid(instruction.registerA)?.let { return it }
                invalid(instruction.registerB)?.let { return it }
                invalid(instruction.registerC)?.let { return it }
            }
            is TwoRegisterInstruction -> {
                invalid(instruction.registerA)?.let { return it }
                invalid(instruction.registerB)?.let { return it }
            }
            is OneRegisterInstruction -> invalid(instruction.registerA)?.let { return it }
        }
        if (instruction is FiveRegisterInstruction) {
            val registers = intArrayOf(
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
            )
            for (index in 0 until instruction.registerCount) {
                invalid(registers[index])?.let { return it }
            }
        }
        if (instruction is RegisterRangeInstruction && instruction.registerCount > 0) {
            val last = instruction.startRegister.toLong() + instruction.registerCount - 1L
            if (instruction.startRegister < 0 || last >= registerCount) {
                return last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            }
        }
        return null
    }

    private fun invalidWideRegister(instruction: Instruction, registerCount: Int): Int? {
        fun invalidHighRegister(lowRegister: Int): Int? =
            (lowRegister + 1).takeIf { lowRegister < 0 || it !in 0 until registerCount }

        if (instruction.opcode.setsWideRegister() && instruction is OneRegisterInstruction) {
            invalidHighRegister(instruction.registerA)?.let { return it }
        }
        when (instruction.opcode) {
            Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16,
            Opcode.NEG_LONG, Opcode.NOT_LONG, Opcode.NEG_DOUBLE,
            Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.LONG_TO_DOUBLE,
            Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_LONG, Opcode.DOUBLE_TO_FLOAT,
            -> invalidHighRegister((instruction as TwoRegisterInstruction).registerB)?.let { return it }

            Opcode.RETURN_WIDE,
            Opcode.SPUT_WIDE,
            Opcode.SPUT_WIDE_VOLATILE,
            -> invalidHighRegister((instruction as OneRegisterInstruction).registerA)?.let { return it }

            Opcode.IPUT_WIDE,
            Opcode.IPUT_WIDE_VOLATILE,
            -> invalidHighRegister((instruction as TwoRegisterInstruction).registerA)?.let { return it }

            Opcode.APUT_WIDE,
            -> invalidHighRegister((instruction as ThreeRegisterInstruction).registerA)?.let { return it }

            Opcode.CMP_LONG, Opcode.CMPL_DOUBLE, Opcode.CMPG_DOUBLE -> {
                instruction as ThreeRegisterInstruction
                invalidHighRegister(instruction.registerB)?.let { return it }
                invalidHighRegister(instruction.registerC)?.let { return it }
            }
            in WIDE_BINARY_OPCODES -> {
                instruction as ThreeRegisterInstruction
                invalidHighRegister(instruction.registerB)?.let { return it }
                if (instruction.opcode !in WIDE_SHIFT_OPCODES) {
                    invalidHighRegister(instruction.registerC)?.let { return it }
                }
            }
            in WIDE_TWO_ADDRESS_OPCODES -> {
                instruction as TwoRegisterInstruction
                if (instruction.opcode !in WIDE_TWO_ADDRESS_SHIFT_OPCODES) {
                    invalidHighRegister(instruction.registerB)?.let { return it }
                }
            }
            else -> Unit
        }
        return null
    }

    private fun validateMoveResult(
        previous: Instruction?,
        moveResultOpcode: Opcode,
    ): MethodEditRejectionReason? {
        if (previous == null || !previous.opcode.setsResult()) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "$moveResultOpcode does not follow a result-producing instruction",
            )
        }
        val expected = when {
            previous.opcode == Opcode.FILLED_NEW_ARRAY || previous.opcode == Opcode.FILLED_NEW_ARRAY_RANGE ->
                Opcode.MOVE_RESULT_OBJECT
            previous.opcode in ORDINARY_INVOKE_OPCODES &&
                previous is ReferenceInstruction &&
                previous.reference is MethodReference -> {
                val returnType = (previous.reference as MethodReference).returnType
                moveResultOpcodeFor(returnType) ?: return MethodEditRejectionReason.StructuralValidationFailure(
                    "$moveResultOpcode follows an invocation with no result",
                )
            }
            else -> return null
        }
        if (moveResultOpcode != expected) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "$moveResultOpcode does not match ${previous.opcode}'s result type",
            )
        }
        return null
    }

    private fun validateInvoke(instruction: Instruction): MethodEditRejectionReason? {
        if (instruction.opcode !in ORDINARY_INVOKE_OPCODES) return null
        val method = ((instruction as? ReferenceInstruction)?.reference as? MethodReference) ?: return null
        val variable = instruction as? VariableRegisterInstruction
            ?: return MethodEditRejectionReason.StructuralValidationFailure(
                "${instruction.opcode} has no invocation register list",
            )
        val hasReceiver = instruction.opcode !in STATIC_INVOKE_OPCODES
        val expectedWords = method.parameterTypes.sumOf { type -> if (isWideType(type)) 2 else 1 } +
            if (hasReceiver) 1 else 0
        if (variable.registerCount != expectedWords) {
            return MethodEditRejectionReason.StructuralValidationFailure(
                "${instruction.opcode} uses ${variable.registerCount} register words for $expectedWords argument words",
            )
        }
        if (instruction is FiveRegisterInstruction) {
            val registers = intArrayOf(
                instruction.registerC,
                instruction.registerD,
                instruction.registerE,
                instruction.registerF,
                instruction.registerG,
            )
            var word = if (hasReceiver) 1 else 0
            for (type in method.parameterTypes) {
                if (isWideType(type) && registers[word + 1] != registers[word] + 1) {
                    return MethodEditRejectionReason.StructuralValidationFailure(
                        "${instruction.opcode} has a non-adjacent wide argument",
                    )
                }
                word += if (isWideType(type)) 2 else 1
            }
        }
        return null
    }

    private fun isWideType(type: CharSequence): Boolean = type.length == 1 && (type[0] == 'J' || type[0] == 'D')

    private fun validateReturnOpcode(
        returnType: String,
        opcode: Opcode,
    ): MethodEditRejectionReason? {
        if (opcode !in OpcodeGroups.RETURN) return null
        val expected = when {
            returnType == "V" -> Opcode.RETURN_VOID
            returnType == "J" || returnType == "D" -> Opcode.RETURN_WIDE
            returnType.startsWith('L') || returnType.startsWith('[') -> Opcode.RETURN_OBJECT
            else -> Opcode.RETURN
        }
        return if (opcode == expected) {
            null
        } else {
            MethodEditRejectionReason.StructuralValidationFailure(
                "$opcode does not match method return type $returnType",
            )
        }
    }

    private fun moveResultOpcodeFor(returnType: String): Opcode? = when {
        returnType == "V" -> null
        returnType == "J" || returnType == "D" -> Opcode.MOVE_RESULT_WIDE
        returnType.startsWith('L') || returnType.startsWith('[') -> Opcode.MOVE_RESULT_OBJECT
        else -> Opcode.MOVE_RESULT
    }

    private val MOVE_RESULT_OPCODES = setOf(
        Opcode.MOVE_RESULT,
        Opcode.MOVE_RESULT_WIDE,
        Opcode.MOVE_RESULT_OBJECT,
    )
    private val STATIC_INVOKE_OPCODES = setOf(Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE)
    private val ORDINARY_INVOKE_OPCODES = setOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_SUPER,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_STATIC,
        Opcode.INVOKE_INTERFACE,
        Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.INVOKE_SUPER_RANGE,
        Opcode.INVOKE_DIRECT_RANGE,
        Opcode.INVOKE_STATIC_RANGE,
        Opcode.INVOKE_INTERFACE_RANGE,
    )
    private val WIDE_BINARY_OPCODES = setOf(
        Opcode.ADD_LONG,
        Opcode.SUB_LONG,
        Opcode.MUL_LONG,
        Opcode.DIV_LONG,
        Opcode.REM_LONG,
        Opcode.AND_LONG,
        Opcode.OR_LONG,
        Opcode.XOR_LONG,
        Opcode.SHL_LONG,
        Opcode.SHR_LONG,
        Opcode.USHR_LONG,
        Opcode.ADD_DOUBLE,
        Opcode.SUB_DOUBLE,
        Opcode.MUL_DOUBLE,
        Opcode.DIV_DOUBLE,
        Opcode.REM_DOUBLE,
    )
    private val WIDE_SHIFT_OPCODES = setOf(Opcode.SHL_LONG, Opcode.SHR_LONG, Opcode.USHR_LONG)
    private val WIDE_TWO_ADDRESS_OPCODES = setOf(
        Opcode.ADD_LONG_2ADDR,
        Opcode.SUB_LONG_2ADDR,
        Opcode.MUL_LONG_2ADDR,
        Opcode.DIV_LONG_2ADDR,
        Opcode.REM_LONG_2ADDR,
        Opcode.AND_LONG_2ADDR,
        Opcode.OR_LONG_2ADDR,
        Opcode.XOR_LONG_2ADDR,
        Opcode.SHL_LONG_2ADDR,
        Opcode.SHR_LONG_2ADDR,
        Opcode.USHR_LONG_2ADDR,
        Opcode.ADD_DOUBLE_2ADDR,
        Opcode.SUB_DOUBLE_2ADDR,
        Opcode.MUL_DOUBLE_2ADDR,
        Opcode.DIV_DOUBLE_2ADDR,
        Opcode.REM_DOUBLE_2ADDR,
    )
    private val WIDE_TWO_ADDRESS_SHIFT_OPCODES = setOf(
        Opcode.SHL_LONG_2ADDR,
        Opcode.SHR_LONG_2ADDR,
        Opcode.USHR_LONG_2ADDR,
    )
    private const val MAX_TRY_ITEMS = 65_535
    private const val MAX_HANDLER_OFFSET = 65_535L
    private const val MAX_REGISTER_COUNT = 65_535
    private const val MAX_SWITCH_ELEMENTS = 65_535
    private const val MAX_ARRAY_DIMENSIONS = 255
    private val NON_VOID_PRIMITIVE_TYPES = setOf('Z', 'B', 'S', 'C', 'I', 'J', 'F', 'D')
}
