package org.cf.smalivm.core.op

import org.cf.smalivm.core.AbortReason
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Limits
import org.cf.smalivm.core.MethodView
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpBuilder
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.currentResolver
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.cf.smalivm.core.requireMethodView

/**
 * `packed-switch` and `sparse-switch` (`31t`). Expected addresses follow [BC]: case targets are relative to the
 * switch opcode, the fall-through is the switch address plus three code units, and the payload is data that
 * control flow never reaches ([CON] B22). Every `Lswitch_test;` fixture keeps the switch at address 1 so that
 * offsets relative to the switch and to the method start differ.
 */
class SwitchOpTest {
    companion object {
        private const val CLASS_NAME = "Lswitch_test;"
        private const val ADDRESS_SWITCH = 1
        private const val ADDRESS_FALL_THROUGH = 4

        private val INT = VirtualType.of("I")
    }

    /**
     * `packedSwitch()V` has keys 1, 2, 3 at targets 5, 6, 7 and `sparseSwitch()V` has ascending keys 1, 2 at
     * 5, 6; neither payload address is ever visited.
     */
    @TestFactory
    fun aSelectorVisitsItsCaseTheFallThroughOrEveryArm(): List<DynamicTest> = listOf(
        Triple("packedSwitch()V", CoreState().apply { setRegisters(0, 1, "Z") }, intArrayOf(0, 1, 5)),
        Triple("packedSwitch()V", CoreState().apply { setRegisters(0, 100, "S") }, intArrayOf(0, 1, 4)),
        Triple("packedSwitch()V", CoreState().apply { setRegister(0, Value.Unknown(INT)) }, intArrayOf(0, 1, 4, 5, 6, 7)),
        Triple("sparseSwitch()V", CoreState().apply { setRegisters(0, 1, "B") }, intArrayOf(0, 1, 5)),
        Triple("sparseSwitch()V", CoreState().apply { setRegisters(0, 100, "I") }, intArrayOf(0, 1, 4)),
        Triple("sparseSwitch()V", CoreState().apply { setRegister(0, Value.Unknown(INT)) }, intArrayOf(0, 1, 4, 5, 6)),
    ).map { (method, initial, expected) ->
        DynamicTest.dynamicTest("$method ${expected.toList()}") {
            CoreTester.testVisitation(CLASS_NAME, method, initial, expected)
        }
    }

    /**
     * Packed keys next to `Int.MAX_VALUE`, against ART's `DoPackedSwitch` (`interpreter_common.h`)
     * transcribed as [artPackedIndex]: `index = test_val - first_key` in wrapping 32-bit arithmetic, taken
     * when `0 <= index < size`. On `packedSwitchEndingAtIntMax()V`, the verifier's limit, MIN_VALUE is out
     * of range and falls through; on `packedSwitchWrappingPastIntMax()V` ART's index for MIN_VALUE wraps to 1
     * and takes the second case, which a widened 64-bit index would miss.
     */
    @TestFactory
    fun packedKeysNearIntMaxDispatchAsArtsWrappingIndexDoes(): List<DynamicTest> {
        val selectors = listOf(Int.MAX_VALUE - 1, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE + 1, -1, 0)
        return listOf("packedSwitchEndingAtIntMax()V" to 0x7ffffffe, "packedSwitchWrappingPastIntMax()V" to 0x7fffffff)
            .flatMap { (method, firstKey) ->
                selectors.map { selector ->
                    val taken = artPackedIndex(selector, firstKey, size = 2)?.let { 5 + it } ?: ADDRESS_FALL_THROUGH
                    DynamicTest.dynamicTest("$method selector $selector -> $taken") {
                        CoreTester.testVisitation(
                            CLASS_NAME, method, CoreState().apply { setRegisters(0, selector, "I") },
                            intArrayOf(0, ADDRESS_SWITCH, taken),
                        )
                    }
                }
            }
    }

