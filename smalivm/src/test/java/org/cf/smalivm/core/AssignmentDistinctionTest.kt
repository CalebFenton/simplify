package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * `Value.Top` means **three** different things, and before this test nothing in the tree could tell
 * them apart:
 *
 *  1. **a path genuinely holds a `Value.Top`** -- `MoveOp`'s `Source.Register` arm reads through
 *     [Frame.read], which is `peek(r) ?: Value.Top`, so `move v0, v2` from an untouched v2 assigns a
 *     real `Top`. [ExecutionGraph.registerOccupancyOverExploredPaths] answers
 *     [RegisterOccupancy.HELD].
 *  2. **the paths disagreed incompatibly**, so the join is `Top`. Also [RegisterOccupancy.HELD] --
 *     deliberately, because both mean "live and unknowable", and the number of disagreeing values is
 *     what [ExecutionGraph.registerValuesOverExploredPaths] is for.
 *  3. **nothing ever assigned the register** -- [ExecutionGraph.valueIn] turns `peek`'s null into
 *     `Top`. [RegisterOccupancy.NEVER_ASSIGNED].
 *
 * plus the two "there was nothing to ask" cases that come free with a total answer and are pinned
 * here rather than left to be discovered: an address no path ran an op at, and a method that never
 * returns normally. Both are [RegisterOccupancy.NO_PATH].
 *
 * **Why every test below asserts a PAIR.** One half proves nothing. "`consensus` is `Top`" is true for
 * all five facts, and "the register was held" is uninteresting without the `Top` it explains -- so each
 * test pins the two together, and most pin two registers (or one register at two addresses) whose
 * *existing* observables are identical.
 *
 * **Why this is a distinct test class rather than rows in `ExecutionGraphTest`.** The subject is not one
 * query, it is whether a set of queries is jointly sufficient to distinguish facts a consumer would act
 * on differently -- `AnalysisContracts.kt` says the consumer "would ACT on -- fold a constant, delete a
 * method". `Ldifferential/branch;` exists for the same reason; its own comment says which method carries
 * which fact.
 */
class AssignmentDistinctionTest {

    private companion object {
        const val CLASS = "Ldifferential/branch;"
        const val DISAGREEING = "disagreeingArms(I)V"
        const val PROVABLY_ZERO = "provablyZeroSkipsAnAddress()V"
        const val MOVE_UNTOUCHED = "moveFromAnUntouchedRegister()V"
        const val NEVER_RETURNS = "neverReturnsNormally(Ljava/lang/RuntimeException;)V"

        /** The join of both arms of [DISAGREEING], where the pile holds two nodes. */
        val JOIN = CoreTester.classIndex.requireMethodView(CLASS, DISAGREEING).let { view ->
            view.addresses.single { view.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.RETURN_VOID }
        }

        /** [PROVABLY_ZERO]'s dead fall-through, and its `return-void`. */
        const val DEAD = 3
        const val AFTER_BRANCH = 4
    }

