package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * `fill-array-data` (op 26, format **31t** = THREE code units, so the successor of an op at 0 is address 3
 * and addresses 1 and 2 do not exist). ONE op that does the whole fill in ONE step and returns
 * [Transition.Continue].
 *
 * **The payload address is DATA, not code — design decision 3, and it is SPEC-backed rather than merely
 * convenient.** The engine being replaced jumped to the `.array-data` pseudo-instruction and let a second op
 * execute there; that op then read its own return address and its target register off its **parent node's**
 * state (`opcode/FillArrayDataPayloadOp.kt`'s `getPsuedoInstructionReturnAddress()` and
 * `parent.registersAssigned.toList()[0]`) — the hidden control state spec §5.2 says must stop. This op
 * receives its payload already decoded, at BUILD time, from [org.cf.smalivm.core.OpBuilder], which is also
 * the only place a dexlib2 `ArrayPayload` may be named (the two-tier dependency rule: nothing under
 * `core/op/` may import `com.android.tools.smali.dexlib2`).
 *
 * The grounding, quoted in `docs/DALVIK-SEMANTICS.md#fill-array-data-op-26-31t-and-the-array-data-payload`:
 * [BC]'s "General design" says payload pseudo-instructions *"must never be encountered during the normal
 * flow of execution"*, and **[CON] B22** makes it a verifier constraint — *"The **packed-switch-data,
 * sparse-switch-data**, and fill-array-data pseudo-instructions must not be reachable by control flow."*
 * [BC] even writes the operand as a *"branch"* offset in scare quotes. B22 is Dalvik-specific with no JVMS
 * analogue: the JVM has no payload pseudo-instruction, because `tableswitch`/`lookupswitch` carry their data
 * inline. **Plan 8's switch payloads are data by the same rule** and should be decoded at build time
 * exactly as this op is; the projection convention in `SmaliVmUnderTest.run` is already written to cover
 * them.
 *
 * Rejected alternative, so it is not re-opened: keeping the jump with the return address resolved at decode
 * time. It preserves visitation parity with the engine being replaced, but the return address is only
 * derivable by scanning the method for the `fill-array-data` that targets this payload, and that reverse
 * lookup is **ambiguous** when two instructions target one payload. A single op has no such hazard.
 *
 * **NEITHER exception is INHERITED, which is unusual for this plan.** Every other array opcode's NPE /
 * AIOOBE / `ArrayStoreException` comes from [JVMS] §6.5 by way of [VER]. `fill-array-data` has no JVM
 * counterpart at all, so there is nothing to inherit:
 *
 *  1. **null array -> `NullPointerException`. [ART] ONLY — genuinely unspecified.** ART's message is
 *     *"null array in FILL_ARRAY_DATA"* and its null check precedes its length check. The zero-idiom half of
 *     the row IS **SPEC**: [BC], *"In terms of bitwise representation, `(Object) null == (int) 0`"*, which is
 *     why [org.cf.smalivm.core.readsAsNullReference] is consulted rather than
 *     [org.cf.smalivm.core.isNull] alone -- and why it also covers a NON-zero primitive-typed `Known`.
 *     **That last extension is VERIFIER tier, not [ART]** (2026-08-04): both of ART's interpreters agree on a
 *     zero raw, but nterp reads this operand RAW (`mterp/x86_64ng/array.S:125-132`) so a **non-zero**
 *     primitive is a tier divergence. See [org.cf.smalivm.core.readsAsNullReference]'s KDoc.
 *  2. **payload longer than the array -> `ArrayIndexOutOfBoundsException`, and NOTHING is stored.** The
 *     REQUIREMENT is **SPEC** ([BC]: the table *"must contain no more elements than will fit in the
 *     array"*); the failure mode is **[ART] ONLY** (*"failed FILL_ARRAY_DATA; length=%d, index=%d"*, whose
 *     second number is the element COUNT rather than a failing index — ART length-checks and then does one
 *     `memcpy`, so the store is all-or-nothing).
 *
 * Both rows are one 20-line ART function, `art::FillArrayData`
 * (`runtime/entrypoints/entrypoint_utils.cc:175-194`), whose raw source was fetched and base64-decoded
 * during Task 10 rather than read through a summarizing tool: null check at `:177-180`, length check at
 * `:183-189` with `payload->element_count` literally as the second format argument, then a single `memcpy`
 * at `:192` sized by the *payload*. That last line grounds three claims at once — the ordering, the
 * all-or-nothing store, and the untouched tail of an over-long array. Quoted per-row in
 * `docs/DALVIK-SEMANTICS.md`.
 *
 * **`Opcode.canThrow()` is WRONG here and is never consulted.** dexlib2 2.5.2 declares
 * `FILL_ARRAY_DATA.flags == 0x0004` — `CAN_CONTINUE` only, no `CAN_THROW`, so `canThrow()` answers
 * **false** — while ART throws both exceptions above. Throw edges are decided from this op's own semantics.
 * (`canContinue()`, the only flag the core does read, IS correct for this opcode.)
 *
 * **Neither throwable carries a message, matching [APutOp]'s NPE and for the same reason.** No corpus
 * expectation exists for this opcode — the engine being replaced synthesized neither exception, it let a
 * host `ArrayIndexOutOfBoundsException` out of `java.lang.reflect.Array.set` escape and kill the run — and
 * [RT] records that runtimes deliberately changed their array-exception text, so matching a particular
 * string would be false precision. ART's two strings are in `docs/DALVIK-SEMANTICS.md`; a later plan may
 * adopt them as an improvement, and nothing here blocks it.
 *
 * **An array LONGER than the payload is legal and its tail is left alone** — **SPEC** ([BC]). No corpus
 * fixture covers it (all ten size the array to the payload), so
 * `FillArrayDataOpTest.anArrayLongerThanThePayloadKeepsItsTrailingElements` pins it rather than leaving a
 * SPEC row unpinned.
 *
 * **`registersAssigned` is EMPTY and [execute] calls `frame.assign` nowhere.** The fill goes into the array
 * OBJECT. This is a **deliberate correction to Task 10's brief**, which prescribed `setOf(arrayRegister)`
 * plus a reassignment to `Unknown(declaredType)` on the two unprovable rows; it is the same claim Task 9
 * adjudicated for `aput` and rejected. On the unprovable row `Unknown(array.type)` reconstructs the
 * identical lattice point and is not even constructible for [Value.Top], which carries no type at all; on
 * the non-tracked-array row it would discard a provable constant while accounting for nothing, because the
 * unmodellable write goes to *memory* and this op does not write the array register. The engine being
 * replaced did assign it, but only as the channel its payload op used to find its target — the hidden
 * control state this design deletes, so mirroring it would copy the syntax and drop the reason. No
 * [org.cf.smalivm.core.ExecutionGraph] `availableRegisters` consequence: `usedRegisters` unions
 * [registersRead], which already names the array register.
 *
 * **The two unprovable rows declare [SideEffectLevel.STRONG], and it is load-bearing.**
 * `Frame.entryFrame` seeds every parameter `Unknown` and no opcode in this build turns a parameter into a
 * tracked array, so a method that fills a caller-supplied array arrives on row 2 — and without the
 * declaration it would report `Complete` *and* `methodSideEffectLevel() == NONE`, the pair
 * `AnalysisContracts`' own KDoc says would let a strategy delete a method whose only observable behaviour is
 * a write its caller can see. A fill into a TRACKED array is `WEAK`: the heap models it exactly, but aliases
 * make the mutation observable. Either definite throw stays `NONE` because nothing was stored. The declaration reaches the node through
 * `Frame.recordSideEffect` -> `ExecutionSession.step`, whose transfer is unconditional and sits ABOVE
 * `contained`'s null check, so a host `Throwable` after the declaration cannot strand it.
 */
