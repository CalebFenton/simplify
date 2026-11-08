package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

/** Compile and behavior coverage for the public execution path defined by Plan 12 Task 44. */
class PublicExecutionApiTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val vmServices = VmServices(classIndex)

    @Test
    fun snapshotMethodExecutionReturnsFiveAndExposesExactOpMetadata() {
        val view = classIndex.requireMethodView(SPINE, "returnFive()I")
        val graph = vmServices.newSession(view).run()
        val expected = listOf(Outcome.Returned(Value.Known(5, VirtualType.of("I"))))

        assertEquals(expected, graph.outcomes)
        val const = graph.getOp(0)
        assertEquals(0, const.address)
        assertEquals("const/4", const.name)
        assertEquals(emptySet<Int>(), const.registersRead)
        assertEquals(setOf(0), const.registersAssigned)
        val returnOp = graph.getOp(1)
        assertEquals("return", returnOp.name)
        assertEquals(setOf(0), returnOp.registersRead)
        assertEquals(emptySet<Int>(), returnOp.registersAssigned)
    }

    @Test
    fun preRunAndReturnedSnapshotsCannotMutateStoredOutcomes() {
        val method = classIndex.requireMethodView(SPINE, "throwUncaught(Ljava/lang/RuntimeException;)V")
        val session = vmServices.newSession(method)
        val beforeRun = session.outcomes

        val graph = session.run()
        val expected = listOf(Outcome.Threw(Value.Unknown(VirtualType.of("Ljava/lang/RuntimeException;"))))
        assertTrue(beforeRun.isEmpty(), "a snapshot taken before execution must not change during the run")
        assertEquals(expected, session.outcomes)
        assertEquals(expected, graph.outcomes)

        runCatching { (session.outcomes as MutableList<Outcome>).add(Outcome.Returned(null)) }
        assertEquals(expected, session.outcomes, "mutating a returned session snapshot must not mutate the run")
        assertEquals(expected, graph.outcomes)

        runCatching { (graph.outcomes as MutableList<Outcome>).add(Outcome.Returned(null)) }
        assertEquals(expected, graph.outcomes)
    }

    @Test
    fun negativeLimitValueIsRejectedForEveryField() {
        val invalidLimits = listOf<() -> Limits>(
            { Limits(maxAddressVisits = -1) },
            { Limits(maxSteps = -1) },
            { Limits(maxExecutionTimeMillis = -1) },
            { Limits(maxCallDepth = -1) },
        )

        invalidLimits.forEach { construct ->
            assertThrows(IllegalArgumentException::class.java) { construct() }
        }
    }

    @Test
    fun unknownBranchWithEqualReturnsPreservesDuplicateOutcomes() {
        val graph = vmServices.newSession(
            classIndex.requireMethodView(SPINE, "returnsFiveOnBothUnknownBranches(I)I")
        ).run()
        val returnedFive = Outcome.Returned(Value.Known(5, VirtualType.of("I")))

        assertEquals(listOf(returnedFive, returnedFive), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun guestCreatedThrowableEscapesAsKnownHeapRefOutcome() {
        val graph = vmServices.newSession(
            classIndex.requireMethodView(SPINE, "createsAndThrowsRuntimeException()V")
        ).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val exception = (graph.outcomes.single() as Outcome.Threw).exception
        assertTrue(exception is Value.Known, "the guest-created throwable must remain concrete: $exception")
        assertEquals(VirtualType.of("Ljava/lang/RuntimeException;"), exception.type)
        assertTrue((exception as Value.Known).raw is HeapRef, "the concrete throwable must preserve guest identity")
    }

    @Test
    fun `EX-12 direct guest-defined CustomException escape is Complete and concrete`() {
        val graph = vmServices.newSession(
            classIndex.requireMethodView(EXCEPTIONAL_CODE, "createAndThrowException()V"),
        ).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val exception = (graph.outcomes.single() as Outcome.Threw).exception
        assertTrue(exception is Value.Known, "the guest-defined throwable must remain concrete: $exception")
        assertEquals(CUSTOM_EXCEPTION, exception.type)
        assertTrue((exception as Value.Known).raw is HeapRef)
    }

    @Test
    fun `EX-12 callsExceptionalMethod catch and rethrow preserve the CustomException HeapRef`() {
        val graph = vmServices.newSession(
            classIndex.requireMethodView(EXCEPTIONAL_CODE,
            "callsExceptionalMethodAndReturnsException()Lorg/cf/test/CustomException;",),
        ).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val exception = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(exception is Value.Known, "the rethrown guest-defined throwable must remain concrete: $exception")
        val known = exception as Value.Known
        assertEquals(CUSTOM_EXCEPTION, known.type)
        val ref = known.raw
        assertTrue(ref is HeapRef)

        val heapObject = graph.explored.terminatingHeapObject(ref as HeapRef).agreed()
        assertTrue(heapObject is HeapObject.TrackedObject)
        val tracked = heapObject as HeapObject.TrackedObject
        assertEquals(CUSTOM_EXCEPTION, tracked.type)
        // Plan 12 principle 7 forbids reconstructing a throwable from its type during catch and rethrow.
        assertEquals(known, tracked.field(CAUGHT_IDENTITY_FIELD))
    }

    @Test
    fun mixedReturnAndThrowOutcomesRetainBothPathResultsWithoutOrderingClaims() {
        val graph = vmServices.newSession(classIndex.requireMethodView(SPINE, "returnsOrThrows(I)I")).run()
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>()
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, graph.outcomes.size)
        assertEquals(listOf(Value.Known(5, VirtualType.of("I"))), returned.map(Outcome.Returned::value))
        assertEquals(1, threw.size)
        assertEquals(VirtualType.of("Ljava/lang/RuntimeException;"), threw.single().exception.type)
    }

    @Test
    fun equalLookingExceptionsRemainTwoOutcomesWithTwoGuestRefs() {
        val graph = vmServices.newSession(
            classIndex.requireMethodView(SPINE, "throwsTwoEqualLookingExceptions(I)V"),
        ).run()
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>()
        val refs = threw.map { (it.exception as Value.Known).raw as HeapRef }

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, graph.outcomes.size)
        assertEquals(2, threw.size)
        assertEquals(setOf(VirtualType.of("Ljava/lang/RuntimeException;")), threw.map { it.exception.type }.toSet())
        assertEquals(2, refs.toSet().size)
    }

    @Test
    fun partialGraphKeepsReturnedAndThrownPathsWithoutTrustedQueries() {
        val target = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwNested",
            "()V",
        )
        val vm = VmServices(classIndex).also { services ->
            services.invocationServices.emulators.register(
                EmulatorRegistration(
                    EmulatorMatcher.Exact(target),
                    EmulatorCapability(handlesUnknownArguments = false),
                    SideEffectLevel.WEAK,
                    EmulatorHandler {
                        SynchronousCallResult.InfrastructureFailure(IllegalStateException("infrastructure"))
                    },
                ),
            )
        }
        val graph = vm.newSession(
            classIndex.requireMethodView(SPINE, "returnsThrowsOrCallsHost(II)I"),
        ).run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(3, graph.outcomes.size)
        assertEquals(
            listOf(Value.Known(7, VirtualType.of("I"))),
            graph.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull(Outcome.Returned::value),
        )
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>().single().exception as Value.Known
        assertEquals(VirtualType.of("Ljava/lang/RuntimeException;"), threw.type)
        assertTrue(threw.raw is HeapRef)
        assertEquals(1, graph.outcomes.count { it.abortReason == AbortReason.HOST_FAILURE })
    }

    private companion object {
        const val CAUGHT_IDENTITY_FIELD =
            "Lorg/cf/test/CustomException;->caughtIdentity:Lorg/cf/test/CustomException;"
        val CUSTOM_EXCEPTION = VirtualType.of("Lorg/cf/test/CustomException;")
        const val EXCEPTIONAL_CODE = "Lorg/cf/test/ExceptionalCode;"
        const val SPINE = "Lspine/spine;"
    }
}
