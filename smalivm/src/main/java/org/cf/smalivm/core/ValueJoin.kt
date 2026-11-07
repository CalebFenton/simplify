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
 * Before those rows, a `Known(0, I)` meeting a reference operand of type `T` reads as `Known(null, T)`:
 * Dalvik writes null as `const vA, 0`, and the verifier merges that zero constant into any reference type.
 *
 * Information only ever decreases across a join; nothing below re-derives a value the
 * paths did not all carry. This is what makes consensus trustworthy (spec §6).
 *
 * [superclasses] has no default: `joinTypes` is the one function every consensus query routes through, so a
 * call site that silently fell back to the hierarchy-free rule would lose precision with nothing failing.
 */
fun join(a: Value, b: Value, superclasses: Superclasses): Value {
    if (a is Value.Top || b is Value.Top) return Value.Top
    val left = a.asNullAgainst(b)
    val right = b.asNullAgainst(a)
    if (left is Value.Known && right is Value.Known && left.agreesWith(right)) return left
    // At least one side is Unknown, or the Knowns disagree: the value is gone.
    val ta = checkNotNull(left.type) // non-Top values always carry a type
    val tb = checkNotNull(right.type)
    return when (val joined = joinTypes(ta, tb, superclasses)) {
        is JoinedType.Joined -> Value.Unknown(joined.type)
        JoinedType.Incompatible -> Value.Top
    }
}

/**
 * This value as the null of [other]'s reference type when it is the null idiom, else itself.
 *
 * Only the `I` tag that the `const` opcodes produce qualifies. Admitting a second primitive tag would make
 * the join non-associative: `Known(0, I) ⊔ Known(0, B)` is `Top`, while each zero alone would join with a
 * reference to that reference's null.
 */
private fun Value.asNullAgainst(other: Value): Value {
    val referenceType = other.type?.takeIf { it.isReference } ?: return this
    return if (isDalvikNull && type == INT) Value.Known(null, referenceType) else this
}

private val INT = VirtualType.of("I")

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