internal class FillArrayDataOp(
    address: Int,
    override val name: String,
    private val arrayRegister: Int,
    /**
     * The payload's `element_width` in bytes — 1, 2, 4 or 8 in any payload the verifier accepts. It is the
     * FIRST thing [elementValue] consults, before the component type, which is what makes the width table
     * total and the bit reinterpretations below unambiguous.
     */
    private val elementWidth: Int,
    /**
     * The payload's elements, decoded once at build time. Read-only here; ops are stateless templates
     * (spec §5.2) and this one never mutates the list.
     */
    private val payloadElements: List<Number>,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(arrayRegister)

    /** See the class KDoc: the fill goes into the array object, so no register is written. */
    override val registersAssigned: Set<Int> get() = emptySet()

    override fun execute(frame: Frame): OpExecution {
        val array = frame.read(arrayRegister)

        // ROW 1: the array register READS AS NULL -> a DEFINITE NPE, no fork, nothing stored. THREE spellings
        // of one thing, and the third was added in Plan 7 Task 12's fix round: a null raw; Dalvik's integral
        // zero ([BC], SPEC -- `const/4 v0, 0x0` then `fill-array-data` is the cheapest hostile input in this
        // plan: it passes ART's verifier, throws at runtime, and dexlib2 claims the instruction cannot throw);
        // and ANY primitive-typed `Known`, whatever its value. The third is [ART]'s SWITCH interpreter, in two
        // hops rather than one:
        // `FILL_ARRAY_DATA()` (interpreter_switch_impl-inl.h:920-931) FETCHES the array with
        // `GetVRegReference(A())` at :924 and does not itself null-check -- it hands the result to
        // `art::FillArrayData`, whose `if (UNLIKELY(obj == nullptr)) ThrowNullPointerException("null array in
        // FILL_ARRAY_DATA")` is the throw site (entrypoint_utils.cc:177-180, function at :175-194). And
        // `GetVRegReference` reads ShadowFrame's PARALLEL reference array, which every primitive write clears
        // (shadow_frame.h:190-228; ART's own prose at :401-408), so the fetch yields nullptr. Hence
        // `const/4 v0, 0x5` then `fill-array-data v0, :data` is a definite NPE under `-Xint`, and testing only
        // `isDalvikNull` DROPPED it. See `readsAsNullReference`'s KDoc for the verified chain.
        //
        // TIER, added 2026-08-04: nterp is the DEFAULT interpreter and reads this operand RAW
        // (`op_fill_array_data`, mterp/x86_64ng/array.S:125-132, `GET_VREG` -> art_quick_handle_fill_data).
        // Zero agrees across both tiers -- the same `art::FillArrayData` null check catches it -- so those rows
        // gain a second tier. A NON-ZERO primitive diverges, so that row is VERIFIER tier and NOT an [ART]
        // fact. The answer here does not change: it is the sound direction and matches the switch tier.
        //
        // The superseded "Two spellings of one thing" paragraph that used to sit here was DELETED rather than
        // marked, because it made a bare count claim about this very `if` with no way to tell which of two
        // adjacent numbers was current -- and this whole task exists because a stale reading survived in a
        // comment. The three ops beside this one replaced theirs the same way.
        //
        // The bounds exception does NOT compose with this row: ART's null check PRECEDES its length check,
        // and a null array has no length to compare the payload against.
        if (array.readsAsNullReference) return OpExecution.single(throwing(nullArray()))

        // ROW 2: the array is not provable at all (Unknown or Top), so THREE outcomes are feasible and all
        // three are explored -- it may be null, it may be non-null but shorter than the payload, or it may
        // be long enough. Dropping either throw would leave an unexplored feasible path on a `Complete` run,
        // which spec §2.2 calls the worst outcome this engine can have. This is the same shape as `APutOp`'s
        // own unprovable-array row, which forks both for the same reason; Task 10's brief prescribed only
        // the NPE and that omission is corrected here.
        //
        // The bounds fork is guarded on a NON-EMPTY payload rather than declared unconditionally: with zero
        // elements the SPEC requirement ("no more elements than will fit") holds for every length, including
        // 0, so the throw is provably INFEASIBLE and forking it would invent a reachable handler -- the
        // mirror defect, and the one this plan has shipped twice.
        //
        // STRONG because the fill landed in an object the engine cannot name, so some other tracked object's
        // contents may have changed and no heap state records it. The array REGISTER is deliberately left
        // alone -- see the class KDoc.
        if (array !is Value.Known) {
            val effect = OpUpdate.effect(SideEffectLevel.STRONG)
            val arms = mutableListOf<OpArm>(OpArm.Continue(effect), throwing(nullArray(), effect))
            if (payloadElements.isNotEmpty()) arms += throwing(tooShort(), effect)
            return OpExecution.of(arms)
        }

        // PROVABLY NON-NULL from here down, so the NPE is INFEASIBLE in every row below and must not be
        // forked by any of them.
        val trackedRef = array.raw as? HeapRef
        val tracked = trackedRef?.let { frame.arrayAt(it) }
        if (tracked == null) {
            // ROW 3: a `Known` non-zero value with no tracked array behind it -- a raw that is not a HeapRef
            // at all, or a HeapRef to something else (`frame.arrayAt` answers null for a GuestThrowable,
            // which is NOT the same as a null array). NO NPE (provably non-null) and NO AIOOBE (a bounds
            // exception needs a length this input does not have): both are infeasible, so forking either
            // would invent a reachable handler. Keep this consistent with `AGetOp`, `APutOp` and
            // `ArrayLengthOp` -- a single `tracked == null` fork in any of the four invents a handler for a
            // value just proved non-null.
            //
            // VERIFIER -> the engine's choice: no verified dex reaches this row ([CON] B1), and ART would
            // `memcpy` the payload over whatever those bytes are -- it corrupts the wrong memory, so there
            // is no behavior to copy. What IS recorded is that the write happened somewhere unmodellable,
            // and that is an EFFECT rather than a value.
            return OpExecution.single(OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)))
        }

        // ROW 4: the payload does not fit -> a DEFINITE AIOOBE and NOTHING is stored. All-or-nothing rather
        // than a partial fill is [ART]: FillArrayData length-checks and then does a single `memcpy`, which
        // is also why its message can report a COUNT where an index would go. The requirement itself is
        // SPEC ([BC]). The engine being replaced did not check this at all -- it let `Array.set` raise and,
        // catching nothing, killed the whole run.
        if (payloadElements.size > tracked.length) return OpExecution.single(throwing(tooShort()))

        // ROW 5: the fill. Object over tag: the component is the ARRAY's own, never the register's, because
        // the slot's type is a property of the array (`canGetObject`-style loose tags are deliberate in the
        // corpus). A tracked array's type is always a real array descriptor
        // (`HeapObject.TrackedArray`'s KDoc, enforced at allocation), so this is an invariant rather than a
        // case to be lenient about. Only the LEADING `payloadElements.size` slots are written; the
        // remainder is left alone -- SPEC ([BC]).
        val component = requireNotNull(tracked.type.componentType) {
            "Tracked array ${tracked.type} at r$arrayRegister is not an array type: the allocation lost the " +
                "component type (`CoreState.trueTypeOf` / `Frame.allocateArrayObject` -- the harness path " +
                "is the only one that bypasses `HeapStore.allocateArray`'s component check)"
        }
        checkNotNull(trackedRef)
        val write = OpUpdate.writeArrayElements(
            trackedRef,
            0,
            payloadElements.map { payload -> Value.Known(elementValue(payload, component), component) },
        )
        val update = if (payloadElements.isEmpty()) write else write.then(OpUpdate.effect(SideEffectLevel.WEAK))
        return OpExecution.single(OpArm.Continue(update))
    }

    /**
     * The payload element as it lands in the slot: **normalized by [elementWidth] FIRST, then converted by
     * the component type**, with every case named.
     *
     * | width | normalized to |
     * |---|---|
     * | 1 | `Byte` |
     * | 2 | `Short` |
     * | 4 | `Int` |
     * | 8 | `Long` |
     *
     * | component | stored raw |
     * |---|---|
     * | `Z` | `normalized.toInt() == 1` -> `Boolean` (Dalvik has no boolean literal) — see the note below |
     * | `B` | `normalized.toByte()` |
     * | `C` | `normalized.toShort().toInt().toChar()` |
     * | `S` | `normalized.toShort()` |
     * | `I` | `normalized.toInt()` |
     * | `J` | `normalized.toLong()` |
     * | `F` | `Float.fromBits(normalized.toInt())` |
     * | `D` | `Double.fromBits(normalized.toLong())` |
     * | anything else | `normalized`, unchanged — no expectation asserts it |
     *
     * **Simpler than the two-stage version it replaces, which normalized by the payload element's BOXED
     * CLASS and then converted by the host array's component class** and whose second `when` had **no
     * `Byte` branch at all**, so `[B` worked only by falling through its `else`. Deriving the normalization
     * from the payload's declared width instead makes every case reachable by construction.
     *
     * **`F`/`D` are BIT REINTERPRETATIONS, not numeric conversions**, and this is the one place in the
     * engine where that distinction is settled rather than deferred. `CARRY-FORWARD.md`'s cross-domain-raw-
     * read item (Plans 6-7 item 7) is about the opposite situation and does **not** apply here: there, a
     * register's raw is a host `Float` whose *bits* the engine cannot see, so `int-to-float` on it must
     * answer `Unknown` rather than invent a number. Here the payload **declares its width**, so the bits
     * are unambiguous — `0x3f800000` at width 4 IS the bit pattern `1.0f`, exactly as the dex file says.
     *
     * **A width that disagrees with the component gets no runtime check, and this is the engine's own
     * choice.** It is a verifier HARD failure and ART just `memcpy`s raw bytes, so there is no behavior to
     * reproduce — **VERIFIER -> the engine's choice**, justified by spec §2.2 rather than by fidelity: the
     * rule above is total (every width and every component has an answer), deterministic, and invents no
     * constant that is not already in the payload. A width outside {1, 2, 4, 8} — which the verifier also
     * rejects, and which dexlib will not produce from assembled smali — leaves the element unnormalized
     * rather than guessing at a width.
     *
     * **`Z` uses `== 1`, not `!= 0`, and that is a CHOICE with an untested consequence.** A payload byte of
     * `0x2t` into a `[Z` therefore stores `false`, where `!= 0` is the other defensible reading. The choice
     * is inherited verbatim from the engine being replaced (`opcode/FillArrayDataPayloadOp.kt`'s
     * `classValue.toInt() == 1`) and kept for migration parity, and it is **VERIFIER**-tier either way: [BC]
     * gives `Z` the values 0 and 1 only, so a non-{0,1} byte cannot appear in verified dex. No expectation
     * anywhere covers one — `fillArrayDataBoolean`'s payload is `0x1t,0x1t,0x0t,0x1t`, which both readings
     * agree on — so a later plan may switch to `!= 0` freely. Named here for the same reason the `else` row
     * above is: an undisclosed untested row reads as a decided one.
     */
    private fun elementValue(element: Number, component: VirtualType): Any {
        val normalized = normalize(element)
        return when (component.descriptor) {
            "Z" -> normalized.toInt() == 1
            "B" -> normalized.toByte()
            "C" -> normalized.toShort().toInt().toChar()
            "S" -> normalized.toShort()
            "I" -> normalized.toInt()
            "J" -> normalized.toLong()
            "F" -> Float.fromBits(normalized.toInt())
            "D" -> Double.fromBits(normalized.toLong())
            else -> normalized
        }
    }

    /**
     * The width half of [elementValue]'s table, split out so the two stages are separately readable.
     *
     * **MEASURED: three of these four rows are load-bearing and the fourth is a confirmation.** Task 10's
     * battery mutated each row independently against the whole suite: `2 -> toByte()` kills
     * `canFillArrayDataShortWithShorts` (its `0xc8s` = 200 does not fit a byte), `4 -> toShort()` kills
     * `canFillArrayDataFloat` (`0x3f800000` truncates to 0 and `Float.fromBits(0)` is `0.0f`), and
     * `8 -> toInt()` kills `canFillArrayDataDouble` **and** `canFillArrayDataLongWithLongs`.
     *
     * **The `1 -> toByte()` row changes nothing on any input, and neither does [elementValue]'s `"B"`
     * branch — proven rather than assumed.** Making this row a pure identity *and* deleting the `"B"`
     * branch at the same time leaves all 858 tests green, which is only possible if dexlib2 already boxes a
     * width-1 (`t`-suffixed) `.array-data` element as `java.lang.Byte`. So the two are mutually redundant
     * confirmations of a value the decoder already supplies. **Both are kept deliberately**: together they
     * make the stored raw independent of that dexlib2 boxing detail, which is the whole point of naming
     * every case — but neither is *tested behaviour*, and a reader should not believe otherwise. (The
     * brief's Step 7 predicted `canFillArrayDataByte` would die under a width-always-`Int` mutation. It
     * cannot: that prediction describes the two-stage scheme this replaces, where `[B` fell through an
     * `else` and the width-derived normalization WAS load-bearing for it.)
     */
    private fun normalize(element: Number): Number = when (elementWidth) {
        1 -> element.toByte()
        2 -> element.toShort()
        4 -> element.toInt()
        8 -> element.toLong()
        else -> element
    }

    /** See the class KDoc for why there is no message. */
    private fun nullArray(): ThrowableRecipe = ThrowableRecipe.of(NPE)

    /**
     * The payload-does-not-fit exception. No message, and no index either: the offending quantity is the
     * element COUNT rather than an index (ART reports the count in the index position precisely because its
     * store is one `memcpy`), and naming a number the engine would have to pick a convention for is worse
     * than naming none. See the class KDoc.
     */
    private fun tooShort(): ThrowableRecipe = ThrowableRecipe.of(ARRAY_INDEX_OUT_OF_BOUNDS)

    private fun throwing(recipe: ThrowableRecipe, update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
        OpArm.Throw(update, ThrowEvidence.Fresh(recipe))

    /**
     * **A deliberate format divergence**, recorded next to the two already in `CARRY-FORWARD.md`'s Plan 10
     * item 3: the engine being replaced printed `:addr_<n>` with the payload's own address, which this core
     * no longer carries at execution time — the payload is decoded away at build time, which is the whole
     * point. Plan 10's instruction listing should expect `:payload`.
     */
    override fun toString(): String = "$name r$arrayRegister, :payload"

    private companion object {
        val NPE: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")
        val ARRAY_INDEX_OUT_OF_BOUNDS: VirtualType =
            VirtualType.of("Ljava/lang/ArrayIndexOutOfBoundsException;")
    }
}
