package org.cf.simplify.strategy

import java.util.ArrayDeque
import java.util.BitSet
import org.cf.smalivm.core.Agreement
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.MethodControlFlow
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.TrustedGraphView
import org.cf.smalivm.core.Value
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

/**
 * The objects a method allocates that no kept instruction observes, with every instruction whose only purpose is
 * such an object. Valid only for a complete run on which every structurally reachable instruction ran.
 *
 * A forward pass over the static control flow records, per register, the allocation sites its value may come from
 * and whether it may also hold any other value; an exceptional edge carries the state from before the throwing
 * instruction. An instruction that reads an allocation is removed with it only when every allocation it reads is
 * dead and it is one of these, run normally on every execution:
 * - the allocation itself, with no effect;
 * - a `move-object` or `check-cast` of a register that holds only allocations, with no effect;
 * - a `fill-array-data` or `aput*` into an array register that holds only allocations, whose effect is at most the
 *   write; an `aput-object` value must itself be dead or an immutable host value;
 * - a call other than a constructor with no effect; or a constructor with no effect, or a call whose `WEAK` effect is
 *   a policy-authorized host method's, when each reference operand holds only allocations or an immutable host
 *   value. A constructor's receiver must hold only allocations. `Class.forName` always stays.
 * Any other reader keeps every allocation it reads. A call's result carries the allocations its operands hold, so a
 * kept reader of the result keeps the call; a result that every execution proves to be one of its operands (a
 * builder returning `this`) may hold another value only where that operand may.
 */
