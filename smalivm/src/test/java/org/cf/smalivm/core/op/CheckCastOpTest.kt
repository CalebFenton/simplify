package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `check-cast` (21c). Expected values follow `docs/DALVIK-SEMANTICS.md`, "`check-cast` (21c)" and "Resolution
 * failure is one family, tiered once".
 */
class CheckCastOpTest {
    companion object {
        private const val CLASS_NAME = "Lcheck_cast_test;"

        private const val ADDRESS_CHECK_CAST = 0
        private const val ADDRESS_RETURN = 2

        /**
         * Layout of `castToStringWithCatch()V` and `castToStringWithCatchOnAnotherRegister()V`: `check-cast`@0,
         * `nop`@2, `return-void`@3, `move-exception`@4. @2 is the success arm's first node.
         */
        private const val ADDRESS_FALL_THROUGH = 2
        private const val ADDRESS_HANDLER = 4

        private const val OBJECT = "Ljava/lang/Object;"
        private const val STRING = "Ljava/lang/String;"
        private const val CLASS_CAST_EXCEPTION = "Ljava/lang/ClassCastException;"

        private val OBJECT_TYPE = VirtualType.of(OBJECT)
        private val STRING_TYPE = VirtualType.of(STRING)
        private val CCE_TYPE = VirtualType.of(CLASS_CAST_EXCEPTION)

        /** ART's `ThrowClassCastException` text over the object's runtime class. */
        private const val CCE_MESSAGE = "java.lang.Integer cannot be cast to java.lang.String"
    }

