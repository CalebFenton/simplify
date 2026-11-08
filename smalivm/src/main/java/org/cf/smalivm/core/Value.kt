package org.cf.smalivm.core

/**
 * A reference into the VM-tracked heap. Aliasing is "same id", never host `==`: the raw of a
 * [Value.Known] for a tracked mutable object is a HeapRef, not the live host object.
 */
public data class HeapRef(val id: Int)

/**
 * The three-point value lattice used for reconciliation.
 *
 *  - [Known]: definitely this value (raw == null means definitely the null reference).
 *  - [Unknown]: value not known; type approximately known. *May* be null.
 *  - [Top]: nothing known (join of incompatibles).
 *
 * An uninitialized instance is not a lattice point: a `new-instance` result is `Known(HeapRef, T)`
 * whose heap object is in an uninitialized state.
 */
public sealed interface Value {
    /** The value's type; null only for [Top]. */
    public val type: VirtualType?

    /** A concrete value [raw] of [type]. */
    public data class Known(val raw: Any?, override val type: VirtualType) : Value
    /** Any value of [type]. */
    public data class Unknown(override val type: VirtualType) : Value
    /** Any value of any type. */
    public data object Top : Value {
        override val type: VirtualType? = null
    }
}

/** Definitely the null reference: only `Known(null)` is null. */
public val Value.isNull: Boolean get() = this is Value.Known && raw == null

/**
 * The primitive-bits half of Dalvik's null: a primitive-tagged [Value.Known] whose raw is integral zero.
 *
 * In Dalvik `(Object) null == (int) 0`, so `const/4 v0, 0x0` yields `Known(0, "I")` and still means null in a
 * reference context. [isNull] answers "is this the null reference"; this answers "would the runtime treat
 * these bits as null", which [join] and `aput-object`'s value coercion need. Integral covers `B`, `S`, `I`,
 * `J` and `C`; `Z`, `F` and `D` are excluded because no `const` opcode produces them. Ops that dereference a
 * register use [readsAsNullReference] instead. See `docs/DALVIK-SEMANTICS.md`, "Registers and the null idiom".
 */
public val Value.isDalvikNull: Boolean
    get() = this is Value.Known && !type.isReference && when (val r = raw) {
        is Byte, is Short, is Int, is Long -> (r as Number).toLong() == 0L
        is Char -> r.code == 0
        else -> false
    }

/**
 * Whether a reference-operand read of this register yields `null`: true for a definite null and for any
 * primitive-typed [Value.Known], zero or not, because a primitive write clears the reference half of an
 * interpreter register.
 *
 * This models the switch interpreter (`-Xint`). On a non-zero primitive, `nterp` dereferences the bits as a
 * pointer and the process dies; that input is verifier-rejected, and reading it as null is the sound
 * answer. [Value.Unknown] and [Value.Top] answer `false`: a definite throw needs a proved state. Evidence:
 * `docs/DALVIK-SEMANTICS.md`, "Reading a register as a reference" and "ART has two interpreter tiers that
 * read a register differently".
 */
public val Value.readsAsNullReference: Boolean
    get() = isNull || (this is Value.Known && !type.isReference)

/**
 * Semantic agreement, the primitive under consensus. Two values agree iff they are interchangeable for
 * reconciliation:
 *  - Known vs Known, same type: heap-id equality when both raws are [HeapRef]; host `equals` otherwise
 *    (null == null included). A HeapRef never equals a non-HeapRef raw.
 *  - Everything else, including any Unknown or Top, does not agree; "unknown" twice is not "the same value".
 *
 * A non-HeapRef raw must have a symmetric, stable `equals`, so mutable VM-tracked objects must be
 * [HeapRef]s. The raw's boxed type is trusted to match [Value.type]; this function does not check it.
 */
internal fun Value.agreesWith(other: Value): Boolean {
    if (this !is Value.Known || other !is Value.Known) return false
    if (type != other.type) return false
    val a = raw
    val b = other.raw
    return when {
        a is HeapRef || b is HeapRef -> a is HeapRef && b is HeapRef && a.id == b.id
        else -> a == b
    }
}
