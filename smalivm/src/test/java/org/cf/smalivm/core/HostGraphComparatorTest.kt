package org.cf.smalivm.core

import java.time.Duration
import java.util.ArrayDeque
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.TreeMap
import java.util.TreeSet
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTimeoutPreemptively
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

        val state = graph(*roots.toTypedArray())

        assertNotNull(state.nodeIdOf(roots[0]))
        assertNotNull(state.nodeIdOf(roots[1]))
        assertNotNull(state.nodeIdOf(child))
        assertEquals(3, state.nodeCount)
    }

    @Test
    fun equalStringBuildersCompareEqualWithoutUsingIdentityEquals() {
        assertEquals(
            HostGraphComparison.Equal,
            compareGraphs(StringBuilder("same"), StringBuilder("same")),
        )
    }

    @Test
    fun changedStringBuilderComparesDifferent() {
        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(StringBuilder("before"), StringBuilder("after")),
        )
    }

    @Test
    fun aNodeTheCallOnlyAddedIsNotAChange() {
        // A call that reads an argument admits it after the checkpoint; the checkpoint's own objects are unchanged.
        val before = graph(StringBuilder("same"))
        val after = graph(StringBuilder("same"), StringBuilder("argument"))

        assertEquals(HostGraphComparison.Equal, comparator.compare(before, after))
    }

    @Test
    fun anAddedNodeReachedFromACheckpointObjectIsAChange() {
        val before = graph(arrayListOf<Any>())
        val after = graph(arrayListOf<Any>(StringBuilder("added")))

        assertEquals(HostGraphComparison.Different, comparator.compare(before, after))
    }

    @Test
    fun stringBuilderCapacityOnlyMutationComparesDifferent() {
        val left = StringBuilder("same")
        val right = StringBuilder("same").apply { ensureCapacity(128) }

        assertEquals(HostGraphComparison.Different, compareGraphs(left, right))
    }

    @Test
    fun orderedListsAndDequesCompareInIterationOrder() {
        assertEquals(
            HostGraphComparison.Equal,
            compareGraphs(arrayListOf("a", "b"), arrayListOf("a", "b")),
        )
        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(arrayListOf("a", "b"), arrayListOf("b", "a")),
        )
        assertEquals(
            HostGraphComparison.Equal,
            compareGraphs(ArrayDeque(listOf("a", "b")), ArrayDeque(listOf("a", "b"))),
        )
        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(ArrayDeque(listOf("a", "b")), ArrayDeque(listOf("b", "a"))),
        )
    }

    @Test
    fun setsCompareWithoutTrustingElementHashOrIterationOrder() {
        val leftA = StringBuilder("a")
        val leftB = StringBuilder("b")
        val rightA = StringBuilder("a")
        val rightB = StringBuilder("b")
        val left = LinkedHashSet<Any>(listOf(leftA, leftB))
        val right = LinkedHashSet<Any>(listOf(rightB, rightA))

        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(graph(left, leftA, leftB), graph(right, rightA, rightB)),
        )
    }

    @Test
    fun changedSetMemberComparesDifferent() {
        val left = linkedSetOf<Any>(StringBuilder("a"), StringBuilder("b"))
        val right = linkedSetOf<Any>(StringBuilder("a"), StringBuilder("c"))

        assertEquals(HostGraphComparison.Different, compareGraphs(left, right))
    }

    @Test
    fun unorderedCollisionBucketsBacktrackAfterFailedCandidates() {
        class Box(var value: Int) {
            override fun hashCode(): Int = 0
        }

        val left1 = Box(1)
        val left2 = Box(2)
        val right1 = Box(1)
        val right2 = Box(2)
        val leftSet = linkedSetOf<Any>(left1, left2)
        val rightSet = linkedSetOf<Any>(right2, right1)
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(graph(leftSet, left1, left2), graph(rightSet, right1, right2)),
        )

        val leftKey1 = Box(1)
        val leftValue1 = Box(10)
        val leftKey2 = Box(2)
        val leftValue2 = Box(20)
        val rightKey1 = Box(1)
        val rightValue1 = Box(10)
        val rightKey2 = Box(2)
        val rightValue2 = Box(20)
        val leftMap = HashMap<Any, Any>().apply {
            put(leftKey1, leftValue1)
            put(leftKey2, leftValue2)
        }
        val rightMap = HashMap<Any, Any>().apply {
            put(rightKey2, rightValue2)
            put(rightKey1, rightValue1)
        }
        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(
                graph(
                    leftMap,
                    entryOf(leftMap, leftKey1),
                    leftKey1,
                    leftValue1,
                    entryOf(leftMap, leftKey2),
                    leftKey2,
                    leftValue2,
                ),
                graph(
                    rightMap,
                    entryOf(rightMap, rightKey1),
                    rightKey1,
                    rightValue1,
                    entryOf(rightMap, rightKey2),
                    rightKey2,
                    rightValue2,
                ),
            ),
        )
    }

    @Test
    fun sameShapedSetMembersCannotForceFactorialMatching() {
        // Bug class: every member fingerprints alike, so matching backtracked through every pairing.
        // A pairing fails only when it reaches the second member holding the shared builder, so an
        // unbounded search visits a factorial number of partial pairings before giving up.
        // Oracle: after the call two right members alias one builder and no left members do, so the
        // graphs are not isomorphic; a bounded search may answer Different or Unprovable, never Equal.
        val set = HashSet<Any>()
        repeat(MEMBERS) { set.add(arrayListOf(StringBuilder("x"), StringBuilder("x"))) }
        val before = graph(set)
        val after = cloneHostGraph(before)
        @Suppress("UNCHECKED_CAST")
        val members = (after.objectAt(HostGraphNodeId(0)) as Set<MutableList<Any>>).toList()
        members[0][0] = members[1][0]

        // A termination guard, not a cost bound: the bounded search ends in well under a second, and a search
        // through every pairing of the members runs for hours.
        val result = assertTimeoutPreemptively(Duration.ofSeconds(60)) { comparator.compare(before, after) }

        assertNotEquals(HostGraphComparison.Equal, result)
    }

    @Test
    fun largeSetsOfSameShapedMembersInAnotherIterationOrderStillCompareEqual() {
        // Bug class: a work bound small enough to give up on ordinary sets whose members share a fingerprint.
        // Oracle: the call only reinserted the same members in reverse order, which a set does not observe.
        val set = LinkedHashSet<Any>((0 until 500).map { arrayListOf(StringBuilder(it.toString())) })
        val before = graph(set)
        val after = cloneHostGraph(before)
        @Suppress("UNCHECKED_CAST")
        val clonedSet = after.objectAt(HostGraphNodeId(0)) as MutableSet<Any>
        val members = clonedSet.toList()
        clonedSet.clear()
        members.asReversed().forEach(clonedSet::add)

        assertEquals(HostGraphComparison.Equal, comparator.compare(before, after))
    }

    @Test
    fun mapsCompareKeysAndValuesAsPairsWithoutUsingCustomEquals() {
        val leftA = StringBuilder("a")
        val leftOne = StringBuilder("one")
        val leftB = StringBuilder("b")
        val leftTwo = StringBuilder("two")
        val rightA = StringBuilder("a")
        val rightOne = StringBuilder("one")
        val rightB = StringBuilder("b")
        val rightTwo = StringBuilder("two")
        val left = HashMap<Any, Any>().apply {
            put(leftA, leftOne)
            put(leftB, leftTwo)
        }
        val right = HashMap<Any, Any>().apply {
            put(rightB, rightTwo)
            put(rightA, rightOne)
        }

        assertEquals(
            HostGraphComparison.Equal,
            comparator.compare(
                graph(left, entryOf(left, leftA), leftA, leftOne, entryOf(left, leftB), leftB, leftTwo),
                graph(right, entryOf(right, rightA), rightA, rightOne, entryOf(right, rightB), rightB, rightTwo),
            ),
        )
    }

    @Test
    fun changedMapValueAndSwappedPairingsCompareDifferent() {
        fun map(first: String, second: String) = HashMap<Any, Any>().apply {
            put(StringBuilder("a"), StringBuilder(first))
            put(StringBuilder("b"), StringBuilder(second))
        }

        assertEquals(HostGraphComparison.Different, compareGraphs(map("one", "two"), map("one", "changed")))
        assertEquals(HostGraphComparison.Different, compareGraphs(map("one", "two"), map("two", "one")))
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

        assertEquals(HostGraphComparison.Different, compareGraphs(left, right))
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
        assertEquals(HostGraphComparison.Equal, compareGraphs(leftMap, equalMap))
        assertEquals(HostGraphComparison.Different, compareGraphs(leftMap, changedMap))

        val leftSet = TreeSet<String>(DirectionComparator(false)).apply { add("a") }
        val changedSet = TreeSet<String>(DirectionComparator(true)).apply { add("a") }
        assertEquals(HostGraphComparison.Different, compareGraphs(leftSet, changedSet))
    }

    @Test
    fun primitiveAndObjectArraysCompareRecursively() {
        assertEquals(HostGraphComparison.Equal, compareGraphs(intArrayOf(1, 2), intArrayOf(1, 2)))
        assertEquals(HostGraphComparison.Different, compareGraphs(intArrayOf(1, 2), intArrayOf(1, 3)))
        assertEquals(
            HostGraphComparison.Equal,
            compareGraphs(arrayOf(StringBuilder("x")), arrayOf(StringBuilder("x"))),
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

        assertEquals(HostGraphComparison.Equal, compareGraphs(left, equal))
        assertEquals(HostGraphComparison.Different, compareGraphs(left, changed))
    }

    @Test
    fun aliasSplitAndAliasMergeAreDifferent() {
        class Pair(var first: Any, var second: Any)

        // Both graphs keep the same node count, so only the identity bindings can tell them apart.
        val shared = StringBuilder("x")
        val beforeSplit = graph(Pair(shared, shared))
        val afterSplit = cloneHostGraph(beforeSplit)
        (afterSplit.objectAt(HostGraphNodeId(0)) as Pair).second = StringBuilder("x")
        assertEquals(beforeSplit.nodeCount, afterSplit.nodeCount)
        assertEquals(HostGraphComparison.Different, comparator.compare(beforeSplit, afterSplit))

        val beforeMerge = graph(Pair(StringBuilder("x"), StringBuilder("x")))
        val afterMerge = cloneHostGraph(beforeMerge)
        val mergedPair = afterMerge.objectAt(HostGraphNodeId(0)) as Pair
        mergedPair.second = mergedPair.first
        assertEquals(beforeMerge.nodeCount, afterMerge.nodeCount)
        assertEquals(HostGraphComparison.Different, comparator.compare(beforeMerge, afterMerge))
    }

    @Test
    fun aliasSplitAndMergeAcrossSeparateGraphRootsAreDifferent() {
        class Root(var child: StringBuilder)

        val leftShared = StringBuilder("x")
        val left = graph(
            Root(leftShared),
            Root(leftShared),
            Root(StringBuilder("x")),
            Root(StringBuilder("x")),
        )
        val rightShared = StringBuilder("x")
        val right = graph(
            Root(StringBuilder("x")),
            Root(StringBuilder("x")),
            Root(rightShared),
            Root(rightShared),
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

        assertEquals(HostGraphComparison.Equal, compareGraphs(left, right))
    }

    @Test
    fun customEqualsCannotHideMutableFieldChanges() {
        class Misleading(var value: Int) {
            override fun equals(other: Any?): Boolean = other is Misleading
            override fun hashCode(): Int = 1
        }

        assertEquals(HostGraphComparison.Different, compareGraphs(Misleading(1), Misleading(2)))
    }

    @Test
    fun inheritedInstanceFieldsParticipateInComparison() {
        open class Base(var inherited: Int)
        class Child(inherited: Int, var own: Int) : Base(inherited)

        assertEquals(HostGraphComparison.Equal, compareGraphs(Child(1, 2), Child(1, 2)))
        assertEquals(HostGraphComparison.Different, compareGraphs(Child(1, 2), Child(3, 2)))
    }

    @Test
    fun customCollectionSubclassFieldsCannotBeIgnored() {
        class TaggedList(var tag: Int) : ArrayList<String>()
        class TaggedMap(var tag: Int) : LinkedHashMap<String, String>()

        val left = TaggedList(1).apply { add("same") }
        val right = TaggedList(2).apply { add("same") }

        assertEquals(HostGraphComparison.Different, compareGraphs(left, right))
        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(
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
        val state = graph(list)

        assertNotNull(state.nodeIdOf(sideState))
    }

    @Test
    fun graphIdentityIndexNeverCallsCustomEqualsOrHashCode() {
        class HostileIdentity {
            override fun equals(other: Any?): Boolean = error("equals must not run")
            override fun hashCode(): Int = error("hashCode must not run")
        }

        val raw = HostileIdentity()
        val state = graph(raw)
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
                graph(arrayListOf(schedulerValue))
            }
        }
    }

    @Test
    fun floatAndDoubleBitsCompareExactly() {
        class Floating(var floatValue: Float, var doubleValue: Double)

        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(Floating(0.0f, 0.0), Floating(-0.0f, 0.0)),
        )
        assertEquals(
            HostGraphComparison.Different,
            compareGraphs(Floating(0.0f, 0.0), Floating(0.0f, -0.0)),
        )
    }

    @Test
    fun inaccessibleJdkStateIsUnprovableRatherThanEqual() {
        // AtomicInteger is a JDK class but not a recognized container, so its fields are not walked even
        // when they hold equal values.
        assertEquals(
            HostGraphComparison.Unprovable(),
            compareGraphs(AtomicInteger(7), AtomicInteger(7)),
        )
    }

    /**
     * A graph whose roots take node ids in argument order. The comparator pairs node i on the left with node i
     * on the right, so tests whose two sides discover members in different orders list those members as roots.
     */
    private fun graph(vararg roots: Any): HostGraphState = HostGraphState.empty().addRoots(roots.asList()).state

    private fun compareGraphs(left: Any, right: Any): HostGraphComparison =
        comparator.compare(graph(left), graph(right))

    private fun entryOf(map: Map<Any, Any>, key: Any): Map.Entry<Any, Any> = map.entries.first { it.key === key }

    private companion object {
        const val MEMBERS = 12
    }
}
