package org.cf.simplify.strategy

import java.util.ArrayDeque
import org.cf.smalivm.core.ExecutionGraphView
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.SideEffectLevel
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.*
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.iface.reference.CallSiteReference
import com.android.tools.smali.dexlib2.iface.reference.MethodProtoReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/** Static-CFG liveness with exceptional edges retaining the pre-instruction register state. */
class DeadRemovalStrategy : OptimizationStrategy {
    override val optimizationCategories: Set<String> = setOf(
        DEAD_OPS_REMOVED, DEAD_ASSIGNMENTS_REMOVED, DEAD_RESULTS_REMOVED, USELESS_GOTOS_REMOVED, NOPS_REMOVED,
    )

    override fun perform(input: StrategyInput) {
        val graph = input.structureGraph
        val analysis = Analysis.build(input.graph.methodView) ?: return
        if (analysis.addresses.isEmpty()) return
        // This engine exposes decoded register sets only for visited instructions. An unexplored
        // structural successor is not a proof of no reads. Keep assignments unless coverage is complete.
        val livenessComplete = analysis.addresses.indices.all {
            !analysis.reachable[it] || graph.wasReached(analysis.addresses[it])
        }
        if (livenessComplete) analysis.computeLiveness(graph)

        val removals = linkedSetOf<Int>()
        fun remove(address: Int, category: String) {
            if (removals.add(address)) input.recordOptimization(category)
        }
        for (index in analysis.addresses.indices) {
            val address = analysis.addresses[index]
            val instruction = analysis.instructions[index]
            val opcode = instruction.opcode
            if (!analysis.reachable[index]) {
                if (!analysis.protectedAddresses[index] && opcode !in PAYLOAD_OWNERS && !isTerminal(opcode) &&
                    !analysis.isPayloadPadding(index)) remove(address, DEAD_OPS_REMOVED)
                continue
            }
            if (!graph.wasReached(address)) continue
            if ((opcode as Enum<*>).name.startsWith("GOTO") && instruction is OffsetInstruction &&
                instruction.codeOffset == instruction.codeUnits) remove(address, USELESS_GOTOS_REMOVED)
            if (opcode == Opcode.NOP && !analysis.isPayloadPadding(index)) remove(address, NOPS_REMOVED)
            if (!livenessComplete || graph.sideEffectLevel(address) != SideEffectLevel.NONE ||
                !input.graph.didEveryExecutionContinueNormally(address) || analysis.isSelfInitialization(index)) continue
            val assigned = analysis.assigned[index]
            if (!assigned.isEmpty && opcode.setsRegister() && !opcode.setsResult() &&
                !assigned.intersects(analysis.liveOut[index])) {
                remove(address, if (isMoveResult(opcode)) DEAD_RESULTS_REMOVED else DEAD_ASSIGNMENTS_REMOVED)
            }
            if ((opcode as Enum<*>).name.startsWith("INVOKE_") && !isOneArgumentClassForName(instruction)) {
                val returnType = invokeReturnType(instruction)
                val next = analysis.indexByAddress[analysis.method.nextAddress(address)]
                if (returnType != null && returnType != "V" &&
                    (next == null || !isMoveResult(analysis.instructions[next].opcode))) {
                    remove(address, DEAD_RESULTS_REMOVED)
                }
            }
        }
        if (removals.isNotEmpty()) input.transaction.removeInstructions(removals)
    }

