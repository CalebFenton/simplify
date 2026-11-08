package org.cf.smalivm.core

import java.util.Collections

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

/** Which Dalvik invoke instruction family a call site uses; resolution rules differ per kind (spec §5.3). */
enum class InvokeKind { STATIC, DIRECT, SUPER, VIRTUAL, INTERFACE }

/**
 * Minimal identity of an invoke target as written in the bytecode: the statically referenced class, the
 * method name, and the dex **proto**. The `Invoker` resolves this to an actual method per [InvokeKind]
 * rules; this type deliberately carries no resolution result.
 *
 * **Three names for three things, none of them interchangeable** -- the field below was called `signature`
 * and was the only one of the three that is not a signature in any sense (`CARRY-FORWARD.md`'s cross-cutting
 * row, discharged in Plan 9 Task 14):
 *
 * | Name | Example |
 * |---|---|
 * | [proto] | `(ILjava/lang/String;)V` |
 * | [descriptor] | `bar(ILjava/lang/String;)V` |
 * | `MethodView.signature` | `Lfoo;->bar(ILjava/lang/String;)V` |
 */
data class MethodDescriptor(val definingClass: VirtualType, val name: String, val proto: String) {
    /**
     * [name] + [proto], i.e. `returnsSeven()I` -- the form the class model looks a member up by
     * ([ClassIndex.lookupMethod], [ClassIndex.findMethod]). A **member** descriptor: no defining class, unlike
     * `MethodView.signature`, and not a type descriptor, unlike [VirtualType.descriptor].
     */
    val descriptor: String get() = "$name$proto"

    internal fun parameterTypes(): List<VirtualType> {
        val parameters = proto.substringAfter('(').substringBefore(')')
        val result = mutableListOf<VirtualType>()
        var index = 0
        while (index < parameters.length) {
            val start = index
            while (parameters[index] == '[') index++
            index = if (parameters[index] == 'L') parameters.indexOf(';', index) + 1 else index + 1
            require(index > start) { "Malformed method proto $proto" }
            result += VirtualType.of(parameters.substring(start, index))
        }
        return result
    }

    /**
     * The invoke register words of a call to this method: the receiver first unless [kind] is static, then each
     * parameter, a wide parameter taking two words.
     */
    internal fun argumentWords(kind: InvokeKind): ArgumentWords {
        val arguments = ArrayList<ArgumentWords.Argument>()
        var word = 0
        if (kind != InvokeKind.STATIC) arguments += ArgumentWords.Argument(definingClass, word++)
        for (type in parameterTypes()) {
            arguments += ArgumentWords.Argument(type, word)
            word += if (type.isWide) 2 else 1
        }
        return ArgumentWords(arguments, word)
    }
}

/** Logical arguments of one invoke and the first register word each occupies. */
internal class ArgumentWords(val arguments: List<Argument>, val wordCount: Int) {
    class Argument(val type: VirtualType, val word: Int)

    /** The type of the argument occupying [word], or null past the last word. */
    fun typeAt(word: Int): VirtualType? = arguments.lastOrNull { it.word <= word && word < wordCount }?.type
}

/**
 * Whether a run explored everything it set out to (spec §6.4, §9): the load-bearing gate
 * for optimizer trust. There is deliberately no boolean accessor on Partial that could be
 * confused for "mostly done" — trust is all or nothing.
 *
 * Three-valued, not two: trust has to be refused for a run that has not *finished* as firmly as
 * for one that aborted, and [InProgress] is the case that makes that true in **time** rather than
 * only in kind. Read its KDoc before touching this hierarchy.
 */
sealed interface CompletionStatus {
    /**
     * Every path this run set out to explore ran to an end, and none of them aborted. The only
     * status [canTrustConsensus] answers `true` for.
     */
    data object Complete : CompletionStatus

    /**
     * At least one path stopped short of a normal end: a bound, an unimplemented opcode, a host
     * failure. Truncation is permanent — further stepping cannot un-truncate a path already
     * abandoned — so this outranks [InProgress] even while the frontier is still non-empty. [reasons]
     * is a non-empty immutable snapshot.
     */
    class Partial(reasons: Set<AbortReason>) : CompletionStatus {
        init {
            require(reasons.isNotEmpty()) { "Partial requires at least one abort reason" }
        }

        val reasons: Set<AbortReason> = Collections.unmodifiableSet(LinkedHashSet(reasons))

        override fun equals(other: Any?): Boolean =
            this === other || other is Partial && reasons == other.reasons

        override fun hashCode(): Int = reasons.hashCode()

        override fun toString(): String = "Partial(reasons=$reasons)"
    }

    /**
     * Nothing has aborted **yet**, and the run is not finished: paths are still scheduled.
     *
     * **Why this case exists — do not "simplify" it away, and do not fold it into
     * `Partial(emptySet())`.** `ExecutionSession.graph()` is documented as safe to call mid-run,
     * because Plan 10's debugger is precisely that consumer. While `completion()` read only the abort
     * set, a mid-run snapshot answered `Complete` and handed out a non-null
     * `ExecutionGraph.trusted()`, and the consequence was demonstrated rather than theoretical: step
     * `Lspine/spine;.unsupportedWideFilledArray()I` **once**, snapshot it, and the trusted view claimed
     * `methodSideEffectLevel() == NONE` and `consensus(0, 0) == Known(3, I)` — "no side effects, safe
     * to delete" — while the frontier still held the unsupported `filled-new-array {v0}, [J` that makes the finished run
     * `Partial(UNSUPPORTED_OP)` with `STRONG` effects and no trusted view at all. The mid-run
     * snapshot authorized exactly what the finished run refuses, and it did so **through** the gate
     * rather than around it: the one demonstrated path by which a confident wrong answer could reach
     * a consumer.
     *
     * The recipe above is runnable as written, and must stay that way: it was first demonstrated on
     * `unsupportedOpcode()I`, whose `add-int` Plan 4 then implemented, so that method now runs to
     * `Complete` and a reader following the original recipe would conclude this case has no motivating
     * defect and delete it. `TrustedGraphViewTest.aMidRunSnapshotNeverAuthorizesWhatTheFinishedRunRefuses`
     * is the executable counterpart, now carried by the clearly named malformed-input fixture.
     *
     * A distinct sealed member rather than `Partial(emptySet())` for two reasons. It is honest —
     * nothing aborted, the run simply is not done, and calling that "partial with no reasons" invents
     * a truncation that did not happen. And being its own member makes every exhaustive `when` over
     * [CompletionStatus] force each consumer to decide about it, which is what makes the gate hard to
     * bypass by *accident* rather than merely documented against — the same reasoning that made the
     * trust gate a type (spec §9) instead of a KDoc rule.
     */
    data object InProgress : CompletionStatus
}

/**
 * Completion-gated trust (spec §9): a strategy may trust consensus only from a graph
 * whose exploration completed. Any abort reason — timeout, limits, host failure —
 * means some path was never walked, so agreement across walked paths proves nothing; and an
 * [CompletionStatus.InProgress] run has paths still scheduled, which refuses trust for a reason
 * that has nothing to do with aborting (see that type's KDoc for the snapshot that motivated it).
 *
 * An exhaustive `when` rather than `this is Complete`: this predicate is the single definition the
 * trust gate routes through, so a future status must be an explicit decision here instead of
 * silently inheriting an answer from the shape of a type test.
 */
val CompletionStatus.canTrustConsensus: Boolean
    get() = when (this) {
        is CompletionStatus.Complete -> true
        is CompletionStatus.Partial -> false
        is CompletionStatus.InProgress -> false
    }
