package org.cf.smalivm.core

import java.lang.reflect.Modifier
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
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction10t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction10x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11n
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction11x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction12x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20bc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction20t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21ih
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21lh
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21s
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22b
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22cs
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22s
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction22x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction23x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction30t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31i
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31t
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction32x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35mi
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35ms
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rmi
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rms
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction45cc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction4rcc
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction51l
import com.android.tools.smali.dexlib2.iface.reference.CallSiteReference
import com.android.tools.smali.dexlib2.iface.reference.MethodProtoReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEndLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEpilogueBegin
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableLineNumber
import com.android.tools.smali.dexlib2.immutable.debug.ImmutablePrologueEnd
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableRestartLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableSetSourceFile
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableStartLocal
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction12x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction20bc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction20t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21ih
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21lh
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22b
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22cs
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction23x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction30t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31i
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction32x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35mi
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35ms
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rmi
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction3rms
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction45cc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction4rcc
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction51l

internal sealed interface ScratchRegisterAllocation {
    data class Accepted(
        val body: MethodBody,
        val positionsAfterAllocation: Map<LogicalCodePosition, LogicalCodePosition>,
    ) : ScratchRegisterAllocation

    data class Rejected(val reason: MethodEditRejectionReason) : ScratchRegisterAllocation
}

/** Selects physical register encoding after all transaction reservations are known. */
internal object ScratchRegisterAllocator {
    private enum class Access {
        READ,
        WRITE,
        READ_WRITE,
    }

    private enum class Kind(val width: Int) {
        NARROW(1),
        OBJECT(1),
        WIDE(2),
    }

    private enum class RegisterCategory {
        ZERO,
        NARROW,
        OBJECT,
        WIDE,
        CONFLICT,
    }

    private data class Operand(val access: Access, val kind: Kind)

    private data class Expansion(
        val instructions: List<Instruction>,
        val operationIndex: Int,
    )

    private data class RewrittenBody(
        val body: MethodBody,
        val positionsAfterAllocation: Map<LogicalCodePosition, LogicalCodePosition>,
    )

    private data class PendingRecord(val instruction: Instruction)

    fun allocate(
        body: MethodBody,
        method: Method,
        scratchWordCount: Int,
    ): ScratchRegisterAllocation {
        require(scratchWordCount >= 0)
        if (scratchWordCount == 0 && body.logicalRegistersByPosition.isEmpty()) {
            return ScratchRegisterAllocation.Accepted(body, body.positionsByCodeAddress.values.associateWith { it })
        }
        val registerKinds = registerKindHints(body, method, scratchWordCount)
        val branchKinds = ambiguousBranchKinds(body, method)
        validateScratchWideOperands(body, scratchWordCount, registerKinds, branchKinds)?.let { return it }
        var hiddenWordCount = 0
        while (true) {
            val addedWordCount = scratchWordCount.toLong() + hiddenWordCount
            if (body.registerCount.toLong() + addedWordCount > MAX_REGISTER_COUNT) {
                return registerOverflow(body.registerCount.toLong() + addedWordCount)
            }
            var requiredHiddenWords = hiddenWordCount
            for (record in body.instructions) {
                val registers = mappedRegisters(record, body, scratchWordCount, hiddenWordCount)
                if (directInstruction(record.instruction, registers) == null) {
                    requiredHiddenWords = maxOf(
                        requiredHiddenWords,
                        repairWordCount(record, body, registerKinds, branchKinds),
                    )
                }
            }
            if (requiredHiddenWords == hiddenWordCount) break
            hiddenWordCount = requiredHiddenWords
        }

        val addedWordCount = scratchWordCount + hiddenWordCount
        val expansions = LinkedHashMap<LogicalCodePosition, Expansion>(body.instructions.size)
        for (record in body.instructions) {
            val registers = mappedRegisters(record, body, scratchWordCount, hiddenWordCount)
            val direct = directInstruction(record.instruction, registers)
            val expansion = if (direct != null) {
                Expansion(listOf(direct), operationIndex = 0)
            } else {
                repairInstruction(
                    record.instruction,
                    registers,
                    if (record.instruction is FiveRegisterInstruction ||
                        record.instruction is RegisterRangeInstruction
                    ) {
                        emptyList()
                    } else {
                        operandKinds(record, body, registerKinds, branchKinds)
                    },
                )
            }
            expansions[record.position] = expansion
        }
        val rewritten = rewriteBody(
            body,
            registerCount = body.registerCount + addedWordCount,
            expansions = expansions,
            debugRegister = { register -> register + addedWordCount },
        )
        return ScratchRegisterAllocation.Accepted(rewritten.body, rewritten.positionsAfterAllocation)
    }

    private fun registerOverflow(registerCount: Long): ScratchRegisterAllocation.Rejected =
        ScratchRegisterAllocation.Rejected(
            MethodEditRejectionReason.RegisterOverflow(minOf(registerCount - 1, Int.MAX_VALUE.toLong()).toInt()),
        )

