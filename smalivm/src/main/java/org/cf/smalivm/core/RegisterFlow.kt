package org.cf.smalivm.core

import java.lang.reflect.Modifier
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction

/** What a register holds at one program point. A zero constant is compatible with both narrow and reference uses. */
internal enum class RegisterValue {
    UNDEFINED,
    CONFLICT,
    ZERO,
    NARROW,
    REFERENCE,
    WIDE_LOW,
    WIDE_HIGH,
    ;

    fun join(other: RegisterValue): RegisterValue = when {
        this == other -> this
        this == UNDEFINED || other == UNDEFINED -> UNDEFINED
        this == ZERO && (other == REFERENCE || other == NARROW) -> other
        other == ZERO && (this == REFERENCE || this == NARROW) -> this
        else -> CONFLICT
    }
}

/**
 * Register values before each reachable executable instruction of one body: a forward fixpoint over branch,
 * switch, fall-through and exception edges, with the transfer function taken from [RegisterEffects].
 * Parameters occupy the top of the body's declared register count; every other register starts undefined.
 */
internal class RegisterFlow private constructor(private val states: Map<LogicalCodePosition, ByteArray>) {
    /** The state before [position], or null when no path reaches it. */
    fun before(position: LogicalCodePosition): State? = states[position]?.let(::State)

    @JvmInline
    value class State(private val values: ByteArray) {
        /** A register outside the register file reads as [RegisterValue.CONFLICT]. */
        operator fun get(register: Int): RegisterValue =
            if (register in values.indices) VALUES[values[register].toInt()] else RegisterValue.CONFLICT
    }

