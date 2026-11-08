package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

/** JLS 12.4.2 steps 5, 7, and 11-12 plus ART ThrowEarlierClassFailure behavior. */
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
            it.view.signature == "$CLASS_NAME->newInstance()I" && it.outcome is Outcome.Returned
        }
        val frame = requireNotNull(returning.endingFrame)
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
        val graph = CoreTester.execute(CLASS_NAME, "writesBeforeFailureRemainVisible()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
        assertEquals(SideEffectLevel.WEAK, graph.addresses().maxOf(graph::sideEffectLevel))
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
        val target = MethodDescriptor(
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
    fun initializerThrowTransformFailureAbortsOnlyThatExitWithoutCommittingFailure() {
        // The fixture's unknown branch gives one returning initializer exit and one exact NPE exit.
        val failure = IllegalStateException("injected throwable model failure")
        val session = transformFailureSession(failure)

        val graph = session.run()

        assertEquals(
            setOf(
                Outcome.Returned(Value.Known(7, INT)),
                Outcome.Aborted(AbortReason.HOST_FAILURE),
            ),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertTrue(graph.outcomes.none { outcome ->
            val type = (outcome as? Outcome.Threw)?.exception?.type
            type == EXCEPTION_IN_INITIALIZER_ERROR || type == NO_CLASS_DEF_FOUND_ERROR
        })

        val nodes = allNodes(graph.nodePile(0).single { it.parent == null })
        assertSame(failure, nodes.single { it.hostFailure != null }.hostFailure)
        val failedTransform = nodes.single { node ->
            node.view.signature == "$TRANSFORM_BOUNDARY-><clinit>()V" && node.outcome is Outcome.Threw
        }
        assertEquals(
            ClassInitState.INITIALIZING,
            requireNotNull(failedTransform.endingFrame).initState(VirtualType.of(TRANSFORM_BOUNDARY)),
        )
    }

    @Test
    fun virtualMachineErrorDuringInitializerThrowTransformEscapesUnchanged() {
        val fatal = StackOverflowError("injected fatal throwable model failure")

        val thrown = assertThrows<StackOverflowError> { transformFailureSession(fatal).run() }

        assertSame(fatal, thrown)
    }

    @Test
    fun hostOnlyErrorSkipsTheEarlierInitializerWrapperHandler() {
        val target = MethodDescriptor(
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

        assertFalse(CoreTester.classIndex.throwableHierarchy.facts(runtimeType).descriptorPresent)
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
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
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
        val graph = CoreTester.execute(CLASS_NAME, method)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
    }

    private fun transformFailureSession(failure: Throwable): ExecutionSession {
        val delegate = CoreTester.classIndex.throwableHierarchy
        var wrapperValidations = 0
        val hierarchy = object : ThrowableHierarchy {
            override fun facts(type: VirtualType): ThrowableTypeFacts = delegate.facts(type)

            override fun relation(candidate: VirtualType, ancestor: VirtualType): TypeRelation {
                if (candidate == EXCEPTION_IN_INITIALIZER_ERROR && ancestor == THROWABLE) {
                    wrapperValidations++
                    // The first two validate the outward path. The third must run before Failed is committed.
                    if (wrapperValidations == 3) throw failure
                }
                return delegate.relation(candidate, ancestor)
            }
        }
        val view = view("transformFailureBoundary()I")
        return ExecutionSession(
            methodView = view,
            vmServices = CoreTester.vmServices,
            limits = Limits(),
            entryFrame = Frame.entryFrame(view),
            throwableHierarchy = hierarchy,
        )
    }

    private fun allNodes(root: ExecutionNode): List<ExecutionNode> = buildList {
        fun visit(node: ExecutionNode) {
            add(node)
            node.children.forEach(::visit)
        }
        visit(root)
    }

    private fun view(method: String): MethodView = CoreTester.classIndex.requireMethodView(CLASS_NAME, method)

    private companion object {
        const val CLASS_NAME = "Lclass_init_failure/triggers;"
        const val TRANSFORM_BOUNDARY = "Lclass_init_failure/transform_boundary;"
        const val ESCAPES = "Lclass_init_failure/escapes;"
        val INT: VirtualType = VirtualType.of("I")
        val THROWABLE: VirtualType = VirtualType.of("Ljava/lang/Throwable;")
        val EXCEPTION_IN_INITIALIZER_ERROR: VirtualType =
            VirtualType.of("Ljava/lang/ExceptionInInitializerError;")
        val NO_CLASS_DEF_FOUND_ERROR: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
    }
}

private class HostOnlyClassInitError(message: String) : Error(message)
