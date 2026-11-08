package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test

/** Host graphs deeper than the host thread stack must clone and compare without a fatal error. */
class HostGraphDepthTest {
    private val comparator = HostGraphComparator()

    @Test
    fun deeplyNestedListsCloneAndCompareWithoutExhaustingTheHostStack() {
        // Bug class: clone and compare recursed once per nesting level, so a deep chain threw
        // StackOverflowError, which the session rethrows as fatal and which ends the whole run.
        // Oracle: a clone is an exact copy (Equal); changing only the innermost list makes it Different.
        val innermost = ArrayList<Any>()
        var outermost: ArrayList<Any> = innermost
        repeat(DEPTH - 1) { outermost = arrayListOf(outermost) }
        val state = HostGraphState.empty().addRoots(listOf(outermost)).state

        val clone = cloneHostGraph(state)

        assertEquals(DEPTH, clone.nodeCount)
        var clonedInnermost = clone.objectAt(HostGraphNodeId(0)) as ArrayList<Any>
        while (clonedInnermost.isNotEmpty()) clonedInnermost = clonedInnermost.single() as ArrayList<Any>
        assertNotSame(innermost, clonedInnermost)
        assertEquals(HostGraphComparison.Equal, comparator.compare(state, clone))

        clonedInnermost.add("changed")
        assertEquals(HostGraphComparison.Different, comparator.compare(state, clone))
    }

    @Test
    fun deepCustomFieldChainsCloneAndCompareWithoutExhaustingTheHostStack() {
        // Bug class: as above, through instance fields rather than container contents.
        // Oracle: a clone is an exact copy (Equal); changing only the last link's value makes it Different.
        class Link(var value: Int, var next: Link?)

        val last = Link(0, null)
        var head = last
        repeat(DEPTH - 1) { head = Link(it + 1, head) }
        val state = HostGraphState.empty().addRoots(listOf(head)).state

        val clone = cloneHostGraph(state)

        assertEquals(DEPTH, clone.nodeCount)
        var clonedLast = clone.objectAt(HostGraphNodeId(0)) as Link
        while (clonedLast.next != null) clonedLast = clonedLast.next!!
        assertNotSame(last, clonedLast)
        assertEquals(HostGraphComparison.Equal, comparator.compare(state, clone))

        clonedLast.value = -1
        assertEquals(HostGraphComparison.Different, comparator.compare(state, clone))
    }

    private companion object {
        const val DEPTH = 100_000
    }
}
