package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Bug classes (spec §4.1, from the audit): (a) host-identity aliasing — two boxed 5s or
 * two equal mutable objects collapsing as "the same object"; (b) `UnknownValue != itself`
 * making unknowns randomly disagree vs agree depending on instance reuse; (c) nullness
 * buried in raw. Oracle: the spec's agreement rules — heap-id identity for tracked refs,
 * structural value equality for untracked raws, no agreement through Unknown/Top.
 */
class ValueTest {
    private val int = VirtualType.of("I")
    private val obj = VirtualType.OBJECT
    private val str = VirtualType.of("Ljava/lang/String;")

    @Test
    fun knownPrimitivesAgreeOnValueAndType() {
        assertTrue(Value.Known(5, int).agreesWith(Value.Known(5, int)))
        assertFalse(Value.Known(5, int).agreesWith(Value.Known(6, int)))
    }

    @Test
    fun sameValueDifferentTypeDoesNotAgree() {
        // Bug class: int 5 and short 5 collapsing — the optimizer would then
        // constant-propagate across a width boundary.
        assertFalse(Value.Known(5, int).agreesWith(Value.Known(5, VirtualType.of("S"))))
    }

    @Test
    fun knownNullIsExplicitAndAgreesWithKnownNull() {
        val a = Value.Known(null, str)
        assertTrue(a.isNull)
        assertTrue(a.agreesWith(Value.Known(null, str)))
        assertFalse(Value.Unknown(str).isNull) // may be null is not IS null
    }

    @Test
    fun trackedReferencesAgreeOnHeapIdNotValue() {
        // Bug class: equal-mutables-collapse — two distinct StringBuilders with equal
        // content must NOT agree; the same allocation seen twice MUST.
        val alloc1 = Value.Known(HeapRef(1), obj)
        val alloc1Again = Value.Known(HeapRef(1), obj)
        val alloc2 = Value.Known(HeapRef(2), obj)
        assertTrue(alloc1.agreesWith(alloc1Again))
        assertFalse(alloc1.agreesWith(alloc2))
    }

    @Test
    fun unknownNeverAgreesEvenWithItself() {
        // Bug class: UnknownValue instance-reuse making unknown==unknown "agree",
        // letting consensus report a constant where none exists.
        val u = Value.Unknown(int)
        assertFalse(u.agreesWith(u))
        assertFalse(u.agreesWith(Value.Unknown(int)))
        assertFalse(Value.Known(5, int).agreesWith(u))
    }

    @Test
    fun topNeverAgrees() {
        assertFalse(Value.Top.agreesWith(Value.Top))
        assertFalse(Value.Top.agreesWith(Value.Known(5, int)))
    }

    /**
     * `isDalvikNull`'s documented boundary, in **both** directions.
     *
     * Bug class: a predicate whose KDoc makes eight falsifiable claims and whose only exercise anywhere was
     * a single `Known(0, "I")` seed two files away
     * (`op/ArrayLengthOpTest.aKnownIntegralZeroIsDalviksNullAndThrowsTheSameNullPointerException`). That test
     * pins the CALL SITE, not the predicate's shape: dropping four of the five documented inclusions and
     * adding all three documented exclusions left the whole suite green. This test is what makes the
     * boundary a contract rather than a comment -- which matters because Task 9's rule 1 and Task 12 are
     * told to consume this predicate as settled.
     *
     * Oracle for the inclusions: **SPEC** [BC], "General design" -- *"In terms of bitwise representation,
     * `(Object) null == (int) 0`"* -- narrowed to [JLS] §4.2's integral types (`byte`, `short`, `int`,
     * `long`, `char`). Oracle for the exclusions: no Dalvik `const` opcode produces a `Z`/`F`/`D`-tagged
     * zero (the narrow forms are all tagged `I`, per `OpBuilder`'s type-tag note), so widening the
     * predicate to `false`/`0.0f`/`0.0` would claim more than that sentence supports -- and would make
     * `aput-object`/`throw` synthesize an NPE for a genuine `false`, i.e. invent a reachable handler.
     */
    @Test
    fun everyIntegralZeroIsDalviksNullAndNothingElseIs() {
        for (zero in listOf<Any>(0.toByte(), 0.toShort(), 0, 0L, '\u0000')) {
            assertTrue(Value.Known(zero, int).isDalvikNull, "$zero (${zero.javaClass.simpleName}) is Dalvik's null")
        }
        for (nonZero in listOf<Any>(1.toByte(), 1.toShort(), 1, 1L, 'a', -1)) {
            assertFalse(Value.Known(nonZero, int).isDalvikNull, "$nonZero (${nonZero.javaClass.simpleName}) is not")
        }
        // The documented EXCLUSIONS. Each shares the all-zero bit pattern, so this half is the one that
        // stops the predicate from being quietly widened on the "bitwise representation" argument alone.
        for (excluded in listOf<Any>(false, 0.0f, 0.0)) {
            assertFalse(
                Value.Known(excluded, int).isDalvikNull,
                "$excluded (${excluded.javaClass.simpleName}) is deliberately excluded",
            )
        }
        // A non-numeric raw is not zero-anything.
        assertFalse(Value.Known("0", str).isDalvikNull)
        assertFalse(Value.Known(HeapRef(0), obj).isDalvikNull, "heap id 0 is an identity, not a value")
        // Only a Known can be definitely anything (spec §4.1).
        assertFalse(Value.Unknown(int).isDalvikNull, "may be zero is not IS zero")
        assertFalse(Value.Top.isDalvikNull)
        // ...and `Known(null)` is [isNull]'s job, not this one's. The two predicates exist separately
        // BECAUSE they answer different questions, so neither may quietly subsume the other.
        assertFalse(Value.Known(null, str).isDalvikNull, "a null raw is isNull's case, not isDalvikNull's")
        assertFalse(Value.Known(0, int).isNull, "and the converse: an integral zero is not a null raw")
    }

