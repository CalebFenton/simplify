package org.cf.smalivm.core.op

import org.cf.smalivm.core.CastOutcome
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.InstanceOfTest
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.castOutcome

/**
 * `instance-of vA, vB, type@CCCC` (op 32, format `22c`): stores `1` in `vA` if `vB` holds an instance of
 * the type operand and `0` if it does not.
 *
 * **SPEC** [BC], and this is the whole of it: *"Store in the given destination register `1` if the
 * indicated reference is an instance of the given type, or `0` if not."* Result type is always `Z`.
 *
 * **It never branches.** `Format22c`, `canContinue=true`, exactly one successor on every row -- the fork
 * machinery is irrelevant here, and an implementation that forked "because the value is unknown" would
 * invent an infeasible path. A type test decides a *value*; it does not choose a *path*. Pinned by
 * `core/op/InstanceOfOpTest.aTypeTestNeverForks` through the three channels that can see fork arity.
 *
 * **Side effects: NONE; `recordSideEffect` is called nowhere in this file.** One register write, which is
 * covered by [registersAssigned] rather than by the effect channel, and **not even a class
 * initialisation** -- and the grounds for that are [ART]'s WHOLE `INSTANCE_OF` handler body, fourteen lines
 * at `runtime/interpreter/interpreter_switch_impl-inl.h:837-850`: `ResolveVerifyAndClinit`, an
 * `if (UNLIKELY(c == nullptr)) return false;`, one `GetVRegReference(B())`, one
 * `SetVReg(A(), (obj != nullptr && obj->InstanceOf(c)) ? 1 : 0)`, `return true`. Nothing allocates, no field
 * is touched, nothing else is called -- so there is nothing in the opcode that *could* initialise a class,
 * and the resolution below is effect-free (`docs/DALVIK-SEMANTICS.md#instance-of-22c`).
 *
 * ⚠️ **Do NOT re-ground this on `can_run_clinit = false`; that inference is INVALID and it is what this KDoc
 * used to assert.** The flag is verbatim (fourth positional argument at `:842`, parameter list at
 * `runtime/entrypoints/entrypoint_utils.h:155-161`) and proves only *"this call site does not itself run
 * `<clinit>`"*. **`NEW_INSTANCE` (`interpreter_switch_impl-inl.h:862-894`) passes the same `false` at the
 * same argument position (`:868`) and DOES initialise the class**, downstream in `AllocObjectFromCode` at
 * `:882` -> `CheckObjectAlloc` -> `CheckClassInitializedForObjectAlloc` -> `EnsureInitialized`
 * (`runtime/entrypoints/entrypoint_utils-inl.h:236-252`, `:213-230`, `:185-211`), which is why that handler
 * can assert `AssertInitializedOrInitializingInThread` at `:891`.
 *
 * Declared rather than left implicit, because a spurious
 * `STRONG` would silently stop every constant fold in any method containing a type test
 * (`ExecutionGraph.methodSideEffectLevelOverExploredPaths` maxes over addresses). Pinned by
 * `core/op/InstanceOfOpTest.aTypeTestDeclaresNoSideEffectOnEveryRow`.
 *
 * **THE TYPE OPERAND'S LOADABILITY IS DECIDED AT DECODE TIME; THE THROW HAPPENS AT EXECUTE TIME.**
 * dexlib2 reports `canThrow=true` for this opcode, and it is right about a device: [ART] resolves the type
 * operand **before** reading the register (`ResolveVerifyAndClinit` at `:838-843`, the `nullptr` bail at
 * `:844-846`, and only then `GetVRegReference(B())` at `:847`), so an unresolvable operand throws
 * `NoClassDefFoundError` **even when the source register is null** -- the **opposite** of the [JVMS]
 * ordering, where null short-circuits to `0` first. An implementation written from [JVMS] is wrong here.
 * `OpBuilder.buildInstanceOf` therefore decides `loadable` from the class model (which `core/op/` may not
 * reach -- the two-tier dependency rule) and hands over a plain flag; the `!loadable` row below sits ahead
 * of `frame.read` so the measured ordering is structural rather than commented.
 *
 * 🔴 **DESIGN REVERSAL, Plan 9 Task 15 (ROW-23). This KDoc used to argue for the opposite and the argument
 * is now retracted rather than deleted, because the reason it was wrong is worth inheriting.** Plan 8
 * degraded an unloadable operand to a per-address [org.cf.smalivm.core.UnsupportedOp] and defended it on
 * the grounds that a definite throw *"commits to an exception type that is [ART]-only"*, so a consumer
 * should see a refusal instead of an ART-specific guess. **That premise no longer holds: the exception type
 * is no longer [ART]-only.** It is measured -- `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`,
 * cross-validated `[DEVICE](Tier B, nterp default, API 36)` -- on all five resolving opcodes, each against a
 * present-type control (`tools/art-runner/fixtures/Fx_InstanceOfAbsentType.smali`,
 * `Fx_InstanceOfPresentType.smali`). A `[DEVICE]` measurement outranks the caution that was standing in for
 * one. And the refusal was not neutral: `Aborted(UNSUPPORTED_OP)` makes the whole run `Partial`, which
 * withdraws trust from every other address in the method to avoid answering one -- expensive for a
 * deobfuscator, on the single most common hostile shape there is (an obfuscated APK referencing a class it
 * does not contain). See [resolutionFailure] for the family and the `Error`-not-`Exception` consequence, and
 * `core/op/InstanceOfOpTest.anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource`.
 *
 * **The semantics are [org.cf.smalivm.core.castOutcome]'s, mapped in four lines below, and this op owns
 * none of them.** That is what stops it and Task 9's `check-cast` disagreeing about one value shape.
 * The two mappings differ on **three** of the five rows -- a null register FAILS an instance test but
 * PASSES a cast (`ReadsAsNull`), "null or an instance" is undecided for an instance test but a provable
 * success for a cast (`NullOrInstance` -- design decision 5), and ambiguity chooses a *value* here where
 * it chooses a *path* there (`Unprovable`). Read the outcome type's KDoc for the table and for why the
 * distinction matters to `check-cast`; do not carry "they differ on the null row" as the whole of it.
 *
 * **The `ReadsAsNull` row's `0` is not one tier of evidence but two, and they cover different halves of it**
 * (2026-08-04). ART's switch interpreter reaches `SetVReg(A(), 0)` through `GetVRegReference(B())` at `:847`
 * for every spelling; nterp's `op_instance_of` (`runtime/interpreter/mterp/x86_64ng/object.S:86-107`) reads
 * the **raw** register instead and stores `0` only when those bits are zero -- for a non-zero primitive it
 * dereferences them. So the null-raw and integral-zero spellings are **[ART] under both tiers**, while
 * `Known(5, "I")` is **VERIFIER** tier: this engine's spec §2.2 choice on input
 * `runtime/verifier/method_verifier.cc:3156-3158` refuses to link. The answer stays `0` -- registry D9's
 * divergence from the oracle is unaffected -- but do not cite [ART] for that row. See
 * [org.cf.smalivm.core.readsAsNullReference]'s KDoc and `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter
 * tiers* subsection.
 *
 * **`toString` prefixes BOTH registers, deliberately unlike the oracle.**
 * `docs/architecture/opcodes.md:174` records **Correctness Defect 19** -- *"`InstanceOfOp.toString()`
 * omits the `r` prefix on its source register"* -- and none of the seven migrated tests asserted
 * `toString`, so the defect had no corpus protection and there is nothing to keep byte-identical.
 */
