package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * JLS 12.4.2 steps 5, 7, and 11-12 plus ART ThrowEarlierClassFailure behavior. A fixture that counts initializer
 * attempts in a non-final static runs as a fresh process, where the count starts at 0.
 */
class ClassInitFailureBehaviorTest {
    @TestFactory
    fun firstAndLaterFailureUseTheRequiredProtocolForEveryTrigger(): List<DynamicTest> =
        listOf("newInstance", "sget", "sput", "invokeStatic", "classForName", "reflectiveStatic").map { trigger ->
            DynamicTest.dynamicTest("EX-06/08 $trigger wraps first failure and gives later use a fresh NCDFE") {
                val graph = CoreTester.execute(CLASS_NAME, "$trigger()I")

                assertEquals(CompletionStatus.Complete, graph.completion())
                assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
            }
        }

    /**
     * ART `ClassLinker::ThrowEarlierClassFailure` (art/runtime/class_linker.cc, AOSP main at
     * 6484611fd45e69db9f33f98bfd6864014b030ecf) throws `NoClassDefFoundError(c->PrettyDescriptor())` wrapping the
     * stored error, and `PrettyDescriptor` of `Lclass_init_failure/non_error;` is `class_init_failure.non_error`.
     * Every trigger kind reaches the same message.
     */
    @TestFactory
    fun aLaterTriggerOfAFailedClassThrowsNoClassDefFoundErrorNamedByItsPrettyDescriptor(): List<DynamicTest> =
        listOf("newInstance", "sget", "invokeStatic", "classForName", "reflectiveStatic").map { trigger ->
            DynamicTest.dynamicTest(trigger) {
                CoreTester.testThrows(
                    ESCAPES,
                    "$trigger()V",
                    exceptionType = NO_CLASS_DEF_FOUND_ERROR.descriptor,
                    message = "class_init_failure.non_error",
                )
            }
        }

    @Test
    fun aSuperclassFailureThrowsNoClassDefFoundErrorNamedByTheFailedSuperclass() {
        // ART `ClassLinker::InitializeClass` initializes the superclass first; an erroneous superclass throws
        // ThrowEarlierClassFailure for that superclass, so the message names it, not the requested subclass.
        CoreTester.testThrows(
            ESCAPES,
            "superclassFailure()V",
            exceptionType = NO_CLASS_DEF_FOUND_ERROR.descriptor,
            message = "class_init_failure.grandparent",
        )
    }

    @Test
    fun firstErrorKeepsItsIdentityAndLaterFailureUsesItAsCause() {
        assertReturnsOne("errorIdentity()I")
    }

    @Test
    fun freshClassInitThrowablesAreMaterializedOnceEach() {
        // Oracle: the fixture throws one source NPE in <clinit>, which JLS 12.4.2 step 11 wraps in one
        // ExceptionInInitializerError, and the later use raises one fresh NoClassDefFoundError (ART
        // ThrowEarlierClassFailure). The returning path's heap holds exactly those three, in allocation order.
        val graph = VmServices(CoreTester.classIndex).newSession(view("newInstance()I")).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
        val returning = graph.addresses().flatMap(graph::nodePile).single {
            it.view.signature.toString() == "$CLASS_NAME->newInstance()I" && it.outcome is Outcome.Returned
        }
        val frame = returning.terminalFrame
        val throwableTypes = heapRefsOf(frame).mapNotNull { ref ->
            (frame.heapObject(ref) as? HeapObject.GuestThrowable)?.type?.descriptor
        }
        assertEquals(
            listOf(
                "Ljava/lang/NullPointerException;",
                "Ljava/lang/ExceptionInInitializerError;",
                "Ljava/lang/NoClassDefFoundError;",
            ),
            throwableTypes,
        )
    }

    @Test
    fun unknownThrowableForksAtErrorWithoutDroppingEitherPartition() {
        val graph = CoreTester.execute(CLASS_NAME, "unknownSplit()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            setOf(Value.Known(1, INT), Value.Known(2, INT)),
            graph.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull(Outcome.Returned::value).toSet(),
        )
    }

    @Test
    fun grandparentFailureMarksAllDependentsAndRunsNoDescendantInitializer() {
        assertReturnsOne("grandparentFailure()I")
    }

    @Test
    fun sequentialDependencyFailuresUseTheNearestFailedSuperclassAsCause() {
        // Selecting the first failed ancestor skips the parent's stored NCDFE and makes the witness return 0.
        assertReturnsOne("sequentialDependencyFailureChain()I")
    }

    @Test
    fun anInitializerLocalCatchCompletesAndDoesNotRerun() {
        assertReturnsOne("initializerLocalCatch()I")
    }

    @Test
    fun separateLaterTriggersAllocateDistinctErrorsWithTheSameStoredCause() {
        assertReturnsOne("freshLaterTriggers()I")
    }