    private fun validateScratchWideOperands(
        body: MethodBody,
        scratchWordCount: Int,
        registerKinds: Map<Int, Kind>,
        branchKinds: Map<LogicalCodePosition, Map<Int, Kind>>,
    ): ScratchRegisterAllocation.Rejected? {
        for (record in body.instructions) {
            val logicalRegisters = body.logicalRegistersByPosition[record.position] ?: continue
            val operands = operandKinds(record, body, registerKinds, branchKinds)
            for (index in logicalRegisters.indices) {
                if (operands.getOrNull(index)?.kind != Kind.WIDE) continue
                val allocation = logicalRegisters[index].allocation
                if (allocation is LogicalRegister.Allocation.Scratch && allocation.offset + 1 >= scratchWordCount) {
                    return ScratchRegisterAllocation.Rejected(
                        MethodEditRejectionReason.InvalidInstructionTemplate(
                            "A wide scratch operand must fit completely inside its reserved scratch range",
                        ),
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
            ?: return encodedRegisters(record.instruction).map { it + scratchWordCount + hiddenWordCount }
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
            return rebuildInstruction(source, registers)
        } catch (_: IllegalArgumentException) {
            // The source format cannot encode the selected physical registers. Try its legal wider form.
        }
        if (source.opcode in MOVE_OPCODES && registers.size == 2) {
            return moveInstruction(moveKind(source.opcode), registers[0], registers[1])
        }
        val singleRegister = registers.singleOrNull()
        if (source.opcode == Opcode.CONST_4 && singleRegister != null && singleRegister in 0..U8_MAX) {
            return ImmutableInstruction21s(Opcode.CONST_16, singleRegister, (source as Instruction11n).narrowLiteral)
        }
        if (source.opcode.name.endsWith("/2addr") && registers.all { it in 0..U8_MAX }) {
            val opcode = Opcode.valueOf(source.opcode.name.removeSuffix("/2addr").replace('-', '_').uppercase())
            return ImmutableInstruction23x(opcode, registers[0], registers[0], registers[1])
        }
        val rangeOpcode = RANGE_OPCODES[source.opcode]
        if (rangeOpcode != null && registersAreContiguous(registers)) {
            return rangeInstruction(source, rangeOpcode, registers)
        }
        return null
    }

    private fun repairWordCount(
        record: EncodedInstruction,
        body: MethodBody,
        registerKinds: Map<Int, Kind>,
        branchKinds: Map<LogicalCodePosition, Map<Int, Kind>>,
    ): Int = record.instruction.let { instruction ->
        if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) {
            argumentOperands(instruction).sumOf { it.kind.width }
        } else {
            operandKinds(record, body, registerKinds, branchKinds).sumOf { it.kind.width }
        }
    }

    private fun repairInstruction(
        source: Instruction,
        registers: List<Int>,
        modeledOperands: List<Operand>,
    ): Expansion {
        if (source is FiveRegisterInstruction || source is RegisterRangeInstruction) {
            return repairArguments(source, registers)
        }
        val operands = modeledOperands
        check(operands.size == registers.size) {
            "No register legalization model for ${source.opcode}: ${registers.size} registers"
        }
        val prelude = ArrayList<Instruction>()
        val postlude = ArrayList<Instruction>()
        val temporaryRegisters = ArrayList<Int>(operands.size)
        var temporary = 0
        operands.forEachIndexed { index, operand ->
            temporaryRegisters += temporary
            if (operand.access != Access.WRITE) {
                prelude += moveInstruction(operand.kind, temporary, registers[index])
            }
            if (operand.access != Access.READ) {
                postlude += moveInstruction(operand.kind, registers[index], temporary)
            }
            temporary += operand.kind.width
        }
        val operation = rebuildInstruction(source, temporaryRegisters)
        return Expansion(prelude + operation + postlude, prelude.size)
    }

    private fun repairArguments(source: Instruction, registers: List<Int>): Expansion {
        val operands = argumentOperands(source)
        val expectedWords = operands.sumOf { it.kind.width }
        check(expectedWords == registers.size) {
            "Argument words for ${source.opcode} differ from its encoded register count"
        }
        val prelude = ArrayList<Instruction>(operands.size)
        val operationRegisters = ArrayList<Int>(registers.size)
        var sourceWord = 0
        var temporary = 0
        for (operand in operands) {
            prelude += moveInstruction(operand.kind, temporary, registers[sourceWord])
            repeat(operand.kind.width) { word -> operationRegisters += temporary + word }
            sourceWord += operand.kind.width
            temporary += operand.kind.width
        }
        val operation = rebuildInstruction(source, operationRegisters)
        return Expansion(prelude + operation, prelude.size)
    }

    private fun rewriteBody(
        body: MethodBody,
        registerCount: Int,
        expansions: Map<LogicalCodePosition, Expansion>,
        debugRegister: (Int) -> Int,
    ): RewrittenBody {
        val pending = ArrayList<PendingRecord>(expansions.values.sumOf { it.instructions.size })
        val incomingIndexes = LinkedHashMap<LogicalCodePosition, Int>()
        val operationIndexes = LinkedHashMap<LogicalCodePosition, Int>()
        for (record in body.instructions) {
            val expansion = requireNotNull(expansions[record.position])
            incomingIndexes[record.position] = pending.size
            expansion.instructions.forEachIndexed { index, instruction ->
                if (index == expansion.operationIndex) operationIndexes[record.position] = pending.size
                pending += PendingRecord(instruction)
            }
        }
        val positions = LogicalCodePosition.forBody(pending.size + 1)
        val oldEnd = requireNotNull(
            body.positionsByCodeAddress.maxByOrNull(Map.Entry<Int, LogicalCodePosition>::key)?.value,
        )
        val incomingPositions = incomingIndexes.mapValues { (_, index) -> positions[index] }.toMutableMap()
        incomingPositions[oldEnd] = positions.last()
        val operationPositions = operationIndexes.mapValues { (_, index) -> positions[index] }

        val instructions = ArrayList<EncodedInstruction>(pending.size)
        val positionsByCodeAddress = LinkedHashMap<Int, LogicalCodePosition>(pending.size + 1)
        var codeAddress = 0
        pending.forEachIndexed { index, record ->
            val position = positions[index]
            positionsByCodeAddress[codeAddress] = position
            instructions += EncodedInstruction(position, codeAddress, record.instruction)
            codeAddress += record.instruction.codeUnits
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

    private fun encodedRegisters(instruction: Instruction): List<Int> = when (instruction) {
        is FiveRegisterInstruction -> listOf(
            instruction.registerC,
            instruction.registerD,
            instruction.registerE,
            instruction.registerF,
            instruction.registerG,
        ).take(instruction.registerCount)
        is RegisterRangeInstruction -> List(instruction.registerCount) { instruction.startRegister + it }
        is ThreeRegisterInstruction -> listOf(instruction.registerA, instruction.registerB, instruction.registerC)
        is TwoRegisterInstruction -> listOf(instruction.registerA, instruction.registerB)
        is OneRegisterInstruction -> listOf(instruction.registerA)
        else -> emptyList()
    }

    @Suppress("ComplexMethod", "LongMethod")
    private fun rebuildInstruction(source: Instruction, registers: List<Int>): Instruction = when (source) {
        is Instruction10x -> ImmutableInstruction10x(source.opcode)
        is Instruction10t -> ImmutableInstruction10t(source.opcode, source.codeOffset)
        is Instruction11n -> ImmutableInstruction11n(source.opcode, registers[0], source.narrowLiteral)
        is Instruction11x -> ImmutableInstruction11x(source.opcode, registers[0])
        is Instruction12x -> ImmutableInstruction12x(source.opcode, registers[0], registers[1])
        is Instruction20bc -> ImmutableInstruction20bc(source.opcode, source.verificationError, source.reference)
        is Instruction20t -> ImmutableInstruction20t(source.opcode, source.codeOffset)
        is Instruction21c -> ImmutableInstruction21c(source.opcode, registers[0], source.reference)
        is Instruction21ih -> ImmutableInstruction21ih(source.opcode, registers[0], source.narrowLiteral)
        is Instruction21lh -> ImmutableInstruction21lh(source.opcode, registers[0], source.wideLiteral)
        is Instruction21s -> ImmutableInstruction21s(source.opcode, registers[0], source.narrowLiteral)
        is Instruction21t -> ImmutableInstruction21t(source.opcode, registers[0], source.codeOffset)
        is Instruction22b -> ImmutableInstruction22b(
            source.opcode,
            registers[0],
            registers[1],
            source.narrowLiteral,
        )
        is Instruction22c -> ImmutableInstruction22c(source.opcode, registers[0], registers[1], source.reference)
        is Instruction22cs -> ImmutableInstruction22cs(source.opcode, registers[0], registers[1], source.fieldOffset)
        is Instruction22s -> ImmutableInstruction22s(
            source.opcode,
            registers[0],
            registers[1],
            source.narrowLiteral,
        )
        is Instruction22t -> ImmutableInstruction22t(source.opcode, registers[0], registers[1], source.codeOffset)
        is Instruction22x -> ImmutableInstruction22x(source.opcode, registers[0], registers[1])
        is Instruction23x -> ImmutableInstruction23x(source.opcode, registers[0], registers[1], registers[2])
        is Instruction30t -> ImmutableInstruction30t(source.opcode, source.codeOffset)
        is Instruction31c -> ImmutableInstruction31c(source.opcode, registers[0], source.reference)
        is Instruction31i -> ImmutableInstruction31i(source.opcode, registers[0], source.narrowLiteral)
        is Instruction31t -> ImmutableInstruction31t(source.opcode, registers[0], source.codeOffset)
        is Instruction32x -> ImmutableInstruction32x(source.opcode, registers[0], registers[1])
        is Instruction35c -> ImmutableInstruction35c(
            source.opcode,
            source.registerCount,
            registers.getOrElse(0) { 0 },
            registers.getOrElse(1) { 0 },
            registers.getOrElse(2) { 0 },
            registers.getOrElse(3) { 0 },
            registers.getOrElse(4) { 0 },
            source.reference,
        )
        is Instruction35mi -> ImmutableInstruction35mi(
            source.opcode,
            source.registerCount,
            registers.getOrElse(0) { 0 },
            registers.getOrElse(1) { 0 },
            registers.getOrElse(2) { 0 },
            registers.getOrElse(3) { 0 },
            registers.getOrElse(4) { 0 },
            source.inlineIndex,
        )
        is Instruction35ms -> ImmutableInstruction35ms(
            source.opcode,
            source.registerCount,
            registers.getOrElse(0) { 0 },
            registers.getOrElse(1) { 0 },
            registers.getOrElse(2) { 0 },
            registers.getOrElse(3) { 0 },
            registers.getOrElse(4) { 0 },
            source.vtableIndex,
        )
        is Instruction3rc -> ImmutableInstruction3rc(
            source.opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            source.reference,
        )
        is Instruction3rmi -> ImmutableInstruction3rmi(
            source.opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            source.inlineIndex,
        )
        is Instruction3rms -> ImmutableInstruction3rms(
            source.opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            source.vtableIndex,
        )
        is Instruction45cc -> ImmutableInstruction45cc(
            source.opcode,
            source.registerCount,
            registers.getOrElse(0) { 0 },
            registers.getOrElse(1) { 0 },
            registers.getOrElse(2) { 0 },
            registers.getOrElse(3) { 0 },
            registers.getOrElse(4) { 0 },
            source.reference,
            source.reference2,
        )
        is Instruction4rcc -> ImmutableInstruction4rcc(
            source.opcode,
            registers.firstOrNull() ?: source.startRegister,
            source.registerCount,
            source.reference,
            source.reference2,
        )
        is Instruction51l -> ImmutableInstruction51l(source.opcode, registers[0], source.wideLiteral)
        else -> ImmutableInstruction.of(source)
    }

    private fun rangeInstruction(source: Instruction, opcode: Opcode, registers: List<Int>): Instruction = when (source) {
        is Instruction35c -> ImmutableInstruction3rc(opcode, registers.firstOrNull() ?: 0, registers.size, source.reference)
        is Instruction35mi -> ImmutableInstruction3rmi(
            opcode,
            registers.firstOrNull() ?: 0,
            registers.size,
            source.inlineIndex,
        )
        is Instruction35ms -> ImmutableInstruction3rms(
            opcode,
            registers.firstOrNull() ?: 0,
            registers.size,
            source.vtableIndex,
        )
        is Instruction45cc -> ImmutableInstruction4rcc(
            opcode,
            registers.firstOrNull() ?: 0,
            registers.size,
            source.reference,
            source.reference2,
        )
        else -> error("No range form for ${source.opcode}")
    }

    private fun moveInstruction(kind: Kind, destination: Int, source: Int): Instruction {
        val opcodes = when (kind) {
            Kind.NARROW -> Triple(Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16)
            Kind.OBJECT -> Triple(Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16)
            Kind.WIDE -> Triple(Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16)
        }
        return when {
            destination <= U4_MAX && source <= U4_MAX -> ImmutableInstruction12x(opcodes.first, destination, source)
            destination <= U8_MAX -> ImmutableInstruction22x(opcodes.second, destination, source)
            else -> ImmutableInstruction32x(opcodes.third, destination, source)
        }
    }

    private fun moveKind(opcode: Opcode): Kind = when {
        opcode.name.contains("object") -> Kind.OBJECT
        opcode.name.contains("wide") -> Kind.WIDE
        else -> Kind.NARROW
    }

    private fun operands(instruction: Instruction): List<Operand> = when (instruction.opcode) {
        Opcode.MOVE_RESULT -> listOf(write(Kind.NARROW))
        Opcode.MOVE_RESULT_WIDE -> listOf(write(Kind.WIDE))
        Opcode.MOVE_RESULT_OBJECT, Opcode.MOVE_EXCEPTION -> listOf(write(Kind.OBJECT))
        Opcode.RETURN -> listOf(read(Kind.NARROW))
        Opcode.RETURN_WIDE -> listOf(read(Kind.WIDE))
        Opcode.RETURN_OBJECT, Opcode.MONITOR_ENTER, Opcode.MONITOR_EXIT, Opcode.THROW -> listOf(read(Kind.OBJECT))
        Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16 -> listOf(write(Kind.NARROW))
        Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE, Opcode.CONST_WIDE_HIGH16 ->
            listOf(write(Kind.WIDE))
        Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO, Opcode.CONST_CLASS,
        Opcode.NEW_INSTANCE, Opcode.CONST_METHOD_HANDLE, Opcode.CONST_METHOD_TYPE -> listOf(write(Kind.OBJECT))
        Opcode.CHECK_CAST -> listOf(readWrite(Kind.OBJECT))
        Opcode.INSTANCE_OF -> listOf(write(Kind.NARROW), read(Kind.OBJECT))
        Opcode.ARRAY_LENGTH -> listOf(write(Kind.NARROW), read(Kind.OBJECT))
        Opcode.NEW_ARRAY -> listOf(write(Kind.OBJECT), read(Kind.NARROW))
        Opcode.FILL_ARRAY_DATA -> listOf(read(Kind.OBJECT))
        Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH -> listOf(read(Kind.NARROW))
        Opcode.IF_EQ, Opcode.IF_NE -> listOf(read(Kind.NARROW), read(Kind.NARROW))
        Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE -> listOf(read(Kind.NARROW), read(Kind.NARROW))
        Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ ->
            listOf(read(Kind.NARROW))
        in AGET_OPCODES -> listOf(write(arrayValueKind(instruction.opcode)), read(Kind.OBJECT), read(Kind.NARROW))
        in APUT_OPCODES -> listOf(read(arrayValueKind(instruction.opcode)), read(Kind.OBJECT), read(Kind.NARROW))
        in IGET_OPCODES -> listOf(write(fieldValueKind(instruction.opcode)), read(Kind.OBJECT))
        in IPUT_OPCODES -> listOf(read(fieldValueKind(instruction.opcode)), read(Kind.OBJECT))
        in SGET_OPCODES -> listOf(write(fieldValueKind(instruction.opcode)))
        in SPUT_OPCODES -> listOf(read(fieldValueKind(instruction.opcode)))
        Opcode.CMPL_FLOAT, Opcode.CMPG_FLOAT ->
            listOf(write(Kind.NARROW), read(Kind.NARROW), read(Kind.NARROW))
        Opcode.CMPL_DOUBLE, Opcode.CMPG_DOUBLE, Opcode.CMP_LONG ->
            listOf(write(Kind.NARROW), read(Kind.WIDE), read(Kind.WIDE))
        in UNARY_OPCODES -> unaryOperands(instruction.opcode)
        in BINARY_OPCODES -> binaryOperands(instruction.opcode)
        in TWO_ADDRESS_OPCODES -> twoAddressOperands(instruction.opcode)
        in LITERAL_OPCODES -> listOf(write(Kind.NARROW), read(Kind.NARROW))
        in MOVE_OPCODES -> listOf(write(moveKind(instruction.opcode)), read(moveKind(instruction.opcode)))
        else -> error("No register legalization model for ${instruction.opcode}")
    }

    private fun argumentOperands(instruction: Instruction): List<Operand> {
        if (instruction.opcode == Opcode.FILLED_NEW_ARRAY || instruction.opcode == Opcode.FILLED_NEW_ARRAY_RANGE) {
            val descriptor = (instruction as com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)
                .reference.let { it as TypeReference }.type
            val kind = kindOfDescriptor(descriptor.removePrefix("["))
            return List(encodedRegisters(instruction).size) { read(kind) }
        }
        val reference = (instruction as? com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction)?.reference
        if (instruction.opcode == Opcode.INVOKE_POLYMORPHIC ||
            instruction.opcode == Opcode.INVOKE_POLYMORPHIC_RANGE
        ) {
            val prototype = (instruction as DualReferenceInstruction).reference2 as MethodProtoReference
            return buildList {
                add(read(Kind.OBJECT))
                prototype.parameterTypes.forEach { descriptor -> add(read(kindOfDescriptor(descriptor.toString()))) }
            }
        }
        if (instruction.opcode == Opcode.INVOKE_CUSTOM || instruction.opcode == Opcode.INVOKE_CUSTOM_RANGE) {
            val prototype = (reference as CallSiteReference).methodProto
            return prototype.parameterTypes.map { descriptor -> read(kindOfDescriptor(descriptor.toString())) }
        }
        if (reference is MethodReference) {
            return buildList {
                if (!instruction.opcode.name.contains("static")) add(read(Kind.OBJECT))
                reference.parameterTypes.forEach { descriptor -> add(read(kindOfDescriptor(descriptor.toString()))) }
            }
        }
        return List(encodedRegisters(instruction).size) { read(Kind.NARROW) }
    }

    private fun registerKindHints(
        body: MethodBody,
        method: Method,
        scratchWordCount: Int,
    ): Map<Int, Kind> {
        val hints = HashMap<Int, Kind>()
        val parameterKinds = buildList {
            if (!Modifier.isStatic(method.accessFlags)) add(Kind.OBJECT)
            method.parameterTypes.mapTo(this) { descriptor -> kindOfDescriptor(descriptor.toString()) }
        }
        var parameterRegister = body.registerCount - parameterKinds.sumOf(Kind::width)
        for (kind in parameterKinds) {
            hints[parameterRegister] = kind
            if (kind == Kind.WIDE) hints[parameterRegister + 1] = Kind.WIDE
            parameterRegister += kind.width
        }
        for (record in body.instructions) {
            val registers = logicalRegisterNumbers(record, body)
            if (registers.isEmpty()) continue
            val instruction = record.instruction
            val modeled = if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) {
                argumentOperands(instruction)
            } else {
                operands(instruction)
            }
            if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) {
                var word = 0
                for (operand in modeled) {
                    if (operand.kind != Kind.NARROW && word < registers.size) hints[registers[word]] = operand.kind
                    word += operand.kind.width
                }
            } else {
                modeled.forEachIndexed { index, operand ->
                    if (operand.kind != Kind.NARROW && index < registers.size) hints[registers[index]] = operand.kind
                }
            }
        }
        repeat(scratchWordCount) { hints.putIfAbsent(body.registerCount + it, Kind.NARROW) }
        return hints
    }

    private fun operandKinds(
        record: EncodedInstruction,
        body: MethodBody,
        registerKinds: Map<Int, Kind>,
        branchKinds: Map<LogicalCodePosition, Map<Int, Kind>>,
    ): List<Operand> {
        val base = if (record.instruction is FiveRegisterInstruction ||
            record.instruction is RegisterRangeInstruction
        ) {
            argumentOperands(record.instruction)
        } else {
            operands(record.instruction)
        }
        if (record.instruction.opcode !in AMBIGUOUS_BRANCH_OPCODES) return base
        val registers = logicalRegisterNumbers(record, body)
        return base.mapIndexed { index, operand ->
            operand.copy(
                kind = branchKinds[record.position]?.get(registers[index])
                    ?: nearestDefinedKind(body, record.position, registers[index])
                    ?: registerKinds[registers[index]]
                    ?: Kind.NARROW,
            )
        }
    }

    private fun nearestDefinedKind(
        body: MethodBody,
        position: LogicalCodePosition,
        register: Int,
    ): Kind? {
        val index = body.instructions.indexOfFirst { it.position == position }
        for (record in body.instructions.subList(0, index).asReversed()) {
            val registers = logicalRegisterNumbers(record, body)
            if (registers.isEmpty()) continue
            val instruction = record.instruction
            if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) continue
            val modeled = operands(instruction)
            modeled.forEachIndexed { operandIndex, operand ->
                if (operandIndex < registers.size &&
                    registers[operandIndex] == register &&
                    operand.access != Access.READ
                ) {
                    return operand.kind
                }
            }
        }
        return null
    }

    private fun ambiguousBranchKinds(
        body: MethodBody,
        method: Method,
    ): Map<LogicalCodePosition, Map<Int, Kind>> {
        val ambiguousRegisters = body.instructions
            .filter { it.instruction.opcode in AMBIGUOUS_BRANCH_OPCODES }
            .flatMapTo(linkedSetOf()) { logicalRegisterNumbers(it, body) }
        if (ambiguousRegisters.isEmpty()) return emptyMap()

        val records = body.instructions
        val indexByPosition = records.mapIndexed { index, record -> record.position to index }.toMap()
        val endPosition = requireNotNull(
            body.positionsByCodeAddress.maxByOrNull(Map.Entry<Int, LogicalCodePosition>::key)?.value,
        )
        val states = HashMap<LogicalCodePosition, MutableMap<Int, RegisterCategory>>()
        val entryState = parameterCategories(body, method, ambiguousRegisters)
        val first = records.firstOrNull()?.position ?: return emptyMap()
        states[first] = entryState.toMutableMap()
        val work = ArrayDeque<LogicalCodePosition>()
        work += first

        while (work.isNotEmpty()) {
            val position = work.removeFirst()
            val index = requireNotNull(indexByPosition[position])
            val record = records[index]
            val before = states.getValue(position)
            val after = transferCategories(before, record, body, ambiguousRegisters)
            val successors = categorySuccessors(body, records, index)
            for (successor in successors) {
                if (successor !in indexByPosition) continue
                val current = states[successor]
                if (current == null) {
                    states[successor] = after.toMutableMap()
                    work += successor
                } else if (mergeCategories(current, after, ambiguousRegisters)) {
                    work += successor
                }
            }
            if (record.instruction.opcode.canThrow()) {
                for (handler in categoryHandlers(body, records, indexByPosition, index, endPosition)) {
                    val current = states[handler]
                    if (current == null) {
                        states[handler] = before.toMutableMap()
                        work += handler
                    } else if (mergeCategories(current, before, ambiguousRegisters)) {
                        work += handler
                    }
                }
            }
        }

        return records.filter { it.instruction.opcode in AMBIGUOUS_BRANCH_OPCODES }.associate { record ->
            val before = states[record.position].orEmpty()
            record.position to logicalRegisterNumbers(record, body).mapNotNull { register ->
                when (before[register]) {
                    RegisterCategory.OBJECT -> register to Kind.OBJECT
                    RegisterCategory.NARROW, RegisterCategory.ZERO -> register to Kind.NARROW
                    else -> null
                }
            }.toMap()
        }
    }

    private fun parameterCategories(
        body: MethodBody,
        method: Method,
        trackedRegisters: Set<Int>,
    ): Map<Int, RegisterCategory> {
        val kinds = buildList {
            if (!Modifier.isStatic(method.accessFlags)) add(Kind.OBJECT)
            method.parameterTypes.mapTo(this) { descriptor -> kindOfDescriptor(descriptor.toString()) }
        }
        var register = body.registerCount - kinds.sumOf(Kind::width)
        return buildMap {
            for (kind in kinds) {
                if (register in trackedRegisters) put(register, kind.category())
                register += kind.width
            }
        }
    }

    private fun transferCategories(
        before: Map<Int, RegisterCategory>,
        record: EncodedInstruction,
        body: MethodBody,
        trackedRegisters: Set<Int>,
    ): Map<Int, RegisterCategory> {
        val registers = logicalRegisterNumbers(record, body)
        if (registers.isEmpty() ||
            record.instruction is FiveRegisterInstruction ||
            record.instruction is RegisterRangeInstruction
        ) {
            return before
        }
        val modeled = operands(record.instruction)
        val after = before.toMutableMap()
        modeled.forEachIndexed { index, operand ->
            if (operand.access == Access.READ || index >= registers.size || registers[index] !in trackedRegisters) {
                return@forEachIndexed
            }
            val category = when {
                index == 0 && record.instruction.opcode in MOVE_OPCODES -> when (moveKind(record.instruction.opcode)) {
                    Kind.OBJECT -> RegisterCategory.OBJECT
                    Kind.WIDE -> RegisterCategory.WIDE
                    Kind.NARROW -> before[registers[1]] ?: RegisterCategory.NARROW
                }
                record.instruction.opcode in CONST_NARROW_OPCODES &&
                    (record.instruction as NarrowLiteralInstruction).narrowLiteral == 0 -> RegisterCategory.ZERO
                else -> operand.kind.category()
            }
            after[registers[index]] = category
        }
        return after
    }

    private fun categorySuccessors(
        body: MethodBody,
        records: List<EncodedInstruction>,
        index: Int,
    ): Set<LogicalCodePosition> = buildSet {
        val record = records[index]
        val opcode = record.instruction.opcode
        val next = records.getOrNull(index + 1)?.position
        when {
            opcode in GOTO_OPCODES -> body.controlTargets[record.position]?.let(::add)
            opcode in CONDITIONAL_OPCODES -> {
                body.controlTargets[record.position]?.let(::add)
                next?.let(::add)
            }
            opcode in SWITCH_OPCODES -> {
                body.switchCases[record.position].orEmpty().mapTo(this) { it.target }
                next?.let(::add)
            }
            opcode.canContinue() -> next?.let(::add)
        }
    }

    private fun categoryHandlers(
        body: MethodBody,
        records: List<EncodedInstruction>,
        indexByPosition: Map<LogicalCodePosition, Int>,
        index: Int,
        endPosition: LogicalCodePosition,
    ): Set<LogicalCodePosition> = body.tryRegions.filter { region ->
        val start = requireNotNull(indexByPosition[region.start])
        val end = if (region.end == endPosition) records.size else requireNotNull(indexByPosition[region.end])
        index in start until end
    }.flatMapTo(linkedSetOf()) { region -> region.handlers.map(ImportedExceptionHandler::target) }

    private fun mergeCategories(
        current: MutableMap<Int, RegisterCategory>,
        incoming: Map<Int, RegisterCategory>,
        trackedRegisters: Set<Int>,
    ): Boolean {
        var changed = false
        for (register in trackedRegisters) {
            val joined = joinCategory(current[register], incoming[register])
            if (joined != current[register]) {
                if (joined == null) current.remove(register) else current[register] = joined
                changed = true
            }
        }
        return changed
    }

    private fun joinCategory(
        left: RegisterCategory?,
        right: RegisterCategory?,
    ): RegisterCategory? = when {
        left == right -> left
        left == null || right == null -> RegisterCategory.CONFLICT
        left == RegisterCategory.CONFLICT || right == RegisterCategory.CONFLICT -> RegisterCategory.CONFLICT
        left == RegisterCategory.ZERO && right == RegisterCategory.OBJECT ||
            right == RegisterCategory.ZERO && left == RegisterCategory.OBJECT -> RegisterCategory.OBJECT
        left == RegisterCategory.ZERO && right == RegisterCategory.NARROW ||
            right == RegisterCategory.ZERO && left == RegisterCategory.NARROW -> RegisterCategory.NARROW
        else -> RegisterCategory.CONFLICT
    }

    private fun Kind.category(): RegisterCategory = when (this) {
        Kind.NARROW -> RegisterCategory.NARROW
        Kind.OBJECT -> RegisterCategory.OBJECT
        Kind.WIDE -> RegisterCategory.WIDE
    }

    private fun logicalRegisterNumbers(record: EncodedInstruction, body: MethodBody): List<Int> =
        body.logicalRegistersByPosition[record.position]?.map { register ->
            when (val allocation = register.allocation) {
                is LogicalRegister.Allocation.Baseline -> allocation.register
                is LogicalRegister.Allocation.Scratch -> body.registerCount + allocation.offset
            }
        } ?: encodedRegisters(record.instruction)

    private fun unaryOperands(opcode: Opcode): List<Operand> {
        val (destination, source) = when (opcode) {
            Opcode.NEG_LONG, Opcode.NOT_LONG, Opcode.NEG_DOUBLE -> Kind.WIDE to Kind.WIDE
            Opcode.INT_TO_LONG, Opcode.INT_TO_DOUBLE, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE ->
                Kind.WIDE to Kind.NARROW
            Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_FLOAT ->
                Kind.NARROW to Kind.WIDE
            Opcode.LONG_TO_DOUBLE, Opcode.DOUBLE_TO_LONG -> Kind.WIDE to Kind.WIDE
            else -> Kind.NARROW to Kind.NARROW
        }
        return listOf(write(destination), read(source))
    }

    private fun binaryOperands(opcode: Opcode): List<Operand> {
        val destination = when {
            opcode.name.contains("long") || opcode.name.contains("double") -> Kind.WIDE
            else -> Kind.NARROW
        }
        val right = when {
            opcode.name.startsWith("shl-long") || opcode.name.startsWith("shr-long") ||
                opcode.name.startsWith("ushr-long") -> Kind.NARROW
            else -> destination
        }
        return listOf(write(destination), read(destination), read(right))
    }

    private fun twoAddressOperands(opcode: Opcode): List<Operand> {
        val kind = if (opcode.name.contains("long") || opcode.name.contains("double")) Kind.WIDE else Kind.NARROW
        val right = if (
            opcode.name.startsWith("shl-long") || opcode.name.startsWith("shr-long") ||
            opcode.name.startsWith("ushr-long")
        ) {
            Kind.NARROW
        } else {
            kind
        }
        return listOf(readWrite(kind), read(right))
    }

    private fun fieldValueKind(opcode: Opcode): Kind = when {
        opcode.name.contains("object") -> Kind.OBJECT
        opcode.name.contains("wide") -> Kind.WIDE
        else -> Kind.NARROW
    }

    private fun arrayValueKind(opcode: Opcode): Kind = fieldValueKind(opcode)

    private fun kindOfDescriptor(descriptor: String): Kind = when (descriptor.firstOrNull()) {
        'J', 'D' -> Kind.WIDE
        'L', '[' -> Kind.OBJECT
        else -> Kind.NARROW
    }

    private fun read(kind: Kind): Operand = Operand(Access.READ, kind)
    private fun write(kind: Kind): Operand = Operand(Access.WRITE, kind)
    private fun readWrite(kind: Kind): Operand = Operand(Access.READ_WRITE, kind)

    private fun registersAreContiguous(registers: List<Int>): Boolean =
        registers.zipWithNext().all { (left, right) -> right == left + 1 }

    private val MOVE_OPCODES = setOf(
        Opcode.MOVE,
        Opcode.MOVE_FROM16,
        Opcode.MOVE_16,
        Opcode.MOVE_WIDE,
        Opcode.MOVE_WIDE_FROM16,
        Opcode.MOVE_WIDE_16,
        Opcode.MOVE_OBJECT,
        Opcode.MOVE_OBJECT_FROM16,
        Opcode.MOVE_OBJECT_16,
    )
    private val AMBIGUOUS_BRANCH_OPCODES = setOf(Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_EQZ, Opcode.IF_NEZ)
    private val CONDITIONAL_OPCODES = AMBIGUOUS_BRANCH_OPCODES + setOf(
        Opcode.IF_LT,
        Opcode.IF_GE,
        Opcode.IF_GT,
        Opcode.IF_LE,
        Opcode.IF_LTZ,
        Opcode.IF_GEZ,
        Opcode.IF_GTZ,
        Opcode.IF_LEZ,
    )
    private val GOTO_OPCODES = setOf(Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32)
    private val SWITCH_OPCODES = setOf(Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
    private val CONST_NARROW_OPCODES = setOf(Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16)
    private val AGET_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("aget") }
    private val APUT_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("aput") }
    private val IGET_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("iget") }
    private val IPUT_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("iput") }
    private val SGET_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("sget") }
    private val SPUT_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.startsWith("sput") }
    private val UNARY_OPCODES = setOf(
        Opcode.NEG_INT, Opcode.NOT_INT, Opcode.NEG_LONG, Opcode.NOT_LONG, Opcode.NEG_FLOAT, Opcode.NEG_DOUBLE,
        Opcode.INT_TO_LONG, Opcode.INT_TO_FLOAT, Opcode.INT_TO_DOUBLE, Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT,
        Opcode.LONG_TO_DOUBLE, Opcode.FLOAT_TO_INT, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE, Opcode.DOUBLE_TO_INT,
        Opcode.DOUBLE_TO_LONG, Opcode.DOUBLE_TO_FLOAT, Opcode.INT_TO_BYTE, Opcode.INT_TO_CHAR, Opcode.INT_TO_SHORT,
    )
    private val BINARY_PREFIXES = listOf(
        "add-", "sub-", "mul-", "div-", "rem-", "and-", "or-", "xor-", "shl-", "shr-", "ushr-",
    )
    private val BINARY_OPCODES = Opcode.values().filterTo(hashSetOf()) { opcode ->
        BINARY_PREFIXES.any { opcode.name.startsWith(it) } && !opcode.name.endsWith("/2addr") &&
            !opcode.name.contains("/lit")
    }
    private val TWO_ADDRESS_OPCODES = Opcode.values().filterTo(hashSetOf()) { it.name.endsWith("/2addr") }
    private val LITERAL_OPCODES = Opcode.values().filterTo(hashSetOf()) {
        it.name.contains("/lit") || it == Opcode.RSUB_INT
    }
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

    private const val MAX_REGISTER_COUNT = 65_535L
    private const val U4_MAX = 15
    private const val U8_MAX = 255
}
