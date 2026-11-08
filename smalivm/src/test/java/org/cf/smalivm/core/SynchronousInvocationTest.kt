package org.cf.smalivm.core

import org.cf.smalivm.core.SynchronousInvocationFixtures.CALLEE
import org.cf.smalivm.core.SynchronousInvocationFixtures.CALLER
import org.cf.smalivm.core.SynchronousInvocationFixtures.INIT_TARGET
import org.cf.smalivm.core.SynchronousInvocationFixtures.INVOKE_REVIEW
import org.cf.smalivm.core.SynchronousInvocationFixtures.allNodes
import org.cf.smalivm.core.SynchronousInvocationFixtures.classType
import org.cf.smalivm.core.SynchronousInvocationFixtures.classIndex
import org.cf.smalivm.core.SynchronousInvocationFixtures.intType
import org.cf.smalivm.core.SynchronousInvocationFixtures.longType
import org.cf.smalivm.core.SynchronousInvocationFixtures.nativePlan
import org.cf.smalivm.core.SynchronousInvocationFixtures.objectType
import org.cf.smalivm.core.SynchronousInvocationFixtures.session
import org.cf.smalivm.core.SynchronousInvocationFixtures.view
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SynchronousInvocationTest {
    @Test
    fun onlyDeclinedAdvancesAndDeniedIsTerminal() {
        val first = ScriptedProbeBackend("emulator") { BackendProbe.Declined("unsupported shape") }
        val denied = ScriptedProbeBackend("reflection") { BackendProbe.Denied("policy denied") }
        val later = ScriptedProbeBackend("later") { error("must not probe") }
        val invoker = invokerOf(classIndex, listOf(first, denied, later))

        val plan = nativePlan(invoker)

        assertTrue(plan is OpaqueInvocation)
        assertEquals("policy denied", (plan as OpaqueInvocation).diagnostic)
        assertEquals(1, first.probeCount)
        assertEquals(1, denied.probeCount)
        assertEquals(0, later.probeCount)
    }

    @Test
    fun deniedCallContinuesOnceWithTypedUnknownAndStrongEffect() {
        val vm = VmServices(classIndex, ReflectionPolicy(exactUnsafeMethods = setOf(NATIVE_CALLEE)))
        val graph = session(CALLER, "callsNativeCallee()I", vm).run()
        val invoke = graph.nodePile(0).single()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(intType), graph.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertEquals("reflection policy denied $NATIVE_CALLEE by EXACT_UNSAFE", invoke.invocationDiagnostic)
        assertEquals(1, invoke.children.size)
    }

    @Test
    fun allDeclinesPlanOneOpaqueCallWithEveryDiagnosticInOrder() {
        val first = ScriptedProbeBackend("emulator") { BackendProbe.Declined("no emulator") }
        val second = ScriptedProbeBackend("reflection") { BackendProbe.Declined("no reflection") }

        val plan = nativePlan(invokerOf(classIndex, listOf(first, second)))

        assertTrue(plan is OpaqueInvocation)
        plan as OpaqueInvocation
        assertEquals(intType, plan.returnType)
        assertEquals("emulator: no emulator; reflection: no reflection", plan.diagnostic)
    }

    @Test
    fun everyAcceptedProbeStopsSelectionBeforeALaterBackend() {
        val accepted = listOf<BackendProbe>(
            BackendProbe.Schedule(view(CALLEE, "returnsSeven()I"), 0),
            BackendProbe.Execute(SideEffectLevel.NONE),
            BackendProbe.Execute(SideEffectLevel.WEAK),
            BackendProbe.Execute(SideEffectLevel.STRONG),
        )
        for (answer in accepted) {
            val first: Backend = if (answer is BackendProbe.Execute) {
                ScriptedSynchronousBackend(
                    "first",
                    { answer },
                    { SynchronousCallResult.Returned(HostReturn.Void) },
                )
            } else {
                ScriptedProbeBackend("first") { answer }
            }
            val later = ScriptedProbeBackend("later") { error("must not probe") }
            val invoker = invokerOf(classIndex, listOf(first, later))

            nativePlan(invoker)

            val firstProbeCount = when (first) {
                is ScriptedProbeBackend -> first.probeCount
                is ScriptedSynchronousBackend -> first.probeCount
                else -> error("unexpected scripted backend")
            }
            assertEquals(1, firstProbeCount)
            assertEquals(0, later.probeCount)
        }
    }

    /**
     * The emulator answers a call whose guest body the application bytecode backend would otherwise schedule,
     * so any fallback to a later backend shows up as an activation of that body.
     */
    @Test
    fun returnedThrownInitAndFailureResultsNeverFallBackToALaterBackend() {
        val results = listOf<SynchronousCallResult>(
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(9, intType))),
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException("target")),
            ),
            SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)),
            SynchronousCallResult.InfrastructureFailure(IllegalStateException("lookup")),
        )
        for (result in results) {
            val vm = VmServices(classIndex)
            var executions = 0
            vm.emulate(RETURNS_SEVEN, SideEffectLevel.STRONG) {
                executions++
                result
            }
            val session = session(CALLER, "callsCalleeAndReturnsItsResult()I", vm)

            val invoke = session.step()!!

            assertEquals(1, executions, result.toString())
            assertTrue(invoke.children.none { it.view.signature == RETURNS_SEVEN_SIGNATURE }, result.toString())
            assertTrue(session.frontier.none { it.view.signature == RETURNS_SEVEN_SIGNATURE }, result.toString())
        }
    }

    @Test
    fun guestScheduleCreatesAnActivationButSynchronousExecuteDoesNot() {
        val guest = session(CALLER, "callsCalleeAndReturnsItsResult()I", VmServices(classIndex))
        val guestInvoke = guest.step()!!
        assertNotSame(guestInvoke.activation, guestInvoke.children.single().activation)

        val vm = VmServices(classIndex)
        vm.emulate(RETURNS_SEVEN) { SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(7, intType))) }
        val host = session(CALLER, "callsCalleeAndReturnsItsResult()I", vm)
        val hostInvoke = host.step()!!

        assertSame(hostInvoke.activation, hostInvoke.children.single().activation)
        assertEquals(0, host.callStack.depth)
        assertEquals(1, host.callStack.frames.size)
    }

    @Test
    fun guestCalleeAbortUsesOpaqueContinuationWithoutSelectingAgain() {
        val vm = VmServices(classIndex)
        val selections = vm.countSelections(MethodDescriptor(VirtualType.of(CALLEE), "spinsForever", "()V"))
        val graph = session(
            CALLER,
            "callsSpinningCallee()I",
            vm,
            Limits(maxAddressVisits = 2, maxSteps = 100),
        ).run()

        assertEquals(1, selections())
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(Value.Known(2, intType), graph.explored.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.explored.sideEffectLevel(1))
        assertTrue(allNodes(graph.nodePile(1).single()).any { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) })
    }

    /** The callee's first op throws a host exception, which no real opcode does. */
    @Test
    fun guestHostFailureKeepsCauseThenResumesCallerOpaqueWithoutSelectingAgain() {
        val cause = IllegalStateException("guest op failed")
        val vm = VmServices(classIndex)
        val selections = vm.countSelections(RETURNS_SEVEN)
        val callerView = view(FORKING_CALLER, "forksThenCallsReturnsSeven(I)I")
        val invokeAt = callerView.nextAddress(0)
        val session = vm.newSession(callerView, Limits(), null) { address, method, resolver ->
            if (method.signature == RETURNS_SEVEN_SIGNATURE && address == 0) {
                object : Op(address) {
                    override val name = "fail-in-guest"
                    override fun execute(frame: Frame): OpExecution = throw cause
                }
            } else {
                OpBuilder.build(address, method, resolver)
            }
        }

        session.step()
        val olderSibling = session.frontier.first()
        val invoke = session.step()!!
        val failedCallee = session.step()!!
        val continuation = failedCallee.children.single()

        assertEquals(invokeAt, invoke.address)
        assertSame(cause, failedCallee.outcome.hostFailure)
        assertEquals(AbortReason.HOST_FAILURE, failedCallee.outcome.abortReason)
        assertEquals(RETURNS_SEVEN_SIGNATURE, failedCallee.view.signature)
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertSame(invoke.activation, continuation.activation)
        assertEquals(callerView.nextAddress(invokeAt), continuation.address)
        assertEquals(OpProgress.Pending, continuation.progress)
        assertEquals(listOf(olderSibling, continuation), session.frontier)
        assertEquals(OpProgress.Pending, olderSibling.progress)
        assertEquals(1, selections())

        val moveResult = session.step()!!
        val callerReturn = session.step()!!

        assertSame(continuation, moveResult)
        assertEquals(Outcome.Returned(Value.Unknown(intType)), callerReturn.outcome)
        assertEquals(listOf(Outcome.Returned(Value.Unknown(intType))), session.outcomes)
        assertEquals(listOf(olderSibling), session.frontier)
        assertEquals(OpProgress.Pending, olderSibling.progress)
    }

    @Test
    fun effectIsVisibleOnNodeAndActivationBeforeTargetThrowAndFailureExecution() {
        val results = listOf<SynchronousCallResult>(
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException("target")),
            ),
            SynchronousCallResult.InfrastructureFailure(IllegalStateException("machinery")),
        )
        for (result in results) {
            lateinit var execution: ExecutionSession
            val observed = mutableListOf<SideEffectLevel>()
            val vm = VmServices(classIndex)
            vm.emulate(NATIVE_CALLEE, SideEffectLevel.WEAK) {
                observed += execution.currentNode!!.sideEffectLevel
                observed += execution.currentNode!!.activation.declaredEffect
                result
            }
            execution = session(CALLER, "callsNativeCallee()I", vm)

            execution.step()

            assertEquals(listOf(SideEffectLevel.WEAK, SideEffectLevel.WEAK), observed, result.toString())
            assertEquals(SideEffectLevel.WEAK, execution.currentNode!!.sideEffectLevel)
        }
    }

    /** Each emulated value differs from what the guest callee would return, so a guest result cannot pass. */
    @Test
    fun narrowWideObjectTypedNullAndVoidReturnsUseTheCorrectMoveResultBehavior() {
        val narrow = returning(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            RETURNS_SEVEN,
            HostReturn.Value(HostValue.Primitive(9, intType)),
        )
        assertEquals(Value.Known(9, intType), narrow.outcomes.single().returnedValue())

        val wide = returning(
            CALLER,
            "passesAWideArgument()J",
            MethodDescriptor(VirtualType.of(CALLEE), "identityLong", "(J)J"),
            HostReturn.Value(HostValue.Primitive(43L, longType)),
        )
        assertEquals(Value.Known(43L, longType), wide.outcomes.single().returnedValue())

        val objectGraph = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            IDENTITY_OBJECT,
            HostReturn.Value(HostValue.Reference("canonical", objectType)),
        )
        assertEquals(Value.Known("canonical", objectType), objectGraph.outcomes.single().returnedValue())

        val typedNull = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            IDENTITY_OBJECT,
            HostReturn.Value(HostValue.TypedNull(objectType)),
        )
        assertEquals(Value.Known(null, objectType), typedNull.outcomes.single().returnedValue())

        val voidGraph = returning(
            CALLER,
            "callsVoidCallee()I",
            MethodDescriptor(VirtualType.of(CALLEE), "returnsNothing", "()V"),
            HostReturn.Void,
        )
        assertEquals(Value.Known(3, intType), voidGraph.outcomes.single().returnedValue())
    }

    @Test
    fun classValueReturnPreservesTheRepresentedClassAndClassType() {
        val represented = String::class.java
        val graph = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            IDENTITY_OBJECT,
            HostReturn.Value(HostValue.Reference(represented, classType)),
        )
        val returned = graph.outcomes.single().returnedValue() as Value.Known

        assertSame(represented, returned.raw)
        assertEquals(classType, returned.type)
    }

    @Test
    fun targetThrowDoesNotRetryTheInvokeOrFallBackToTheGuestBody() {
        val vm = VmServices(classIndex)
        var executions = 0
        vm.emulate(THROWS_UNCAUGHT, SideEffectLevel.STRONG) {
            executions++
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException("target")),
            )
        }
        val session = session(CALLER, "callsThrowingCalleeWithCatch()I", vm)
        val invoke = session.frontier.single()
        session.run()
        val nodes = allNodes(invoke)
        val invokeAttempts = nodes.filter { it.view.signature == invoke.view.signature && it.address == invoke.address }

        assertEquals(1, invokeAttempts.size)
        assertEquals(1, executions)
        assertTrue(nodes.none { it.view.signature == THROWS_UNCAUGHT_SIGNATURE })
    }

    @Test
    fun targetThrowUsesHostRuntimeTypeWhenBackendTagIsOnlyThrowable() {
        val vm = VmServices(classIndex)
        var executions = 0
        vm.emulate(THROWS_UNCAUGHT) {
            executions++
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/Throwable;"), NullPointerException("runtime type wins")),
            )
        }

        val graph = session(CALLER, "callsThrowingCalleeWithCompetingCatch()I", vm).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(5, intType), graph.outcomes.single().returnedValue())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(1, executions)
    }

    @Test
    fun infrastructureFailureAbortsOnlyItsPathAndKeepsTheExactCauseAndSibling() {
        val cause = IllegalStateException("host lookup broke")
        val vm = VmServices(classIndex)
        vm.emulate(NATIVE_CALLEE, SideEffectLevel.STRONG) { SynchronousCallResult.InfrastructureFailure(cause) }
        val entry = view(FORKING_CALLER, "forksThenCallsNativeCallee(I)I")
        val session = vm.newSession(entry)

        session.step()
        val olderSibling = session.frontier.first()
        val failed = session.step()!!

        assertSame(cause, failed.outcome.hostFailure)
        assertEquals(AbortReason.HOST_FAILURE, failed.outcome.abortReason)
        assertEquals(listOf(olderSibling), session.frontier)
        assertEquals(OpProgress.Pending, olderSibling.progress)
        assertSame(failed, session.currentNode)
        assertEquals(entry.signature, failed.view.signature)
        assertEquals(entry.nextAddress(0), failed.address)
    }

    @Test
    fun virtualMachineErrorThrownByASynchronousHandlerEscapes() {
        val fatal = StackOverflowError("fatal")
        val vm = VmServices(classIndex)
        vm.emulate(NATIVE_CALLEE, SideEffectLevel.STRONG) { throw fatal }
        val session = session(CALLER, "callsNativeCallee()I", vm)

        assertSame(fatal, assertThrows<StackOverflowError> { session.step() })
        assertEquals(null, session.currentNode!!.outcome.hostFailure)
    }

    @Test
    fun classInitRequestSchedulesVisibleDescendantWorkThenRetriesTheSameInvoke() {
        val results = ArrayDeque<SynchronousCallResult>().apply {
            add(SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)))
            add(SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(9, intType))))
        }
        val vm = VmServices(classIndex)
        var executions = 0
        vm.emulate(NATIVE_CALLEE, SideEffectLevel.STRONG) {
            executions++
            results.removeFirst()
        }
        val session = session(CALLER, "callsNativeCallee()I", vm)
        val root = session.frontier.single()
        val graph = session.run()
        val nodes = allNodes(root)
        val invokes = nodes.filter { it.view.signature == root.view.signature && it.address == root.address }
        val initializer = nodes.single {
            it.activation.classInitContext?.activeInitializer?.type == VirtualType.of(INIT_TARGET) && it.address == 0
        }

        assertEquals(2, executions)
        assertEquals(2, invokes.size)
        assertSame(invokes.first().activation, invokes.last().activation)
        assertNotSame(invokes.first().activation, initializer.activation)
        assertSame(invokes.first().activation, initializer.activation.caller)
        assertTrue(initializer.progress is OpProgress.Executed)
        assertEquals(Value.Known(9, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun dirtyMirrorSynchronizationDoesNotTurnClassInitPreparationIntoUserMutation() {
        // Synchronizing a dirty host mirror before the call is preparation, not a host mutation, so it must not
        // block the class-init retry: the guest lattice state is already authoritative.
        val results = ArrayDeque<SynchronousCallResult>().apply {
            add(SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)))
            add(SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(9, intType))))
        }
        val vm = VmServices(classIndex)
        var executions = 0
        vm.emulate(
            MethodDescriptor(VirtualType.of("Lorg/cf/smalivm/core/BackendCorpusHostFixture;"), "arrayNeedsInit", "([I)I"),
        ) {
            executions++
            results.removeFirst()
        }
        val view = view("Linvoke_backend_test;", "dirtyArrayNeedsInit([I)I")
        val frame = Frame.entryFrame(view)
        val raw = intArrayOf(0)
        val admitted = frame.admitHostSeeds(
            listOf(HostSeed(Value.Known(raw, VirtualType.of("[I")))),
        ).single() as Value.Known
        val ref = admitted.raw as HeapRef
        frame.assign(view.parameters.single().register, admitted)
        frame.writeArrayElement(ref, 0, Value.Known(7, intType))
        val session = vm.newSession(view, Limits(), frame)

        val graph = session.run()

        assertEquals(2, executions)
        assertEquals(Value.Known(9, intType), graph.outcomes.single().returnedValue())
        assertEquals(0, raw[0])
        assertEquals(7, (requireNotNull(session.currentNode).frame.hostObjectAt(ref) as IntArray)[0])
    }

    private fun returning(
        className: String,
        descriptor: String,
        target: MethodDescriptor,
        result: HostReturn,
    ): ExecutionGraph {
        val vm = VmServices(classIndex)
        vm.emulate(target) { SynchronousCallResult.Returned(result) }
        return session(className, descriptor, vm).run()
    }

    private fun Outcome.returnedValue(): Value? = (this as Outcome.Returned).value

    private companion object {
        const val FORKING_CALLER = "Lcalls/resolution_caller;"
        const val RETURNS_SEVEN_SIGNATURE = "Lcalls/callee;->returnsSeven()I"
        const val THROWS_UNCAUGHT_SIGNATURE = "Lcalls/callee;->throwsUncaught()I"
        val RETURNS_SEVEN = MethodDescriptor(VirtualType.of(CALLEE), "returnsSeven", "()I")
        val THROWS_UNCAUGHT = MethodDescriptor(VirtualType.of(CALLEE), "throwsUncaught", "()I")
        val NATIVE_CALLEE = MethodDescriptor(VirtualType.of(CALLEE), "nativeCallee", "()I")
        val IDENTITY_OBJECT = MethodDescriptor(
            VirtualType.of("Linvoke_review_callee;"),
            "identityObject",
            "(Ljava/lang/Object;)Ljava/lang/Object;",
        )
    }
}