    @Test
    fun oneLaterTriggerSharesOneErrorAcrossAmbiguousHandlerFanout() {
        val graph = CoreTester.execute(CLASS_NAME, "fanoutLaterTrigger()V")
        val thrown = graph.outcomes.filterIsInstance<Outcome.Threw>().map(Outcome.Threw::exception)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, graph.outcomes.size)
        assertEquals(2, thrown.size)
        assertEquals(setOf(VirtualType.of("Ljava/lang/NoClassDefFoundError;")), thrown.mapNotNull(Value::type).toSet())
        assertEquals(thrown.first(), thrown.last())
    }

    @Test
    fun hostSelectionKeepsAStoredFailureCauseAfterTheRegisterRootIsCleared() {
        val graph = CoreTester.execute(CLASS_NAME, "hostBoundaryKeepsStoredCause(I)Ljava/lang/Throwable;")
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull(Outcome.Returned::value)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, returned.size)
        assertEquals(returned.first(), returned.last())
        assertEquals(VirtualType.of("Ljava/lang/ExceptionInInitializerError;"), returned.first().type)
    }

    @Test
    fun staticWritesBeforeFailureRemainVisibleToTheCatchPath() {
        val graph = CoreTester.execute(CLASS_NAME, "writesBeforeFailureRemainVisible()I", entryClassState = FRESH)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
        assertEquals(SideEffectLevel.WEAK, graph.addresses().maxOf(graph.explored::sideEffectLevel))
    }

    @Test
    fun initializerLimitStaysPartialAndDoesNotResumeTheTrigger() {
        val view = view("limitAbort()I")
        val graph = VmServices(CoreTester.classIndex).newSession(
            view,
            Limits(maxAddressVisits = 2),
        ).run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertEquals(emptyList<Outcome.Threw>(), graph.outcomes.filterIsInstance<Outcome.Threw>())
        assertEquals(emptyList<Outcome.Returned>(), graph.outcomes.filterIsInstance<Outcome.Returned>())
    }

    @Test
    fun initializerHostFailureStaysPartialAndDoesNotBecomeGuestFailure() {
        val target = MethodSignature(
            VirtualType.of("Lclass_init_failure/service;"),
            "fail",
            "()V",
        )
        val vm = VmServices(CoreTester.classIndex)
        vm.invocationServices.emulators.register(
            EmulatorRegistration(
                EmulatorMatcher.Exact(target),
                EmulatorCapability(handlesUnknownArguments = false),
                SideEffectLevel.STRONG,
                EmulatorHandler { SynchronousCallResult.InfrastructureFailure(IllegalStateException("host")) },
            ),
        )

        val graph = vm.newSession(view("hostAbort()I")).run()

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertEquals(emptyList<Outcome.Threw>(), graph.outcomes.filterIsInstance<Outcome.Threw>())
        assertEquals(emptyList<Outcome.Returned>(), graph.outcomes.filterIsInstance<Outcome.Returned>())
    }

    @Test
    fun hostOnlyErrorSkipsTheEarlierInitializerWrapperHandler() {
        val target = MethodSignature(
            VirtualType.of("Lclass_init_failure/service;"),
            "fail",
            "()V",
        )
        val thrown = HostOnlyClassInitError("host-only error")
        val runtimeType = virtualTypeOfHostClass(thrown.javaClass)
        val vm = VmServices(CoreTester.classIndex)
        vm.invocationServices.emulators.register(
            EmulatorRegistration(
                EmulatorMatcher.Exact(target),
                EmulatorCapability(handlesUnknownArguments = false),
                SideEffectLevel.STRONG,
                EmulatorHandler {
                    SynchronousCallResult.TargetThrew(HostThrowable(runtimeType, thrown))
                },
            ),
        )

        assertFalse(CoreTester.classIndex.currentSnapshot.hierarchy.facts(runtimeType).descriptorPresent)
        val graph = vm.newSession(view("hostOnlyError()I")).run()
        val clauses = graph.methodView.tryBlocks.flatMap(TryBlock::catches)
        val wrapperHandler = clauses.single {
            it.exceptionType == VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        }.handlerAddress
        val errorHandler = clauses.single {
            it.exceptionType == VirtualType.of("Ljava/lang/Error;")
        }.handlerAddress

        assertEquals(
            listOf(
                VirtualType.of("Ljava/lang/ExceptionInInitializerError;"),
                VirtualType.of("Ljava/lang/Error;"),
            ),
            clauses.map(CatchClause::exceptionType),
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
        assertFalse(graph.wasReached(wrapperHandler))
        assertTrue(graph.wasReached(errorHandler))
    }

    @Test
    fun unsupportedInitializerStaysPartialAndDoesNotBecomeGuestFailure() {
        val graph = CoreTester.execute("Linvoke_review;", "readAbortedClinit()I")

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.UNSUPPORTED_OP)), graph.completion())
        assertEquals(emptyList<Outcome.Threw>(), graph.outcomes.filterIsInstance<Outcome.Threw>())
        assertEquals(emptyList<Outcome.Returned>(), graph.outcomes.filterIsInstance<Outcome.Returned>())
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun branchLocalFailuresKeepDistinctStoredOutwardRefsThroughLaterCauseChains() {
        val graph = CoreTester.execute(CLASS_NAME, "branchLocal(I)Ljava/lang/Throwable;")
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().mapNotNull(Outcome.Returned::value)

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, returned.size)
        assertNotEquals(returned.first(), returned.last())
        assertEquals(
            setOf(VirtualType.of("Ljava/lang/ExceptionInInitializerError;")),
            returned.mapNotNull(Value::type).toSet(),
        )
    }

    private fun assertReturnsOne(method: String) {
        val graph = CoreTester.execute(CLASS_NAME, method, entryClassState = FRESH)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
    }

    private fun view(method: String): MethodView = CoreTester.classIndex.requireMethodView(CLASS_NAME, method)

    private companion object {
        const val CLASS_NAME = "Lclass_init_failure/triggers;"
        const val TRANSFORM_BOUNDARY = "Lclass_init_failure/transform_boundary;"
        const val ESCAPES = "Lclass_init_failure/escapes;"
        val FRESH = EntryClassState.FRESH_PROCESS
        val INT: VirtualType = VirtualType.of("I")
        val THROWABLE: VirtualType = VirtualType.of("Ljava/lang/Throwable;")
        val EXCEPTION_IN_INITIALIZER_ERROR: VirtualType =
            VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val NO_CLASS_DEF_FOUND_ERROR: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
    }
}

private class HostOnlyClassInitError(message: String) : Error(message)
