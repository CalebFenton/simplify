package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CallCompletionTest {
    @Test
    fun drainBeforeZeroLeavesTheCollectorOpen() {
        val completion = CallCompletion()

        assertNull(completion.drainIfComplete())
        assertEquals(CallCompletionMetrics(1, 0, 0, drained = false), completion.metrics())
    }

    @Test
    fun replacingOnePathAccountsForSameActivationAndNestedPathsAtomically() {
        val completion = CallCompletion()

        completion.replacePath(2)
        assertEquals(2, completion.metrics().openPaths)
        completion.replacePath(0)
        assertEquals(1, completion.metrics().openPaths)
        completion.replacePath(0)

        assertEquals(emptyList<ActivationExit>(), completion.drainIfComplete()!!.exitsSnapshot())
    }

    @Test
    fun negativePathCountFailsBeforeStateChanges() {
        val completion = CallCompletion()
        completion.replacePath(0)

        assertThrows<IllegalArgumentException> { completion.replacePath(0) }
        assertEquals(0, completion.metrics().openPaths)
        assertEquals(emptyList<ActivationExit>(), completion.drainIfComplete()!!.exitsSnapshot())
    }

    @Test
    fun secondDrainAndLateVoteFailLoudly() {
        val completion = CallCompletion()
        completion.replacePath(0)
        completion.drainIfComplete()

        assertThrows<IllegalStateException> { completion.drainIfComplete() }
        assertThrows<IllegalStateException> { completion.replacePath(0) }
    }

    @Test
    fun drainTransfersFramesAndReleaseDropsBatchStorage() {
        val view = CoreTester.classIndex.requireMethodView("Lspine/spine;", "returnFive()I")
        val frame = Frame.entryFrame(view)
        val exits = List(5) { ActivationExit.Aborted(frame.copy(), AbortReason.MALFORMED_INPUT) }
        val completion = CallCompletion()
        completion.replacePath(0, exits)
        val before = completion.metrics()
        assertEquals(5, before.retainedFrames)
        assertTrue(before.retainedCollectionCapacity >= before.retainedFrames)

        val batch = completion.drainIfComplete()!!

        assertEquals(0, completion.metrics().retainedFrames)
        assertEquals(5, batch.retainedFrames)
        assertTrue(batch.retainedCollectionCapacity >= batch.retainedFrames)
        batch.release()
        assertEquals(0, batch.retainedFrames)
        assertEquals(0, batch.retainedCollectionCapacity)
        assertThrows<IllegalStateException> { batch.exitsSnapshot() }
        assertThrows<IllegalStateException> { batch.release() }
    }

    @Test
    fun cancellationReleasesFramesAndRejectsLateVotes() {
        val view = CoreTester.classIndex.requireMethodView("Lspine/spine;", "returnFive()I")
        val completion = CallCompletion()
        val retained = ActivationExit.Aborted(Frame.entryFrame(view), AbortReason.TIMEOUT)
        completion.replacePath(
            replacementOpenPaths = 1,
            exits = listOf(retained),
        )

        val cancelled = completion.cancelAndRelease()

        assertEquals(1, cancelled.openPaths)
        assertEquals(1, cancelled.exits.size)
        assertSame(retained, cancelled.exits.single())
        assertEquals(CallCompletionMetrics(0, 0, 0, drained = true), completion.metrics())
        assertThrows<IllegalStateException> { completion.replacePath(0) }
        assertThrows<IllegalStateException> { completion.drainIfComplete() }
        assertThrows<IllegalStateException> { completion.cancelAndRelease() }
    }
}
