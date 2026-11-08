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
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * `packed-switch` and `sparse-switch` (`31t`), whose payload tables are **data** and are decoded at
 * build time. Migrated from `org.cf.smalivm.opcode.SwitchOpTest` -- all **6** behavioral tests, keeping
 * the `@Nested` structure -- plus the dispatch, fork, payload, liveness, effect and path-explosion rows
 * the corpus never covered. `opcode/SwitchPayloadOpTest`'s single test was a mockk change-detector and
 * is adjudicated in [mockkAdjudication].
 *
 * **EVERY MIGRATED EXPECTATION DROPS ONE ADDRESS, and it is always the payload's.** The six oracle
 * expectations were `[0, 1, 5, 8]`, `[0, 1, 4, 8]`, `[0, 1, 4, 5, 6, 7, 8]` (packed) and
 * `[0, 1, 5, 8]`, `[0, 1, 4, 8]`, `[0, 1, 4, 5, 6, 8]` (sparse); address 8 is the payload
 * pseudo-instruction in both fixtures, and the oracle *jumps to it and executes it*
 * (`opcode/SwitchOp.kt`'s `finishOp(targetAddress)`, with `opcode/SwitchPayloadOp` doing the dispatch).
 * This core never visits it, because **[CON] B22** makes that a verifier constraint rather than a
 * preference: *"The `packed-switch-data`, `sparse-switch-data`, and `fill-array-data`
 * pseudo-instructions must not be reachable by control flow."* The drop is a change in what the harness
 * observes, not in behavior, and it is pinned by the PAIR in
 * [aPayloadIsDataSoItIsNeverReachedAndTheGraphSaysSoOnACompleteRun] rather than by a bare negative.
 *
 * **The packed/sparse difference is the KEY COUNT and nothing else.** Packed's unknown-selector
 * expectation has seven addresses and sparse's has six because `packedSwitch()V` has three targets and
 * `sparseSwitch()V` has two. It is NOT the alignment `nop`: `sparseSwitch()V` has one (at 7) and
 * `packedSwitch()V` has none, and neither appears in any expectation, because no path reaches an
 * alignment nop -- it exists only to push the payload to an even code-unit boundary.
 * [theseFixturesHaveTheAddressesEveryExpectationInThisFileIsWrittenAgainst] measures all of that
 * instead of trusting the fixture's inline comments, two of which are wrong.
 */
class SwitchOpTest {
    companion object {
        private const val CLASS_NAME = "Lswitch_test;"

        /** The switch itself, in every `Lswitch_test;` fixture: address 0 is a `goto` or a `nop`. */
        private const val ADDRESS_SWITCH = 1

        /** `31t` is three code units, so the fall-through is always `switchAddress + 3` -- **SPEC**. */
        private const val ADDRESS_FALL_THROUGH = 4

        private val INT = VirtualType.of("I")
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }

    /**
     * The three migrated `packed-switch` tests. Keys are 1, 2, 3 and their targets are 5, 6, 7 --
     * offsets 4, 5, 6 **relative to the switch at 1**, which is the whole of [BC]'s *"The targets are
     * relative to the address of the switch opcode, not of this table"*.
     */
    @Nested
    inner class PackedSwitch {
        private lateinit var initial: CoreState
        private val METHOD_NAME = "packedSwitch()V"

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        /** Was `[0, 1, 5, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "Z")
            val expectedAddresses = intArrayOf(0, 1, 5)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        /** Was `[0, 1, 4, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "S")
            val expectedAddresses = intArrayOf(0, 1, 4)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        /** Was `[0, 1, 4, 5, 6, 7, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegister(0, Value.Unknown(INT))
            val expectedAddresses = intArrayOf(0, 1, 4, 5, 6, 7)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }
    }

    /**
     * The three migrated `sparse-switch` tests. Keys are 1, 2 with targets 5, 6, and the table is
     * strictly ascending -- so ART's binary search and a map lookup agree, and this nest cannot observe
     * the dispatch divergence at all. [aDescendingSparseTableForksBecauseNeitherDispatchIsProvablyTheRuntimes]
     * is what does.
     */
    @Nested
    inner class SparseSwitch {
        private lateinit var initial: CoreState
        private val METHOD_NAME = "sparseSwitch()V"

        @BeforeEach
        fun setUp() {
            initial = CoreState()
        }

        /** Was `[0, 1, 5, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "B")
            val expectedAddresses = intArrayOf(0, 1, 5)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        /** Was `[0, 1, 4, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "I")
            val expectedAddresses = intArrayOf(0, 1, 4)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }

        /** Was `[0, 1, 4, 5, 6, 8]`; 8 is the payload and is data ([CON] B22), so it is not executed. */
        @Test
        fun testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegister(0, Value.Unknown(INT))
            val expectedAddresses = intArrayOf(0, 1, 4, 5, 6)
            CoreTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses)
        }
    }

    /**
     * **Every address and every key/target pair this file depends on, measured from the assembled
     * `MethodView` rather than read off a comment.** Two of `swich_test.smali`'s own inline `# @n`
     * comments are wrong -- `packedSwitch()V` marks a `nop # for alignment` at "@8" that does not
     * survive assembly at all (its payload is already 4-byte aligned there), so the payload is at 8 and
     * the nop is absent, while `sparseSwitch()V`'s identical-looking comments are correct because it
     * genuinely needs its nop. Nothing else in the repository measures that, and every expectation above
     * would be silently wrong if the assembler's answer changed.
     *
     * Also pins the SPEC fact the whole decode rests on -- **targets are relative to the switch, not to
     * the table**. Offsets 4, 5, 6 against a switch at 1 give 5, 6, 7; against the payload at 8 they
     * would give 12, 13, 14, which is off the end of the method.
     */
    @Test
    fun theseFixturesHaveTheAddressesEveryExpectationInThisFileIsWrittenAgainst() {
        val packed = viewOf("packedSwitch()V")
        assertEquals(listOf(0, 1, 4, 5, 6, 7, 8), packed.addresses.toList(), "no alignment nop survives here")
        assertArrayEquals(intArrayOf(8), packed.payloadAddresses)
        // The nop-ABSENCE half of the elision claim, asserted rather than inferred. The address list plus
        // `payloadAddresses` above does imply it, but only by elimination; this names what is actually at
        // 7 -- the THIRD case arm -- so "the written nop is gone" is a positive measurement.
        assertEquals(
            "return-void", packed.opcodeAt(7).name,
            "7 is `:pswitch_2`, not an alignment nop: the payload is already even-aligned at 8, so the " +
                "`nop # for alignment` the fixture's own comment marks at \"@8\" does not survive assembly",
        )

        val sparse = viewOf("sparseSwitch()V")
        assertEquals(listOf(0, 1, 4, 5, 6, 7, 8), sparse.addresses.toList(), "and here the nop at 7 does")
        assertArrayEquals(intArrayOf(8), sparse.payloadAddresses)
        assertEquals(
            "nop", sparse.opcodeAt(7).name,
            "7 IS the alignment nop here -- two case arms, so the payload would land at odd 7 -- and no " +
                "path ever reaches it, which is why it appears in no expectation",
        )

        // The switch is never at address 0 in this fixture file, deliberately: with the switch at 0,
        // "offset relative to the switch" and "offset relative to the method start" are the same number
        // and the decode below could be wrong without any expectation moving. Asserted on what is AT
        // address 0 -- `ADDRESS_SWITCH > 0` would have asserted `1 > 0`, i.e. a fact about this file's own
        // literal, inside the one test whose job is to measure rather than to trust.
        assertEquals("goto", packed.opcodeAt(0).name, "`packedSwitch()V` puts a goto first on purpose")
        assertEquals(
            ADDRESS_FALL_THROUGH, packed.nextAddress(ADDRESS_SWITCH),
            "the fall-through is switchAddress + 3, because `31t` is three code units -- SPEC",
        )

        val zeroKeyed = viewOf("packedSwitchIncludingKeyZero()V")
        assertEquals(listOf(0, 1, 4, 5, 6, 7, 8), zeroKeyed.addresses.toList())
        assertArrayEquals(intArrayOf(8), zeroKeyed.payloadAddresses)
        assertEquals(
            "nop", zeroKeyed.opcodeAt(0).name,
            "and every fixture added by this task puts a nop first, for the same reason the two migrated " +
                "ones put a goto there",
        )

        val fallThroughCase = viewOf("packedSwitchWhoseCaseTargetsItsFallThrough()V")
        assertEquals(listOf(0, 1, 4, 5, 6), fallThroughCase.addresses.toList())
        assertArrayEquals(intArrayOf(6), fallThroughCase.payloadAddresses)

        val descending = viewOf("sparseSwitchWithDescendingKeys()V")
        assertEquals(listOf(0, 1, 4, 5, 6, 7, 8), descending.addresses.toList())
        assertArrayEquals(intArrayOf(8), descending.payloadAddresses)

        val empty = viewOf("packedSwitchWithEmptyTable()V")
        assertEquals(listOf(0, 1, 4, 5, 6), empty.addresses.toList())
        assertArrayEquals(intArrayOf(6), empty.payloadAddresses)

        val duplicate = viewOf("sparseSwitchWithDuplicateKeys()V")
        assertEquals(listOf(0, 1, 4, 5, 6, 7, 8), duplicate.addresses.toList())
        assertArrayEquals(intArrayOf(8), duplicate.payloadAddresses)
    }

    /**
     * **THE PAIR, and it is the whole of Step 2's consumer work.** On a **`Complete`** run
     * `wasReached(payload)` is `false` -- and [org.cf.smalivm.core.ExecutionGraphView.wasReached]'s own
     * KDoc tells a consumer that a completion check is what makes a `false` a dead-code PROOF. That
     * would have been enough for a dead-code strategy to delete a switch's table and break every switch
     * referencing it, which is not hypothetical: `docs/architecture/graph-optimizer-contract.md:136`
     * records that the OLD dead-removal strategy needed an explicit payload carve-out **in production**,
     * and it got there the ugly way (the old engine executed payloads, so their addresses were reached,
     * and Simplify still needed the special case).
     *
     * So the negative is never asserted alone. `isPayload(address)` on
     * [org.cf.smalivm.core.ExecutionGraphView] is the companion query a consumer must consult, and this
     * is the one place in this plan where an unqualified `wasReached` negative is legitimate: it is
     * *unconditionally* correct rather than completion-dependent, which is why it takes a companion query
     * instead of a completion gate (`CARRY-FORWARD.md` Plan 8 item 11 is the shape being avoided).
     *
     * The controls matter as much as the claim. `isPayload` must be `false` for the switch instruction
     * itself and for an ordinary `return-void`, or "everything is a payload" would satisfy the pair; and
     * the switch address must be *reached*, or a run that decoded nothing would satisfy it too.
     */
    @Test
    fun aPayloadIsDataSoItIsNeverReachedAndTheGraphSaysSoOnACompleteRun() {
        for (methodName in listOf("packedSwitch()V", "sparseSwitch()V")) {
            val graph = CoreTester.execute(CLASS_NAME, methodName, CoreState().apply { setRegisters(0, 1, "I") })
            assertEquals(CompletionStatus.Complete, graph.completion(), "$methodName explored everything")
            val payload = graph.methodView.payloadAddresses.single()
            assertTrue(payload in graph.addresses(), "$methodName: graph addresses include payload data")
            assertFalse(
                graph.wasReached(payload),
                "$methodName: a payload is DATA ([CON] B22 forbids control flow reaching it), so no op ran there",
            )
            assertTrue(
                graph.isPayload(payload),
                "$methodName: and the graph must SAY so, or a dead-code consumer reads the false above as " +
                    "proof and deletes the table -- graph-optimizer-contract.md:136 is the production precedent",
            )
            assertFalse(graph.isPayload(ADDRESS_SWITCH), "$methodName: the switch instruction itself is code")
            assertTrue(graph.wasReached(ADDRESS_SWITCH), "$methodName: ...and it ran, so this run decoded something")
            assertFalse(graph.isPayload(ADDRESS_FALL_THROUGH), "$methodName: and so is an ordinary return-void")
        }
    }

    /**
     * **A DECIDED selector takes exactly ONE transition, and the child list is what makes that a claim.**
     *
     * **The standing rule needs stating precisely here, because this task measured it narrower than it had
     * been relayed for five tasks.** *"Migrated tests are blind to fork arity"* holds only for a spurious
     * path that **adds no address**. It is false for over-forking a switch: the mirror mutation -- route a
     * provable selector through the unprovable row -- kills **11**, and **both** migrated
     * `testKnownPredicateVisitsExpectedLabel`s and **both** migrated `testUnhandledPredicateVisitsNextOp`s
     * are among them, because those expectations are EXACT sets and an extra arm adds addresses to them.
     * So the migrated set assertions do catch this one.
     *
     * What they cannot catch is arity at an address they already contain -- a second node at one address
     * (see [aCaseTargetingItsOwnFallThroughIsOneSuccessorRatherThanTwoNodesAtOneAddress]) or an extra
     * successor whose target is already in the set. That is why the assertions below are on
     * `children`/`outcomes` rather than on visitation, and why forking a provable selector is graded as a
     * defect in its own right: it invents live branches, which this plan family has shipped three times.
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
            "100 is in no table, so Dalvik falls through -- SPEC, both [BC] rows end with that sentence",
        )
        assertEquals(1, unmatched.outcomes.size)
    }

    /**
     * **An UNPROVABLE selector forks the fall-through FIRST and then every distinct target, in payload
     * order.** Two claims in one assertion, and each needs the other:
     *  - **arity**, because a set-valued visitation assertion cannot count paths;
     *  - **construction order**, because the op's successor list is deterministic even though public
     *    `graph.outcomes` order has no semantic meaning. Arms are pushed onto the LIFO frontier
     *    reversed, so the first declared arm becomes the first child.
     *
     * **This is the switch's own focused construction-order unit.** It does not define public outcome order.
     *
     * Fall-through first also matches `IfOp`, and deliberately does NOT match the oracle, which appends
     * it LAST. That is not a parity break: the oracle's unknown-selector children come out in `HashMap`
     * iteration order, so **its switch fork order is not well defined at all** and is not a parity
     * requirement.
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
     * **A case whose target IS the fall-through is ONE successor, not two.** Dedup is over distinct
     * successor ADDRESSES with the fall-through included -- "every distinct target as a `Jump`" dedupes
     * among targets but not against `Continue`'s `switchAddress + 3`, and an offset of `+3` is legal and
     * is what an obfuscator emits for an opaque predicate.
     *
     * Harmless for consensus (the join is idempotent) and NOT harmless otherwise: two `SuccessorSpec`s at
     * one address build two nodes and produce two `Outcome`s for one feasible path. Path-local visit budgets
     * prevent sibling interference, but they do not make duplicate paths semantically correct.
     * `nodePile(4).size` is the assertion that sees it; the child count alone would not, because
     * `addChild` appends and nothing dedupes downstream.
     *
     * **Both selector kinds, because they reach the coincidence through different code and only one of them
     * tests the SPELLING.** The unprovable half exercises the dedup in the fork row. The provable half
     * exercises `dispatchOn`'s answer, where the *matching target* happens to equal the fall-through: there
     * the op must emit `OpArm.Continue` and not `OpArm.Jump(fallThroughAddress)`. That difference is
     * invisible to the graph -- `Continue` resolves to `view.nextAddress`, which is the same address, so both
     * spellings build the same single node -- so it is asserted on the op's own arm list, which is the only
     * place it is observable at all. It matters because `Continue` is what says "this op did not choose a
     * target here", and a `Jump` to the fall-through would make a listing or editor believe a case edge
     * exists where the table merely names the next instruction.
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
        assertEquals(1, graph.nodePile(ADDRESS_FALL_THROUGH).size, "ONE node at that address, not two")
        assertEquals(2, graph.outcomes.size, "and two ended paths, not three")

        // The PROVABLE half: key 0 is decided, its target IS the fall-through, and the arm must be
        // spelled `Continue`.
        val view = viewOf(method)
        val frame = Frame.entryFrame(view)
        frame.assign(0, Value.Known(0, INT))
        assertEquals(
            listOf(OpArm.Continue()), switchOpOf(method).execute(frame).arms,
            "a DECIDED case whose target is the fall-through is a Continue, never a Jump to that address",
        )
        val decided = CoreTester.execute(CLASS_NAME, method, CoreState().apply { setRegisters(0, 0, "I") })
        assertEquals(CompletionStatus.Complete, decided.completion())
        assertEquals(listOf(ADDRESS_FALL_THROUGH), childAddressesOfSwitch(decided), "one successor")
        assertEquals(1, decided.nodePile(ADDRESS_FALL_THROUGH).size, "and one node there")
    }

    /**
     * **A definitely-NULL selector dispatches on key `0`.** **SPEC**, [BC]'s "General design": *"In terms
     * of bitwise representation, `(Object) null == (int) 0`."* So a null selector's 32 bits are provably
     * zero and case 0 -- if the table has one -- is the only feasible arm.
     *
     * Two wrong answers this excludes, one in each direction of spec §2.2. Treating a null raw as a
     * *reference* (the plausible reading, and what `Value.readsAsNullReference` would say) makes no case
     * match and takes the fall-through, leaving a feasible arm unexplored on a `Complete` run. Treating
     * it as unprovable forks all three arms, inventing two infeasible paths.
     *
     * **The rule is not restated here: `provableInt` owns it** ([org.cf.smalivm.core.provableInt]'s row
     * 1), which is why the mutation that kills this test also kills named `core/op/IfOpTest` tests -- one
     * rule, one implementation, two op families. The fixture needs a table containing key 0, which
     * neither migrated fixture has, or this answer and the fall-through answer would be the same address
     * set.
     *
     * The ORACLE gets this wrong and it is registry disagreement **D7b**, adjudicated by disassembling
     * its compiled `execute`: `Utils.getIntegerValue(null)` returns null, `HashMap.containsKey(null)` is
     * false, and it falls through. It agrees by accident whenever the table has no key `0`, which is why
     * both migrated fixtures hide it.
     */
    @Test
    fun aDefinitelyNullSelectorDispatchesOnKeyZeroBecauseItsBitsAreZero() {
        val nulled = CoreState().apply { setRegisters(0, null, "Ljava/lang/String;") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", nulled, intArrayOf(0, 1, 5))
        val graph = CoreTester.execute(CLASS_NAME, "packedSwitchIncludingKeyZero()V", nulled)
        assertEquals(listOf(5), childAddressesOfSwitch(graph), "key 0's target, and it is the only successor")
    }

    /**
     * **A live pointer matches no case and falls through, without forking** -- both spellings of a
     * pointer, because this engine has two ([org.cf.smalivm.core.HeapRef] for a tracked mutable, a host
     * object for everything else) and a row keyed on only one of them would silently take the other
     * branch.
     *
     * Half of this is **provable** and half is an engine **CHOICE**, and they are worth separating:
     *  - key `0` provably cannot match, because a live pointer is provably non-zero -- the same [BC]
     *    sentence as the null row, read the other way. `packedSwitchIncludingKeyZero()V` is the fixture
     *    that exercises it.
     *  - the NON-ZERO keys are a choice. The lattice does not hold a pointer's numeric value, so strictly
     *    it cannot prove `pointer != 1`. This op answers "a reference's bits are a heap address and are
     *    none of this table's keys", which is what a device does for real code (references are aligned
     *    heap addresses; switch keys in compiled code are small) and which is unreachable in verified
     *    input anyway -- [ART]'s verifier requires an integral selector, so a reference here is
     *    VERIFIER-invalid by construction. Recorded as a choice in `DALVIK-SEMANTICS.md`'s *"Choices this
     *    plan makes"* table.
     *
     * The mirror answer -- fork every arm -- is what an implementation without this row does, and it is
     * the invents-a-live-branch defect on the corpus's own fixture: `packedSwitch()V` with a reference
     * selector would report addresses 5, 6 and 7 live when a device reaches none of them.
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

        // A HeapRef raw, which reaches the same row by the other spelling -- `CoreState` turns a host
        // array seed into a tracked array, so v0 holds Known(HeapRef, "[B").
        val heapRef = CoreState().apply { setRegisters(0, byteArrayOf(0x1), "[B") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitch()V", heapRef, intArrayOf(0, 1, 4))

        // And the PROVABLE half, against a table that does contain key 0.
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", hostObject, intArrayOf(0, 1, 4))
        CoreTester.testVisitation(CLASS_NAME, "packedSwitchIncludingKeyZero()V", heapRef, intArrayOf(0, 1, 4))
    }

    /**
     * **A `Float` selector is UNPROVABLE, so every arm is explored** -- registry disagreement **D6**, the
     * same cross-domain defect as `if`'s at a second op family. A device reads the register's 32 bits, so
     * `2.9f` is `0x4039999A` and matches no small key; the oracle reads the host box's numeric *value*
     * through `Utils.getIntegerValue` (`opcode/SwitchPayloadOp.kt:33`) and dispatches `2.9f` to case
     * **2**, confidently and wrongly.
     *
     * The core could fold this exactly -- `Float.floatToRawIntBits` is a JDK call on a raw it holds -- and
     * deliberately does not, because that is `Coerce`'s rule to change and not this op's: three of four
     * refused raw/width combinations are exactly determinable and `DALVIK-SEMANTICS.md`'s spec-2a section
     * says so. `Unknown` is the sound direction meanwhile (explore more, spec §2.2), so this is a
     * precision loss and not a wrong answer.
     *
     * **Not corpus-observable**: all six migrated seeds hold `Integer` raws, which is why this row needs
     * its own test.
     */
    @Test
    fun aFloatSelectorIsUnprovableSoEveryArmIsExplored() {
        val float = CoreState().apply { setRegisters(0, 2.9f, "F") }
        CoreTester.testVisitation(CLASS_NAME, "packedSwitch()V", float, intArrayOf(0, 1, 4, 5, 6, 7))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5, 6, 7),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, "packedSwitch()V", float)),
            "and it FORKS rather than picking case 2 the way the oracle's intValue() does",
        )
    }

    /**
     * **A `size == 0` table falls through, and needs no row of its own.** **[ART]** says so in two
     * functions with two different comments, so both are cited: `DoPackedSwitch`'s
     * `// Empty packed switch, move forward by 3 (size of PACKED_SWITCH).`
     * (`runtime/interpreter/interpreter_common.h:453`, quoted in full -- an earlier revision of
     * `DALVIK-SEMANTICS.md` dropped the parenthetical and substituted a period without marking the
     * elision) guarding `return 3;` at `:454`, and `DoSparseSwitch`'s
     * `// Return length of SPARSE_SWITCH if size is 0.` at `:480` guarding `if (size == 0) { return 3; }`
     * at `:481-483`. A `size` of 0 is representable -- the field is a `ushort` -- and is not forbidden by
     * [BC].
     *
     * **It falls out of the general rule rather than being special-cased**, which is the point of testing
     * it: with no cases, a provable selector matches nothing and takes the fall-through, and an
     * unprovable one forks over the fall-through plus zero targets. Both are `listOf(Continue)` with no
     * `if (cases.isEmpty())` anywhere. The empty table IS assemblable from smali, measured rather than
     * assumed -- `.packed-switch 0x1` / `.end packed-switch` with no labels produces a four-code-unit
     * payload with an empty element list.
     */
    @Test
    fun anEmptyTableFallsThroughWhateverTheSelectorIs() {
        val method = "packedSwitchWithEmptyTable()V"
        assertTrue(
            switchOpOf(method) is SwitchOp,
            "an empty table still decodes to a real switch -- an UnsupportedOp aborts the path, which " +
                "would make `Complete` fail rather than making the assertions below pass, but say it anyway",
        )
        val known = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, known, intArrayOf(0, 1, 4))
        val unknown = CoreState().apply { setRegister(0, Value.Unknown(INT)) }
        CoreTester.testVisitation(CLASS_NAME, method, unknown, intArrayOf(0, 1, 4))
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH),
            childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, unknown)),
            "an unprovable selector over an empty table forks over NOTHING, so one successor",
        )
    }

    /**
     * **A DESCENDING sparse table forks over both dispatches, because neither is provably the runtime's.**
     *
     * ART binary-searches the raw key array (`interpreter_common.h:488-500`, `return entries[mid]` at
     * `:498`, a bare `return 3;` at `:502` when the loop exits), so on a table that is not strictly
     * ascending some keys are silently **unreachable** and their selectors fall through. A map lookup
     * finds every key. On `sparseSwitchWithDescendingKeys()V` -- keys 2 then 1 -- selector **1** is the
     * separating input: the search reads `keys[0] == 2`, goes left, finds nothing, and falls through to
     * 4; a map finds key 1 and takes 6.
     *
     * **The condition is VERIFIER-tier, so this is a CHOICE and it is made here rather than left open.**
     * [CON] A8: *"All targets of a sparse-switch instruction must be opcodes within the same method. The
     * corresponding table must be consistent and sorted low-to-high."*, and [ART] is stricter still --
     * `FailSparseSwitchPayloadKeyOrder` rejects duplicates too, via `if (UNLIKELY(key <= last_key))`
     * (`runtime/verifier/method_verifier.cc:2315`). No verified input can reach this, and obfuscated
     * input is not verified. So the honest statement is NOT "the binary search is the only way to agree
     * with the runtime" -- that overstates one runtime's implementation at [ART] tier, and a different
     * runtime could linear-scan.
     *
     * **Decision: fork over the union.** Spec §2.2 says an unprovable *feasibility* explores MORE, and
     * which dispatch a runtime uses is exactly that kind of unprovable: taking only the search's answer
     * or only the map's is a single confident answer resting on an implementation detail. So the
     * successors are ART's answer (a target, or the fall-through) united with every case whose key
     * matches, deduped by address. **On a strictly ascending table the two answers are IDENTICAL, so this
     * costs nothing on any verifiable input** -- which is why there is no `isAscending` flag anywhere:
     * the union IS the rule, and it collapses to one successor by itself.
     *
     * **This is a behavioural divergence from the ORACLE, not merely a tiering question**, because the
     * oracle dispatches BOTH kinds through one `HashMap.containsKey` (`opcode/SwitchPayloadOp.kt:34`,
     * table built at `:78-81`) and never distinguishes packed from sparse. Registered as
     * `switch-sparse-descending-keys` in `Fixtures.intendedDivergences`. Packed needs no entry: dexlib2
     * synthesizes packed keys as `firstKey + index` in wrapping `int` arithmetic, so a map over the
     * expanded keys reproduces ART's wrapping subtraction exactly and the two engines agree.
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

        // THE CONTROL, and it is what makes the fork above a claim about malformed tables rather than
        // about sparse switches: selector 2 is the search's FIRST probe, so both dispatches agree and
        // exactly one successor survives the union.
        val foundByBoth = CoreState().apply { setRegisters(0, 2, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, foundByBoth, intArrayOf(0, 1, 5))
        assertEquals(
            listOf(5), childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, foundByBoth)),
            "the union of two identical answers is one successor",
        )

        // And the ASCENDING table -- the shape all verifiable input has -- must be unaffected in both
        // directions, or the union rule would have widened every ordinary sparse switch into a fork.
        CoreTester.testVisitation(
            CLASS_NAME, "sparseSwitch()V", CoreState().apply { setRegisters(0, 1, "I") }, intArrayOf(0, 1, 5),
        )
    }

    /**
     * **DUPLICATE KEYS, which is the input a `Map<Int, Int>` structurally cannot represent** -- and the
     * half of this op's interface deviation that was justified in prose and not pinned until now. A map
     * keeps one entry per key, so building one from `sparseSwitchWithDuplicateKeys()V`'s table silently
     * loses an arm; an ordered `List<Case>` keeps both.
     *
     * **Assemblable, measured, not assumed**: `.sparse-switch { 0x1 -> :a; 0x1 -> :b }` produces a payload
     * whose element list is `(k=1, off=4) (k=1, off=5)`. [ART]'s verifier rejects it harder than [CON] A8
     * does -- `CheckAndMarkSwitchTargets` fails on `if (UNLIKELY(key <= last_key))`
     * (`method_verifier.cc:2315`), i.e. on `<=` and not merely on `<` -- so it is VERIFIER-invalid and
     * therefore reachable only in the unverified input this engine exists for.
     *
     * **Both arms are successors and the fall-through is not.** ART's binary search hits key 1 on its first
     * probe, so the runtime does not fall through; *which* arm it lands on is an artifact of the search's
     * midpoint, and a linear-scan runtime would take the first. Neither is provably the runtime's, so the
     * union of both is what spec §2.2 requires -- the same rule as the descending table, reached by a
     * different malformation. A `LinkedHashMap`-backed decode passes every other test in this suite and
     * fails this one.
     */
    @Test
    fun aDuplicateKeyKeepsBothArmsWhichAMapCouldNotRepresent() {
        val method = "sparseSwitchWithDuplicateKeys()V"
        val selector = CoreState().apply { setRegisters(0, 1, "I") }
        CoreTester.testVisitation(CLASS_NAME, method, selector, intArrayOf(0, 1, 5, 6))
        assertEquals(
            listOf(5, 6), childAddressesOfSwitch(CoreTester.execute(CLASS_NAME, method, selector)),
            "both arms of the repeated key, in payload order, and NO fall-through -- the search hits",
        )
        assertEquals(
            "sparse-switch r0, [1 -> :addr_5, 1 -> :addr_6]",
            switchOpOf(method).toString(),
            "and the decoded op still carries both entries, which is the claim a map would break",
        )
    }

    /**
     * **A switch reads its selector and assigns NOTHING**, and both halves are real rather than capacity
     * hints: `ExecutionGraph.availableRegisters` is purely static, so an op's declared read/assign sets
     * are the sole guarantors of a correct free-register answer for
     * `docs/architecture/graph-optimizer-contract.md:136`'s consumer -- **Dead removal**, which is the row
     * that *"reads read/assignment metadata"*, and the same row this task cites for the payload carve-out.
     * (`:138` is **Unreflection**, which consumes register LAYOUT and is the free-register consumer in the
     * `availableRegisters` sense; both are real consumers of these sets and the read/assignment one is
     * `:136`. Two sibling citations in `core/op/IfOp.kt` and `core/op/IfOpTest` name only `:138` for the
     * read/assignment claim and are wrong in the same way -- corrected in the same commit as this, since
     * leaving a known-bad citation next to a fixed one is how the class propagates.)
     *
     * The oracle declares `registersAssignedCount = 2` (`opcode/SwitchOp.kt:16`) and it is **honest
     * there**: its `execute` really does write two things -- the selector into the result register and
     * the fall-through address into a pseudo-return-address slot -- because that is how it smuggles them
     * to the payload op. This core has neither channel, so the count is 0 and **must not be "restored to
     * parity"**: `state-model.md:80` records the oracle's counts as *"only initial-capacity hints ... not
     * effect contracts"*.
     *
     * **Not vacuous, and the control is the non-empty read set, NOT the completion gate.** A bare
     * `assertEquals(emptySet(), registersAssigned(1))` passes HARDEST when nothing works -- the base `Op`
     * answers `emptySet()`, so an `UnsupportedOp`, or no op at all, satisfies it. Both queries are static
     * (`ExecutionGraph.kt:198-199` reads the decoded op and no node), so the `Complete` and `wasReached`
     * assertions prove nothing about them and are here only so a fixture that stops decoding fails loudly.
     *
     * **And the emptiness is pinned by a mutation that ADDS an override, which is the same methodology the
     * side-effect claim below uses** ("declare, pin, mutate"): giving [SwitchOp] `registersAssigned =
     * setOf(selectorRegister)` -- the oracle's honest-but-inapplicable count, transcribed -- kills this test
     * and only this test. A row that merely asserts the inherited default is not a claim about this op;
     * the mutation is what makes it one.
     */
    @Test
    fun aSwitchReadsOnlyItsSelectorAndAssignsNothing() {
        val graph = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 1, "I") })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_SWITCH), "the fixture still decodes and runs")
        assertEquals(setOf(0), graph.registersRead(ADDRESS_SWITCH), "the selector, and nothing else")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(ADDRESS_SWITCH),
            "a switch chooses a successor; it writes no register -- unlike the oracle's two out-of-band writes",
        )
    }

    /**
     * **Side effects are `NONE` on every row, declared and pinned.** No `SwitchOp` arm declares an effect.
     * A switch chooses a successor and touches nothing outside its own frame -- there is no register write
     * and no heap write to over-approximate.
     *
     * The mutation that proves this ADDS a declaration rather than removing one, because a spurious
     * `STRONG` is the silent and expensive direction: `methodSideEffectLevelOverExploredPaths` maxes over
     * addresses, so one `STRONG` switch would stop every constant fold in the method -- and
     * control-flow-flattened code, which is what this tool exists to undo, is one giant switch.
     */
    @Test
    fun aSwitchDeclaresNoSideEffectOnEveryRow() {
        val decided = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply { setRegisters(0, 1, "I") })
        assertEquals(SideEffectLevel.NONE, decided.sideEffectLevel(ADDRESS_SWITCH), "a decided switch mutates nothing")
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
            SideEffectLevel.NONE, forked.sideEffectLevel(ADDRESS_SWITCH),
            "and an ambiguous selector is still not an unmodellable write -- it is four successors",
        )

        val pointer = CoreTester.execute(CLASS_NAME, "packedSwitch()V", CoreState().apply {
            setRegisters(0, "not a number", "Ljava/lang/String;")
        })
        assertEquals(SideEffectLevel.NONE, pointer.sideEffectLevel(ADDRESS_SWITCH), "nor is a reference selector")
    }

    /**
     * **PATH EXPLOSION IN A SINGLE OPCODE without sibling-path pruning.** 501 distinct targets plus
     * fall-through create 502 paths that each reach the same terminal address once. Address visits are
     * path-local, so reconvergence is not mistaken for 502 loop iterations and the default budget of 500
     * keeps every feasible path.
     *
     * **`SwitchOp` does NOT cap its fork arity** with `OpArm.Abort(POLICY_REFUSED)` above some N. (1) It
     * would refuse methods the engine handles: 501 forks in one `commit` completes here, and a 60000-case
     * table whose cases all target ONE address dedupes to a single successor -- a cap keyed on table size
     * would refuse it anyway. (2) The address limit already converts the cliff into a refusal, with a reason
     * that says which bound was hit. (3) Frontier width is a SCHEDULER concern, not one op's: N sequential
     * diamonds reach the same wall (measured, in `IfOpTest`), so a per-op cap would cover one construct out
     * of two. The residual: a maximal 65535-case table builds 65536 persistent frame wrappers inside one
     * `commit`; address and step limits act only after dequeue, and a `VirtualMachineError` escapes the run.
     *
     * The `POLICY_REFUSED` assertion below is what makes the decision falsifiable: adding a cap changes the
     * reason set.
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
            "and ONE node forked all of them, which is what makes this a single-opcode explosion",
        )
    }

    /**
     * **One malformed case target loses ITS path and keeps its siblings**, reporting
     * [AbortReason.MALFORMED_INPUT] rather than `HOST_FAILURE`: a switch table pointing off the end of the
     * method is the shape an obfuscator emits, and must stay distinguishable from a bug in this engine.
     *
     * **Driven through the session's decode seam, because no smali can express it**: the assembler resolves
     * every `.packed-switch` label to a placed location, so a case target that is not an address in the
     * method is not assemblable. The op built here is a REAL [SwitchOp] with a real fixture's view, so the
     * arm list under test is the one the op itself produces.
     *
     * A switch never produces a multi-spec commit group: each `Continue` and each `Jump` arm is planned as
     * its own group holding exactly ONE successor with `OnUnbuildable.SKIP_ONE`, so there is nothing for a
     * group to mix.
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
            "an address the method does not contain is malformed INPUT, never a bug in this codebase",
        )
        assertEquals(
            listOf(ADDRESS_FALL_THROUGH, 5), childAddressesOfSwitch(graph),
            "the two feasible siblings survive -- SKIP_ONE, because one bad clause is no reason to " +
                "discard a branch a device would take",
        )
        assertTrue(
            Outcome.Aborted(AbortReason.MALFORMED_INPUT) in graph.outcomes,
            "and the lost path is accounted for by its own Outcome: ${graph.outcomes}",
        )
    }

    /**
     * `opcode/SwitchPayloadOpTest.payloadOpHasExpectedToString` -- the second of this plan's two
     * adjudicated mockk change-detectors -- is **RETIRED, for two reasons and not one.**
     *
     * It was insensitive: the expected string interpolated the mocks' OWN `key`/`offset` values
     * (`"...[${element1.key} -> :addr_${element1.offset}, ..."`), so it could not fail on a wrong key or
     * a wrong offset -- only on punctuation. And, decisively, **the formatting it pinned is a recorded
     * defect**: `docs/architecture/opcodes.md:174` files Correctness Defect 19 as *"`SwitchPayloadOp`
     * labels relative offsets as addresses"*, verified at `opcode/SwitchPayloadOp.kt:50`, which appends
     * `" -> :addr_"` followed by an **offset**. Preserving it byte-identically would carry that defect
     * into the new core.
     *
     * **The coverage was NOT simply dropped, per the Plan 4 rule that cost 13 tests to learn.** Mutating
     * [SwitchOp.toString] (rendering the fall-through address instead of the case list) killed **0** of
     * the behavioral tests in this file, so the rendering is genuinely uncovered elsewhere -- and a real
     * `toString` row now lives in `core/OpBuilderTest.opsRenderTheirOperandsInTheirDeclaredOrder`, with
     * the other families' rows, asserting `packed-switch r0, [1 -> :addr_5, 2 -> :addr_6, 3 -> :addr_7]`.
     * The rendered numbers are **absolute addresses**, which is defect 19 fixed rather than reproduced:
     * this op resolves every target at decode time, so it has no offsets left to mislabel.
     */
    @Suppress("unused")
    private fun mockkAdjudication() = Unit

    private fun viewOf(methodDescriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(CLASS_NAME, methodDescriptor)

    private fun switchOpOf(methodDescriptor: String): Op =
        OpBuilder.build(ADDRESS_SWITCH, viewOf(methodDescriptor), CoreTester.classIndex.currentResolver())

    /**
     * The switch node's children **in arm order**, which is the only channel that can see fork
     * arity and fork order (a visitation set can see neither). One node at the switch address always:
     * nothing forks before it in any of these fixtures.
     */
    private fun childAddressesOfSwitch(graph: ExecutionGraph): List<Int> =
        graph.nodePile(ADDRESS_SWITCH).single().children.map { it.address }
}