    private class Analysis(
        val method: MethodView,
        val addresses: IntArray,
        val instructions: Array<Instruction>,
        val indexByAddress: Map<Int, Int>,
        val normal: Array<IntArray>,
        val exceptional: Array<IntArray>,
        val predecessors: Array<IntArray>,
        val protectedAddresses: BooleanArray,
        val reachable: BooleanArray,
    ) {
        val reads = Array(addresses.size) { RegisterSet.EMPTY }
        val assigned = Array(addresses.size) { RegisterSet.EMPTY }
        private val liveIn = Array(addresses.size) { RegisterSet.EMPTY }
        val liveOut = Array(addresses.size) { RegisterSet.EMPTY }

        fun isPayloadPadding(index: Int): Boolean {
            val next = method.nextAddress(addresses[index])
            return method.hasAddress(next) && method.isPayload(next)
        }

        fun isSelfInitialization(index: Int): Boolean {
            if (!method.signature.contains(";-><init>(")) return false
            val instruction = instructions[index]
            if (instruction.opcode != Opcode.INVOKE_DIRECT && instruction.opcode != Opcode.INVOKE_DIRECT_RANGE)
                return false
            val reference = (instruction as? ReferenceInstruction)?.reference as? MethodReference ?: return false
            if (reference.name != "<init>") return false
            val receiver = when (instruction) {
                is RegisterRangeInstruction -> instruction.startRegister
                is FiveRegisterInstruction -> instruction.registerC
                else -> return false
            }
            return receiver == method.firstParameterRegister
        }

        fun computeLiveness(graph: ExecutionGraphView) {
            val interner = RegisterSetInterner()
            for (i in addresses.indices) {
                reads[i] = RegisterSet.from(graph.registersRead(addresses[i]), method.registerCount, interner)
                    .withWidePartners(instructions[i].opcode, method.registerCount, interner)
                assigned[i] = RegisterSet.from(graph.registersAssigned(addresses[i]), method.registerCount, interner)
            }
            val work = ArrayDeque<Int>()
            val queued = BooleanArray(addresses.size)
            for (i in addresses.indices.reversed()) {
                work.addLast(i)
                queued[i] = true
            }
            while (work.isNotEmpty()) {
                val index = work.removeFirst()
                queued[index] = false
                var normalOut = RegisterSet.EMPTY
                for (successor in normal[index]) normalOut = normalOut.union(liveIn[successor], interner)
                var exceptionalOut = RegisterSet.EMPTY
                for (successor in exceptional[index]) exceptionalOut = exceptionalOut.union(liveIn[successor], interner)
                val nextOut = normalOut.union(exceptionalOut, interner)
                val nextIn = normalOut.minus(assigned[index], interner).union(exceptionalOut, interner)
                    .union(reads[index], interner)
                if (nextOut != liveOut[index] || nextIn != liveIn[index]) {
                    liveOut[index] = nextOut
                    liveIn[index] = nextIn
                    for (predecessor in predecessors[index]) {
                        if (!queued[predecessor]) {
                            queued[predecessor] = true
                            work.addLast(predecessor)
                        }
                    }
                }
            }
        }

        companion object {
            fun build(method: MethodView): Analysis? {
                val addresses = method.instructionAddresses
                val instructions = Array(addresses.size) { method.instructionAt(addresses[it]) }
                val indexes = HashMap<Int, Int>(addresses.size)
                for (i in addresses.indices) indexes[addresses[i]] = i
                val protectedAddresses = BooleanArray(addresses.size)
                val handlers = Array(addresses.size) { linkedSetOf<Int>() }
                for (block in method.tryBlocks) {
                    val targets = linkedSetOf<Int>()
                    for (handler in block.catches) targets.add(handler.handlerAddress)
                    block.catchAllAddress?.let(targets::add)
                    for (target in targets) {
                        val start = indexes[target] ?: return null
                        for (i in start until addresses.size) {
                            protectedAddresses[i] = true
                            if (!instructions[i].opcode.canContinue()) break
                        }
                    }
                    val found = addresses.binarySearch(block.startAddress)
                    val start = if (found >= 0) found else -found - 1
                    for (i in start until addresses.size) {
                        if (addresses[i] >= block.endAddress) break
                        if (mayThrow(instructions[i].opcode)) handlers[i].addAll(targets)
                    }
                }
                val normal = Array(addresses.size) { IntArray(0) }
                val exceptional = Array(addresses.size) { IntArray(0) }
                val successors = Array(addresses.size) { IntArray(0) }
                val predecessorLists = Array(addresses.size) { ArrayList<Int>() }
                for (i in addresses.indices) {
                    val address = addresses[i]
                    val instruction = instructions[i]
                    val opcode = instruction.opcode
                    val next = linkedSetOf<Int>()
                    val exceptionalNext = linkedSetOf<Int>()
                    if (opcode in PAYLOAD_OWNERS) {
                        if (instruction !is OffsetInstruction) return null
                        val payloadAddress = address + instruction.codeOffset
                        if (!method.hasAddress(payloadAddress) || !method.isPayload(payloadAddress)) return null
                        val payload = method.payloadAt(payloadAddress)
                        if (opcode == Opcode.PACKED_SWITCH || opcode == Opcode.SPARSE_SWITCH) {
                            if (payload !is SwitchPayload) return null
                            for (element in payload.switchElements) {
                                val target = indexes[address + element.offset] ?: return null
                                next.add(target)
                                protectedAddresses[target] = true
                            }
                        } else if (payload !is ArrayPayload) return null
                    } else if (instruction is OffsetInstruction &&
                        ((opcode as Enum<*>).name.startsWith("IF_") || !opcode.canContinue())) {
                        next.add(indexes[address + instruction.codeOffset] ?: return null)
                    }
                    if (opcode.canContinue()) next.add(indexes[method.nextAddress(address)] ?: return null)
                    if (mayThrow(opcode)) for (handler in handlers[i]) exceptionalNext.add(indexes[handler] ?: return null)
                    normal[i] = next.toIntArray()
                    exceptional[i] = exceptionalNext.toIntArray()
                    next.addAll(exceptionalNext)
                    successors[i] = next.toIntArray()
                    for (successor in next) predecessorLists[successor].add(i)
                }
                for (terminal in method.terminalInstructionAddresses) protectedAddresses[indexes[terminal] ?: return null] = true
                val reachable = BooleanArray(addresses.size)
                if (addresses.isNotEmpty()) {
                    val pending = ArrayDeque<Int>()
                    pending.add(0)
                    reachable[0] = true
                    while (pending.isNotEmpty()) {
                        for (next in successors[pending.removeFirst()]) {
                            if (!reachable[next]) {
                                reachable[next] = true
                                pending.addLast(next)
                            }
                        }
                    }
                }
                return Analysis(method, addresses, instructions, indexes, normal, exceptional,
                    Array(addresses.size) { predecessorLists[it].toIntArray() }, protectedAddresses, reachable)
            }
        }
    }

