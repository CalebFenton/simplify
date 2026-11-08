package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
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
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11n
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEndLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEpilogueBegin
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableLineNumber
import com.android.tools.smali.dexlib2.immutable.debug.ImmutablePrologueEnd
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableRestartLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableSetSourceFile
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableStartLocal
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction12x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction23x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction32x

internal sealed interface ScratchRegisterAllocation {
    data class Accepted(
        val body: MethodBody,
        val positionsAfterAllocation: Map<LogicalCodePosition, LogicalCodePosition>,
    ) : ScratchRegisterAllocation

    data class Rejected(val reason: MethodEditRejectionReason) : ScratchRegisterAllocation
}

/**
 * Selects physical register encoding after all transaction reservations are known. Scratch registers take the
 * lowest numbers, so baseline registers and parameters shift up and parameters stay last. An instruction whose
 * shifted registers its format cannot encode is either re-encoded in a wider form or spilled through hidden low
 * temporaries: moves in before the operation, moves out after it.
 */
internal object ScratchRegisterAllocator {
    private data class Expansion(
        val instructions: List<Instruction>,
        val operationIndex: Int,
    )

    private data class RewrittenBody(
        val body: MethodBody,
        val positionsAfterAllocation: Map<LogicalCodePosition, LogicalCodePosition>,
    )

    fun allocate(
        body: MethodBody,
        method: Method,
        scratchWordCount: Int,
    ): ScratchRegisterAllocation {
        require(scratchWordCount >= 0)
        if (scratchWordCount == 0 && body.logicalRegistersByPosition.isEmpty()) {
            return ScratchRegisterAllocation.Accepted(body, body.positionsByCodeAddress.values.associateWith { it })
        }
        val operands = operandModel(body, method, scratchWordCount)
        validateScratchWideOperands(body, scratchWordCount, operands)?.let { return it }
        var hiddenWordCount = 0
        while (true) {
            val addedWordCount = scratchWordCount.toLong() + hiddenWordCount
            if (body.registerCount.toLong() + addedWordCount > DexLimits.MAX_REGISTER_COUNT) {
                return registerOverflow(body.registerCount.toLong() + addedWordCount)
            }
            var requiredHiddenWords = hiddenWordCount
            for (record in body.instructions) {
                val registers = mappedRegisters(record, body, scratchWordCount, hiddenWordCount)
                if (directInstruction(record.instruction, registers) == null) {
                    requiredHiddenWords = maxOf(requiredHiddenWords, operands(record).sumOf { it.category.width })
                }
            }
            if (requiredHiddenWords == hiddenWordCount) break
            hiddenWordCount = requiredHiddenWords
        }

        val addedWordCount = scratchWordCount + hiddenWordCount
        val expansions = LinkedHashMap<LogicalCodePosition, Expansion>(body.instructions.size)
        for (record in body.instructions) {
            val registers = mappedRegisters(record, body, scratchWordCount, hiddenWordCount)
            expansions[record.position] = directInstruction(record.instruction, registers)
                ?.let { Expansion(listOf(it), operationIndex = 0) }
                ?: repairInstruction(record.instruction, registers, operands(record))
        }
        val rewritten = rewriteBody(
            body,
            registerCount = body.registerCount + addedWordCount,
            expansions = expansions,
            debugRegister = { register -> register + addedWordCount },
        )
        return ScratchRegisterAllocation.Accepted(rewritten.body, rewritten.positionsAfterAllocation)
    }

    /**
     * Operands from [RegisterEffects]. `if-eq`, `if-ne`, `if-eqz` and `if-nez` also compare references, so their
     * operands take the category the register holds there, which decides between `move` and `move-object` spills.
     * A body with malformed control flow has no register states here; logical validation rejects it after allocation.
     */
    private fun operandModel(
        body: MethodBody,
        method: Method,
        scratchWordCount: Int,
    ): (EncodedInstruction) -> List<RegisterOperand> {
        val flow by lazy {
            MethodControlFlow.of(body)?.let { controlFlow ->
                RegisterFlow.analyze(controlFlow, method, body.registerCount, body.registerCount + scratchWordCount) {
                    logicalRegisterNumbers(it, body)
                }
            }
        }
        return { record ->
            val operands = RegisterEffects.operands(record.instruction)
            if (record.instruction.opcode !in REFERENCE_COMPARING_BRANCHES) {
                operands
            } else {
                val state = flow?.before(record.position)
                val registers = logicalRegisterNumbers(record, body)
                operands.mapIndexed { index, operand ->
                    val holdsReference = state?.get(registers[index]) == RegisterValue.REFERENCE
                    operand.copy(category = if (holdsReference) ValueCategory.REFERENCE else ValueCategory.NARROW)
                }
            }
        }
    }

