package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvokeReviewTest {
    private val int = VirtualType.of("I")

    @Test
    fun mixedReturnAndLimitedAbortHasTheSameOutcomesInEitherArmOrder() {
        for (abortFirst in listOf(true, false)) {
            val graph = mixedAbortSession(abortFirst).run()
            assertEquals(
                CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)),
                graph.completion(),
                "abortFirst=$abortFirst",
            )
            assertNull(graph.trusted(), "abortFirst=$abortFirst")
            assertEquals(
                setOf(Outcome.Returned(Value.Known(7, int)), Outcome.Returned(Value.Unknown(int))),
                graph.outcomes.toSet(),
                "abortFirst=$abortFirst",
            )
            assertEquals(2, graph.outcomes.size, "abortFirst=$abortFirst")
            assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        }
    }

    @Test
    fun incompatibleClassInitStatesStayOnSeparateCallerPaths() {
        val graph = CoreTester.execute(REVIEW, "conditionalThenTrigger(I)Linvoke_review_identity;")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            Value.Unknown(VirtualType.of("Linvoke_review_identity;")),
            graph.trusted()!!.terminatingConsensus(0),
            "one concrete path initializes before the call returns; the other initializes after it",
        )
        assertEquals(2, graph.outcomes.filterIsInstance<Outcome.Returned>().size)
    }

    @Test
    fun invokeStaticNeverInheritsAParentClinit() {
        val graph = CoreTester.execute(REVIEW, "invokeInheritedClinit()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
    }

    @Test
    fun invokeSuperResolvesAnUnambiguousInterfaceDefault() {
        CoreTester.assertReturns(REVIEW, "callInterfaceDefault()I", Value.Known(11, int))
    }

    @Test
    fun onlyExactKnownIntZeroCoercesToNullDuringBinding() {
        CoreTester.assertReturns(
            REVIEW,
            "nonzeroIntPassedAsReference()Ljava/lang/Object;",
            Value.Known(5, int),
        )
        CoreTester.assertReturns(
            "Linvoke_task20;",
            "zeroBecomesNull()Ljava/lang/Object;",
            Value.Known(null, VirtualType.OBJECT),
        )
    }

    @Test
    fun arityMismatchUsesTypedOpaqueFallbackAndCollapsesArguments() {
        val graph = CoreTester.execute(REVIEW, "arityMismatchOpaque()I")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(int), graph.trusted()!!.terminatingConsensus(2))
        val arrayValue = graph.trusted()!!.terminatingConsensus(1) as Value.Known
        val array = graph.trusted()!!.terminatingHeapObject(arrayValue.raw as HeapRef) as HeapObject.TrackedArray
        assertEquals(Value.Unknown(int), array.element(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        val diagnostic = graph.addresses().flatMap { graph.nodePile(it) }.mapNotNull { it.invocationDiagnostic }.single()
        assertTrue(diagnostic.contains("argument register"), diagnostic)
    }

    @Test
    fun malformedSyntheticResumeInNestedCallerDoesNotLeaveAPhantomPath() {
        val graph = CoreTester.execute(REVIEW, "nestedMalformedResume()I")
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun malformedSyntheticHandlerInNestedCallerDoesNotLeaveAPhantomPath() {
        val outer = view(REVIEW, "nestedMalformedHandler()I")
        val innerSignature = "$REVIEW->innerWithHandler()V"
        val innerView = view(REVIEW, "innerWithHandler()V")
        val badHandler = innerView.tryBlocks.single().catches.single().handlerAddress
        val graph = ExecutionSession.of(outer, CoreTester.classIndex, Limits(), Frame.entryFrame(outer)) { address, v, index ->
            if (v.signature == innerSignature && address == badHandler) error("synthetic handler decode failure")
            OpBuilder.build(address, v, index)
        }.run()
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(2, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun resultTransportCommittedBeforeMalformedResumeRemainsVisible() {
        val graph = CoreTester.execute(REVIEW, "readCommittedResultAfterMalformedResume()I")

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(listOf(Outcome.Returned(Value.Known(7, int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun caughtSlotConsumedBeforeMalformedResumeRetainsTheCaughtReference() {
        val graph = CoreTester.execute(REVIEW, "readCommittedCaughtAfterMalformedResume()Ljava/lang/Throwable;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertEquals(VirtualType.of("Ljava/lang/NullPointerException;"), returned.type)
        assertNotNull(graph.terminatingHostObjectOverExploredPaths(returned.raw as HeapRef))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun constructorCompletionCommittedBeforeMalformedResumeRemainsVisible() {
        val graph = CoreTester.execute(REVIEW, "readCommittedConstructorAfterMalformedResume()Ljava/lang/Object;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val obj = graph.terminatingHeapObjectOverExploredPaths(returned.raw as HeapRef) as HeapObject.TrackedObject

        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        assertNull(graph.trusted())
        assertFalse(obj.uninitialized)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun opaqueReferenceResultsKeepTheirExactDeclaredArrayAndClassTypes() {
        CoreTester.assertReturns(
            REVIEW,
            "callOpaqueArrayResult()[Linvoke_review_identity;",
            Value.Unknown(VirtualType.of("[Linvoke_review_identity;")),
        )
        CoreTester.assertReturns(
            REVIEW,
            "callOpaqueClassResult()Linvoke_review_identity;",
            Value.Unknown(VirtualType.of("Linvoke_review_identity;")),
        )
    }

    @Test
    fun incompleteDispatchWorldDegradesOneCandidateWithoutSampleSpecificPolicy() {
        val index = ClassIndex(CoreTester.classManager, hierarchyComplete = false)
        val view = index.requireMethodView(REVIEW, "incompleteWorld(Linvoke_unique_base;)I")
        val graph = ExecutionSession.of(view, index, Limits(), Frame.entryFrame(view)).run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Unknown(int))), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        val invoke = graph.addresses().flatMap { graph.nodePile(it) }.single { it.dispatchCandidates.isNotEmpty() }
        assertEquals(setOf("Linvoke_unique_base;->value()I"), invoke.dispatchCandidates)
    }

    @Test
    fun knownThrowableSelectsOnlyItsFirstMatchingHandler() {
        CoreTester.assertReturns(REVIEW, "nestedTwoHandlers()I", Value.Known(1, int))
    }

    @Test
    fun normalConstructorAbortFallbackClearsOnlyTheExactReceiverId() {
        val graph = CoreTester.execute(
            REVIEW,
            "initializeOneOfTwoThroughAbortFallback()Linvoke_abort_ctor;",
            limits = Limits(maxAddressVisits = 3, maxSteps = 100),
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)), graph.completion())
        assertNull(graph.trusted())
        val receiver = graph.terminatingConsensusOverExploredPaths(0) as Value.Known
        assertEquals(
            receiver,
            graph.terminatingConsensusOverExploredPaths(1),
            "the receiver alias keeps the exact HeapRef",
        )
        val initialized = graph.terminatingHeapObjectOverExploredPaths(receiver.raw as HeapRef) as HeapObject.TrackedObject
        assertFalse(initialized.uninitialized, "normal opaque fallback completes this constructor call")

        val unrelated = graph.terminatingConsensusOverExploredPaths(2) as Value.Known
        val unrelatedObject =
            graph.terminatingHeapObjectOverExploredPaths(unrelated.raw as HeapRef) as HeapObject.TrackedObject
        assertTrue(unrelatedObject.uninitialized, "an unrelated same-type object is not the constructor receiver")
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun ambiguousDispatchCandidatesAreOrderedCopiedAndImmutable() {
        val lookup = CoreTester.classIndex.resolveInvocation(
            MethodDescriptor(VirtualType.of("Linvoke_dispatch_base;"), "value", "()I"),
            InvokeKind.VIRTUAL,
            VirtualType.of(REVIEW),
            receiverType = null,
        ) as InvocationLookup.Ambiguous
        assertEquals(
            listOf("Linvoke_dispatch_base;->value()I", "Linvoke_dispatch_child;->value()I"),
            lookup.candidates.toList(),
        )
        assertFalse(lookup.candidates.javaClass.name.contains("LinkedHashMap"), lookup.candidates.javaClass.name)
        @Suppress("UNCHECKED_CAST")
        val mutable = lookup.candidates as MutableSet<String>
        assertThrows(UnsupportedOperationException::class.java) { mutable.add("Lleak;->value()I") }
    }

    private fun mixedAbortSession(abortFirst: Boolean): ExecutionSession {
        val caller = view(REVIEW, "abortArmDeclaredFirst(I)I")
        val calleeSignature = "Linvoke_review_callee;->loopOrReturnDeclaredFirst(I)I"
        val callee = view("Linvoke_review_callee;", "loopOrReturnDeclaredFirst(I)I")
        val returnAddress = callee.addresses.first { callee.opcodeAt(it).name.startsWith("const") }
        val loopAddress = callee.addresses.first { callee.opcodeAt(it).name.startsWith("goto") }
        return ExecutionSession.of(
            caller,
            CoreTester.classIndex,
            Limits(maxAddressVisits = 3, maxSteps = 200),
            Frame.entryFrame(caller),
        ) { address, v, index ->
            if (v.signature == calleeSignature && address == 0) {
                val transitions = if (abortFirst) {
                    listOf(Transition.Jump(loopAddress), Transition.Jump(returnAddress))
                } else {
                    listOf(Transition.Jump(returnAddress), Transition.Jump(loopAddress))
                }
                object : Op(address) {
                    override val name = "review-branch"
                    override fun execute(frame: Frame) = transitions.asOpExecution()
                }
            } else {
                OpBuilder.build(address, v, index)
            }
        }
    }

    private fun view(owner: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(owner, descriptor)

    private companion object {
        const val REVIEW = "Linvoke_review;"
    }
}
