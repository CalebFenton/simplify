package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * Reads a tracked array's length. FOUR cases, and the split between the last two is the one that is easy
 * to get wrong:
 *  - **reads as null** -> a definite NPE, with the message ART uses (and the corpus asserts). Wider than
 *    "definitely null", which is what this bullet used to say: it covers a null raw, an integral zero, and
 *    **any primitive-typed `Known`**, because ART's **switch** interpreter's `ARRAY_LENGTH` fetches the array
 *    with `GetVRegReference(B())` and every primitive write clears `ShadowFrame`'s reference slot. See
 *    [org.cf.smalivm.core.readsAsNullReference]. **The tiers split inside this bullet**: the null-raw and
 *    integral-zero rows are corroborated by nterp as well (`mterp/x86_64ng/array.S:111-123` reads the raw and
 *    branches to `common_errNullObject` on zero), while the **non-zero** primitive is a tier divergence and so
 *    carries **VERIFIER** tier rather than [ART] -- see the predicate's KDoc and
 *    `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*;
 *  - a tracked array -> its length, and NO fork, because the throw is provably infeasible;
 *  - a `Known` **reference** with a non-null raw that is NOT a tracked array -> `Unknown(I)` and STILL no
 *    fork. It is provably non-null -- because every primitive-typed `Known` was diverted by the first
 *    bullet, so what is left is a real reference. `frame.arrayAt(ref)` answers null for a HeapRef pointing
 *    at a GuestThrowable, so a single `tracked == null` branch would fork an NPE for a value the engine just
 *    proved non-null -- an invented reachable handler, the mirror of the defect design decision 4 rejects.
 *    VERIFIER -> engine's choice: past the null check, ART's `ARRAY_LENGTH` does `AsArray()` unchecked and
 *    reads whatever word sits at the length offset, which is not a behavior to reproduce;
 *  - `Unknown`/`Top` -> `Unknown(I)` AND the NPE is forked, because nothing is known, both outcomes are
 *    feasible, and dropping the throw would leave an unexplored feasible path on a Complete run.
 */
internal class ArrayLengthOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val arrayRegister: Int,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(arrayRegister)
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        val array = frame.read(arrayRegister)
        // Every spelling of "this register reads as null" -> a DEFINITE NPE. THREE of them, and the third
        // was added in Plan 7 Task 12's fix round: a null raw; an integral zero under a reference type
        // ([BC], SPEC: "In terms of bitwise representation, (Object) null == (int) 0"); and ANY
        // primitive-typed `Known`, whatever its value. The last is [ART]'s SWITCH interpreter, not a guess --
        // its ARRAY_LENGTH fetches the array with `GetVRegReference(B())`
        // (interpreter_switch_impl-inl.h:852-860) and throws an NPE when it is null, and `GetVRegReference`
        // reads ShadowFrame's PARALLEL reference array, which every primitive write clears
        // (shadow_frame.h:190-228; ART's own prose at :401-408). So `const/4 v0, 0x5` followed by
        // `array-length v1, v0` is a definite NPE under `-Xint`.
        // TIER, corrected 2026-08-04: this line used to end "on a real device", and a DEFAULT device runs
        // nterp, whose op_array_length (mterp/x86_64ng/array.S:111-123) reads the RAW register and would
        // dereference the 5. The zero spellings agree across both tiers; the non-zero primitive is a
        // VERIFIER-tier choice (method_verifier.cc:3174-3177 hard-fails "array-length on non-array"), NOT an
        // [ART] behaviour. The answer here does not change -- it is the sound direction and it matches the
        // switch tier exactly. See `readsAsNullReference`'s KDoc for the full split.
        // `isDalvikNull` alone used to be the test here and DROPPED the non-zero case -- an unexplored
        // feasible path on a Complete run.
        if (array.readsAsNullReference) return throwingNullArray()
        if (array is Value.Known) {
            // PROVABLY NON-NULL from here down -- and now for a sound reason rather than the wrong one this
            // comment used to give ("a Known non-zero raw"). What makes it provable is that every
            // primitive-typed `Known` was diverted above, so a `Known` reaching here is a REFERENCE with a
            // non-null raw. The NPE is INFEASIBLE and must not be forked in either of the next two branches.
            val tracked = (array.raw as? HeapRef)?.let { frame.arrayAt(it) }
            if (tracked != null) {
                return continuingWith(Value.Known(tracked.length, INT))
            } else {
                // A HeapRef to a non-array (a GuestThrowable), or a raw that is not a HeapRef at all: input
                // the DEX verifier would have rejected, which is expected input for a deobfuscator.
                // VERIFIER -> engine's choice, and the choice is total-but-uninformative rather than
                // throwing: ART reads whatever word sits at the length offset, which is not a behavior to
                // reproduce. NO NPE -- the oracle keeps its exception child here and over-approximates.
                return continuingWith(Value.Unknown(INT))
            }
        }
        // Unknown / Top: the array may be null, so the throw is feasible and is explored.
        return OpExecution.of(
            OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(INT))),
            OpArm.Throw(evidence = ThrowEvidence.Fresh(nullArray())),
        )
    }

    // The message is load-bearing: ArrayLengthOpTest.nullArrayThrowsExpectedException asserts it, the
    // oracle declares the same string at opcode/ArrayLengthOp.kt:19, and unusually for a message in this
    // plan it has a second source -- [ART] emits exactly it (runtime/common_throws.cc:639-667). CORPUS+ART.
    private fun continuingWith(value: Value): OpExecution =
        OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, value)))

    private fun throwingNullArray(): OpExecution =
        OpExecution.single(OpArm.Throw(evidence = ThrowEvidence.Fresh(nullArray())))

    private fun nullArray(): ThrowableRecipe =
        ThrowableRecipe.of(NPE, "Attempt to get length of null array")

    override fun toString(): String = "$name r$destRegister, r$arrayRegister"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
        val NPE: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")
    }
}
