package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExceptionPropagationTest {
    private val int = VirtualType.of("I")

    @Test
    fun localOneCallerAndThreeFrameHandlersRunAtTheCorrectBoundary() {
        assertReturns("localCatch()I", 1)
        assertReturns("oneCaller()I", 2)
        assertReturns("outerThreeFrames()I", 3)
    }

    @Test
    fun intermediateHandlerStopsTheUnwind() {
        assertReturns("outerIntermediateStops()I", 4)
    }

    @Test
    fun callerRegistersAndAddressVisitsSurviveCalleeThrowTransport() {
        assertReturns("callerRegisterSurvives()I", 7)

        val graph = execute("callerAddressVisitsSurvive()I", Limits(maxAddressVisits = 1))
        assertEquals(
            CompletionStatus.Partial(setOf(AbortReason.ADDRESS_LIMIT)),
            graph.completion(),
            "the second visit must use the suspended caller's visit root",
        )
    }

    @Test
    fun calleeHeapAndStaticWritesBeforeThrowReachTheMatchingHandler() {
        assertReturns("callerSeesPreThrowWrites()I", 11)
    }

    @Test
    fun rethrowPreservesTheConcreteReference() {
        val graph = execute("outerReturnsRethrown()Ljava/lang/Throwable;")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val owner = VirtualType.of(OWNER)
        val saved = ClassIndex.fieldDescriptorOf(owner, "saved", "Ljava/lang/Throwable;")

        assertEquals(returned, graph.terminatingStaticFieldConsensusOverExploredPaths(owner, saved))
    }

    @Test
    fun callerLookupUsesInvokeAddressInsteadOfResumeAddress() {
        assertReturns("invokeOnlyRange()I", 8)
        val resumeOnly = execute("resumeOnlyRange()I")
        assertEquals(1, resumeOnly.outcomes.filterIsInstance<Outcome.Threw>().size)
        assertEquals(CompletionStatus.Complete, resumeOnly.completion())
    }

    @Test
    fun everyReturnAndThrowEndingProducesOneCallerResult() {
        val mixed = execute("callerKeepsEveryEnding(I)I")
        assertEquals(
            listOf(Value.Known(1, int), Value.Known(2, int)),
            mixed.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }.sortedBy { (it as Value.Known).raw as Int },
        )

        val returned = execute("callerKeepsBothReturns(I)I")
        assertEquals(
            listOf(Value.Known(1, int), Value.Known(2, int)),
            returned.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }.sortedBy { (it as Value.Known).raw as Int },
        )
    }

    @Test
    fun sameConcreteRefWithIncompatibleVisibleStateUsesTwoMultiFramePartitions() {
        val view = view("callerObservesSameRefStates(ILexception_propagation_test;Ljava/lang/Throwable;)I")
        val frame = Frame.entryFrame(view)
        val host = NullPointerException("same ref")
        val npe = VirtualType.of("Ljava/lang/NullPointerException;")
        val throwable = frame.allocateThrowable(npe, host)
        val owner = VirtualType.of(OWNER)
        val valueField = ClassIndex.fieldDescriptorOf(owner, "value", "I")
        val witness = frame.allocateObject(owner, mapOf(valueField to Value.Known(0, int)))
        frame.assign(view.parameters[0].register, Value.Unknown(int))
        frame.assign(view.parameters[1].register, witness)
        frame.assign(view.parameters[2].register, throwable)
        val callee = view("sameRefWithVisibleState(ILexception_propagation_test;Ljava/lang/Throwable;)V")
        val throwAddress = callee.addresses.single { callee.opcodeAt(it).name == "throw" }
        val calleeSignature = callee.signature

        val session = ExecutionSession.of(view, CoreTester.classIndex, Limits(), frame) { address, currentView, index ->
            if (currentView.signature == calleeSignature && address == throwAddress) {
                val throwableRegister = currentView.parameters[2].register
                object : Op(address) {
                    override val name = "two-equivalent-same-ref-throws"

                    override fun execute(frame: Frame): OpExecution {
                        val evidence = ThrowEvidence.Existing(frame.readFrameValue(throwableRegister))
                        return OpExecution.of(OpArm.Throw(evidence = evidence), OpArm.Throw(evidence = evidence))
                    }
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }

        val graph = session.run()
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        val saved = ClassIndex.fieldDescriptorOf(owner, "saved", "Ljava/lang/Throwable;")

        assertEquals(
            listOf(Value.Known(2, int), Value.Known(2, int), Value.Known(4, int), Value.Known(4, int)),
            returned.sortedBy { (it as Value.Known).raw as Int },
            "the handler must see each path's object and static writes",
        )
        assertEquals(throwable, graph.terminatingStaticFieldConsensusOverExploredPaths(owner, saved))
        val metrics = session.callPropagationMetrics()
        assertEquals(2, metrics.sourceThrowPartitions)
        assertEquals(4, metrics.sourceThrowExits)
        assertEquals(2, metrics.sourcePartitionMerges)
        assertEquals(2, metrics.handlerResolutions)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun twoSameClassConcreteRefsNeverJoin() {
        val graph = execute("callerReturnsDistinctNpes(I)Ljava/lang/Throwable;")
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { (it.value as Value.Known).raw }

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, graph.outcomes.size)
        assertEquals(2, returned.size)
        assertEquals(2, returned.distinct().size)
    }

    @Test
    fun sameConcreteRefWithDifferentRouteConstraintsMergesAsExactRuntimeSubject() {
        val caller = CoreTester.classIndex.requireMethodView("Lexception_handler_test;",
        "callerReturnsCaught()Ljava/lang/Throwable;",)
        val frame = Frame.entryFrame(caller)
        val npe = VirtualType.of("Ljava/lang/NullPointerException;")
        val runtime = VirtualType.of("Ljava/lang/RuntimeException;")
        val value = frame.allocateThrowable(npe, NullPointerException("same ref constraints"))
        val exact = ThrowableDomain.exact(npe, CoreTester.classIndex.throwableHierarchy)
        val narrowed = requireNotNull(
            exact.narrowUpperBound(runtime, ResolverMetricsCounter()),
        )
        val calleeSignature = "Linvoke_review_callee;->throwsNpe()V"
        val session = ExecutionSession.of(caller, CoreTester.classIndex, Limits(), frame) { address, currentView, index ->
            if (currentView.signature == calleeSignature && address == 0) {
                object : Op(address) {
                    override val name = "same-ref-different-constraints"

                    override fun execute(frame: Frame): OpExecution = OpExecution.of(
                        OpArm.Throw(evidence = ThrowEvidence.Existing(FrameValue.refined(value, exact))),
                        OpArm.Throw(evidence = ThrowEvidence.Existing(FrameValue.refined(value, narrowed))),
                    )
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }

        val graph = session.run()
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
        val metrics = session.callPropagationMetrics()

        assertEquals(listOf(value, value), returned)
        assertEquals(1, metrics.sourceThrowPartitions)
        assertEquals(2, metrics.sourceThrowExits)
        assertEquals(1, metrics.partitionMerges)
        assertEquals(1, metrics.sourcePartitionMerges)
        assertEquals(1, metrics.handlerResolutions)
    }

    @Test
    fun sameConcreteRefWithOpposingMissingCatchResidualsStaysPartitionedAtCaller() {
        val caller = view("callerRoutesOpposingMissingCatchResiduals(Ljava/lang/Throwable;)Ljava/lang/Throwable;")
        val frame = Frame.entryFrame(caller)
        val npe = VirtualType.of("Ljava/lang/NullPointerException;")
        val throwable = frame.allocateThrowable(npe, NullPointerException("opposing residuals"))
        frame.assign(caller.parameters.single().register, throwable)
        val session = CoreTester.vmServices.newSession(caller, entryFrame = frame)

        val graph = session.run()
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().single().value
        val escaped = graph.outcomes.filterIsInstance<Outcome.Threw>().single().exception
        val handler = graph.methodView.tryBlocks.single().catches.single().handlerAddress

        assertEquals(1, graph.nodePile(handler).size)
        assertEquals(throwable, returned)
        assertEquals(throwable, escaped)
        assertEquals(2, graph.outcomes.size)
        val metrics = session.callPropagationMetrics()
        assertEquals(2, metrics.sourceThrowPartitions)
        assertEquals(2, metrics.sourceThrowExits)
        assertEquals(2, metrics.sourcePartitionMerges)
        assertEquals(2, metrics.handlerResolutions)
        assertEquals(1, metrics.handlerRoutes)
        assertEquals(2, metrics.sourceDomainHashReads)
        assertEquals(0, metrics.sourceDomainHashEntryVisits)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun loosePublicValueTypeTagSurvivesCalleeEscape() {
        val caller = view("callerEscapesLooseTag(Ljava/lang/Throwable;)V")
        val frame = Frame.entryFrame(caller)
        val npe = VirtualType.of("Ljava/lang/NullPointerException;")
        val allocated = frame.allocateThrowable(npe, NullPointerException("loose tag"))
        val loose = Value.Known(allocated.raw, THROWABLE_TYPE)
        frame.assign(caller.parameters.single().register, loose)

        val graph = CoreTester.vmServices.newSession(caller, entryFrame = frame).run()

        assertEquals(listOf(Outcome.Threw(loose)), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun distinctSymbolicResidualDomainsTargetingOneHandlerRemainSeparate() {
        val caller = view("callerCatchesThrowable()I")
        val calleeSignature = "$OWNER->throwNpe()V"
        val throwable = VirtualType.of("Ljava/lang/Throwable;")
        val exception = VirtualType.of("Ljava/lang/Exception;")
        val runtime = VirtualType.of("Ljava/lang/RuntimeException;")
        val first = requireNotNull(ThrowableDomain.any().exclude(exception, ResolverMetricsCounter()))
        val second = requireNotNull(ThrowableDomain.any().exclude(runtime, ResolverMetricsCounter()))
        val session = ExecutionSession.of(
            caller,
            CoreTester.classIndex,
            Limits(),
            Frame.entryFrame(caller),
        ) { address, currentView, index ->
            if (currentView.signature == calleeSignature && address == 0) {
                object : Op(address) {
                    override val name = "distinct-symbolic-residuals"

                    override fun execute(frame: Frame): OpExecution = OpExecution.of(
                        OpArm.Throw(
                            evidence = ThrowEvidence.Existing(
                                FrameValue.refined(Value.Unknown(throwable), first),
                            ),
                        ),
                        OpArm.Throw(
                            evidence = ThrowEvidence.Existing(
                                FrameValue.refined(Value.Unknown(throwable), second),
                            ),
                        ),
                    )
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }

        val graph = session.run()
        val metrics = session.callPropagationMetrics()

        assertEquals(listOf(Outcome.Returned(Value.Known(1, int)), Outcome.Returned(Value.Known(1, int))), graph.outcomes)
        assertEquals(2, metrics.sourceThrowPartitions)
        assertEquals(2, metrics.partitionMerges)
        assertEquals(2, metrics.sourcePartitionMerges)
        assertEquals(2, metrics.handlerResolutions)
    }

    @Test
    fun sameDomainTopAndUnknownObjectJoinIntoOneSoundSymbolicPartition() {
        val caller = view("callerReturnsSymbolicCaught()Ljava/lang/Throwable;")
        val calleeSignature = "$OWNER->throwNpe()V"
        val domain = ThrowableDomain.any()
        val session = ExecutionSession.of(
            caller,
            CoreTester.classIndex,
            Limits(),
            Frame.entryFrame(caller),
        ) { address, currentView, index ->
            if (currentView.signature == calleeSignature && address == 0) {
                object : Op(address) {
                    override val name = "same-domain-different-symbolic-values"

                    override fun execute(frame: Frame): OpExecution = OpExecution.of(
                        OpArm.Throw(
                            evidence = ThrowEvidence.Existing(FrameValue.refined(Value.Top, domain)),
                        ),
                        OpArm.Throw(
                            evidence = ThrowEvidence.Existing(
                                FrameValue.refined(Value.Unknown(VirtualType.OBJECT), domain),
                            ),
                        ),
                    )
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }

        val graph = session.run()
        val metrics = session.callPropagationMetrics()

        assertEquals(listOf(Outcome.Returned(Value.Top), Outcome.Returned(Value.Top)), graph.outcomes)
        assertEquals(1, metrics.sourceThrowPartitions)
        assertEquals(2, metrics.sourceThrowExits)
        assertEquals(1, metrics.sourcePartitionMerges)
        assertEquals(1, metrics.handlerResolutions)
    }

    @Test
    fun concurrentRecursiveOutcomesPairEachSentinelWithItsOwnThrowableRef() {
        val session = CoreTester.vmServices.newSession(view("recursionMergeWitness(I)Ljava/lang/Throwable;"))
        val graph = session.run()
        val owner = VirtualType.of(OWNER)
        val state = ClassIndex.fieldDescriptorOf(owner, "state", "I")
        val returns = allNodes(graph).filter {
            it.activation.view.signature == "$OWNER->recursionMergeWitness(I)Ljava/lang/Throwable;" &&
                it.outcome is Outcome.Returned
        }
        val pairs = returns.associate { node ->
            val ending = requireNotNull(node.endingFrame)
            val sentinel = (ending.getStaticField(owner, state) as Value.Known).raw as Int
            val ref = ((node.outcome as Outcome.Returned).value as Value.Known).raw as HeapRef
            sentinel to ref
        }

        assertEquals(setOf(1, 2), pairs.keys)
        assertEquals(2, pairs.values.toSet().size)
        assertEquals(2, graph.outcomes.size)
        assertEquals(6, session.callPropagationMetrics().partitionMerges)
        assertEquals(2, session.callPropagationMetrics().sourceThrowPartitions)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun oneCalleeProducesTwoReturnsAndTwoDistinctThrowsExactlyOnce() {
        val graph = execute("callerKeepsFourEndings(II)I")
        val values = graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }
            .sortedBy { (it as Value.Known).raw as Int }

        assertEquals(
            listOf(Value.Known(1, int), Value.Known(2, int), Value.Known(3, int), Value.Known(4, int)),
            values,
        )
        assertEquals(4, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun executedStrongEffectSurvivesThreeFrameUnwind() {
        val graph = execute("effectOuter([I)I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(setOf(Value.Known(3, int)), graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value }.toSet())
    }

    @Test
    fun equivalentSymbolicExitsMergeAndResolveOnceBeforeHandlerFanout() {
        val handlerCount = 16
        val exitCount = 8
        val caller = view("manySymbolicHandlers()I")
        val calleeSignature = "$OWNER->throwNpe()V"
        val session = ExecutionSession.of(
            caller,
            CoreTester.classIndex,
            Limits(),
            Frame.entryFrame(caller),
        ) { address, currentView, index ->
            if (currentView.signature == calleeSignature && address == 0) {
                object : Op(address) {
                    override val name = "equivalent-symbolic-throws"

                    override fun execute(frame: Frame): OpExecution = OpExecution.of(
                        List(exitCount) {
                            OpArm.Throw(evidence = ThrowEvidence.Existing(Value.Unknown(THROWABLE_TYPE)))
                        },
                    )
                }
            } else {
                OpBuilder.build(address, currentView, index)
            }
        }

        val graph = session.run()
        val metrics = session.callPropagationMetrics()

        assertEquals(1, metrics.sourceThrowPartitions)
        assertEquals(exitCount, metrics.sourceThrowExits)
        assertEquals(1, metrics.partitionMerges)
        assertEquals(1, metrics.sourcePartitionMerges)
        assertEquals(1, metrics.handlerResolutions)
        assertEquals(handlerCount, metrics.handlerClausesVisited)
        assertEquals(handlerCount * 2, metrics.handlerRelationRequests)
        assertEquals(handlerCount, metrics.handlerRoutes)
        assertEquals(exitCount, metrics.sourceDomainHashReads)
        assertEquals(0, metrics.sourceDomainHashEntryVisits)
        assertEquals(exitCount * handlerCount, graph.outcomes.filterIsInstance<Outcome.Returned>().size)
        assertEquals(exitCount, graph.outcomes.filterIsInstance<Outcome.Threw>().size)
    }

    @Test
    fun threeFrameGuestPropagationStaysCompleteWithoutInventingEffects() {
        val view = view("outerThreeFrames()I")
        val session = CoreTester.vmServices.newSession(view)
        val graph = session.run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.NONE, graph.methodSideEffectLevelOverExploredPaths())
        val metrics = session.callPropagationMetrics()
        assertEquals(0, metrics.retainedFrames)
        assertEquals(0, metrics.retainedCollectionCapacity)
        assertEquals(2, metrics.partitionMerges)
        assertEquals(2, metrics.sourceThrowPartitions)
        assertEquals(2, metrics.handlerResolutions)
        assertEquals(0, metrics.pendingRoutingCommands)
        assertEquals(2, metrics.routingCommands)
        assertEquals(
            9,
            metrics.schedulerWork,
            "seven real Smali ops plus two caller-routing commands must exhaust the work count",
        )
    }

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val out = mutableListOf<ExecutionNode>()
        fun walk(node: ExecutionNode) {
            out.add(node)
            node.children.forEach(::walk)
        }
        walk(graph.nodePile(0).first())
        return out
    }

    private fun assertReturns(descriptor: String, value: Int) {
        val graph = execute(descriptor)
        assertEquals(listOf(Outcome.Returned(Value.Known(value, int))), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    private fun execute(descriptor: String, limits: Limits = Limits()): ExecutionGraph =
        CoreTester.vmServices.newSession(view(descriptor), limits).run()

    private fun view(descriptor: String): MethodView = CoreTester.classIndex.requireMethodView(OWNER, descriptor)

    private companion object {
        const val OWNER = "Lexception_propagation_test;"
    }
}
