package org.cf.smalivm.core

import java.util.Date
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * The heap's three load-bearing properties (spec §4.2): allocation gives a fresh identity, a fork
 * isolates writes, and a fork preserves aliasing. The id-uniqueness test is the soundness one -- see
 * its own comment.
 */
class HeapStoreTest {

    private val INT = VirtualType.of("I")
    private val INT_ARRAY = VirtualType.of("[I")
    private val OBJECT_ARRAY = VirtualType.of("[Ljava/lang/Object;")

    @Test
    fun aFreshArrayHoldsJavasDefaultForItsComponentType() {
        val heap = HeapStore(HeapIds())
        val ref = heap.allocateArray(INT_ARRAY, 2)
        val array = heap.arrayAt(ref)!!
        assertEquals(2, array.length)
        assertEquals(Value.Known(0, INT), array.element(0))
        assertEquals(Value.Known(0, INT), array.element(1))
    }

    @Test
    fun aFreshReferenceArrayHoldsDefinitelyNullElements() {
        // Known(null, T) is DEFINITELY null (spec §4.1), which is what a Java reference array holds --
        // not Unknown, which would mean "may be null" and would lose a provable fact.
        val heap = HeapStore(HeapIds())
        val array = heap.arrayAt(heap.allocateArray(OBJECT_ARRAY, 1))!!
        assertEquals(Value.Known(null, VirtualType.of("Ljava/lang/Object;")), array.element(0))
    }

    @Test
    fun twoPathsAllocatingSeparatelyGetDistinctIdsAndDoNotAgree() {
        // THE SOUNDNESS TEST. Both paths fork from the same parent heap, so a per-copy counter would
        // hand both the same id, `agreesWith` would call them one object, and consensus would report a
        // Known array where two genuinely distinct allocations exist -- authorizing a strategy to
        // eliminate one. HeapIds is deliberately NOT copied for exactly this reason.
        val parent = HeapStore(HeapIds())
        val pathA = parent.copy()
        val pathB = parent.copy()
        val a = Value.Known(pathA.allocateArray(INT_ARRAY, 1), INT_ARRAY)
        val b = Value.Known(pathB.allocateArray(INT_ARRAY, 1), INT_ARRAY)
        assertNotEquals(a, b)
        assertTrue(!a.agreesWith(b), "two separate allocations must never agree: $a vs $b")
        assertEquals(Value.Unknown(INT_ARRAY), join(a, b, CoreTester.classIndex))
    }

    @Test
    fun aCopyIsolatesElementWrites() {
        val heap = HeapStore(HeapIds())
        val ref = heap.allocateArray(INT_ARRAY, 1)
        val forked = heap.copy()
        forked.setArrayElement(ref, 0, Value.Known(7, INT))
        assertEquals(Value.Known(0, INT), heap.arrayAt(ref)!!.element(0))
        assertEquals(Value.Known(7, INT), forked.arrayAt(ref)!!.element(0))
    }

    @Test
    fun aCopyPreservesAliasingWithinItsOwnPath() {
        // One allocation reachable from two registers is ONE object, and a fork must not silently
        // split it -- the audit's cloned-array-diverges defect. Same id in the copy, same object.
        val heap = HeapStore(HeapIds())
        val ref = heap.allocateArray(INT_ARRAY, 1)
        val forked = heap.copy()
        forked.setArrayElement(ref, 0, Value.Known(7, INT))
        assertEquals(Value.Known(7, INT), forked.objectAt(ref).let { (it as HeapObject.TrackedArray).element(0) })
    }

    @Test
    fun anUnallocatedRefIsAbsentRatherThanFabricated() {
        assertNull(HeapStore(HeapIds()).objectAt(HeapRef(99)))
        assertNull(HeapStore(HeapIds()).arrayAt(HeapRef(99)))
    }

    @Test
    fun aSynthesizedThrowableKeepsItsHostInstanceAcrossACopy() {
        // A throwable is never mutated after synthesis, so copy() may share the instance -- and must,
        // or a handler would see a different exception object than the throw produced.
        val heap = HeapStore(HeapIds())
        val type = VirtualType.of("Ljava/lang/ArithmeticException;")
        val host = ArithmeticException("/ by zero")
        val ref = heap.allocateThrowable(type, host)
        val copied = heap.copy().objectAt(ref) as HeapObject.GuestThrowable
        assertTrue(heap.copy().hostObjectAt(ref) === host)
        assertEquals(type, copied.type)
    }

    @Test
    fun allocatingFromExplicitElementsKeepsThemVerbatim() {
        // filled-new-array and the test harness's array seeding both need this overload: the elements
        // are already lattice points and must not be re-defaulted.
        val heap = HeapStore(HeapIds())
        val elements = arrayOf<Value>(Value.Known(1, INT), Value.Unknown(INT))
        val array = heap.arrayAt(heap.allocateArray(INT_ARRAY, elements))!!
        assertEquals(Value.Known(1, INT), array.element(0))
        assertEquals(Value.Unknown(INT), array.element(1))
    }

