package org.cf.smalivm.core

import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Bug classes (audit + spec §5.4/§9): (a) one graph type that was simultaneously a static view, a
 * dynamic result and a mutable editor, so a graph could describe two revisions at once;
 * (b) queries that cloned-on-read and therefore mutated the state they measured; (c) a consensus
 * that mixed throwing paths into a return consensus; (d) availableRegisters crashing on ordinary
 * traversal. Oracle: spec §6 (consensus is the join over an address's live nodes) and §9 (queries
 * are pure and total).
 */
class ExecutionGraphTest {
    private val classIndex = ClassIndex(
        ClassManagerFactory().build("src/test/resources/smali", DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)))
    )
    private val int = VirtualType.of("I")

    private fun graphOf(descriptor: String, limits: Limits = Limits()): ExecutionGraph =
        ExecutionSession.of(classIndex.requireMethodView("Lspine/spine;", descriptor), classIndex, limits).run()

    /**
     * The trust-bearing queries of a graph whose run COMPLETED (spec §9's gate; see
     * [TrustedGraphView]). Fails loudly rather than returning null, so a test that unexpectedly turns
     * Partial says so instead of silently NPEing further down.
     *
     * Tests below that deliberately assert on a *Partial* run must NOT use this -- they call the
     * ungated `...OverExploredPaths` accessors, because their subject is the pile filter, not the
     * trust gate.
     */
    private fun ExecutionGraph.trustedOrFail(): TrustedGraphView =
        requireNotNull(trusted()) { "expected a Complete run to be trusted, but completion was ${completion()}" }

    @Test
    fun reachedAddressesAreTheExecutedOnes() {
        val g = graphOf("gotoSkip()I")
        val skipped = g.methodView.addresses.first { it > 0 && g.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.CONST_4 }
        assertTrue(g.wasReached(0))
        assertFalse(g.wasReached(skipped), "the jumped-over instruction was never executed")
        assertArrayEquals(g.methodView.addresses, g.addresses(), "addresses() is the static address set")
    }

    @Test
    fun consensusAtAnAddressIsThePostOpValue() {
        val g = graphOf("returnFive()I")
        assertEquals(
            Value.Known(5, int), g.trustedOrFail().consensus(0, 0),
            "consensus at an op's address is what it produced",
        )
    }

    @Test
    fun knownStringResolvesAnImmutableConstructedReceiverWithoutExposingItsHostObject() {
        val view = classIndex.requireMethodView("Lspine/spine;", "returnFive()I")
        val stringType = VirtualType.of("Ljava/lang/String;")
        val frame = Frame.entryFrame(view)
        val receiver = frame.allocateObject(stringType, emptyMap())
        val ref = receiver.raw as HeapRef
        assertTrue(frame.installConstructedHostObject(ref, "constructed"))
        frame.assign(0, receiver)
        val node = ExecutionNode(
            address = 0,
            op = OpBuilder.build(0, view, classIndex),
            frame = frame,
            parent = null,
            activation = Activation.entry(view),
        )
        val graph = ExecutionGraph(
            methodView = view,
            addressToNodes = mapOf(0 to listOf(node)),
            outcomes = emptyList(),
            completion = CompletionStatus.Complete,
            executedNodes = setOf(node),
            superclasses = classIndex,
        )

        assertEquals("constructed", graph.trustedOrFail().knownString(0, 0))
        assertNull(graph.trustedOrFail().knownString(0, 1))
    }

    @Test
    fun consensusIsTotalForUnreachedAddressesAndUnknownRegisters() {
        // Pinned in AnalysisContracts: never throws, yields Top when nothing is known.
        val g = graphOf("returnFive()I").trustedOrFail()
        assertEquals(Value.Top, g.consensus(999, 0), "an address that does not exist yields Top")
        assertEquals(Value.Top, g.consensus(0, 0 + 1_000), "a register never assigned yields Top")
    }

    @Test
    fun terminatingConsensusUsesReturnPathsOnly() {
        val g = graphOf("moveChain()I")
        assertEquals(Value.Known(9, int), g.trustedOrFail().terminatingConsensus(2))
    }

    @Test
    fun terminatingConsensusIgnoresThrowingPaths() {
        // Bug class (c): a throwing path contaminating a return consensus. A throw is a guest outcome,
        // not an abort, so this run still COMPLETES and the trusted view is the right accessor.
        val g = graphOf("throwUncaught(Ljava/lang/RuntimeException;)V")
        assertEquals(
            Value.Top, g.trustedOrFail().terminatingConsensus(0),
            "no return path means no return consensus",
        )
    }

    @Test
    fun parentAndChildAddressesFollowControlFlow() {
        val g = graphOf("nopThenConst()I")
        val addresses = g.methodView.addresses
        assertArrayEquals(intArrayOf(), g.parentAddresses(addresses[0]))
        assertArrayEquals(intArrayOf(addresses[0]), g.parentAddresses(addresses[1]))
        assertArrayEquals(intArrayOf(addresses[1]), g.children(addresses[0]))
    }

    @Test
    fun livenessComesFromTheOps() {
        val g = graphOf("moveChain()I")
        val moveAddress = g.methodView.addresses.first { g.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.MOVE }
        assertEquals(setOf(0), g.registersRead(moveAddress))
        assertEquals(setOf(1), g.registersAssigned(moveAddress))
        assertTrue(g.wasRegisterRead(moveAddress, 0))
        assertFalse(g.wasRegisterRead(moveAddress, 2))
        assertTrue(g.wasRegisterAssigned(moveAddress, 1))
    }

    @Test
    fun availableRegistersNeverThrowsAndExcludesUsedOnes() {
        // Bug class (d): the old equivalent crashed on ordinary non-leaf traversal.
        val g = graphOf("moveChain()I")
        for (address in g.addresses()) assertNotNull(g.availableRegisters(address))
        val available = g.availableRegisters(g.addresses().first()).toSet()
        assertFalse(available.contains(0), "v0 is written by the const")
        assertFalse(available.contains(2), "v2 is written by the second move")
    }

    @Test
    fun sideEffectsAreNoneForSpineOpsAndStrongWhenAborted() {
        val clean = graphOf("returnFive()I")
        assertEquals(SideEffectLevel.NONE, clean.trustedOrFail().methodSideEffectLevel())
        // MIGRATED TO THE UNGATED ACCESSOR: an unimplemented opcode aborts its path, so this run is
        // Partial by construction and has no trusted view at all. The subject here is the STRONG
        // aggregation itself, not the trust gate, so reading the truncated picture deliberately is the
        // right outcome rather than a workaround.
        val aborted = graphOf("unsupportedWideFilledArray()I")
        assertEquals(SideEffectLevel.STRONG, aborted.methodSideEffectLevelOverExploredPaths(),
            "an unimplemented opcode could have done anything; assume the worst")
    }

    /**
     * A wide result occupies two registers, so the high half is not free scratch. Without this,
     * `availableRegisters` offers a live pair's high half and the first consumer to take it as a
     * scratch register corrupts the pair -- documented on `ExecutionGraph.availableRegisters` and
     * carried as Plan 14 item 2 of the Plan 3 deferred note.
     *
     * `returnWide()J` is the demonstration case recorded there: `.registers 2`, one `const-wide v0`
     * (five code units, so `return-wide` is at address 5), and `availableRegisters` used to answer
     * `[1]` -- v1 being the high half of the very pair the next instruction returns. Asserted through
     * the public query rather than on `Op.registersAssigned`, because the query is what a consumer
     * acts on and the bug lived in the aggregation between them.
     */
    @Test
    fun theHighHalfOfAWideResultIsNotOfferedAsAFreeRegister() {
        val g = graphOf("returnWide()J")
        assertFalse(1 in g.availableRegisters(0).toSet(), "v1 is the high half of the wide value in v0")
    }

    @Test
    fun completionAndOutcomesAreReported() {
        val g = graphOf("returnFive()I")
        assertEquals(CompletionStatus.Complete, g.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), g.outcomes)
    }

    @Test
    fun nodePileAndOpAreExposedForConsumers() {
        val g = graphOf("returnFive()I")
        assertEquals(1, g.nodePile(0).size)
        assertEquals(emptyList<ExecutionNode>(), g.nodePile(999))
        assertEquals(0, g.getOp(0).address)
    }

    @Test
    fun tryBlocksComeFromTheStaticView() {
        val g = graphOf("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        assertEquals(g.methodView.tryBlocks, g.tryBlocks())
    }

    @Test
    fun queriesDoNotMutateTheGraph() {
        // Bug class (b): clone-on-read queries perturbing the state they measure.
        val g = graphOf("moveChain()I")
        val trusted = g.trustedOrFail()
        val before = g.addresses().map { trusted.consensus(it, 0) }
        repeat(3) {
            g.addresses().forEach { a ->
                trusted.consensus(a, 0); trusted.registerValues(a, 0); g.availableRegisters(a)
            }
        }
        assertEquals(before, g.addresses().map { trusted.consensus(it, 0) })
    }

    // ------------------------------------------------------------------------------------------
    // The piling decision (spec §6.4): step() dequeues a node before it knows whether a bound
    // (address-visit / step / timeout) will stop it running that node's op. Piling only nodes
    // that got to run -- the pre-fix behavior -- left a limit-aborted node belonging to NO pile at
    // all: it existed only as some sibling's unqueried ExecutionNode.children entry, invisible to
    // nodePile/wasReached/terminatingConsensus even though graph.outcomes recorded its abort. The
    // three tests below are the load-bearing proof of the fix and of the invariant it must uphold
    // (wasReached must not claim a node that never ran "reached" its address) -- kept permanently
    // per this plan's process rule, not scratch tests to delete once green.
    // ------------------------------------------------------------------------------------------

    @Test
    fun addressLimitAbortedNodeIsVisibleInItsPile() {
        // The exact scenario the piling decision exists for: pre-fix, a probe on spinForever()V
        // with maxAddressVisits = 1 yielded piles {0=[1 node], 1=[1 node]} in which NEITHER piled
        // node had a non-null outcome, even though graph.outcomes recorded Aborted(ADDRESS_LIMIT)
        // -- the abort was unattributable to any address a consumer could query. Post-fix, the
        // SECOND visit to address 0 (the goto's revisit) is piled too, carrying that outcome.
        val g = graphOf("spinForever()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), g.completion())
        assertFalse(g.completion().canTrustConsensus, "a Partial run must never look complete")

        val pile0 = g.nodePile(0)
        assertEquals(2, pile0.size, "the executed first visit AND the limit-aborted revisit both belong to address 0's pile")
        assertEquals(
            1, pile0.count { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "the abort must be attributable to the node/address it happened at, not just floating free in graph.outcomes",
        )
        assertTrue(g.wasReached(0), "the first visit genuinely executed, so the address IS reached")
    }

    @Test
    fun wasReachedExcludesAnAddressWhoseOnlyNodeNeverExecuted() {
        // The other half of the same decision: nodePile is deliberately UNFILTERED (so a consumer
        // can see a limit-aborted attempt), but wasReached must not be fooled by a node that was
        // dequeued and piled yet never ran its op. maxSteps = 1 lets address 0's const execute
        // normally, but the very next dequeue -- address 1's nop -- is stopped by the step budget
        // before its op runs, so address 1's ONLY node is piled but un-executed.
        val g = graphOf("nopThenConst()I", Limits(maxSteps = 1))
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.METHOD_LIMIT)), g.completion())
        assertTrue(g.wasReached(0), "address 0's const genuinely ran")
        assertEquals(1, g.nodePile(1).size, "the scheduler DID dequeue and pile a node for address 1...")
        assertFalse(g.wasReached(1), "...but that node was stopped by the limit before its op ran, so it never reached")
        // wasAttempted is the WEAKER, actual precondition getOp needs: true here even though
        // wasReached is false, because a node WAS dequeued and piled at address 1 -- it just never
        // got to run. Review finding 2: without this member, getOp's real precondition could only
        // be stated in prose, not checked.
        assertTrue(g.wasAttempted(1), "a node WAS dequeued and piled at address 1, even though it never reached")
        assertFalse(g.wasAttempted(999), "an address with no node at all was never attempted either")
    }

    @Test
    fun limitAbortedRevisitNeverContaminatesConsensus() {
        // The sharpest half: getting THIS wrong is exactly "wrong optimizations", the worst
        // failure mode a graph query can produce. loopRevisitChangesRegister()V reassigns v1 on
        // every iteration, so the second (limit-aborted) visit's stale, never-executed Frame
        // genuinely disagrees with the first (executed) visit's value in v1. If the graph ever
        // folded that stale frame into consensus, a sound single-writer value would wrongly
        // collapse to Unknown. Oracle: Dalvik semantics of the fixture (only the executed move
        // ever produces a real v1-at-this-address value; the aborted revisit's v1 is leftover
        // from the FIRST iteration's const, not this address's own op).
        //
        // MIGRATED TO THE UNGATED ACCESSORS when the trust gate landed. This test asserts `Known` on a
        // deliberately Partial run, so it has no trusted view by construction -- and that is the right
        // outcome, not a workaround: its subject is the pile filter (which nodes may vote), not the
        // trust gate (who may act on the vote). Both properties are real and independent, which is why
        // TrustedGraphViewTest re-uses this very fixture to prove the other half: this confident Known
        // is exactly what `trusted()` must refuse to hand out.
        val g = graphOf("loopRevisitChangesRegister()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        val moveAddress = g.methodView.addresses.first { g.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.MOVE }
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), g.completion())
        assertEquals(2, g.nodePile(moveAddress).size, "both the executed visit and the limit-aborted revisit are piled")

        assertEquals(
            Value.Known(1, int), g.consensusOverExploredPaths(moveAddress, 1),
            "only the executed visit may vote; an unfiltered pile would see v1 disagree (1 vs 2) and collapse to Unknown",
        )
        assertEquals(
            setOf(Value.Known(1, int)), g.registerValuesOverExploredPaths(moveAddress, 1),
            "registerValues must also exclude the never-executed revisit's stale frame",
        )
    }

    // ------------------------------------------------------------------------------------------
    // Review round 1 findings.
    // ------------------------------------------------------------------------------------------

    @Test
    fun addressesReturnsAFreshArrayEachCall() {
        // Bug class: addresses() handed out methodView.addresses itself -- the SAME array every
        // query and every ExecutionSession.graph() snapshot reads. A consumer mutating the returned
        // array (`graph.addresses()[0] = 12345`) would permanently corrupt availableRegisters,
        // methodSideEffectLevel* and terminatingConsensus* (all of which iterate methodView.addresses)
        // for this graph AND every other snapshot of the same run. Oracle: this task's own class
        // KDoc -- "an observer ... cannot perturb what it measures" -- which a shared, mutable
        // return value directly violates.
        val g = graphOf("moveChain()I")
        val first = g.addresses()
        val originalFirstElement = first[0]
        first[0] = 12345
        assertEquals(originalFirstElement, g.addresses()[0], "mutating a returned array must not affect a later call")
        assertEquals(originalFirstElement, g.methodView.addresses[0], "...or the MethodView every query reads through")
        assertNotSame(first, g.addresses(), "two calls must not hand out the same array instance")
    }

    @Test
    fun getOpFailureMessageNamesAttemptedNotReached() {
        // Bug class (review finding 2): the exception message said "was never reached", but this
        // task defines "reached" to mean "a node here actually executed" -- getOp's real
        // precondition is the weaker "was never attempted" (wasAttempted). A message using the
        // stricter word leads a reader to the wrong conclusion about what actually failed.
        val g = graphOf("returnFive()I")
        val failure = assertThrows(IllegalArgumentException::class.java) { g.getOp(999) }
        assertTrue(failure.message!!.contains("attempted"), "message must name the real precondition: ${failure.message}")
        assertFalse(g.wasAttempted(999))
    }

    @Test
    fun consensusOverMultipleAddressesUnionsNodesThenJoins() {
        // Bug class (review finding 4): mutation-verified during review that replacing
        // `addresses.flatMap { executedPile(it) }` with `executedPile(addresses.first())` left the
        // entire 656-test suite green -- the union semantics AnalysisContracts itself calls
        // "required, not convenience" (unreflection reads an invoke's PARENT addresses, plural, and
        // any invoke with two predecessors or inside a try block has several) were completely
        // unpinned. Three properties, no new fixture: two addresses that agree, an empty array, and
        // (since moveChain()I assigns every register exactly once and so can never show a genuine
        // Known-vs-Known disagreement) two addresses on loopRevisitChangesRegister()V that do.
        val agree = graphOf("moveChain()I")
        val agreeAddrs = agree.addresses() // [const v0,0x9 ; move v1,v0 ; move v2,v1 ; return v2]
        // v0 is assigned once and never reassigned, so it holds 9 at both the const's own address
        // and the first move's address (which touches v1, not v0) -- both genuinely contribute.
        assertEquals(
            Value.Known(9, int), agree.trustedOrFail().consensus(intArrayOf(agreeAddrs[0], agreeAddrs[1]), 0),
            "two addresses whose register agrees must still report the shared Known value",
        )
        assertEquals(
            Value.Top, agree.trustedOrFail().consensus(intArrayOf(), 0),
            "no addresses means no contributing node -- Top, never a crash (the mutation this guards against: .first() on empty)",
        )

        // Ungated accessor: the disagreement fixture needs a tight address budget to produce two
        // genuinely different values, which makes the run Partial. Same reasoning as
        // limitAbortedRevisitNeverContaminatesConsensus above -- the subject is the JOIN, not the gate.
        val disagree = graphOf("loopRevisitChangesRegister()V", Limits(maxAddressVisits = 1, maxSteps = 100))
        val disagreeAddrs = disagree.addresses() // [const v1,0x1 ; move v0,v1 ; const v1,0x2 ; goto]
        assertEquals(
            Value.Unknown(int),
            disagree.consensusOverExploredPaths(intArrayOf(disagreeAddrs[0], disagreeAddrs[2]), 1),
            "two addresses whose register genuinely disagrees must join to Unknown, not silently pick one side",
        )
    }

    @Test
    fun consensusRangeChecksAgainstTheNodesOwnViewNotTheGraphsMethodView() {
        // Bug class (review finding 5): valueIn range-checked the requested register against the
        // GRAPH's methodView.registerCount rather than the NODE's own view.registerCount. Frame.peek
        // range-checks against its OWN view (a Frame is built per-node, one per MethodView), so a
        // node whose view is NARROWER than the graph's method -- unreachable through any public API
        // today, but guaranteed once Plan 9 gives every callee node its own MethodView -- would pass
        // this query's bound check yet still throw IllegalArgumentException straight out of
        // Frame.peek, in a query the spec says must never throw on ordinary traversal. Both
        // constructors used below are `internal` (reachable from this module); no public API can
        // build a node whose view differs from its graph's yet, which is exactly why this needs a
        // hand-built graph rather than a real session run.
        val wideEntry = classIndex.requireMethodView("Lspine/spine;", "moveChain()I") // .registers 3
        val narrowForeign = classIndex.requireMethodView("Lspine/spine;", "returnFive()I") // .registers 1
        val foreignNode = ExecutionNode(
            address = 0,
            op = OpBuilder.build(0, narrowForeign, classIndex),
            frame = Frame.entryFrame(narrowForeign),
            parent = null,
            activation = Activation.entry(narrowForeign),
        )
        val graph = ExecutionGraph(
            methodView = wideEntry,
            addressToNodes = mapOf(0 to listOf(foreignNode)),
            outcomes = emptyList(),
            completion = CompletionStatus.Complete,
            executedNodes = setOf(foreignNode),
            superclasses = CoreTester.classIndex,
        )
        assertEquals(
            Value.Top, graph.trustedOrFail().consensus(0, 1),
            "register 1 is in-range for the graph's OWN methodView (3 registers) but out of range for " +
                "this particular node's view (1 register) -- must be Top, never throw",
        )
    }
}
