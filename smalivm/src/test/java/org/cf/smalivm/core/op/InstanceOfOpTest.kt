package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * `instance-of` (22c): stores 1 if the source is an instance of the type operand and 0 if not, and never forks.
 * Expected answers follow the condition table in `docs/DALVIK-SEMANTICS.md#instance-of-22c`.
 */
class InstanceOfOpTest {
    companion object {
        private const val CLASS_NAME = "Linstanceof_test;"

        /** `instance-of` is two code units, so the `return-void` is at 2. */
        private const val ADDRESS_INSTANCE_OF = 0
        private const val ADDRESS_RETURN = 2

        private val BOOL = VirtualType.of("Z")
        private val TRUE = Value.Known(true, BOOL)
        private val FALSE = Value.Known(false, BOOL)
        private val UNDECIDED = Value.Unknown(BOOL)
        private const val OBJECT = "Ljava/lang/Object;"
        private const val STRING = "Ljava/lang/String;"
        private const val ABSENT = "Lobf/absent;"
    }

    /**
     * Subtype answers are INHERITED from [JLS] §4.10.3 (`docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`):
     * reference arrays are covariant, primitive arrays are not, and every array is an `Object`, `Cloneable`, and
     * `Serializable`. Most seeds carry a looser tag than their object, so the answer must come from the object's type.
     */
    @TestFactory
    fun aProvableSubtypeQuestionFolds(): List<DynamicTest> = listOf(
        answers("int[][] is an Object[]", "instanceOfObjectArray()V", TRUE) {
            setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT)
        },
        answers("int[][] is not an Object[][]", "instanceOf2DObjectArray()V", FALSE) {
            setRegisters(0, arrayOfNulls<IntArray>(0), OBJECT)
        },
        answers("int[] tagged Object is an int[]", "instanceOfIntArray()V", TRUE) {
            setRegisters(0, IntArray(0), OBJECT)
        },
        answers("int[] is not a String", "instanceOfString()V", FALSE) {
            setRegisters(0, IntArray(0), OBJECT)
        },
        // The tag satisfies neither the target nor the object, so only the object's type can answer. The
        // harness rejects a tag the object is not assignable to, so the source register is not asserted.
        answers(
            "String[][] tagged String is an Object[][]", "instanceOf2DObjectArray()V", TRUE, assertsSource = false,
        ) {
            setRegisters(0, arrayOfNulls<Array<String>>(0), STRING)
        },
        answers("String is an Object", "instanceOfObject()V", TRUE) { setRegisters(0, "great maker", STRING) },
        answers("String is a String", "instanceOfString()V", TRUE) { setRegisters(0, "great maker", STRING) },
        answers("String[] is an Object[]", "instanceOfObjectArray()V", TRUE) {
            setRegisters(0, arrayOfNulls<String>(0), OBJECT)
        },
        answers("int[] is not an Object[]", "instanceOfObjectArray()V", FALSE) {
            setRegisters(0, IntArray(0), OBJECT)
        },
        answers("int[] is an Object", "instanceOfObject()V", TRUE) { setRegisters(0, IntArray(0), OBJECT) },
        answers("int[] is Serializable", "instanceOfSerializable()V", TRUE) { setRegisters(0, IntArray(0), OBJECT) },
        answers("String[][] is Serializable", "instanceOfSerializable()V", TRUE) {
            setRegisters(0, arrayOfNulls<Array<String>>(0), "[[Ljava/lang/String;")
        },
        // The control for the two rows above. `String`, the boxed primitives, and `StringBuilder` are all
        // `Serializable`, so only a plain `Object` shows the row is about arrays.
        answers("Object is not Serializable", "instanceOfSerializable()V", FALSE, assertsSource = false) {
            setRegisters(0, Any(), OBJECT)
        },
        // A class missing from the program is ambiguity, not a refusal: the object's own class still decides.
        // The target is `String` because an `Object` target answers without consulting any class.
        answers("String with an absent declared tag is a String", "instanceOfString()V", TRUE) {
            setRegisters(0, "great maker", ABSENT)
        },
    )

    /**
     * Zero rows. SPEC [BC]: a primitive type operand "will always result in 0 being stored", even for an unknown
     * source. INHERITED [JVMS] `instanceof`: a null reference yields 0. A primitive-typed register reads as null,
     * as in ART's switch interpreter (`docs/DALVIK-SEMANTICS.md#reading-a-register-as-a-reference`).
     */
    @TestFactory
    fun aNullSourceOrPrimitiveTargetStoresZero(): List<DynamicTest> = listOf(
        answers("a definite null", "instanceOfString()V", FALSE) { setRegisters(0, null, STRING) },
        answers("a primitive-typed register reads as null", "instanceOfObject()V", FALSE) {
            setRegisters(0, 5, "I")
        },
        // The control: a boxed zero under a reference tag is a live object, not primitive register bits.
        answers("a reference-tagged boxed zero is a live object", "instanceOfString()V", TRUE) {
            setRegisters(0, 0, STRING)
        },
        answers("a primitive type operand with an unknown source", "instanceOfPrimitiveInt()V", FALSE) {
            setRegisters(0, CoreState.UNKNOWN, OBJECT)
        },
        answers("a primitive type operand with an int source", "instanceOfPrimitiveInt()V", FALSE) {
            setRegisters(0, 5, "I")
        },
    )

    /**
     * ENGINE CHOICE: an unknown reference may be null, so even a tag that satisfies the target decides nothing,
     * and a subtype question the class model cannot answer is `Unknown(Z)`, never `false`.
     */
    @TestFactory
    fun anUndecidableQuestionAnswersUnknown(): List<DynamicTest> = listOf(
        answers("an unknown String against String", "instanceOfString()V", UNDECIDED) {
            setRegisters(0, CoreState.UNKNOWN, STRING)
        },
        answers("an unknown String against Object", "instanceOfObject()V", UNDECIDED) {
            setRegisters(0, CoreState.UNKNOWN, STRING)
        },
        answers("an unknown Object against String", "instanceOfString()V", UNDECIDED) {
            setRegisters(0, CoreState.UNKNOWN, OBJECT)
        },
        // `Top` carries no type.
        answers("Top against String", "instanceOfString()V", UNDECIDED) { setRegister(0, Value.Top) },
        answers("a String with an absent declared tag against String[]", "instanceOfStringArray()V", UNDECIDED) {
            setRegisters(0, "great maker", ABSENT)
        },
    )

    /**
     * An unknown source is the seed where a fork would be tempting. Fork arity is read off the node piles and
     * outcomes because a register consensus cannot see two agreeing arms.
     */
    @Test
    fun aTypeTestNeverForks() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, STRING)
        })
        assertEquals(CompletionStatus.Complete, graph.completion(), "nothing aborted, so the arity below is real")
        assertEquals(1, graph.nodePile(ADDRESS_INSTANCE_OF).size, "one node at the instance-of itself")
        assertEquals(
            1, graph.nodePile(ADDRESS_INSTANCE_OF).single().children.size,
            "a type test decides a value, it does not choose a path",
        )
        assertEquals(1, graph.nodePile(ADDRESS_RETURN).size, "so the return is reached once, not twice")
        assertEquals(1, graph.outcomes.size, "and the run has exactly one outcome")
    }

    /**
     * ART's `INSTANCE_OF` handler resolves the type, reads one register, and writes one register; it allocates,
     * initializes, and calls nothing (`docs/DALVIK-SEMANTICS.md#instance-of-22c`). A spurious effect would block
     * every constant fold in the method, because the method level is the maximum over its addresses.
     */
    @Test
    fun aTypeTestDeclaresNoSideEffectOnEveryRow() {
        val seeds = listOf(
            "a folded true" to CoreState().apply { setRegisters(0, "great maker", STRING) },
            "a refused unknown" to CoreState().apply { setRegisters(0, CoreState.UNKNOWN, STRING) },
            "a null source" to CoreState().apply { setRegisters(0, null, STRING) },
            "a primitive-typed source" to CoreState().apply { setRegisters(0, 5, "I") },
        )
        for ((label, seed) in seeds) {
            val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", seed)
            assertEquals(
                SideEffectLevel.NONE, graph.explored.sideEffectLevel(ADDRESS_INSTANCE_OF),
                "$label: a type test mutates nothing outside its own frame",
            )
            val trusted = graph.trusted()
            assertNotNull(trusted, "$label: nothing threw and nothing aborted")
            assertEquals(
                SideEffectLevel.NONE, trusted!!.methodSideEffectLevel(),
                "$label: and the whole-method query a strategy reads agrees",
            )
        }

        // The array-interface row answers without consulting the class model.
        val arrayGraph = CoreTester.execute(CLASS_NAME, "instanceOfSerializable()V", CoreState().apply {
            setRegisters(0, IntArray(0), OBJECT)
        })
        assertEquals(SideEffectLevel.NONE, arrayGraph.explored.sideEffectLevel(ADDRESS_INSTANCE_OF))
    }

    /**
     * Dead-register removal acts on these sets. The `return-void` is the control: the base `Op` also answers the
     * empty set, so only a non-empty set at the instance-of shows the op declares its own.
     */
    @Test
    fun aTypeTestReadsItsSourceAndAssignsItsDestination() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, "great maker", STRING)
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(ADDRESS_INSTANCE_OF), "the op ran, so the sets below describe a live op")
        assertEquals(setOf(1), graph.registersAssigned(ADDRESS_INSTANCE_OF), "vA is the destination")
        assertEquals(emptySet<Int>(), graph.registersAssigned(ADDRESS_RETURN))
        val op = graph.nodePile(ADDRESS_INSTANCE_OF).single().op
        assertEquals(setOf(0), op.registersRead, "vB is the source")
        assertEquals(setOf(1), op.registersAssigned)
    }

    /**
     * An absent type operand throws `NoClassDefFoundError` even for a null source, because ART resolves the type
     * before reading the register: the opposite of the [JVMS] order, measured on device
     * (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`). The same null source against a
     * present type is the control.
     */
    @Test
    fun anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource() {
        val nullSource = CoreState().apply { setRegisters(0, null, OBJECT) }

        CoreTester.test(
            CLASS_NAME, "instanceOfString()V", nullSource,
            CoreState().apply {
                setRegisters(0, null, OBJECT)
                setRegisters(1, false, "Z")
            },
        )

        CoreTester.testThrows(
            CLASS_NAME, "instanceOfAbsentClass()V", nullSource,
            exceptionType = "Ljava/lang/NoClassDefFoundError;",
        )
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfAbsentClass()V", nullSource)
        assertEquals(CompletionStatus.Complete, graph.completion(), "so the negatives below are dead-code claims")
        assertEquals(
            listOf("Ljava/lang/NoClassDefFoundError;"), CoreTester.escapingTypes(graph),
            "exactly one escaping throw, of exactly this type",
        )
        assertEquals(
            0, graph.nodePile(ADDRESS_INSTANCE_OF).single().children.size,
            "a definite throw, not a fork",
        )
        assertTrue(!graph.wasReached(ADDRESS_RETURN), "the fall-through the JVMS ordering would take is dead")
        // `peek` answers null for a slot never written, where `read` would answer `Value.Top`.
        assertEquals(
            null, graph.nodePile(ADDRESS_INSTANCE_OF).first().frame.peek(1),
            "no boolean was folded, so nothing downstream can act on an invented 0",
        )
    }

    @Test
    fun toStringPrefixesBothRegisters() {
        val graph = CoreTester.execute(CLASS_NAME, "instanceOfString()V", CoreState().apply {
            setRegisters(0, "great maker", STRING)
        })
        assertEquals(
            "instance-of r1, r0, Ljava/lang/String;",
            graph.nodePile(ADDRESS_INSTANCE_OF).single().op.toString(),
        )
    }

    /**
     * One table row: [seed] fills register 0, and `vA` (register 1) must hold [answer]. The seed is asserted to
     * survive unless [assertsSource] is false.
     */
    private fun answers(
        name: String,
        method: String,
        answer: Value,
        assertsSource: Boolean = true,
        seed: CoreState.() -> Unit,
    ): DynamicTest = DynamicTest.dynamicTest("$name -> $answer") {
        val initial = CoreState().apply(seed)
        val expected = CoreState().apply {
            if (assertsSource) seed()
            setRegister(1, answer)
        }
        CoreTester.test(CLASS_NAME, method, initial, expected)
    }
}
