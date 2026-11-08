package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File

/**
 * Throw routing in a session: the caught object is the thrown object, a provable throw type takes exactly one
 * handler, and an unprovable one forks every feasible handler plus the escape. Oracle: Dalvik try/catch and the
 * `throw` table in `docs/DALVIK-SEMANTICS.md`; fixtures are in `spine.smali`.
 */
class ThrowRoutingTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val vmServices = VmServices(classIndex)
    private val runtimeException = VirtualType.of("Ljava/lang/RuntimeException;")
    private val illegalStateException = VirtualType.of("Ljava/lang/IllegalStateException;")
    private val ioException = VirtualType.of("Ljava/io/IOException;")
    private val npe = VirtualType.of("Ljava/lang/NullPointerException;")
    private val objectType = VirtualType.of("Ljava/lang/Object;")
    private val string = VirtualType.of("Ljava/lang/String;")
    private val int = VirtualType.of("I")

    private val caughtDescriptor = "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;"
    private val twoHandlersDescriptor = "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;"
    private val catchAllDescriptor = "throwCatchAll(Ljava/lang/Object;)Ljava/lang/Object;"
    private val unloadableDescriptor = "throwUnloadableCatchType(Ljava/lang/RuntimeException;)Ljava/lang/Object;"
    private val afterNopDescriptor = "throwCaughtAfterNop(Ljava/lang/RuntimeException;)Ljava/lang/Object;"

    private fun session(
        descriptor: String,
        limits: Limits = Limits(),
        seed: (Frame, MethodView) -> Unit = { _, _ -> },
    ): ExecutionSession {
        val view = classIndex.requireMethodView("Lspine/spine;", descriptor)
        val frame = Frame.entryFrame(view).also { seed(it, view) }
        return vmServices.newSession(view, limits = limits, entryFrame = frame)
    }

    /** A session whose single parameter holds [thrown]. */
    private fun sessionThrowing(descriptor: String, thrown: Value): ExecutionSession =
        session(descriptor) { frame, view -> frame.assign(view.parameters.single().register, thrown) }

    /**
     * A fixture method with replaced try blocks. A handler at [MethodView.endAddress] names no instruction, which
     * reaches the malformed-try-block guard.
     */
    private fun withTryBlocks(descriptor: String, tryBlocks: (MethodView) -> List<TryBlock>): MethodView {
        val real = classIndex.requireMethodView("Lspine/spine;", descriptor)
        return classIndex.rebuiltView(real, tryBlocks(real))
    }

    private fun throwAddressOf(view: MethodView): Int = view.addresses.first { view.opcodeAt(it) == Opcode.THROW }

    @Test
    fun caughtNullPointerExceptionIsRethrownWithTheSameIdentity() {
        // A null receiver throws; move-exception captures that NPE; throw must preserve its identity.
        val view = classIndex.requireMethodView("Lthrow_test;", "throwNullPointerException()V")
        val graph = vmServices.newSession(view).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        val escaped = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
        assertEquals(npe, escaped.type)
        val terminal = graph.nodePile(6).single()
        val frame = terminal.terminalFrame
        assertEquals(escaped, frame.read(0))
        assertEquals(escaped, frame.thrownOrNull())
        assertTrue(frame.heap.hostObjectAt(escaped.raw as HeapRef) is NullPointerException)
        assertEquals(listOf(0, 1, 5, 6), graph.addresses().filter(graph::wasReached))
    }

    @Test
    fun aKnownSubtypeReachesItsSupertypeHandlerWithItsIdentity() {
        // The clause names RuntimeException and the thrown class is a strict subtype, so a catch resolved by
        // descriptor equality instead of subtyping fails here.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val graph = sessionThrowing(caughtDescriptor, thrown).run()

        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "the typed handler must execute")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        assertEquals(illegalStateException, returned.type)
        assertTrue(returned.raw is HeapRef, "the imported throwable must have guest heap identity")
        // A caught throw is a guest outcome, not an abort, so the run completes and the trusted view answers.
        assertEquals(returned, graph.trusted()!!.terminatingConsensus(0))
    }

    /** The escaped value carries the thrown object's own class, not the register's declared type. */
    @TestFactory
    fun anUncaughtExceptionEscapesAsThrewWithItsOwnType(): List<DynamicTest> = listOf(
        Triple("no try block", "throwUncaught(Ljava/lang/RuntimeException;)V",
            Value.Known(IllegalStateException("boom"), runtimeException) to illegalStateException),
        Triple("a clause for an unrelated type", caughtDescriptor,
            Value.Known(java.io.IOException("io"), ioException) to ioException),
    ).map { (name, descriptor, row) ->
        val (thrown, expectedType) = row
        DynamicTest.dynamicTest(name) {
            // A guest throw is Threw carrying the thrown value; only engine and limit failures abort.
            val graph = sessionThrowing(descriptor, thrown).run()
            val escaped = (graph.outcomes.single() as Outcome.Threw).exception as Value.Known
            assertEquals(expectedType, escaped.type)
            assertTrue(escaped.raw is HeapRef)
            graph.methodView.tryBlocks.flatMap { it.catches }.forEach {
                assertFalse(graph.wasReached(it.handlerAddress), "handler ${it.handlerAddress} must not run")
            }
        }
    }

    @Test
    fun aSymbolicExceptionIsConstrainedByItsDeclaredType() {
        // The unseeded parameter is declared RuntimeException, which the RuntimeException clause consumes whole,
        // so no escape remains.
        val graph = session(caughtDescriptor).run()
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress))
        assertTrue(graph.outcomes.none { it is Outcome.Threw })
        assertEquals(1, graph.outcomes.count { it is Outcome.Returned })
    }

    @Test
    fun aKnownThrowResolvesHandlersAtItsOwnAddress() {
        // Try ranges are per address. The `nop` puts the throw at address 1 with a try block covering only it,
        // so resolving handlers at the method start would find none and report an escape on a complete run.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val graph = sessionThrowing(afterNopDescriptor, thrown).run()

        val tryBlock = graph.methodView.tryBlocks.single()
        assertEquals(1, throwAddressOf(graph.methodView))
        assertFalse(tryBlock.covers(0), "the fixture discriminates only if address 0 is uncovered")

        assertTrue(graph.wasReached(tryBlock.catches.single().handlerAddress), "the handler must execute")
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        assertEquals(illegalStateException, returned.type)
        assertTrue(returned.raw is HeapRef, "the throw is caught and keeps imported guest identity")
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @TestFactory
    fun anUnprovableThrowReachesEveryFeasibleHandlerAndTheEscape(): List<DynamicTest> = listOf(
        // The unseeded Object parameter, and an Unknown(I): a primitive tag is an upper bound, so the register
        // may still hold an untracked reference and the throw is not provably null.
        "unseeded Object parameter" to null,
        "Unknown(I)" to Value.Unknown(int),
    ).map { (name, seeded) ->
        DynamicTest.dynamicTest(name) {
            val graph = if (seeded == null) session(twoHandlersDescriptor).run()
            else sessionThrowing(twoHandlersDescriptor, seeded).run()
            val thrown = seeded ?: Value.Unknown(objectType)

            graph.methodView.tryBlocks.flatMap { it.catches }.forEach {
                assertTrue(graph.wasReached(it.handlerAddress), "handler ${it.handlerAddress} must execute")
            }
            assertEquals(
                setOf(
                    Outcome.Threw(thrown),
                    Outcome.Returned(thrown),
                    Outcome.Returned(Value.Known("later", string)),
                ),
                graph.outcomes.toSet(),
                "the escape stays feasible and carries the unknown value; no NPE is invented for it",
            )
            assertEquals(3, graph.outcomes.size)
            assertEquals(CompletionStatus.Complete, graph.completion(), "ambiguity is not an abort")
            // escapingTypes renders a non-Known escape, so the empty-escape assertions in this class are not vacuous.
            assertEquals(listOf("Unknown(${thrown.type.descriptor})"), CoreTester.escapingTypes(graph))
        }
    }

    /**
     * `throw` of a primitive register reads it as null and throws a message-less NPE, whatever its bits; a
     * non-zero primitive is VERIFIER tier (see "Thrown value" under `throw` in `docs/DALVIK-SEMANTICS.md`). Each
     * fixture covers the throw with `catch (IOException)` at the throw's fall-through address and
     * `catch (RuntimeException)`; only the second catches an NPE.
     */
    @TestFactory
    fun aThrownPrimitiveEntersOnlyTheHandlerThatCatchesNullPointerException(): List<DynamicTest> = listOf(
        "zero" to "throwNullConstTwoHandlers()Ljava/lang/Object;",
        "non-zero" to "throwNonZeroConstTwoHandlers()Ljava/lang/Object;",
    ).map { (name, descriptor) ->
        DynamicTest.dynamicTest(name) {
            val graph = session(descriptor).run()
            assertEquals(CompletionStatus.Complete, graph.completion())

            // dexlib2 emits one TryBlock per `.catch` directive, so clauses are flattened across blocks.
            val clauses = graph.methodView.tryBlocks.flatMap { it.catches }
            val npeHandler = clauses.single { it.exceptionType == runtimeException }.handlerAddress
            val ioHandler = clauses.single { it.exceptionType == ioException }.handlerAddress
            assertEquals(graph.methodView.nextAddress(throwAddressOf(graph.methodView)), ioHandler,
                "a router that fell through instead of resolving would land on the IOException handler")

            assertTrue(graph.wasReached(npeHandler), "the clause that catches a NullPointerException must run")
            assertFalse(graph.wasReached(ioHandler), "an NPE is not an IOException, so that handler is dead")
            assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "nothing escapes: ${CoreTester.escapingTypes(graph)}")

            val caught = (graph.outcomes.single() as Outcome.Returned).value
            assertTrue(caught is Value.Known && caught.type == npe, "move-exception bound an NPE: $caught")
            val host = CoreTester.terminatingHostObject(graph, (caught as Value.Known).raw as HeapRef)
            assertTrue(host is NullPointerException, "got $host")
            // ART's THROW calls the no-argument ThrowNullPointerException().
            assertNull((host as Throwable).message)
        }
    }

    @Test
    fun aNullReferenceThrowDeliversASynthesizedNpe() {
        // Known(null, RuntimeException) reaches ThrowOp through `isNull`, not the primitive path above.
        val thrown = Value.Known(null, runtimeException)
        val graph = sessionThrowing(caughtDescriptor, thrown).run()
        assertEquals(CompletionStatus.Complete, graph.completion())
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "an NPE is a RuntimeException, so this handler catches it")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "the throw is definitely caught, so nothing escapes")

        val caught = (graph.outcomes.single() as Outcome.Returned).value
        assertTrue(caught is Value.Known && caught.type == npe,
            "the handler must receive the synthesized NPE, not the null reference: $caught")
        assertNotEquals(thrown, caught)
    }

    @TestFactory
    fun aThrownNonThrowableEscapesAsInternalError(): List<DynamicTest> = listOf(
        // A host Integer under a String tag is neither a null reference nor a heap object.
        "Integer under a String tag" to Value.Known(0, string),
        // A host String under an unloadable tag is not a heap object, so its tag never reaches the subtype query.
        "String under an unloadable tag" to Value.Known("stand-in", VirtualType.of("Lspine/absent;")),
    ).map { (name, thrown) ->
        DynamicTest.dynamicTest(name) {
            val graph = sessionThrowing(caughtDescriptor, thrown).run()
            assertEquals(listOf("Ljava/lang/InternalError;"), CoreTester.escapingTypes(graph))
            assertEquals(CompletionStatus.Complete, graph.completion())
        }
    }

    @Test
    fun synthesizingTheNpeIsNotASideEffect() {
        // Allocating the NPE is a modelled write to the run's own heap. A spurious STRONG level would stop every
        // constant fold in a method containing a `throw` of null, because the method level maxes over addresses.
        val graph = session("throwNullConst()Ljava/lang/Object;").run()
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(throwAddressOf(graph.methodView)))
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun aCoveringCatchAllMakesTheUnhandledEscapeInfeasible() {
        // An unknown thrown type forks the typed handler and the catch-all; the catch-all catches everything else,
        // so no escape is feasible.
        val graph = session(catchAllDescriptor).run()
        assertTrue(graph.outcomes.none { it is Outcome.Threw },
            "a catch-all guarantees the exception is handled in this method")
        assertEquals(
            setOf(
                Outcome.Returned(Value.Unknown(objectType)),
                Outcome.Returned(Value.Known("all", string)),
            ),
            graph.outcomes.toSet(),
            "both the typed handler and the catch-all handler are still feasible",
        )
        assertEquals(2, graph.outcomes.size)
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @Test
    fun aKnownTypeNoTypedClauseMatchesFallsToTheCatchAll() {
        val thrown = Value.Known(java.io.IOException("io"), ioException)
        val graph = sessionThrowing(catchAllDescriptor, thrown).run()
        assertEquals(listOf(Outcome.Returned(Value.Known("all", string))), graph.outcomes)
    }

    @Test
    fun anUnloadableCatchTypeForksTheHandlerAndTheEscape() {
        // `Lspine/absent;` is a valid descriptor with no class behind it, as is normal in an obfuscated APK, so
        // whether the clause catches is unanswerable. The clause might catch: the handler and the escape are both
        // explored and the run stays Complete rather than ending as a host failure.
        val thrown = Value.Known(IllegalStateException("boom"), runtimeException)
        val graph = sessionThrowing(unloadableDescriptor, thrown).run()
        val handlerAddress = graph.methodView.tryBlocks.single().catches.single().handlerAddress
        assertTrue(graph.wasReached(handlerAddress), "an unanswerable catch type might catch")
        val escaped = (graph.outcomes.single { it is Outcome.Threw } as Outcome.Threw).exception as Value.Known
        val returned = (graph.outcomes.single { it is Outcome.Returned } as Outcome.Returned).value
        assertEquals(escaped, returned, "all routes of one imported event share one HeapRef")
        assertEquals(CompletionStatus.Complete, graph.completion())
    }

    @TestFactory
    fun aHandlerAddressOutsideTheMethodAbortsTheThrowAsMalformedInput(): List<DynamicTest> = listOf(
        "known throwable" to Value.Known(IllegalStateException("boom"), runtimeException),
        "unseeded parameter" to null,
    ).map { (name, thrown) ->
        DynamicTest.dynamicTest(name) {
            // The table names an address the method does not contain: the input is malformed, the run does not
            // crash, and the throw neither routes nor escapes.
            val view = withTryBlocks(caughtDescriptor) {
                listOf(TryBlock(0, 1, listOf(CatchClause(runtimeException, it.endAddress)), null))
            }
            val frame = thrown?.let { Frame.entryFrame(view).also { f -> f.assign(view.parameters.single().register, it) } }
            val graph = vmServices.newSession(view, Limits(), frame).run()
            assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
            assertEquals(CompletionStatus.Partial(setOf(AbortReason.MALFORMED_INPUT)), graph.completion())
        }
    }

    @Test
    fun oneMalformedHandlerAbortsTheWholeForkRatherThanCommittingHalfOfIt() {
        // The malformed handler is the second one forked, so scheduling handlers one at a time would already have
        // committed the first. A malformed table makes all of its routing suspect: nothing routes.
        val view = withTryBlocks(caughtDescriptor) {
            listOf(
                TryBlock(
                    0, 1,
                    listOf(
                        CatchClause(runtimeException, 1),
                        CatchClause(ioException, it.endAddress),
                    ),
                    null,
                ),
            )
        }
        val frame = Frame.entryFrame(view).also { it.assign(view.parameters.single().register, Value.Top) }
        val graph = vmServices.newSession(view, Limits(), frame).run()
        assertEquals(listOf(Outcome.Aborted(AbortReason.MALFORMED_INPUT)), graph.outcomes)
        assertFalse(graph.wasReached(1), "the well-formed handler must not have been committed either")
    }
}
