package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The provability helpers row by row. `core/op/IfOpTest` covers them through a real opcode; this covers the
 * rows an `if` cannot reach.
 */
class ProvableTest {
    private val int = VirtualType.of("I")
    private val string = VirtualType.of("Ljava/lang/String;")
    private val float = VirtualType.of("F")

    /** A `Float`/`Double` raw is unprovable: its box's bits are not what the original register held. */
    @Test
    fun everyLatticePointHasExactlyOneProvableBitsAnswer() {
        assertEquals(ProvableInt.Exactly(0), provableInt(Value.Known(null, int)), "null reads as 0")
        assertEquals(ProvableInt.Exactly(0), provableInt(Value.Known(null, string)), "whatever the tag")
        assertEquals(ProvableInt.Exactly(5), provableInt(Value.Known(5, int)))
        assertEquals(ProvableInt.Exactly(-1), provableInt(Value.Known(-1, int)), "read signed, as Dalvik does")
        assertEquals(ProvableInt.Exactly(1), provableInt(Value.Known(true, VirtualType.of("Z"))))
        assertEquals(ProvableInt.Exactly(65), provableInt(Value.Known('A', VirtualType.of("C"))))
        assertEquals(
            ProvableInt.Exactly(1), provableInt(Value.Known(1L, VirtualType.of("J"))),
            "a Long read at 32 bits keeps the low word, as a device does",
        )
        assertEquals(ProvableInt.NonZero, provableInt(Value.Known(HeapRef(3), VirtualType.of("[B"))))
        assertEquals(ProvableInt.NonZero, provableInt(Value.Known("live", string)))
        assertEquals(
            ProvableInt.Unprovable, provableInt(Value.Known(1f, float)),
            "a Float raw is unprovable, not read as its bits 0x3f800000",
        )
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Known(1.0, VirtualType.of("D"))))
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Unknown(int)))
        assertEquals(ProvableInt.Unprovable, provableInt(Value.Top))
    }

    /**
     * The last two rows are ones no `if` reaches: [IfOp] calls [sameReference] only when both operands are
     * [ProvableInt.NonZero], so a not-`Known` operand never gets there.
     *
     * [IfOp]: [org.cf.smalivm.core.op.IfOp]
     */
    @Test
    fun sameReferenceIsPointerIdentityAcrossBothRepresentations() {
        val obj = "one object"
        assertTrue(sameReference(Value.Known(obj, string), Value.Known(obj, string))!!, "host identity")
        assertFalse(
            sameReference(Value.Known(obj, string), Value.Known(StringBuilder(obj).toString(), string))!!,
            "equal content is not identity",
        )
        assertTrue(
            sameReference(Value.Known(HeapRef(7), int), Value.Known(HeapRef(7), string))!!,
            "a tracked object's identity is its heap id and the tag is ignored, so a check-cast re-tag " +
                "stays one pointer",
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
     * [Value.agreesWith] answers consensus agreement, not pointer identity: it compares content with host `==`
     * and requires equal tags. Routing `if-ne` through it would leave a feasible path unexplored.
     * [sameReferenceIsPointerIdentityAcrossBothRepresentations] holds the opposite answers for the same inputs.
     */
    @Test
    fun agreesWithComparesContentAndTagsRatherThanPointers() {
        val a = "content"
        val b = StringBuilder(a).toString()
        assertTrue(Value.Known(a, string).agreesWith(Value.Known(b, string)), "equal content agrees")

        val ref = HeapRef(1)
        assertFalse(
            Value.Known(ref, string).agreesWith(Value.Known(ref, VirtualType.of("Ljava/lang/Object;"))),
            "one pointer under two tags does not agree",
        )
    }

    /**
     * A declared tag is only an upper bound on the class of the object a register holds, so a tag that fails
     * the target proves nothing when the object's own class cannot be determined.
     */
    @Test
    fun aKnownReferenceWhoseClassCannotBeDeterminedIsUnprovableRatherThanNever() {
        val types = object : TypeHierarchy {
            override fun isInstanceOf(candidate: VirtualType, target: VirtualType) =
                candidate == target || target == VirtualType.OBJECT
            override fun isSubtypeOf(candidate: VirtualType, ancestor: VirtualType): Boolean = error("castOutcome asks only isInstanceOf")
            override fun superclassOf(type: VirtualType): VirtualType? = error("castOutcome asks only isInstanceOf")
        }
        val heapRefWithNoObject = Value.Known(HeapRef(7), VirtualType.OBJECT)
        assertEquals(CastOutcome.Unprovable, castOutcome(heapRefWithNoObject, string, types) { null })
        assertEquals(
            CastOutcome.Definitely, castOutcome(heapRefWithNoObject, string, types) { string },
            "once the object's class is known, it decides",
        )
    }
}