internal class InstanceOfOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val sourceRegister: Int,
    private val targetType: VirtualType,
    private val types: InstanceOfTest,
    private val loadable: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(sourceRegister)

    /** Never wide: the result is a `Z`, so the destination is one register. */
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        // RESOLUTION PRECEDES THE NULL SHORTCUT, so this row sits ahead of `frame.read` -- measured, not
        // assumed: `Fx_InstanceOfAbsentType` tests a NULL source against an absent type and a device throws
        // rather than folding `0`. Reversing these two lines would fold a `Known(false, Z)` a device never
        // produces, which is the ONE row where this op's "it never branches" invariant would become a lie
        // (it still does not branch here: a definite throw is one transition).
        if (!loadable) return resolutionFailure(frame, targetType)
        val source = frame.read(sourceRegister)
        val outcome = castOutcome(source, targetType, types) { frame.heapObject(it)?.type }
        val result = when (outcome) {
            // Null is an instance of nothing. INHERITED, [JVMS] `instanceof`: "If objectref is null, the
            // instanceof instruction pushes an int result of 0 as an int onto the operand stack." [BC]
            // never mentions null at all. `check-cast` reads this SAME outcome as a success, which is why
            // it is its own case rather than folded into Never.
            CastOutcome.ReadsAsNull -> Value.Known(false, BOOL)
            CastOutcome.Never -> Value.Known(false, BOOL)
            CastOutcome.Definitely -> Value.Known(true, BOOL)
            // 🔴 An `Unknown` MAY BE NULL, so a satisfying declared type does not decide this. The oracle
            // folds `Known(true, Z)` here and `docs/architecture/opcodes.md:173` files that as its
            // Correctness Defect 18; registered as a divergence rather than reproduced.
            //
            // The two rows below are ONE answer here and TWO in `check-cast`, which is why they are two
            // cases in `castOutcome` rather than one: for a cast, "null or an instance" is a provable
            // SUCCESS because both disjuncts pass, while for an instance test the disjuncts give `0` and
            // `1` (design decision 5). Kept as separate branches rather than merged with a comma, so that
            // this file states the answer for each outcome rather than inheriting one by grouping.
            CastOutcome.NullOrInstance -> Value.Unknown(BOOL)
            CastOutcome.Unprovable -> Value.Unknown(BOOL)
        }
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, result)))
    }

    override fun toString(): String = "$name r$destRegister, r$sourceRegister, $targetType"

    private companion object {
        val BOOL: VirtualType = VirtualType.of("Z")
    }
}
