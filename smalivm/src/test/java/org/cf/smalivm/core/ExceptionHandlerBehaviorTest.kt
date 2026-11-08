package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExceptionHandlerBehaviorTest {
    @Test
    fun `EX-03 Exception before RuntimeException shadows its subtype route`() {
        val graph = execute("exceptionThenRuntime(Ljava/lang/Throwable;)I")
        val handlers = handlers(graph)
        val clauses = graph.methodView.tryBlocks.flatMap { it.catches }

        assertEquals(listOf(EXCEPTION, RUNTIME), clauses.map { it.exceptionType })
        assertTrue(
            clauses[0].handlerAddress > clauses[1].handlerAddress,
            "declaration order must differ from handler-address order",
        )
        assertTrue(graph.wasReached(handlers.getValue(EXCEPTION)))
        assertFalse(graph.wasReached(handlers.getValue(RUNTIME)))
        assertEquals(
            setOf(Outcome.Returned(Value.Known(1, INT)), Outcome.Threw(Value.Unknown(THROWABLE))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun `EX-03 RuntimeException before Exception keeps both first-match residuals`() {
        val graph = execute("runtimeThenException(Ljava/lang/Throwable;)I")
        val handlers = handlers(graph)

        assertTrue(graph.wasReached(handlers.getValue(RUNTIME)))
        assertTrue(graph.wasReached(handlers.getValue(EXCEPTION)))
        assertEquals(
            setOf(
                Outcome.Returned(Value.Known(1, INT)),
                Outcome.Returned(Value.Known(2, INT)),
                Outcome.Threw(Value.Unknown(THROWABLE)),
            ),
            graph.outcomes.toSet(),
        )
        assertEquals(3, graph.outcomes.size)
    }

    @Test
    fun `typed Throwable suppresses the escape`() {
        val graph = execute("catchThrowable(Ljava/lang/Throwable;)I")

        assertEquals(listOf(Outcome.Returned(Value.Known(3, INT))), graph.outcomes)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun `catch all consumes only the residual after typed clauses`() {
        val graph = execute("catchAll(Ljava/lang/Throwable;)I")

        assertEquals(
            setOf(
                Outcome.Returned(Value.Known(1, INT)),
                Outcome.Returned(Value.Known(2, INT)),
            ),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
        assertTrue(graph.outcomes.none { it is Outcome.Threw })
    }

    @Test
    fun `rethrow keeps the residual exclusion from the first handler table`() {
        val graph = execute("rethrowResidual(Ljava/lang/Throwable;)I")
        val runtimeHandlers = graph.methodView.tryBlocks
            .flatMap { it.catches }
            .filter { it.exceptionType == RUNTIME }
            .map { it.handlerAddress }

        assertEquals(2, runtimeHandlers.size)
        assertTrue(graph.wasReached(runtimeHandlers.first()), "the first RuntimeException partition is feasible")
        assertFalse(graph.wasReached(runtimeHandlers.last()), "the Exception residual excludes RuntimeException")
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Known(1, INT)) })
        assertEquals(2, graph.outcomes.count { it is Outcome.Threw })
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun `EX-02 non-Throwable reaches Error and not Exception`() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "nonThrowableReachesError(Ljava/lang/Object;)I",
            mapOf(1 to Value.Known(0, VirtualType.of("Ljava/lang/String;"))),
        )
        val handlers = handlers(graph)

        assertFalse(graph.wasReached(handlers.getValue(EXCEPTION)))
        assertTrue(graph.wasReached(handlers.getValue(ERROR)))
        assertEquals(listOf(Outcome.Returned(Value.Known(2, INT))), graph.outcomes)
    }

    @Test
    fun `local and caller routing expose the same first-match partitions`() {
        val local = execute("runtimeThenException(Ljava/lang/Throwable;)I")
        val caller = execute("callerRuntimeThenException(Ljava/lang/Throwable;)I")

        for (graph in listOf(local, caller)) {
            val handlers = handlers(graph)
            assertTrue(graph.wasReached(handlers.getValue(RUNTIME)))
            assertTrue(graph.wasReached(handlers.getValue(EXCEPTION)))
            assertEquals(
                setOf(
                    Outcome.Returned(Value.Known(1, INT)),
                    Outcome.Returned(Value.Known(2, INT)),
                    Outcome.Threw(Value.Unknown(THROWABLE)),
                ),
                graph.outcomes.toSet(),
            )
            assertEquals(3, graph.outcomes.size)
            assertEquals(CompletionStatus.Complete, graph.completion())
        }
    }

    @Test
    fun `broad-first caller table shadows its later subtype despite lower handler address`() {
        val graph = execute("callerExceptionThenRuntime(Ljava/lang/Throwable;)I")
        val clauses = graph.methodView.tryBlocks.flatMap { it.catches }
        val handlers = handlers(graph)

        assertEquals(listOf(EXCEPTION, RUNTIME), clauses.map { it.exceptionType })
        assertTrue(clauses[0].handlerAddress > clauses[1].handlerAddress)
        assertTrue(graph.wasReached(handlers.getValue(EXCEPTION)))
        assertFalse(graph.wasReached(handlers.getValue(RUNTIME)))
        assertEquals(
            setOf(Outcome.Returned(Value.Known(1, INT)), Outcome.Threw(Value.Unknown(THROWABLE))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun `raw host NPE runtime type beats a loose Throwable tag`() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "rawRuntimeTypeBeatsThrowableTag(Ljava/lang/Throwable;)I",
            mapOf(1 to Value.Known(NullPointerException("runtime witness"), THROWABLE)),
        )
        val handlers = handlers(graph)

        assertTrue(graph.wasReached(handlers.getValue(NPE)))
        assertFalse(graph.wasReached(handlers.getValue(THROWABLE)))
        assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes)
    }

    @Test
    fun `unknown absent declared type preserves both handler and escape`() {
        val absent = VirtualType.of("Lmissing/AbsentThrowable;")
        val graph = execute("absentDeclaredType(Lmissing/AbsentThrowable;)I")

        assertTrue(graph.wasReached(graph.methodView.tryBlocks.single().catches.single().handlerAddress))
        assertEquals(
            setOf(Outcome.Returned(Value.Known(1, INT)), Outcome.Threw(Value.Unknown(absent))),
            graph.outcomes.toSet(),
        )
        assertEquals(2, graph.outcomes.size)
    }

    @Test
    fun `EX-04 public Smali boundaries are start inclusive and end exclusive`() {
        val rows = listOf(
            Triple("boundaryStartMinusOne()I", -1, false),
            Triple("boundaryStart()I", 0, true),
            Triple("boundaryEndMinusOne()I", -1, true),
            Triple("boundaryEnd()I", 0, false),
        )

        for ((descriptor, offset, handled) in rows) {
            val graph = execute(descriptor)
            val block = graph.methodView.tryBlocks.single()
            val throwAddress = graph.methodView.addresses.single {
                graph.methodView.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW
            }
            if (descriptor.contains("Start")) {
                assertEquals(block.startAddress + offset, throwAddress, descriptor)
            } else {
                assertEquals(block.endAddress + offset, throwAddress, descriptor)
            }
            assertEquals(handled, graph.wasReached(block.catches.single().handlerAddress), descriptor)
            if (handled) {
                assertEquals(listOf(Outcome.Returned(Value.Known(1, INT))), graph.outcomes, descriptor)
            } else {
                assertEquals(1, graph.outcomes.filterIsInstance<Outcome.Threw>().size, descriptor)
            }
        }
    }

    @Test
    fun `direct move-exception and rethrow retain the broad-first residual`() {
        assertResidualSurvives("directRethrowResidual(Ljava/lang/Throwable;)I")
    }

    @Test
    fun `move-object retains the broad-first residual`() {
        assertResidualSurvives("rethrowResidual(Ljava/lang/Throwable;)I")
    }

    @Test
    fun `cast to Object retains the broad-first residual`() {
        assertResidualSurvives("castObjectResidual(Ljava/lang/Throwable;)I")
    }

    @Test
    fun `narrow cast partitions success and failure domains`() {
        val graph = execute("narrowCastResidual(Ljava/lang/Throwable;)I")
        val ioHandlers = graph.methodView.tryBlocks.flatMap { it.catches }
            .filter { it.exceptionType == VirtualType.of("Ljava/io/IOException;") }
            .map { it.handlerAddress }

        assertEquals(2, ioHandlers.size)
        assertTrue(graph.wasReached(ioHandlers.first()), "the intersected IOException domain must reach success")
        assertFalse(graph.wasReached(ioHandlers.last()), "the failure residual excludes IOException")
        assertTrue(Outcome.Returned(Value.Known(2, INT)) in graph.outcomes)
        assertFalse(Outcome.Returned(Value.Known(3, INT)) in graph.outcomes)
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Known(1, INT)) })
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Known(2, INT)) })
        assertEquals(2, graph.outcomes.count { it is Outcome.Threw })
        assertEquals(4, graph.outcomes.size)
    }

    @Test
    fun `guest argument and callee throw retain the broad-first residual`() {
        assertResidualSurvives("guestArgumentResidual(Ljava/lang/Throwable;)I")
    }

    @Test
    fun `guest return and move-result retain the broad-first residual`() {
        assertResidualSurvives("guestReturnResidual(Ljava/lang/Throwable;)I")
    }

    @Test
    fun `heap static host and opaque boundaries explicitly forget throwable refinements`() {
        for (
            descriptor in listOf(
                "arrayForgetsResidual(Ljava/lang/Throwable;)I",
                "fieldForgetsResidual(Ljava/lang/Throwable;)I",
                "staticForgetsResidual(Ljava/lang/Throwable;)I",
                "hostCallForgetsResidual(Ljava/lang/Throwable;)I",
                "opaqueCallForgetsResidual(Ljava/lang/Throwable;)I",
            )
        ) {
            val graph = execute(descriptor)
            val runtimeHandlers = graph.methodView.tryBlocks.flatMap { it.catches }
                .filter { it.exceptionType == RUNTIME }
                .map { it.handlerAddress }

            assertEquals(2, runtimeHandlers.size, descriptor)
            assertTrue(graph.wasReached(runtimeHandlers.last()), "$descriptor must forget at the store boundary")
            assertTrue(Outcome.Returned(Value.Known(2, INT)) in graph.outcomes, descriptor)
        }
    }

    @Test
    fun `public new-session entry strips hidden refinements from a reused frame`() {
        val view = view("routePublicValue(Ljava/lang/Throwable;)I")
        val parameter = view.parameters.single().register
        val value = Value.Unknown(THROWABLE)
        val residual = requireNotNull(ThrowableDomain.any().exclude(RUNTIME))
        val reused = Frame.entryFrame(view).also {
            it.assignFrameValue(parameter, FrameValue.refined(value, residual))
        }
        val plain = Frame.entryFrame(view).also { it.assign(parameter, value) }

        val reusedFirst = CoreTester.vmServices.newSession(view, entryFrame = reused).run()
        val reusedSecond = CoreTester.vmServices.newSession(view, entryFrame = reused).run()
        val publicSeed = CoreTester.vmServices.newSession(view, entryFrame = plain).run()

        assertPublicValueRoutes(reusedFirst)
        assertEquals(publicSeed.outcomes, reusedFirst.outcomes)
        assertEquals(publicSeed.outcomes, reusedSecond.outcomes)
    }

    @Test
    fun `public Value projection reassignment and rethrow widen conservatively`() {
        val view = view("routePublicValue(Ljava/lang/Throwable;)I")
        val parameter = view.parameters.single().register
        val value = Value.Unknown(THROWABLE)
        val residual = requireNotNull(ThrowableDomain.any().exclude(RUNTIME))
        val source = Frame.entryFrame(view).also {
            it.assignFrameValue(parameter, FrameValue.refined(value, residual))
        }
        val projected = requireNotNull(source.peek(parameter))
        val reassigned = Frame.entryFrame(view).also { it.assign(parameter, projected) }

        val graph = CoreTester.vmServices.newSession(view, entryFrame = reassigned).run()

        assertPublicValueRoutes(graph)
    }

    @Test
    fun `overlapping local ranges abort only the throw path as malformed input`() {
        val real = view("runtimeThenException(Ljava/lang/Throwable;)I")
        val throwAddress = real.addresses.single { real.opcodeAt(it) == com.android.tools.smali.dexlib2.Opcode.THROW }
        val handlers = real.tryBlocks.flatMap { it.catches }.map(CatchClause::handlerAddress)
        val forged = forgedView(
            "runtimeThenException(Ljava/lang/Throwable;)I",
            listOf(
                TryBlock(throwAddress, throwAddress + 1, listOf(CatchClause(RUNTIME, handlers[0])), null),
                TryBlock(throwAddress, throwAddress + 2, listOf(CatchClause(EXCEPTION, handlers[1])), null),
            ),
        )

        val graph = CoreTester.vmServices.newSession(forged).run()

        assertMalformedRouting(graph, handlers)
    }

    @Test
    fun `overlapping caller ranges abort only the throw path as malformed input`() {
        val descriptor = "callerRuntimeThenException(Ljava/lang/Throwable;)I"
        val real = view(descriptor)
        val invokeAddress = real.addresses.single { real.opcodeAt(it).name.startsWith("invoke-") }
        val handlers = real.tryBlocks.flatMap { it.catches }.map(CatchClause::handlerAddress)
        val afterInvoke = real.nextAddress(invokeAddress)
        val forged = forgedView(
            descriptor,
            listOf(
                TryBlock(invokeAddress, afterInvoke, listOf(CatchClause(RUNTIME, handlers[0])), null),
                TryBlock(invokeAddress, real.nextAddress(afterInvoke), listOf(CatchClause(EXCEPTION, handlers[1])), null),
            ),
        )

        val graph = CoreTester.vmServices.newSession(forged).run()

        assertMalformedRouting(graph, handlers)
    }

    @Test
    fun `one malformed caller handler rejects sibling handlers and escape atomically`() {
        val descriptor = "callerRuntimeThenException(Ljava/lang/Throwable;)I"
        val real = view(descriptor)
        val invokeAddress = real.addresses.single { real.opcodeAt(it).name.startsWith("invoke-") }
        val validHandler = real.tryBlocks.flatMap { it.catches }
            .single { it.exceptionType == RUNTIME }
            .handlerAddress
        val forged = forgedView(
            descriptor,
            listOf(
                TryBlock(
                    invokeAddress,
                    real.nextAddress(invokeAddress),
                    listOf(CatchClause(RUNTIME, validHandler), CatchClause(EXCEPTION, real.endAddress)),
                    null,
                ),
            ),
        )

        val graph = CoreTester.vmServices.newSession(forged).run()

        assertMalformedRouting(graph, listOf(validHandler))
    }

    @Test
    fun `source partitioning preserves malformed caller group atomicity for every equivalent exit`() {
        val exitCount = 4
        val descriptor = "callerRuntimeThenException(Ljava/lang/Throwable;)I"
        val real = view(descriptor)
        val invokeAddress = real.addresses.single { real.opcodeAt(it).name.startsWith("invoke-") }
        val validHandler = real.tryBlocks.flatMap { it.catches }
            .single { it.exceptionType == RUNTIME }
            .handlerAddress
        val forged = forgedView(
            descriptor,
            listOf(
                TryBlock(
                    invokeAddress,
                    real.nextAddress(invokeAddress),
                    listOf(CatchClause(RUNTIME, validHandler), CatchClause(EXCEPTION, real.endAddress)),
                    null,
                ),
            ),
        )
        val calleeSignature = "$CLASS_NAME->throwInCallee(Ljava/lang/Throwable;)V"
        val session = CoreTester.vmServices.newSession(forged, Limits(), null) { address, currentView, resolver ->
            if (currentView.signature == calleeSignature && address == 0) {
                val parameter = currentView.parameters.single().register
                object : Op(address) {
                    override val name = "equivalent-malformed-throws"

                    override fun execute(frame: Frame): OpExecution = OpExecution.of(
                        List(exitCount) {
                            OpArm.Throw(evidence = ThrowEvidence.Existing(frame.readFrameValue(parameter)))
                        },
                    )
                }
            } else {
                OpBuilder.build(address, currentView, resolver)
            }
        }

        val graph = session.run()

        assertEquals(List(exitCount) { Outcome.Aborted(AbortReason.MALFORMED_INPUT) }, graph.outcomes)
        assertFalse(graph.wasReached(validHandler))
        assertTrue(graph.outcomes.none { it is Outcome.Threw })
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    private fun assertResidualSurvives(descriptor: String) {
        val graph = execute(descriptor)
        val runtimeHandlers = graph.methodView.tryBlocks.flatMap { it.catches }
            .filter { it.exceptionType == RUNTIME }
            .map { it.handlerAddress }

        assertEquals(2, runtimeHandlers.size)
        assertTrue(graph.wasReached(runtimeHandlers.first()), "the initial RuntimeException partition is feasible")
        assertFalse(graph.wasReached(runtimeHandlers.last()), "identity transport must retain its exclusion")
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Known(1, INT)) })
        assertEquals(0, graph.outcomes.count { it == Outcome.Returned(Value.Known(2, INT)) })
        assertEquals(2, graph.outcomes.count { it is Outcome.Threw })
        assertEquals(3, graph.outcomes.size)
    }

    private fun execute(descriptor: String): ExecutionGraph = CoreTester.execute(CLASS_NAME, descriptor)

    private fun view(descriptor: String): MethodView = CoreTester.classIndex.requireMethodView(CLASS_NAME, descriptor)

    private fun forgedView(descriptor: String, tryBlocks: List<TryBlock>): MethodView =
        CoreTester.classIndex.rebuiltView(view(descriptor), tryBlocks)

    private fun assertMalformedRouting(graph: ExecutionGraph, handlerAddresses: List<Int>) {
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        handlerAddresses.forEach { assertFalse(graph.wasReached(it), "handler $it must not commit") }
        assertTrue(graph.outcomes.none { it is Outcome.Threw }, "the sibling escape must not commit")
    }

    private fun assertPublicValueRoutes(graph: ExecutionGraph) {
        assertEquals(1, graph.outcomes.count { it == Outcome.Returned(Value.Known(1, INT)) })
        assertEquals(1, graph.outcomes.count { it == Outcome.Threw(Value.Unknown(THROWABLE)) })
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    private fun handlers(graph: ExecutionGraph): Map<VirtualType, Int> =
        graph.methodView.tryBlocks.flatMap { it.catches }.associate { it.exceptionType to it.handlerAddress }

    private companion object {
        const val CLASS_NAME = "Lexception_handler_test;"
        val INT = VirtualType.of("I")
        val THROWABLE = VirtualType.of("Ljava/lang/Throwable;")
        val EXCEPTION = VirtualType.of("Ljava/lang/Exception;")
        val RUNTIME = VirtualType.of("Ljava/lang/RuntimeException;")
        val ERROR = VirtualType.of("Ljava/lang/Error;")
        val NPE = VirtualType.of("Ljava/lang/NullPointerException;")
    }
}