    @Test
    fun twoPathsAllocatingObjectsOfOneTypeGetDistinctIdsAndDoNotAgree() {
        // The object twin of twoPathsAllocatingSeparatelyGetDistinctIdsAndDoNotAgree, with the same stake:
        // `allocateObject` must mint from the shared HeapIds counter, or two forked paths' unrelated
        // `new-instance Lfoo;` share one id, `agreesWith` calls them one object, and consensus reports a single
        // Known where two allocations exist. Identity is the id, never the type.
        val type = VirtualType.of("Lnew_instance_test;")
        val parent = HeapStore(HeapIds())
        val pathA = parent.copy()
        val pathB = parent.copy()
        val a = Value.Known(pathA.allocateObject(type), type)
        val b = Value.Known(pathB.allocateObject(type), type)
        assertNotEquals(a, b)
        assertTrue(!a.agreesWith(b), "two separate allocations must never agree: $a vs $b")
        assertEquals(Value.Unknown(type), join(a, b, CoreTester.classIndex))
    }

    @Test
    fun aFreshObjectIsUninitializedAndACopySharesItsImmutableVersion() {
        // Set at allocation, cleared only when an `<init>` whose receiver is this ref returns. The copy shares
        // the immutable object version under the same id; a later field or initialization write replaces the
        // object only in the writing store.
        val heap = HeapStore(HeapIds())
        val type = VirtualType.of("Lnew_instance_test;")
        val ref = heap.allocateObject(type)
        val original = heap.objectAt(ref) as HeapObject.TrackedObject
        assertEquals(type, original.type)
        assertTrue(original.uninitialized, "a fresh new-instance allocation is uninitialized")
        val copied = heap.copy().objectAt(ref) as HeapObject.TrackedObject
        assertEquals(type, copied.type)
        assertTrue(copied.uninitialized)
        assertTrue(copied === original, "an unchanged immutable object version must be shared across forks")
    }

    @Test
    fun copiedObjectFieldsAreIsolatedUnderTheSameHeapId() {
        val type = VirtualType.of("Liput_test;")
        val field = "Liput_test;->myInt:I"
        val parent = HeapStore(HeapIds())
        val ref = parent.allocateObject(type, mapOf(field to Value.Known(0, INT)))
        val first = parent.copy()
        val sibling = parent.copy()

        assertTrue(first.setField(ref, field, Value.Known(7, INT)))
        assertEquals(Value.Known(7, INT), first.getField(ref, field))
        assertEquals(
            Value.Known(0, INT),
            sibling.getField(ref, field),
            "a write through one fork must not mutate its sibling's object under the shared id",
        )
    }

    @Test
    fun arraysCannotEnterHostObjectAndKnownImportsRequireAnExistingNode() {
        val heap = HeapStore(HeapIds())

        assertThrows<IllegalArgumentException> {
            heap.allocateHostObject(VirtualType.of("Ljava/lang/Object;"), intArrayOf(1))
        }
        assertThrows<IllegalArgumentException> {
            heap.importHostNode(
                HostGraphNodeId(999),
                VirtualType.of("Ljava/lang/Object;"),
                StringBuilder("missing"),
            )
        }
    }

    @Test
    fun unprovableFinalizationKeepsTheDetachedHostGraphRoot() {
        val heap = HeapStore(HeapIds())
        val type = VirtualType.of("Ljava/util/Date;")
        val ref = heap.allocateHostObject(type, Date(1_234L))
        val roots = listOf(Value.Known(ref, type))
        val originalRoot = heap.hostGraphVersionToken()
        val checkpoint = heap.detachHostGraph(roots)
        val detachedRoot = heap.hostGraphVersionToken()
        assertNotSame(originalRoot, detachedRoot)

        val result = heap.finalizeHostGraph(checkpoint, roots)

        assertEquals(HostGraphComparison.Unprovable(), result)
        assertSame(detachedRoot, heap.hostGraphVersionToken())
        assertNotSame(originalRoot, heap.hostGraphVersionToken())
    }

    @Test
    fun aFinalizationThatThrowsIsUnprovableAndCarriesTheFailure() {
        // Bug class: a host failure while re-walking the graph after a selected call is turned into Unprovable
        // and dropped, so a host-library or cloning bug is invisible. Oracle: the Java object's own accessor
        // throws EntryWalkFailure during the walk; that throwable, and no other, is the result's cause.
        val heap = HeapStore(HeapIds())
        val ref = heap.allocateHostObject(VirtualType.of("Ljava/lang/Object;"), BreakableEntry())
        val roots = listOf(Value.Known(ref, VirtualType.of("Ljava/lang/Object;")))
        val checkpoint = heap.detachHostGraph(roots)
        (heap.hostObjectAt(ref) as BreakableEntry).broken = true

        val result = heap.finalizeHostGraph(checkpoint, roots)

        assertTrue((result as HostGraphComparison.Unprovable).cause is EntryWalkFailure, "cause: ${result.cause}")
    }

    private class EntryWalkFailure : RuntimeException("entry state cannot be read")

    /** A Java object whose state walk fails once [broken] is set, as a host library's accessor can. */
    private class BreakableEntry : Map.Entry<Any?, Any?> {
        var broken = false
        override val key: Any? get() = if (broken) throw EntryWalkFailure() else null
        override val value: Any? get() = null
    }
}
