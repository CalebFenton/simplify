package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.debug.DebugItem
import com.android.tools.smali.dexlib2.iface.debug.EndLocal
import com.android.tools.smali.dexlib2.iface.debug.EpilogueBegin
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.debug.PrologueEnd
import com.android.tools.smali.dexlib2.iface.debug.RestartLocal
import com.android.tools.smali.dexlib2.iface.debug.SetSourceFile
import com.android.tools.smali.dexlib2.iface.debug.StartLocal
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.instruction.VariableRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import org.cf.smalivm.core.MethodEditRejectionReason.*

/** The structural rules an edited method must satisfy before it is published. */
internal object MethodEditValidator {
    /**
     * Rules on the logical body: control targets, payload ownership, move-exception entry, try regions and debug
     * metadata. An empty try range is legal and is dropped when the body is laid out.
     */
    fun validateLogicalBody(body: MethodBody): MethodEditRejectionReason? {
        val recordsByPosition = body.instructions.associateBy(EncodedInstruction::position)
        val payloadPositions = recordsByPosition.filterValues {
            it.instruction.opcode in OpcodeGroups.PAYLOAD
        }.keys
        val payloadOwners = HashMap<LogicalCodePosition, MutableList<EncodedInstruction>>()

        for (record in body.instructions) {
            val instruction = record.instruction
            val address = record.codeAddress
            val target = body.controlTargets[record.position]
            if (instruction is OffsetInstruction && target == null) return MissingControlTarget(address)
            if (target == null) continue
            val targetRecord = recordsByPosition[target]
            when (instruction.opcode) {
                Opcode.FILL_ARRAY_DATA -> if (targetRecord?.instruction !is ArrayPayload) {
                    return WrongPayloadTarget(address)
                }
                in OpcodeGroups.SWITCH -> {
                    val expectedPayload = if (instruction.opcode == Opcode.PACKED_SWITCH) {
                        Opcode.PACKED_SWITCH_PAYLOAD
                    } else {
                        Opcode.SPARSE_SWITCH_PAYLOAD
                    }
                    if (targetRecord?.instruction?.opcode != expectedPayload) return WrongPayloadTarget(address)
                    val cases = body.switchCases[record.position].orEmpty()
                    val payloadKeys = (targetRecord.instruction as SwitchPayload).switchElements.map { it.key }
                    if (cases.map(ImportedSwitchCase::key) != payloadKeys) return SwitchCasesMismatchPayload(address)
                    for (case in cases) {
                        if (case.target in payloadPositions || case.target !in recordsByPosition) {
                            return SwitchCaseTargetNotExecutable(address, case.key)
                        }
                    }
                }
                else -> if (targetRecord == null || target in payloadPositions) {
                    return BranchTargetNotExecutable(address)
                }
            }
            if (instruction.opcode in OpcodeGroups.PAYLOAD_OWNER) {
                payloadOwners.getOrPut(target, ::mutableListOf).add(record)
            }
        }

        for (payloadPosition in payloadPositions) {
            val payload = recordsByPosition.getValue(payloadPosition)
            if (payloadOwners[payloadPosition].isNullOrEmpty()) return UnownedPayload(payload.codeAddress)
            validatePayloadKeys(payload)?.let { return it }
        }

        val paddingFreeRecords = body.instructions.filter { it.position !in body.syntheticPayloadPaddingPositions }
        if (paddingFreeRecords.firstOrNull()?.position in payloadPositions) return PayloadAtMethodEntry
        paddingFreeRecords.zipWithNext().forEach { (previous, next) ->
            if (next.position in payloadPositions && previous.instruction.opcode.canContinue()) {
                return FallsIntoPayload(next.codeAddress)
            }
            if (next.instruction.opcode == Opcode.MOVE_EXCEPTION && previous.instruction.opcode.canContinue()) {
                return FallsIntoMoveException(next.codeAddress)
            }
        }
        val moveExceptionAddresses = body.instructions
            .filter { it.instruction.opcode == Opcode.MOVE_EXCEPTION }
            .associate { it.position to it.codeAddress }
        val branchTargets = body.controlTargets.values.asSequence() +
            body.switchCases.values.asSequence().flatten().map(ImportedSwitchCase::target)
        branchTargets.firstNotNullOfOrNull(moveExceptionAddresses::get)?.let { return BranchIntoMoveException(it) }

        validateTryRegions(body, recordsByPosition, payloadPositions)?.let { return it }
        for (item in body.debugItems) {
            if (item.position != body.endPosition &&
                (item.position !in recordsByPosition || item.position in payloadPositions)
            ) {
                return DebugItemNotAtInstruction
            }
            val register = debugRegister(item.item)
            if (register != null && register !in 0 until body.registerCount) return RegisterOverflow(register)
            if (item.item !is LineNumber && item.item !is StartLocal && item.item !is EndLocal &&
                item.item !is RestartLocal && item.item !is PrologueEnd && item.item !is EpilogueBegin &&
                item.item !is SetSourceFile
            ) {
                return UnsupportedDebugItem(item.item.debugItemType)
            }
        }
        return null
    }