internal class DeadAllocations(
    private val graph: TrustedGraphView,
    private val flow: MethodControlFlow,
) {
    private val size = flow.size
    private val siteOf = IntArray(size) { -1 }
    private val states = arrayOfNulls<Map<Int, Origin>>(size)
    private val aliases = HashMap<Int, IntArray>()

    /** Indices of the dead allocations and of every instruction removed with them, ascending. */
    fun removableInstructions(): List<Int> {
        var siteCount = 0
        for (index in 0 until size) {
            if (flow.isReachable(index) && flow.instructionAt(index).opcode in ALLOCATIONS) siteOf[index] = siteCount++
        }
        if (siteCount == 0) return emptyList()
        computeOrigins()
        val reads = Array(size) { readSites(it) }
        val eligible = BooleanArray(size) { index ->
            (reads[index] != null || siteOf[index] >= 0) && servesOnlyAllocations(index)
        }
        val dead = BitSet(siteCount).apply { set(0, siteCount) }
        do {
            var escaped = false
            for (index in 0 until size) {
                val read = reads[index]
                val site = siteOf[index]
                val ownsDead = site >= 0 && dead[site]
                if (!ownsDead && read?.intersects(dead) != true) continue
                if (eligible[index] && (read == null || read.isSubsetOf(dead))) continue
                if (read != null) dead.andNot(read)
                if (site >= 0) dead.clear(site)
                escaped = true
            }
        } while (escaped)

        val removed = mutableListOf<Int>()
        for (index in 0 until size) {
            val site = siteOf[index]
            if (!(site >= 0 && dead[site]) && reads[index]?.intersects(dead) != true) continue
            removed += index
            if (flow.instructionAt(index).opcode.setsResult()) {
                val next = index + 1
                if (next < size && flow.instructionAt(next).opcode in MOVE_RESULTS) removed += next
            }
        }
        return removed
    }

    /** Fills [states] with each reachable instruction's entry state, to a fixpoint. */
    private fun computeOrigins() {
        val work = ArrayDeque<Int>()
        val queued = BooleanArray(size)
        fun flowInto(successor: Int, incoming: Map<Int, Origin>) {
            val merged = join(states[successor], incoming)
            if (merged == states[successor]) return
            states[successor] = merged
            if (!queued[successor]) {
                queued[successor] = true
                work.addLast(successor)
            }
        }
        flowInto(0, emptyMap())
        while (work.isNotEmpty()) {
            val index = work.removeFirst()
            queued[index] = false
            val state = requireNotNull(states[index])
            val after = transfer(index, state)
            for (successor in flow.normalSuccessors(index)) flowInto(successor, after)
            for (successor in flow.exceptionalSuccessors(index)) flowInto(successor, state)
        }
    }

    private fun transfer(index: Int, state: Map<Int, Origin>): Map<Int, Origin> {
        val instruction = flow.instructionAt(index)
        val written = flow.registersWritten(index)
        if (written.isEmpty()) return state
        val defined = when (instruction.opcode) {
            Opcode.NEW_INSTANCE, Opcode.NEW_ARRAY -> Origin(siteBits(siteOf[index]), mayBeOther = false)
            in MOVE_OBJECTS -> state[(instruction as TwoRegisterInstruction).registerB]
            Opcode.CHECK_CAST -> state[(instruction as OneRegisterInstruction).registerA]
            in MOVE_RESULTS -> resultOrigin(index, state)
            else -> null
        }
        if (defined == null && written.none(state::containsKey)) return state
        val next = HashMap(state)
        for (register in written) if (defined == null) next.remove(register) else next[register] = defined
        return next
    }

    /** What the `move-result*` at [index] receives: the producer's allocation, or what its call's operands hold. */
    private fun resultOrigin(index: Int, state: Map<Int, Origin>): Origin? {
        val producer = flow.fallthroughPredecessor(index) ?: return null
        val instruction = flow.instructionAt(producer)
        if (instruction.opcode in FILLED_NEW_ARRAYS) return Origin(siteBits(siteOf[producer]), mayBeOther = false)
        val operands = invokeRegisters(instruction)
        val sites = BitSet()
        for (register in operands) state[register]?.let { sites.or(it.sites) }
        if (sites.isEmpty) return null
        val aliased = aliases.getOrPut(index) { aliasedOperands(producer, index, operands) }
        val mayBeOther = aliased.isEmpty() || aliased.any { state[it]?.mayBeOther ?: true }
        return Origin(sites, mayBeOther)
    }

    /**
     * The operand registers of the call at [producer] that held, on every execution, the very object the
     * `move-result-object` at [moveResult] received. Empty unless every execution agrees on one tracked object.
     */
    private fun aliasedOperands(producer: Int, moveResult: Int, operands: IntArray): IntArray {
        val instruction = flow.instructionAt(moveResult)
        if (instruction.opcode != Opcode.MOVE_RESULT_OBJECT) return IntArray(0)
        val destination = (instruction as OneRegisterInstruction).registerA
        val result = singleRef(flow.addressAt(moveResult), destination) ?: return IntArray(0)
        val callAddress = flow.addressAt(producer)
        return operands.filter { singleRef(callAddress, it) == result }.toIntArray()
    }

    private fun singleRef(address: Int, register: Int): HeapRef? =
        (graph.registerValues(address, register).singleOrNull() as? Value.Known)?.raw as? HeapRef

    private fun readSites(index: Int): BitSet? {
        val state = states[index] ?: return null
        var sites: BitSet? = null
        for (register in flow.registersRead(index)) {
            val origin = state[register] ?: continue
            (sites ?: BitSet().also { sites = it }).or(origin.sites)
        }
        return sites
    }

    /**
     * Whether the instruction at [index] may go with the allocations it reads or makes, given that all of them are
     * dead. The caller checks that they are.
     */
    private fun servesOnlyAllocations(index: Int): Boolean {
        val address = flow.addressAt(index)
        if (!graph.didEveryExecutionContinueNormally(address)) return false
        val state = states[index] ?: return false
        val effect = graph.sideEffectLevel(address)
        val none = effect == SideEffectLevel.NONE
        val atMostWrite = effect <= SideEffectLevel.WEAK
        val instruction = flow.instructionAt(index)
        return when (instruction.opcode) {
            Opcode.NEW_INSTANCE, Opcode.NEW_ARRAY -> none
            in FILLED_NEW_ARRAYS -> none && (!hasReferenceElements(instruction) ||
                invokeRegisters(instruction).all { holdsDeadOrImmutable(state, address, it) })
            in MOVE_OBJECTS -> none && holdsOnlyAllocations(state, (instruction as TwoRegisterInstruction).registerB)
            Opcode.CHECK_CAST -> none && holdsOnlyAllocations(state, (instruction as OneRegisterInstruction).registerA)
            Opcode.FILL_ARRAY_DATA ->
                atMostWrite && holdsOnlyAllocations(state, (instruction as OneRegisterInstruction).registerA)
            in ARRAY_STORES -> {
                val store = instruction as ThreeRegisterInstruction
                atMostWrite && holdsOnlyAllocations(state, store.registerB) &&
                    (instruction.opcode != Opcode.APUT_OBJECT || holdsDeadOrImmutable(state, address, store.registerA))
            }
            in INVOKES -> isConfinedCall(instruction, state, address, effect)
            else -> false
        }
    }

    private fun isConfinedCall(
        instruction: Instruction,
        state: Map<Int, Origin>,
        address: Int,
        effect: SideEffectLevel,
    ): Boolean {
        val method = invokedMethod(instruction) ?: return false
        if (isClassForName(instruction)) return false
        val references = referenceOperands(instruction, method) ?: return false
        val constructor = method.name == "<init>"
        if (constructor && !holdsOnlyAllocations(state, references.first())) return false
        return when (effect) {
            SideEffectLevel.NONE -> !constructor || references.all { holdsDeadOrImmutable(state, address, it) }
            SideEffectLevel.WEAK -> graph.didEveryExecutionRunHostMethod(address) &&
                references.all { holdsDeadOrImmutable(state, address, it) }
            SideEffectLevel.STRONG -> false
        }
    }

    /** The registers of the call's receiver and reference arguments, or null when the words do not fit the method. */
    private fun referenceOperands(instruction: Instruction, method: MethodReference): IntArray? {
        val registers = invokeRegisters(instruction)
        val references = mutableListOf<Int>()
        var word = 0
        if (instruction.opcode != Opcode.INVOKE_STATIC && instruction.opcode != Opcode.INVOKE_STATIC_RANGE) {
            references += registers.getOrNull(word++) ?: return null
        }
        for (parameter in method.parameterTypes) {
            val type = parameter.toString()
            if (type.isReferenceType()) references += registers.getOrNull(word) ?: return null
            word += if (type == "J" || type == "D") 2 else 1
        }
        return if (word == registers.size) references.toIntArray() else null
    }

    /** Whether [register] holds an allocation of this method on every path, never anything else. */
    private fun holdsOnlyAllocations(state: Map<Int, Origin>, register: Int): Boolean =
        state[register]?.mayBeOther == false

    /** [holdsOnlyAllocations], or every execution holds null or an immutable host value there. */
    private fun holdsDeadOrImmutable(state: Map<Int, Origin>, address: Int, register: Int): Boolean {
        if (holdsOnlyAllocations(state, register)) return true
        // A tracked mutable object is always a HeapRef; any other known raw is null or an immutable host value.
        val values = graph.registerValues(address, register)
        if (values.isNotEmpty() && values.all { it is Value.Known && it.raw !is HeapRef }) return true
        return when (graph.knownString(address, register)) {
            is Agreement.Agreed, Agreement.Disagree -> true
            Agreement.Absent, Agreement.NoPath -> false
        }
    }

    private fun hasReferenceElements(instruction: Instruction): Boolean =
        ((instruction as ReferenceInstruction).reference as TypeReference).type.substring(1).isReferenceType()

    /** A register absent from one side holds no allocation there, so the merge may also hold another value. */
    private fun join(current: Map<Int, Origin>?, incoming: Map<Int, Origin>): Map<Int, Origin> {
        if (current == null || current === incoming) return incoming
        val merged = HashMap<Int, Origin>(current.size + incoming.size)
        for ((register, left) in current) {
            val right = incoming[register]
            merged[register] = if (right == null) {
                Origin(left.sites, mayBeOther = true)
            } else {
                Origin(left.sites.unionWith(right.sites), left.mayBeOther || right.mayBeOther)
            }
        }
        for ((register, right) in incoming) {
            if (register !in current) merged[register] = Origin(right.sites, mayBeOther = true)
        }
        return if (merged == current) current else merged
    }

    /** The allocation sites a register's value may come from, and whether it may hold any other value. */
    private data class Origin(val sites: BitSet, val mayBeOther: Boolean)

    private companion object {
        val ALLOCATIONS = setOf(
            Opcode.NEW_INSTANCE, Opcode.NEW_ARRAY, Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE,
        )
        val FILLED_NEW_ARRAYS = setOf(Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE)
        val MOVE_OBJECTS = setOf(Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16)
        val ARRAY_STORES = setOf(
            Opcode.APUT, Opcode.APUT_WIDE, Opcode.APUT_OBJECT, Opcode.APUT_BOOLEAN, Opcode.APUT_BYTE, Opcode.APUT_CHAR,
            Opcode.APUT_SHORT,
        )
        val INVOKES = setOf(
            Opcode.INVOKE_VIRTUAL, Opcode.INVOKE_SUPER, Opcode.INVOKE_DIRECT, Opcode.INVOKE_STATIC,
            Opcode.INVOKE_INTERFACE, Opcode.INVOKE_VIRTUAL_RANGE, Opcode.INVOKE_SUPER_RANGE, Opcode.INVOKE_DIRECT_RANGE,
            Opcode.INVOKE_STATIC_RANGE, Opcode.INVOKE_INTERFACE_RANGE,
        )

        fun siteBits(site: Int): BitSet = BitSet(site + 1).apply { set(site) }

        fun String.isReferenceType(): Boolean = startsWith("L") || startsWith("[")

        fun BitSet.isSubsetOf(other: BitSet): Boolean = (clone() as BitSet).apply { andNot(other) }.isEmpty

        fun BitSet.unionWith(other: BitSet): BitSet =
            if (this == other) this else (clone() as BitSet).apply { or(other) }
    }
}
