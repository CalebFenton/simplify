package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload

/**
 * Every control-flow edge the Dalvik bytecode of one [MethodView] permits, whether or not an execution took it.
 * Instructions are numbered by their position in [MethodView.instructionAddresses]; payload data is not an
 * instruction, and the method entry is index 0.
 *
 * Normal edges are fall-through, branch and switch-case edges. Exceptional edges run from each instruction that can
 * throw to every handler of the try range covering it, and carry the register state from before that instruction.
 */
class MethodControlFlow private constructor(
    val method: MethodView,
    private val addresses: IntArray,
    private val instructions: Array<Instruction>,
    private val normal: Array<IntArray>,
    private val exceptional: Array<IntArray>,
    private val predecessors: Array<IntArray>,
    private val handlerEntries: BooleanArray,
    private val reachable: BooleanArray,
) {
    val size: Int get() = addresses.size

    fun addressAt(index: Int): Int = addresses[index]

    fun instructionAt(index: Int): Instruction = instructions[index]

    fun indexOf(address: Int): Int? = addresses.binarySearch(address).takeIf { it >= 0 }

    /** Fall-through, branch and switch-case successors of [index]. Each read returns a copy. */
    fun normalSuccessors(index: Int): IntArray = normal[index].copyOf()

    /** Handler entries [index] reaches when it throws. Each read returns a copy. */
    fun exceptionalSuccessors(index: Int): IntArray = exceptional[index].copyOf()

    /** Instructions with a normal or exceptional edge into [index]. Each read returns a copy. */
    fun predecessors(index: Int): IntArray = predecessors[index].copyOf()

    /** Whether some path of edges from the method entry reaches [index]. */
    fun isReachable(index: Int): Boolean = reachable[index]

    /** Whether a try range names [index] as a handler. */
    fun isHandlerEntry(index: Int): Boolean = handlerEntries[index]

    /**
     * The instruction before [index] when falling through from it is the only way to enter [index]: [index] is not
     * the method entry, not a handler entry, and no other edge targets it. The register state entering [index] is then
     * exactly the state after that instruction. Null otherwise.
     */
    fun fallthroughPredecessor(index: Int): Int? {
        if (index == 0 || handlerEntries[index]) return null
        val previous = index - 1
        if (predecessors[index].any { it != previous }) return null
        return previous.takeIf { method.canContinue(addresses[it]) && method.nextAddress(addresses[it]) == addresses[index] }
    }

    /** Registers the instruction at [index] reads; a wide operand names both halves of its pair. */
    fun registersRead(index: Int): IntArray = registerEffect(index, reads = true)

    /** Registers the instruction at [index] writes; a wide operand names both halves of its pair. */
    fun registersWritten(index: Int): IntArray = registerEffect(index, reads = false)

    private fun registerEffect(index: Int, reads: Boolean): IntArray {
        val instruction = instructions[index]
        val registers = instruction.registerOperands()
        // Invokes and filled-new-array read every argument word and write none.
        if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) {
            return if (reads) registers.toSortedSet().toIntArray() else IntArray(0)
        }
        val result = sortedSetOf<Int>()
        RegisterEffects.operands(instruction).forEachIndexed { operand, effect ->
            val register = registers.getOrNull(operand) ?: return@forEachIndexed
            if (if (reads) !effect.reads else !effect.writes) return@forEachIndexed
            result += register
            if (effect.category == ValueCategory.WIDE) result += register + 1
        }
        return result.toIntArray()
    }

    companion object {
        /**
         * The control flow of [method], or null when its bytecode names a branch, switch-case or handler target that
         * is not an instruction, a switch or `fill-array-data` without a payload of the matching kind, overlapping
         * try ranges, or a reachable instruction that falls through into payload data or off the end of the code. The
         * Dalvik verifier rejects each of these.
         */
        fun of(method: MethodView): MethodControlFlow? {
            val addresses = method.instructionAddresses
            val size = addresses.size
            val instructions = Array(size) { method.instructionAt(addresses[it]) }
            fun indexOf(address: Int): Int? = addresses.binarySearch(address).takeIf { it >= 0 }

            val handlerEntries = BooleanArray(size)
            val handlers = arrayOfNulls<IntArray>(size)
            for (index in 0 until size) {
                if (!mayThrow(instructions[index].opcode)) continue
                val table = when (val lookup = method.handlerTableAt(addresses[index])) {
                    HandlerTableLookup.None -> continue
                    HandlerTableLookup.MalformedMetadata -> return null
                    is HandlerTableLookup.Found -> lookup.table
                }
                val targets = LinkedHashSet<Int>()
                for (clause in table.clauses) targets += indexOf(clause.handlerAddress) ?: return null
                handlers[index] = targets.toIntArray()
            }
            for (block in method.tryBlocks) {
                for (clause in block.catches) handlerEntries[indexOf(clause.handlerAddress) ?: return null] = true
                block.catchAllAddress?.let { handlerEntries[indexOf(it) ?: return null] = true }
            }

            val normal = Array(size) { IntArray(0) }
            val fallsOffCode = BooleanArray(size)
            for (index in 0 until size) {
                val address = addresses[index]
                val instruction = instructions[index]
                val opcode = instruction.opcode
                val successors = LinkedHashSet<Int>()
                when (opcode) {
                    in OpcodeGroups.BRANCH -> {
                        val target = indexOf(address + (instruction as OffsetInstruction).codeOffset) ?: return null
                        successors += target
                    }
                    in OpcodeGroups.PAYLOAD_OWNER -> {
                        val payloadAddress = address + (instruction as OffsetInstruction).codeOffset
                        if (!method.isPayload(payloadAddress)) return null
                        val payload = method.payloadAt(payloadAddress)
                        if (opcode == Opcode.FILL_ARRAY_DATA) {
                            if (payload !is ArrayPayload) return null
                        } else {
                            if (payload !is SwitchPayload) return null
                            for (element in payload.switchElements) {
                                successors += indexOf(address + element.offset) ?: return null
                            }
                        }
                    }
                    else -> Unit
                }
                if (opcode.canContinue()) {
                    val next = indexOf(address + instruction.codeUnits)
                    if (next == null) fallsOffCode[index] = true else successors += next
                }
                normal[index] = successors.toIntArray()
            }
            val exceptional = Array(size) { handlers[it] ?: IntArray(0) }

            val predecessorLists = Array(size) { ArrayList<Int>(2) }
            for (index in 0 until size) {
                for (successor in normal[index]) predecessorLists[successor] += index
                for (successor in exceptional[index]) {
                    if (index !in predecessorLists[successor]) predecessorLists[successor] += index
                }
            }
            val reachable = BooleanArray(size)
            if (size > 0) {
                val pending = ArrayDeque<Int>()
                reachable[0] = true
                pending += 0
                while (pending.isNotEmpty()) {
                    val index = pending.removeFirst()
                    if (fallsOffCode[index]) return null
                    for (successor in normal[index]) if (!reachable[successor]) { reachable[successor] = true; pending += successor }
                    for (successor in exceptional[index]) if (!reachable[successor]) { reachable[successor] = true; pending += successor }
                }
            }
            return MethodControlFlow(
                method, addresses, instructions, normal, exceptional,
                Array(size) { predecessorLists[it].toIntArray() }, handlerEntries, reachable,
            )
        }

        /** dexlib2 does not flag `fill-array-data` as throwing; Dalvik throws for a null or too-short array. */
        private fun mayThrow(opcode: Opcode): Boolean = opcode.canThrow() || opcode == Opcode.FILL_ARRAY_DATA
    }
}
