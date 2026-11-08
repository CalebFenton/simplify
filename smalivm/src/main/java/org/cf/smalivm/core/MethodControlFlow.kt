package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.Instruction

/**
 * Every control-flow edge the Dalvik bytecode of one method body permits, whether or not an execution took it.
 * Instructions are numbered in body order without payload data, so index `i` is the instruction at
 * [MethodView.instructionAddresses]`[i]`, and the method entry is index 0.
 *
 * Normal edges are fall-through, branch and switch-case edges. Exceptional edges run from each instruction that can
 * throw to every handler of the try range covering it, and carry the register state from before that instruction.
 */
public class MethodControlFlow private constructor(
    private val records: Array<EncodedInstruction>,
    private val addresses: IntArray,
    private val normal: Array<IntArray>,
    private val exceptional: Array<IntArray>,
    private val predecessors: Array<IntArray>,
    private val fallthrough: IntArray,
    private val fallsOffCode: BooleanArray,
    private val handlerEntries: BooleanArray,
    private val reachable: BooleanArray,
) {
    /** Number of instructions. */
    public val size: Int get() = records.size

    /** Code address of instruction [index]. */
    public fun addressAt(index: Int): Int = addresses[index]

    /** Instruction [index]. */
    public fun instructionAt(index: Int): Instruction = records[index].instruction

    internal fun positionAt(index: Int): LogicalCodePosition = records[index].position

    internal fun recordAt(index: Int): EncodedInstruction = records[index]

    /** Index of the instruction at [address], or null when none starts there. */
    public fun indexOf(address: Int): Int? = addresses.binarySearch(address).takeIf { it >= 0 }

    /** Fall-through, branch and switch-case successors of [index]. Each read returns a copy. */
    public fun normalSuccessors(index: Int): IntArray = normal[index].copyOf()

    /** Handler entries [index] reaches when it throws. Each read returns a copy. */
    public fun exceptionalSuccessors(index: Int): IntArray = exceptional[index].copyOf()

    /** Instructions with a normal or exceptional edge into [index]. Each read returns a copy. */
    public fun predecessors(index: Int): IntArray = predecessors[index].copyOf()

    /** Whether some path of edges from the method entry reaches [index]. */
    public fun isReachable(index: Int): Boolean = reachable[index]

    /** Whether a try range names [index] as a handler. */
    public fun isHandlerEntry(index: Int): Boolean = handlerEntries[index]

    /** The instruction [index] falls through to, or null when it cannot continue or falls off the code. */
    public fun fallthroughSuccessor(index: Int): Int? = fallthrough[index].takeIf { it >= 0 }

    /** Whether [index] can continue but the next code unit is payload data or past the last instruction. */
    internal fun fallsOffCode(index: Int): Boolean = fallsOffCode[index]

    /** Whether a reachable instruction falls off the code, which ART's verifier rejects. */
    internal val hasReachableFallOff: Boolean get() = records.indices.any { reachable[it] && fallsOffCode[it] }

    /**
     * The instruction before [index] when falling through from it is the only way to enter [index]: [index] is not
     * the method entry, not a handler entry, and no other edge targets it. The register state entering [index] is then
     * exactly the state after that instruction. Null otherwise.
     */
    public fun fallthroughPredecessor(index: Int): Int? {
        if (index == 0 || handlerEntries[index]) return null
        val previous = index - 1
        if (fallthrough[previous] != index || predecessors[index].any { it != previous }) return null
        return previous
    }

    /** Registers the instruction at [index] reads, ascending; a wide operand names both halves of its pair. */
    public fun registersRead(index: Int): IntArray = RegisterEffects.registersRead(records[index].instruction)

    /** Registers the instruction at [index] writes, ascending; a wide operand names both halves of its pair. */
    public fun registersWritten(index: Int): IntArray = RegisterEffects.registersWritten(records[index].instruction)

    internal companion object {
        /**
         * The control flow of [body], or null when the body names a branch, switch-case or handler target that is not
         * an instruction, a switch or `fill-array-data` target that is not a payload of the matching kind, or
         * overlapping try ranges. The Dalvik verifier rejects each of these. A reachable fall off the code is recorded
         * rather than rejected, so the editor can name it.
         */
        internal fun of(body: MethodBody): MethodControlFlow? {
            val bodyRecords = body.instructions
            val records = bodyRecords.filter { it.instruction.opcode !in OpcodeGroups.PAYLOAD }.toTypedArray()
            val size = records.size
            val indexByPosition = HashMap<LogicalCodePosition, Int>(size * 2)
            records.forEachIndexed { index, record -> indexByPosition[record.position] = index }
            val bodyIndexByPosition = HashMap<LogicalCodePosition, Int>(bodyRecords.size * 2)
            bodyRecords.forEachIndexed { index, record -> bodyIndexByPosition[record.position] = index }

            val normal = Array(size) { IntArray(0) }
            val fallthrough = IntArray(size) { -1 }
            val fallsOffCode = BooleanArray(size)
            for (index in 0 until size) {
                val record = records[index]
                val opcode = record.instruction.opcode
                val successors = LinkedHashSet<Int>()
                when (opcode) {
                    in OpcodeGroups.BRANCH -> {
                        successors += body.controlTargets[record.position]?.let(indexByPosition::get) ?: return null
                    }
                    in OpcodeGroups.PAYLOAD_OWNER -> {
                        val payload = body.controlTargets[record.position]
                            ?.let(bodyIndexByPosition::get)
                            ?.let(bodyRecords::get)
                        if (payload?.instruction?.opcode != PAYLOAD_BY_OWNER.getValue(opcode)) return null
                        for (case in body.switchCases[record.position].orEmpty()) {
                            successors += indexByPosition[case.target] ?: return null
                        }
                    }
                    else -> Unit
                }
                if (opcode.canContinue()) {
                    val next = bodyRecords.getOrNull(requireNotNull(bodyIndexByPosition[record.position]) + 1)
                    val nextIndex = next?.position?.let(indexByPosition::get)
                    if (nextIndex == null) {
                        fallsOffCode[index] = true
                    } else {
                        fallthrough[index] = nextIndex
                        successors += nextIndex
                    }
                }
                normal[index] = successors.toIntArray()
            }

            val handlerEntries = BooleanArray(size)
            val exceptional = exceptionalEdges(body, records, indexByPosition, bodyIndexByPosition, handlerEntries)
                ?: return null

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
                    for (successor in normal[index]) if (!reachable[successor]) { reachable[successor] = true; pending += successor }
                    for (successor in exceptional[index]) if (!reachable[successor]) { reachable[successor] = true; pending += successor }
                }
            }
            return MethodControlFlow(
                records,
                IntArray(size) { records[it].codeAddress },
                normal,
                exceptional,
                Array(size) { predecessorLists[it].toIntArray() },
                fallthrough,
                fallsOffCode,
                handlerEntries,
                reachable,
            )
        }

        /**
         * Handler entries per instruction, marking every handler target in [handlerEntries]. Regions sharing one
         * range form one try item whose clauses keep their order. A region ends before its end position.
         */
        private fun exceptionalEdges(
            body: MethodBody,
            records: Array<EncodedInstruction>,
            indexByPosition: Map<LogicalCodePosition, Int>,
            bodyIndexByPosition: Map<LogicalCodePosition, Int>,
            handlerEntries: BooleanArray,
        ): Array<IntArray>? {
            val bodySize = body.instructions.size
            val targetsByRange = LinkedHashMap<IntRange, LinkedHashSet<Int>>()
            for (region in body.tryRegions) {
                val start = bodyIndexByPosition[region.start] ?: bodySize
                val end = bodyIndexByPosition[region.end] ?: bodySize
                val targets = targetsByRange.getOrPut(start until end, ::LinkedHashSet)
                for (handler in region.handlers) {
                    val target = indexByPosition[handler.target] ?: return null
                    handlerEntries[target] = true
                    targets += target
                }
            }
            val ranges = targetsByRange.keys.filterNot(IntRange::isEmpty).sortedBy(IntRange::first)
            if (ranges.zipWithNext().any { (previous, next) -> next.first <= previous.last }) return null

            val exceptional = Array(records.size) { IntArray(0) }
            for (range in ranges) {
                val targets = requireNotNull(targetsByRange[range]).toIntArray()
                for (bodyIndex in range) {
                    val index = indexByPosition[body.instructions[bodyIndex].position] ?: continue
                    if (mayThrow(records[index].instruction.opcode)) exceptional[index] = targets
                }
            }
            return exceptional
        }

        /** ART's opcode table flags `fill-array-data` as throwing (a null or too-short array); dexlib2 does not. */
        private fun mayThrow(opcode: Opcode): Boolean = opcode.canThrow() || opcode == Opcode.FILL_ARRAY_DATA

        private val PAYLOAD_BY_OWNER = mapOf(
            Opcode.FILL_ARRAY_DATA to Opcode.ARRAY_PAYLOAD,
            Opcode.PACKED_SWITCH to Opcode.PACKED_SWITCH_PAYLOAD,
            Opcode.SPARSE_SWITCH to Opcode.SPARSE_SWITCH_PAYLOAD,
        )
    }
}