    private fun completed(descriptor: String, className: String = CLASS): ExecutionGraph {
        val graph = CoreTester.execute(className, descriptor)
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "$descriptor must complete, or every negative below is merely unexplored",
        )
        return graph
    }

    private fun trustedOf(graph: ExecutionGraph): TrustedGraphView {
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete run must yield a trusted view")
        return trusted!!
    }

    @Test
    fun disagreeingArmsAndAnUntouchedRegisterAreBothTopAndAreNoLongerTheSameAnswer() {
        // (a) and (b) of the four cases, at ONE address, so a single `consensus` call answers `Top` for
        // both and only the new query separates them. v0 is assigned `Known(1, I)` on one arm and
        // `Known("x", Ljava/lang/String;)` on the other -- `joinTypes` calls a primitive and a reference
        // incompatible, so the join is `Top`. v1 is assigned by nothing in the method at all.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        assertTrue(graph.wasReached(JOIN), "the join must be reached, or (b) is really (c)")
        assertEquals(2, graph.nodePile(JOIN).size, "both arms must reach the join, or the join is idempotent")

        // The SAME `Top` from both queries, for both registers: this is the conflation itself.
        assertEquals(Value.Top, graph.consensusOverExploredPaths(JOIN, 0))
        assertEquals(Value.Top, graph.consensusOverExploredPaths(JOIN, 1))
        assertEquals(Value.Top, trusted.terminatingConsensus(0))
        assertEquals(Value.Top, trusted.terminatingConsensus(1))

        // ...and the answers that are no longer the same.
        assertEquals(RegisterOccupancy.HELD, trusted.registerOccupancy(JOIN, 0), "the paths DISAGREED about v0")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(JOIN, 1),
            "nothing in the method assigns v1",
        )
        assertNotEquals(trusted.registerOccupancy(JOIN, 0), trusted.registerOccupancy(JOIN, 1))
        assertEquals(RegisterOccupancy.HELD, trusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(1))

        // The free precision `registerValues` already had, asserted so the new query is not credited with
        // it: a set of size two IS a disagreement proof today. What it cannot do is separate a singleton
        // `{Top}` into its two causes -- see the `moveFromAnUntouchedRegister` test, where the singleton
        // is identical for a held `Top` and an unassigned register.
        assertEquals(
            setOf(Value.Known(1, VirtualType.of("I")), Value.Known("x", VirtualType.of("Ljava/lang/String;"))),
            graph.registerValuesOverExploredPaths(JOIN, 0),
        )
        assertEquals(setOf<Value>(Value.Top), graph.registerValuesOverExploredPaths(JOIN, 1))
    }

    @Test
    fun aParameterIsHeldEvenThoughTheFramesOwnAssignedSetHasForgottenIt() {
        // Why `Frame.peek` is the raw material and `Frame.registersAssigned()` is not.
        // `Frame.entryFrame` seeds every parameter and then calls `assigned.clear()`, deliberately --
        // seeding is not an assignment BY the method. So a parameter register holding a perfectly live
        // `Unknown(T)` is absent from the dynamic assigned set, and sourcing this query from there would
        // report the commonest live register in any real method as "never assigned".
        val graph = completed(DISAGREEING)
        val frame = graph.nodePile(JOIN).first().frame
        assertEquals(Value.Unknown(VirtualType.of("I")), frame.peek(2), "v2 is the (I) parameter")
        assertFalse(2 in frame.registersAssigned(), "entryFrame clears `assigned` after seeding parameters")
        assertEquals(RegisterOccupancy.HELD, graph.registerOccupancyOverExploredPaths(JOIN, 2))

        // The static liveness query is a third answer again, and is not a substitute either: it reads the
        // decoded op's DECLARED destination set and no frame at all, so at a `return-void` it is empty for
        // every register including the live parameter.
        //
        // The empty-set assertion needs a NON-EMPTY control at another address, because the base `Op`
        // answers `emptySet()` too -- so on its own it passes hardest when nothing works (an unbuilt op or
        // an `UnsupportedOp` satisfies it). Address 2 is the `const/4 v0` on the fall-through arm, which
        // declares v0, so a real decoded op is demonstrably answering.
        assertEquals(setOf(0), graph.registersAssigned(2), "the control: `const/4 v0` declares v0")
        assertEquals(emptySet<Int>(), graph.registersAssigned(JOIN), "`return-void` declares no destination")
        assertFalse(graph.wasRegisterAssigned(JOIN, 2), "the static query cannot see a parameter either")
    }

    @Test
    fun anAddressNoPathAttemptedAndAnUnassignedRegisterAreBothTopForTheSameRegister() {
        // (c), placed next to (b) for the SAME register so the third reading is what separates them.
        // `const/4 v0, 0x0` makes the predicate provably zero, so `if-eqz` emits one transition and the
        // fall-through is never even attempted.
        val graph = completed(PROVABLY_ZERO)
        val trusted = trustedOf(graph)
        // The control that stops both negatives below from being vacuous: an `assertFalse(wasReached(n))`
        // is silently true for an address that does not exist.
        assertTrue(DEAD in graph.addresses().toList(), "address $DEAD must be a real address in this method")
        assertEquals(1, graph.nodePile(AFTER_BRANCH).size, "the predicate must be DECIDED, not forked")

        assertFalse(graph.wasAttempted(DEAD), "the provably-zero predicate must not schedule the fall-through")
        assertFalse(graph.wasReached(DEAD))
        assertTrue(graph.wasReached(AFTER_BRANCH))

        assertEquals(Value.Top, graph.consensusOverExploredPaths(DEAD, 1))
        assertEquals(Value.Top, graph.consensusOverExploredPaths(AFTER_BRANCH, 1))
        assertEquals(RegisterOccupancy.NO_PATH, trusted.registerOccupancy(DEAD, 1), "no node ran an op here")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(AFTER_BRANCH, 1),
            "a node ran here and did not hold v1",
        )

        // The one distinction `registerValues` already makes, recorded so the new query is not credited
        // with it: the empty set is exactly NO_PATH. It is still not a substitute -- see the singleton
        // `{Top}` case in the next test -- and it forces a consumer to reason about set emptiness rather
        // than reading an answer.
        assertEquals(emptySet<Value>(), graph.registerValuesOverExploredPaths(DEAD, 1))
        assertEquals(setOf<Value>(Value.Top), graph.registerValuesOverExploredPaths(AFTER_BRANCH, 1))
    }

    @Test
    fun aFrameHeldTopAndAnUnassignedRegisterHaveIDENTICALValuesAndDifferentOccupancy() {
        // The case that shows this query is not a re-spelling of `registerValues`, and the one the brief
        // for this task got wrong: a singleton `{Top}` is NOT "a path holds a real Top". It is that OR
        // "no path assigned it" OR any mixture, because `valueIn` maps `peek`'s null onto the same
        // `Value.Top` a frame can hold.
        //
        // `move v0, v2` with v2 untouched: `MoveOp` reads through `Frame.read`, which is
        // `peek(r) ?: Value.Top`, so v0 holds a REAL `Top` while v1 and v2 hold nothing.
        val graph = completed(MOVE_UNTOUCHED)
        val trusted = trustedOf(graph)

        // The mechanism, asserted on the frame itself rather than inferred from the query that reads it.
        val frame = graph.nodePile(1).first().frame
        assertEquals(Value.Top, frame.peek(0), "`move` from an untouched register assigns a real Top")
        assertNull(frame.peek(1), "nothing assigned v1")
        assertNull(frame.peek(2), "nothing assigned v2 -- it is what `move` read")

        // Every existing observable is IDENTICAL for all three registers.
        for (register in 0..2) {
            assertEquals(Value.Top, graph.consensusOverExploredPaths(1, register), "consensus r$register")
            assertEquals(
                setOf<Value>(Value.Top), graph.registerValuesOverExploredPaths(1, register),
                "registerValues r$register",
            )
            assertEquals(Value.Top, trusted.terminatingConsensus(register), "terminatingConsensus r$register")
        }

        // ...and the new one is not.
        assertEquals(RegisterOccupancy.HELD, trusted.registerOccupancy(1, 0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(1, 1))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(1, 2))
        assertEquals(RegisterOccupancy.HELD, trusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(1))
    }

    @Test
    fun aMethodThatNeverReturnsIsNotAMethodWhoseReturnValueIsUnknowable() {
        // (d), the case the carry-forward register named as its own motivation and could not express.
        // Every path throws, so the returning-node set is empty and `terminatingConsensus` answers `Top`
        // for EVERY register -- including one that is demonstrably held on the throwing path.
        val throwing = completed(NEVER_RETURNS)
        val throwingTrusted = trustedOf(throwing)
        assertTrue(throwing.outcomes.all { it is Outcome.Threw }, "no path may return, or this tests nothing")

        assertFalse(throwingTrusted.didAnyPathReturn(), "this method never returns normally")
        assertEquals(Value.Top, throwingTrusted.terminatingConsensus(0))
        assertEquals(Value.Top, throwingTrusted.terminatingConsensus(1))
        assertEquals(RegisterOccupancy.NO_PATH, throwingTrusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NO_PATH, throwingTrusted.terminatingRegisterOccupancy(1))

        // The sharp half: v1 IS held -- it is the thrown parameter -- and the per-address query says so.
        // "No path returned" is therefore a fact about the RUN and not about the register, which is why
        // one boolean about assignment could never have carried it.
        assertEquals(
            Value.Unknown(VirtualType.of("Ljava/lang/RuntimeException;")),
            throwing.consensusOverExploredPaths(0, 1),
        )
        assertEquals(RegisterOccupancy.HELD, throwingTrusted.registerOccupancy(0, 1))

        // ...against a method that DOES return and whose return value is genuinely unknowable. Same
        // `terminatingConsensus`, opposite licence: a consumer may treat the first as "this method has no
        // normal result at all" and must treat the second as "live, do not fold".
        val returning = completed(DISAGREEING)
        val returningTrusted = trustedOf(returning)
        assertTrue(returningTrusted.didAnyPathReturn())
        assertEquals(Value.Top, returningTrusted.terminatingConsensus(0))
        assertEquals(RegisterOccupancy.HELD, returningTrusted.terminatingRegisterOccupancy(0))
    }

    @Test
    fun theHighHalfOfALiveWidePairReadsAsNeverAssignedAndAvailableRegistersIsWhatKnowsBetter() {
        // The fourth ambiguity, stated because it is measured rather than because it is feared, and it is
        // inherited from `Frame.peek` rather than introduced here: `Frame.assign` records a wide value's
        // high half as an internal `WideHigh` slot and `peek` answers null for it, so this query reports
        // NEVER_ASSIGNED for the high half of a live `J` parameter pair. That is the right answer to the
        // question asked ("does any path hold a readable value here") and the WRONG answer to the question
        // a consumer hunting scratch registers is asking -- which has its own owner, `availableRegisters`,
        // and that one accounts for wide pairs on both sides.
        val graph = completed("instanceWideParams(JLjava/lang/String;I)I", className = "Lspine/spine;")
        val at = 0
        assertEquals(RegisterOccupancy.HELD, graph.registerOccupancyOverExploredPaths(at, 4), "v4 is the J low half")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, graph.registerOccupancyOverExploredPaths(at, 5),
            "v5 is the J high half; `peek` answers null for it",
        )
        assertFalse(
            5 in graph.availableRegisters(at).toList(),
            "the high half of a live pair is NOT free, and this is the query that knows it",
        )
        assertTrue(1 in graph.availableRegisters(at).toList(), "a genuinely untouched local IS free")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, graph.registerOccupancyOverExploredPaths(at, 1),
            "so NEVER_ASSIGNED alone cannot mean 'free' -- v1 and v5 answer the same and differ",
        )
    }

    @Test
    fun bothQueriesAreTotalForARegisterTheMethodDoesNotHave() {
        // Spec section 9: these queries must never throw on ordinary traversal. `Frame.peek` DOES throw
        // for an out-of-range register (`requireInRange`), so the range guard is not decoration -- it is
        // what `ExecutionGraph.valueIn` already does, and answering the same way as `consensus` for the
        // same input is the property that keeps the two queries about one fact.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        val absent = 99
        assertTrue(absent >= graph.methodView.registerCount, "register $absent must not exist in this method")
        assertEquals(Value.Top, graph.consensusOverExploredPaths(JOIN, absent))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(JOIN, absent))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(absent))
    }

    @Test
    fun theGatedTwinsDelegateExactlyAndAreUnreachableOnATruncatedRun() {
        // `ExecutionGraph.TrustedView`'s own contract: the gate decides WHO may act on an answer, never
        // WHAT the answer is. A re-derivation there -- even a "conservative" one -- would push consumers
        // back onto the ungated query, which is the hole the gate exists to close.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        for (register in 0..2) {
            assertEquals(
                graph.registerOccupancyOverExploredPaths(JOIN, register),
                trusted.registerOccupancy(JOIN, register),
                "gated and ungated must agree for r$register",
            )
            assertEquals(
                graph.terminatingRegisterOccupancyOverExploredPaths(register),
                trusted.terminatingRegisterOccupancy(register),
                "gated and ungated terminating must agree for r$register",
            )
        }
        assertEquals(graph.didAnyPathReturnOverExploredPaths(), trusted.didAnyPathReturn())

        // And the gate itself, on the truncation that actually matters. A run stopped before ANY path got
        // to a `return` answers `false` here for a method that plainly does return -- so on a run that is
        // not `canTrustConsensus`, a `false` is not the fact case (d) asserts, it is an artifact of where
        // the walk stopped. That is the same hazard `wasReached`'s KDoc records, arriving in a second
        // query, and it is why the gated twin exists rather than a KDoc warning.
        val truncated = CoreTester.execute(CLASS, DISAGREEING, limits = Limits(maxSteps = 1))
        assertTrue(truncated.completion() is CompletionStatus.Partial, "the run must have been cut short")
        assertNull(truncated.trusted(), "a Partial run may not hand out the gated twins")
        assertFalse(truncated.didAnyPathReturnOverExploredPaths(), "...while the ungated twin still answers")
        assertEquals(
            RegisterOccupancy.NO_PATH, truncated.terminatingRegisterOccupancyOverExploredPaths(0),
            "and so does the register-indexed twin, with the same caveat",
        )
        // The control that makes the two `false`s above a truncation artifact rather than a property of
        // the method: the SAME method on a complete run returns twice.
        assertTrue(trusted.didAnyPathReturn(), "disagreeingArms returns on both arms when allowed to finish")
    }

    /**
     * The consumer-reachability claim, written so the COMPILER checks it: this function can see nothing but
     * a [TrustedGraphView], and it still reaches all three verdicts a Plan 14 strategy needs to keep apart.
     *
     * That is not incidental. [ExecutionGraph.outcomes] is on neither [ExecutionGraphView] nor
     * [TrustedGraphView], and [ExecutionGraphView.wasReached]/[ExecutionGraphView.wasAttempted] are on the
     * ungated interface only -- so a consumer handed only the trusted view had no route to "no path
     * returned" at all before [RegisterOccupancy] existed, however many booleans it was given.
     *
     * The `error` branch is a checked invariant rather than defensive padding: if any path returned, the
     * returning-node set is non-empty, so [TrustedGraphView.terminatingRegisterOccupancy] cannot answer
     * [RegisterOccupancy.NO_PATH]. It is **evaluated** on every call and must **fire on none** -- and it does
     * fire, i.e. the test goes red, if the two queries are ever made to disagree, which is what M11 (invert
     * `didAnyPathReturn`) measures.
     */
    private fun verdictFrom(trusted: TrustedGraphView, register: Int): String =
        if (!trusted.didAnyPathReturn()) {
            "no normal result at all"
        } else {
            when (trusted.terminatingRegisterOccupancy(register)) {
                RegisterOccupancy.HELD -> "live and unknowable"
                RegisterOccupancy.NEVER_ASSIGNED -> "dead"
                RegisterOccupancy.NO_PATH ->
                    error("NO_PATH contradicts didAnyPathReturn() == true; the two queries disagree")
            }
        }

    @Test
    fun aConsumerHoldingONLYTheTrustedViewCanTellAllThreeApart() {
        assertEquals(
            "live and unknowable", verdictFrom(trustedOf(completed(DISAGREEING)), 0),
            "two arms disagreed about v0: do not fold it",
        )
        assertEquals(
            "dead", verdictFrom(trustedOf(completed(DISAGREEING)), 1),
            "nothing assigned v1 on any returning path",
        )
        assertEquals(
            "no normal result at all", verdictFrom(trustedOf(completed(NEVER_RETURNS)), 0),
            "every path throws, which licenses a different action from either verdict above",
        )
        // Three distinct verdicts from three inputs whose `terminatingConsensus` is the SAME `Value.Top`.
        val consensuses = listOf(
            trustedOf(completed(DISAGREEING)).terminatingConsensus(0),
            trustedOf(completed(DISAGREEING)).terminatingConsensus(1),
            trustedOf(completed(NEVER_RETURNS)).terminatingConsensus(0),
        )
        assertEquals(listOf<Value>(Value.Top, Value.Top, Value.Top), consensuses, "...and that is the point")
    }

}
