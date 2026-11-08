package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Value agreement and the null predicates. Tracked references agree by heap id, untracked raws by value and
 * type, and nothing agrees through Unknown or Top.
 */
class ValueTest {
    private val int = VirtualType.of("I")
    private val obj = VirtualType.OBJECT
    private val str = VirtualType.of("Ljava/lang/String;")

    @TestFactory
    fun agreement(): List<DynamicTest> {
        val unknown = Value.Unknown(int)
        return listOf(
            Triple("equal primitives", Value.Known(5, int) to Value.Known(5, int), true),
            Triple("different primitives", Value.Known(5, int) to Value.Known(6, int), false),
            // Agreeing across a width boundary would let a constant propagate from a short into an int.
            Triple("same value, different width", Value.Known(5, int) to Value.Known(5, VirtualType.of("S")), false),
            Triple("two nulls", Value.Known(null, str) to Value.Known(null, str), true),
            Triple("one allocation seen twice", Value.Known(HeapRef(1), obj) to Value.Known(HeapRef(1), obj), true),
            Triple("distinct allocations", Value.Known(HeapRef(1), obj) to Value.Known(HeapRef(2), obj), false),
            Triple("an unknown with itself", unknown to unknown, false),
            Triple("two unknowns", unknown to Value.Unknown(int), false),
            Triple("a known with an unknown", Value.Known(5, int) to unknown, false),
            Triple("top with itself", Value.Top to Value.Top, false),
            Triple("top with a known", Value.Top to Value.Known(5, int), false),
            Triple("a heap id with an equal boxed int", Value.Known(HeapRef(5), obj) to Value.Known(5, obj), false),
        ).map { (name, pair, agrees) ->
            DynamicTest.dynamicTest(name) { assertEquals(agrees, pair.first.agreesWith(pair.second)) }
        }
    }

    /**
     * `isDalvikNull` asks whether a raw is an integral zero: the Dalvik bytecode reference's
     * `(Object) null == (int) 0` ("General design"), narrowed to JLS §4.2's integral types. No `const` opcode
     * produces a `Z`/`F`/`D`-tagged zero, so `false`, `0.0f`, and `0.0` are excluded even though their bits are zero.
     */
    @Test
    fun everyIntegralZeroIsDalviksNullAndNothingElseIs() {
        for (zero in listOf<Any>(0.toByte(), 0.toShort(), 0, 0L, '\u0000')) {
            assertTrue(Value.Known(zero, int).isDalvikNull, "$zero (${zero.javaClass.simpleName}) is Dalvik's null")
        }
        for (nonZero in listOf<Any>(1.toByte(), 1.toShort(), 1, 1L, 'a', -1)) {
            assertFalse(Value.Known(nonZero, int).isDalvikNull, "$nonZero (${nonZero.javaClass.simpleName}) is not")
        }
        for (excluded in listOf<Any>(false, 0.0f, 0.0)) {
            assertFalse(Value.Known(excluded, int).isDalvikNull, "$excluded (${excluded.javaClass.simpleName}) is excluded")
        }
        assertFalse(Value.Known("0", str).isDalvikNull)
        assertFalse(Value.Known(HeapRef(0), obj).isDalvikNull, "heap id 0 is an identity, not a value")
        assertFalse(Value.Unknown(int).isDalvikNull, "may be zero is not is zero")
        assertFalse(Value.Top.isDalvikNull)
        // A null raw is `isNull`'s case; the two predicates answer different questions.
        assertFalse(Value.Known(null, str).isDalvikNull)
        assertTrue(Value.Known(null, str).isNull)
        assertFalse(Value.Known(0, int).isNull)
        assertFalse(Value.Unknown(str).isNull, "may be null is not is null")
    }

    /**
     * `readsAsNullReference` is what every dereferencing op consults. On ART's switch interpreter a primitive
     * write clears the register's reference slot, so any primitive-typed Known reads as null whatever its bits;
     * a non-zero primitive is VERIFIER tier (see "ART has two interpreter tiers" in `docs/DALVIK-SEMANTICS.md`).
     * This is why `Z`/`F`/`D` raws read as null here while `isDalvikNull` excludes them. Unknown and Top are
     * unproven and never answer true.
     */
    @Test
    fun everyPrimitiveTypedKnownReadsAsANullReferenceAndNoReferenceDoes() {
        val primitives = listOf("I", "J", "Z", "B", "C", "S", "F", "D").map { VirtualType.of(it) }
        for (type in primitives) {
            assertTrue(Value.Known(0, type).readsAsNullReference, "a zero under ${type.descriptor} reads as null")
            assertTrue(Value.Known(5, type).readsAsNullReference, "and so does a non-zero under ${type.descriptor}")
        }
        // The same raws on both predicates, so their divergence is the claim.
        for (excluded in listOf<Any>(false, true, 0.0f, 1.5f, 0.0, -3.0)) {
            assertTrue(Value.Known(excluded, VirtualType.of("Z")).readsAsNullReference, "$excluded reads as null")
            assertFalse(Value.Known(excluded, int).isDalvikNull, "$excluded is not an integral zero")
        }
        // References: only a null raw. A boxed number is still a live reference.
        assertTrue(Value.Known(null, str).readsAsNullReference, "a null raw under a reference type")
        assertFalse(Value.Known(0, str).readsAsNullReference, "a boxed Integer(0) under a reference type is a live pointer")
        assertFalse(Value.Known("text", str).readsAsNullReference, "a live String does not read as null")
        assertFalse(Value.Known(HeapRef(0), obj).readsAsNullReference, "heap id 0 is an identity, not a null")
        assertFalse(Value.Known(HeapRef(7), VirtualType.of("[I")).readsAsNullReference, "nor is an array ref")
        assertFalse(Value.Unknown(int).readsAsNullReference, "an Unknown(I) is not proved primitive-valued")
        assertFalse(Value.Unknown(str).readsAsNullReference)
        assertFalse(Value.Top.readsAsNullReference)
    }
}
