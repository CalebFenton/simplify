package org.cf.smalivm.core

/**
 * The reconciliation operator ⊔ (spec §4.1):
 *
 *     Known a ⊔ Known b = a                      if a agrees with b
 *                       = Unknown(join(Ta,Tb))   if types join
 *                       = Top                    if types incompatible
 *     Known  ⊔ Unknown  = Unknown(join)  (Top if types incompatible)
 *     Unknown⊔ Unknown  = Unknown(join)  (Top if types incompatible)
 *     x      ⊔ Top      = Top
 *
 * Information only ever decreases across a join; nothing below re-derives a value the
 * paths did not all carry. This is what makes consensus trustworthy (spec §6).
 *
 * [superclasses] is threaded rather than defaulted, deliberately (Plan 8 Task 7). A default would let a
 * future call site silently fall back to the hierarchy-free rule, which is sound but coarser -- and the
 * whole point of the parameter is that `joinTypes` is the one function every consensus query routes
 * through, so a forgotten hierarchy would lose precision engine-wide with nothing failing. Handler
 * resolution uses the same explicit-dependency shape through its separate [ThrowableHierarchy].
 */
fun join(a: Value, b: Value, superclasses: Superclasses): Value {
    if (a is Value.Top || b is Value.Top) return Value.Top
    if (a is Value.Known && b is Value.Known && a.agreesWith(b)) return a
    // At least one side is Unknown, or the Knowns disagree: the value is gone.
    val ta = checkNotNull(a.type) // non-Top values always carry a type
    val tb = checkNotNull(b.type)
    return when (val joined = joinTypes(ta, tb, superclasses)) {
        is JoinedType.Joined -> Value.Unknown(joined.type)
        JoinedType.Incompatible -> Value.Top
    }
}

/** Folds ⊔ over [values]: the per-address consensus read. Returns null for no input. */
fun joinAll(values: Iterable<Value>, superclasses: Superclasses): Value? =
    values.fold(null as Value?) { acc, v ->
        if (acc == null) v else join(acc, v, superclasses)
    }

/**
 * Joins one atomic frame cell at an explicit summary boundary.
 *
 * Equal refinements survive. Any disagreement forgets both refinements; retaining either side would make a
 * fact from one path appear true on the other path.
 */
internal fun joinFrameValues(a: FrameValue, b: FrameValue, superclasses: Superclasses): FrameValue {
    val value = join(a.value, b.value, superclasses)
    return if (
        a is FrameValue.RefinedThrowable && b is FrameValue.RefinedThrowable && a.domain == b.domain
    ) {
        FrameValue.refined(value, a.domain)
    } else {
        FrameValue.plain(value)
    }
}

internal fun joinAllFrameValues(values: Iterable<FrameValue>, superclasses: Superclasses): FrameValue? =
    values.fold(null as FrameValue?) { acc, value ->
        if (acc == null) value else joinFrameValues(acc, value, superclasses)
    }

/** Joins modeled throwable causes without treating a definite absent cause as an ordinary reference value. */
internal fun joinThrowableCauses(causes: Iterable<Value?>, superclasses: Superclasses): Value? {
    var joined: Value? = null
    var absentCell = false
    var nullCause = false
    var nonNullCause = false
    for (cause in causes) {
        if (cause == null) {
            absentCell = true
            continue
        }
        if (cause.readsAsNullReference) nullCause = true else nonNullCause = true
        joined = joined?.let { join(it, cause, superclasses) } ?: cause
    }
    if (joined == null) return null
    if (absentCell || (nullCause && nonNullCause)) return Value.Top
    return joined
}
