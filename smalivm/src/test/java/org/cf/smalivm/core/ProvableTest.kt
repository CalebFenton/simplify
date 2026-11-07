package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The provability rule's two helpers, row by row -- the level at which the rule is ONE thing rather
 * than a table repeated per op. `core/op/IfOpTest` covers them through a real opcode; this covers the
 * rows an `if` cannot reach and the row ORDER that makes the rest correct.
 */
class ProvableTest {
    private val int = VirtualType.of("I")
    private val string = VirtualType.of("Ljava/lang/String;")
    private val float = VirtualType.of("F")

    /**
     * Every row of [provableInt]. A `Float`/`Double` raw is unprovable rather than read by its bits, and it
     * must not reach the host-object row, which would report it as a live pointer.
     */
    @Test
    fun everyLatticePointHasExactlyOneProvableBitsAnswer() {
        assertEquals(ProvableInt.Exactly(0), provableInt(Value.Known(null, int)), "SPEC: (Object) null == (int) 0")
        assertEquals(ProvableInt.Exactly(0), provableInt(Value.Known(null, string)), "and the tag is irrelevant")
        assertEquals(ProvableInt.Exactly(5), provableInt(Value.Known(5, int)))
        assertEquals(ProvableInt.Exactly(-1), provableInt(Value.Known(-1, int)), "read SIGNED, as Dalvik does")
        assertEquals(ProvableInt.Exactly(1), provableInt(Value.Known(true, VirtualType.of("Z"))))
        assertEquals(ProvableInt.Exactly(65), provableInt(Value.Known('A', VirtualType.of("C"))))
        assertEquals(
            ProvableInt.Exactly(1), provableInt(Value.Known(1L, VirtualType.of("J"))),
            "a Long read at 32 bits is bit truncation, which is what a device does",
        )
        assertEquals(ProvableInt.NonZero, provableInt(Value.Known(HeapRef(3), VirtualType.of("[B"))))
        assertEquals(ProvableInt.NonZero, provableInt(Value.Known("live", string)))
        assertEquals(
            ProvableInt.Unprovable, provableInt(Value.Known(1f, float)),
            "the Float row must come BEFORE the host-object row, or this answers NonZero",
        )
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Known(1.0, VirtualType.of("D"))))
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Unknown(int)))
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Top))
    }

    /**
     * [sameReference]'s four rows. The last two are the ones no `if` reaches -- [IfOp] gates the call on
     * both operands being [ProvableInt.NonZero], so a not-`Known` operand never gets there -- and they
     * are pinned here rather than left as untested insurance, because the helper is specified as total
     * over the lattice for the callers Plan 9 adds.
     *
     * [IfOp]: [org.cf.smalivm.core.op.IfOp]
     */
    @Test
    fun sameReferenceIsPointerIdentityAcrossBothRepresentations() {
        val obj = "one object"
        assertTrue(sameReference(Value.Known(obj, string), Value.Known(obj, string))!!, "host identity")
        assertFalse(
            sameReference(Value.Known(obj, string), Value.Known(StringBuilder(obj).toString(), string))!!,
            "equal CONTENT is not identity -- this is the row Value.agreesWith gets wrong",
        )
        assertTrue(
            sameReference(Value.Known(HeapRef(7), int), Value.Known(HeapRef(7), string))!!,
            "a tracked object's identity is its heap id, and the declared tag is ignored entirely -- " +
                "which is what keeps a check-cast re-tag from reporting one pointer as two",
        )
        assertFalse(sameReference(Value.Known(HeapRef(7), int), Value.Known(HeapRef(8), int))!!)
        assertFalse(
            sameReference(Value.Known(HeapRef(7), int), Value.Known(obj, string))!!,
            "a HeapRef and a host object are never the same guest object",
        )
        assertNull(sameReference(Value.Unknown(string), Value.Known(obj, string)), "unanswerable, not false")
        assertNull(sameReference(Value.Known(obj, string), Value.Top))
    }

    /**
     * **`sameReference` is NOT `Value.agreesWith`, in both directions**, which is why they are two
     * functions with two owners (spec §2 principle 4) rather than one with a flag.
     *
     * `agreesWith` answers *semantic agreement for consensus*: it uses host `==`, so two equal-content
     * `String`s agree (routing `if-ne` through it leaves a feasible path unexplored on a `Complete`
     * run), and it refuses when the declared tags differ, so one pointer under two tags disagrees
     * (which is what Task 9's `check-cast` re-tag produces).
     */
    @Test
    fun agreesWithAnswersADifferentQuestionInBothDirections() {
        val a = "content"
        val b = StringBuilder(a).toString()
        assertTrue(Value.Known(a, string).agreesWith(Value.Known(b, string)), "host == says these agree")
        assertFalse(sameReference(Value.Known(a, string), Value.Known(b, string))!!, "identity says they do not")

        val ref = HeapRef(1)
        assertFalse(
            Value.Known(ref, string).agreesWith(Value.Known(ref, VirtualType.of("Ljava/lang/Object;"))),
            "agreesWith refuses one pointer under two tags",
        )
        assertTrue(
            sameReference(Value.Known(ref, string), Value.Known(ref, VirtualType.of("Ljava/lang/Object;")))!!,
            "sameReference ignores the tag, because if-eq compares pointers and not types",
        )
    }

    /**
     * A declared tag is only an upper bound on the class of the object a register holds, so a tag that fails
     * the target proves nothing when the object's own class cannot be determined. Answering `Never` here made
     * `check-cast` throw a definite `ClassCastException` at an object that may well be a `String`.
     */
    @Test
    fun aKnownReferenceWhoseClassCannotBeDeterminedIsUnprovableRatherThanNever() {
        val types = InstanceOfTest { candidate, target -> candidate == target || target == VirtualType.OBJECT }
        val heapRefWithNoObject = Value.Known(HeapRef(7), VirtualType.OBJECT)
        assertEquals(CastOutcome.Unprovable, castOutcome(heapRefWithNoObject, string, types) { null })
        assertEquals(
            CastOutcome.Definitely, castOutcome(heapRefWithNoObject, string, types) { string },
            "the CONTROL: once the object's class is known, it decides",
        )
    }
}
