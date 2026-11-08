package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Task 49 public behavior for accepted target throws and infrastructure failures. */
class HostExceptionBoundaryBehaviorTest {
    @Test
    fun reflectedAndEmulatedTargetsShareTheGuestIdentityPath() {
        val cases = listOf(
            "reflection" to reflectionVm(),
            "emulation" to emulatorVm(
                effect = SideEffectLevel.NONE,
                result = {
                    SynchronousCallResult.TargetThrew(
                        HostThrowable(ILLEGAL_STATE, IllegalStateException("emulated target")),
                    )
                },
            ),
        )

        for ((name, vm) in cases) {
            val roundTrip = execute(vm, "identityRoundTrip()I")
            assertEquals(CompletionStatus.Complete, roundTrip.completion(), name)
            assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), roundTrip.outcomes, name)

            val publicOutcomes = execute(vm, "identityAcrossPublicOutcomes(I)Ljava/lang/Throwable;")
            assertEquals(CompletionStatus.Complete, publicOutcomes.completion(), name)
            val returned = (publicOutcomes.outcomes.single { it is Outcome.Returned } as Outcome.Returned).value
                as Value.Known
            val escaped = (publicOutcomes.outcomes.single { it is Outcome.Threw } as Outcome.Threw).exception
                as Value.Known
            assertEquals(ILLEGAL_STATE, returned.type, name)
            assertEquals(returned, escaped, name)
        }
    }

    @Test
    fun guestRoutingPreservesDeclaredEffectsWithoutAddingItsOwn() {
        for (effect in listOf(SideEffectLevel.NONE, SideEffectLevel.WEAK)) {
            val vm = emulatorVm(
                effect = effect,
                result = {
                    SynchronousCallResult.TargetThrew(
                        HostThrowable(ILLEGAL_STATE, IllegalStateException("emulated target")),
                    )
                },
            )

            val graph = execute(vm, "identityRoundTrip()I")

            assertEquals(effect, graph.addresses().maxOf(graph::sideEffectLevel))
        }
    }

    @Test
    fun linkageErrorTargetRemainsGuestVisible() {
        val vm = emulatorVm(
            effect = SideEffectLevel.NONE,
            result = {
                SynchronousCallResult.TargetThrew(
                    HostThrowable(ILLEGAL_ACCESS_ERROR, IllegalAccessError("guest linkage")),
                )
            },
        )

        val graph = execute(vm, "identityAcrossPublicOutcomes(I)Ljava/lang/Throwable;")
        val returned = (graph.outcomes.single { it is Outcome.Returned } as Outcome.Returned).value
        val escaped = (graph.outcomes.single { it is Outcome.Threw } as Outcome.Threw).exception

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, graph.outcomes.size)
        assertEquals(ILLEGAL_ACCESS_ERROR, escaped.type)
        assertEquals(escaped, returned)
        assertTrue(escaped is Value.Known && escaped.raw is HeapRef)
    }

    @Test
    fun hostOnlyRuntimeExceptionUsesTheFirstMatchingHandler() {
        val thrown = HostOnlyRuntimeException("host-only")
        val runtimeType = virtualTypeOfHostClass(thrown.javaClass)
        val vm = emulatorVm(
            effect = SideEffectLevel.NONE,
            result = { SynchronousCallResult.TargetThrew(HostThrowable(runtimeType, thrown)) },
        )

        assertFalse(CoreTester.classIndex.currentSnapshot.hierarchy.facts(runtimeType).descriptorPresent)
        val graph = execute(vm, "hostOnlyRuntimeFirstMatch()I")
        val clauses = graph.methodView.tryBlocks.flatMap(TryBlock::catches)
        val runtimeHandler = clauses.single { it.exceptionType == RUNTIME_EXCEPTION }.handlerAddress
        val exceptionHandler = clauses.single { it.exceptionType == EXCEPTION }.handlerAddress

        assertEquals(listOf(RUNTIME_EXCEPTION, EXCEPTION), clauses.map(CatchClause::exceptionType))
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
        assertTrue(graph.wasReached(runtimeHandler))
        assertFalse(graph.wasReached(exceptionHandler))
        assertFalse(
            CoreTester.classIndex.currentSnapshot.hierarchy.facts(runtimeType).descriptorPresent,
            "runtime evidence must remain attached to this throwable, not mutate ClassIndex",
        )
    }

    @Test
    fun hostOnlyNonThrowableBecomesAFreshInternalError() {
        val raw = HostOnlyNonThrowable()
        val runtimeType = virtualTypeOfHostClass(raw.javaClass)
        val view = view("nonThrowableBecomesInternalError(Ljava/lang/Object;)Ljava/lang/Throwable;")
        val vm = VmServices(CoreTester.classIndex)
        val frame = Frame.entryFrame(view)
        val source = frame.admitHostSeeds(
            listOf(HostSeed(Value.Known(raw, OBJECT))),
        ).single() as Value.Known
        frame.assign(view.parameters.single().register, source)

        assertFalse(CoreTester.classIndex.currentSnapshot.hierarchy.facts(runtimeType).descriptorPresent)
        val graph = vm.newSession(view, entryFrame = frame).run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val returnedRef = returned.raw as HeapRef

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(INTERNAL_ERROR, returned.type)
        assertNotEquals(source.raw, returnedRef)
        assertTrue(graph.terminatingHostObjectOverExploredPaths(returnedRef) is InternalError)
    }

    @Test
    fun infrastructureFailureIsPartialKeepsItsSiblingAndCannotReachAGuestHandler() {
        val failure = IllegalStateException("infrastructure")
        val vm = emulatorVm(
            effect = SideEffectLevel.WEAK,
            result = { SynchronousCallResult.InfrastructureFailure(failure) },
        )

        val branching = execute(vm, "branchingInfrastructure(I)I")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), branching.completion())
        assertEquals(
            listOf(Value.Known(7, INT)),
            branching.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull(Outcome.Returned::value),
        )
        assertEquals(1, branching.outcomes.count { it == Outcome.Aborted(AbortReason.HOST_FAILURE) })
        assertEquals(SideEffectLevel.WEAK, branching.addresses().maxOf(branching::sideEffectLevel))

        val caught = execute(vm, "catchTarget()I")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), caught.completion())
        assertEquals(emptyList<Outcome.Returned>(), caught.outcomes.filterIsInstance<Outcome.Returned>())
    }

    @Test
    fun reflectionMachineryFailureCannotReachAGuestHandler() {
        val graph = execute(reflectionVm(MISSING_ON_HOST), "reflectionInfrastructureFailure()I")

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertEquals(emptyList<Outcome.Returned>(), graph.outcomes.filterIsInstance<Outcome.Returned>())
    }

    @Test
    fun targetThrowKeepsMutationCompletedBeforeTheThrow() {
        val vm = reflectionVm(MUTATE_THEN_THROW, STRING_BUILDER_TO_STRING)
        val view = view("mutationSurvivesTargetThrow(Ljava/lang/StringBuilder;)Ljava/lang/String;")
        val frame = Frame.entryFrame(view)
        val builder = frame.allocateHostObject(STRING_BUILDER, StringBuilder("before"))
        frame.assign(view.parameters.single().register, Value.Known(builder, STRING_BUILDER))

        val graph = vm.newSession(view, entryFrame = frame).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known("before-thrown", STRING))), graph.outcomes)
    }

    @Test
    fun virtualMachineErrorEscapesUnchanged() {
        val emulatedFatal = StackOverflowError("emulated fatal")
        val emulated = emulatorVm(
            effect = SideEffectLevel.STRONG,
            result = { throw emulatedFatal },
        )
        val escapedEmulated = assertThrows<StackOverflowError> {
            emulated.newSession(view("catchTarget()I")).step()
        }
        assertSame(emulatedFatal, escapedEmulated)

        val reflected = reflectionVm(THROW_FATAL)
        assertSame(
            ReflectBackendFixture.fatal,
            assertThrows<StackOverflowError> { reflected.newSession(view("fatalEscape()V")).step() },
        )
    }

    private fun reflectionVm(vararg extraTargets: MethodDescriptor): VmServices = VmServices(
        CoreTester.classIndex,
        ReflectionPolicy(exactSafeMethods = setOf(THROW_NESTED, *extraTargets)),
    )

    private fun emulatorVm(
        effect: SideEffectLevel,
        result: () -> SynchronousCallResult,
    ): VmServices = VmServices(CoreTester.classIndex).also { vm ->
        vm.invocationServices.emulators.register(
            EmulatorRegistration(
                EmulatorMatcher.Exact(THROW_NESTED),
                EmulatorCapability(handlesUnknownArguments = false),
                effect,
                EmulatorHandler { result() },
            ),
        )
    }

    private fun execute(vm: VmServices, descriptor: String): ExecutionGraph = vm.newSession(view(descriptor)).run()

    private fun view(descriptor: String): MethodView = CoreTester.classIndex.requireMethodView(CLASS_NAME, descriptor)

    private companion object {
        const val CLASS_NAME = "Lhost_exception_boundary;"
        val INT: VirtualType = VirtualType.of("I")
        val STRING: VirtualType = VirtualType.of("Ljava/lang/String;")
        val STRING_BUILDER: VirtualType = VirtualType.of("Ljava/lang/StringBuilder;")
        val ILLEGAL_STATE: VirtualType = VirtualType.of("Ljava/lang/IllegalStateException;")
        val ILLEGAL_ACCESS_ERROR: VirtualType = VirtualType.of("Ljava/lang/IllegalAccessError;")
        val RUNTIME_EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/RuntimeException;")
        val EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/Exception;")
        val INTERNAL_ERROR: VirtualType = VirtualType.of("Ljava/lang/InternalError;")
        val OBJECT: VirtualType = VirtualType.OBJECT
        val THROW_NESTED = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwNested",
            "()V",
        )
        val MUTATE_THEN_THROW = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "mutateThenThrow",
            "(Ljava/lang/StringBuilder;)V",
        )
        val STRING_BUILDER_TO_STRING = MethodDescriptor(
            STRING_BUILDER,
            "toString",
            "()Ljava/lang/String;",
        )
        val THROW_FATAL = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "throwFatal",
            "()V",
        )
        val MISSING_ON_HOST = MethodDescriptor(
            VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;"),
            "missingOnHost",
            "()I",
        )
    }
}

private class HostOnlyRuntimeException(message: String) : RuntimeException(message)

private class HostOnlyNonThrowable
