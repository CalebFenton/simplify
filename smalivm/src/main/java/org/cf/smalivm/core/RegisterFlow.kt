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
 * Register values before each reachable instruction of one body: a forward fixpoint over the edges of its
 * [MethodControlFlow], with the transfer function taken from [RegisterEffects]. A handler entry receives the state
 * from before the throwing instruction. Parameters occupy the top of the body's declared register count; every
 * other register starts undefined.
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
         * scratch registers numbered after [registerCount], the baseline register count.
         */
        fun analyze(
            flow: MethodControlFlow,
            method: Method,
            registerCount: Int,
            registerFileSize: Int,
            registersOf: (EncodedInstruction) -> List<Int>,
        ): RegisterFlow {
            if (flow.size == 0) return RegisterFlow(emptyMap())
            val states = arrayOfNulls<ByteArray>(flow.size)
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

            val normal = Array(flow.size, flow::normalSuccessors)
            val exceptional = Array(flow.size, flow::exceptionalSuccessors)
            flowInto(0, entryState(method, registerCount, registerFileSize))
            while (pending.isNotEmpty()) {
                val index = pending.removeFirst()
                val record = flow.recordAt(index)
                val before = requireNotNull(states[index])
                val after = transfer(before, record, registersOf(record))
                for (successor in normal[index]) flowInto(successor, after)
                for (handler in exceptional[index]) flowInto(handler, before)
            }
            return RegisterFlow(
                (0 until flow.size).mapNotNull { index -> states[index]?.let { flow.positionAt(index) to it } }.toMap(),
            )
        }

        private fun entryState(method: Method, registerCount: Int, registerFileSize: Int): ByteArray {
            val state = ByteArray(registerFileSize) { RegisterValue.UNDEFINED.ordinal.toByte() }
            val parameters = buildList {
                if (!Modifier.isStatic(method.accessFlags)) add(ValueCategory.REFERENCE)
                method.parameterTypes.mapTo(this, ValueCategory::ofDescriptor)
            }
            var register = registerCount - parameters.sumOf(ValueCategory::width)
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

        private val VALUES = RegisterValue.entries
        private val NARROW_CONSTANTS = setOf(Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16)
    }
}