    private fun registerOverflow(registerCount: Long): ScratchRegisterAllocation.Rejected =
        ScratchRegisterAllocation.Rejected(
            MethodEditRejectionReason.RegisterOverflow(minOf(registerCount - 1, Int.MAX_VALUE.toLong()).toInt()),
        )

    /** A wide operand names its low register; the high register must also lie inside the reserved scratch range. */
    private fun validateScratchWideOperands(
        body: MethodBody,
        scratchWordCount: Int,
        operands: (EncodedInstruction) -> List<RegisterOperand>,
    ): ScratchRegisterAllocation.Rejected? {
        for (record in body.instructions) {
            if (record.instruction.hasArgumentWords()) continue
            val logicalRegisters = body.logicalRegistersByPosition[record.position] ?: continue
            val modeled = operands(record)
            for (index in logicalRegisters.indices) {
                if (modeled.getOrNull(index)?.category != ValueCategory.WIDE) continue
                val allocation = logicalRegisters[index].allocation
                if (allocation is LogicalRegister.Allocation.Scratch && allocation.offset + 1 >= scratchWordCount) {
                    return ScratchRegisterAllocation.Rejected(
                        MethodEditRejectionReason.WideScratchPairOutsideReservation(allocation.offset),
                    )
                }
            }
        }
        return null
    }

    private fun mappedRegisters(
        record: EncodedInstruction,
        body: MethodBody,
        scratchWordCount: Int,
        hiddenWordCount: Int,
    ): List<Int> {
        val logical = body.logicalRegistersByPosition[record.position]
            ?: return record.instruction.registerOperands().map { it + scratchWordCount + hiddenWordCount }
        return logical.map { register ->
            when (val allocation = register.allocation) {
                is LogicalRegister.Allocation.Baseline -> allocation.register + scratchWordCount + hiddenWordCount
                is LogicalRegister.Allocation.Scratch -> hiddenWordCount + allocation.offset
            }
        }
    }

    private fun directInstruction(source: Instruction, registers: List<Int>): Instruction? {
        if (source is RegisterRangeInstruction && !registersAreContiguous(registers)) return null
        try {
            return source.withRegisters(registers)
        } catch (_: IllegalArgumentException) {
            // The source format cannot encode the selected physical registers. Try its legal wider form.
        }
        if (source.opcode in OpcodeGroups.MOVE && registers.size == 2) {
            return moveInstruction(RegisterEffects.operands(source)[0].category, registers[0], registers[1])
        }
        val singleRegister = registers.singleOrNull()
        if (source.opcode == Opcode.CONST_4 && singleRegister != null && singleRegister in 0..U8_MAX) {
            return ImmutableInstruction21s(Opcode.CONST_16, singleRegister, (source as Instruction11n).narrowLiteral)
        }
        val threeAddress = RegisterEffects.threeAddressForm(source.opcode)
        if (threeAddress != null && registers.all { it in 0..U8_MAX }) {
            return ImmutableInstruction23x(threeAddress, registers[0], registers[0], registers[1])
        }
        val rangeOpcode = RANGE_OPCODES[source.opcode]
        if (rangeOpcode != null && registersAreContiguous(registers)) {
            return source.toRangeForm(rangeOpcode, registers)
        }
        return null
    }