    /**
     * Sparse keys `MIN_VALUE, -2, -1, MAX_VALUE` at 5, 6, 7, 8. ART's sparse lookup is a binary search with
     * signed comparisons over the ascending keys, so each key finds its own case and every other selector
     * falls through to 4. Unsigned, 0x7fffffff sorts below the negative keys, so an unsigned search misses it.
     */
    @TestFactory
    fun negativeSparseKeysAreFoundBySignedOrder(): List<DynamicTest> = listOf(
        Int.MIN_VALUE to 5, -2 to 6, -1 to 7, Int.MAX_VALUE to 8,
        0 to ADDRESS_FALL_THROUGH, -3 to ADDRESS_FALL_THROUGH, Int.MIN_VALUE + 1 to ADDRESS_FALL_THROUGH,
    ).map { (selector, taken) ->
        DynamicTest.dynamicTest("selector $selector -> $taken") {
            CoreTester.testVisitation(
                CLASS_NAME, "sparseSwitchWithNegativeKeys()V", CoreState().apply { setRegisters(0, selector, "I") },
                intArrayOf(0, ADDRESS_SWITCH, taken),
            )
        }
    }

    private fun artPackedIndex(selector: Int, firstKey: Int, size: Int): Int? {
        val index = selector - firstKey
        return if (index in 0 until size) index else null
    }

    /**
     * A payload is data ([CON] B22: control flow must not reach it), so no op ever runs at its address, even on
     * a complete run. `isDead` must still answer `false` for it, or a dead-code strategy deletes the table and
     * breaks every switch that names it. The controls: the selector `1` takes the case at address 5, so the
     * fall-through `return-void` at 4 is genuinely dead, and the switch itself ran.
     */
    @Test
    fun aPayloadIsNeverReachedAndNeverDeadOnACompleteRun() {
        for (methodName in listOf("packedSwitch()V", "sparseSwitch()V")) {
            val graph = CoreTester.execute(CLASS_NAME, methodName, CoreState().apply { setRegisters(0, 1, "I") })
            val trusted = requireNotNull(graph.trusted()) { "$methodName explored everything" }
            val payload = graph.methodView.payloadAddresses.single()
            assertFalse(graph.wasReached(payload), "$methodName: no op runs at payload data")
            assertFalse(trusted.isDead(payload), "$methodName: unreached payload data is not dead code")
            assertFalse(trusted.isDead(ADDRESS_SWITCH), "$methodName: the switch ran")
            assertTrue(trusted.isDead(ADDRESS_FALL_THROUGH), "$methodName: selector 1 never falls through")
        }
    }

