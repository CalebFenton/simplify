package org.cf.smalivm.core

/**
 * A reference into the VM-tracked heap (spec §4.2). Aliasing is "same id", never host
 * `==` — the raw of a [Value.Known] for a tracked mutable object is a HeapRef, not the
 * live host object.
 */
data class HeapRef(val id: Int)

/**
 * The three-point value lattice (spec §4.1) — the reconciliation primitive.
 *
 *  - [Known]  : definitely this value (raw == null means definitely the null reference).
 *  - [Unknown]: value not known; type approximately known. *May* be null.
 *  - [Top]    : nothing known (join of incompatibles).
 *
 * `UninitializedInstance` is deliberately not a lattice point: a `new-instance` result is
 * `Known(HeapRef, T)` whose heap object is in an uninitialized state (Plan 6+).
 */
sealed interface Value {
    val type: VirtualType?

    data class Known(val raw: Any?, override val type: VirtualType) : Value
    data class Unknown(override val type: VirtualType) : Value
    data object Top : Value {
        override val type: VirtualType? = null
    }
}

/** Definitely-null check (spec §4.1: nullness is explicit, only Known(null) IS null). */
val Value.isNull: Boolean get() = this is Value.Known && raw == null

/**
 * The primitive-bits half of Dalvik's null: a primitive-tagged [Value.Known] whose raw is integral zero.
 *
 * **SPEC** -- the Dalvik bytecode reference ([BC], "General design"): *"In terms of bitwise
 * representation, `(Object) null == (int) 0`"*. A Dalvik register is 32 (or 64) bits with no type of its
 * own, so `const/4 v0, 0x0` -- the null-reference idiom every obfuscator emits -- produces exactly
 * `Known(0, "I")` with this engine's op set. An op that tests only [isNull] therefore treats that
 * register as a non-null object and silently DROPS a definite `NullPointerException`.
 *
 * Both predicates exist rather than one, because they answer different questions: [isNull] is "this is
 * the null reference as a reference", which is what handler resolution and consensus need, while this is
 * "the runtime would treat these bits as null", which is what an op dereferencing a register needs.
 *
 * **Where the boundary is drawn, and why.** Integral means [JLS] §4.2's integral types -- `byte`,
 * `short`, `int`, `long` **and** `char`. `Boolean`, `Float` and `Double` are deliberately excluded even
 * though `false` and positive zero share the all-zero bit pattern: no Dalvik `const` opcode produces a
 * `Z`/`F`/`D`-tagged zero (the narrow forms are all tagged `I` -- see `OpBuilder`'s type-tag note), so
 * such a value can only arrive from a test seed or from another engine's coercion, and widening the
 * predicate to cover it would claim more than the quoted sentence supports.
 *
 * **Exactly one OP reads this directly now, and that is the point of the split below.** Every op that
 * *dereferences* a register — `throw`, `array-length`, `aget`, `aput`'s array operand, `fill-array-data` —
 * moved to [readsAsNullReference] in Plan 7 Task 12's fix round, which subsumes this predicate and adds the
 * primitive-typed case ART actually requires. The one remaining op-level caller is `aput`'s
 * **value**-coercion rule (`APutOp`'s rule 1, gated on the `OBJECT` suffix), which is a genuinely different
 * question: whether storing primitive integer bits into a reference component stores `null`.
 *
 * **Why `aput`'s value operand needs the suffix gate and a dereference never does.** `aput` has eight
 * spellings and only `aput-object`'s value operand sits in a reference context, so the mnemonic is the only
 * discriminator available. A dereferenced operand is a reference context by construction — for `throw`,
 * [CON] B16 says so outright: *"The A operand of a throw instruction must be assignment-compatible with
 * java.lang.Throwable."*
 */
val Value.isDalvikNull: Boolean
    get() = this is Value.Known && !type.isReference && when (val r = raw) {
        is Byte, is Short, is Int, is Long -> (r as Number).toLong() == 0L
        is Char -> r.code == 0
        else -> false
    }

