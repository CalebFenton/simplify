package org.cf.smalivm.core

import java.util.Collections

data class Breakpoint(val methodSignature: String, val instructionIndex: Int) {
    init {
        require(methodSignature.isNotBlank()) { "Breakpoint method signature must not be blank" }
        require(instructionIndex >= 0) { "Breakpoint instruction index must not be negative" }
    }
}

/** A driveable view over one existing [ExecutionSession]. */
class Debugger(private val session: ExecutionSession) {
    private val mutableBreakpoints = linkedSetOf<Breakpoint>()
    private var stoppedAtBreakpointNode: ExecutionNode? = null

    /** The next scheduled pre-op node. */
    val cursor: ExecutionNode? get() = session.nextNode

    /** The node most recently dequeued by the session, including a node stopped before its op. */
    val lastNode: ExecutionNode? get() = session.currentNode

    /** The actual pre-op frame of [cursor], exposed through its consumer-facing state type. */
    val currentState: ExecutionState? get() = cursor?.frame

    /** Payload-free executable instruction index for [cursor], or null when there is no executable cursor. */
    val currentInstructionIndex: Int? get() = cursor?.let { it.view.instructionIndexAtCodeAddress(it.address) }

    val isFinished: Boolean get() = session.isFinished

    /** Breakpoints in insertion order, copied so callers cannot change debugger policy. */
    val breakpoints: Set<Breakpoint>
        get() = Collections.unmodifiableSet(LinkedHashSet(mutableBreakpoints))

    fun addBreakpoint(breakpoint: Breakpoint): Boolean = mutableBreakpoints.add(breakpoint)

    fun removeBreakpoint(breakpoint: Breakpoint): Boolean = mutableBreakpoints.remove(breakpoint)

    fun clearBreakpoints() = mutableBreakpoints.clear()

    fun isAtBreakpoint(): Boolean = cursor?.let(::breakpointAt) != null

    /**
     * Advances one scheduled node through the session and returns the next cursor.
     * A pre-op limit can stop the selected node before its op starts.
     */
    fun stepInto(): ExecutionNode? {
        stoppedAtBreakpointNode = null
        if (cursor == null) return null
        session.step()
        return cursor
    }

    /**
     * Advances the current activation once, then drains work in activations called by that cursor.
     *
     * The session's LIFO insertion order guarantees that descendant fork paths opened by the stepped cursor
     * drain before older caller siblings. This loop depends on that frontier invariant.
     */
    fun stepOver(): ExecutionNode? {
        val steppedNode = cursor ?: return null
        if (steppedNode === stoppedAtBreakpointNode) stoppedAtBreakpointNode = null
        val steppedActivation = steppedNode.activation
        session.step()

        while (true) {
            val next = cursor ?: return null
            if (!next.activation.isStrictDescendantOf(steppedActivation)) return next
            if (breakpointAt(next) != null) {
                stoppedAtBreakpointNode = next
                return next
            }
            session.step()
        }
    }

    /** Runs scheduler steps until the next breakpoint cursor, without executing that cursor. */
    fun runToBreakpoint(): ExecutionNode? {
        while (true) {
            val node = cursor ?: return null
            if (node === stoppedAtBreakpointNode) {
                stoppedAtBreakpointNode = null
            } else if (breakpointAt(node) != null) {
                stoppedAtBreakpointNode = node
                return node
            }
            session.step()
        }
    }

    private fun breakpointAt(node: ExecutionNode): Breakpoint? {
        val instructionIndex = node.view.instructionIndexAtCodeAddress(node.address) ?: return null
        return mutableBreakpoints.firstOrNull {
            it.methodSignature == node.view.signature && it.instructionIndex == instructionIndex
        }
    }

    private fun Activation.isStrictDescendantOf(ancestor: Activation): Boolean {
        var current = caller
        while (current != null) {
            if (current === ancestor) return true
            current = current.caller
        }
        return false
    }
}
