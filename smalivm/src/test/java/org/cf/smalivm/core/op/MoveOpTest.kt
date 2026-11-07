package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * `move*` behavior on the clean core. Migrated from the oracle-driven original; the fixture
 * (`ops/move_test.smali`) and every expected value are unchanged, so a divergence here is the new
 * core's, not a re-specification.
 *
 * Every method in that fixture is two instructions of one code unit each -- derived from the fixture
 * rather than assumed: `@0` the move (`move`/`move-object`/`move-result`/`move-exception`, all
 * one-unit forms), `@1` `return-void`.
 */
class MoveOpTest {
    private lateinit var initial: CoreState
    private lateinit var expected: CoreState

    @BeforeEach
    fun setUp() {
        initial = CoreState()
        expected = CoreState()
    }

    @Test
    fun canMoveRegisterPrimitive() {
        initial.setRegisters(0, 42, "I")
        expected.setRegisters(0, 42, "I", 1, 42, "I")
        CoreTester.test(CLASS_NAME, "moveRegisterPrimitive()V", initial, expected)
    }

    /**
     * The caught value is a typed frame slot now, not the old engine's negative pseudo-register.
     * `move-exception` consumes it once, which is the slot lifecycle `Frame` enforces.
     */
    @Test
    fun canMoveException() {
        val exception = "any object would do"
        initial.setCaught(exception, "Ljava/lang/Exception;")
        expected.setRegisters(0, exception, "Ljava/lang/Exception;")
        CoreTester.test(CLASS_NAME, "moveException()V", initial, expected)
    }

    @Test
    fun canMoveResult() {
        initial.setResult(42, "I")
        expected.setRegisters(0, 42, "I")
        CoreTester.test(CLASS_NAME, "moveResult()V", initial, expected)
    }

    /**
     * A `move` of an UNKNOWN propagates the unknown to both registers rather than inventing a value.
     *
     * Also the only end-to-end witness for [CoreState.UNKNOWN] and its `raw === UNKNOWN ->
     * Value.Unknown(type)` mapping, which every ambiguity test in the later opcode tasks depends on.
     * Oracle: spec §4.1 -- an unknown operand stays unknown, and information may only decrease.
     *
     * The expected value is a literal [Value.Unknown], NOT `expected.setRegisters(0,
     * CoreState.UNKNOWN, "I")`. That matters, and it was found by mutation rather than by reasoning:
     * routing both sides through `CoreState`'s own sentinel mapping makes the mapping
     * self-consistent, so a sentinel that fell through to `Value.Known(UNKNOWN, I)` compared equal to
     * itself and the test passed while asserting nonsense. Verified: replacing that mapping with a
     * bare `Value.Known(raw, type)` now fails here, and left the whole suite green before this shape.
     */
    @Test
    fun movingAnUnknownYieldsAnUnknownInBothRegisters() {
        val unknownInt = Value.Unknown(VirtualType.of("I"))
        initial.setRegisters(0, CoreState.UNKNOWN, "I")
        val graph = CoreTester.execute(CLASS_NAME, "moveRegisterPrimitive()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(unknownInt, trusted!!.terminatingConsensus(0), "the source register is still unknown")
        assertEquals(unknownInt, trusted.terminatingConsensus(1), "and so is the register moved into")
    }

    /**
     * REWRITTEN, not ported. The original asserted `assertSame` on the two registers' consensus
     * values -- host identity, which the new lattice deliberately replaced with structural equality
     * plus heap ids for tracked mutables (spec §4.1; host identity is the audit's
     * "equal mutables collapse" defect). The behavior it was protecting -- both registers hold the
     * same value after the move, and the method terminates where expected -- is what is asserted.
     */
    @Test
    fun movingAnObjectLeavesBothRegistersHoldingTheSameValue() {
        val objectType = VirtualType.of("Ljava/lang/Object;")
        val seeded = Value.Known(Any(), objectType)
        initial.setRegister(0, seeded)
        val graph = CoreTester.execute(CLASS_NAME, "moveRegisterObject()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        // Address 1 is the `return-void`, so this reads the state after the whole method ran -- the
        // same address the original asserted through the oracle's `getRegisterConsensus(1, …)`.
        val source = trusted!!.consensus(1, 0) as Value.Known
        val alias = trusted.consensus(1, 1) as Value.Known
        assertEquals(source, alias)
        assertTrue(source.raw is HeapRef)
        // The "terminates where expected" half of the claim above, which `consensus(1, …)` alone does
        // not make: it proves address 1 EXECUTED (only executed nodes vote) but not that the path ended
        // there. The original asserted `getConnectedTerminatingAddresses() == {1}`; the analogue here is
        // that the sole outcome is the `return-void` at @1 returning no value.
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
    }

    companion object {
        private const val CLASS_NAME = "Lmove_test;"
    }
}