/**
 * Whether a **reference-operand read** of this register yields `null` — the question every op that
 * *dereferences* a register asks, as opposed to [isDalvikNull]'s narrower "are these bits an integral zero".
 *
 * **TIER-DIVERGENCE(nterp):** on a **non-zero** primitive under a reference tag, `nterp` dereferences the raw
 * bits as a pointer and **the VM dies** (SIGSEGV whose fault address IS the primitive, or an abort in ART's
 * implicit-null-check window); the switch interpreter reads null. Measured on the quarantined Tier-B-only
 * probes (`tools/art-runner/tier-b-only/`). We model `-Xint` (see DALVIK-SEMANTICS.md "THE MODELLED TIER") —
 * and here that is not merely a default but the only option, because *"the VM dies"* is not an analysis
 * answer this engine can carry. Consequence: the sound null-read stands as a spec §2.2 choice on
 * verifier-rejected input, not as a claim about what a device does. The **zero** case has no divergence: both
 * tiers agree.
 *
 * **[ART]'s ShadowFrame interpreter — NAME THE TIER, because ART has two and they do not agree here.**
 * Verified from raw source 2026-07-28 (`?format=TEXT` piped through `base64 -d`, `refs/heads/main`,
 * re-checked against `android14-release`); the tier split below was added 2026-08-04 from the same method.
 * The chain in this paragraph is the **switch** interpreter, the one `-Xint` forces. ART's *default* on a
 * modern device is **`nterp`**, which reads the same operands out of the RAW dex-register array — see
 * **The two tiers** below before quoting any of this as *"[ART] does X"*. A Dalvik register is not one
 * machine word: `ShadowFrame` is *"a two-part array"* whose second half holds only references, and ART
 * states the invariant in its own prose (`runtime/interpreter/shadow_frame.h:401-408`):
 *
 * > *"when a primitive is stored in vX, the second (reference) part of the array will be null. When a
 * > reference is stored in vX, the second (reference) part of the array will be a copy of vX."*
 *
 * It is maintained on **every** primitive write, not merely documented: `SetVReg` (`:190-197`),
 * `SetVRegFloat` (`:199-206`), `SetVRegLong` (`:208-217`) and `SetVRegDouble` (`:219-228`) each end with
 * `References()[i].Clear()`, which is `reference_ = 0` (`runtime/mirror/object_reference.h:140-143`). A
 * debug build even `CHECK`s it (`shadow_frame.h:346-355`, `CheckConsistentVRegs`). The reference half is
 * allocated unconditionally (`ComputeSize`, `:76-79`; `grep -nw HasReferenceArray` finds nothing in current
 * ART) and zeroed by the constructor (`:358-365`; Task 12's fix round briefly "corrected" this to
 * `:357-364`, which was wrong -- `:357` is ` private:` and the closing brace is `:365`. The `memset` itself
 * is `:364`). So `GetVRegReference(i)` (`:174-183`) — which is how
 * `THROW`, `ARRAY_LENGTH`, `HandleAGet`, `HandleAPut`, `MONITOR_*`, every field op **and
 * `DoFilledNewArray`'s reference-component fill** (`interpreter_common.cc:1463-1464`) fetch their object
 * operand — returns **`nullptr`** for a register whose last write was a primitive.
 *
 * **`DoFilledNewArray` was missing from that list, and its absence is exactly how the sixth consumer got
 * missed** (the whole-branch review's C1). Task 12's sweep covered the ops with an **array operand**;
 * `filled-new-array` has none — it *creates* the array — but it still reads a register **as a reference**,
 * which is the actual trigger. **State the trigger that way**: "reads a register as a reference", never
 * "holds an array". The field and monitor ops Plan 9 inherits are the same shape.
 *
 * **Consequence, and it is the whole reason this predicate exists separately:** the switch interpreter does
 * **not** reinterpret a primitive's bits as a pointer. `const/4 v0, 0x5` then `throw v0` reaches the same
 * definite, message-less `NullPointerException` as `const/4 v0, 0x0` — the *value* is irrelevant to it, only
 * the fact that the last write was primitive. The three clauses below are that fact expressed over this
 * engine's lattice, and **they do not all carry the same tier** — see **The two tiers** below:
 *
 *  - [isNull] — a definite null reference (spec §4.1). **Both tiers agree**;
 *  - a **primitive-typed** [Value.Known] — any value, zero or not, because a primitive write cleared the
 *    reference slot. **This is the one clause the two tiers CONTRADICT each other on, for a non-zero raw**,
 *    so its tier is **VERIFIER** (our design choice under spec §2.2) rather than **[ART]**;
 *  - a reference-tagged boxed number is a live pointer. Its numeric payload never supplies reference bits.
 *
 *    **This bullet used to add "No op in this build produces one", and that was FALSE** — the whole-branch
 *    review's C1. `filled-new-array` with a reference component produced exactly `Known(0, "L…;")` for
 *    `new String[]{null}`, so the clause was load-bearing on real op output rather than only on a seed, and
 *    three documents (this one, `FilledNewArrayOp`'s KDoc, `ThrowRoutingTest`'s) asserted otherwise. It no
 *    longer does, now that the op consults this predicate — but **do not restore the claim**. It is a
 *    property of every op in the build at once, so it is false the moment any op stops asking, and a reader
 *    who trusts it skips the check that would have caught this.
 *
 * ## The two tiers — where they corroborate this predicate and where they contradict it
 *
 * **ART ships two interpreters and they read a register differently, so *"[ART] does X"* is under-specified
 * for anything that depends on how a register is READ.** Verified by raw fetch at `refs/heads/main`,
 * 2026-08-04. `nterp` keeps its own parallel reference array and clears it on every primitive write exactly
 * as `ShadowFrame` does (`runtime/interpreter/mterp/x86_64ng/main.S:157-160`'s `SET_VREG` writes
 * `VREG_ADDRESS` *and* zeroes `VREG_REF_ADDRESS`), but its **handlers do not consult it**: `GET_VREG`
 * (`:144-146`) reads `VREG_ADDRESS`, i.e. `(rFP,…)`, while `GET_VREG_OBJECT` (`:148-150`) reads
 * `VREG_REF_ADDRESS`, i.e. `(rREFS,…)` (`:88-91`, `rFP` `:68`, `rREFS` `:75`), and every dereferencing
 * handler uses the **raw** one. `arm64ng` is the same split over `xFP` versus `xREFS`
 * (`arm64ng/main.S:184-189`, `xREFS` at `:84`). Which tier runs is a runtime decision:
 * `mterp/nterp.cc`'s `CanRuntimeUseNterp()` (`:60-74`) requires `IsNterpSupported()` (`:37-58` — every ISA
 * Android ships on, subject to two build constants) **and** `!instr->InterpretOnly()`, and `-Xint` sets that
 * (`parsed_options.cc:139-141` -> `runtime.cc:1745-1747`'s `ForceInterpretOnly()` ->
 * `instrumentation.h:361-363`, read back at `:375-377`). So the chain above is what `-Xint` measures and
 * **not** what a default run measures.
 *
 *  - **Where the raw is ZERO — [isNull] and [isDalvikNull] — the two tiers AGREE, which STRENGTHENS those
 *    rows rather than weakening them.** nterp branches on the raw being zero and takes the null path:
 *    `x86_64ng/array.S`'s `op_array_length` (`:111-123`), `op_aget` (`:1-38`) and `op_aput` (`:58-80`) all
 *    do `GET_VREG` then `testl`/`je common_errNullObject`; `control_flow.S`'s `op_throw` (`:171-176`) hands
 *    the raw to `art_quick_deliver_exception`, whose `artDeliverExceptionFromCode`
 *    (`runtime/entrypoints/quick/quick_throw_entrypoints.cc:51-69`) says in its own comment *"exception may
 *    be null, in which case this routine should throw NPE"* and does; and nterp's own `DoFilledNewArray`
 *    (`mterp/nterp.cc:683-756`) stores `reinterpret_cast<mirror::Object*>(regs[src_reg])` at `:751-752`,
 *    which for a zero raw **is** `nullptr`. Same answers as `GetVRegReference`'s `nullptr`, reached by a
 *    different mechanism — two independent tiers, plus verifier-VALID input (`reg_type.h:167-169`:
 *    `IsReferenceTypes()` admits `IsZero()`/`IsNull()`, and `VerifyAGet` allows `IsZeroOrNull()` outright at
 *    `method_verifier.cc:5035-5037`, commenting *"Null array class; this code path will fail at runtime"*).
 *  - **Where the raw is a NON-ZERO primitive — the second clause, `Known(5, "I")` — the two tiers
 *    CONTRADICT each other.** The switch interpreter reads `nullptr` and answers null-like; nterp reads the
 *    raw `5`, finds it non-zero, and **dereferences it as an object pointer** (or, for
 *    `filled-new-array`, stores it into an object array as one). The failure class is certain — an invalid
 *    pointer is dereferenced — and it is now **MEASURED** (Task 9, Tier B, API 36, default `dalvikvm`,
 *    x86_64): nterp faults on the raw value as a pointer — a **SIGSEGV whose fault address is the primitive
 *    itself** when that address is unmapped (`instance-of 0x7fabc000` → fault `0x7fabc000`, backtrace
 *    `nterp_op_instance_of`), or a **process abort** when the fault lands in ART's implicit-null-check window
 *    (ART tries to synthesize an NPE for a type op and cannot). Either way the process dies; the switch tier
 *    (`-Xint`) instead returns `0` / succeeds. See `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers*
 *    → *Measured on Tier B*. This does NOT change the engine's answer (below); it fills in the outcome the
 *    tier row's evidence line previously left unmeasured.
 *  - **Therefore the honest tier for that row is VERIFIER, not [ART]**, and it is unreachable on verified
 *    input for every consumer: `method_verifier.cc` hard-fails a non-reference register with
 *    `VERIFY_ERROR_BAD_CLASS_HARD` — which rejects the whole class — for `check-cast`/`instance-of`
 *    (`:3156-3158`, in the shared case at `:3120-3121`), `throw` (`:3253-3255`), `array-length`
 *    (`:3174-3177`) and `aget`/`aput` (`VerifyAGet`'s `:5054-5055`). No linkable APK can present it.
 *  - **The engine's answer does NOT change**, and that is the point of recording this: reading the register
 *    as null is the sound direction (`instance-of` -> `0`, `check-cast` succeeds, every dereference -> a
 *    definite NPE), it matches one real tier exactly, and the alternative — modelling a wild pointer
 *    dereference — is not a behaviour an analysis engine can or should reproduce. What changes is the
 *    *claim about why*: this is a spec §2.2 choice on input no device links, not a measured ART behaviour.
 *
 * **This is a general instrument, not a footnote about one predicate** — see
 * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers* subsection, which every future
 * register-read claim has to pass through.
 *
 * **[Value.Unknown] and [Value.Top] are NOT included, and that boundary is the soundness line.** An
 * `Unknown(I)` is a register the engine could not prove anything about; answering "definitely null" for it
 * would replace a fork with a definite throw on unproven grounds, which is the narrowing direction spec §2.2
 * forbids. This predicate only ever answers `true` for a **proved** state.
 *
 * **The question this answers is "what does the op READ this register AS", not "is this value being
 * stored".** Both spellings exist and the boundary moved once, so it is stated precisely. `aput`'s rule 1
 * asks a *coercion* question — whether the bits of integer 0 mean `null` in a reference component — which is
 * [isDalvikNull]'s, and is why that call site stays gated on the `OBJECT` suffix. `filled-new-array`'s
 * reference component asks *this* question even though the value it reads is then stored, because ART
 * literally calls `GetVRegReference` on the element register
 * (`interpreter_common.cc:1464`) and there is no `storeCheck` behind it to refuse the wide rows the way
 * [org.cf.smalivm.core.op.APutOp]'s rule 4 does. See that op's `stored` for the full comparison.
 */