    /**
     * A decided selector takes exactly one transition. Asserted on the switch node's children and the outcome
     * count, because a visitation set cannot see a second path to an address it already contains.
     */
    @Test
    fun aProvableSelectorTakesExactlyOneTransition() {
        val matched = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 2, "I") })
        assertEquals(CompletionStatus.Complete, matched.completion())
        assertEquals(
            listOf(6), childAddressesOfSwitch(matched),
            "key 2's target is 6 (offset 5 from the switch at 1), and nothing else is feasible",
        )
        assertEquals(1, matched.outcomes.size, "one path, one outcome")

        val unmatched = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 100, "I") })
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH), childAddressesOfSwitch(unmatched),
            "100 is in no table, so the switch falls through ([BC])",
        )
        assertEquals(1, unmatched.outcomes.size)
    }

    /**
     * An unprovable selector forks the fall-through first and then every distinct target in payload order,
     * as [SwitchOp] declares; the first declared arm becomes the first child. Public outcome order carries no
     * meaning, so this pins only the switch's own arm order.
     */
    @Test
    fun anUnprovableSelectorForksTheFallThroughFirstAndThenEveryTarget() {
        val graph = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply {
            setRegister(0, Value.Unknown(INT))
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5, 6, 7), childAddressesOfSwitch(graph),
            "fall-through first, then keys 1, 2, 3 in payload order",
        )
        assertEquals(4, graph.outcomes.size, "four feasible paths, four outcomes")
    }

    /**
     * A case whose target is the fall-through is one successor, not a second node at that address: the
     * successor set is deduplicated by address with the fall-through included. Decided, that case is spelled
     * `Continue` rather than a `Jump` to the next instruction, which only the op's own arm list can show.
     */
    @Test
    fun aCaseTargetingItsOwnFallThroughIsOneSuccessorRatherThanTwoNodesAtOneAddress() {
        val method = "packedSwitchWhoseCaseTargetsItsFallThrough()V"
        val graph = CoreTester.execute(CLASS_NAME, method, CoreState().apply { setRegister(0, Value.Unknown(INT)) })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5), childAddressesOfSwitch(graph),
            "key 0's offset is +3, i.e. the fall-through, so it contributes no second successor",
        )
        assertEquals(1, graph.nodePile(ADDRESS_FALL_THROUGH).size, "one node at that address, not two")
        assertEquals(2, graph.outcomes.size, "and two ended paths, not three")

        // Key 0 is decided and its target is the fall-through.
        val view = viewOf(method)
        val frame = Frame.entryFrame(view)
        frame.assign(0, Value.Known(0, INT))
        assertEquals(
            listOf(OpArm.Continue()), switchOpOf(method).execute(frame).arms,
            "a decided case whose target is the fall-through is a Continue, never a Jump to that address",
        )
        val decided = CoreTester.execute(CLASS_NAME, method, CoreState().apply { setRegisters(0, 0, "I") })
        assertEquals(CompletionStatus.Complete, decided.completion())
        assertEquals(listOf(ADDRESS_FALL_THROUGH), childAddressesOfSwitch(decided), "one successor")
        assertEquals(1, decided.nodePile(ADDRESS_FALL_THROUGH).size, "and one node there")
    }

    /**
     * A null selector dispatches on key 0: [BC] "General design" states `(Object) null == (int) 0`, so its 32
     * bits are provably zero. `provableInt` owns this rule for both switch and `if`.
     */
    @Test
    fun aDefinitelyNullSelectorDispatchesOnKeyZeroBecauseItsBitsAreZero() {
        val nulled = CoreState().apply { setRegisters(0, null, "Ljava/lang/String;") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", nulled, intArrayOf(0, 1, 5))
        val graph = CoreTester.execute(CLASS_NAME, "packedSwitchIncludingKeyZero()V", nulled)
        assertEquals(listOf(5), childAddressesOfSwitch(graph), "key 0's target, and it is the only successor")
    }

    /**
     * A live pointer, as a host object or a [org.cf.smalivm.core.HeapRef], takes the fall-through without
     * forking. Key 0 provably cannot match a non-zero pointer; the non-zero keys are DALVIK-SEMANTICS.md's
     * verifier-excluded "a live pointer" row, since the verifier requires an integral selector.
     */
    @Test
    fun aLivePointerSelectorMatchesNoCaseAndDoesNotFork() {
        val hostObject = CoreState().apply { setRegisters(0, "not a number", "Ljava/lang/String;") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitch()V", hostObject, intArrayOf(0, 1, 4))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, "packedSwitch()V", hostObject)),
            "one successor: a pointer is not any of keys 1, 2, 3",
        )

        // `CoreState` turns a host array seed into a tracked array, so v0 holds Known(HeapRef, "[B").
        val heapRef = CoreState().apply { setRegisters(0, byteArrayOf(0x1), "[B") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitch()V", heapRef, intArrayOf(0, 1, 4))

        // Against a table that contains key 0.
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", hostObject, intArrayOf(0, 1, 4))
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", heapRef, intArrayOf(0, 1, 4))
    }

    /**
     * A `Float` raw is unprovable as a selector (DALVIK-SEMANTICS.md, `if-eq` section: its box's bits need not
     * match the register's), so every arm is explored rather than dispatching on the float's numeric value.
     */
    @Test
    fun aFloatSelectorIsUnprovableSoEveryArmIsExplored() {
        val float = CoreState().apply { setRegisters(0, 2.9f, "F") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitch()V", float, intArrayOf(0, 1, 4, 5, 6, 7))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5, 6, 7),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, "packedSwitch()V", float)),
            "it forks rather than truncating 2.9f to case 2",
        )
    }

    /**
     * A `size == 0` table falls through for any selector, as ART's `DoPackedSwitch` and `DoSparseSwitch`
     * (`interpreter_common.h`) return the switch length when `size` is 0. No special case exists: an
     * unprovable selector forks over the fall-through plus zero targets.
     */
    @Test
    fun anEmptyTableFallsThroughWhateverTheSelectorIs() {
        val method = "packedSwitchWithEmptyTable()V"
        val known = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, known, intArrayOf(0, 1, 4))
        val unknown = CoreState().apply { setRegister(0, Value.Unknown(INT)) }
        CoreTester.testVisitation(CLASS_NAME, method, unknown, intArrayOf(0, 1, 4))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, unknown)),
            "an unprovable selector over an empty table has one successor",
        )
    }

    /**
     * A descending sparse table is verifier-rejected ([CON] A8; ART's `key <= last_key` check), and ART's
     * binary search over it misses some present keys while an exact match finds them. Neither is provably the
     * runtime's, so the successors are the union (DALVIK-SEMANTICS.md, `packed-switch` / `sparse-switch`). On
     * `sparseSwitchWithDescendingKeys()V` (keys 2 then 1) selector 1 separates them; on an ascending table the
     * two answers coincide.
     */
    @Test
    fun aDescendingSparseTableForksBecauseNeitherDispatchIsProvablyTheRuntimes() {
        val method = "sparseSwitchWithDescendingKeys()V"
        val unreachableByBinarySearch = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, unreachableByBinarySearch, intArrayOf(0, 1, 4, 6))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 6),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, unreachableByBinarySearch)),
            "ART's fall-through AND the map's case, because neither dispatch is provably the runtime's",
        )

        // Selector 2 is the search's first probe, so both dispatches agree and one successor survives.
        val foundByBoth = CoreState().apply { setRegisters(0, 2, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, foundByBoth, intArrayOf(0, 1, 5))
        assertEquals(
            listOf(5), childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, foundByBoth)),
            "the union of two identical answers is one successor",
        )

        // An ascending table, the shape of all verifiable input, takes exactly its case.
        CoreTester.testVisitation(
            CLASS_NAME, "sparseSwitch()V", CoreState().apply { setRegisters(0, 1, "I") }, intArrayOf(0, 1, 5),
        )
    }

    /**
     * Repeated sparse keys are verifier-rejected (ART's `key <= last_key` check). ART's binary search hits key
     * 1, so the switch does not fall through, but which arm it lands on depends on the search midpoint; both
     * arms are successors, in payload order, as with the descending table.
     */
    @Test
    fun aDuplicateKeyKeepsBothArmsWithoutTheFallThrough() {
        val method = "sparseSwitchWithDuplicateKeys()V"
        val selector = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, selector, intArrayOf(0, 1, 5, 6))
        assertEquals(
            listOf(5, 6), childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, selector)),
            "both arms of the repeated key, in payload order, and no fall-through",
        )
    }

    /**
     * Dead-code removal relies on declared read and assignment sets, since `availableRegisters` is static. The
     * non-empty read set shows the op decoded; the base `Op` would also answer an empty assignment set.
     */
    @Test
    fun aSwitchReadsOnlyItsSelectorAndAssignsNothing() {
        val graph = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 1, "I") })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_SWITCH), "the fixture still decodes and runs")
        assertEquals(setOf(0), graph.registersRead(ADDRESS_SWITCH), "the selector, and nothing else")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(ADDRESS_SWITCH),
            "a switch chooses a successor; it writes no register",
        )
    }

    /**
     * No `SwitchOp` arm declares an effect. A spurious `STRONG` would stop every constant fold in the method,
     * because `methodSideEffectLevel` takes the maximum over addresses.
     */
    @Test
    fun aSwitchDeclaresNoSideEffectOnEveryRow() {
        val decided = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 1, "I") })
        assertEquals(SideEffectLevel.NONE, decided.explored.sideEffectLevel(ADDRESS_SWITCH), "a decided switch mutates nothing")
        val trusted = decided.trusted()
        assertNotNull(trusted, "nothing threw and nothing aborted")
        assertEquals(
            SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
            "and the whole-method query a strategy actually reads agrees",
        )

        val forked = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply {
            setRegister(0, Value.Unknown(INT))
        })
        assertEquals(
            SideEffectLevel.NONE, forked.explored.sideEffectLevel(ADDRESS_SWITCH),
            "an ambiguous selector is still not an unmodellable write",
        )

        val pointer = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply {
            setRegisters(0, "not a number", "Ljava/lang/String;")
        })
        assertEquals(SideEffectLevel.NONE, pointer.explored.sideEffectLevel(ADDRESS_SWITCH), "nor is a reference selector")
    }

    /**
     * 501 distinct targets plus the fall-through make 502 loop-free paths to one join. Address visits are
     * path-local, so the default budget of 500 keeps every path, and `SwitchOp` has no fork-arity cap: frontier
     * width is the scheduler's concern, bounded by the address and step limits.
     */
    @Test
    fun fiveHundredAndOneDistinctTargetsKeepEveryLoopFreeSiblingPath() {
        val className = "Lswitch_explosion_test;"
        val method = "manyTargetsConvergingOnOneAddress()V"
        val unknownSelector = CoreState().apply { setRegister(0, Value.Unknown(INT)) }
        val join = CoreTester.classIndex.requireMethodView(className, method)
            .terminalInstructionAddresses.single()

        val bounded = CoreTester.execute(className, method, unknownSelector)
        assertEquals(
            CompletionStatus.Complete, bounded.completion(),
            "each of 502 loop-free paths reaches $join once and owns its own visit count",
        )
        assertNotNull(bounded.trusted())
        assertEquals(0, bounded.outcomes.count { it == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) })
        assertEquals(502, bounded.outcomes.size, "the default limit retains every feasible path")
        assertEquals(502, bounded.nodePile(join).size, "every default-limit path reaches the join")

        val unbounded = CoreTester.execute(className, method, unknownSelector, limits = Limits(maxAddressVisits = 2_000))
        assertEquals(
            CompletionStatus.Complete, unbounded.completion(),
            "raising a path-local budget cannot change a loop-free result",
        )
        assertEquals(502, unbounded.outcomes.size, "501 targets plus the fall-through")
        assertEquals(502, unbounded.nodePile(join).size, "all of them at the one join address")
        assertEquals(
            502, unbounded.nodePile(ADDRESS_SWITCH).single().children.size,
            "one node forked all of them",
        )
    }

    /**
     * A case target outside the method loses only its own path, reported as [AbortReason.MALFORMED_INPUT]
     * rather than `HOST_FAILURE`. No smali assembles such a target, so a real [SwitchOp] with a bad case is
     * injected through the session's decode seam; each arm is its own successor group with
     * `OnUnbuildable.SKIP_ONE`.
     */
    @Test
    fun oneMalformedCaseTargetLosesItsOwnPathAndReportsMalformedInput() {
        val view = viewOf("packedSwitch()V")
        val frame = Frame.entryFrame(view)
        frame.assign(0, Value.Unknown(INT))
        val malformed = SwitchOp(
            ADDRESS_SWITCH, "packed-switch",
            selectorRegister = 0,
            cases = listOf(SwitchOp.Case(1, 5), SwitchOp.Case(2, 9999)),
            fallThroughAddress = ADDRESS_FALL_THROUGH,
            dispatch = SwitchDispatch.PACKED,
        )
        val graph = CoreTester.vmServices.newSession(view, Limits(), frame) { address, current, resolver ->
            if (address == ADDRESS_SWITCH) malformed else OpBuilder.build(address, current, resolver)
        }.run()

        assertEquals(
            CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion(),
            "an address the method does not contain is malformed input, never a bug in this codebase",
        )
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5), childAddressesOfSwitch(graph),
            "the two feasible siblings survive, because one bad clause is no reason to discard a branch a " +
                "device would take",
        )
        assertTrue(
            Outcome.Aborted(AbortReason.MALFORMED_INPUT) in graph.outcomes,
            "and the lost path is accounted for by its own Outcome: ${graph.outcomes}",
        )
    }

    private fun viewOf(methodDescriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(CLASS_NAME, methodDescriptor)

    private fun switchOpOf(methodDescriptor: String): Op =
        OpBuilder.build(ADDRESS_SWITCH, viewOf(methodDescriptor), CoreTester.classIndex.currentResolver())

    /** The switch node's children in arm order; no fixture forks before the switch, so it has one node. */
    private fun childAddressesOfSwitch(graph: ExecutionGraph): List<Int> =
        graph.nodePile(ADDRESS_SWITCH).single().children.map { it.address }
}
