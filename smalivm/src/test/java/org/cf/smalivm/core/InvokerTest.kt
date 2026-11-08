package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Bug classes this file exists for, all three of them a **silent wrong answer** rather than a crash
 * (`CARRY-FORWARD.md` Plan 9 items 1, 2 and 10):
 *
 *  1. **Colliding node piles.** `addressToNodes` was keyed by a bare `Int`, unambiguous only while one
 *     session walked one method. A caller's address 0 and a callee's address 0 are different program
 *     points, and merging their piles lets a callee's `Outcome.Returned` node vote in the CALLER's
 *     `terminatingConsensus` -- inventing agreement about a value the caller never returned.
 *  2. **A colliding visit budget.** `addressVisits` was session-global, so N loop-free callees each
 *     starting at address 0 shared one budget: a spurious `ADDRESS_LIMIT` makes the run `Partial`, which
 *     switches the consumer trust gate off with no visible cause. The oracle scopes visits per
 *     activation (`VirtualMachine.kt`'s `ExecutionQueueEntry.addressToVisitCount`), so this was a
 *     fidelity divergence as well.
 *  3. **Colliding heap ids.** `HeapRef` is a structural data class, so a callee frame built through the
 *     public `Frame.entryFrame(view)` -- a FRESH id space starting at 0 -- gives the callee's first
 *     object the same id as the caller's, and heap consensus silently treats two objects as one.
 *
 * Oracle/spec: spec §5.3 (one `Invoker`, one call site), §4.2 (heap identity is "same id"), §6.5
 * (return consensus over return paths), and the per-activation visit scoping the old engine already has.
 *
 * **Why the invoke arrives through the `opFactory` seam rather than a real `invoke-static` op.** Task 14
 * lands the Invoker/Backend/CallStack seam only; `OpBuilder` still decodes `invoke-static` as an
 * `UnsupportedOp`, because the `ratchet-invoke-static` differential fixture retires in Task 20 with the
 * real `InvokeOp` (the ratchet rule: retire a ratchet in the task that lands the opcode). The fixtures
 * nevertheless contain REAL `invoke-static` instructions, so instruction widths, `nextAddress` past a
 * call and every address this file pins are the ones Task 20 inherits unchanged.
 */
class InvokerTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun descriptorOf(className: String, name: String, proto: String) =
        MethodDescriptor(VirtualType.of(className), name, proto)

    /**
     * Stands in for Task 20's `InvokeOp`: it returns the [Transition.Invoke] the real op will return,
     * and nothing else. Deliberately minimal -- everything this file tests happens *after* the
     * transition reaches the scheduler.
     */
    private class InvokeStub(address: Int, private val invoke: Transition.Invoke) : Op(address) {
        override val name = "invoke-static-stub"
        override val registersRead: Set<Int> get() = invoke.argumentRegisters.toSet()
        override fun execute(frame: Frame): OpExecution = listOf(invoke).asOpExecution()
    }

    /**
     * The address of the method's one real `invoke-static`, read off the decoded instructions rather than
     * counted by hand: every fixture here has exactly one, and hard-coding the number is how a `new-array`
     * (two code units, not one) silently moves a stub off the instruction it was meant to replace.
     */
    private fun invokeAddressIn(view: MethodView): Int =
        view.addresses.single { view.opcodeAt(it) == Opcode.INVOKE_STATIC }

    private fun run(
        callerDescriptor: String,
        calleeName: String,
        calleeProto: String,
        argumentRegisters: IntArray = IntArray(0),
        limits: Limits = Limits(),
    ): ExecutionGraph {
        val view = viewOf(CALLER, callerDescriptor)
        val invokeAt = invokeAddressIn(view)
        val invoke = Transition.Invoke(
            descriptorOf(CALLEE, calleeName, calleeProto), argumentRegisters, InvokeKind.STATIC,
        )
        return ExecutionSession.of(view, classIndex, limits, Frame.entryFrame(view)) { address, v, index ->
            // Keyed on the METHOD as well as the address: `callsRecursiveCallee` and `recursesForever`
            // both hold their invoke at address 0, and replacing both would make the recursion the
            // caller's rather than the callee's.
            if (address == invokeAt && v.signature == view.signature) InvokeStub(address, invoke)
            else OpBuilder.build(address, v, index)
        }.run()
    }

    @Test
    fun aCalleesNodesDoNotJoinTheCallersPileAtTheSameAddress() {
        // Item 2. `callsCalleeAndReturnsItsOwn()I` holds `const/4 v0, 0x1` at address 0 and
        // `returnsSeven()I` holds `const/4 v0, 0x7` at ITS address 0 -- two different program points
        // writing the same register two different values. One pile per bare address merges them.
        val graph = run("callsCalleeAndReturnsItsOwn()I", "returnsSeven", "()I")
        assertEquals(CompletionStatus.Complete, graph.completion(), "the whole call must run to completion")
        assertEquals(
            1, graph.nodePile(0).size,
            "address 0 of the CALLER was executed once; the callee's own address 0 belongs to the callee",
        )
        assertEquals(
            setOf(Value.Known(1, int)), graph.registerValuesOverExploredPaths(0, 0),
            "the caller's address 0 wrote 1; 7 is the callee's constant at the callee's address 0",
        )
    }

    @Test
    fun aCalleesReturningNodeDoesNotVoteInTheCallersReturnConsensus() {
        // Item 2's sharp edge, and the worst defect this engine can have. The callee's `return` sits at
        // ITS address 1, which the caller also has (the caller's `invoke-static`), so under a bare
        // address key the callee's `Outcome.Returned` node lands in the caller's pile at 1 and
        // `returningNodes()` counts it. Its v0 is 7 and the caller's is 1, so the contaminated join
        // answers Unknown/Top where the truth is a foldable constant.
        val graph = run("callsCalleeAndReturnsItsOwn()I", "returnsSeven", "()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            Value.Known(1, int), trusted!!.terminatingConsensus(0),
            "the caller returns its OWN constant; the callee's 7 never returned from the caller",
        )
        assertEquals(
            listOf(Outcome.Returned(Value.Known(1, int))), graph.outcomes,
            "ONE path of the RUN ended, in the caller -- a callee's return continues the run's path",
        )
    }

    @Test
    fun eachActivationGetsItsOwnAddressVisitBudget() {
        // Item 1. Both methods start at address 0 and neither loops, so a per-activation budget of ONE
        // suffices for both. A session-global counter sees address 0 twice and aborts the second --
        // a truncated exploration reported for a run that visited every address exactly once.
        val graph = run(
            "callsCalleeAndReturnsItsOwn()I", "returnsSeven", "()I",
            limits = Limits(maxAddressVisits = 1, maxSteps = 100),
        )
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "no address is visited twice in either activation, so a budget of 1 must not trip",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(1, int))), graph.outcomes)
    }

    @Test
    fun aCalleesFirstObjectIsNotTheCallersFirstObject() {
        // Item 10. The caller allocates a 1-element `[I`, the callee allocates a 2-element one. With a
        // fresh callee id space both are `HeapRef(0)` and compare EQUAL, so `Value.agreesWith` reports
        // one object for two allocations and a consumer could eliminate one of them.
        val graph = run("allocatesThenCalleeAllocates()[I", "allocatesTwoElementArray", "()[I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val callersArray = trusted!!.terminatingConsensus(1)
        val calleesArray = trusted.terminatingConsensus(0)
        assertTrue(callersArray is Value.Known && callersArray.raw is HeapRef, "v1 holds the caller's array: $callersArray")
        assertTrue(calleesArray is Value.Known && calleesArray.raw is HeapRef, "v0 holds the callee's array: $calleesArray")
        assertNotEquals(
            (callersArray as Value.Known).raw, (calleesArray as Value.Known).raw,
            "two allocations in one run are two objects, whichever frame allocated them",
        )
        // And both are really on the heap the caller returns with, with their own lengths -- which is
        // what makes the id distinction load-bearing rather than cosmetic.
        val callers = trusted.terminatingHeapObject(callersArray.raw as HeapRef)
        val callees = trusted.terminatingHeapObject(calleesArray.raw as HeapRef)
        assertTrue(callers is HeapObject.TrackedArray && callers.length == 1, "the caller's 1-element array: $callers")
        assertTrue(callees is HeapObject.TrackedArray && callees.length == 2, "the callee's 2-element array: $callees")
    }

    @Test
    fun aLoopingCalleeStillSpendsItsOwnAddressVisitBudget() {
        // The complement of `eachActivationGetsItsOwnAddressVisitBudget`, and what stops it being vacuous:
        // per-activation scoping must not degenerate into never counting. Deleting the bound entirely
        // satisfies that test and hangs (up to maxSteps) here.
        val graph = run(
            "callsSpinningCallee()I", "spinsForever", "()V",
            limits = Limits(maxAddressVisits = 3, maxSteps = 200),
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val calleeNodes = graph.nodePile(invokeAddressIn(viewOf(CALLER, "callsSpinningCallee()I"))).single().children
            .flatMap { root -> generateSequence(root) { it.children.singleOrNull() }.toList() }
        assertTrue(
            calleeNodes.any { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "the callee must still exhaust its own budget before resolved decision 6 collapses it",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(2, int))), graph.outcomes)
        // The CALLER's own addresses were each visited once, so nothing about the caller was truncated by
        // the callee's loop -- the abort belongs to the callee's activation, which is why no address of
        // this method carries it.
        assertTrue(graph.wasReached(0), "the caller's const ran before the call")
    }

    @Test
    fun aCalleesReturnValueReachesTheCallerThroughTheResultSlot() {
        // The walking skeleton's headline: a call runs end to end and the caller can use what came back.
        // `move-result` reads the frame's result slot (`Frame.takeResult`), which is the mechanism the
        // resumed frame is seeded through -- no new machinery, and a caller that ignores the result simply
        // never takes it (see `aVoidCalleeResumesTheCallerWithNothingToMove`).
        val graph = run("callsCalleeAndReturnsItsResult()I", "returnsSeven", "()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(7, int), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aVoidCalleeResumesTheCallerWithNothingToMove() {
        val graph = run("callsVoidCallee()I", "returnsNothing", "()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Known(3, int), graph.trusted()!!.terminatingConsensus(0),
            "the caller's own register survives a void call untouched",
        )
    }

    @Test
    fun anArgumentLandsInTheCalleesHighestRegister() {
        // Parameters occupy the HIGHEST registers ([BC] / the appendix's register-layout rule), which
        // `MethodView.parameters` already computes. `identityInt(I)I` has `.registers 1`, so its parameter
        // is v0, and it returns it -- so the 6 coming back is proof the binding landed where Dalvik puts it.
        val graph = run("passesAnArgument()I", "identityInt", "(I)I", argumentRegisters = intArrayOf(0))
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(6, int), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aWideArgumentOccupiesTwoRegistersWhileBeingOneParameter() {
        // The silent trap the plan's Global Constraints warn about: the instruction's register list has TWO
        // entries for one `J` parameter, so an Invoker that bound one value per register would put the
        // high half's `Top` into the callee's second parameter -- or, with one parameter, would report an
        // arity mismatch and refuse a perfectly good call. `MethodView.parameters` sums the widths and the
        // Invoker consumes two slots for a wide one.
        val long = VirtualType.of("J")
        val graph = run("passesAWideArgument()J", "identityLong", "(J)J", argumentRegisters = intArrayOf(0, 1))
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(42L, long), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aCalleesDeclaredSideEffectIsReportedAtTheCallersInvokeAddress() {
        // An `ExecutionGraph` describes ONE activation, so the callee's own `STRONG` declaration is not in
        // this graph's piles at all. Without propagation the caller reports NONE -- "no side effects, safe
        // to delete" -- for a method whose callee stored through an array reference nothing proved was an
        // array. Spec §4.5: effects err upward, and they are statements about ONE address, which is why
        // this lands on the invoke rather than on the whole method.
        val view = viewOf(CALLER, "callsStrongEffectCallee()I")
        val graph = run("callsStrongEffectCallee()I", "strongEffect", "()V")
        assertEquals(CompletionStatus.Complete, graph.completion(), "the callee runs fine; it just has effects")
        assertEquals(
            SideEffectLevel.STRONG, graph.sideEffectLevel(invokeAddressIn(view)),
            "the address that caused the effect is the invoke",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.sideEffectLevel(0),
            "and NOT every address: the caller's own const declared nothing",
        )
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(
            SideEffectLevel.STRONG, graph.trusted()!!.methodSideEffectLevel(),
            "a Complete run hands this to a consumer through the trust gate, so it had better be true",
        )
    }

    @Test
    fun aCalleesEscapingThrowReachesTheCallerHandler() {
        // Task 20 adjudication 3 replaces Task 14's temporary abort: throwing states form their own
        // partition and the caller handler receives that partition without contaminating normal state.
        val view = viewOf(CALLER, "callsThrowingCalleeWithCatch()I")
        val graph = run("callsThrowingCalleeWithCatch()I", "throwsUncaught", "()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        // THE CALLEE MUST HAVE RUN, and this assertion is why the test is not vacuous. Without it all three
        // claims below are byte-identical to "the call never happened": stub `planFor`'s `Transition.Invoke`
        // branch back to its old `Aborted(UNSUPPORTED_OP)` no-op -- deleting the entire feature -- and an
        // emptiness assertion on a `Partial` run passes hardest when nothing works. Measured: that stub
        // killed 16 of these 25 tests and this one survived.
        val calleeEntry = graph.nodePile(invokeAddressIn(view)).single().children.single()
        assertEquals(
            "Lcalls/callee;->throwsUncaught()I", calleeEntry.view.signature,
            "the invoke's one child is the callee's entry node, so the callee genuinely executed",
        )
        assertEquals(1, calleeEntry.callDepth, "and it ran one frame down")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "the caller catches the callee throw")
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), graph.outcomes)
    }

    // --- an INCOMPLETE call is unanalyzable, so it declares STRONG (spec 2.2) ---

    @Test
    fun aFullyAnalyzedThrowingCalleeDoesNotInventAStrongEffect() {
        // `Activation.declaredEffect` is transferred onto the invoke node when the callee RETURNS, so a
        // callee path that ends any other way transferred nothing -- and because the abort is recorded on a
        // CALLEE node, which this graph never sees, `sideEffectLevel`'s `UNSUPPORTED_OP -> STRONG` rule could
        // not fire either. The caller's invoke address read NONE, i.e. "no side effects, safe to delete", for
        // a call this engine could not run to completion. Worse, it was a REGRESSION: before invocation
        // existed the invoke node itself carried the abort and answered STRONG.
        val view = viewOf(CALLER, "callsThrowingCalleeWithCatch()I")
        val graph = run("callsThrowingCalleeWithCatch()I", "throwsUncaught", "()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        // Same non-vacuity guard as `aCalleesEscapingThrowAbortsRatherThanClaimingTheCallerThrew`, and it is
        // needed for the same measured reason: with the invoke feature stubbed back to its no-op the invoke
        // node carries `Aborted(UNSUPPORTED_OP)` itself, so `sideEffectLevel`'s rule 2 answers STRONG at this
        // very address for a completely different reason and every other assertion here still holds.
        assertEquals(
            "Lcalls/callee;->throwsUncaught()I",
            graph.nodePile(invokeAddressIn(view)).single().children.single().view.signature,
            "the STRONG below must come from an INCOMPLETE CALL, so the callee has to have run",
        )
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(invokeAddressIn(view)))
        assertEquals(SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun aCalleeTruncatedByTheAddressLimitCollapsesAndResumesStrong() {
        // The same hole through a different exit: a bound, not a throw. Any callee path that does not RETURN
        // leaves the call incomplete, whether or not anything was declared -- which is why the fix keys on
        // "ended without returning" rather than on a declaration.
        val view = viewOf(CALLER, "callsSpinningCallee()I")
        val graph = run(
            "callsSpinningCallee()I", "spinsForever", "()V",
            limits = Limits(maxAddressVisits = 3, maxSteps = 200),
        )
        // Resolved decision 6: a limit-aborted callee is an opaque call, not an aborted caller path.
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(Value.Known(2, int), graph.terminatingConsensusOverExploredPaths(0))
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(invokeAddressIn(view)))
        assertEquals(
            SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths(),
            "a truncated callee is exactly as unanalyzable as a throwing one",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.sideEffectLevel(0),
            "and still not every address: the caller's own const declared nothing and completed",
        )
    }

    // --- recursion into the ENTRY method: reachability unions, the run's return value does not ---

    @Test
    fun aRecursiveActivationsReachedAddressesAreNotReportedAsDeadCode() {
        // `AnalysisContracts` writes the guarantee down: a `false` from `wasReached` on a run whose completion
        // `canTrustConsensus` is a dead-code PROOF, with exactly ONE enumerated exception (payload addresses).
        // Scoping the graph's piles to the entry activation quietly added a second: a recursive activation of
        // the ENTRY method. Here the nested activation takes the `:base` branch the entry activation never
        // reaches, so the graph would certify as unreached the very addresses that computed the constant it
        // simultaneously reports as the run's return value.
        val (view, graph) = recursiveRun()
        assertEquals(CompletionStatus.Complete, graph.completion(), "one recursive call, both frames finish")
        val base = baseBranchAddressesOf(view)
        for (address in base) {
            assertTrue(
                graph.wasReached(address),
                "address $address ran in the nested activation; a `false` here is a dead-code proof of " +
                    "the code that produced this run's own answer",
            )
        }
        // Non-vacuity: the ENTRY activation really does not reach them, so this is a union and not a
        // coincidence. Read through the call tree rather than the graph, which is the only place the
        // per-activation truth is still visible.
        val entryActivationNodes = graph.nodePile(base.first()).filter { it.callDepth == 0 }
        assertEquals(
            emptyList<ExecutionNode>(), entryActivationNodes,
            "the entry activation branched the other way, so only the nested one ran ${base.first()}",
        )
    }

    @Test
    fun aRecursiveActivationsOwnValuesJoinIntoPerAddressConsensusButNotIntoTheRunsReturnValue() {
        // THE ASYMMETRY, and both halves fail under the OPPOSITE mutation, which is the point of asserting
        // them together.
        //
        // Per-address consensus is a REWRITING question: folding the code at an address must be valid on
        // every activation that executes it. v1 is 1 in the entry activation and 0 in the nested one, so
        // `if-eqz v1` at address 0 cannot be folded -- and an entry-only scope would have said `Known(1, I)`,
        // authorizing exactly that wrong fold.
        //
        // Terminating consensus is a different question: what THIS run returns to ITS caller. The entry
        // activation returns 6 and the nested one returns 5; unioning would answer Unknown for a foldable
        // constant, which is `CARRY-FORWARD.md` Plan 9 item 2's invented agreement with recursion in place
        // of a callee.
        val (_, graph) = recursiveRun()
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            Value.Unknown(int), trusted!!.consensus(0, 1),
            "v1 disagrees between the two activations, so no fold at address 0 is valid for both",
        )
        assertEquals(
            Value.Known(6, int), trusted.terminatingConsensus(0),
            "THIS run returns 6; the nested activation's 5 is not this method's return value",
        )
        assertTrue(trusted.didAnyPathReturn())
    }

    /**
     * `recursesOnceThenAddsOne(I)I` seeded with v1 = 1, so it recurses exactly once and the nested activation
     * takes the `:base` branch. Returns the view alongside the graph because the tests read addresses off it.
     */
    private fun recursiveRun(): Pair<MethodView, ExecutionGraph> {
        val view = viewOf(CALLER, "recursesOnceThenAddsOne(I)I")
        val invoke = Transition.Invoke(
            MethodDescriptor(VirtualType.of(CALLER), "recursesOnceThenAddsOne", "(I)I"),
            intArrayOf(0),
            InvokeKind.STATIC,
        )
        val frame = Frame.entryFrame(view).also { it.assign(1, Value.Known(1, int)) }
        val graph = ExecutionSession.of(view, classIndex, Limits(), frame) { address, v, index ->
            // No method filter here, unlike the callee tests: the recursion IS this method calling itself, so
            // the stub must fire in the nested activation too.
            if (address == invokeAddressIn(v)) InvokeStub(address, invoke) else OpBuilder.build(address, v, index)
        }.run()
        return view to graph
    }

    /**
     * The `:base` branch's addresses, read off the `if-eqz`'s relative offset rather than counted by hand,
     * and asserted to be the tail of the method so a fixture edit cannot make the test vacuous.
     */
    private fun baseBranchAddressesOf(view: MethodView): List<Int> {
        val branch = view.addresses.first { view.opcodeAt(it) == Opcode.IF_EQZ }
        val target = branch + (view.instructionAt(branch) as OffsetInstruction).codeOffset
        val base = view.addresses.filter { it >= target }
        assertEquals(2, base.size, "`:base` is a const and a return; the fixture changed if it is not")
        return base
    }

    // --- resolution: what the Invoker refuses, and that none of it is a host crash (§7) ---

    @Test
    fun anAbsentMemberOfALoadedClassThrowsTheMeasuredGuestError() {
        val view = viewOf(CALLER, "callsAbsentMethod()I")
        val graph = run("callsAbsentMethod()I", "noSuchMethod", "()I")
        // Task 20, adjudication 7b: the Task 14 refusal was temporary. A present class with a missing
        // method throws guest NoSuchMethodError, measured by Fx_InvokeMissingMethod.
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
        assertEquals(
            SideEffectLevel.NONE, graph.sideEffectLevel(invokeAddressIn(view)),
            "resolution failed before a callee could have an effect",
        )
    }

    @Test
    fun aMethodWithNoBodyCollapsesToUnknownRatherThanCrashing() {
        // `MethodView.of` REQUIRES an implementation, so this is the case that would have been a host
        // failure rather than a refusal: `nativeCallee()I` is declared and has no code. Plan 11's
        // Emulate/Reflect backends are what eventually run it.
        val graph = run("callsNativeCallee()I", "nativeCallee", "()I")
        // Resolved decision 6: an opaque call continues with a typed Unknown result and STRONG effect.
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(int), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertTrue(
            graph.nodePile(0).single().hostFailure == null,
            "a declared-but-bodyless callee is a fact about the input, not a throwable to record",
        )
    }

    @Test
    fun anAbsentClassThrowsTheMeasuredGuestErrorRatherThanCrashing() {
        // The normal case in a real obfuscated APK: a call site referencing a class the input never
        // shipped. Task 15 makes this a guest NoClassDefFoundError; until then it is a conservative abort.
        val view = viewOf(CALLER, "callsAbsentMethod()I")
        val invoke = Transition.Invoke(
            MethodDescriptor(VirtualType.of("Lcalls/absent;"), "gone", "()I"), IntArray(0), InvokeKind.STATIC,
        )
        val session = ExecutionSession.of(view, classIndex, Limits(), Frame.entryFrame(view)) { address, v, index ->
            if (address == invokeAddressIn(view) && v.signature == view.signature) InvokeStub(address, invoke)
            else OpBuilder.build(address, v, index)
        }
        val graph = session.run()
        // Task 20, adjudication 7: Fx_InvokeAbsentType measures guest NoClassDefFoundError.
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.nodePile(0).single().hostFailure == null, "an absent class is input, not an engine bug")
    }

    @Test
    fun theOtherFourInvokeKindsUseTheirOwnTaskTwentyResolutionRules() {
        // Task 20 replaces the temporary-refusal assertion with one exact target per kind. This is stronger:
        // it catches a branch routed through another kind instead of checking only that refusal stopped.
        val child = VirtualType.of("Lchild_class;")
        val parent = VirtualType.of("Lparent_class;")
        val impl = VirtualType.of("Linvoke_single_impl;")
        val direct = classIndex.resolveInvocation(
            MethodDescriptor(child, "<init>", "()V"), InvokeKind.DIRECT, child, child,
        ) as InvocationLookup.Resolved
        assertEquals("Lchild_class;-><init>()V", (direct.lookup as MethodLookup.Runnable).view.signature)

        val superCall = classIndex.resolveInvocation(
            MethodDescriptor(parent, "parentMethod", "()Ljava/lang/String;"), InvokeKind.SUPER, child, child,
        ) as InvocationLookup.Resolved
        assertEquals(
            "Lparent_class;->parentMethod()Ljava/lang/String;",
            (superCall.lookup as MethodLookup.Runnable).view.signature,
        )

        val virtual = classIndex.resolveInvocation(
            MethodDescriptor(parent, "abstractMethod", "()Ljava/lang/String;"), InvokeKind.VIRTUAL, child, child,
        ) as InvocationLookup.Resolved
        assertEquals(
            "Lchild_class;->abstractMethod()Ljava/lang/String;",
            (virtual.lookup as MethodLookup.Runnable).view.signature,
        )

        val iface = classIndex.resolveInvocation(
            MethodDescriptor(VirtualType.of("Linvoke_single_interface;"), "value", "()I"),
            InvokeKind.INTERFACE,
            impl,
            impl,
        ) as InvocationLookup.Resolved
        assertEquals("Linvoke_single_impl;->value()I", (iface.lookup as MethodLookup.Runnable).view.signature)
    }

    @Test
    fun anArityMismatchUsesResolvedDecisionSixOpaqueFallback() {
        // The register list comes from the bytecode and the parameter list from the resolved method, and an
        // obfuscated APK routinely references a method whose shape does not match the call site. Resolved
        // decision 6 requires typed opaque fallback, not Task 14's temporary Partial refusal.
        val graph = run(
            "passesAnArgument()I", "returnsSeven", "()I", argumentRegisters = intArrayOf(0),
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun anArgumentRegisterTheCallerDoesNotHaveAbortsThePathRatherThanTheRun() {
        // Plan design decision 5's other half. `planFor` runs OUTSIDE the host-failure boundary, and
        // `Frame.read` range-checks and throws -- so an invoke naming a register the caller does not have
        // would have taken the whole run down with an IllegalArgumentException. `planInvoke` runs inside
        // `contained`, so it becomes one aborted path with the real throwable kept for debugging.
        val view = viewOf(CALLER, "callsCalleeAndReturnsItsResult()I") // .registers 1
        val invoke = Transition.Invoke(
            descriptorOf(CALLEE, "identityInt", "(I)I"), intArrayOf(99), InvokeKind.STATIC,
        )
        val graph = ExecutionSession.of(view, classIndex, Limits(), Frame.entryFrame(view)) { address, v, index ->
            if (address == invokeAddressIn(view) && v.signature == view.signature) InvokeStub(address, invoke)
            else OpBuilder.build(address, v, index)
        }.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        val failure = graph.nodePile(0).single().hostFailure
        assertTrue(failure is IllegalArgumentException, "the frame's own range check is what raised: $failure")
    }

    // --- the Invoker on its own, with no session ---

    @Test
    fun theInvokerResolvesAStaticTargetToARunnableBodyWithoutASession() {
        // The Invoker returns a PLAN and applies nothing, which is what makes it testable like this and
        // what keeps the scheduler's two-phase commit the only writer of node/frame/frontier state.
        val invoker = defaultInvoker(classIndex)
        val caller = viewOf(CALLER, "passesAnArgument()I")
        val preparation = invoker.prepare(
            Invocation(descriptorOf(CALLEE, "identityInt", "(I)I"), InvokeKind.STATIC, listOf(Value.Known(4, int))),
            caller.definingClass,
        )
        val plan = invoker.planOn(preparation as InvocationPreparation.Ready, Frame.entryFrame(caller))
        assertTrue(plan is InvocationPlan.RunGuest, "a present static method with a body is runnable: $plan")
        val guest = plan as InvocationPlan.RunGuest
        assertEquals("Lcalls/callee;->identityInt(I)I", guest.view.signature)
        assertEquals(0, guest.entryAddress, "a dex method's code starts at address 0")
        val binding = guest.parameterBindings.single()
        assertEquals(0, binding.register, "`.registers 1` puts the one parameter in v0 -- the HIGHEST register")
        assertEquals(Value.Known(4, int), binding.value)
    }

    @Test
    fun theClassIndexTellsTheThreeLookupFailuresApart() {
        // Not one nullable answer: an absent CLASS and an absent MEMBER are the two halves of Task 15's
        // resolution-failure family, while a bodyless method is Plan 11's backend work and not a failure at
        // all. Collapsing them would make "abstract" indistinguishable from "missing".
        val callee = VirtualType.of(CALLEE)
        assertTrue(classIndex.lookupMethod(callee, "returnsSeven()I") is MethodLookup.Runnable)
        assertEquals(MethodLookup.NoSuchMethod, classIndex.lookupMethod(callee, "noSuchMethod()I"))
        assertTrue(classIndex.lookupMethod(callee, "nativeCallee()I") is MethodLookup.NoBody)
        assertEquals(
            MethodLookup.NoSuchClass,
            classIndex.lookupMethod(VirtualType.of("Lcalls/absent;"), "gone()I"),
        )
    }

    /**
     * 🔴 **AN ARRAY DEFINING CLASS MUST NOT HOST-CRASH THE LOOKUP** (Plan 9 Task 15, adjudication 7b's
     * array-pseudo-method sub-question; found while writing that adjudication, at source).
     *
     * `[I->clone()Ljava/lang/Object;` is a real method on a device, and an array type has no class def to
     * look it up in, so a lookup that asked for one would throw a HOST exception out of `lookupMethod` --
     * the same "hard host crash on ordinary input" failure mode adjudication 7 exists to remove, one layer up
     * from the ops.
     *
     * **The ANSWER is [MethodLookup.NoBody], and it is a classification rather than a shrug**
     * (adjudication 7b, "array pseudo-methods"): the method *exists* -- so `NoSuchMethod`, which Task 20 will
     * turn into a guest `NoSuchMethodError`, would be a WRONG answer that invents a linking error a device
     * never raises -- and it has no dex body, because it is a runtime intrinsic. That is precisely what
     * `NoBody` means, and Plan 11's `EmulateBackend` is what runs it. So Plan 9 does NOT implement
     * `[I->clone()`, and `opcode/InvokeOpTest`'s two `[I->clone()` tests stay red as red-test-rule case 3
     * until Plan 11 -- now with a recorded adjudication rather than an open question.
     *
     * **`NoBody` is also not the answer for everything an array is asked**, which is the half a
     * blanket array rule would have got wrong in the other direction, so both directions are asserted below:
     * membership is decided by `Object`'s declared set ([JLS] §10.7), so a descriptor `Object` does not
     * declare is genuinely `NoSuchMethod`. And **it must not be a delegation to `Object`'s BODY**: the
     * framework smali's `Object.clone()` really has one (`java/lang/Object.smali:33-45`), so delegating would
     * hand Task 20 a runnable `Object.clone()` to execute for an array clone -- a body that cannot copy an
     * array, i.e. a silently wrong value where even the oracle produced a copy. Measured while writing this
     * test: the delegating version answered `Runnable`, which is what caught it.
     */
    @Test
    fun anArrayDefiningClassAnswersNoBodyRatherThanCrashingTheHost() {
        assertTrue(
            classIndex.lookupMethod(VirtualType.of("[I"), "clone()Ljava/lang/Object;") is MethodLookup.NoBody,
            "an array pseudo-method exists and has no dex body: Plan 11's emulator owns it -- and it must " +
                "NOT be Runnable, which is what delegating to Object's real framework body produced",
        )
        // Every inherited descriptor, not just `clone()`: the rule is about the DEFINING CLASS being an array,
        // and `Object`'s methods reach the same lookup. Answering `NoSuchMethod` for these would make Task 20
        // synthesize a NoSuchMethodError for `[Ljava/lang/String;->toString()`, which a device runs fine.
        assertTrue(
            classIndex.lookupMethod(
                VirtualType.of("[Ljava/lang/String;"),
                "toString()Ljava/lang/String;",
            ) is MethodLookup.NoBody,
        )
        // THE OTHER DIRECTION, and it is what stops the array row being a blanket "arrays answer
        // NoBody": a descriptor `Object` does not declare is a real missing member, so Task 20 can
        // still raise the guest NoSuchMethodError a device raises. Without this assertion the row could be
        // written as one line and nothing would notice.
        assertEquals(
            MethodLookup.NoSuchMethod,
            classIndex.lookupMethod(VirtualType.of("[I"), "definitelyNotAMethod()V"),
            "an array's member set is Object's plus clone(), so anything else is genuinely missing",
        )
        // The CONTROL for the ORDER of the two gates: an array whose COMPONENT class is absent is still a
        // resolution failure, so the array row must not shadow the `isLoadable` check above it. Without this,
        // "arrays answer NoBody" would also pass against a build that skipped the class check.
        assertEquals(
            MethodLookup.NoSuchClass,
            classIndex.lookupMethod(VirtualType.of("[Lcalls/absent;"), "clone()Ljava/lang/Object;"),
            "an absent component class is still an absent class",
        )
    }

    private companion object {
        const val CALLER = "Lcalls/caller;"
        const val CALLEE = "Lcalls/callee;"
    }
}
