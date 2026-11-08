package org.cf.smalivm.core

/**
 * One successor to schedule: an address in [activation]'s method, where its [Frame] comes from
 * ([frameOrigin]), what to do if it cannot be built ([onUnbuildable]), plus a [seed] applied to that
 * successor's own frame. The seed carries what the parent's frame must not hold (a handler's `caught` slot, a
 * callee's bound parameters, a resumed caller's result slot), so the parent's frame is never mutated.
 *
 * [onUnbuildable] and [frameOrigin] have no default: each is a soundness decision a new successor site must make.
 */
internal class SuccessorSpec(
    val address: Int,
    val activation: Activation,
    val onUnbuildable: OnUnbuildable,
    val frameOrigin: FrameOrigin,
    val seed: (Frame) -> Unit = {},
) {
    val view: MethodView get() = activation.view
}

/**
 * Where a successor's [Frame] comes from. Every origin produces a fresh frame, so a speculative path cannot
 * inherit or clobber a sibling's writes. Picking the wrong origin is silent, which is why this is a type.
 */
internal sealed interface FrameOrigin {
    /** Ordinary control flow inside one activation: a copy of the parent node's post-op frame. */
    data object Continuation : FrameOrigin

    /**
     * A call: a fresh callee entry frame sharing the caller's heap id space (`Frame.calleeEntryFrame`), with
     * parameters bound by the [SuccessorSpec.seed].
     */
    data object CalleeEntry : FrameOrigin

    /** A call outcome partition already merged into a caller-shaped frame. */
    class CallerPartition(val frame: Frame) : FrameOrigin

    /** An opaque call's collapsed post-call frame, copied for each successor that resumes from it. */
    class OpaqueResult(val frame: Frame) : FrameOrigin
}

/**
 * What to do when this successor cannot be built (a malformed address, a decode failure). The policy applies
 * only within the group of successors one transition declared; groups never interact.
 *
 * [ALL_OR_NOTHING] abandons the whole group, including successors already built and the group's ending: a
 * malformed try-block table makes every clause in it suspect, so routing part of a throw would leave paths no
 * consumer can account for. [SKIP_ONE] keeps the buildable siblings: a malformed fall-through is no reason to
 * discard a feasible exceptional branch. Either way the loss is recorded as an aborted path with its own reason,
 * so a truncated exploration never reports as complete.
 */
internal enum class OnUnbuildable { ALL_OR_NOTHING, SKIP_ONE }
