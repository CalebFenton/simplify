package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MethodControlFlowTest {
    private lateinit var classIndex: ClassIndex

    @BeforeAll
    fun load(@TempDir directory: File) {
        File(directory, "Flow.smali").writeText(FLOW)
        classIndex = ClassIndex.open(directory)
    }

    private fun flow(descriptor: String): MethodControlFlow? =
        classIndex.requireMethodView("LFlow;", descriptor).controlFlow

    private fun successors(flow: MethodControlFlow, index: Int) = flow.normalSuccessors(index).toSortedSet()

    @Test
    fun branchAndSwitchEdgesFollowTheirTargetsAndFallThrough() {
        val flow = requireNotNull(flow("branches(I)I"))
        // Code addresses 0, 1, 3, 4, 7, 8, 9; the packed-switch payload at 10 is not an instruction.
        assertEquals(listOf(0, 1, 3, 4, 7, 8, 9), List(flow.size) { flow.addressAt(it) })
        assertEquals(sortedSetOf(1), successors(flow, 0))
        assertEquals(sortedSetOf(2, 3), successors(flow, 1))
        assertEquals(sortedSetOf(3), successors(flow, 2))
        assertEquals(sortedSetOf(4, 5), successors(flow, 3))
        assertEquals(sortedSetOf<Int>(), successors(flow, 6))
        assertEquals(sortedSetOf(1, 2), flow.predecessors(3).toSortedSet())
        assertEquals(sortedSetOf(3, 4), flow.predecessors(5).toSortedSet())
        assertTrue((0 until flow.size).all(flow::isReachable))
    }

    @Test
    fun fallthroughPredecessorExistsOnlyWhenFallingThroughIsTheSoleEntry() {
        val flow = requireNotNull(flow("branches(I)I"))
        assertNull(flow.fallthroughPredecessor(0), "the method entry")
        assertEquals(0, flow.fallthroughPredecessor(1))
        assertEquals(1, flow.fallthroughPredecessor(2), "the fall-through arm of if-eqz")
        assertNull(flow.fallthroughPredecessor(3), "joined by the if-eqz branch")
        assertEquals(3, flow.fallthroughPredecessor(4), "the fall-through arm of packed-switch")
        assertNull(flow.fallthroughPredecessor(5), "a switch case target")

        val intoHandler = requireNotNull(flow("fallsIntoHandler()V"))
        assertTrue(intoHandler.isHandlerEntry(2))
        assertEquals(sortedSetOf(1), intoHandler.predecessors(2).toSortedSet())
        assertNull(intoHandler.fallthroughPredecessor(2), "an exceptional edge enters with the pre-throw state")
    }

    @Test
    fun throwingInstructionsInATryRangeReachTheirHandler() {
        val flow = requireNotNull(flow("guarded()V"))
        assertArrayEquals(IntArray(0), flow.exceptionalSuccessors(0), "const/4 cannot throw")
        assertArrayEquals(intArrayOf(3), flow.exceptionalSuccessors(1))
        assertEquals(sortedSetOf(2), successors(flow, 1))
        assertTrue(flow.isHandlerEntry(3))
        assertTrue(flow.isReachable(3))
        assertArrayEquals(intArrayOf(1), flow.predecessors(3))
    }

    @Test
    fun deadPaddingBeforePayloadIsUnreachableAndAccepted() {
        val flow = requireNotNull(flow("padded()V"))
        assertEquals(listOf(0, 1, 3, 6, 7), List(flow.size) { flow.addressAt(it) })
        assertEquals(sortedSetOf(3), successors(flow, 2), "fill-array-data continues to the next instruction")
        assertFalse(flow.isReachable(4))
        assertTrue(flow.isReachable(3))
    }

    @Test
    fun reachableFallOffTheEndOfCodeIsRejectedButUnreachableIsNot() {
        assertNull(flow("fallsOff()V"))
        assertNotNull(flow("deadFallsOff()V"))
    }

    @Test
    fun wideOperandsNameBothRegistersOfThePair() {
        val flow = requireNotNull(flow("wide(J)V"))
        assertArrayEquals(intArrayOf(3, 4), flow.registersRead(0), "move-wide v0, v3")
        assertArrayEquals(intArrayOf(0, 1), flow.registersWritten(0))
        assertArrayEquals(intArrayOf(0, 1), flow.registersRead(1), "invoke-static {v0, v1}")
        assertArrayEquals(IntArray(0), flow.registersWritten(1))
        assertArrayEquals(intArrayOf(4), flow.registersWritten(2), "move-result-object v4")
    }

    private companion object {
        val FLOW = """
            .class public LFlow;
            .super Ljava/lang/Object;

            .method public static branches(I)I
                .registers 3
                const/4 v0, 0x0
                if-eqz v2, :skip
                const/4 v0, 0x1
                :skip
                packed-switch v2, :table
                const/4 v1, 0x2
                :case
                add-int/2addr v0, v1
                return v0
                :table
                .packed-switch 0x0
                    :case
                .end packed-switch
            .end method

            .method public static guarded()V
                .registers 2
                :start
                const/4 v0, 0x0
                div-int/lit8 v1, v0, 0x1
                :end
                return-void
                :handler
                move-exception v0
                return-void
                .catch Ljava/lang/ArithmeticException; {:start .. :end} :handler
            .end method

            .method public static fallsIntoHandler()V
                .registers 2
                :start
                const/4 v0, 0x0
                div-int/lit8 v1, v0, 0x1
                :end
                :handler
                return-void
                .catchall {:start .. :end} :handler
            .end method

            .method public static padded()V
                .registers 1
                const/4 v0, 0x1
                new-array v0, v0, [I
                fill-array-data v0, :data
                return-void
                nop
                :data
                .array-data 4
                    0x1
                .end array-data
            .end method

            .method public static fallsOff()V
                .registers 1
                const/4 v0, 0x1
            .end method

            .method public static deadFallsOff()V
                .registers 1
                return-void
                const/4 v0, 0x1
            .end method

            .method public static wide(J)V
                .registers 5
                move-wide v0, v3
                invoke-static {v0, v1}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
                move-result-object v4
                return-void
            .end method
        """.trimIndent()
    }
}