    /** A caught throwable carries a throwable refinement; a cast to Serializable, which every Throwable is, never throws. */
    @Test
    fun refinedThrowableSucceedsWhenCastToSerializableInterface() {
        val graph = CoreTester.execute(CLASS_NAME, "castCaughtThrowableToSerializable(Ljava/lang/Throwable;)V")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.outcomes.isNotEmpty())
        assertTrue(graph.outcomes.all { it == Outcome.Returned(null) }, "every caught path returns: ${graph.outcomes}")
    }

    /**
     * A symbolic caught Exception may be a Cloneable subclass, so the cast forks; a caught exact
     * NullPointerException is not Cloneable and a caught exact `cloneable_exception` is.
     */
    @Test
    fun symbolicExceptionKeepsCloneableSuccessButConcreteTypesRemainExact() {
        val symbolic = CoreTester.execute(CLASS_NAME, "castCaughtExceptionToCloneable(Ljava/lang/Exception;)V")
        val nonCloneable = CoreTester.execute(CLASS_NAME, "castCaughtNullPointerExceptionToCloneable()V")
        val cloneable = CoreTester.execute(CLASS_NAME, "castCaughtCloneableExceptionToCloneable()V")

        assertEquals(CompletionStatus.Complete, symbolic.completion())
        assertEquals(
            1, symbolic.outcomes.filterIsInstance<Outcome.Returned>().size,
            "only the symbolic Exception keeps a success arm; a null p0's NullPointerException does not",
        )
        assertEquals(setOf(CLASS_CAST_EXCEPTION), CoreTester.escapingTypes(symbolic).toSet())
        assertEquals(listOf(CLASS_CAST_EXCEPTION), CoreTester.escapingTypes(nonCloneable))
        assertEquals(listOf(Outcome.Returned(null)), cloneable.outcomes)
    }

    /**
     * A passing cast keeps the raw and re-tags the register with the cast type, as ART's verifier does on the
     * success path, including a widening re-tag. A register that reads as a null reference passes any cast
     * ([JVMS] `checkcast`, ART `CHECK_CAST`); the re-tag is skipped when it would stop the register reading as null,
     * so `Known(5, I)` keeps its tag. A non-zero primitive is verifier-excluded input (see "Reading a register as a
     * reference").
     */
    @TestFactory
    fun aPassingCastKeepsTheRawAndRetagsTheRegister(): List<DynamicTest> = listOf(
        PassingCast("null Object to String", "castToString()V", null, OBJECT, STRING),
        PassingCast("String tagged Object to String", "castToString()V", "great maker", OBJECT, STRING),
        PassingCast("String to Object widens the tag", "castToObject()V", "great maker", STRING, OBJECT),
        PassingCast("null under a primitive tag", "castToString()V", null, "I", STRING),
        PassingCast("integral zero under a reference tag", "castToObject()V", 0, STRING, OBJECT),
        PassingCast("non-zero primitive is not re-tagged", "castToString()V", 5, "I", "I"),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply { setRegisters(0, case.raw, case.type) }
            val expected = CoreState().apply { setRegisters(0, case.raw, case.expectedType) }
            CoreTester.test(CLASS_NAME, case.method, initial, expected)
        }
    }

    private data class PassingCast(
        val name: String,
        val method: String,
        val raw: Any?,
        val type: String,
        val expectedType: String,
    )

    /**
     * `testCaught` because the fixture's handler covers the cast, so a caught throw leaves no `Outcome.Threw`.
     * Message per ART's `ThrowClassCastException`.
     */
    @Test
    fun objectNotOfCastTypeThrowsClassCastException() {
        val initial = CoreState().apply { setRegisters(0, 0x42, "Ljava/lang/Integer;") }
        CoreTester.testCaught(
            CLASS_NAME,
            "castToStringWithCatch()V",
            initial,
            exceptionType = CLASS_CAST_EXCEPTION,
            message = CCE_MESSAGE,
            caughtInRegister = 0,
            unreachedAddress = ADDRESS_FALL_THROUGH,
        )
    }

    /**
     * An unknown whose tag does not satisfy the target may or may not be castable, so both outcomes are explored
     * (engine choice). Bug class: dropping the throw arm leaves a feasible path unexplored on a `Complete` run.
     * The handler covers the cast, so arity is read from the node's children and the bound exception, not from
     * escaping outcomes.
     */
    @Test
    fun anAmbiguousCastExploresBothTheSuccessAndTheThrowAndTheThrowIsCaughtHere() {
        val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, OBJECT)
        })
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "the fork must be explored to the end, not truncated; an arity claim on a Partial run is void",
        )
        assertEquals(1, graph.nodePile(ADDRESS_CHECK_CAST).size, "one node at the cast itself")
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "two successors: the cast may succeed and it may throw, and both are feasible",
        )
        assertTrue(graph.wasReached(ADDRESS_FALL_THROUGH), "the success arm ran")
        assertTrue(graph.wasReached(ADDRESS_HANDLER), "and so did the handler")
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "the handler covers the op, so nothing escapes: ${graph.outcomes}",
        )
        val caught = graph.nodePile(ADDRESS_HANDLER).single().frame.peek(0)
        assertTrue(
            caught is Value.Known && caught.type == CCE_TYPE,
            "`move-exception v0` must bind a ClassCastException, but bound $caught",
        )
        val ref = (caught as Value.Known).raw
        assertTrue(ref is HeapRef, "a synthesized guest throwable is a tracked heap object, not $ref")
        val thrown = graph.nodePile(ADDRESS_HANDLER).single().frame.heapObject(ref as HeapRef)
        assertTrue(thrown is HeapObject.GuestThrowable, "no synthesized throwable at $ref; got $thrown")
        assertNull(
            (graph.nodePile(ADDRESS_HANDLER).single().frame.heap.hostObjectAt(ref) as Throwable).message,
            "ART's text names the object's runtime class, which an Unknown does not have",
        )
    }

    /**
     * The success arm carries the register narrowed to the cast type; the throw arm keeps the pre-op value, since
     * the throw proves it is not a String. `castToStringWithCatchOnAnotherRegister()V` binds the exception to v1 so
     * v0 stays observable on the handler arm.
     */
    @Test
    fun theForksSuccessArmIsNarrowedAndItsThrowingArmIsNot() {
        val graph = CoreTester.execute(
            CLASS_NAME,
            "castToStringWithCatchOnAnotherRegister()V",
            CoreState().apply { setRegisters(0, CoreState.UNKNOWN, OBJECT) },
        )
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "the premise: this is the forking row, so there are two arms to tell apart",
        )
        assertEquals(
            Value.Unknown(STRING_TYPE), trusted!!.consensus(ADDRESS_FALL_THROUGH, 0),
            "the arm on which the cast succeeded knows the register is a String (or null)",
        )
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.consensus(ADDRESS_HANDLER, 0),
            "the arm on which it threw must not: the throw is the proof that it is not a String",
        )
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.terminatingConsensus(0),
            "the two arms disagree about the type, so their join is the common supertype",
        )
        assertEquals(
            Value.Unknown(OBJECT_TYPE), trusted.consensus(ADDRESS_CHECK_CAST, 0),
            "the multi-arm source is the conservative join and never selects the successful arm's type",
        )
    }

    /**
     * `Unknown(T)` holds null or an instance of `T`, and both pass a cast to `T` or a supertype, so the cast cannot
     * fail. Bug class: forking a never-taken `ClassCastException` for every `String` parameter cast to `String`.
     * Fork arity is asserted directly because the value join cannot see it.
     */
    @Test
    fun anUnknownWhoseDeclaredTypeSatisfiesTheTargetNeverForks() {
        for ((method, target) in listOf("castToObject()V" to OBJECT_TYPE, "castToString()V" to STRING_TYPE)) {
            val graph = CoreTester.execute(CLASS_NAME, method, CoreState().apply {
                setRegisters(0, CoreState.UNKNOWN, STRING)
            })
            assertEquals(CompletionStatus.Complete, graph.completion(), "$method: nothing truncated")
            assertEquals(
                1, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
                "$method: one successor; this cast provably succeeds, so there is no exception to explore",
            )
            assertEquals(1, graph.nodePile(ADDRESS_RETURN).size, "$method: so the return is reached once")
            assertEquals(1, graph.outcomes.size, "$method: and the run has exactly one outcome")
            assertTrue(
                graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
                "$method: and it is not a throw: ${graph.outcomes}",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$method: a Complete exploration must yield a trusted view")
            assertEquals(
                Value.Unknown(target), trusted!!.terminatingConsensus(0),
                "$method: and the register is still re-tagged, because the cast succeeded",
            )
        }
    }

    /**
     * A definite failing cast throws and explores nothing else. The register is tagged Object while holding an
     * Integer, so the message names the runtime class, as ART's `ThrowClassCastException` does, not the tag.
     */
    @Test
    fun aDefinitelyFailingCastThrowsAndDoesNotAlsoContinue() {
        val initial = CoreState().apply { setRegisters(0, 0x42, OBJECT) }
        CoreTester.testThrows(
            CLASS_NAME, "castToString()V", initial,
            exceptionType = CLASS_CAST_EXCEPTION, message = CCE_MESSAGE,
        )
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            0, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "the throw escapes and nothing else is feasible, so the cast node has no children",
        )
        assertFalse(graph.wasReached(ADDRESS_RETURN), "and the fall-through was not explored")
        assertEquals(1, graph.outcomes.size, "exactly one outcome: the throw")
    }

    /**
     * `Value.Top` has no type, so both outcomes are feasible. The success arm still narrows to `Unknown(C)`, and the
     * synthesized exception carries no message because there is no class to name.
     */
    @Test
    fun aTopRegisterForksAndTheSuccessArmIsStillRefined() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegister(0, Value.Top)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(
            1, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "one child: the throw escapes, so it has no node",
        )
        assertEquals(2, graph.outcomes.size, "two outcomes: the return and the escaping throw")
        val threw = graph.outcomes.filterIsInstance<Outcome.Threw>()
        assertEquals(1, threw.size, "exactly one throw, and it is the cast's")
        val exception = threw.single().exception
        assertTrue(
            exception is Value.Known && exception.type == CCE_TYPE,
            "a ClassCastException, not something derived from a type Top does not have: $exception",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        assertEquals(
            Value.Unknown(STRING_TYPE), trusted!!.terminatingConsensus(0),
            "the only returning path is the one the cast succeeded on, and it knows more than Top did",
        )
    }

    /**
     * A tag naming a class the input does not ship, over a raw whose class does not satisfy the target, leaves the
     * cast unprovable. Bug classes: absorbing the unanswerable walk into a definite `ClassCastException`, or
     * re-asking the class index from the op so `ClassLoadFailure` aborts the path as a host failure.
     */
    @Test
    fun anUnanswerableClassWalkForksInsteadOfRaisingOrInventingAThrow() {
        val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", CoreState().apply {
            setRegisters(0, Any(), "Lobf/absent;")
        })
        assertEquals(
            CompletionStatus.Complete, graph.completion(),
            "a ClassLoadFailure must not escape as a host failure: ${graph.outcomes}",
        )
        assertEquals(
            2, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "unprovable, so both outcomes are explored; not a definite throw and not a definite success",
        )
        assertTrue(graph.wasReached(ADDRESS_FALL_THROUGH), "the success arm ran")
        assertTrue(graph.wasReached(ADDRESS_HANDLER), "and so did the throw arm's handler")
    }

    /**
     * ART's `CHECK_CAST` resolves, reads one register and conditionally throws; nothing allocates or initializes.
     * A spurious `STRONG` would block every constant fold in the method. Effects are per arm, so every row is driven.
     */
    @Test
    fun aCastDeclaresNoSideEffectOnEveryRow() {
        val seeds = listOf(
            "a provable success" to CoreState().apply { setRegisters(0, "great maker", OBJECT) },
            "a null register" to CoreState().apply { setRegisters(0, null, OBJECT) },
            "a primitive-typed register" to CoreState().apply { setRegisters(0, 5, "I") },
            "a definite failure" to CoreState().apply { setRegisters(0, 0x42, "Ljava/lang/Integer;") },
            "an ambiguous fork" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, OBJECT) },
            "a satisfying unknown" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) },
        )
        for ((label, seed) in seeds) {
            val graph = CoreTester.execute(CLASS_NAME, "castToStringWithCatch()V", seed)
            assertEquals(
                SideEffectLevel.NONE, graph.explored.sideEffectLevel(ADDRESS_CHECK_CAST),
                "$label: a cast mutates nothing outside its own frame",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$label: nothing aborted")
            assertEquals(
                SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
                "$label: and the whole-method query a strategy actually reads agrees",
            )
        }
    }

    /**
     * dexlib2 marks `check-cast` `SETS_REGISTER` because ART's verifier re-types vA on success, so the register is
     * both read and assigned. The `return-void` control shows an op that assigns nothing answers the empty set.
     */
    @Test
    fun aSuccessfulCastAssignsItsTargetRegisterAndReadsIt() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegisters(0, "great maker", OBJECT)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_CHECK_CAST), "the op ran, so the sets below describe a live op")
        assertEquals(setOf(0), graph.registersAssigned(ADDRESS_CHECK_CAST), "vAA is re-tagged, so it is assigned")
        assertEquals(
            emptySet<Int>(), graph.registersAssigned(ADDRESS_RETURN),
            "the control: an op that assigns nothing answers the empty set",
        )
        val op = graph.nodePile(ADDRESS_CHECK_CAST).single().op
        assertEquals(setOf(0), op.registersRead, "and the same register is read: one operand, read and written")
        assertEquals(setOf(0), op.registersAssigned)
    }

    /**
     * An absent cast type throws `NoClassDefFoundError` before the register is read, so even a null source throws
     * (device-measured by `Fx_CheckCastAbsentType`). The control casts the same null to a present type and passes,
     * so the throw is the absent type's, not the null's.
     */
    @Test
    fun anAbsentCastTypeThrowsNoClassDefFoundErrorEvenOnANullSource() {
        val nullSource = CoreState().apply { setRegisters(0, null, OBJECT) }

        CoreTester.test(
            CLASS_NAME, "castToString()V", nullSource,
            CoreState().apply { setRegisters(0, null, STRING) },
        )

        CoreTester.testThrows(
            CLASS_NAME, "castToAbsentClass()V", nullSource,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "castToAbsentClass()V", nullSource)
        assertEquals(CompletionStatus.Complete, graph.completion(), "so the negatives below are dead-code claims")
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type, not a ClassCastException",
        )
        assertEquals(
            0, graph.nodePile(ADDRESS_CHECK_CAST).single().children.size,
            "definite, not a fork: the loaded class set is the whole program",
        )
        assertFalse(graph.wasReached(ADDRESS_RETURN), "and the fall-through a null source would take is dead")
        assertEquals(
            Value.Known(null, OBJECT_TYPE), graph.nodePile(ADDRESS_CHECK_CAST).first().frame.peek(0),
            "the throw happened before the register was read, so no re-tag survives",
        )
    }

    @Test
    fun toStringRendersTheRegisterAndCastType() {
        val graph = CoreTester.execute(CLASS_NAME, "castToString()V", CoreState().apply {
            setRegisters(0, "great maker", OBJECT)
        })
        assertEquals(
            "check-cast r0, Ljava/lang/String;",
            graph.nodePile(ADDRESS_CHECK_CAST).single().op.toString(),
        )
    }
}
