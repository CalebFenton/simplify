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
 * Each bug class here is a silent wrong answer rather than a crash:
 *
 *  1. Colliding node piles. A caller's address 0 and a callee's address 0 are different program points;
 *     merging their piles lets a callee's returning node vote in the caller's `terminatingConsensus`.
 *  2. A colliding visit budget. Visits are scoped per activation, so loop-free callees that each start at
 *     address 0 do not share one budget and spuriously make the run `Partial`.
 *  3. Colliding heap ids. A callee frame shares the caller's heap id space; a fresh id space would give
 *     the callee's first object the caller's first id, and heap consensus would treat two objects as one.
 */
class InvokerTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun descriptorOf(className: String, name: String, proto: String) =
        MethodSignature(VirtualType.of(className), name, proto)

    /** The address of the method's one `invoke-static`, read off the decoded instructions. */
    private fun invokeAddressIn(view: MethodView): Int =
        view.addresses.single { view.opcodeAt(it) == Opcode.INVOKE_STATIC }

    private fun run(callerDescriptor: String, limits: Limits = Limits(), className: String = CALLER): ExecutionGraph =
        CoreTester.vmServices.newSession(viewOf(className, callerDescriptor), limits).run()

    @Test
    fun aCalleesNodesDoNotJoinTheCallersPileAtTheSameAddress() {
        // `callsCalleeAndReturnsItsOwn()I` holds `const/4 v0, 0x1` at address 0 and `returnsSeven()I` holds
        // `const/4 v0, 0x7` at its address 0: two program points writing one register two different values.
        val graph = run("callsCalleeAndReturnsItsOwn()I")
        assertEquals(CompletionStatus.Complete, graph.completion(), "the whole call must run to completion")
        assertEquals(
            1, graph.nodePile(0).size,
            "address 0 of the CALLER was executed once; the callee's own address 0 belongs to the callee",
        )
        assertEquals(
            setOf(Value.Known(1, int)), graph.explored.registerValues(0, 0),
            "the caller's address 0 wrote 1; 7 is the callee's constant at the callee's address 0",
        )
    }

    @Test
    fun aCalleesReturningNodeDoesNotVoteInTheCallersReturnConsensus() {
        // The callee's `return` sits at its address 1, which the caller also has. Under a bare address key the
        // callee's returning node (v0 = 7) would vote in the caller's return consensus (v0 = 1).
        val graph = run("callsCalleeAndReturnsItsOwn()I")
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
        // Both methods start at address 0 and neither loops, so a per-activation budget of one suffices for
        // both. A session-global counter sees address 0 twice and aborts the second.
        val graph = run("callsCalleeAndReturnsItsOwn()I", Limits(maxAddressVisits = 1, maxSteps = 100))
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "no address is visited twice in either activation, so a budget of 1 must not trip",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(1, int))), graph.outcomes)
    }

    @Test
    fun aCalleesFirstObjectIsNotTheCallersFirstObject() {
        // The caller allocates a 1-element `[I`, the callee a 2-element one. With a fresh callee id space both
        // would be `HeapRef(0)` and compare equal.
        val graph = run("allocatesThenCalleeAllocates()[I")
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
        // Both objects are on the heap the caller returns with, each with its own length.
        val callers = trusted.terminatingHeapObject(callersArray.raw as HeapRef).agreed()
        val callees = trusted.terminatingHeapObject(calleesArray.raw as HeapRef).agreed()
        assertTrue(callers is HeapObject.TrackedArray && callers.length == 1, "the caller's 1-element array: $callers")
        assertTrue(callees is HeapObject.TrackedArray && callees.length == 2, "the callee's 2-element array: $callees")
    }

    @Test
    fun aLoopingCalleeStillSpendsItsOwnAddressVisitBudget() {
        // Per-activation scoping must not degenerate into never counting: without the bound this callee loops
        // until maxSteps.
        val graph = run("callsSpinningCallee()I", Limits(maxAddressVisits = 3, maxSteps = 200))
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val calleeNodes = graph.nodePile(invokeAddressIn(viewOf(CALLER, "callsSpinningCallee()I"))).single().children
            .flatMap { root -> generateSequence(root) { it.children.singleOrNull() }.toList() }
        assertTrue(
            calleeNodes.any { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) },
            "the callee must exhaust its own budget before the call collapses to an opaque result",
        )
        assertEquals(listOf(Outcome.Returned(Value.Known(2, int))), graph.outcomes)
        // The abort belongs to the callee's activation; the caller's own addresses were each visited once.
        assertTrue(graph.wasReached(0), "the caller's const ran before the call")
    }

    @Test
    fun aCalleesReturnValueReachesTheCallerThroughTheResultSlot() {
        // `move-result` reads the frame's result slot, which the resumed caller frame is seeded through.
        val graph = run("callsCalleeAndReturnsItsResult()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(7, int), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aVoidCalleeResumesTheCallerWithNothingToMove() {
        val graph = run("callsVoidCallee()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Known(3, int), graph.trusted()!!.terminatingConsensus(0),
            "the caller's own register survives a void call untouched",
        )
    }

    @Test
    fun anArgumentLandsInTheCalleesHighestRegister() {
        // Parameters occupy the highest registers. `identityInt(I)I` has `.registers 1`, so its parameter is v0,
        // and the 6 coming back proves the binding is where Dalvik puts it.
        val graph = run("passesAnArgument()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(6, int), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aWideArgumentOccupiesTwoRegistersWhileBeingOneParameter() {
        // The instruction's register list has two entries for one `J` parameter. Binding one value per register
        // would put the high half into a second parameter, or report an arity mismatch for a valid call.
        val long = VirtualType.of("J")
        val graph = run("passesAWideArgument()J")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(42L, long), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun aCalleesDeclaredSideEffectIsReportedAtTheCallersInvokeAddress() {
        // An `ExecutionGraph` describes one method, so the callee's own `STRONG` declaration is not in this
        // graph's piles. Effects err upward and land on the invoke address, not the whole method.
        val view = viewOf(CALLER, "callsStrongEffectCallee()I")
        val graph = run("callsStrongEffectCallee()I")
        assertEquals(CompletionStatus.Complete, graph.completion(), "the callee runs fine; it just has effects")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(invokeAddressIn(view)),
            "the address that caused the effect is the invoke",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "and NOT every address: the caller's own const declared nothing",
        )
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        assertEquals(
            SideEffectLevel.STRONG, graph.trusted()!!.methodSideEffectLevel(),
            "a Complete run hands this to a consumer through the trust gate, so it had better be true",
        )
    }

    @Test
    fun aCalleesEscapingThrowReachesTheCallerHandler() {
        // Throwing states form their own partition, and the caller's handler receives it without contaminating
        // the normal state.
        val view = viewOf(CALLER, "callsThrowingCalleeWithCatch()I")
        val graph = run("callsThrowingCalleeWithCatch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        // The callee must have run: without this, the claims below also hold for a call that never happened.
        val calleeEntry = graph.nodePile(invokeAddressIn(view)).single().children.single()
        assertEquals(
            "Lcalls/callee;->throwsUncaught()I", calleeEntry.view.signature.toString(),
            "the invoke's one child is the callee's entry node, so the callee genuinely executed",
        )
        assertEquals(1, calleeEntry.callDepth, "and it ran one frame down")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph), "the caller catches the callee throw")
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), graph.outcomes)
    }

    @Test
    fun aFullyAnalyzedThrowingCalleeDoesNotInventAStrongEffect() {
        // A callee that throws and is caught by the caller ran to completion, so the invoke address reports the
        // callee's declared effect, which is NONE.
        val view = viewOf(CALLER, "callsThrowingCalleeWithCatch()I")
        val graph = run("callsThrowingCalleeWithCatch()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        // An invoke node that aborted itself would also answer at this address; the callee must have run.
        assertEquals(
            "Lcalls/callee;->throwsUncaught()I",
            graph.nodePile(invokeAddressIn(view)).single().children.single().view.signature.toString(),
            "the callee has to have run for the effect below to describe the call",
        )
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(invokeAddressIn(view)))
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun aCalleeTruncatedByTheAddressLimitCollapsesAndResumesStrong() {
        // Any callee path that does not return leaves the call incomplete, whether or not anything was
        // declared. A limit-aborted callee is an opaque call, not an aborted caller path.
        val view = viewOf(CALLER, "callsSpinningCallee()I")
        val graph = run("callsSpinningCallee()I", Limits(maxAddressVisits = 3, maxSteps = 200))
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(Value.Known(2, int), graph.explored.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(invokeAddressIn(view)))
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel(),
            "a truncated callee is exactly as unanalyzable as a throwing one",
        )
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(0),
            "and still not every address: the caller's own const declared nothing and completed",
        )
    }

    @Test
    fun aRecursiveActivationsReachedAddressesAreNotReportedAsDeadCode() {
        // A `false` from `wasReached` on a trusted run is a dead-code proof. The nested activation of the entry
        // method takes the `:base` branch the entry activation never reaches, so those addresses are reached.
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
        // The entry activation does not reach them, so this is a union and not a coincidence.
        val entryActivationNodes = graph.nodePile(base.first()).filter { it.callDepth == 0 }
        assertEquals(
            emptyList<ExecutionNode>(), entryActivationNodes,
            "the entry activation branched the other way, so only the nested one ran ${base.first()}",
        )
    }

    @Test
    fun aRecursiveActivationsOwnValuesJoinIntoPerAddressConsensusButNotIntoTheRunsReturnValue() {
        // Per-address consensus is a rewriting question: a fold must be valid in every activation that runs
        // the address. v1 is 1 in the entry activation and 0 in the nested one, so `if-eqz v1` at address 0
        // cannot be folded.
        //
        // Terminating consensus is what this run returns to its caller: the entry activation returns 6 and the
        // nested one 5, and unioning them would answer Unknown for a foldable constant.
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
        val frame = Frame.entryFrame(view).also { it.assign(1, Value.Known(1, int)) }
        return view to CoreTester.vmServices.newSession(view, Limits(), frame).run()
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

    @Test
    fun anAbsentMemberOfALoadedClassThrowsTheMeasuredGuestError() {
        // A present class with a missing method throws guest NoSuchMethodError.
        val view = viewOf(CALLER, "callsAbsentMethod()I")
        val graph = run("callsAbsentMethod()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
        assertEquals(
            SideEffectLevel.NONE, graph.explored.sideEffectLevel(invokeAddressIn(view)),
            "resolution failed before a callee could have an effect",
        )
    }

    @Test
    fun aMethodWithNoBodyCollapsesToUnknownRatherThanCrashing() {
        // `nativeCallee()I` is declared and has no code, and no backend runs it: an opaque call continues with a
        // typed Unknown result and STRONG effect.
        val graph = run("callsNativeCallee()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(int), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
        assertTrue(
            graph.nodePile(0).single().outcome.hostFailure == null,
            "a declared-but-bodyless callee is a fact about the input, not a throwable to record",
        )
    }

    @Test
    fun anAbsentClassThrowsTheMeasuredGuestErrorRatherThanCrashing() {
        // A call site referencing a class the input never shipped throws guest NoClassDefFoundError.
        val graph = run("callsAbsentClass()I", className = RESOLUTION_CALLER)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.nodePile(0).single().outcome.hostFailure == null, "an absent class is input, not an engine bug")
    }

    @Test
    fun theOtherFourInvokeKindsResolveTheirOwnExactTargets() {
        // One exact target per kind catches a branch routed through another kind.
        val child = VirtualType.of("Lchild_class;")
        val parent = VirtualType.of("Lparent_class;")
        val impl = VirtualType.of("Linvoke_single_impl;")
        val direct = classIndex.currentResolver().resolveInvocation(
            MethodSignature(child, "<init>", "()V"), InvokeKind.DIRECT, child, child,
        ) as InvocationLookup.Resolved
        assertEquals("Lchild_class;-><init>()V", (direct.lookup as MethodLookup.Runnable).view.signature.toString())

        val superCall = classIndex.currentResolver().resolveInvocation(
            MethodSignature(parent, "parentMethod", "()Ljava/lang/String;"), InvokeKind.SUPER, child, child,
        ) as InvocationLookup.Resolved
        assertEquals(
            "Lparent_class;->parentMethod()Ljava/lang/String;",
            (superCall.lookup as MethodLookup.Runnable).view.signature.toString(),
        )

        val virtual = classIndex.currentResolver().resolveInvocation(
            MethodSignature(parent, "abstractMethod", "()Ljava/lang/String;"), InvokeKind.VIRTUAL, child, child,
        ) as InvocationLookup.Resolved
        assertEquals(
            "Lchild_class;->abstractMethod()Ljava/lang/String;",
            (virtual.lookup as MethodLookup.Runnable).view.signature.toString(),
        )

        val iface = classIndex.currentResolver().resolveInvocation(
            MethodSignature(VirtualType.of("Linvoke_single_interface;"), "value", "()I"),
            InvokeKind.INTERFACE,
            impl,
            impl,
        ) as InvocationLookup.Resolved
        assertEquals("Linvoke_single_impl;->value()I", (iface.lookup as MethodLookup.Runnable).view.signature.toString())
    }

    @Test
    fun anArityMismatchUsesTypedOpaqueFallback() {
        // The register list comes from the bytecode and the parameter list from the resolved method; an
        // obfuscated APK routinely references a method whose shape does not match the call site.
        val graph = run("passesAnArgumentToANoArgumentCallee()I", className = RESOLUTION_CALLER)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun anArgumentRegisterTheCallerDoesNotHaveAbortsThePathRatherThanTheRun() {
        // An invoke naming a register the caller frame does not have fails the frame's range check while the
        // call is planned. Planning runs inside the host-failure boundary, so it aborts one path with the
        // throwable kept. Verified dex never names a register outside the frame, so a scripted invoke supplies it.
        val view = viewOf(CALLER, "callsCalleeAndReturnsItsResult()I") // .registers 1
        val invoke = OpArm.Invoke(descriptorOf(CALLEE, "identityInt", "(I)I"), intArrayOf(99), InvokeKind.STATIC)
        val invokeAt = invokeAddressIn(view)
        val graph = CoreTester.vmServices.newSession(view, Limits(), null) { address, v, resolver ->
            if (address == invokeAt && v.signature == view.signature) {
                object : Op(address) {
                    override val name = "invoke-static"
                    override val registersRead: Set<Int> get() = setOf(99)
                    override fun execute(frame: Frame): OpExecution = OpExecution.single(invoke)
                }
            } else {
                OpBuilder.build(address, v, resolver)
            }
        }.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        val failure = graph.nodePile(0).single().outcome.hostFailure
        assertTrue(failure is IllegalArgumentException, "the frame's own range check is what raised: $failure")
    }

    @Test
    fun theInvokerResolvesAStaticTargetToARunnableBodyWithoutASession() {
        // The Invoker returns a plan and applies nothing; the scheduler is the only writer of run state.
        val invoker = defaultInvoker(classIndex)
        val caller = viewOf(CALLER, "passesAnArgument()I")
        val preparation = invoker.prepare(
            Invocation(descriptorOf(CALLEE, "identityInt", "(I)I"), InvokeKind.STATIC, listOf(Value.Known(4, int))),
            caller.definingClass,
        )
        val plan = invoker.plan(preparation as InvocationPreparation.Ready, Frame.entryFrame(caller))
        assertTrue(plan is InvocationPlan.RunGuest, "a present static method with a body is runnable: $plan")
        val guest = plan as InvocationPlan.RunGuest
        assertEquals("Lcalls/callee;->identityInt(I)I", guest.view.signature.toString())
        assertEquals(0, guest.entryAddress, "a dex method's code starts at address 0")
        val binding = guest.parameterBindings.single()
        assertEquals(0, binding.register, "`.registers 1` puts the one parameter in v0 -- the HIGHEST register")
        assertEquals(Value.Known(4, int), binding.value)
    }

    @Test
    fun theClassIndexTellsTheThreeLookupFailuresApart() {
        // An absent class and an absent member are two resolution failures, while a bodyless method is backend
        // work and not a failure at all.
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
     * An array type has no class def, yet `[I->clone()Ljava/lang/Object;` is a real method on a device. The
     * lookup answers [MethodLookup.NoBody]: the method exists, so `NoSuchMethod` would invent a linking error,
     * and it has no dex body because it is a runtime intrinsic. It must not delegate to `Object.clone()`'s
     * framework body, which cannot copy an array. Membership is `Object`'s declared set ([JLS] §10.7), so any
     * other descriptor is genuinely missing.
     */
    @Test
    fun anArrayDefiningClassAnswersNoBodyRatherThanCrashingTheHost() {
        assertTrue(
            classIndex.lookupMethod(VirtualType.of("[I"), "clone()Ljava/lang/Object;") is MethodLookup.NoBody,
            "an array pseudo-method exists and has no dex body; it must not be Object's runnable framework body",
        )
        // Every descriptor `Object` declares, not just `clone()`: a device runs `[Ljava/lang/String;->toString()`.
        assertTrue(
            classIndex.lookupMethod(
                VirtualType.of("[Ljava/lang/String;"),
                "toString()Ljava/lang/String;",
            ) is MethodLookup.NoBody,
        )
        // A descriptor `Object` does not declare is a real missing member.
        assertEquals(
            MethodLookup.NoSuchMethod,
            classIndex.lookupMethod(VirtualType.of("[I"), "definitelyNotAMethod()V"),
            "an array's member set is Object's plus clone(), so anything else is genuinely missing",
        )
        // An array whose component class is absent is still a resolution failure: the array rule must not
        // shadow the class check.
        assertEquals(
            MethodLookup.NoSuchClass,
            classIndex.lookupMethod(VirtualType.of("[Lcalls/absent;"), "clone()Ljava/lang/Object;"),
            "an absent component class is still an absent class",
        )
    }

    private companion object {
        const val CALLER = "Lcalls/caller;"
        const val CALLEE = "Lcalls/callee;"
        const val RESOLUTION_CALLER = "Lcalls/resolution_caller;"
    }
}