    private fun repairInstruction(
        source: Instruction,
        registers: List<Int>,
        operands: List<RegisterOperand>,
    ): Expansion {
        if (source.hasArgumentWords()) return repairArguments(source, registers, operands)
        check(operands.size == registers.size) {
            "No register legalization model for ${source.opcode}: ${registers.size} registers"
        }
        val prelude = ArrayList<Instruction>()
        val postlude = ArrayList<Instruction>()
        val temporaryRegisters = ArrayList<Int>(operands.size)
        var temporary = 0
        operands.forEachIndexed { index, operand ->
            temporaryRegisters += temporary
            if (operand.reads) prelude += moveInstruction(operand.category, temporary, registers[index])
            if (operand.writes) postlude += moveInstruction(operand.category, registers[index], temporary)
            temporary += operand.category.width
        }
        val operation = source.withRegisters(temporaryRegisters)
        return Expansion(prelude + operation + postlude, prelude.size)
    }

    private fun repairArguments(
        source: Instruction,
        registers: List<Int>,
        operands: List<RegisterOperand>,
    ): Expansion {
        check(operands.sumOf { it.category.width } == registers.size) {
            "Argument words for ${source.opcode} differ from its encoded register count"
        }
        val prelude = ArrayList<Instruction>(operands.size)
        val operationRegisters = ArrayList<Int>(registers.size)
        var word = 0
        for (operand in operands) {
            prelude += moveInstruction(operand.category, word, registers[word])
            repeat(operand.category.width) { operationRegisters += word + it }
            word += operand.category.width
        }
        val operation = source.withRegisters(operationRegisters)
        return Expansion(prelude + operation, prelude.size)
    }

    private fun rewriteBody(
        body: MethodBody,
        registerCount: Int,
        expansions: Map<LogicalCodePosition, Expansion>,
        debugRegister: (Int) -> Int,
    ): RewrittenBody {
        val pending = ArrayList<Instruction>(expansions.values.sumOf { it.instructions.size })
        val incomingIndexes = LinkedHashMap<LogicalCodePosition, Int>()
        val operationIndexes = LinkedHashMap<LogicalCodePosition, Int>()
        for (record in body.instructions) {
            val expansion = requireNotNull(expansions[record.position])
            incomingIndexes[record.position] = pending.size
            expansion.instructions.forEachIndexed { index, instruction ->
                if (index == expansion.operationIndex) operationIndexes[record.position] = pending.size
                pending += instruction
            }
        }
        val positions = LogicalCodePosition.forBody(pending.size + 1)
        val incomingPositions = incomingIndexes.mapValues { (_, index) -> positions[index] }.toMutableMap()
        incomingPositions[body.endPosition] = positions.last()
        val operationPositions = operationIndexes.mapValues { (_, index) -> positions[index] }

        val instructions = ArrayList<EncodedInstruction>(pending.size)
        val positionsByCodeAddress = LinkedHashMap<Int, LogicalCodePosition>(pending.size + 1)
        var codeAddress = 0
        pending.forEachIndexed { index, instruction ->
            val position = positions[index]
            positionsByCodeAddress[codeAddress] = position
            instructions += EncodedInstruction(position, codeAddress, instruction)
            codeAddress += instruction.codeUnits
        }
        positionsByCodeAddress[codeAddress] = positions.last()

        val controlTargets = body.controlTargets.map { (source, target) ->
            requireNotNull(operationPositions[source]) to requireNotNull(incomingPositions[target])
        }.toMap(LinkedHashMap())
        val switchCases = body.switchCases.map { (source, cases) ->
            requireNotNull(operationPositions[source]) to cases.map { case ->
                ImportedSwitchCase(case.key, requireNotNull(incomingPositions[case.target]))
            }
        }.toMap(LinkedHashMap())
        val tryRegions = body.tryRegions.map { region ->
            ImportedTryRegion(
                requireNotNull(incomingPositions[region.start]),
                requireNotNull(incomingPositions[region.end]),
                region.handlers.map { handler ->
                    ImportedExceptionHandler(
                        handler.exceptionType,
                        requireNotNull(incomingPositions[handler.target]),
                    )
                }.toPersistentList(),
            )
        }
        val debugItems = body.debugItems.map { item ->
            ImportedDebugItem(
                operationPositions[item.position] ?: requireNotNull(incomingPositions[item.position]),
                remapDebugRegister(item.item, debugRegister),
            )
        }
        val referenceOperands = body.referenceOperandsByPosition.map { (position, operands) ->
            requireNotNull(operationPositions[position]) to operands
        }.toMap(LinkedHashMap())
        return RewrittenBody(
            MethodBody.logical(
                registerCount,
                instructions,
                positionsByCodeAddress,
                controlTargets,
                switchCases,
                tryRegions,
                debugItems,
                referenceOperandsByPosition = referenceOperands,
            ),
            incomingPositions,
        )
    }

