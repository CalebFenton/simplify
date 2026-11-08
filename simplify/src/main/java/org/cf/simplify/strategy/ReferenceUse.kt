package org.cf.simplify.strategy

import org.cf.smalivm.core.MethodControlFlow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import java.util.BitSet

/** How the code after one instruction uses the object that instruction leaves in a register. */
internal enum class ReferenceUse {
    /** No path reads the object from a register before every register holding it is redefined. */
    UNREAD,

    /** Some path reads the object, and none compares it by reference or locks on it. */
    READ,

    /** Some path reaches a reference `if-eq`/`if-ne` or a `monitor-enter`/`monitor-exit` on the object. */
    IDENTITY_OBSERVED,
}

/**
 * How the object [register] holds after the instruction at [index] completes normally is used by the method's later
 * code, over every control-flow edge [flow] permits, whether or not an execution took it. The object is followed
 * through `move-object*` copies and `check-cast`, and lost where a register holding it is redefined. Handlers
 * entered from an instruction see the registers from before it. Reads inside callees, fields, arrays and returned
 * values are not followed: a call, a store or a return is only a read.
 */
internal fun referenceUseAfter(flow: MethodControlFlow, index: Int, register: Int): ReferenceUse {
    // Registers that may hold the object on entry to each instruction, joined over every edge into it.
    val holders = arrayOfNulls<BitSet>(flow.size)
    val pending = ArrayDeque<Int>()
    fun enter(target: Int, registers: BitSet) {
        val known = holders[target] ?: BitSet().also { holders[target] = it }
        val before = known.cardinality()
        known.or(registers)
        if (known.cardinality() != before) pending.addLast(target)
    }
    val received = BitSet().apply { set(register) }
    flow.normalSuccessors(index).forEach { enter(it, received) }
    var read = false
    while (pending.isNotEmpty()) {
        val at = pending.removeFirst()
        val held = holders[at]!!.clone() as BitSet
        val instruction = flow.instructionAt(at)
        if (flow.registersRead(at).any(held::get)) {
            if (instruction.opcode in IDENTITY_OBSERVERS) return ReferenceUse.IDENTITY_OBSERVED
            read = true
        }
        flow.exceptionalSuccessors(at).forEach { enter(it, held) }
        val after = held.clone() as BitSet
        // check-cast writes its register with the same object it read.
        if (instruction.opcode != Opcode.CHECK_CAST) flow.registersWritten(at).forEach(after::clear)
        if (instruction.opcode in OBJECT_MOVES && held[(instruction as TwoRegisterInstruction).registerB]) {
            after.set(instruction.registerA)
        }
        if (!after.isEmpty) flow.normalSuccessors(at).forEach { enter(it, after) }
    }
    return if (read) ReferenceUse.READ else ReferenceUse.UNREAD
}

/** A reference read by one of these is compared by identity or used as a lock; `if-eqz`/`if-nez` only test null. */
private val IDENTITY_OBSERVERS = setOf(Opcode.IF_EQ, Opcode.IF_NE, Opcode.MONITOR_ENTER, Opcode.MONITOR_EXIT)