    /** Sorted sparse register words: memory does not scale with the largest register number. */
    private class RegisterSet private constructor(private val values: IntArray) {
        val size: Int get() = values.size
        val isEmpty: Boolean get() = values.isEmpty()

        fun withWidePartners(opcode: Opcode, registerCount: Int, interner: RegisterSetInterner): RegisterSet {
            val name = (opcode as Enum<*>).name
            if (isEmpty || name.startsWith("INVOKE_") ||
                (!name.contains("WIDE") && !name.contains("LONG") && !name.contains("DOUBLE"))) return this
            val widened = values.copyOf(size * 2)
            var count = size
            for (low in values) if (low + 1 < registerCount) widened[count++] = low + 1
            return sorted(widened, count, interner)
        }

        fun union(other: RegisterSet, interner: RegisterSetInterner): RegisterSet {
            if (isEmpty || this === other) return other
            if (other.isEmpty) return this
            val merged = IntArray(size + other.size)
            var left = 0
            var right = 0
            var count = 0
            while (left < size || right < other.size) {
                merged[count++] = when {
                    right >= other.size || (left < size && values[left] < other.values[right]) -> values[left++]
                    left >= size || other.values[right] < values[left] -> other.values[right++]
                    else -> { right++; values[left++] }
                }
            }
            if (count == size && prefixEquals(values, merged, count)) return this
            if (count == other.size && prefixEquals(other.values, merged, count)) return other
            return interner.intern(RegisterSet(merged.copyOf(count)))
        }