    companion object {
        /**
         * [registerFileSize] covers every register [registersOf] can name; for a logical body that includes the
         * scratch registers numbered after the baseline register count.
         */
        fun analyze(
            body: MethodBody,
            method: Method,
            registerFileSize: Int,
            registersOf: (EncodedInstruction) -> List<Int>,
        ): RegisterFlow {
            val records = body.instructions.filter { it.instruction.opcode !in OpcodeGroups.PAYLOAD }
            if (records.isEmpty()) return RegisterFlow(emptyMap())
            val indexByPosition = records.withIndex().associate { (index, record) -> record.position to index }
            val handlers = exceptionHandlers(body, records)
            val states = arrayOfNulls<ByteArray>(records.size)
            val pending = ArrayDeque<Int>()

            fun flowInto(target: Int, incoming: ByteArray) {
                val current = states[target]
                if (current == null) {
                    states[target] = incoming.copyOf()
                    pending += target
                    return
                }
                var changed = false
                for (register in current.indices) {
                    val joined = VALUES[current[register].toInt()].join(VALUES[incoming[register].toInt()])
                    if (joined.ordinal.toByte() != current[register]) {
                        current[register] = joined.ordinal.toByte()
                        changed = true
                    }
                }
                if (changed) pending += target
            }

            flowInto(0, entryState(body, method, registerFileSize))
            while (pending.isNotEmpty()) {
                val index = pending.removeFirst()
                val record = records[index]
                val before = requireNotNull(states[index])
                val after = transfer(before, record, registersOf(record))
                val opcode = record.instruction.opcode
                val target = body.controlTargets[record.position]?.let(indexByPosition::get)
                val next = (index + 1).takeIf { it < records.size }
                when (opcode) {
                    in OpcodeGroups.GOTO -> target?.let { flowInto(it, after) }
                    in OpcodeGroups.CONDITIONAL_BRANCH -> {
                        target?.let { flowInto(it, after) }
                        next?.let { flowInto(it, after) }
                    }
                    in OpcodeGroups.SWITCH -> {
                        body.switchCases[record.position].orEmpty().forEach { case ->
                            indexByPosition[case.target]?.let { flowInto(it, after) }
                        }
                        next?.let { flowInto(it, after) }
                    }
                    else -> if (opcode.canContinue()) next?.let { flowInto(it, after) }
                }
                if (opcode.canThrow()) handlers[index]?.forEach { flowInto(it, before) }
            }
            return RegisterFlow(
                records.indices.mapNotNull { index -> states[index]?.let { records[index].position to it } }.toMap(),
            )
        }

        private fun entryState(body: MethodBody, method: Method, registerFileSize: Int): ByteArray {
            val state = ByteArray(registerFileSize) { RegisterValue.UNDEFINED.ordinal.toByte() }
            val parameters = buildList {
                if (!Modifier.isStatic(method.accessFlags)) add(ValueCategory.REFERENCE)
                method.parameterTypes.mapTo(this, ValueCategory::ofDescriptor)
            }
            var register = body.registerCount - parameters.sumOf(ValueCategory::width)
            for (category in parameters) {
                write(state, register, category)
                register += category.width
            }
            return state
        }

        private fun transfer(before: ByteArray, record: EncodedInstruction, registers: List<Int>): ByteArray {
            val instruction = record.instruction
            val operands = RegisterEffects.operands(instruction)
            if (operands.none(RegisterOperand::writes)) return before
            val after = before.copyOf()
            operands.forEachIndexed { index, operand ->
                if (!operand.writes) return@forEachIndexed
                val register = registers.getOrNull(index) ?: return@forEachIndexed
                when {
                    instruction.opcode in OpcodeGroups.MOVE && operand.category != ValueCategory.WIDE -> {
                        val source = registers.getOrNull(1)?.takeIf { it in before.indices }
                        val value = source?.let { before[it] } ?: RegisterValue.CONFLICT.ordinal.toByte()
                        if (register in after.indices) after[register] = value
                    }
                    instruction.opcode in NARROW_CONSTANTS && (instruction as NarrowLiteralInstruction).narrowLiteral == 0 ->
                        if (register in after.indices) after[register] = RegisterValue.ZERO.ordinal.toByte()
                    else -> write(after, register, operand.category)
                }
            }
            return after
        }

        private fun write(state: ByteArray, register: Int, category: ValueCategory) {
            fun set(index: Int, value: RegisterValue) {
                if (index in state.indices) state[index] = value.ordinal.toByte()
            }
            when (category) {
                ValueCategory.NARROW -> set(register, RegisterValue.NARROW)
                ValueCategory.REFERENCE -> set(register, RegisterValue.REFERENCE)
                ValueCategory.WIDE -> {
                    set(register, RegisterValue.WIDE_LOW)
                    set(register + 1, RegisterValue.WIDE_HIGH)
                }
            }
        }

        /** Handler entry indexes for each record a try region covers; a region's end position is exclusive. */
        private fun exceptionHandlers(body: MethodBody, records: List<EncodedInstruction>): Array<Set<Int>?> {
            val result = arrayOfNulls<Set<Int>>(records.size)
            if (body.tryRegions.isEmpty()) return result
            val bodyIndex = body.instructions.withIndex().associate { (index, record) -> record.position to index }
            val recordBodyIndexes = IntArray(records.size) { requireNotNull(bodyIndex[records[it].position]) }
            val recordIndexByPosition = records.withIndex().associate { (index, record) -> record.position to index }
            for (region in body.tryRegions) {
                val start = bodyIndex[region.start] ?: body.instructions.size
                val end = bodyIndex[region.end] ?: body.instructions.size
                val targets = region.handlers.mapNotNullTo(LinkedHashSet()) { recordIndexByPosition[it.target] }
                var index = recordBodyIndexes.binarySearch(start).let { if (it < 0) -it - 1 else it }
                while (index < records.size && recordBodyIndexes[index] < end) {
                    result[index] = result[index].orEmpty() + targets
                    index++
                }
            }
            return result
        }

        private val VALUES = RegisterValue.entries
        private val NARROW_CONSTANTS = setOf(Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16)
    }
}