val Value.readsAsNullReference: Boolean
    get() = isNull || (this is Value.Known && !type.isReference)

/**
 * Semantic agreement (spec §4.1) — the primitive under consensus. Two values agree iff
 * they are interchangeable for reconciliation:
 *  - Known vs Known, same type: heap-id equality when both raws are [HeapRef];
 *    structural value equality otherwise (null == null included). A HeapRef never
 *    equals a non-HeapRef raw.
 *  - Everything else — differing types, any Unknown, any Top — does NOT agree.
 *    Unknown never agrees with itself: "I don't know" twice is not "the same value".
 *
 * Preconditions this function trusts about [Value.Known.raw]: a non-[HeapRef] raw is
 * compared with the host `equals`, which MUST be symmetric and stable across calls — only
 * host-immutable or otherwise configured-value types belong in a non-HeapRef raw; any
 * mutable VM-tracked object MUST instead be represented as a [HeapRef]. A raw's boxed
 * runtime type is also expected to match the declared [Value.type] (e.g. never store a
 * `Long` under type `I`); this function does not itself enforce that — Plan 4 arithmetic
 * is where such mismatches get normalized.
 */
fun Value.agreesWith(other: Value): Boolean {
    if (this !is Value.Known || other !is Value.Known) return false
    if (type != other.type) return false
    val a = raw
    val b = other.raw
    return when {
        a is HeapRef || b is HeapRef -> a is HeapRef && b is HeapRef && a.id == b.id
        else -> a == b
    }
}