    /**
     * `readsAsNullReference`'s boundary, in **both** directions -- the predicate every DEREFERENCING op
     * consults (`throw`, `array-length`, `aget`, `aput`'s array operand, `fill-array-data`).
     *
     * Bug class this exists for, and it shipped: Plan 7 Task 12's first round tested only
     * `isNull || isDalvikNull` and reasoned that a NON-zero primitive register is undefined behavior on ART.
     * **In ART's switch interpreter it is not.** `GetVRegReference` reads `ShadowFrame`'s parallel reference
     * array, every primitive write clears the matching slot (`shadow_frame.h:190-228`, ART's prose at
     * `:401-408`), so the *value* is
     * irrelevant -- `Known(5, "I")` reads as null exactly as `Known(0, "I")` does. Forking it invented a
     * reachable `catch (IOException)`; in `aget`/`array-length` the same misreading DROPPED a definite NPE.
     *
     * **TIER SPLIT, 2026-08-04.** ART's
     * *default* interpreter, `nterp`, reads these operands out of the RAW dex-register array, so:
     *  - the primitive zero and null-raw rows (`Known(0, I)`, `Known(null, T)`) are
     *    corroborated by **both** interpreter tiers -- stronger than when they were written;
     *  - the **non-zero** primitive rows (`Known(5, …)`, and the `Boolean`/`Float`/`Double` non-zeros) are a
     *    row the two tiers **contradict** each other on, so their tier is **VERIFIER** -- this project's
     *    spec §2.2 choice on input `method_verifier.cc` hard-fails -- and **not [ART]**. That contradiction is
     *    now MEASURED (Task 9, Tier B, API 36, default `dalvikvm`): nterp CRASHES the VM (a SIGSEGV whose fault
     *    address is the primitive itself when unmapped; a process abort otherwise) where `-Xint` returns
     *    `0`/succeeds -- which is why the sound null-read the assertions below pin is kept unchanged.
     *
     * Task 35 adds the separate boxed-zero rule: `Known(0, referenceType)` is a live pointer. See
     * [org.cf.smalivm.core.readsAsNullReference]'s KDoc for the verified chain and
     * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers* for the general instrument.
     *
     * The two halves that matter most:
     *  - **`Boolean`/`Float`/`Double` ARE included here while `isDalvikNull` excludes them.** Not a
     *    contradiction: `isDalvikNull` asks whether the BITS are an integral zero (a coercion question, which
     *    `aput`'s value rule needs), and no `const` opcode produces a `Z`/`F`/`D`-tagged zero. This asks
     *    whether a reference-slot read yields null, and `SetVRegFloat`/`SetVRegDouble` clear it just as
     *    `SetVReg` does. A `Z`-tagged `false` is therefore NOT Dalvik's null for `aput`'s coercion and IS
     *    null for a dereference.
     *  - **`Unknown` and `Top` are excluded, and that is the soundness line.** Answering true for them would
     *    replace a fork with a definite throw on unproven grounds (spec §2.2's forbidden direction). Only a
     *    PROVED state may answer true.
     */
    @Test
    fun everyPrimitiveTypedKnownReadsAsANullReferenceAndNoReferenceDoes() {
        val primitives = listOf("I", "J", "Z", "B", "C", "S", "F", "D").map { VirtualType.of(it) }
        for (type in primitives) {
            assertTrue(
                Value.Known(0, type).readsAsNullReference,
                "a zero under primitive ${type.descriptor} reads as null",
            )
            assertTrue(
                Value.Known(5, type).readsAsNullReference,
                "and so does a NON-zero under ${type.descriptor} -- the value is irrelevant, only primitiveness",
            )
        }
        // The raws isDalvikNull deliberately excludes, included here for the reason in the KDoc. Both halves
        // are asserted on the SAME value, so the divergence between the two predicates is the claim rather
        // than an accident of which values each list happens to contain.
        for (excluded in listOf<Any>(false, true, 0.0f, 1.5f, 0.0, -3.0)) {
            assertTrue(
                Value.Known(excluded, VirtualType.of("Z")).readsAsNullReference,
                "$excluded under a primitive type reads as null...",
            )
            assertFalse(
                Value.Known(excluded, int).isDalvikNull,
                "...while isDalvikNull refuses the identical raw ($excluded) -- different question, see the KDoc",
            )
        }
        // References: only a null raw. A boxed number is still a live reference.
        assertTrue(Value.Known(null, str).readsAsNullReference, "a null raw under a reference type")
        assertFalse(
            Value.Known(0, str).readsAsNullReference,
            "a boxed Integer(0) under a reference type is a live pointer",
        )
        assertFalse(Value.Known("text", str).readsAsNullReference, "a live String does not read as null")
        assertFalse(Value.Known(HeapRef(0), obj).readsAsNullReference, "heap id 0 is an identity, not a null")
        assertFalse(Value.Known(HeapRef(7), VirtualType.of("[I")).readsAsNullReference, "nor is an array ref")
        // The soundness line: unproven states never answer true.
        assertFalse(Value.Unknown(int).readsAsNullReference, "an Unknown(I) is NOT proved primitive-valued")
        assertFalse(Value.Unknown(str).readsAsNullReference)
        assertFalse(Value.Top.readsAsNullReference, "Top knows nothing, so it proves nothing")
    }

    @Test
    fun heapRefAndBoxedIntNeverConfused() {
        // Bug class: raw untyped Any? letting a heap id masquerade as an integer value.
        assertFalse(Value.Known(HeapRef(5), obj).agreesWith(Value.Known(5, obj)))
    }
}
