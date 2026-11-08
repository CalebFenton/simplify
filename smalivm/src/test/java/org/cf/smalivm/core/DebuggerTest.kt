package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DebuggerTest {
    private val classIndex = CoreTester.classIndex
    private val int = VirtualType.of("I")

    private fun viewOf(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun sessionOf(
        descriptor: String,
        limits: Limits = Limits(),
    ): ExecutionSession = ExecutionSession.of(viewOf(SPINE, descriptor), classIndex, limits)

    @Test
    fun stepOverDirectCallDrainsTheCalleeAndStopsBeforeTheCallerContinuation() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        val calleeEntry = invoke.children.single()
        val calleeReturn = calleeEntry.children.single()
        assertSame(invoke.activation, continuation.activation)
        assertSame(calleeReturn, session.currentNode)
        assertSame(calleeReturn, continuation.parent)
        assertEquals(CALLEE_RETURNS_SEVEN, calleeEntry.view.signature)
        assertTrue(calleeEntry.opExecutionStarted)
        assertTrue(calleeReturn.opExecutionStarted)
        assertEquals(3, continuation.address)
        assertFalse(continuation.opExecutionStarted, "step-over stops before the caller continuation")
    }

    @Test
    fun stepOverFromCalleeReturnStopsBeforeAncestorCallerResume() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val callerActivation = debugger.cursor!!.activation
        debugger.stepInto()
        debugger.stepInto()
        val calleeReturn = debugger.cursor!!

        val callerResume = debugger.stepOver()!!

        assertSame(calleeReturn, session.currentNode)
        assertSame(callerResume, debugger.cursor)
        assertSame(callerActivation, callerResume.activation)
        assertNotSame(calleeReturn.activation, callerResume.activation)
        assertEquals(3, callerResume.address)
        assertFalse(callerResume.opExecutionStarted, "step-over stops before executing the ancestor caller")
    }

    @Test
    fun stepOverSelfRecursionUsesActivationIdentityRatherThanMethodSignature() {
        val view = viewOf(CALLER, "recursesOnceThenAddsOne(I)I")
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, Value.Known(1, int))
        val session = ExecutionSession.of(view, classIndex, Limits(), frame)
        val debugger = Debugger(session)
        debugger.stepInto()
        debugger.stepInto()
        val invoke = debugger.cursor!!
        assertEquals(3, invoke.address)

        val continuation = debugger.stepOver()!!

        val recursiveEntry = invoke.children.single()
        assertNotSame(invoke.activation, recursiveEntry.activation)
        assertSame(invoke.activation, recursiveEntry.activation.caller)
        assertEquals(invoke.view.signature, recursiveEntry.view.signature)
        assertSame(invoke.activation, continuation.activation)
        assertEquals(6, continuation.address)
        assertTrue(recursiveEntry.opExecutionStarted)
        assertFalse(continuation.opExecutionStarted)
    }

    @Test
    fun stepOverDrainsNestedCallsBeforeTheOuterCallerResumes() {
        val session = ExecutionSession.of(
            viewOf(INVOKE_REVIEW, "nestedTwoHandlers()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val outerInvoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        val nestedNodes = mutableListOf<ExecutionNode>()
        fun collect(node: ExecutionNode) {
            node.children.forEach {
                nestedNodes += it
                collect(it)
            }
        }
        collect(outerInvoke)
        assertTrue(nestedNodes.any { it.view.signature == "$INVOKE_REVIEW->innerWithTwoHandlers()I" })
        assertTrue(nestedNodes.any { it.view.signature == "Linvoke_review_callee;->throwsNpe()V" })
        assertTrue(nestedNodes.filter { it.activation !== outerInvoke.activation }.all { it.opExecutionStarted })
        assertSame(outerInvoke.activation, continuation.activation)
        assertEquals(3, continuation.address)
        assertFalse(continuation.opExecutionStarted)
    }

    @Test
    fun pendingExceptionRoutingIsInvisibleAndStepOverStopsAtTheRealOuterHandler() {
        val session = CoreTester.vmServices.newSession(
            viewOf(EXCEPTION_PROPAGATION, "outerThreeFrames()I"),
        )
        val debugger = Debugger(session)
        val startingInvoke = debugger.cursor!!

        val handler = debugger.stepOver()!!

        assertNotSame(startingInvoke, handler)
        assertSame(startingInvoke.activation, handler.activation)
        assertEquals(Opcode.MOVE_EXCEPTION, handler.view.opcodeAt(handler.address))
        assertFalse(handler.opExecutionStarted)
        assertTrue(session.frontier.all { !it.opExecutionStarted })
        assertSame(handler, session.frontier.last())
    }

    @Test
    fun invokeBreakpointDoesNotStopAgainDuringPendingExceptionRouting() {
        val view = viewOf(EXCEPTION_PROPAGATION, "outerThreeFrames()I")
        val session = CoreTester.vmServices.newSession(view)
        val debugger = Debugger(session)
        val breakpoint = Breakpoint(view.signature, 0)
        debugger.addBreakpoint(breakpoint)

        val first = debugger.runToBreakpoint()
        val second = debugger.runToBreakpoint()

        assertNotNull(first)
        assertNull(second, "internal completion routing must not publish the already-executed invoke")
        assertEquals(listOf(Outcome.Returned(Value.Known(3, int))), session.outcomes)
        assertEquals(1, session.graph().nodePile(view.codeAddressAtInstructionIndex(0)).size)
    }

    @Test
    fun publicFrontierContainsOnlyExecutablePreOpNodesDuringThreeFrameUnwind() {
        val session = CoreTester.vmServices.newSession(
            viewOf(EXCEPTION_PROPAGATION, "outerThreeFrames()I"),
        )

        while (!session.isFinished) {
            val next = session.frontier.last()
            assertFalse(next.opExecutionStarted)
            assertSame(next, session.step())
            assertTrue(session.frontier.all { !it.opExecutionStarted })
        }
    }

    @Test
    fun stepOverDrainsClassInitializerAndStopsBeforeTheSameAddressTriggerRetry() {
        val session = ExecutionSession.of(
            viewOf(CLASS_INIT_TRIGGER, "callsWitnessTouch()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val trigger = debugger.cursor!!

        val retry = debugger.stepOver()!!

        val initializerEntry = trigger.children.single()
        assertEquals(CLASS_INIT_WITNESS, initializerEntry.view.signature)
        assertTrue(initializerEntry.opExecutionStarted)
        assertTrue(initializerEntry.children.single().opExecutionStarted)
        assertTrue(initializerEntry.children.single().children.single().opExecutionStarted)
        assertNotSame(trigger, retry)
        assertSame(trigger.activation, retry.activation)
        assertEquals(trigger.address, retry.address)
        assertFalse(retry.opExecutionStarted, "the visible trigger retry remains pre-op")
    }

    @Test
    fun classForNameInitializerHonorsBreakpointThenReturnsToTheSameInvokeAddress() {
        val caller = viewOf(CLASS_FOR_NAME, "guestInitializationIsVisibleThenRetryReturnsStableClass()Ljava/lang/Class;")
        val session = CoreTester.vmServices.newSession(caller)
        val debugger = Debugger(session)
        val invokeIndex = caller.addresses.indexOfFirst { caller.opcodeAt(it) == Opcode.INVOKE_STATIC }
        val invokeAddress = caller.codeAddressAtInstructionIndex(invokeIndex)
        val initializer = viewOf(CLASS_FOR_NAME, "<clinit>()V")
        val initializerBreakpoint = Breakpoint(initializer.signature, 1)
        val retryBreakpoint = Breakpoint(caller.signature, invokeIndex)

        debugger.stepInto()
        val invoke = debugger.cursor!!
        assertEquals(invokeAddress, invoke.address)
        debugger.addBreakpoint(initializerBreakpoint)
        debugger.stepInto()

        val initializerStop = debugger.runToBreakpoint()!!
        assertEquals(initializer.signature, initializerStop.view.signature)
        assertEquals(initializer.codeAddressAtInstructionIndex(1), initializerStop.address)
        assertFalse(initializerStop.opExecutionStarted)

        debugger.removeBreakpoint(initializerBreakpoint)
        debugger.addBreakpoint(retryBreakpoint)
        val retry = debugger.runToBreakpoint()!!
        assertEquals(caller.signature, retry.view.signature)
        assertEquals(invokeAddress, retry.address)
        assertNotSame(invoke, retry)
        assertSame(invoke.activation, retry.activation)
        assertFalse(retry.opExecutionStarted)

        debugger.removeBreakpoint(retryBreakpoint)
        assertNull(debugger.runToBreakpoint())
        val graph = session.graph()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val fixtureType = VirtualType.of(CLASS_FOR_NAME)
        val countField = ClassIndex.fieldDescriptorOf(fixtureType, "guestInitCount", "I")
        assertSame(classIndex.loadClassConstant(CLASS_FOR_NAME), returned.raw)
        assertEquals(
            Value.Known(1, int),
            graph.terminatingStaticFieldConsensusOverExploredPaths(fixtureType, countField),
        )
        assertEquals(2, graph.nodePile(invokeAddress).size, "completion must not schedule a third retry")
        assertTrue(graph.nodePile(caller.nextAddress(invokeAddress)).single().opExecutionStarted)
    }

    @Test
    fun stepOverOpaqueCallConsumesOnlyTheInvokeSchedulerStep() {
        val session = ExecutionSession.of(
            viewOf(INVOKE_REVIEW, "arityMismatchOpaque()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        while (debugger.cursor!!.view.opcodeAt(debugger.cursor!!.address) != Opcode.INVOKE_STATIC) {
            debugger.stepInto()
        }
        val invoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        assertSame(invoke, session.currentNode)
        assertSame(invoke.activation, continuation.activation)
        assertSame(invoke.children.single(), continuation)
        assertEquals(Opcode.MOVE_RESULT, continuation.view.opcodeAt(continuation.address))
        assertFalse(continuation.opExecutionStarted)
    }

    @Test
    fun stepIntoPlainSynchronousCallConsumesOneInvokeNode() {
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            {
                SynchronousCallResult.Returned(
                    HostReturn.Value(HostValue.Primitive(7, int)),
                )
            },
        )
        val session = SynchronousInvocationFixtures.session(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            listOf(backend),
        )
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val moveResult = debugger.stepInto()!!

        assertSame(invoke, debugger.lastNode)
        assertSame(invoke.activation, moveResult.activation)
        assertEquals(Opcode.MOVE_RESULT, moveResult.view.opcodeAt(moveResult.address))
        assertEquals(1, invoke.children.size)
        assertFalse(moveResult.opExecutionStarted)
    }

    @Test
    fun stepOverPlainSynchronousCallCreatesNoFakeChildActivation() {
        val backend = ScriptedSynchronousBackend(
            "sync",
            { BackendProbe.Execute(SideEffectLevel.NONE) },
            {
                SynchronousCallResult.Returned(
                    HostReturn.Value(HostValue.Primitive(7, int)),
                )
            },
        )
        val session = SynchronousInvocationFixtures.session(
            CALLER,
            "callsCalleeAndReturnsItsResult()I",
            listOf(backend),
        )
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val moveResult = debugger.stepOver()!!

        assertSame(invoke, debugger.lastNode)
        assertSame(invoke.activation, moveResult.activation)
        assertTrue(invoke.children.all { it.activation === invoke.activation })
        assertEquals(0, session.callStack.depth)
    }

    @Test
    fun stepOverObservesCalleeAbortFallbackAndStopsBeforeCallerResume() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsSpinningCallee()I"),
            classIndex,
            Limits(maxAddressVisits = 2, maxSteps = 100),
        )
        val debugger = Debugger(session)
        debugger.stepInto()
        val invoke = debugger.cursor!!

        val resumedCaller = debugger.stepOver()!!

        val descendantNodes = mutableListOf<ExecutionNode>()
        fun collectDescendants(node: ExecutionNode) {
            node.children.forEach {
                if (it.activation !== invoke.activation) descendantNodes += it
                collectDescendants(it)
            }
        }
        collectDescendants(invoke)
        assertTrue(descendantNodes.any { it.outcome == Outcome.Aborted(AbortReason.ADDRESS_LIMIT) })
        assertSame(invoke.activation, resumedCaller.activation)
        assertEquals(4, resumedCaller.address)
        assertFalse(resumedCaller.opExecutionStarted, "Plan 9 fallback resumes the caller but step-over does not run it")
    }

    @Test
    fun stepOverDrainsEveryForkedDescendantBeforeAnOlderCallerSibling() {
        val outerView = viewOf(SPINE, "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val outerSignature = outerView.signature
        val calleeSignature = CALLEE_RETURNS_SEVEN
        val target = MethodDescriptor(VirtualType.of("Lcalls/callee;"), "returnsSeven", "()I")
        val session = ExecutionSession.of(outerView, classIndex, Limits(), Frame.entryFrame(outerView)) {
                address, view, index ->
            when {
                view.signature == outerSignature && address == 3 -> object : Op(address) {
                    override val name = "invoke-forking-callee"
                    override fun execute(frame: Frame) =
                        listOf(Transition.Invoke(target, IntArray(0), InvokeKind.STATIC)).asOpExecution()
                }
                view.signature == calleeSignature && address == 0 -> object : Op(address) {
                    override val name = "fork-two-return-paths"
                    override fun execute(frame: Frame) =
                        listOf(Transition.Jump(1), Transition.Jump(1)).asOpExecution()
                }
                else -> OpBuilder.build(address, view, index)
            }
        }
        val debugger = Debugger(session)
        debugger.stepInto()
        val olderCallerSibling = session.frontier.first()
        val invoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        val calleeEntry = invoke.children.single()
        val returnPaths = calleeEntry.children
        assertEquals(2, returnPaths.size)
        assertNotSame(returnPaths[0], returnPaths[1])
        assertTrue(returnPaths.all { it.opExecutionStarted })
        assertSame(returnPaths[1], session.currentNode, "both descendant paths execute in child order before resume")
        assertSame(returnPaths[1], continuation.parent)
        val callerChildren = returnPaths[1].children
        assertEquals(2, callerChildren.size)
        assertEquals(listOf(olderCallerSibling, callerChildren[1], continuation), session.frontier)
        assertFalse(olderCallerSibling.opExecutionStarted, "the older non-descendant stays scheduled")
        assertFalse(continuation.opExecutionStarted)
    }

    @Test
    fun stepOverStopsBeforeADescendantBreakpointAndContinueExecutesThatExactNode() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(CALLEE_RETURNS_SEVEN, 1))
        debugger.addBreakpoint(Breakpoint(debugger.cursor!!.view.signature, 1))

        val calleeReturn = debugger.stepOver()!!

        assertEquals(CALLEE_RETURNS_SEVEN, calleeReturn.view.signature)
        assertEquals(1, calleeReturn.address)
        assertFalse(calleeReturn.opExecutionStarted)
        assertSame(calleeReturn, debugger.cursor)

        val callerContinuation = debugger.runToBreakpoint()!!
        assertTrue(calleeReturn.opExecutionStarted, "continue consumes the shared exact-node suppression marker")
        assertSame(calleeReturn.children.single(), callerContinuation)
        assertEquals(3, callerContinuation.address)
        assertFalse(callerContinuation.opExecutionStarted)
    }

    @Test
    fun stepOverReturnsNullAtCompletionAndDoesNothingWithoutACursor() {
        val session = sessionOf("returnFive()I")
        val debugger = Debugger(session)
        debugger.stepInto()
        val finalCursor = debugger.cursor!!

        assertNull(debugger.stepOver())
        assertTrue(finalCursor.opExecutionStarted)
        assertTrue(debugger.isFinished)
        val lastNode = debugger.lastNode
        assertNull(debugger.stepOver())
        assertSame(lastNode, debugger.lastNode)
    }

    @Test
    fun stepOverFromABreakpointConsumesSuppressionAndProgresses() {
        val session = sessionOf("spinForever()V")
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(debugger.cursor!!.view.signature, 0))
        val stopped = debugger.runToBreakpoint()!!

        val next = debugger.stepOver()!!
        val revisit = debugger.runToBreakpoint()!!

        assertTrue(stopped.opExecutionStarted)
        assertSame(stopped.children.single(), next)
        assertEquals(1, next.address)
        assertNotSame(stopped, revisit)
        assertEquals(stopped.address, revisit.address)
        assertFalse(revisit.opExecutionStarted)
    }

    @Test
    fun runToBreakpointStopsBeforeTheInitialEntryExecutes() {
        val session = sessionOf("returnFive()I")
        val debugger = Debugger(session)
        val entry = debugger.cursor!!
        val breakpoint = Breakpoint(entry.view.signature, 0)
        assertTrue(debugger.addBreakpoint(breakpoint))

        val stopped = debugger.runToBreakpoint()

        assertSame(entry, stopped)
        assertSame(entry, debugger.cursor)
        assertTrue(debugger.isAtBreakpoint())
        assertFalse(entry.opExecutionStarted, "the breakpoint stops before the entry op")
        assertNull(debugger.lastNode, "stopping at the initial cursor does not select a node")
    }

    @Test
    fun runToBreakpointStopsBeforeALaterInstructionInTheCurrentMethod() {
        val session = sessionOf("returnFive()I")
        val debugger = Debugger(session)
        val entry = debugger.cursor!!
        debugger.addBreakpoint(Breakpoint(entry.view.signature, 1))

        val stopped = debugger.runToBreakpoint()!!

        assertEquals(1, stopped.address)
        assertEquals(1, debugger.currentInstructionIndex)
        assertFalse(stopped.opExecutionStarted, "the matching instruction remains pre-op")
        assertSame(entry, debugger.lastNode, "only the instruction before the breakpoint executed")
    }

    @Test
    fun aFutureCalleeBreakpointIsAcceptedAndMatchedWhenTheCalleeIsReached() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!
        assertTrue(debugger.addBreakpoint(Breakpoint(CALLEE_RETURNS_SEVEN, 0)))
        assertFalse(debugger.isAtBreakpoint(), "the active caller cursor does not match the future callee breakpoint")

        val stopped = debugger.runToBreakpoint()!!

        assertTrue(debugger.isAtBreakpoint())
        assertEquals(CALLEE_RETURNS_SEVEN, stopped.view.signature)
        assertEquals(0, stopped.address)
        assertFalse(stopped.opExecutionStarted)
        assertSame(invoke, debugger.lastNode)
        assertSame(invoke.activation, stopped.activation.caller)
    }

    @Test
    fun aRecursiveActivationStopsAgainAtTheSameBreakpointValue() {
        val view = viewOf(CALLER, "recursesOnceThenAddsOne(I)I")
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, Value.Known(1, int))
        val session = ExecutionSession.of(view, classIndex, Limits(), frame)
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(view.signature, 0))

        val outerEntry = debugger.runToBreakpoint()!!
        val recursiveEntry = debugger.runToBreakpoint()!!

        assertNotSame(outerEntry, recursiveEntry)
        assertNotSame(outerEntry.activation, recursiveEntry.activation)
        assertSame(outerEntry.activation, recursiveEntry.activation.caller)
        assertEquals(outerEntry.view.signature, recursiveEntry.view.signature)
        assertEquals(outerEntry.address, recursiveEntry.address)
        assertTrue(outerEntry.opExecutionStarted, "continue executes the exact stopped node")
        assertFalse(recursiveEntry.opExecutionStarted, "the recursive visit stops before execution")
    }

    @Test
    fun aLoopRevisitStopsAgainAtTheSameBreakpointValue() {
        val session = sessionOf("spinForever()V")
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(debugger.cursor!!.view.signature, 0))

        val firstVisit = debugger.runToBreakpoint()!!
        val secondVisit = debugger.runToBreakpoint()!!

        assertNotSame(firstVisit, secondVisit)
        assertSame(firstVisit.activation, secondVisit.activation)
        assertEquals(firstVisit.address, secondVisit.address)
        assertTrue(firstVisit.opExecutionStarted)
        assertFalse(secondVisit.opExecutionStarted)
    }

    @Test
    fun mixedDrivingDoesNotSuppressANewLoopNodeWithTheSameBreakpointValue() {
        val session = sessionOf("spinForever()V")
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(debugger.cursor!!.view.signature, 0))
        val stopped = debugger.runToBreakpoint()!!

        assertSame(stopped, session.step())
        assertSame(stopped.children.single(), session.step())
        val revisit = debugger.cursor!!
        assertNotSame(stopped, revisit)
        assertEquals(stopped.view.signature, revisit.view.signature)
        assertEquals(
            stopped.view.instructionIndexAtCodeAddress(stopped.address),
            revisit.view.instructionIndexAtCodeAddress(revisit.address),
        )

        assertSame(revisit, debugger.runToBreakpoint())
        assertFalse(revisit.opExecutionStarted, "the new node stops before execution")
    }

    @Test
    fun aForkedSiblingStopsAgainAtTheSameLocation() {
        val view = viewOf(IF_TEST, "ifEqualZeroToFallThrough(I)V")
        val session = ExecutionSession.of(view, classIndex, Limits())
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(view.signature, 1))

        val firstSibling = debugger.runToBreakpoint()!!
        val secondSibling = debugger.runToBreakpoint()!!

        assertNotSame(firstSibling, secondSibling)
        assertSame(firstSibling.parent, secondSibling.parent)
        assertSame(firstSibling.activation, secondSibling.activation)
        assertEquals(firstSibling.address, secondSibling.address)
        assertTrue(firstSibling.opExecutionStarted)
        assertFalse(secondSibling.opExecutionStarted)
    }

    @Test
    fun aSecondContinueExecutesTheExactStoppedNodeAndSearchesOnward() {
        val session = sessionOf("returnFive()I")
        val debugger = Debugger(session)
        val signature = debugger.cursor!!.view.signature
        debugger.addBreakpoint(Breakpoint(signature, 0))
        debugger.addBreakpoint(Breakpoint(signature, 1))

        val entry = debugger.runToBreakpoint()!!
        val next = debugger.runToBreakpoint()!!

        assertTrue(entry.opExecutionStarted)
        assertSame(entry, debugger.lastNode)
        assertSame(entry.children.single(), next)
        assertFalse(next.opExecutionStarted)
        assertEquals(1, debugger.currentInstructionIndex)
    }

    @Test
    fun stepIntoFromABreakpointExecutesTheCursorAndClearsContinueSuppression() {
        val session = sessionOf("spinForever()V")
        val debugger = Debugger(session)
        debugger.addBreakpoint(Breakpoint(debugger.cursor!!.view.signature, 0))
        val stopped = debugger.runToBreakpoint()!!

        val next = debugger.stepInto()!!
        val revisit = debugger.runToBreakpoint()!!

        assertTrue(stopped.opExecutionStarted)
        assertSame(stopped, debugger.lastNode?.parent)
        assertSame(stopped.children.single(), next)
        assertEquals(1, next.address)
        assertNotSame(stopped, revisit)
        assertEquals(stopped.address, revisit.address)
        assertFalse(revisit.opExecutionStarted)
    }

    @Test
    fun breakpointChangesReturnResultsAndExposeStableImmutableInsertionOrderedSnapshots() {
        val debugger = Debugger(sessionOf("returnFive()I"))
        val first = Breakpoint("Lfuture/first;->run()V", 3)
        val second = Breakpoint("Lfuture/second;->run()V", 1)
        val third = Breakpoint("Lfuture/third;->run()V", 2)

        assertTrue(debugger.addBreakpoint(first))
        assertTrue(debugger.addBreakpoint(second))
        assertFalse(debugger.addBreakpoint(first), "adding an equal breakpoint is a no-op")
        val oldSnapshot = debugger.breakpoints
        assertEquals(listOf(first, second), oldSnapshot.toList())

        assertTrue(debugger.removeBreakpoint(first))
        assertFalse(debugger.removeBreakpoint(first))
        assertTrue(debugger.addBreakpoint(third))
        assertTrue(debugger.addBreakpoint(first))
        assertEquals(listOf(second, third, first), debugger.breakpoints.toList())
        assertEquals(listOf(first, second), oldSnapshot.toList(), "an older snapshot does not change")

        @Suppress("UNCHECKED_CAST")
        val mutableView = oldSnapshot as MutableSet<Breakpoint>
        assertThrows<UnsupportedOperationException> { mutableView.add(third) }
        assertEquals(listOf(second, third, first), debugger.breakpoints.toList())

        debugger.clearBreakpoints()
        assertTrue(debugger.breakpoints.isEmpty())
        assertEquals(listOf(first, second), oldSnapshot.toList(), "clear does not change an older snapshot")
    }

    @Test
    fun aNeverReachedBreakpointDoesNotChangeExecutionAndFinishReturnsNull() {
        val direct = sessionOf("returnFive()I")
        val driven = sessionOf("returnFive()I")
        val debugger = Debugger(driven)
        debugger.addBreakpoint(Breakpoint("Lnever/reached;->future()V", 17))

        direct.run()
        assertNull(debugger.runToBreakpoint())

        assertTrue(debugger.isFinished)
        assertFalse(debugger.isAtBreakpoint())
        assertEquals(direct.outcomes, driven.outcomes)
        assertEquals(direct.completion(), driven.completion())
        val lastNode = debugger.lastNode
        assertNull(debugger.runToBreakpoint())
        assertSame(lastNode, debugger.lastNode, "continuing a finished session does no work")
    }

    @Test
    fun breakpointInstructionIndexIsNotTheVariableWidthCodeAddress() {
        val session = sessionOf("nopThenConst()I")
        val debugger = Debugger(session)
        val view = debugger.cursor!!.view
        val returnIndex = view.instructionCount - 1
        val returnAddress = view.codeAddressAtInstructionIndex(returnIndex)
        assertTrue(returnIndex != returnAddress, "the fixture must discriminate index from code address")
        debugger.addBreakpoint(Breakpoint(view.signature, returnIndex))

        val stopped = debugger.runToBreakpoint()!!

        assertEquals(returnIndex, debugger.currentInstructionIndex)
        assertEquals(returnAddress, stopped.address)
        assertFalse(stopped.opExecutionStarted)
    }

    @Test
    fun stepIntoExecutesExactlyTheStraightLineCursorAndReturnsTheNextPreOpCursor() {
        val session = sessionOf("returnFive()I")
        val debugger = Debugger(session)
        val oldCursor = debugger.cursor!!

        val nextCursor = debugger.stepInto()!!

        assertSame(oldCursor, session.currentNode)
        assertTrue(oldCursor.opExecutionStarted)
        assertSame(nextCursor, debugger.cursor)
        assertSame(oldCursor.children.single(), nextCursor)
        assertFalse(nextCursor.opExecutionStarted, "the returned cursor has not executed")
        assertEquals(1, nextCursor.address)
    }

    @Test
    fun stepIntoSelectsOnlyOneNodeFromAnAmbiguousForkAndPreservesSchedulerOrder() {
        val session = sessionOf("throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val debugger = Debugger(session)
        debugger.stepInto()
        val olderSibling = session.frontier.first()
        val selected = debugger.cursor!!

        val nextCursor = debugger.stepInto()!!

        assertSame(selected, session.currentNode)
        assertSame(selected.children.single(), nextCursor)
        assertEquals(2, session.frontier.size)
        assertSame(olderSibling, session.frontier[0])
        assertSame(nextCursor, session.frontier[1])
        assertEquals(listOf(1, 4), session.frontier.map { it.view.instructionIndexAtCodeAddress(it.address) })
        assertFalse(olderSibling.opExecutionStarted, "the older sibling remains scheduled")
        assertFalse(nextCursor.opExecutionStarted, "the selected node's child remains scheduled")
    }

    @Test
    fun debuggerDrivesEveryAmbiguousPathToCompletionInSchedulerOrder() {
        val session = sessionOf("throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val debugger = Debugger(session)
        val explored = mutableListOf<Int>()

        while (!debugger.isFinished) {
            val selected = debugger.cursor!!
            explored += selected.view.instructionIndexAtCodeAddress(selected.address)!!
            debugger.stepInto()
            assertSame(selected, debugger.lastNode)
        }

        assertEquals(listOf(0, 3, 4, 1, 2), explored)
        val graph = session.graph()
        val fork = graph.nodePile(0).single()
        assertEquals(listOf(3, 1), fork.children.map { it.view.instructionIndexAtCodeAddress(it.address) })
        assertEquals(
            listOf(
                Outcome.Threw(Value.Unknown(VirtualType.OBJECT)),
                Outcome.Returned(Value.Known("later", VirtualType.of("Ljava/lang/String;"))),
                Outcome.Returned(Value.Unknown(VirtualType.OBJECT)),
            ),
            graph.outcomes,
        )
        assertTrue(graph.methodView.addresses.filterNot(graph::isPayload).all(graph::wasReached))
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNotNull(graph.trusted())
    }

    @Test
    fun stepIntoEntersAnInvokedMethodThroughTheSessionFrontier() {
        val session = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val calleeEntry = debugger.stepInto()!!

        assertSame(invoke, session.currentNode)
        assertSame(invoke.children.single(), calleeEntry)
        assertSame(calleeEntry, session.frontier.single())
        assertEquals(CALLEE_RETURNS_SEVEN, calleeEntry.view.signature)
        assertEquals(0, calleeEntry.address)
        assertSame(invoke.activation, calleeEntry.activation.caller)
        assertFalse(calleeEntry.opExecutionStarted, "the debugger leaves the callee entry for the next scheduler step")
    }

    @Test
    fun stepIntoLeavesALocalHandlerTransitionAsTheNextCursor() {
        val session = sessionOf("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        val debugger = Debugger(session)
        val throwingNode = debugger.cursor!!

        val handler = debugger.stepInto()!!

        assertSame(throwingNode, session.currentNode)
        assertSame(throwingNode.children.single(), handler)
        assertSame(handler, session.frontier.single())
        assertSame(throwingNode.activation, handler.activation)
        assertEquals(1, handler.address)
        assertTrue(handler.frame.copy().takeCaught() is Value.Unknown)
        assertFalse(handler.opExecutionStarted)
    }

    @Test
    fun stepIntoReturnsNullForAPreOpLimitAbortAndDoesNotStepAFinishedSession() {
        val session = sessionOf("returnFive()I", Limits(maxSteps = 1))
        val debugger = Debugger(session)
        debugger.stepInto()
        val stoppedCursor = debugger.cursor!!

        assertNull(debugger.stepInto())
        assertSame(stoppedCursor, session.currentNode)
        assertEquals(Outcome.Aborted(AbortReason.METHOD_LIMIT), stoppedCursor.outcome)
        assertFalse(stoppedCursor.opExecutionStarted)
        assertTrue(session.isFinished)

        val lastNode = session.currentNode
        val outcomes = session.outcomes.toList()
        assertNull(debugger.stepInto())
        assertSame(lastNode, session.currentNode, "a finished session is not stepped")
        assertEquals(outcomes, session.outcomes)
    }

    @Test
    fun stepIntoMatchesDirectSessionStructuralTraceAndFinalResult() {
        val direct = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val driven = ExecutionSession.of(
            viewOf(CALLER, "callsCalleeAndReturnsItsResult()I"),
            classIndex,
            Limits(),
        )
        val debugger = Debugger(driven)
        val directTrace = mutableListOf<StructuralTrace>()
        val debuggerTrace = mutableListOf<StructuralTrace>()

        while (true) {
            val node = direct.step() ?: break
            directTrace += StructuralTrace(node.view.signature, node.address, node.outcome, node.opExecutionStarted)
        }
        while (!debugger.isFinished) {
            debugger.stepInto()
            val node = driven.currentNode!!
            debuggerTrace += StructuralTrace(node.view.signature, node.address, node.outcome, node.opExecutionStarted)
        }

        assertEquals(directTrace, debuggerTrace)
        assertEquals(direct.outcomes, driven.outcomes)
        assertEquals(direct.completion(), driven.completion())
        assertEquals(CompletionStatus.Complete, driven.completion())
    }

    @Test
    fun debuggerTracksTheInitialCursorAndDirectSessionStepsThroughCompletion() {
        val session = sessionOf("returnFive()I")
        val initial = session.frontier.single()
        val debugger = Debugger(session)

        assertSame(initial, debugger.cursor)
        assertNull(debugger.lastNode)
        val initialState: ExecutionState? = debugger.currentState
        assertSame(initial.frame, initialState)
        assertNull(initialState!!.peekRegister(0), "the initial cursor exposes pre-op state")
        assertEquals(0, debugger.currentInstructionIndex)
        assertFalse(debugger.isFinished)

        assertSame(initial, session.step())
        assertSame(initial, debugger.lastNode)
        assertTrue(initial.opExecutionStarted)
        assertEquals(Value.Known(5, int), initial.frame.peek(0), "lastNode exposes the completed op state")
        val returnNode = session.frontier.single()
        assertSame(returnNode, debugger.cursor)
        assertSame(returnNode.frame, debugger.currentState)
        assertEquals(Value.Known(5, int), debugger.currentState!!.peekRegister(0))
        assertEquals(1, debugger.currentInstructionIndex)

        assertSame(returnNode, session.step())
        assertSame(returnNode, debugger.lastNode)
        assertNull(debugger.cursor)
        assertNull(debugger.currentState)
        assertNull(debugger.currentInstructionIndex)
        assertTrue(debugger.isFinished)
        assertNull(session.step())
        assertEquals(listOf(Outcome.Returned(Value.Known(5, int))), session.outcomes)
    }

    @Test
    fun opExecutionStartedAndWasReachedKeepTheirExactPhaseTruthTable() {
        val preOpLimit = sessionOf("returnFive()I", Limits(maxSteps = 1))
        preOpLimit.step()
        val stopped = preOpLimit.step()!!
        assertFalse(stopped.opExecutionStarted, "a pre-op limit abort did not enter execute")
        assertFalse(preOpLimit.graph().wasReached(stopped.address))

        val normal = sessionOf("returnFive()I")
        val executed = normal.step()!!
        assertTrue(executed.opExecutionStarted)
        assertTrue(normal.graph().wasReached(executed.address))

        val unsupported = sessionOf("unsupportedWideFilledArray()I")
        unsupported.step()
        val aborted = unsupported.step()!!
        assertEquals(Outcome.Aborted(AbortReason.UNSUPPORTED_OP), aborted.outcome)
        assertTrue(aborted.opExecutionStarted, "UnsupportedOp returns Abort from execute")
        assertTrue(unsupported.graph().wasReached(aborted.address))

        val hostFailureView = viewOf(SPINE, "returnFive()I")
        val hostFailure = ExecutionSession.of(
            hostFailureView,
            classIndex,
            Limits(),
            Frame.entryFrame(hostFailureView),
        ) { address, view, index ->
            if (address == 0) {
                object : Op(address) {
                    override val name = "throws-host-failure"

                    override fun execute(frame: Frame): OpExecution =
                        throw IllegalStateException("host failure before execute returned")
                }
            } else {
                OpBuilder.build(address, view, index)
            }
        }
        val failed = hostFailure.step()!!
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), failed.outcome)
        assertTrue(failed.opExecutionStarted, "execute was entered before the host throwable escaped it")
        assertFalse(hostFailure.graph().wasReached(failed.address), "wasReached still means execute returned")
    }

    @Test
    fun aForkedStepLimitLeavesTheSelectedStoppedNodeAsLastNode() {
        val session = sessionOf(
            "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;",
            Limits(maxSteps = 1),
        )
        val debugger = Debugger(session)
        session.step()
        val drainedSibling = session.frontier.first()
        val selected = session.frontier.last()

        val stopped = session.step()!!

        assertSame(selected, stopped, "the LIFO cursor was the node selected for the limit check")
        assertTrue(drainedSibling !== stopped, "the other fork was drained by abortRun, not selected by step")
        assertEquals(Outcome.Aborted(AbortReason.METHOD_LIMIT), drainedSibling.outcome)
        assertFalse(drainedSibling.opExecutionStarted)
        assertEquals(Outcome.Aborted(AbortReason.METHOD_LIMIT), stopped.outcome)
        assertSame(stopped, debugger.lastNode, "draining siblings must not replace the selected current node")
        assertFalse(stopped.opExecutionStarted)
        assertNull(debugger.cursor)
        assertNull(debugger.currentState)
        assertNull(debugger.currentInstructionIndex)
        assertTrue(debugger.isFinished)
    }

    @Test
    fun breakpointSnapshotsRemainUntrustedUntilCleanCompletionAndAfterAbort() {
        val clean = sessionOf("returnFive()I")
        val cleanDebugger = Debugger(clean)
        cleanDebugger.addBreakpoint(Breakpoint(cleanDebugger.cursor!!.view.signature, 1))

        val beforeBreakpoint = clean.graph()
        assertEquals(CompletionStatus.InProgress, beforeBreakpoint.completion())
        assertNull(beforeBreakpoint.trusted())

        val cleanStop = cleanDebugger.runToBreakpoint()!!
        assertEquals(1, cleanStop.address)
        val atBreakpoint = clean.graph()
        assertEquals(CompletionStatus.InProgress, atBreakpoint.completion())
        assertNull(atBreakpoint.trusted())

        assertNull(cleanDebugger.runToBreakpoint())
        val complete = clean.graph()
        assertEquals(CompletionStatus.Complete, complete.completion())
        assertNotNull(complete.trusted())
        assertEquals(CompletionStatus.InProgress, beforeBreakpoint.completion())
        assertNull(beforeBreakpoint.trusted())
        assertEquals(CompletionStatus.InProgress, atBreakpoint.completion())
        assertNull(atBreakpoint.trusted())

        val aborted = sessionOf("unsupportedWideFilledArray()I")
        val root = aborted.frontier.single()
        aborted.enqueue(1, root)
        val abortedDebugger = Debugger(aborted)
        abortedDebugger.addBreakpoint(Breakpoint(root.view.signature, 0))
        val beforeAbort = aborted.graph()
        assertEquals(CompletionStatus.InProgress, beforeAbort.completion())
        assertNull(beforeAbort.trusted())

        assertSame(root, abortedDebugger.runToBreakpoint())
        assertFalse(abortedDebugger.isFinished, "the breakpoint leaves the clean root path scheduled")
        val afterAbort = aborted.graph()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), afterAbort.completion())
        assertNull(afterAbort.trusted())

        assertNull(abortedDebugger.runToBreakpoint())
        assertTrue(abortedDebugger.isFinished)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), aborted.completion())
        assertEquals(CompletionStatus.InProgress, beforeAbort.completion())
        assertNull(beforeAbort.trusted())
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), afterAbort.completion())
        assertNull(afterAbort.trusted())
    }

    @Test
    fun debuggerKeepsAForkedSiblingAfterAHostFailureAndExposesTheExactFailedNode() {
        val view = viewOf(SPINE, "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val failure = IllegalStateException("debugger host failure")
        val session = ExecutionSession.of(view, classIndex, Limits(), Frame.entryFrame(view)) {
                address, currentView, index ->
            if (currentView.signature == view.signature && address == 3) {
                object : Op(address) {
                    override val name = "throws-debugger-host-failure"
                    override fun execute(frame: Frame): OpExecution = throw failure
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }
        val debugger = Debugger(session)

        debugger.stepInto()
        val failed = debugger.cursor!!
        val survivingSibling = session.frontier.first()
        assertEquals(3, failed.address)
        assertEquals(1, survivingSibling.address)

        debugger.stepInto()

        assertSame(failed, debugger.lastNode)
        assertTrue(failed.opExecutionStarted)
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), failed.outcome)
        assertSame(failure, failed.hostFailure)
        assertEquals("debugger host failure", failed.hostFailure!!.message)
        assertTrue(failed.toString().contains("debugger host failure"))
        assertSame(survivingSibling, debugger.cursor)

        val remaining = mutableListOf<Int>()
        while (!debugger.isFinished) {
            remaining += debugger.cursor!!.address
            debugger.stepInto()
        }

        assertEquals(listOf(1, 2), remaining)
        assertEquals(
            listOf(
                Outcome.Threw(Value.Unknown(VirtualType.OBJECT)),
                Outcome.Aborted(AbortReason.HOST_FAILURE),
                Outcome.Returned(Value.Unknown(VirtualType.OBJECT)),
            ),
            session.outcomes,
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), session.completion())
        assertNull(debugger.cursor)
    }

    @Test
    fun cursorUsesTheNextLifoNodeAfterAFork() {
        val session = sessionOf("throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val debugger = Debugger(session)

        val fork = session.step()!!

        assertEquals(listOf(3, 1), fork.children.map { it.address })
        assertEquals(listOf(1, 3), session.frontier.map { it.address })
        assertSame(fork.children.first(), debugger.cursor, "the first declared handler is the next LIFO node")
        assertEquals(3, debugger.currentInstructionIndex)

        val frontierBefore = session.frontier
        val nodesBefore = listOf(fork) + frontierBefore
        val childrenBefore = nodesBefore.associateWith { it.children.toList() }
        val stateBefore = debugger.currentState!!
        val valuesBefore = (0 until stateBefore.registerCount).map(stateBefore::peekRegister)
        val assignedBefore = stateBefore.registersAssigned()
        val readBefore = debugger.cursor!!.frame.registersRead()
        val caughtBefore = debugger.cursor!!.frame.copy().takeCaught()

        repeat(3) {
            debugger.cursor
            debugger.lastNode
            debugger.currentInstructionIndex
            debugger.isFinished
            debugger.currentState!!.let { state ->
                repeat(state.registerCount) { register ->
                    state.peekRegister(register)
                    state.readRegister(register)
                }
                state.registersAssigned()
            }
        }

        assertEquals(frontierBefore.size, session.frontier.size)
        frontierBefore.zip(session.frontier).forEach { (before, after) -> assertSame(before, after) }
        childrenBefore.forEach { (node, children) ->
            assertEquals(children.size, node.children.size)
            children.zip(node.children).forEach { (before, after) -> assertSame(before, after) }
        }
        assertSame(stateBefore, debugger.currentState)
        assertEquals(valuesBefore, (0 until stateBefore.registerCount).map(stateBefore::peekRegister))
        assertEquals(assignedBefore, stateBefore.registersAssigned())
        assertEquals(readBefore, debugger.cursor!!.frame.registersRead())
        assertEquals(caughtBefore, debugger.cursor!!.frame.copy().takeCaught())
    }

    @Test
    fun instructionIndexDependsOnExecutableAddressNotOpSupport() {
        val unsupported = sessionOf("unsupportedWideFilledArray()I")
        val unsupportedDebugger = Debugger(unsupported)
        unsupported.step()
        assertEquals(1, unsupportedDebugger.currentInstructionIndex, "an unsupported executable op still has an index")

        val malformedView = viewOf("Lfill_array_data_test;", "fillArrayDataWithMalformedPayloadTarget()V")
        val malformed = ExecutionSession.of(malformedView, classIndex, Limits())
        assertEquals(0, Debugger(malformed).currentInstructionIndex, "a malformed operand does not erase its op index")

        val payloadView = viewOf("Lfill_array_data_test;", "fillArrayDataInt()V")
        val payloadSession = ExecutionSession.of(payloadView, classIndex, Limits())
        val entry = payloadSession.frontier.single()
        payloadSession.enqueue(payloadView.payloadAddresses.single(), entry)
        assertNull(Debugger(payloadSession).currentInstructionIndex, "payload data is not an executable instruction")
    }

    @Test
    fun repeatedDebuggerReadsDoNotChangeExecutionOrState() {
        val observed = statefulSession()
        val control = statefulSession()
        val debugger = Debugger(observed.session)
        val observedTrace = mutableListOf<Pair<Int, Outcome?>>()
        val controlTrace = mutableListOf<Pair<Int, Outcome?>>()

        while (!control.session.isFinished) {
            assertEquivalentCursorState(
                debugger.currentState!!,
                control.session.frontier.last().frame,
                observed.arrayRef,
                control.arrayRef,
                observed.objectRef,
                control.objectRef,
            )
            assertDebuggerReadsArePure(observed.session, debugger, observed.arrayRef, observed.objectRef)

            val observedNode = observed.session.step()!!
            val controlNode = control.session.step()!!
            observedTrace += observedNode.address to observedNode.outcome
            controlTrace += controlNode.address to controlNode.outcome
            assertEquals(controlNode.address, observedNode.address)
            assertEquals(controlNode.children.map { it.address }, observedNode.children.map { it.address })
            assertEquals(controlNode.outcome, observedNode.outcome)
            assertEquals(controlNode.opExecutionStarted, observedNode.opExecutionStarted)
            assertEquivalentCursorState(
                observedNode.frame,
                controlNode.frame,
                observed.arrayRef,
                control.arrayRef,
                observed.objectRef,
                control.objectRef,
            )
        }

        assertDebuggerReadsArePure(observed.session, debugger, observed.arrayRef, observed.objectRef)
        assertEquals(controlTrace, observedTrace)
        assertEquals(control.session.outcomes, observed.session.outcomes)
        assertEquals(control.session.completion(), observed.session.completion())
        assertEquals(control.session.graph().outcomes, observed.session.graph().outcomes, "final results are unchanged")
    }

    @Test
    fun debuggerObservationPreservesFinalAnalysisFacts() {
        val directOpaque = sessionOf("arityMismatchOpaque()I", className = INVOKE_REVIEW).run()
        val observedOpaque = runWithDebuggerObservation(sessionOf("arityMismatchOpaque()I", className = INVOKE_REVIEW))
        assertEquivalentFinalGraphs(directOpaque, observedOpaque)

        val directArrayRef = (directOpaque.terminatingConsensusOverExploredPaths(1) as Value.Known).raw as HeapRef
        val observedArrayRef = (observedOpaque.terminatingConsensusOverExploredPaths(1) as Value.Known).raw as HeapRef
        val directArray = directOpaque.terminatingHeapObjectOverExploredPaths(directArrayRef) as HeapObject.TrackedArray
        val observedArray = observedOpaque.terminatingHeapObjectOverExploredPaths(observedArrayRef) as HeapObject.TrackedArray
        assertEquals(directArray.type, observedArray.type)
        assertEquals(directArray.elements(), observedArray.elements())
        assertEquals(Value.Unknown(int), observedArray.element(0))
        assertTrue(finalNodeFacts(observedOpaque).any { it.invocationDiagnostic != null })

        val incompleteIndex = ClassIndex(CoreTester.importFixtures(), hierarchyComplete = false)
        val incompleteView = incompleteIndex.requireMethodView(INVOKE_REVIEW, "incompleteWorld(Linvoke_unique_base;)I")
        val directDispatch = ExecutionSession.of(incompleteView, incompleteIndex, Limits()).run()
        val observedDispatch = runWithDebuggerObservation(
            ExecutionSession.of(incompleteView, incompleteIndex, Limits()),
        )
        assertEquivalentFinalGraphs(directDispatch, observedDispatch)
        assertTrue(finalNodeFacts(observedDispatch).any { it.dispatchCandidates.isNotEmpty() })

        val directClassInit = sessionOf("callsWitnessTouch()I", className = CLASS_INIT_TRIGGER).run()
        val observedClassInit = runWithDebuggerObservation(
            sessionOf("callsWitnessTouch()I", className = CLASS_INIT_TRIGGER),
        )
        assertEquivalentFinalGraphs(directClassInit, observedClassInit)
        val witness = VirtualType.of("Lclass_init/witness;")
        val directClassStates = returningNodes(directClassInit).map { it.frame.initState(witness) }
        val observedClassStates = returningNodes(observedClassInit).map { it.frame.initState(witness) }
        assertEquals(directClassStates, observedClassStates)
        assertEquals(listOf(ClassInitState.INITIALIZED), observedClassStates)
    }

    private class StatefulSession(
        val session: ExecutionSession,
        val arrayRef: HeapRef,
        val objectRef: HeapRef,
    )

    private data class StructuralTrace(
        val methodSignature: String,
        val address: Int,
        val outcome: Outcome?,
        val opExecutionStarted: Boolean,
    )

    private data class FinalNodeFact(
        val methodSignature: String,
        val address: Int,
        val outcome: Outcome?,
        val opExecutionStarted: Boolean,
        val children: List<Pair<String, Int>>,
        val sideEffectLevel: SideEffectLevel,
        val dispatchCandidates: Set<String>,
        val invocationDiagnostic: String?,
    )

    private fun statefulSession(): StatefulSession {
        val view = viewOf(SPINE, "moveChain()I")
        val frame = Frame.entryFrame(view)
        val array = frame.allocateArray(INT_ARRAY, arrayOf(Value.Known(4, int), Value.Known(8, int)))
        val instance = frame.allocateObject(OBJECT_TYPE, mapOf(INSTANCE_FIELD to Value.Known(17, int)))
        val arrayRef = array.raw as HeapRef
        val objectRef = instance.raw as HeapRef
        frame.assign(0, array)
        frame.assign(1, instance)
        frame.assign(2, Value.Known(23, int))
        frame.read(1)
        frame.writeStaticField(STATIC_FIELD, Value.Known(31, int))
        frame.completeClassInit(STATE_TYPE)
        return StatefulSession(
            ExecutionSession.of(view, classIndex, Limits(), frame),
            arrayRef,
            objectRef,
        )
    }

    private fun sessionOf(descriptor: String, className: String): ExecutionSession =
        ExecutionSession.of(viewOf(className, descriptor), classIndex, Limits())

    private fun runWithDebuggerObservation(session: ExecutionSession): ExecutionGraph {
        val debugger = Debugger(session)
        while (!debugger.isFinished) {
            debugger.cursor
            debugger.lastNode
            debugger.currentInstructionIndex
            debugger.currentState?.let { state ->
                repeat(state.registerCount) { state.peekRegister(it) }
                state.registersAssigned()
            }
            session.graph().let { snapshot ->
                snapshot.completion()
                snapshot.trusted()
                snapshot.addresses().forEach(snapshot::wasReached)
            }
            debugger.stepInto()
        }
        return session.graph()
    }

    private fun assertEquivalentFinalGraphs(expected: ExecutionGraph, actual: ExecutionGraph) {
        assertEquals(expected.outcomes, actual.outcomes)
        assertEquals(expected.completion(), actual.completion())
        assertEquals(expected.methodSideEffectLevelOverExploredPaths(), actual.methodSideEffectLevelOverExploredPaths())
        assertEquals(expected.addresses().toList(), actual.addresses().toList())
        assertEquals(finalNodeFacts(expected), finalNodeFacts(actual))
        for (address in expected.addresses()) {
            assertEquals(expected.wasReached(address), actual.wasReached(address), "reached address $address")
            assertEquals(expected.parentAddresses(address).toList(), actual.parentAddresses(address).toList())
            assertEquals(expected.children(address).toList(), actual.children(address).toList())
            assertEquals(expected.sideEffectLevel(address), actual.sideEffectLevel(address))
            for (register in 0 until expected.methodView.registerCount) {
                assertEquals(
                    expected.consensusOverExploredPaths(address, register),
                    actual.consensusOverExploredPaths(address, register),
                    "consensus at $address/v$register",
                )
                assertEquals(
                    expected.registerValuesOverExploredPaths(address, register),
                    actual.registerValuesOverExploredPaths(address, register),
                    "values at $address/v$register",
                )
            }
        }
    }

    private fun finalNodeFacts(graph: ExecutionGraph): List<FinalNodeFact> =
        graph.addresses().flatMap { address ->
            graph.nodePile(address).map { node ->
                FinalNodeFact(
                    node.view.signature,
                    node.address,
                    node.outcome,
                    node.opExecutionStarted,
                    node.children.map { it.view.signature to it.address },
                    node.sideEffectLevel,
                    node.dispatchCandidates,
                    node.invocationDiagnostic,
                )
            }
        }

    private fun returningNodes(graph: ExecutionGraph): List<ExecutionNode> =
        graph.addresses().flatMap(graph::nodePile).filter { it.outcome is Outcome.Returned }

    private fun assertDebuggerReadsArePure(
        session: ExecutionSession,
        debugger: Debugger,
        arrayRef: HeapRef,
        objectRef: HeapRef,
    ) {
        val cursorBefore = debugger.cursor
        val lastNodeBefore = debugger.lastNode
        val stateBefore = debugger.currentState
        val indexBefore = debugger.currentInstructionIndex
        val finishedBefore = debugger.isFinished
        val frontierBefore = session.frontier
        val childrenBefore = (frontierBefore + listOfNotNull(lastNodeBefore)).associateWith { it.children.toList() }
        val outcomesBefore = session.outcomes.toList()
        val completionBefore = session.completion()
        val valuesBefore = stateBefore?.let { state -> (0 until state.registerCount).map(state::peekRegister) }
        val assignedBefore = stateBefore?.registersAssigned()
        val readBefore = cursorBefore?.frame?.registersRead()
        val arrayBefore = (stateBefore?.heapObject(arrayRef) as? HeapObject.TrackedArray)?.elements()
        val fieldBefore = stateBefore?.getField(objectRef, INSTANCE_FIELD)
        val classStateBefore = stateBefore?.initState(STATE_TYPE)
        val staticBefore = stateBefore?.getStaticField(STATE_TYPE, STATIC_FIELD)

        repeat(3) {
            debugger.cursor
            debugger.lastNode
            debugger.currentInstructionIndex
            debugger.isFinished
            debugger.currentState?.let { state ->
                repeat(state.registerCount) { register ->
                    state.peekRegister(register)
                    state.readRegister(register)
                }
                state.registersAssigned()
                state.heapObject(arrayRef)
                state.heapObject(objectRef)
                state.getField(objectRef, INSTANCE_FIELD)
                state.initState(STATE_TYPE)
                state.isClassInitialized(STATE_TYPE)
                state.getStaticField(STATE_TYPE, STATIC_FIELD)
            }
        }

        assertSame(cursorBefore, debugger.cursor)
        assertSame(lastNodeBefore, debugger.lastNode)
        assertSame(stateBefore, debugger.currentState)
        assertEquals(indexBefore, debugger.currentInstructionIndex)
        assertEquals(finishedBefore, debugger.isFinished)
        assertEquals(frontierBefore.size, session.frontier.size)
        frontierBefore.zip(session.frontier).forEach { (before, after) -> assertSame(before, after) }
        childrenBefore.forEach { (node, children) ->
            assertEquals(children.size, node.children.size)
            children.zip(node.children).forEach { (before, after) -> assertSame(before, after) }
        }
        assertEquals(outcomesBefore, session.outcomes)
        assertEquals(completionBefore, session.completion())
        assertEquals(valuesBefore, stateBefore?.let { state -> (0 until state.registerCount).map(state::peekRegister) })
        assertEquals(assignedBefore, stateBefore?.registersAssigned())
        assertEquals(readBefore, cursorBefore?.frame?.registersRead(), "debugger register reads do not forge liveness")
        assertEquals(arrayBefore, (stateBefore?.heapObject(arrayRef) as? HeapObject.TrackedArray)?.elements())
        assertEquals(fieldBefore, stateBefore?.getField(objectRef, INSTANCE_FIELD))
        assertEquals(classStateBefore, stateBefore?.initState(STATE_TYPE))
        assertEquals(staticBefore, stateBefore?.getStaticField(STATE_TYPE, STATIC_FIELD))
    }

    private fun assertEquivalentCursorState(
        observed: ExecutionState,
        control: Frame,
        observedArrayRef: HeapRef,
        controlArrayRef: HeapRef,
        observedObjectRef: HeapRef,
        controlObjectRef: HeapRef,
    ) {
        assertEquals(
            (0 until control.registerCount).map(control::peek),
            (0 until observed.registerCount).map(observed::peekRegister),
        )
        assertEquals(control.registersAssigned(), observed.registersAssigned())
        assertEquals(control.registersRead(), (observed as Frame).registersRead())
        assertEquals(
            (control.heapObject(controlArrayRef) as HeapObject.TrackedArray).elements(),
            (observed.heapObject(observedArrayRef) as HeapObject.TrackedArray).elements(),
        )
        assertEquals(
            control.getField(controlObjectRef, INSTANCE_FIELD),
            observed.getField(observedObjectRef, INSTANCE_FIELD),
        )
        assertEquals(control.initState(STATE_TYPE), observed.initState(STATE_TYPE))
        assertEquals(control.getStaticField(STATE_TYPE, STATIC_FIELD), observed.getStaticField(STATE_TYPE, STATIC_FIELD))
    }

    private companion object {
        const val SPINE = "Lspine/spine;"
        const val EXCEPTION_PROPAGATION = "Lexception_propagation_test;"
        const val CALLER = "Lcalls/caller;"
        const val IF_TEST = "Lif_test;"
        const val INVOKE_REVIEW = "Linvoke_review;"
        const val CLASS_FOR_NAME = "Lbackend/class_for_name_test;"
        const val CLASS_INIT_TRIGGER = "Lclass_init/trigger;"
        const val CLASS_INIT_WITNESS = "Lclass_init/witness;-><clinit>()V"
        const val CALLEE_RETURNS_SEVEN = "Lcalls/callee;->returnsSeven()I"
        val INT_ARRAY = VirtualType.of("[I")
        val OBJECT_TYPE = VirtualType.of("Ldebugger/object;")
        val STATE_TYPE = VirtualType.of("Ldebugger/state;")
        const val INSTANCE_FIELD = "Ldebugger/object;->value:I"
        const val STATIC_FIELD = "Ldebugger/state;->value:I"
    }
}