        fun minus(removed: RegisterSet, interner: RegisterSetInterner): RegisterSet {
            if (isEmpty || removed.isEmpty) return this
            if (this === removed) return EMPTY
            val kept = IntArray(size)
            var drop = 0
            var count = 0
            for (value in values) {
                while (drop < removed.size && removed.values[drop] < value) drop++
                if (drop >= removed.size || removed.values[drop] != value) kept[count++] = value
            }
            return when (count) {
                size -> this
                0 -> EMPTY
                else -> interner.intern(RegisterSet(kept.copyOf(count)))
            }
        }

        fun intersects(other: RegisterSet): Boolean {
            var left = 0
            var right = 0
            while (left < size && right < other.size) {
                if (values[left] == other.values[right]) return true
                if (values[left] < other.values[right]) left++ else right++
            }
            return false
        }

        override fun equals(other: Any?): Boolean = this === other || other is RegisterSet && values.contentEquals(other.values)
        override fun hashCode(): Int = values.contentHashCode()

        companion object {
            val EMPTY = RegisterSet(IntArray(0))
            fun from(source: Set<Int>, registerCount: Int, interner: RegisterSetInterner): RegisterSet {
                if (source.isEmpty()) return EMPTY
                val values = IntArray(source.size)
                var count = 0
                for (register in source) if (register in 0 until registerCount) values[count++] = register
                return sorted(values, count, interner)
            }

            private fun sorted(values: IntArray, count: Int, interner: RegisterSetInterner): RegisterSet {
                if (count == 0) return EMPTY
                java.util.Arrays.sort(values, 0, count)
                var unique = 1
                for (i in 1 until count) if (values[i] != values[unique - 1]) values[unique++] = values[i]
                return interner.intern(RegisterSet(values.copyOf(unique)))
            }

            private fun prefixEquals(expected: IntArray, actual: IntArray, count: Int): Boolean {
                for (i in 0 until count) if (expected[i] != actual[i]) return false
                return true
            }
        }
    }

    private class RegisterSetInterner {
        private val sets = hashMapOf(RegisterSet.EMPTY to RegisterSet.EMPTY)
        fun intern(candidate: RegisterSet): RegisterSet = sets.getOrPut(candidate) { candidate }
    }

    companion object {
        const val DEAD_OPS_REMOVED = "dead ops removed"
        const val DEAD_ASSIGNMENTS_REMOVED = "dead assignments removed"
        const val DEAD_RESULTS_REMOVED = "dead results removed"
        const val USELESS_GOTOS_REMOVED = "useless gotos removed"
        const val NOPS_REMOVED = "nops removed"
        private val PAYLOAD_OWNERS = setOf(Opcode.FILL_ARRAY_DATA, Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
        private fun mayThrow(opcode: Opcode): Boolean = opcode.canThrow() || opcode == Opcode.FILL_ARRAY_DATA
        private fun isMoveResult(opcode: Opcode): Boolean =
            opcode == Opcode.MOVE_RESULT || opcode == Opcode.MOVE_RESULT_WIDE || opcode == Opcode.MOVE_RESULT_OBJECT
        private fun isTerminal(opcode: Opcode): Boolean = opcode == Opcode.RETURN_VOID || opcode == Opcode.RETURN ||
            opcode == Opcode.RETURN_WIDE || opcode == Opcode.RETURN_OBJECT || opcode == Opcode.THROW
        private fun invokeReturnType(instruction: Instruction): String? {
            if (instruction is DualReferenceInstruction) return (instruction.reference2 as? MethodProtoReference)?.returnType
            return when (val reference = (instruction as? ReferenceInstruction)?.reference) {
                is MethodReference -> reference.returnType
                is CallSiteReference -> reference.methodProto.returnType
                else -> null
            }
        }
    }
}
