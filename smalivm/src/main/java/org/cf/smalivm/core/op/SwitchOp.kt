package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ProvableInt
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.provableInt

/**
 * Which lookup the payload's key list obeys. dexlib2 normalizes both payload kinds into one `(key, offset)`
 * list, so the opcode is the only thing that says which rule applies.
 */
internal enum class SwitchDispatch { PACKED, SPARSE }

/**
 * `packed-switch` and `sparse-switch`, with the payload decoded at build time so the payload address is never
 * executed ([CON] B22). Targets are absolute addresses resolved against the switch, as both payload formats
 * specify. No match falls through; a Dalvik payload has no default entry. Every successor set is
 * fall-through first, then targets in payload order, deduplicated by address.
 */
internal class SwitchOp(
    address: Int,
    override val name: String,
    private val selectorRegister: Int,
    /** The payload's entries in payload order; a malformed table may repeat a key, which a map would lose. */
    private val cases: List<Case>,
    private val fallThroughAddress: Int,
    private val dispatch: SwitchDispatch,
) : Op(address) {

    internal class Case(val key: Int, val targetAddress: Int)

    private val targetsByKey: Map<Int, List<Int>> = cases.groupBy({ it.key }, { it.targetAddress })

    private val everySuccessor: Set<Int> = linkedSetOf(fallThroughAddress) + cases.map { it.targetAddress }

    override val registersRead: Set<Int> = setOf(selectorRegister)

    override fun execute(frame: Frame): OpExecution = OpExecution.of(
        successorsFor(frame.readRegister(selectorRegister)).map {
            if (it == fallThroughAddress) OpArm.Continue() else OpArm.Jump(it)
        },
    )

    /**
     * Every feasible successor. A selector whose bits are known dispatches; a provable live pointer takes the
     * fall-through, because a heap address is none of the keys (an engine choice for verifier-rejected input);
     * an unprovable selector takes every successor.
     */
    private fun successorsFor(selector: Value): Set<Int> = when (val bits = provableInt(selector)) {
        is ProvableInt.Exactly -> dispatchOn(bits.bits)
        ProvableInt.NonZero -> setOf(fallThroughAddress)
        ProvableInt.Unprovable -> everySuccessor
    }

    /**
     * Every case whose key matches, plus the fall-through when the runtime's own lookup misses. The two can
     * both apply only on a malformed sparse table, where the engine explores both.
     */
    private fun dispatchOn(key: Int): Set<Int> {
        val matching = targetsByKey[key].orEmpty()
        return if (fallsThroughOn(key, matching)) linkedSetOf(fallThroughAddress) + matching else LinkedHashSet(matching)
    }

    /**
     * Whether ART's lookup misses [key] (`interpreter_common.h`). Packed: `key - first_key` in range, in
     * wrapping 32-bit arithmetic, which is the same predicate as membership in dexlib2's `first_key + i` keys.
     * Sparse: a binary search over the keys in payload order, which on a table that is not strictly ascending
     * can miss a key that is present.
     */
    private fun fallsThroughOn(key: Int, matching: List<Int>): Boolean = when (dispatch) {
        SwitchDispatch.PACKED -> matching.isEmpty()
        SwitchDispatch.SPARSE -> {
            var lo = 0
            var hi = cases.size - 1
            var found = false
            while (lo <= hi && !found) {
                val mid = (lo + hi) / 2
                val probe = cases[mid].key
                when {
                    key < probe -> hi = mid - 1
                    key > probe -> lo = mid + 1
                    else -> found = true
                }
            }
            !found
        }
    }

    override fun toString(): String =
        cases.joinToString(prefix = "$name r$selectorRegister, [", postfix = "]") {
            "${it.key} -> :addr_${it.targetAddress}"
        }
}