    /**
     * Checks what the layout decides: encodable registers and opcodes, code size, payload alignment, branch offsets,
     * and the encoded try items. Logical shape is [validateLogicalBody]'s, and a layout preserves it.
     */
    fun validateLayout(
        layout: MethodLayout,
        snapshot: ProgramSnapshot,
        projectedReferences: DexReferenceClosure = snapshot.dexReferenceClosure,
    ): MethodEditRejectionReason? {
        if (layout.registerCount > DexLimits.MAX_REGISTER_COUNT) return RegisterOverflow(layout.registerCount - 1)
        val opcodes = snapshot.outputApiPolicy.opcodes()
        var codeUnits = 0L
        for (record in layout.instructions) {
            val instruction = record.instruction
            if (opcodes.getOpcodeValue(instruction.opcode) == null) {
                return OpcodeUnavailable(instruction.opcode, snapshot.outputApiPolicy.apiLevel)
            }
            invalidRegister(instruction, layout.registerCount)?.let { return RegisterOverflow(it) }
            invalidWideRegister(instruction, layout.registerCount)?.let { return RegisterOverflow(it) }
            codeUnits += instruction.codeUnits
            if (codeUnits > Int.MAX_VALUE) return CodeSizeOverflow(codeUnits)
        }
        val addresses = layout.instructions.mapTo(hashSetOf(), LaidOutInstruction::codeAddress)
        for (record in layout.instructions) {
            val instruction = record.instruction
            if (instruction.opcode in OpcodeGroups.PAYLOAD && record.codeAddress and 1 != 0) {
                return MisalignedPayload(record.codeAddress)
            }
            if (instruction !is OffsetInstruction) continue
            if (record.codeAddress + instruction.codeOffset !in addresses) {
                return BranchOffsetNotAtInstruction(record.codeAddress)
            }
            if (instruction.opcode in OpcodeGroups.BRANCH && instruction.codeOffset == 0 &&
                instruction.opcode != Opcode.GOTO_32
            ) {
                return ZeroBranchOffset(record.codeAddress)
            }
        }
        if (layout.tryBlocks.any { it.codeUnitCount <= 0 }) return EmptyTryRange
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
            return RegisterCountReduced(candidate.baselineRegisterCount, body.registerCount)
        }
        if (body.registerCount > DexLimits.MAX_REGISTER_COUNT) return RegisterOverflow(body.registerCount - 1)
        val executableInstructions = body.instructions.filter {
            it.instruction.opcode !in OpcodeGroups.PAYLOAD &&
                it.position !in body.syntheticPayloadPaddingPositions
        }
        if (executableInstructions.isEmpty()) return NoExecutableCode
        if (executableInstructions.last().instruction.opcode.canContinue()) return FallsPastMethodEnd
        val handlerAddresses = body.implementation.tryBlocks.flatMap { block ->
            block.exceptionHandlers.map { it.handlerCodeAddress }
        }.toSet()
        body.instructions.forEachIndexed { index, record ->
            if (record.instruction.opcode in MOVE_RESULT_OPCODES) {
                validateMoveResult(body.instructions.getOrNull(index - 1)?.instruction, record)?.let { return it }
            }
            if (record.instruction.opcode == Opcode.MOVE_EXCEPTION && record.codeAddress !in handlerAddresses) {
                return MoveExceptionOutsideHandler(record.codeAddress)
            }
            validateInvoke(record)?.let { return it }
            validateReturnOpcode(candidate.declaration.returnType, record)?.let { return it }
        }
        validateRegisterFlow(candidate)?.let { return it }
        val allowedReferences = snapshot.dexReferenceClosure.plus(stagedReferences)
        if (!allowedReferences.containsAll(candidate.dexReferenceClosure)) return UnstagedReference
        return null
    }

    fun validateStagedReference(reference: StagedDexReference): MethodEditRejectionReason? = when (reference) {
        is StagedDexReference.Type -> descriptor(reference.descriptor, ::isValidReturnTypeDescriptor)
        is StagedDexReference.StringLiteral -> null
        is StagedDexReference.Field ->
            descriptor(reference.definingClass, ::isValidClassDescriptor)
                ?: memberName(reference.name)
                ?: descriptor(reference.type, ::isValidNonVoidTypeDescriptor)
        is StagedDexReference.Method ->
            descriptor(reference.definingClass, ::isValidReferenceTypeDescriptor)
                ?: memberName(reference.name)
                ?: reference.parameterTypes.firstNotNullOfOrNull { descriptor(it, ::isValidNonVoidTypeDescriptor) }
                ?: descriptor(reference.returnType, ::isValidReturnTypeDescriptor)
        is StagedDexReference.Prototype ->
            reference.parameterTypes.firstNotNullOfOrNull { descriptor(it, ::isValidNonVoidTypeDescriptor) }
                ?: descriptor(reference.returnType, ::isValidReturnTypeDescriptor)
    }

    fun validateCatchTypeDescriptor(descriptor: String): MethodEditRejectionReason? =
        if (isValidClassDescriptor(descriptor)) null else CatchTypeNotClass(descriptor)

    fun validateTypeOperand(opcode: Opcode, descriptor: String): MethodEditRejectionReason? {
        val valid = when (opcode) {
            Opcode.NEW_INSTANCE -> isValidClassDescriptor(descriptor)
            Opcode.NEW_ARRAY, Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE ->
                descriptor.startsWith('[') && isValidNonVoidTypeDescriptor(descriptor)
            Opcode.CHECK_CAST, Opcode.INSTANCE_OF -> isValidReferenceTypeDescriptor(descriptor)
            Opcode.CONST_CLASS -> isValidReturnTypeDescriptor(descriptor)
            else -> true
        }
        return if (valid) null else InvalidTypeOperand(opcode, descriptor)
    }

    private fun descriptor(descriptor: String, isValid: (String) -> Boolean): MethodEditRejectionReason? =
        if (isValid(descriptor)) null else MalformedDescriptor(descriptor)

    private fun memberName(name: String): MethodEditRejectionReason? =
        if (isValidMemberName(name)) null else MalformedMemberName(name)

    private fun isValidReturnTypeDescriptor(descriptor: String): Boolean =
        descriptor == "V" || isValidNonVoidTypeDescriptor(descriptor)

    private fun isValidNonVoidTypeDescriptor(descriptor: String): Boolean {
        if (descriptor.length == 1) return descriptor[0] in NON_VOID_PRIMITIVE_TYPES
        if (descriptor.startsWith('L')) return isValidClassDescriptor(descriptor)
        if (!descriptor.startsWith('[')) return false
        val dimensions = descriptor.indexOfFirst { it != '[' }.let { if (it < 0) descriptor.length else it }
        if (dimensions !in 1..VirtualType.MAX_ARRAY_DIMENSIONS) return false
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

    private fun validateEncodedTryStructure(
        layout: MethodLayout,
        projectedReferences: DexReferenceClosure,
    ): MethodEditRejectionReason? {
        if (layout.tryBlocks.size > DexLimits.MAX_TRY_ITEMS) return TooManyTryItems(layout.tryBlocks.size)
        if (layout.tryBlocks.isEmpty()) return null
        val typeIndexes = projectedReferences.types.sorted().withIndex().associate { (index, type) -> type to index }
        val lastOffset = ProgramDexWriter.lastHandlerListOffset(layout.tryBlocks, typeIndexes)
            ?: return HandlerTypeOutsideReferenceClosure
        if (lastOffset > DexLimits.MAX_HANDLER_OFFSET) return HandlerListOffsetOverflow(lastOffset)
        return null
    }

    /** Regions sharing one range are encoded as one try item, so catch-all order is checked over that combined list. */
    private fun validateTryRegions(
        body: MethodBody,
        recordsByPosition: Map<LogicalCodePosition, EncodedInstruction>,
        payloadPositions: Set<LogicalCodePosition>,
    ): MethodEditRejectionReason? {
        data class Range(val start: Int, val end: Int)

        val ranges = ArrayList<Range>()
        val handlerTypesByRange = LinkedHashMap<Range, MutableList<String?>>()
        for (region in body.tryRegions) {
            val start = region.start.ordinal
            val end = region.end.ordinal
            val range = Range(start, end)
            if (start > end) return ReversedTryRange
            if (start < end && (region.start !in recordsByPosition || region.start in payloadPositions)) {
                return TryRangeStartNotExecutable
            }
            if (region.handlers.isEmpty()) return TryRangeWithoutHandler
            region.handlers.mapTo(handlerTypesByRange.getOrPut(range, ::mutableListOf)) { it.exceptionType }
            for (handler in region.handlers) {
                if (handler.target !in recordsByPosition || handler.target in payloadPositions) {
                    return HandlerTargetNotExecutable
                }
            }
            if (start < end) ranges += range
        }
        for (exceptionTypes in handlerTypesByRange.values) {
            val catchAllIndex = exceptionTypes.indexOf(null)
            if (catchAllIndex >= 0 && (catchAllIndex != exceptionTypes.lastIndex || exceptionTypes.count { it == null } != 1)) {
                return CatchAllNotLast
            }
        }
        val distinctRanges = ranges.distinct().sortedWith(compareBy(Range::start, Range::end))
        distinctRanges.zipWithNext().forEach { (previous, next) ->
            if (next.start < previous.end) return OverlappingTryRanges
        }
        return null
    }

    private fun validatePayloadKeys(record: EncodedInstruction): MethodEditRejectionReason? {
        val payload = record.instruction
        val address = record.codeAddress
        if (payload is ArrayPayload && payload.elementWidth !in setOf(1, 2, 4, 8)) {
            return InvalidArrayElementWidth(address, payload.elementWidth)
        }
        if (payload !is SwitchPayload) return null
        if (payload.switchElements.size > DexLimits.MAX_SWITCH_ELEMENTS) {
            return SwitchPayloadTooLarge(address, payload.switchElements.size)
        }
        val keys = payload.switchElements.map { it.key }
        when (payload.opcode) {
            Opcode.PACKED_SWITCH_PAYLOAD -> keys.zipWithNext().forEach { (previous, next) ->
                if (next.toLong() != previous.toLong() + 1L) return PackedSwitchKeysNotConsecutive(address)
            }
            Opcode.SPARSE_SWITCH_PAYLOAD -> keys.zipWithNext().forEach { (previous, next) ->
                if (next <= previous) return SparseSwitchKeysNotSorted(address)
            }
            else -> Unit
        }
        return null
    }

    private fun debugRegister(item: DebugItem): Int? = when (item) {
        is StartLocal -> item.register
        is EndLocal -> item.register
        is RestartLocal -> item.register
        else -> null
    }

    /** Reports only definite violations: a read of a never-assigned register, or a return of the wrong category. */
    private fun validateRegisterFlow(
        candidate: ApplicationMethodDefinitionCandidate,
    ): MethodEditRejectionReason? {
        val body = candidate.body
        val flow = RegisterFlow.analyze(body, candidate.declaration, body.registerCount) {
            it.instruction.registerOperands()
        }
        for (record in body.instructions) {
            val state = flow.before(record.position) ?: continue
            for (register in readRegisters(record.instruction)) {
                if (state[register] == RegisterValue.UNDEFINED) return ReadBeforeAssignment(record.codeAddress, register)
            }
            if (hasReturnCategoryMismatch(record.instruction, state)) {
                return ReturnCategoryMismatch(record.codeAddress, (record.instruction as OneRegisterInstruction).registerA)
            }
        }
        return null
    }

    private fun hasReturnCategoryMismatch(instruction: Instruction, state: RegisterFlow.State): Boolean {
        val value = when (instruction.opcode) {
            Opcode.RETURN, Opcode.RETURN_OBJECT -> state[(instruction as OneRegisterInstruction).registerA]
            else -> return false
        }
        return (instruction.opcode == Opcode.RETURN_OBJECT && value == RegisterValue.NARROW) ||
            (instruction.opcode == Opcode.RETURN && value == RegisterValue.REFERENCE)
    }

    /** Every register word an instruction reads: both words of a wide operand and every argument word. */
    private fun readRegisters(instruction: Instruction): List<Int> {
        val registers = instruction.registerOperands()
        if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) return registers
        return buildList {
            RegisterEffects.operands(instruction).forEachIndexed { index, operand ->
                val register = registers.getOrNull(index)
                if (register == null || !operand.reads) return@forEachIndexed
                add(register)
                if (operand.category == ValueCategory.WIDE) add(register + 1)
            }
        }
    }

    /** A range reports its last register, the one a too-small register file cannot hold. */
    private fun invalidRegister(instruction: Instruction, registerCount: Int): Int? {
        if (instruction is RegisterRangeInstruction) {
            if (instruction.registerCount == 0) return null
            val last = instruction.startRegister.toLong() + instruction.registerCount - 1L
            return last.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
                .takeIf { instruction.startRegister < 0 || last >= registerCount }
        }
        return instruction.registerOperands().firstOrNull { it !in 0 until registerCount }
    }

    /** The high register of every wide operand of a fixed-register instruction must exist. */
    private fun invalidWideRegister(instruction: Instruction, registerCount: Int): Int? {
        if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) return null
        val registers = instruction.registerOperands()
        RegisterEffects.operands(instruction).forEachIndexed { index, operand ->
            val low = registers.getOrNull(index) ?: return@forEachIndexed
            if (operand.category == ValueCategory.WIDE && (low < 0 || low + 1 >= registerCount)) return low + 1
        }
        return null
    }

    private fun validateMoveResult(previous: Instruction?, record: EncodedInstruction): MethodEditRejectionReason? {
        if (previous == null || !previous.opcode.setsResult()) return MoveResultWithoutResult(record.codeAddress)
        val expected = when {
            previous.opcode == Opcode.FILLED_NEW_ARRAY || previous.opcode == Opcode.FILLED_NEW_ARRAY_RANGE ->
                Opcode.MOVE_RESULT_OBJECT
            previous.opcode in ORDINARY_INVOKE_OPCODES &&
                previous is ReferenceInstruction &&
                previous.reference is MethodReference ->
                moveResultOpcodeFor((previous.reference as MethodReference).returnType)
                    ?: return MoveResultWithoutResult(record.codeAddress)
            else -> return null
        }
        return if (record.instruction.opcode == expected) null else MoveResultTypeMismatch(record.codeAddress, expected)
    }

    private fun validateInvoke(record: EncodedInstruction): MethodEditRejectionReason? {
        val instruction = record.instruction
        if (instruction.opcode !in ORDINARY_INVOKE_OPCODES) return null
        val method = ((instruction as? ReferenceInstruction)?.reference as? MethodReference) ?: return null
        val hasReceiver = instruction.opcode !in STATIC_INVOKE_OPCODES
        val expectedWords = method.parameterTypes.sumOf { type -> if (isWideType(type)) 2 else 1 } +
            if (hasReceiver) 1 else 0
        val actualWords = (instruction as VariableRegisterInstruction).registerCount
        if (actualWords != expectedWords) {
            return InvokeArgumentWordMismatch(record.codeAddress, expectedWords, actualWords)
        }
        if (instruction is FiveRegisterInstruction) {
            val registers = instruction.registerOperands()
            var word = if (hasReceiver) 1 else 0
            for (type in method.parameterTypes) {
                if (isWideType(type) && registers[word + 1] != registers[word] + 1) {
                    return SplitWideArgument(record.codeAddress)
                }
                word += if (isWideType(type)) 2 else 1
            }
        }
        return null
    }

    private fun isWideType(type: CharSequence): Boolean = type.length == 1 && (type[0] == 'J' || type[0] == 'D')

    private fun validateReturnOpcode(returnType: String, record: EncodedInstruction): MethodEditRejectionReason? {
        val opcode = record.instruction.opcode
        if (opcode !in OpcodeGroups.RETURN) return null
        val expected = when {
            returnType == "V" -> Opcode.RETURN_VOID
            returnType == "J" || returnType == "D" -> Opcode.RETURN_WIDE
            returnType.startsWith('L') || returnType.startsWith('[') -> Opcode.RETURN_OBJECT
            else -> Opcode.RETURN
        }
        return if (opcode == expected) null else ReturnOpcodeMismatch(record.codeAddress, expected)
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
    private val NON_VOID_PRIMITIVE_TYPES = setOf('Z', 'B', 'S', 'C', 'I', 'J', 'F', 'D')
}
