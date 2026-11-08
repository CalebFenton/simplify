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
    fun aReplacementThatLeavesAPathOpenReleasesNothing() {
        val completion = CallCompletion()

        assertNull(completion.replacePath(1, emptyList()))
        assertFalse(completion.isDrained)
        assertEquals(emptyList<ActivationExit>(), completion.replacePath(0, emptyList())!!.exitsSnapshot())
        assertTrue(completion.isDrained)
    }

    @Test
    fun replacingOnePathAccountsForSameActivationAndNestedPathsAtomically() {
        val completion = CallCompletion()

        assertNull(completion.replacePath(2, emptyList()))
        assertNull(completion.replacePath(0, emptyList()), "one of the two replacement paths is still open")

        assertEquals(emptyList<ActivationExit>(), completion.replacePath(0, emptyList())!!.exitsSnapshot())
    }

    @Test
    fun lateVoteAfterReleaseFailsLoudly() {
        val completion = CallCompletion()
        completion.replacePath(0, emptyList())

        assertThrows<IllegalStateException> { completion.replacePath(0, emptyList()) }
    }

    @Test
    fun releaseTransfersFramesInVoteOrderAndReleaseDropsBatchStorage() {
        val view = CoreTester.classIndex.requireMethodView("Lspine/spine;", "returnFive()I")
        val frame = Frame.entryFrame(view)
        val exits = List(5) {
            ActivationExit.Aborted(frame.copy(), Outcome.Aborted(AbortReason.MALFORMED_INPUT))
        }
        val completion = CallCompletion()
        assertNull(completion.replacePath(2, exits.take(2)))
        assertNull(completion.replacePath(0, exits.drop(2)))

        val batch = completion.replacePath(0, emptyList())!!

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
        val retained = ActivationExit.Aborted(Frame.entryFrame(view), Outcome.Aborted(AbortReason.TIMEOUT))
        completion.replacePath(openPaths = 1, ended = listOf(retained))

        val exits = completion.cancelAndRelease()

        assertSame(retained, exits.single())
        assertTrue(completion.isDrained)
        assertThrows<IllegalStateException> { completion.replacePath(0, emptyList()) }
        assertThrows<IllegalStateException> { completion.cancelAndRelease() }
    }
}
