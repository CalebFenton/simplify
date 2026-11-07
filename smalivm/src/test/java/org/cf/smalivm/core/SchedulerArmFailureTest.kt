package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SchedulerArmFailureTest {
    @Test
    fun rejectedCalleeArmKeepsItsExactStateThroughOpaqueFallback() {
        val caller = view("callForkingCallee()I")
        val callee = view("forkingCallee()V")
        val initial = Frame.entryFrame(caller).also {
            it.writeStaticField(STATE_FIELD, Value.Known(0, INT))
            it.completeClassInit(OWNER)
        }
        val fork = object : Op(0) {
            override val name = "fork-with-rejected-state"

            override fun execute(frame: Frame): OpExecution = OpExecution.of(
                OpArm.Jump(9999, OpUpdate.writeStatic(STATE_FIELD, Value.Known(11, INT))),
                OpArm.Continue(OpUpdate.writeStatic(STATE_FIELD, Value.Known(22, INT))),
            )
        }
        val graph = ExecutionSession.of(caller, CoreTester.vmServices, Limits(), initial) { address, current, index ->
            if (current.signature == callee.signature && address == 0) fork
            else OpBuilder.build(address, current, index)
        }.run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        val calleeFork = graph.nodePile(0).single().children.single()
        assertEquals(Value.Unknown(INT), calleeFork.frame.getStaticField(OWNER, STATE_FIELD))
        assertEquals(1, calleeFork.children.size, "the buildable callee arm must survive")
        assertEquals(
            setOf(Value.Known(11, INT), Value.Known(22, INT)),
            graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }.toSet(),
            "the rejected path must carry 11, not the source node's reconciled Unknown, into fallback",
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun calleePreparationFailureKeepsItsExactStateThroughOpaqueFallback() {
        val caller = view("callForkingCallee()I")
        val callee = view("forkingCallee()V")
        val initial = Frame.entryFrame(caller).also {
            it.writeStaticField(STATE_FIELD, Value.Known(0, INT))
            it.completeClassInit(OWNER)
        }
        val failedUpdate = OpUpdate.writeStatic(STATE_FIELD, Value.Known(33, INT))
            .then(OpUpdate.assign(99, Value.Known(34, INT)))
        val fork = scripted(
            OpArm.Continue(failedUpdate),
            OpArm.Continue(OpUpdate.writeStatic(STATE_FIELD, Value.Known(44, INT))),
        )
        val graph = ExecutionSession.of(caller, CoreTester.vmServices, Limits(), initial) { address, current, index ->
            if (current.signature == callee.signature && address == 0) fork
            else OpBuilder.build(address, current, index)
        }.run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(
            setOf(Value.Known(33, INT), Value.Known(44, INT)),
            graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }.toSet(),
            "opaque fallback must resume from the failed arm's state, not its successful sibling",
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun preparationFailureAbortsOnlyItsArmAndKeepsTheSibling() {
        val failedUpdate = OpUpdate.assign(0, Value.Known(3, INT))
            .then(OpUpdate.assign(99, Value.Known(4, INT)))
        val session = sessionWithArms(
            OpArm.Continue(failedUpdate),
            OpArm.Continue(OpUpdate.assign(0, Value.Known(7, INT))),
        )

        val graph = session.run()

        assertEquals(
            setOf(Outcome.Aborted(AbortReason.HOST_FAILURE), Outcome.Returned(Value.Known(7, INT))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        val fork = graph.nodePile(0).single()
        assertEquals(Value.Known(7, INT), fork.frame.peek(0), "only successfully prepared arms are reconciled")
        assertEquals(
            Value.Known(3, INT),
            fork.endingFrame!!.peek(0),
            "the failed arm retains mutation completed before preparation failed",
        )
        assertEquals(1, fork.children.size, "the independently feasible arm must still commit")
    }

    @Test
    fun planningFailureAbortsOnlyItsArmAndKeepsTheSibling() {
        val invalidInvoke = OpArm.Invoke(
            MethodDescriptor(VirtualType.of("Lcalls/callee;"), "identityInt", "(I)I"),
            intArrayOf(99),
            InvokeKind.STATIC,
        )
        val graph = sessionWithArms(
            invalidInvoke,
            OpArm.Continue(OpUpdate.assign(0, Value.Known(7, INT))),
        ).run()

        assertEquals(
            setOf(Outcome.Aborted(AbortReason.HOST_FAILURE), Outcome.Returned(Value.Known(7, INT))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertEquals(1, graph.nodePile(0).single().children.size)
        assertTrue(graph.nodePile(0).single().hostFailure is IllegalArgumentException)
    }

    @Test
    fun planningFailureKeepsADirectReturnSibling() {
        val invalidInvoke = OpArm.Invoke(
            MethodDescriptor(VirtualType.of("Lcalls/callee;"), "identityInt", "(I)I"),
            intArrayOf(99),
            InvokeKind.STATIC,
        )
        val graph = sessionWithArms(
            invalidInvoke,
            OpArm.Return(Value.Known(7, INT)),
        ).run()

        assertEquals(
            setOf(Outcome.Aborted(AbortReason.HOST_FAILURE), Outcome.Returned(Value.Known(7, INT))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun virtualMachineErrorDuringPlanningEscapesWithoutCommittingSibling() {
        val fatal = StackOverflowError("fatal planning failure")
        val target = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwNested",
            "()V",
        )
        val vm = VmServices(CoreTester.classIndex).also {
            it.invocationServices.emulators.register(
                EmulatorRegistration(
                    EmulatorMatcher.Exact(target),
                    EmulatorCapability(handlesUnknownArguments = false),
                    SideEffectLevel.NONE,
                    EmulatorHandler { throw fatal },
                ),
            )
        }
        val view = view("armFailureSibling()I")
        val fork = scripted(
            OpArm.Continue(OpUpdate.assign(0, Value.Known(7, INT))),
            OpArm.Invoke(target, IntArray(0), InvokeKind.STATIC),
        )
        val session = ExecutionSession.of(view, vm, Limits(), Frame.entryFrame(view)) { address, current, index ->
            if (address == 0) fork else OpBuilder.build(address, current, index)
        }

        assertSame(fatal, assertThrows<StackOverflowError> { session.step() })
        assertTrue(session.currentNode!!.children.isEmpty(), "the earlier successful arm must not commit a child")
        assertTrue(session.outcomes.isEmpty(), "the earlier successful arm must not commit an outcome")
    }

    private fun sessionWithArms(vararg arms: OpArm): ExecutionSession {
        val view = view("armFailureSibling()I")
        val fork = scripted(*arms)
        return ExecutionSession.of(
            view,
            CoreTester.classIndex,
            Limits(),
            Frame.entryFrame(view),
        ) { address, current, index ->
            if (address == 0) fork else OpBuilder.build(address, current, index)
        }
    }

    private fun scripted(vararg arms: OpArm): Op = object : Op(0) {
        override val name = "scripted-arms"
        override fun execute(frame: Frame): OpExecution = OpExecution.of(*arms)
    }

    private fun view(descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(OWNER.descriptor, descriptor)

    private companion object {
        val OWNER: VirtualType = VirtualType.of("Lscheduler_regression;")
        val INT: VirtualType = VirtualType.of("I")
        const val STATE_FIELD = "Lscheduler_regression;->state:I"
    }
}
