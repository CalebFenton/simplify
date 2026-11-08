package org.cf.smalivm.core

/**
 * Why one explored path stopped short of a normal end (spec §4.4).
 *
 * **The members are not uniform, and two distinctions in here are load-bearing rather than
 * cosmetic.** [ADDRESS_LIMIT], [METHOD_LIMIT] and [TIMEOUT] all fire strictly *before* an op's
 * `execute` runs, so no half-completed mutation is possible on those paths; [HOST_FAILURE] can fire
 * in the middle of one. And [UNSUPPORTED_OP] is the only member `ExecutionGraph.sideEffectLevel`
 * derives `STRONG` from, because it is the only one that means "a real instruction was here and this
 * build has no idea what it would have done".
 *
 * Every member recorded as a run-level abort makes the run [CompletionStatus.Partial], hence
 * `ExecutionGraph.trusted()` null. An ordinary nested-callee abort can instead be discharged through
 * conservative `STRONG` opaque continuation; it is not added to the outer run's abort set.
 */
enum class AbortReason {
    ADDRESS_LIMIT, METHOD_LIMIT, CALL_DEPTH, TIMEOUT, UNSUPPORTED_OP, POLICY_REFUSED,

    /**
     * **This engine, or its host, threw where it should not have** -- the one reason in this enum that
     * means *bug in this codebase*. Everything crossing the host-failure boundary (spec §7) lands
     * here: a `Throwable` out of an op's `execute`, out of a decode, out of handler resolution.
     * `ExecutionNode.hostFailure` carries the actual throwable so the bug is still findable.
     *
     * Contrast [MALFORMED_INPUT], and keep the two apart: they are the two ways
     * `ExecutionSession.buildSuccessor` can fail, and while both reported here, an off-the-end jump in
     * an obfuscated APK -- which is the *expected* input for this tool -- was indistinguishable in
     * aggregate from a genuine engine defect.
     */
    HOST_FAILURE,

    /**
     * **The bytecode asked for something impossible**: a jump, fall-through or exception-handler
     * target naming an address the method does not contain. Nothing threw and nothing is broken here;
     * the input is simply not something any runtime could execute, and this engine's job is to say so
     * and carry on with the paths that remain.
     *
     * Never a bug in this codebase, which is exactly what distinguishes it from [HOST_FAILURE], and
     * why it is worth its own member: obfuscators emit unreachable and malformed tails routinely, so
     * without the split the *rate* of host failures carried no signal at all.
     *
     * **Deliberately NOT part of `ExecutionGraph.sideEffectLevel`'s `STRONG` rule.** The node that
     * records it ran its own op to completion and declared its own effect honestly, and the target is
     * detected as absent *before* any successor frame is built -- so, unlike [HOST_FAILURE], no
     * half-mutation is possible, and unlike [UNSUPPORTED_OP] no instruction *at the recording address*
     * has effects needing over-approximation: the one that ran there declared them.
     *
     * **Real addresses CAN go unexplored on this reason, and folding `STRONG` in would not help them.**
     * Under `OnUnbuildable.ALL_OR_NOTHING` a table with one malformed clause abandons all of its
     * routing, including well-formed handlers at addresses that do exist
     * (`CommitTest.aPartlyMalformedHandlerTableRoutesNoneOfItsHandlersEvenWhenANormalSiblingSurvives`
     * asserts exactly that). Their piles are empty, so `sideEffectLevel` answers `NONE` for them -- but
     * that is the generic property of *any* unexplored address on *any* truncated run, identical to
     * [ADDRESS_LIMIT]'s, and the mechanism for it is the `Partial` status making
     * `ExecutionGraph.trusted()` null. Raising the level of the address that recorded the abort would
     * mislabel an instruction that behaved perfectly while leaving the unexplored handler at `NONE`:
     * wrong in both directions at once. That ruling is pinned by
     * `CommitTest.aMalformedTargetDeclaresNoSideEffectAtTheAddressThatNamedIt`.
     *
     * Distinct from [UNSUPPORTED_OP] in the other direction too: that one means "the address exists
     * and holds a real instruction this build cannot execute" -- a statement about this build's
     * *capability*. A malformed `fill-array-data` payload target stays `UNSUPPORTED_OP` for that
     * reason (see `Op.kt`'s `buildFillArrayData`), and must not be relabelled here.
     */
    MALFORMED_INPUT,
}

/**
 * The result of one explored path (spec §4.4). Replaces the untyped `aborted` boolean and
 * the reconstruct-from-(Class,message) exception model: [Threw] carries the actual guest
 * throwable [Value], identity preserved end-to-end.
 *
 * Read outcomes from the snapshot returned by `vmServices.newSession(view).run()`. Treat that list as a
 * multiset: path multiplicity is semantic, but traversal order is not. This does not promise deep immutability
 * of a [Value]'s payload.
 */
sealed interface Outcome {
    /** Normal return; [value] is null for void methods. */
    data class Returned(val value: Value?) : Outcome
    data class Threw(val exception: Value) : Outcome
    data class Aborted(val reason: AbortReason) : Outcome
}
