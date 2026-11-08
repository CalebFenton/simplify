package org.cf.smalivm.core.op

import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreState
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.agreed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * The seven `aput*` opcodes (format 23x, two code units, so the successor of an op at 0 is address 2).
 * `vAA` is the value register, not a destination: all three operands are read and no register is assigned.
 * Exceptions, their order, and the store check follow `docs/DALVIK-SEMANTICS.md` section
 * "`aget*` / `aput*` (ops 44–51, 23x)" and its subsection "`ArrayStoreException` comes from `aput-object` and
 * nothing else".
 */
class APutOpTest {
    private data class PutCase(
        val name: String,
        val method: String,
        val array: Any?,
        val arrayType: String,
        val value: Any?,
        val valueType: String,
        val stored: Any,
        val index: Any = 0,
        val indexType: String = "I",
    )

    /**
     * The stored element is the value as the component holds it, narrowed through `Coerce` (section "Coercion
     * and register bits"): a boolean keeps the low byte (ART's `SetWithoutChecks(uint8_t)`), `aput-object` of
     * integral zero stores null ([BC]: `(Object) null == (int) 0`), and a reference to a boxed zero stores the
     * object because only a primitive write makes a register read as null. A `String[]` behind an
     * `[Ljava/lang/Object;` tag is legal covariance.
     */
    @TestFactory
    fun canPutEachVariant(): List<DynamicTest> = listOf(
        PutCase("int", "put()V", IntArray(1), "[I", 4, "I", intArrayOf(4)),
        PutCase("int with short index", "put()V", IntArray(1), "[I", 4, "I", intArrayOf(4), index = 0.toShort(), indexType = "S"),
        PutCase("boolean", "putBoolean()V", BooleanArray(1), "[Z", 0x1, "Z", booleanArrayOf(true)),
        PutCase("boolean from short", "putBoolean()V", BooleanArray(1), "[Z", 0x1.toShort(), "Z", booleanArrayOf(true)),
        PutCase("boolean keeps the low byte", "putBoolean()V", BooleanArray(1), "[Z", 0x101, "I", booleanArrayOf(true)),
        PutCase("byte", "putByte()V", ByteArray(1), "[B", 0xf.toByte(), "B", byteArrayOf(0xf)),
        PutCase("byte from int", "putByte()V", ByteArray(1), "[B", 0xf, "B", byteArrayOf(0xf)),
        PutCase("char", "putChar()V", CharArray(1), "[C", '$', "C", charArrayOf('$')),
        PutCase("char from int", "putChar()V", CharArray(1), "[C", '$'.code, "I", charArrayOf('$')),
        PutCase("short", "putShort()V", ShortArray(1), "[S", 0x42.toShort(), "S", shortArrayOf(0x42)),
        PutCase("short from int", "putShort()V", ShortArray(1), "[S", 0x42, "I", shortArrayOf(0x42)),
        PutCase("wide long", "putWide()V", LongArray(1), "[J", 10000000000L, "J", longArrayOf(10000000000L)),
        PutCase("wide double", "putWide()V", DoubleArray(1), "[D", 100000000000.0, "D", doubleArrayOf(100000000000.0)),
        PutCase("wide float", "putWide()V", FloatArray(1), "[F", 10.45f, "F", floatArrayOf(10.45f)),
        PutCase(
            "string into string array", "putObject()V", arrayOfNulls<String>(1), "[Ljava/lang/String;",
            ARRAKIS, "Ljava/lang/String;", arrayOf(ARRAKIS),
        ),
        PutCase(
            "string into object-tagged string array", "putObject()V", arrayOfNulls<String>(1), "[Ljava/lang/Object;",
            ARRAKIS, "Ljava/lang/String;", arrayOf(ARRAKIS),
        ),
        PutCase(
            "integral zero as null", "putObject()V", arrayOfNulls<String>(1), "[Ljava/lang/String;",
            0, "I", arrayOf<String?>(null),
        ),
        PutCase(
            "reference to a boxed zero", "putObject()V", arrayOfNulls<Any>(1), "[Ljava/lang/Object;",
            BOXED_ZERO, "Ljava/lang/Integer;", arrayOf(BOXED_ZERO),
        ),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply {
                setRegisters(0, case.array, case.arrayType, 1, case.index, case.indexType, 2, case.value, case.valueType)
            }
            val expected = CoreState().apply { setRegisters(0, case.stored, case.arrayType) }
            CoreTester.test(CLASS_NAME, case.method, initial, expected)
        }
    }

    /** Two stores of guest `java.lang.Class` values, one of them a class loaded from the fixture itself. */
    @Test
    fun canInsertLocalClassAndClassIntoSameArray() {
        val valueType = "Ljava/lang/Class;"
        val arrayType = "[$valueType"
        val value1: Class<*> = String::class.java
        val value2: Class<*> = CoreTester.classLoader.loadClass("aput_test")
        val initial = CoreState().apply {
            setRegisters(
                0, arrayOfNulls<Class<*>>(2), arrayType, 1, 0, "I", 2, value1, valueType, 3, 1, "I", 4, value2, valueType,
            )
        }
        val expected = CoreState().apply { setRegisters(0, arrayOf(value1, value2), arrayType) }
        CoreTester.test(CLASS_NAME, "putObjects()V", initial, expected)
    }

    private data class NestedCase(
        val name: String,
        val array: Any,
        val arrayType: String,
        val value: Any,
        val valueType: String,
        val storedElementType: String,
        val innerElements: List<Value>,
    )

    /**
     * An array stored into an array. The stored element's type comes from the outer array object, never the
     * value register's tag, so the `[[Ljava/lang/Object;` tag over a `String[][]` still stores `[Ljava/lang/String;`.
     */
    @TestFactory
    fun canPutAnArrayIntoAnArray(): List<DynamicTest> = listOf(
        NestedCase(
            "byte array into object array", arrayOfNulls<Any>(1), "[Ljava/lang/Object;",
            ByteArray(1), "[B", "Ljava/lang/Object;", listOf(Value.Known(0.toByte(), BYTE)),
        ),
        NestedCase(
            "string array into 2D string array", arrayOfNulls<Array<String?>>(1), "[[Ljava/lang/String;",
            arrayOf<String?>(ARRAKIS), "[Ljava/lang/String;", "[Ljava/lang/String;", listOf(Value.Known(ARRAKIS, STRING)),
        ),
        NestedCase(
            "string array into 2D object-tagged array", arrayOfNulls<Array<String?>>(1), "[[Ljava/lang/Object;",
            arrayOf<String?>(ARRAKIS), "[Ljava/lang/String;", "[Ljava/lang/String;", listOf(Value.Known(ARRAKIS, STRING)),
        ),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply {
                setRegisters(0, case.array, case.arrayType, 1, 0, "I", 2, case.value, case.valueType)
            }
            assertStoredArrayElements(initial, case.storedElementType, case.innerElements)
        }
    }

    /** Element-wise precision: an unknown value makes only the written slot unknown. */
    @Test
    fun anUnknownValueMakesOnlyItsOwnElementUnknown() {
        val array = storedArray("put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 2), "[I", 1, 0, "I", 2, CoreState.UNKNOWN, "I")
        })
        assertEquals(Value.Unknown(INT), array.element(0), "the written slot")
        assertEquals(Value.Known(2, INT), array.element(1), "and only the written slot")
    }

    /**
     * An unknown index joins every element with the stored value, so an element already equal to it stays
     * known: `join(1, 7)` is `Unknown(I)`, `join(7, 7)` is `Known(7)`.
     */
    @Test
    fun anUnknownIndexJoinsEveryElementWithTheStoredValue() {
        val array = storedArray("put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 7), "[I", 1, CoreState.UNKNOWN, "I", 2, 7, "I")
        })
        assertEquals(Value.Unknown(INT), array.element(0), "1 and 7 disagree")
        assertEquals(Value.Known(7, INT), array.element(1), "7 and 7 agree, so this stays known")
    }

    private data class CaughtThrowCase(
        val name: String,
        val array: Any?,
        val arrayType: String,
        val index: Int,
        val value: Any?,
        val valueType: String,
        val exceptionType: String,
        val message: String? = null,
    )

    /**
     * Definite throws caught by `putWithCatch()V`'s `Ljava/lang/Exception;` handler: one child (the handler),
     * no fall-through, no register declared assigned, and no effect because nothing is stored. A primitive-typed
     * array register reads as null whatever it holds (section "Reading a register as a reference"), and NPE
     * precedes the bounds check. The shape refusal (one side primitive, one reference; **VERIFIER**-invalid, so
     * an engine choice) precedes null and bounds, reads only the value's declared type, and holds for an
     * unprovable array because the tag's component bounds the real one from above.
     */
    @TestFactory
    fun definiteThrowsAreCaughtAndStoreNothing(): List<DynamicTest> = listOf(
        CaughtThrowCase("null array", null, "[I", 0, 0, "I", NPE),
        CaughtThrowCase("null array before a bad index", null, "[I", 10, 0x42, "I", NPE, "Attempt to write to null array"),
        CaughtThrowCase("integral zero array", 0, "I", 0, 0x42, "I", NPE),
        CaughtThrowCase("non-zero primitive array", 5, "I", 0, 0x42, "I", NPE),
        CaughtThrowCase("index past the end", IntArray(5), "[I", 10, 0, "I", AIOOBE),
        CaughtThrowCase("negative index", intArrayOf(1, 2, 3), "[I", -1, 0x42, "I", AIOOBE, "length=3; index=-1"),
        CaughtThrowCase(
            "reference into int array", IntArray(5), "[I", 0, "wrong type", "Ljava/lang/String;", ASE,
            "java.lang.String cannot be stored in an array of type int[]",
        ),
        CaughtThrowCase("shape refusal before a bad index", IntArray(5), "[I", 10, "wrong type", "Ljava/lang/String;", ASE),
        CaughtThrowCase("shape refusal of an unknown value", IntArray(5), "[I", 10, CoreState.UNKNOWN, "Ljava/lang/String;", ASE),
        CaughtThrowCase("shape refusal on an unknown array", CoreState.UNKNOWN, "[I", 0, "wrong type", "Ljava/lang/String;", ASE),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val initial = CoreState().apply {
                setRegisters(0, case.array, case.arrayType, 1, case.index, "I", 2, case.value, case.valueType)
            }
            CoreTester.testCaught(
                CLASS_NAME, "putWithCatch()V", initial,
                exceptionType = case.exceptionType,
                message = case.message,
                caughtInRegister = 0,
                unreachedAddress = 2,
            )
            val graph = CoreTester.execute(CLASS_NAME, "putWithCatch()V", initial)
            assertEquals(1, graph.nodePile(0).first().children.size, "a definite caught throw has one child: the handler")
            assertEquals(emptySet<Int>(), graph.registersAssigned(0), "an aput declares no register assigned")
            assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0), "a definite throw stores nothing")
        }
    }

    /**
     * Bug class: an unknown value read as an unanswerable store check. Both sides are primitive, so the store is
     * permitted, nothing forks, and the handler is dead on this input.
     */
    @Test
    fun unknownValueItemMakesOnlyItsOwnElementUnknownAndThrowsNothing() {
        val initial = CoreState().apply { setRegisters(0, IntArray(5), "[I", 1, 0, "I", 2, CoreState.UNKNOWN, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "putWithCatch()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "a provable in-range store of an unknown value throws nothing")
        assertEquals(1, graph.nodePile(0).first().children.size, "one successor: the fall-through")
        assertTrue(graph.wasReached(2), "the store's own fall-through")
        assertFalse(graph.wasReached(4), "and the handler is dead on this input")
        val array = storedArray("putWithCatch()V", initial)
        assertEquals(Value.Unknown(INT), array.element(0), "the written slot")
        assertEquals(List(4) { Value.Known(0, INT) }, (1 until 5).map { array.element(it) }, "and only the written slot")
    }

    private data class ForkCase(
        val name: String,
        val method: String,
        val array: Any?,
        val arrayType: String,
        val index: Any,
        val indexType: String,
        val value: Any?,
        val valueType: String,
        val escaping: List<String>,
        val storeIsFeasible: Boolean,
        val effect: SideEffectLevel? = null,
    )

    /**
     * Fork shape on handler-free methods, where every throw escapes. An unprovable array forks NPE and AIOOBE
     * before index and value are consulted; a provable array with an unknown index forks only the AIOOBE; a
     * zero-length array or a `Float` index read by its bits (`1.9f` is 1072902963) is a definite AIOOBE. For a
     * verified `aput-object`, NPE and AIOOBE strictly precede the assignability ASE ([JVMS] `aastore`). The ASE
     * forks when assignability is unanswerable: an unknown value's tag or a non-final component tag only bounds
     * the runtime class, an absent class is ambiguity rather than a host failure, and a register with no array
     * type has no component. Storing null, or into a final component tag, never throws ASE, and no store whose
     * value and component are both primitive throws ASE whatever the mnemonic.
     */
    @TestFactory
    fun storesForkOnlyFeasibleExceptions(): List<DynamicTest> = listOf(
        ForkCase("unknown non-array-tagged array", "put()V", CoreState.UNKNOWN, "Ljava/lang/Object;", 0, "I", 0x5, "I", listOf(NPE, AIOOBE), true),
        ForkCase("provable array, unknown index", "put()V", intArrayOf(1, 2), "[I", CoreState.UNKNOWN, "I", 7, "I", listOf(AIOOBE), true),
        ForkCase("zero-length array, unknown index", "put()V", intArrayOf(), "[I", CoreState.UNKNOWN, "I", 7, "I", listOf(AIOOBE), false),
        ForkCase("float index read by its bits", "put()V", intArrayOf(1, 7), "[I", 1.9f, "F", 7, "I", listOf(AIOOBE), false),
        ForkCase("long into double array", "putWide()V", DoubleArray(1), "[D", 0, "I", 5L, "J", emptyList(), true),
        ForkCase(
            "bounds before assignability", "putObject()V", arrayOf("Caladan"), "[Ljava/lang/String;", 10, "I",
            42, "Ljava/lang/Integer;", listOf(AIOOBE), false,
        ),
        ForkCase(
            "null array before assignability", "putObject()V", null, "[Ljava/lang/String;", 0, "I",
            42, "Ljava/lang/Integer;", listOf(NPE), false,
        ),
        ForkCase(
            "unknown value with a non-assignable tag", "putObject()V", arrayOfNulls<String>(1), "[Ljava/lang/String;", 0, "I",
            CoreState.UNKNOWN, "Ljava/lang/Object;", listOf(ASE), true,
        ),
        ForkCase(
            "unloadable component tag", "putObject()V", CoreState.UNKNOWN, "[Lspine/absent;", 0, "I",
            "a string", "Ljava/lang/String;", listOf(NPE, AIOOBE, ASE), true,
        ),
        ForkCase(
            "direct string, unknown index", "putObject()V", arrayOfNulls<String>(2), "[Ljava/lang/String;", CoreState.UNKNOWN, "I",
            "a string", "Lspine/absent;", listOf(AIOOBE), true,
        ),
        ForkCase(
            "unknown array, definitely unassignable value", "putObject()V", CoreState.UNKNOWN, "[Ljava/lang/String;", 0, "I",
            42, "Ljava/lang/Integer;", listOf(NPE, AIOOBE, ASE), false, SideEffectLevel.NONE,
        ),
        ForkCase(
            "unknown array, non-final component tag", "putObject()V", CoreState.UNKNOWN, "[Ljava/lang/Object;", 0, "I",
            ARRAKIS, "Ljava/lang/String;", listOf(NPE, AIOOBE, ASE), true,
        ),
        ForkCase(
            "unknown array, final component tag", "putObject()V", CoreState.UNKNOWN, "[Ljava/lang/String;", 0, "I",
            ARRAKIS, "Ljava/lang/String;", listOf(NPE, AIOOBE), true,
        ),
        ForkCase(
            "no array type, reference value", "putObject()V", CoreState.UNKNOWN, "Ljava/lang/Object;", 0, "I",
            "a string", "Ljava/lang/String;", listOf(NPE, AIOOBE, ASE), true,
        ),
        ForkCase(
            "no array type, null value", "putObject()V", CoreState.UNKNOWN, "Ljava/lang/Object;", 0, "I",
            null, "Ljava/lang/String;", listOf(NPE, AIOOBE), true,
        ),
    ).map { case ->
        DynamicTest.dynamicTest(case.name) {
            val graph = CoreTester.execute(CLASS_NAME, case.method, CoreState().apply {
                setRegisters(0, case.array, case.arrayType, 1, case.index, case.indexType, 2, case.value, case.valueType)
            })
            assertEquals(CompletionStatus.Complete, graph.completion(), "guest throws are not truncations")
            assertEquals(case.escaping, CoreTester.escapingTypes(graph), "escaping throws, in transition order")
            assertEquals(case.storeIsFeasible, graph.wasReached(2), "whether the store itself is feasible")
            case.effect?.let { assertEquals(it, graph.explored.sideEffectLevel(0)) }
        }
    }

    /** A speculative bounds exception carries no message, because ART's text names the unknown index. */
    @Test
    fun aSpeculativeBoundsExceptionOmitsItsMessage() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1, 2), "[I", 1, CoreState.UNKNOWN, "I", 2, 7, "I")
        })
        assertEquals(listOf<String?>(null), escapingMessages(graph))
    }

    /**
     * A definite bounds exception leaves every element unchanged. Read off the throwing node's frame on the
     * handler-free `put()V`, because `move-exception` in `putWithCatch()V` would overwrite the array register.
     */
    @Test
    fun aProvableIndexOutsideTheBoundsThrowsDefinitelyAndWritesNoElement() {
        val seed = CoreState().apply { setRegisters(0, intArrayOf(1, 2, 3), "[I", 1, 10, "I", 2, 0x42, "I") }
        val graph = CoreTester.execute(CLASS_NAME, "put()V", seed)
        assertEquals(CompletionStatus.Complete, graph.completion(), "a guest throw is an outcome, not a truncation")
        assertEquals(listOf(AIOOBE), CoreTester.escapingTypes(graph))
        assertFalse(graph.wasReached(2), "a definite bounds exception does not also explore the fall-through")
        val frame = graph.nodePile(0).first().frame
        val ref = (frame.peek(0) as Value.Known).raw as HeapRef
        val array = frame.heapObject(ref) as HeapObject.TrackedArray
        assertEquals(
            listOf(Value.Known(1, INT), Value.Known(2, INT), Value.Known(3, INT)), array.elements(),
            "a throwing store leaves every element as it found it",
        )
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(0), "nothing was stored")
    }

    /**
     * An unassigned array register is `Top` and still forks both exceptions, and the op writes no register.
     * Only `frame.peek` distinguishes "written with `Top`" from "never written".
     */
    @Test
    fun anArrayRegisterWithNoTypeAtAllStillForksBothExceptionsAndIsNeverWritten() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState())
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertNull(graph.nodePile(0).first().frame.peek(0), "an aput writes no register at all, not even Top")
        assertEquals(listOf(NPE, AIOOBE), CoreTester.escapingTypes(graph))
    }

    /**
     * Bug class: forking exceptions for a value the engine proved non-null. A `Known` reference with no tracked
     * array is the not-tracked case (**VERIFIER**): nothing throws, the register keeps its value because `aput`
     * writes memory, not the register, and the unmodellable write is accounted for as `STRONG`.
     */
    @Test
    fun aKnownNonArrayIsProvablyNonNullSoNothingThrowsAndNoElementIsWritten() {
        val initial = CoreState().apply {
            setRegisters(0, "not an array at all", "Ljava/lang/String;", 1, 0, "I", 2, 0x42, "I")
        }
        val expected = CoreState().apply { setRegisters(0, "not an array at all", "Ljava/lang/String;") }
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
        val graph = CoreTester.execute(CLASS_NAME, "put()V", initial)
        assertEquals(1, graph.nodePile(0).first().children.size, "exactly one successor; both throws are infeasible")
        assertTrue(CoreTester.escapingTypes(graph).isEmpty(), "a non-null reference with no length throws nothing")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the store writes into something the engine cannot name",
        )
    }

    /** A direct `String` value proves the store type-checks despite an unloadable value tag. */
    @Test
    fun aKnownNonArrayWithDirectStringHasNoSpeculativeStoreFailure() {
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", CoreState().apply {
            setRegisters(0, "not an array at all", "[Ljava/lang/String;", 1, 0, "I", 2, "a string", "Lspine/absent;")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "nothing about this input is provable enough to refuse outright")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "the store still writes somewhere the engine cannot name",
        )
    }

    /**
     * Bug class: an operand transposition in `buildAPut` (`vAA` value, `vBB` array, `vCC` index). Distinct
     * values in all three registers make each swap change the stored array.
     */
    @Test
    fun theThreeOperandsAreNotInterchangeable() {
        val initial = CoreState().apply { setRegisters(0, intArrayOf(0x11, 0x22, 0x33), "[I", 1, 1, "I", 2, 0x44, "I") }
        val expected = CoreState().apply {
            setRegisters(0, intArrayOf(0x11, 0x44, 0x33), "[I", 1, 1, "I", 2, 0x44, "I")
        }
        CoreTester.test(CLASS_NAME, "put()V", initial, expected)
    }

    /** All three operands are read and no register is assigned: an `aput` stores into the array object. */
    @Test
    fun theStoreReadsAllThreeOperandsAndAssignsNothing() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1), "[I", 1, 0, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(setOf(0, 1, 2), graph.registersRead(0), "value, array and index are all read")
        assertEquals(emptySet<Int>(), graph.registersAssigned(0), "and an aput assigns no register at all")
    }

    /**
     * Bug class: a completed run reporting no side effect for a store through an unprovable array, which is
     * every store into a caller-supplied array. The write may change any object, so the store's address and the
     * method are `STRONG`; the level stays on the store's own address.
     */
    @Test
    fun anUnprovableArrayMakesTheStoreStrongEvenThoughTheRunCompletes() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, CoreState.UNKNOWN, "[I", 1, 0, "I", 2, 5, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        val trusted = graph.trusted()
        assertNotNull(trusted, "a completed run hands out a trusted view")
        assertEquals(
            SideEffectLevel.STRONG, graph.explored.sideEffectLevel(0),
            "a store through an unprovable array reference is an unmodellable write",
        )
        assertEquals(SideEffectLevel.STRONG, trusted!!.methodSideEffectLevel(), "so the method is not safe to delete")
        assertEquals(SideEffectLevel.NONE, graph.explored.sideEffectLevel(2), "and the effect does not leak to successors")
    }

    /** A provable store into a tracked array is modelled exactly but observable through an alias: `WEAK`. */
    @Test
    fun aProvableStoreIntoATrackedArrayDeclaresAnObservableWeakEffect() {
        val graph = CoreTester.execute(CLASS_NAME, "put()V", CoreState().apply {
            setRegisters(0, intArrayOf(1), "[I", 1, 0, "I", 2, 7, "I")
        })
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0), "an alias can observe the tracked write")
        assertEquals(SideEffectLevel.WEAK, graph.trusted()!!.methodSideEffectLevel())
    }

    /**
     * `aput-byte` of a `Float` stores the low byte of its bits: `5.9f` is `0x40BCCCCD`, so the slot holds -51.
     * **VERIFIER**-excluded input; the effect stays `WEAK` because the slot is tracked.
     */
    @Test
    fun aFloatValueStoresTheLowByteOfItsBits() {
        val initial = CoreState().apply { setRegisters(0, ByteArray(1), "[B", 1, 0, "I", 2, 5.9f, "F") }
        val array = storedArray("putByte()V", initial)
        assertEquals(Value.Known((-51).toByte(), BYTE), array.element(0))
        assertEquals(
            SideEffectLevel.WEAK, CoreTester.execute(CLASS_NAME, "putByte()V", initial).explored.sideEffectLevel(0),
            "the heap models the store, but an alias can observe it",
        )
    }

    /**
     * `aastore` tests the array's actual component ([JVMS]), so a `String[]` behind an `[Ljava/lang/Object;`
     * tag refuses an `Integer`. The message is the DEVICE-recorded `Fx_AputObjectIncompatible` form.
     */
    @Test
    fun aCovariantlyTaggedArrayStillThrowsForAnIncompatibleComponent() {
        CoreTester.testThrows(
            CLASS_NAME, "putObject()V",
            CoreState().apply {
                setRegisters(0, arrayOf("Caladan"), "[Ljava/lang/Object;", 1, 0, "I", 2, 42, "Ljava/lang/Integer;")
            },
            exceptionType = ASE,
            message = "java.lang.Integer cannot be stored in an array of type java.lang.String[]",
        )
    }

    /**
     * Integral zero is null only under the `OBJECT` suffix, so a plain `aput` of `Known(0, "I")` into a reference
     * array is a shape refusal (**VERIFIER**-invalid), not a null store.
     */
    @Test
    fun aPlainAputOfDalviksZeroIntoAReferenceArrayIsRefusedRatherThanStoredAsNull() {
        CoreTester.testThrows(
            CLASS_NAME, "put()V",
            CoreState().apply {
                setRegisters(0, arrayOfNulls<String>(1), "[Ljava/lang/String;", 1, 0, "I", 2, 0, "I")
            },
            exceptionType = ASE,
            message = "int cannot be stored in an array of type java.lang.String[]",
        )
    }

    /** A direct immutable reference uses its exact runtime class instead of an imprecise declared tag. */
    @Test
    fun directImmutableRuntimeTypeMakesStoreDefiniteDespiteUnloadableTag() {
        val initial = CoreState().apply {
            setRegisters(0, arrayOfNulls<String>(1), "[Ljava/lang/String;", 1, 0, "I", 2, "a string", "Lspine/absent;")
        }
        val graph = CoreTester.execute(CLASS_NAME, "putObject()V", initial)
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.wasReached(2), "the store's path is feasible")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(graph))
        assertEquals(
            Value.Known("a string", STRING), storedArray("putObject()V", initial).element(0),
            "the runtime String class proves that this store succeeds",
        )
    }

    /**
     * Bug class: the store check reading the value's supertype tag instead of the tracked object's own type,
     * which refuses a store `aastore` permits. The fixture round-trips an `aput_test` instance through an
     * `Object[]` so its register is tagged `Ljava/lang/Object;`, then stores it into `[Laput_test;`.
     */
    @Test
    fun aTrackedObjectsOwnTypeBeatsItsSupertypeTagInTheStoreCheck() {
        val graph = CoreTester.execute(CLASS_NAME, "putRoundTrippedInstanceIntoOwnTypeArray()V")
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(
            graph.outcomes.filterIsInstance<Outcome.Threw>().isEmpty(),
            "aastore tests the actual class, so no ArrayStoreException is feasible: ${graph.outcomes}",
        )
        val trusted = graph.trusted()
        assertNotNull(trusted, "a Complete exploration must yield a trusted view")
        val arrayConsensus = trusted!!.terminatingConsensus(0)
        assertTrue(arrayConsensus is Value.Known && (arrayConsensus as Value.Known).raw is HeapRef)
        val array = trusted.terminatingHeapObject((arrayConsensus as Value.Known).raw as HeapRef).agreed()
        assertTrue(array is HeapObject.TrackedArray, "no array behind v0; got $array")
        val element = (array as HeapObject.TrackedArray).element(0)
        assertTrue(
            element is Value.Known && (element as Value.Known).raw is HeapRef,
            "the element must be the stored tracked object: $element",
        )
        assertEquals(
            VirtualType.of(CLASS_NAME), (element as Value.Known).type,
            "the stored element's declared type comes from the array's component",
        )
        val stored = trusted.terminatingHeapObject(element.raw as HeapRef).agreed()
        assertTrue(
            stored is HeapObject.TrackedObject && stored.type == VirtualType.of(CLASS_NAME),
            "the object behind the round trip is the allocation itself: $stored",
        )
    }

    /** The tracked array in v0 on every returning path of [method]. */
    private fun storedArray(method: String, initial: CoreState): HeapObject.TrackedArray {
        val trusted = CoreTester.execute(CLASS_NAME, method, initial).trusted()
        assertNotNull(trusted, "the run must complete before its consensus is trusted")
        val ref = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        return trusted.terminatingHeapObject(ref).agreed() as HeapObject.TrackedArray
    }

    /** Every escaping throw's message, read off the `aput` node's ending frame, where the throwable was made. */
    private fun escapingMessages(graph: ExecutionGraph): List<String?> =
        graph.outcomes.filterIsInstance<Outcome.Threw>().map { threw ->
            val ref = (threw.exception as Value.Known).raw as HeapRef
            val frame = graph.nodePile(0).first().terminalFrame
            val obj = frame.heapObject(ref)
            assertTrue(obj is HeapObject.GuestThrowable, "no synthesized throwable at $ref; got $obj")
            (frame.heap.hostObjectAt(ref) as Throwable).message
        }

    /**
     * Stores via `putObject()V` and compares the inner array's elements through heap consensus: the engine
     * stores a `HeapRef`, never the live host array, so `CoreState`'s host-array expectation cannot express it.
     */
    private fun assertStoredArrayElements(initial: CoreState, storedElementType: String, innerElements: List<Value>) {
        val trusted = CoreTester.execute(CLASS_NAME, "putObject()V", initial).trusted()
        assertNotNull(trusted, "the run must complete before its consensus is trusted")
        val outerRef = (trusted!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        val outer = trusted.terminatingHeapObject(outerRef).agreed() as HeapObject.TrackedArray
        assertEquals(1, outer.length, "outer array length")
        val stored = outer.element(0)
        assertTrue(stored is Value.Known, "element 0 should be a definite reference, but was $stored")
        assertEquals(
            VirtualType.of(storedElementType), (stored as Value.Known).type,
            "the stored element's declared type comes from the outer array object, not the value register's tag",
        )
        val innerRef = stored.raw
        assertTrue(innerRef is HeapRef, "a stored array must be a HeapRef, never a live host array: $innerRef")
        val inner = trusted.terminatingHeapObject(innerRef as HeapRef).agreed()
        assertTrue(inner is HeapObject.TrackedArray, "no array behind element 0; got $inner")
        assertEquals(innerElements, (inner as HeapObject.TrackedArray).elements())
    }

    private companion object {
        private const val CLASS_NAME = "Laput_test;"
        private const val NPE = "Ljava/lang/NullPointerException;"
        private const val AIOOBE = "Ljava/lang/ArrayIndexOutOfBoundsException;"
        private const val ASE = "Ljava/lang/ArrayStoreException;"
        private const val ARRAKIS = "Arrakis, Dune, desert planet..."
        private val BOXED_ZERO: Any = Integer.valueOf(0)
        private val INT = VirtualType.of("I")
        private val BYTE = VirtualType.of("B")
        private val STRING = VirtualType.of("Ljava/lang/String;")
    }
}
