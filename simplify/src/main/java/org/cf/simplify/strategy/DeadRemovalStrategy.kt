package org.cf.simplify.strategy

import java.util.ArrayDeque
import org.cf.smalivm.core.MethodControlFlow
import org.cf.smalivm.core.SideEffectLevel
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.CallSiteReference
import com.android.tools.smali.dexlib2.iface.reference.MethodProtoReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/** Static-CFG liveness with exceptional edges retaining the pre-instruction register state. */
class DeadRemovalStrategy : OptimizationStrategy {
    override val optimizationCategories: Set<String> = setOf(
        DEAD_OPS_REMOVED, DEAD_ASSIGNMENTS_REMOVED, DEAD_RESULTS_REMOVED, USELESS_GOTOS_REMOVED, NOPS_REMOVED,
    )

    override fun perform(input: StrategyInput) {
        val graph = input.graph
        val flow = input.controlFlow ?: return
        if (flow.size == 0) return
        val analysis = Analysis(flow)
        // Registers an unexplored structural successor reads are not a proof of no reads. Keep assignments
        // unless every structurally reachable instruction ran.
        val livenessComplete = (0 until flow.size).none { flow.isReachable(it) && graph.isDead(flow.addressAt(it)) }
        if (livenessComplete) analysis.computeLiveness()

        val method = flow.method
        val removals = linkedSetOf<Int>()
        fun remove(address: Int, category: String) {
            if (removals.add(address)) input.recordOptimization(category)
        }
        for (index in 0 until flow.size) {
            val address = flow.addressAt(index)
            val instruction = flow.instructionAt(index)
            val opcode = instruction.opcode
            if (!flow.isReachable(index)) {
                if (!analysis.isProtected(index) && opcode !in PAYLOAD_OWNERS && !analysis.isPayloadPadding(index)) {
                    remove(address, DEAD_OPS_REMOVED)
                }
                continue
            }
            if (graph.isDead(address)) continue
            if (opcode in GOTOS && (instruction as OffsetInstruction).codeOffset == instruction.codeUnits) {
                remove(address, USELESS_GOTOS_REMOVED)
            }
            if (opcode == Opcode.NOP && !analysis.isPayloadPadding(index)) remove(address, NOPS_REMOVED)
            if (!livenessComplete || graph.sideEffectLevel(address) != SideEffectLevel.NONE ||
                !graph.didEveryExecutionContinueNormally(address) || analysis.isSelfInitialization(index)) continue
            val assigned = analysis.assigned[index]
            if (!assigned.isEmpty && opcode.setsRegister() && !opcode.setsResult() &&
                !assigned.intersects(analysis.liveOut[index])) {
                remove(address, if (opcode in MOVE_RESULTS) DEAD_RESULTS_REMOVED else DEAD_ASSIGNMENTS_REMOVED)
            }
            val returnType = invokeReturnType(instruction)
            if (returnType != null && returnType != "V" && !isClassForName(instruction) &&
                moveResultAfter(method, address) == null) {
                remove(address, DEAD_RESULTS_REMOVED)
            }
        }
        if (removals.isNotEmpty()) input.transaction.removeInstructions(removals)
    }

    private class Analysis(val flow: MethodControlFlow) {
        private val size = flow.size
        private val normal = Array(size) { flow.normalSuccessors(it) }
        private val exceptional = Array(size) { flow.exceptionalSuccessors(it) }
        private val predecessors = Array(size) { flow.predecessors(it) }
        private val protected = protectedInstructions()
        val assigned = Array(size) { RegisterSet.EMPTY }
        private val reads = Array(size) { RegisterSet.EMPTY }
        private val liveIn = Array(size) { RegisterSet.EMPTY }
        val liveOut = Array(size) { RegisterSet.EMPTY }

        fun isProtected(index: Int): Boolean = protected[index]

        fun isPayloadPadding(index: Int): Boolean {
            val next = flow.method.nextAddress(flow.addressAt(index))
            return flow.method.hasAddress(next) && flow.method.isPayload(next)
        }

        /** A constructor's `invoke-direct` of `<init>` on its own receiver initializes `this` and must stay. */
        fun isSelfInitialization(index: Int): Boolean {
            val method = flow.method
            if (!method.signature.substringAfter("->").startsWith("<init>(")) return false
            val instruction = flow.instructionAt(index)
            if (instruction.opcode != Opcode.INVOKE_DIRECT && instruction.opcode != Opcode.INVOKE_DIRECT_RANGE) return false
            if (invokedMethod(instruction)?.name != "<init>") return false
            return invokeRegisters(instruction).firstOrNull() == method.firstParameterRegister
        }

        /**
         * Unreachable instructions that stay because something still names them: the code from each handler entry up
         * to its first non-continuing instruction, the successors of a kept switch, and the method's terminals.
         */
        private fun protectedInstructions(): BooleanArray {
            val result = BooleanArray(size)
            for (index in 0 until size) {
                if (flow.isHandlerEntry(index)) {
                    for (covered in index until size) {
                        result[covered] = true
                        if (!flow.instructionAt(covered).opcode.canContinue()) break
                    }
                }
                if (flow.instructionAt(index).opcode in SWITCHES) for (target in normal[index]) result[target] = true
            }
            for (terminal in flow.method.terminalInstructionAddresses) flow.indexOf(terminal)?.let { result[it] = true }
            return result
        }

        fun computeLiveness() {
            val interner = RegisterSetInterner()
            for (i in 0 until size) {
                reads[i] = RegisterSet.from(flow.registersRead(i), flow.method.registerCount, interner)
                assigned[i] = RegisterSet.from(flow.registersWritten(i), flow.method.registerCount, interner)
            }
            val work = ArrayDeque<Int>()
            val queued = BooleanArray(size)
            for (i in (0 until size).reversed()) {
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
    }

    /** Sorted sparse register words: memory does not scale with the largest register number. */
    private class RegisterSet private constructor(private val values: IntArray) {
        val size: Int get() = values.size
        val isEmpty: Boolean get() = values.isEmpty()

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
            fun from(source: IntArray, registerCount: Int, interner: RegisterSetInterner): RegisterSet {
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
        private val SWITCHES = setOf(Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
        private val GOTOS = setOf(Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32)

        /** The declared return type of an invoke, or null for any other instruction. */
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
