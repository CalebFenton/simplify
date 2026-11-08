package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * `Value.Top` from a consensus query means one of three things, which [RegisterOccupancy] separates:
 *
 *  1. A path holds a real `Top`: `move v0, v2` from an untouched v2 reads through [Frame.read], which is
 *     `peek(r) ?: Value.Top`. [RegisterOccupancy.HELD].
 *  2. The paths disagreed incompatibly and the join is `Top`. Also [RegisterOccupancy.HELD]: both mean "live and
 *     unknowable", and [ExploredPaths.registerValues] counts the disagreeing values.
 *  3. Nothing assigned the register; [ExecutionGraph.valueIn] turns `peek`'s null into `Top`.
 *     [RegisterOccupancy.NEVER_ASSIGNED].
 *
 * An address where no path ran an op, and a method that never returns normally, are [RegisterOccupancy.NO_PATH].
 * Each test asserts the occupancy together with the identical `Top` it explains, because either half alone
 * distinguishes nothing. `Ldifferential/branch;` documents which method carries which fact.
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
    fun disagreeingArmsAndAnUntouchedRegisterAreBothTopButHaveDifferentOccupancy() {
        // Cases 2 and 3 at one address. v0 is `Known(1, I)` on one arm and `Known("x", String)` on the other,
        // which `joinTypes` cannot join, so the join is `Top`. Nothing in the method assigns v1.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        assertTrue(graph.wasReached(JOIN), "the join must be reached, or this is the no-path case")
        assertEquals(2, graph.nodePile(JOIN).size, "both arms must reach the join, or the join is idempotent")

        // Both queries answer the same `Top` for both registers.
        assertEquals(Value.Top, graph.explored.consensus(JOIN, 0))
        assertEquals(Value.Top, graph.explored.consensus(JOIN, 1))
        assertEquals(Value.Top, trusted.terminatingConsensus(0))
        assertEquals(Value.Top, trusted.terminatingConsensus(1))

        // Occupancy separates them.
        assertEquals(RegisterOccupancy.HELD, trusted.registerOccupancy(JOIN, 0), "the paths disagreed about v0")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(JOIN, 1),
            "nothing in the method assigns v1",
        )
        assertNotEquals(trusted.registerOccupancy(JOIN, 0), trusted.registerOccupancy(JOIN, 1))
        assertEquals(RegisterOccupancy.HELD, trusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(1))

        // `registerValues` already proves a disagreement with a two-element set; it cannot split a singleton
        // `{Top}` into its two causes (see `aFrameHeldTopAndAnUnassignedRegisterHaveIdenticalValuesAndDifferentOccupancy`).
        assertEquals(
            setOf(Value.Known(1, VirtualType.of("I")), Value.Known("x", VirtualType.of("Ljava/lang/String;"))),
            graph.explored.registerValues(JOIN, 0),
        )
        assertEquals(setOf<Value>(Value.Top), graph.explored.registerValues(JOIN, 1))
    }

    @Test
    fun aParameterIsHeldEvenThoughTheFramesOwnAssignedSetHasForgottenIt() {
        // `Frame.entryFrame` clears `assigned` after seeding parameters, because seeding is not an assignment by
        // the method. Sourcing occupancy from `registersAssigned()` would call every parameter never assigned.
        val graph = completed(DISAGREEING)
        val frame = graph.nodePile(JOIN).first().frame
        assertEquals(Value.Unknown(VirtualType.of("I")), frame.peek(2), "v2 is the (I) parameter")
        assertFalse(2 in frame.registersAssigned(), "entryFrame clears `assigned` after seeding parameters")
        assertEquals(RegisterOccupancy.HELD, graph.explored.registerOccupancy(JOIN, 2))

        // The static liveness query reads the op's declared destinations and no frame, so at a `return-void`
        // it is empty for every register, the live parameter included. Address 2 (`const/4 v0`) is the
        // non-empty control: the base `Op` also answers `emptySet()`, so an unbuilt op would pass alone.
        assertEquals(setOf(0), graph.registersAssigned(2), "the control: `const/4 v0` declares v0")
        assertEquals(emptySet<Int>(), graph.registersAssigned(JOIN), "`return-void` declares no destination")
        assertFalse(graph.wasRegisterAssigned(JOIN, 2), "the static query cannot see a parameter either")
    }

    @Test
    fun anAddressNoPathAttemptedAndAnUnassignedRegisterAreBothTopForTheSameRegister() {
        // The no-path case next to case 3 for the same register. `const/4 v0, 0x0` makes the predicate
        // provably zero, so `if-eqz` emits one transition and the fall-through is never attempted.
        val graph = completed(PROVABLY_ZERO)
        val trusted = trustedOf(graph)
        // `assertFalse(wasReached(n))` is vacuously true for an address that does not exist.
        assertTrue(DEAD in graph.addresses().toList(), "address $DEAD must be a real address in this method")
        assertEquals(1, graph.nodePile(AFTER_BRANCH).size, "the predicate must be decided, not forked")

        assertFalse(graph.wasAttempted(DEAD), "the provably-zero predicate must not schedule the fall-through")
        assertFalse(graph.wasReached(DEAD))
        assertTrue(graph.wasReached(AFTER_BRANCH))

        assertEquals(Value.Top, graph.explored.consensus(DEAD, 1))
        assertEquals(Value.Top, graph.explored.consensus(AFTER_BRANCH, 1))
        assertEquals(RegisterOccupancy.NO_PATH, trusted.registerOccupancy(DEAD, 1), "no node ran an op here")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(AFTER_BRANCH, 1),
            "a node ran here and did not hold v1",
        )

        // `registerValues` answers the empty set exactly for NO_PATH, but a singleton `{Top}` still conflates
        // the other two cases (next test).
        assertEquals(emptySet<Value>(), graph.explored.registerValues(DEAD, 1))
        assertEquals(setOf<Value>(Value.Top), graph.explored.registerValues(AFTER_BRANCH, 1))
    }

    @Test
    fun aFrameHeldTopAndAnUnassignedRegisterHaveIdenticalValuesAndDifferentOccupancy() {
        // A singleton `{Top}` means "a path holds a real Top", "no path assigned it", or a mixture, because
        // `valueIn` maps `peek`'s null onto the same `Value.Top` a frame can hold. `move v0, v2` with v2
        // untouched makes v0 hold a real `Top` while v1 and v2 hold nothing.
        val graph = completed(MOVE_UNTOUCHED)
        val trusted = trustedOf(graph)

        // The mechanism, asserted on the frame itself.
        val frame = graph.nodePile(1).first().frame
        assertEquals(Value.Top, frame.peek(0), "`move` from an untouched register assigns a real Top")
        assertNull(frame.peek(1), "nothing assigned v1")
        assertNull(frame.peek(2), "nothing assigned v2 -- it is what `move` read")

        // Every consensus observable is identical for all three registers.
        for (register in 0..2) {
            assertEquals(Value.Top, graph.explored.consensus(1, register), "consensus r$register")
            assertEquals(
                setOf<Value>(Value.Top), graph.explored.registerValues(1, register),
                "registerValues r$register",
            )
            assertEquals(Value.Top, trusted.terminatingConsensus(register), "terminatingConsensus r$register")
        }

        // Occupancy is not.
        assertEquals(RegisterOccupancy.HELD, trusted.registerOccupancy(1, 0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(1, 1))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(1, 2))
        assertEquals(RegisterOccupancy.HELD, trusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(1))
    }

    @Test
    fun aMethodThatNeverReturnsIsNotAMethodWhoseReturnValueIsUnknowable() {
        // Every path throws, so the returning-node set is empty and `terminatingConsensus` answers `Top` for
        // every register, including one that is held on the throwing path.
        val throwing = completed(NEVER_RETURNS)
        val throwingTrusted = trustedOf(throwing)
        assertTrue(throwing.outcomes.all { it is Outcome.Threw }, "no path may return, or this tests nothing")

        assertFalse(throwingTrusted.didAnyPathReturn(), "this method never returns normally")
        assertEquals(Value.Top, throwingTrusted.terminatingConsensus(0))
        assertEquals(Value.Top, throwingTrusted.terminatingConsensus(1))
        assertEquals(RegisterOccupancy.NO_PATH, throwingTrusted.terminatingRegisterOccupancy(0))
        assertEquals(RegisterOccupancy.NO_PATH, throwingTrusted.terminatingRegisterOccupancy(1))

        // v1 is held (it is the thrown parameter). "No path returned" is a fact about the run, not the register.
        assertEquals(
            Value.Unknown(VirtualType.of("Ljava/lang/RuntimeException;")),
            throwing.explored.consensus(0, 1),
        )
        assertEquals(RegisterOccupancy.HELD, throwingTrusted.registerOccupancy(0, 1))

        // A method that returns an unknowable value has the same `terminatingConsensus` but a different meaning:
        // "no normal result" versus "live, do not fold".
        val returning = completed(DISAGREEING)
        val returningTrusted = trustedOf(returning)
        assertTrue(returningTrusted.didAnyPathReturn())
        assertEquals(Value.Top, returningTrusted.terminatingConsensus(0))
        assertEquals(RegisterOccupancy.HELD, returningTrusted.terminatingRegisterOccupancy(0))
    }

    @Test
    fun theHighHalfOfALiveWidePairReadsAsNeverAssignedAndAvailableRegistersIsWhatKnowsBetter() {
        // `Frame.assign` records a wide value's high half as an internal `WideHigh` slot that `peek` answers
        // null for, so occupancy reports NEVER_ASSIGNED for the high half of a live `J` pair. Scratch-register
        // search belongs to `availableRegisters`, which accounts for wide pairs.
        val graph = completed("instanceWideParams(JLjava/lang/String;I)I", className = "Lspine/spine;")
        val at = 0
        assertEquals(RegisterOccupancy.HELD, graph.explored.registerOccupancy(at, 4), "v4 is the J low half")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, graph.explored.registerOccupancy(at, 5),
            "v5 is the J high half; `peek` answers null for it",
        )
        assertFalse(
            5 in graph.availableRegisters(at).toList(),
            "the high half of a live pair is not free, and this is the query that knows it",
        )
        assertTrue(1 in graph.availableRegisters(at).toList(), "a genuinely untouched local is free")
        assertEquals(
            RegisterOccupancy.NEVER_ASSIGNED, graph.explored.registerOccupancy(at, 1),
            "so NEVER_ASSIGNED alone cannot mean 'free': v1 and v5 answer the same and differ",
        )
    }

    @Test
    fun bothQueriesAreTotalForARegisterTheMethodDoesNotHave() {
        // Queries must not throw during traversal. `Frame.peek` throws for an out-of-range register, so the
        // range guard answers the way `ExecutionGraph.valueIn` does.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        val absent = 99
        assertTrue(absent >= graph.methodView.registerCount, "register $absent must not exist in this method")
        assertEquals(Value.Top, graph.explored.consensus(JOIN, absent))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.registerOccupancy(JOIN, absent))
        assertEquals(RegisterOccupancy.NEVER_ASSIGNED, trusted.terminatingRegisterOccupancy(absent))
    }

    @Test
    fun theGatedTwinsDelegateExactlyAndAreUnreachableOnATruncatedRun() {
        // The gate decides who may act on an answer, never what the answer is.
        val graph = completed(DISAGREEING)
        val trusted = trustedOf(graph)
        for (register in 0..2) {
            assertEquals(
                graph.explored.registerOccupancy(JOIN, register),
                trusted.registerOccupancy(JOIN, register),
                "gated and ungated must agree for r$register",
            )
            assertEquals(
                graph.explored.terminatingRegisterOccupancy(register),
                trusted.terminatingRegisterOccupancy(register),
                "gated and ungated terminating must agree for r$register",
            )
        }
        assertEquals(graph.explored.didAnyPathReturn(), trusted.didAnyPathReturn())

        // A run stopped before any path reached a `return` answers `false` here for a method that does
        // return, which is why the twins are gated rather than documented.
        val truncated = CoreTester.execute(CLASS, DISAGREEING, limits = Limits(maxSteps = 1))
        assertTrue(truncated.completion() is CompletionStatus.Partial, "the run must have been cut short")
        assertNull(truncated.trusted(), "a Partial run may not hand out the gated twins")
        assertFalse(truncated.explored.didAnyPathReturn(), "...while the ungated twin still answers")
        assertEquals(
            RegisterOccupancy.NO_PATH, truncated.explored.terminatingRegisterOccupancy(0),
            "and so does the register-indexed twin, with the same caveat",
        )
        // The same method on a complete run returns, so the `false`s above are a truncation artifact.
        assertTrue(trusted.didAnyPathReturn(), "disagreeingArms returns on both arms when allowed to finish")
    }

    /**
     * Sees only a [TrustedGraphView], so the compiler checks that a consumer can reach all three verdicts from
     * it. The `error` branch is an invariant: if any path returned, the returning-node set is non-empty and
     * [TrustedGraphView.terminatingRegisterOccupancy] cannot answer [RegisterOccupancy.NO_PATH].
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
    fun aConsumerHoldingOnlyTheTrustedViewCanTellAllThreeApart() {
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
        // Three distinct verdicts from inputs whose `terminatingConsensus` is the same `Value.Top`.
        val consensuses = listOf(
            trustedOf(completed(DISAGREEING)).terminatingConsensus(0),
            trustedOf(completed(DISAGREEING)).terminatingConsensus(1),
            trustedOf(completed(NEVER_RETURNS)).terminatingConsensus(0),
        )
        assertEquals(listOf<Value>(Value.Top, Value.Top, Value.Top), consensuses)
    }
}
