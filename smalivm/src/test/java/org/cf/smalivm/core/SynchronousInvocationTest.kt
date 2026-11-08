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
import org.cf.smalivm.core.SynchronousInvocationFixtures.nativeCallSession
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
    /**
     * Catches selection that does not probe in list order: a later backend's 9, or an opaque Unknown chosen
     * before every backend is asked, would replace the first accepting backend's 7.
     */
    @Test
    fun firstAcceptingBackendInListOrderSuppliesTheResult() {
        val declined = ScriptedProbeBackend("emulator") { BackendProbe.Declined("emulator declined") }
        fun returning(name: String, value: Int) = ScriptedSynchronousBackend(
            name,
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            { SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(value, intType))) },
        )
        val seven = returning("virtual", 7)
        val nine = returning("reflection", 9)

        val graph = session(CALLER, "callsNativeCallee()I", listOf(declined, seven, nine)).run()

        assertEquals(Value.Known(7, intType), graph.outcomes.single().returnedValue())
        assertEquals(1, declined.probeCount)
        assertEquals(0, nine.probeCount)
    }

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
        val denied = ScriptedProbeBackend("reflection") { BackendProbe.Denied("policy denied") }
        val graph = session(CALLER, "callsNativeCallee()I", listOf(denied)).run()
        val invoke = graph.nodePile(0).single()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(intType), graph.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertEquals("policy denied", invoke.invocationDiagnostic)
        assertEquals(1, invoke.children.size)
    }

    @Test
    fun allDeclinesCreateOneStrongOpaqueContinuationWithOneDiagnostic() {
        val first = ScriptedProbeBackend("emulator") { BackendProbe.Declined("no emulator") }
        val second = ScriptedProbeBackend("reflection") { BackendProbe.Declined("no reflection") }
        val graph = session(CALLER, "callsNativeCallee()I", listOf(first, second)).run()
        val invoke = graph.nodePile(0).single()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(intType), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertEquals(1, invoke.children.size)
        assertEquals("emulator: no emulator; reflection: no reflection", invoke.invocationDiagnostic)
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

    @Test
    fun returnedThrownInitAndFailureResultsNeverProbeAnotherBackend() {
        val results = listOf<SynchronousCallResult>(
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(7, intType))),
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException("target")),
            ),
            SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)),
            SynchronousCallResult.InfrastructureFailure(IllegalStateException("lookup")),
        )
        for (result in results) {
            val backend = ScriptedSynchronousBackend(
                "accepted",
                { BackendProbe.Execute(SideEffectLevel.STRONG) },
                { result },
            )
            val later = ScriptedProbeBackend("later") { error("must not probe") }
            val session = session(CALLER, "callsNativeCallee()I", listOf(backend, later))

            session.step()

            assertEquals(1, backend.probeCount)
            assertEquals(1, backend.executeCount)
            assertEquals(0, later.probeCount)
        }
    }

    @Test
    fun guestScheduleCreatesAnActivationButSynchronousExecuteDoesNot() {
        val schedule = ScriptedProbeBackend("guest") { lookup ->
            BackendProbe.Schedule((lookup as MethodLookup.Runnable).view, 0)
        }
        val guest = session(CALLER, "callsCalleeAndReturnsItsResult()I", listOf(schedule))
        val guestInvoke = guest.step()!!
        assertNotSame(guestInvoke.activation, guestInvoke.children.single().activation)

        val synchronous = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            { SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(7, intType))) },
        )
        val host = session(CALLER, "callsCalleeAndReturnsItsResult()I", listOf(synchronous))
        val hostInvoke = host.step()!!

        assertSame(hostInvoke.activation, hostInvoke.children.single().activation)
        assertEquals(0, host.callStack.depth)
        assertEquals(1, host.callStack.frames.size)
    }

    @Test
    fun guestCalleeAbortUsesOpaqueContinuationWithoutHostProbe() {
        val host = ScriptedProbeBackend("host") { error("guest abort must not retry a backend") }
        val graph = session(
            CALLER,
            "callsSpinningCallee()I",
            listOf(ApplicationBytecodeBackend(classIndex.currentResolver()), host),
            Limits(maxAddressVisits = 2, maxSteps = 100),
        ).run()

        assertEquals(0, host.probeCount)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(Value.Known(2, intType), graph.terminatingConsensusOverExploredPaths(0))
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(1))
        assertTrue(allNodes(graph.nodePile(1).single()).any { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) })
    }

    @Test
    fun guestHostFailureKeepsCauseThenResumesCallerOpaqueWithoutProbingHostBackends() {
        val cause = IllegalStateException("guest op failed")
        val reflection = ScriptedProbeBackend("reflection") { BackendProbe.Declined("not reached") }
        val callerView = view(CALLER, "callsCalleeAndReturnsItsResult()I")
        val calleeView = view(CALLEE, "returnsSeven()I")
        // The root forks to two real invoke nodes at address 0, preserving the fixture's move-result at 3.
        var callerEntryBuilds = 0
        val session = session(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            listOf(ApplicationBytecodeBackend(classIndex.currentResolver()), reflection),
            opFactory = { address, method, index ->
                when {
                    method.signature == callerView.signature && address == 0 -> object : Op(address) {
                        private val delegate = OpBuilder.build(address, method, index)
                        private val forks = callerEntryBuilds++ == 0
                        override val name = if (forks) "fork-before-call" else delegate.name
                        override fun execute(frame: Frame) = if (forks) {
                            listOf(Transition.Jump(0), Transition.Jump(0)).asOpExecution()
                        } else {
                            delegate.execute(frame)
                        }
                    }
                    method.signature == calleeView.signature && address == 0 -> object : Op(address) {
                        override val name = "fail-in-guest"
                        override fun execute(frame: Frame): OpExecution = throw cause
                    }
                    else -> OpBuilder.build(address, method, index)
                }
            },
        )

        session.step()
        val olderSibling = session.frontier.first()
        val invoke = session.step()!!
        val failedCallee = session.step()!!
        val continuation = failedCallee.children.single()

        assertSame(cause, failedCallee.hostFailure)
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), failedCallee.outcome)
        assertEquals(calleeView.signature, failedCallee.view.signature)
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertSame(invoke.activation, continuation.activation)
        assertEquals(3, continuation.address)
        assertFalse(continuation.opExecutionStarted)
        assertEquals(listOf(olderSibling, continuation), session.frontier)
        assertFalse(olderSibling.opExecutionStarted)
        assertEquals(0, reflection.probeCount)

        val moveResult = session.step()!!
        val callerReturn = session.step()!!

        assertSame(continuation, moveResult)
        assertEquals(Outcome.Returned(Value.Unknown(intType)), callerReturn.outcome)
        assertEquals(listOf(Outcome.Returned(Value.Unknown(intType))), session.outcomes)
        assertEquals(listOf(olderSibling), session.frontier)
        assertFalse(olderSibling.opExecutionStarted)
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
            val backend = ScriptedSynchronousBackend(
                "sync",
                { BackendProbe.Execute(SideEffectLevel.WEAK) },
                {
                    assertEquals(SideEffectLevel.WEAK, execution.currentNode!!.sideEffectLevel)
                    assertEquals(SideEffectLevel.WEAK, execution.currentNode!!.activation.declaredEffect)
                    result
                },
            )
            execution = nativeCallSession(backend)

            execution.step()

            assertEquals(SideEffectLevel.WEAK, execution.currentNode!!.sideEffectLevel)
        }
    }

    @Test
    fun narrowWideObjectTypedNullAndVoidReturnsUseTheCorrectMoveResultBehavior() {
        val narrow = returning(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            HostReturn.Value(HostValue.Primitive(7, intType)),
        )
        assertEquals(Value.Known(7, intType), narrow.outcomes.single().returnedValue())

        val wide = returning(
            CALLER,
            "passesAWideArgument()J",
            HostReturn.Value(HostValue.Primitive(42L, longType)),
        )
        assertEquals(Value.Known(42L, longType), wide.outcomes.single().returnedValue())

        val objectGraph = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            HostReturn.Value(
                HostValue.Reference("canonical", objectType),
            ),
        )
        assertEquals(Value.Known("canonical", objectType), objectGraph.outcomes.single().returnedValue())

        val typedNull = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            HostReturn.Value(HostValue.TypedNull(objectType)),
        )
        assertEquals(Value.Known(null, objectType), typedNull.outcomes.single().returnedValue())

        val voidGraph = returning(CALLER, "callsVoidCallee()I", HostReturn.Void)
        assertEquals(Value.Known(3, intType), voidGraph.outcomes.single().returnedValue())
    }

    @Test
    fun classValueReturnPreservesTheRepresentedClassAndClassType() {
        val represented = String::class.java
        val graph = returning(
            INVOKE_REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            HostReturn.Value(
                HostValue.Reference(represented, classType),
            ),
        )
        val returned = graph.outcomes.single().returnedValue() as Value.Known

        assertSame(represented, returned.raw)
        assertEquals(classType, returned.type)
    }

    @Test
    fun targetThrowDoesNotRetryTheInvokeOrProbeAnotherBackend() {
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.STRONG) },
            {
                SynchronousCallResult.TargetThrew(
                    HostThrowable(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException("target")),
                )
            },
        )
        val later = ScriptedProbeBackend("later") { BackendProbe.Declined("must not retry") }
        val session = session(CALLER, "callsThrowingCalleeWithCatch()I", listOf(backend, later))
        val invoke = session.frontier.single()
        session.run()
        val invokeAttempts = allNodes(invoke).filter {
            it.view.signature == invoke.view.signature && it.address == invoke.address
        }

        assertEquals(1, invokeAttempts.size)
        assertEquals(1, backend.probeCount)
        assertEquals(1, backend.executeCount)
        assertEquals(0, later.probeCount)
    }

    @Test
    fun targetThrowUsesHostRuntimeTypeWhenBackendTagIsOnlyThrowable() {
        val backend = ScriptedSynchronousBackend(
            "mislabeled-target",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            {
                SynchronousCallResult.TargetThrew(
                    HostThrowable(
                        VirtualType.of("Ljava/lang/Throwable;"),
                        NullPointerException("runtime type wins"),
                    ),
                )
            },
        )

        val graph = session(CALLER, "callsThrowingCalleeWithCompetingCatch()I", listOf(backend)).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(5, intType), graph.outcomes.single().returnedValue())
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(1, backend.probeCount)
        assertEquals(1, backend.executeCount)
    }

    @Test
    fun infrastructureFailureAbortsOnlyItsPathAndKeepsTheExactCauseAndSibling() {
        val cause = IllegalStateException("host lookup broke")
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.STRONG) },
            { SynchronousCallResult.InfrastructureFailure(cause) },
        )
        val entry = view(CALLER, "callsCalleeAndReturnsItsResult()I")
        val target = MethodDescriptor(VirtualType.of(CALLEE), "nativeCallee", "()I")
        val session = session(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            listOf(backend),
            opFactory = { address, method, index ->
                when (address) {
                    0 -> object : Op(address) {
                        override val name = "fork"
                        override fun execute(frame: Frame) =
                            listOf(Transition.Jump(3), Transition.Jump(3)).asOpExecution()
                    }
                    3 -> object : Op(address) {
                        override val name = "invoke-native"
                        override fun execute(frame: Frame) =
                            listOf(Transition.Invoke(target, IntArray(0), InvokeKind.STATIC)).asOpExecution()
                    }
                    else -> OpBuilder.build(address, method, index)
                }
            },
        )

        session.step()
        val olderSibling = session.frontier.first()
        val failed = session.step()!!

        assertSame(cause, failed.hostFailure)
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), failed.outcome)
        assertEquals(listOf(olderSibling), session.frontier)
        assertFalse(olderSibling.opExecutionStarted)
        assertSame(failed, session.currentNode)
        assertEquals(entry.signature, failed.view.signature)
    }

    @Test
    fun virtualMachineErrorThrownBySynchronousBackendEscapes() {
        val fatal = StackOverflowError("fatal")
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.STRONG) },
            { throw fatal },
        )
        val session = nativeCallSession(backend)

        assertSame(fatal, assertThrows<StackOverflowError> { session.step() })
        assertEquals(null, session.currentNode!!.hostFailure)
    }

    @Test
    fun classInitRequestSchedulesVisibleDescendantWorkThenRetriesTheSameInvoke() {
        val results = ArrayDeque<SynchronousCallResult>().apply {
            add(SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)))
            add(SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(9, intType))))
        }
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.STRONG) },
            { results.removeFirst() },
        )
        val session = nativeCallSession(backend)
        val root = session.frontier.single()
        val graph = session.run()
        val nodes = allNodes(root)
        val invokes = nodes.filter { it.view.signature == root.view.signature && it.address == root.address }
        val initializer = nodes.single {
            it.activation.classInitContext?.activeInitializer?.type == VirtualType.of(INIT_TARGET) && it.address == 0
        }

        assertEquals(2, backend.executeCount)
        assertEquals(2, invokes.size)
        assertSame(invokes.first().activation, invokes.last().activation)
        assertNotSame(invokes.first().activation, initializer.activation)
        assertSame(invokes.first().activation, initializer.activation.caller)
        assertTrue(initializer.opExecutionStarted)
        assertEquals(Value.Known(9, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun dirtyMirrorSynchronizationDoesNotTurnClassInitPreparationIntoUserMutation() {
        // Bug class: pre-execution mirror synchronization compares as host mutation and blocks NeedsClassInit retry.
        // Oracle: guest lattice state is already authoritative; synchronization is preparation and compares equal.
        val results = ArrayDeque<SynchronousCallResult>().apply {
            add(SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET)))
            add(SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(9, intType))))
        }
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            { results.removeFirst() },
        )
        val view = view("Linvoke_backend_test;", "dirtyArrayNeedsInit([I)I")
        val frame = Frame.entryFrame(view)
        val raw = intArrayOf(0)
        val admitted = frame.admitHostSeeds(
            listOf(HostSeed(Value.Known(raw, VirtualType.of("[I")))),
        ).single() as Value.Known
        val ref = admitted.raw as HeapRef
        frame.assign(view.parameters.single().register, admitted)
        frame.writeArrayElement(ref, 0, Value.Known(7, intType))
        val session = ExecutionSession.of(
            view,
            classIndex,
            Limits(),
            frame,
            OpBuilder::build,
            invokerOf(classIndex, listOf(backend)),
        )

        val graph = session.run()

        assertEquals(2, backend.executeCount)
        assertEquals(Value.Known(9, intType), graph.outcomes.single().returnedValue())
        assertEquals(0, raw[0])
        assertEquals(7, (requireNotNull(session.currentNode).frame.hostObjectAt(ref) as IntArray)[0])
    }

    private fun returning(className: String, descriptor: String, result: HostReturn): ExecutionGraph {
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            { SynchronousCallResult.Returned(result) },
        )
        return session(className, descriptor, listOf(backend)).run()
    }

    private fun Outcome.returnedValue(): Value? = (this as Outcome.Returned).value
}