    private fun remapDebugRegister(item: DebugItem, register: (Int) -> Int): DebugItem = when (item) {
        is LineNumber -> ImmutableLineNumber(item.codeAddress, item.lineNumber)
        is StartLocal -> ImmutableStartLocal(
            item.codeAddress,
            register(item.register),
            item.name,
            item.type,
            item.signature,
        )
        is EndLocal -> ImmutableEndLocal(
            item.codeAddress,
            register(item.register),
            item.name,
            item.type,
            item.signature,
        )
        is RestartLocal -> ImmutableRestartLocal(
            item.codeAddress,
            register(item.register),
            item.name,
            item.type,
            item.signature,
        )
        is PrologueEnd -> ImmutablePrologueEnd(item.codeAddress)
        is EpilogueBegin -> ImmutableEpilogueBegin(item.codeAddress)
        is SetSourceFile -> ImmutableSetSourceFile(item.codeAddress, item.sourceFile)
        else -> error("Unsupported debug item type: ${item.javaClass.name}")
    }

    private fun moveInstruction(category: ValueCategory, destination: Int, source: Int): Instruction {
        val opcodes = when (category) {
            ValueCategory.NARROW -> Triple(Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16)
            ValueCategory.REFERENCE -> Triple(Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16)
            ValueCategory.WIDE -> Triple(Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16)
        }
        return when {
            destination <= U4_MAX && source <= U4_MAX -> ImmutableInstruction12x(opcodes.first, destination, source)
            destination <= U8_MAX -> ImmutableInstruction22x(opcodes.second, destination, source)
            else -> ImmutableInstruction32x(opcodes.third, destination, source)
        }
    }

    private fun logicalRegisterNumbers(record: EncodedInstruction, body: MethodBody): List<Int> =
        body.logicalRegistersByPosition[record.position]?.map { register ->
            when (val allocation = register.allocation) {
                is LogicalRegister.Allocation.Baseline -> allocation.register
                is LogicalRegister.Allocation.Scratch -> body.registerCount + allocation.offset
            }
        } ?: record.instruction.registerOperands()

    private fun registersAreContiguous(registers: List<Int>): Boolean =
        registers.zipWithNext().all { (left, right) -> right == left + 1 }

    private val REFERENCE_COMPARING_BRANCHES = setOf(Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_EQZ, Opcode.IF_NEZ)
    private val RANGE_OPCODES = mapOf(
        Opcode.FILLED_NEW_ARRAY to Opcode.FILLED_NEW_ARRAY_RANGE,
        Opcode.INVOKE_VIRTUAL to Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.INVOKE_SUPER to Opcode.INVOKE_SUPER_RANGE,
        Opcode.INVOKE_DIRECT to Opcode.INVOKE_DIRECT_RANGE,
        Opcode.INVOKE_STATIC to Opcode.INVOKE_STATIC_RANGE,
        Opcode.INVOKE_INTERFACE to Opcode.INVOKE_INTERFACE_RANGE,
        Opcode.EXECUTE_INLINE to Opcode.EXECUTE_INLINE_RANGE,
        Opcode.INVOKE_VIRTUAL_QUICK to Opcode.INVOKE_VIRTUAL_QUICK_RANGE,
        Opcode.INVOKE_SUPER_QUICK to Opcode.INVOKE_SUPER_QUICK_RANGE,
        Opcode.INVOKE_POLYMORPHIC to Opcode.INVOKE_POLYMORPHIC_RANGE,
        Opcode.INVOKE_CUSTOM to Opcode.INVOKE_CUSTOM_RANGE,
    )

    private const val U4_MAX = 15
    private const val U8_MAX = 255
}
