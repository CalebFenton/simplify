package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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
        assertFalse(completion.isDrained)
        completion.replacePath(0)
        assertEquals(emptyList<ActivationExit>(), completion.drainIfComplete()!!.exitsSnapshot())
        assertTrue(completion.isDrained)
    }

    @Test
    fun replacingOnePathAccountsForSameActivationAndNestedPathsAtomically() {
        val completion = CallCompletion()

        completion.replacePath(2)
        completion.replacePath(0)
        assertNull(completion.drainIfComplete(), "one of the two replacement paths is still open")
        completion.replacePath(0)

        assertEquals(emptyList<ActivationExit>(), completion.drainIfComplete()!!.exitsSnapshot())
    }

    @Test
    fun negativePathCountFailsBeforeStateChanges() {
        val completion = CallCompletion()
        completion.replacePath(0)

        assertThrows<IllegalArgumentException> { completion.replacePath(0) }
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
    fun drainTransfersFramesInVoteOrderAndReleaseDropsBatchStorage() {
        val view = CoreTester.classIndex.requireMethodView("Lspine/spine;", "returnFive()I")
        val frame = Frame.entryFrame(view)
        val exits = List(5) { ActivationExit.Aborted(frame.copy(), AbortReason.MALFORMED_INPUT) }
        val completion = CallCompletion()
        completion.replacePath(2, exits.take(2))
        completion.replacePath(0, exits.drop(2))
        completion.replacePath(0)

        val batch = completion.drainIfComplete()!!

        assertEquals(5, batch.retainedFrames)
        assertEquals(exits, batch.exitsSnapshot())
        batch.release()
        assertEquals(0, batch.retainedFrames)
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

        val exits = completion.cancelAndRelease()

        assertSame(retained, exits.single())
        assertTrue(completion.isDrained)
        assertThrows<IllegalStateException> { completion.replacePath(0) }
        assertThrows<IllegalStateException> { completion.drainIfComplete() }
        assertThrows<IllegalStateException> { completion.cancelAndRelease() }
    }
}
