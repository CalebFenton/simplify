package org.cf.smalivm.core

/**
 * What the engine can prove about the 32 bits a comparison reads out of a register. A question is provable
 * exactly when every bit it reads is determined by the value the lattice holds; an unprovable successor set
 * explores more, and an unprovable value answers [Value.Unknown] (spec §2.2).
 */
internal sealed interface ProvableInt {
    /** These 32 bits are exactly [bits], read signed, which is how Dalvik compares them. */
    data class Exactly(val bits: Int) : ProvableInt

    /**
     * A live pointer: provably not zero, and not any particular `Int`. In Dalvik `(Object) null == (int) 0`
     * ([BC]), so a non-null reference is never bitwise zero and decides a zero comparison.
     */
    data object NonZero : ProvableInt

    /** Not determined by what the lattice holds: [Value.Unknown], [Value.Top], or a `Float`/`Double` raw. */
    data object Unprovable : ProvableInt
}

/**
 * A register's low 32 bits as a comparison reads them. A `Float`/`Double` raw is unprovable here, because a
 * host box's 32 bits are not what a comparison of the original register would read once the value was
 * computed in another domain. This is not [Value.readsAsNullReference]: `Known(5, "I")` reads as a null
 * reference but its 32 bits are `5`.
 */
internal fun provableInt(value: Value): ProvableInt = when (value) {
    is Value.Unknown, Value.Top -> ProvableInt.Unprovable
    is Value.Known -> if (value.type.isReference) {
        if (value.raw == null) ProvableInt.Exactly(0) else ProvableInt.NonZero
    } else when (val raw = value.raw) {
        null -> ProvableInt.Exactly(0)
        is HeapRef -> ProvableInt.NonZero
        is Float, is Double -> ProvableInt.Unprovable
        else -> Coerce.toInt(raw)?.let { ProvableInt.Exactly(it) } ?: ProvableInt.NonZero
    }
}

/**
 * Whether two registers hold the same reference, or null when either is not a definite value. A tracked
 * object's identity is its heap id, a host object's is `===`, and the declared tags are ignored, because a
 * `check-cast` re-tags a register without changing what it points at. This is not [Value.agreesWith], which
 * compares content and tags.
 *
 * Callers must first prove both operands live pointers ([ProvableInt.NonZero]): two boxes of one primitive
 * value are not `===`, so ungated this answers "different" for `1000 == 1000`.
 */
internal fun sameReference(a: Value, b: Value): Boolean? {
    if (a !is Value.Known || b !is Value.Known) return null
    val x = a.raw
    val y = b.raw
    return when {
        x is HeapRef || y is HeapRef -> x is HeapRef && y is HeapRef && x.id == y.id
        else -> x === y
    }
}

/**
 * Whether a value of one type is an instance of another, over arrays and primitives as well as classes. May
 * raise [ClassLoadFailure] for a class the program does not contain.
 */
fun interface InstanceOfTest {
    fun isInstanceOf(candidate: VirtualType, target: VirtualType): Boolean
}

/**
 * What the engine can prove about "is the value in this register an instance of `C`?". `instance-of` and
 * `check-cast` map it differently:
 *
 * | outcome | `instance-of` | `check-cast` |
 * |---|---|---|
 * | [ReadsAsNull] | `0` | succeeds |
 * | [Definitely] | `1` | succeeds |
 * | [Never] | `0` | throws `ClassCastException` |
 * | [NullOrInstance] | unknown | succeeds |
 * | [Unprovable] | unknown | forks |
 */
internal sealed interface CastOutcome {
    /** The register reads as a null reference ([Value.readsAsNullReference]). */
    data object ReadsAsNull : CastOutcome

    /** Some type the value is known to have satisfies the target. */
    data object Definitely : CastOutcome

    /** Provably not an instance, and provably not null. */
    data object Never : CastOutcome

    /**
     * Not a particular object, but its tag satisfies the target, so it holds null or an instance: every
     * content passes a cast, while an instance test cannot answer.
     */
    data object NullOrInstance : CastOutcome

    /**
     * Nothing decides it: an unknown whose tag does not satisfy, `Top`, a known reference whose class cannot
     * be determined, or a class walk that cannot answer.
     */
    data object Unprovable : CastOutcome
}

/**
 * The instance test over a [Value], in order:
 *  1. a register that reads as a null reference: [CastOutcome.ReadsAsNull], before anything else, because a
 *     null passes a cast even to a primitive type;
 *  2. a primitive target: [CastOutcome.Never] ([BC] `instance-of`);
 *  3. an unknown: its tag is an upper bound, so a satisfying tag is [CastOutcome.NullOrInstance] and any
 *     other answer is unprovable;
 *  4. a known reference: tested against its tag and its object's class, and one satisfying type decides.
 *     The tag alone is an upper bound too, so when the object's class cannot be determined a failing tag
 *     proves nothing.
 *
 * A [ClassLoadFailure] is ambiguity, never `false`: the walk continues past it, and the answer is unprovable
 * only if nothing satisfied.
 */
internal fun castOutcome(
    value: Value,
    target: VirtualType,
    types: InstanceOfTest,
    heapObjectTypeOf: (HeapRef) -> VirtualType?,
): CastOutcome {
    if (value.readsAsNullReference) return CastOutcome.ReadsAsNull
    if (!target.isReference) return CastOutcome.Never
    if (value !is Value.Known) {
        val tag = value.type ?: return CastOutcome.Unprovable
        val satisfied = try {
            types.isInstanceOf(tag, target)
        } catch (_: ClassLoadFailure) {
            return CastOutcome.Unprovable
        }
        return if (satisfied) CastOutcome.NullOrInstance else CastOutcome.Unprovable
    }
    val runtimeType = runtimeTypeOf(value, heapObjectTypeOf)
    var unanswerable = runtimeType == null
    for (candidate in listOfNotNull(value.type, runtimeType.takeIf { it != value.type })) {
        val satisfied = try {
            types.isInstanceOf(candidate, target)
        } catch (_: ClassLoadFailure) {
            unanswerable = true
            continue
        }
        if (satisfied) return CastOutcome.Definitely
    }
    return if (unanswerable) CastOutcome.Unprovable else CastOutcome.Never
}

/**
 * The class of the object a known reference holds: the heap object's type for a tracked object, the host
 * class otherwise. Null when it cannot be determined: a null raw, a [HeapRef] with no heap object, or a host
 * class no descriptor can name.
 */
internal fun runtimeTypeOf(value: Value.Known, heapObjectTypeOf: (HeapRef) -> VirtualType?): VirtualType? =
    when (val raw = value.raw) {
        null -> null
        is HeapRef -> heapObjectTypeOf(raw)
        else -> try {
            virtualTypeOfHostClass(raw.javaClass)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
