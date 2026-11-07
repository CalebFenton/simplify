package org.cf.smalivm.core

import java.util.ArrayDeque
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.TreeMap
import java.util.TreeSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/** Exact graph comparison required before an unchanged selected host call can discard its detached graph. */
class HostGraphComparatorTest {
    private val comparator = HostGraphComparator()

    @Test
    fun hostGraphStateIndexesNestedAliasesAndCyclesOnce() {
        class Node(var next: Node? = null)

        val child = Node()
        child.next = child
        val roots = listOf(arrayListOf(child), arrayListOf(child))

        val state = HostGraphState.fromRoots(roots)

        assertNotNull(state.nodeIdOf(roots[0]))
        assertNotNull(state.nodeIdOf(roots[1]))
        assertNotNull(state.nodeIdOf(child))
        assertEquals(3, state.nodeCount)
    }

    @Test
    fun equalStringBuildersCompareEqualWithoutUsingIdentityEquals() {
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(StringBuilder("same"), StringBuilder("same")),
        )
    }

    @Test
    fun changedStringBuilderComparesDifferent() {
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(StringBuilder("before"), StringBuilder("after")),
        )
    }

    @Test
    fun stringBuilderCapacityOnlyMutationComparesDifferent() {
        val left = StringBuilder("same")
        val right = StringBuilder("same").apply { ensureCapacity(128) }

        assertEquals(HostGraphComparison.Different, comparator.compare(left, right))
    }

    @Test
    fun orderedListsAndDequesCompareInIterationOrder() {
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(listOf("a", "b"), listOf("a", "b")),
        )
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(listOf("a", "b"), listOf("b", "a")),
        )
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(ArrayDeque(listOf("a", "b")), ArrayDeque(listOf("a", "b"))),
        )
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(ArrayDeque(listOf("a", "b")), ArrayDeque(listOf("b", "a"))),
        )
    }

    @Test
    fun setsCompareWithoutTrustingElementHashOrIterationOrder() {
        val left = LinkedHashSet<Any>(listOf(StringBuilder("a"), StringBuilder("b")))
        val right = LinkedHashSet<Any>(listOf(StringBuilder("b"), StringBuilder("a")))

        assertEquals(HostGraphComparison.Equal, comparator.compare(left, right))
    }

    @Test
    fun changedSetMemberComparesDifferent() {
        val left = linkedSetOf<Any>(StringBuilder("a"), StringBuilder("b"))
        val right = linkedSetOf<Any>(StringBuilder("a"), StringBuilder("c"))

        assertEquals(HostGraphComparison.Different, comparator.compare(left, right))
    }

    @Test
    fun unorderedCollisionBucketsBacktrackAfterFailedCandidates() {
        class Box(var value: Int) {
            override fun hashCode(): Int = 0
        }

        val leftSet = linkedSetOf<Any>(Box(1), Box(2))
        val rightSet = linkedSetOf<Any>(Box(2), Box(1))
        assertEquals(HostGraphComparison.Equal, comparator.compare(leftSet, rightSet))

        val leftMap = HashMap<Any, Any>().apply {
            put(Box(1), Box(10))
            put(Box(2), Box(20))
        }
        val rightMap = HashMap<Any, Any>().apply {
            put(Box(2), Box(20))
            put(Box(1), Box(10))
        }
        assertEquals(HostGraphComparison.Equal, comparator.compare(leftMap, rightMap))
    }

    @Test
    fun mapsCompareKeysAndValuesAsPairsWithoutUsingCustomEquals() {
        val left = HashMap<Any, Any>().apply {
            put(StringBuilder("a"), StringBuilder("one"))
            put(StringBuilder("b"), StringBuilder("two"))
        }
        val right = HashMap<Any, Any>().apply {
            put(StringBuilder("b"), StringBuilder("two"))
            put(StringBuilder("a"), StringBuilder("one"))
        }

        assertEquals(HostGraphComparison.Equal, comparator.compare(left, right))
    }

    @Test
    fun changedMapValueAndSwappedPairingsCompareDifferent() {
        fun map(first: String, second: String) = HashMap<Any, Any>().apply {
            put(StringBuilder("a"), StringBuilder(first))
            put(StringBuilder("b"), StringBuilder(second))
        }

        assertEquals(HostGraphComparison.Different, comparator.compare(map("one", "two"), map("one", "changed")))
        assertEquals(HostGraphComparison.Different, comparator.compare(map("one", "two"), map("two", "one")))
    }

    @Test
    fun linkedHashMapOrderIsObservable() {
        val left = LinkedHashMap<String, Int>().apply {
            put("a", 1)
            put("b", 2)
        }
        val right = LinkedHashMap<String, Int>().apply {
            put("b", 2)
            put("a", 1)
        }

        assertEquals(HostGraphComparison.Different, comparator.compare(left, right))
    }

    @Test
    fun sortedCollectionsComparePublicComparatorState() {
        class DirectionComparator(var descending: Boolean) : Comparator<String> {
            override fun compare(left: String, right: String): Int =
                if (descending) right.compareTo(left) else left.compareTo(right)
        }

        val leftMap = TreeMap<String, Int>(DirectionComparator(false)).apply { put("a", 1) }
        val equalMap = TreeMap<String, Int>(DirectionComparator(false)).apply { put("a", 1) }
        val changedMap = TreeMap<String, Int>(DirectionComparator(true)).apply { put("a", 1) }
        assertEquals(HostGraphComparison.Equal, comparator.compare(leftMap, equalMap))
        assertEquals(HostGraphComparison.Different, comparator.compare(leftMap, changedMap))

        val leftSet = TreeSet<String>(DirectionComparator(false)).apply { add("a") }
        val changedSet = TreeSet<String>(DirectionComparator(true)).apply { add("a") }
        assertEquals(HostGraphComparison.Different, comparator.compare(leftSet, changedSet))
    }

    @Test
    fun primitiveAndObjectArraysCompareRecursively() {
        assertEquals(HostGraphComparison.Equal, comparator.compare(intArrayOf(1, 2), intArrayOf(1, 2)))
        assertEquals(HostGraphComparison.Different, comparator.compare(intArrayOf(1, 2), intArrayOf(1, 3)))
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(arrayOf(StringBuilder("x")), arrayOf(StringBuilder("x"))),
        )
    }

    @Test
    fun cyclesTerminateAndCompareTheirFields() {
        class Node(var value: Int) {
            var next: Node? = null
        }

        val left = Node(1).also { it.next = it }
        val equal = Node(1).also { it.next = it }
        val changed = Node(2).also { it.next = it }

        assertEquals(HostGraphComparison.Equal, comparator.compare(left, equal))
        assertEquals(HostGraphComparison.Different, comparator.compare(left, changed))
    }

    @Test
    fun aliasSplitAndAliasMergeAreDifferent() {
        class Pair(var first: Any, var second: Any)

        val leftShared = StringBuilder("x")
        val rightFirst = StringBuilder("x")
        val rightSecond = StringBuilder("x")
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(Pair(leftShared, leftShared), Pair(rightFirst, rightSecond)),
        )
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(Pair(rightFirst, rightSecond), Pair(leftShared, leftShared)),
        )
    }

    @Test
    fun aliasSplitAndMergeAcrossSeparateGraphRootsAreDifferent() {
        class Root(var child: StringBuilder)

        val leftShared = StringBuilder("x")
        val left = HostGraphState.fromRoots(
            listOf(
                Root(leftShared),
                Root(leftShared),
                Root(StringBuilder("x")),
                Root(StringBuilder("x")),
            ),
        )
        val rightShared = StringBuilder("x")
        val right = HostGraphState.fromRoots(
            listOf(
                Root(StringBuilder("x")),
                Root(StringBuilder("x")),
                Root(rightShared),
                Root(rightShared),
            ),
        )

        assertEquals(left.nodeCount, right.nodeCount)
        assertEquals(HostGraphComparison.Different, comparator.compare(left, right))
        assertEquals(HostGraphComparison.Different, comparator.compare(right, left))
    }

    @Test
    fun equalButDistinctRootsRemainDistinct() {
        class Pair(var first: Any, var second: Any)

        val left = Pair(StringBuilder("x"), StringBuilder("x"))
        val right = Pair(StringBuilder("x"), StringBuilder("x"))

        assertEquals(HostGraphComparison.Equal, comparator.compare(left, right))
    }

    @Test
    fun customEqualsCannotHideMutableFieldChanges() {
        class Misleading(var value: Int) {
            override fun equals(other: Any?): Boolean = other is Misleading
            override fun hashCode(): Int = 1
        }

        assertEquals(HostGraphComparison.Different, comparator.compare(Misleading(1), Misleading(2)))
    }

    @Test
    fun inheritedInstanceFieldsParticipateInComparison() {
        open class Base(var inherited: Int)
        class Child(inherited: Int, var own: Int) : Base(inherited)

        assertEquals(HostGraphComparison.Equal, comparator.compare(Child(1, 2), Child(1, 2)))
        assertEquals(HostGraphComparison.Different, comparator.compare(Child(1, 2), Child(3, 2)))
    }

    @Test
    fun customCollectionSubclassFieldsCannotBeIgnored() {
        class TaggedList(var tag: Int) : ArrayList<String>()
        class TaggedMap(var tag: Int) : LinkedHashMap<String, String>()

        val left = TaggedList(1).apply { add("same") }
        val right = TaggedList(2).apply { add("same") }

        assertEquals(HostGraphComparison.Different, comparator.compare(left, right))
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(
                TaggedMap(1).apply { put("key", "value") },
                TaggedMap(2).apply { put("key", "value") },
            ),
        )
    }

    @Test
    fun hostGraphScanningIncludesCustomCollectionSubclassFields() {
        class ExtendedList(var sideState: Any) : ArrayList<String>()

        val sideState = StringBuilder("side")
        val list = ExtendedList(sideState).apply { add("content") }
        val state = HostGraphState.fromRoots(listOf(list))

        assertNotNull(state.nodeIdOf(sideState))
    }

    @Test
    fun graphIdentityIndexNeverCallsCustomEqualsOrHashCode() {
        class HostileIdentity {
            override fun equals(other: Any?): Boolean = error("equals must not run")
            override fun hashCode(): Int = error("hashCode must not run")
        }

        val raw = HostileIdentity()
        val state = HostGraphState.fromRoots(listOf(raw))
        val admission = state.addRoots(listOf(raw))

        assertEquals(state.nodeIdOf(raw), admission.rootIds.single())
        assertEquals(1, admission.state.nodeCount)
    }

    @Test
    fun nestedSchedulerObjectsAreRejectedAtHostGraphAdmission() {
        val schedulerValues = listOf<Any>(
            Value.Known(1, VirtualType.of("I")),
            HeapRef(1),
            HeapObject.TrackedArray(VirtualType.of("[I"), arrayOf(Value.Known(0, VirtualType.of("I")))),
        )

        schedulerValues.forEach { schedulerValue ->
            org.junit.jupiter.api.assertThrows<HostGraphOwnershipException> {
                HostGraphState.fromRoots(listOf(arrayListOf(schedulerValue)))
            }
        }
    }

    @Test
    fun floatAndDoubleBitsCompareExactly() {
        class Floating(var floatValue: Float, var doubleValue: Double)

        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(Floating(0.0f, 0.0), Floating(-0.0f, 0.0)),
        )
        assertEquals(
            HostGraphComparison.Different,
            comparator.compare(Floating(0.0f, 0.0), Floating(0.0f, -0.0)),
        )
    }

    @Test
    fun inaccessibleJdkStateIsUnprovableRatherThanEqual() {
        assertEquals(
            HostGraphComparison.Unprovable,
            comparator.compare(Thread.currentThread(), Thread.currentThread()),
        )
    }
}
