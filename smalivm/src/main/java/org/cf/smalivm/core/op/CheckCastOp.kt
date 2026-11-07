package org.cf.smalivm.core.op

import org.cf.smalivm.core.CastOutcome
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.FrameValue
import org.cf.smalivm.core.InstanceOfTest
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.ThrowableHierarchy
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.castOutcome
import org.cf.smalivm.core.partitionThrowableDomain
import org.cf.smalivm.core.readsAsNullReference

/**
 * `check-cast vAA, type@BBBB` (op 1f, format `21c`): throws `ClassCastException` if the reference in the
 * register cannot be cast to the type operand, and narrows the register's declared type when it can.
 *
 * **SPEC** [BC], and this is the whole of it: *"Throw a `ClassCastException` if the reference in the given
 * register cannot be cast to the indicated type."* **The exception type is genuinely SPEC rather than
 * INHERITED**, which is rare in this rewrite: [BC] names exactly two `Exception` types in the entire
 * document -- `ArithmeticException` and this one -- so tiering it SPEC is correct in the *less*
 * safe-looking direction (`docs/DALVIK-SEMANTICS.md#check-cast-21c`).
 *
 * **The semantics are [castOutcome]'s, mapped in five branches below, and this op owns none of them.**
 * That is what stops it and [InstanceOfOp] disagreeing about one value shape. The two mappings differ on
 * **three** of the five rows -- a null register PASSES a cast and FAILS an instance test, "null or an
 * instance" is a provable success for a cast and undecided for an instance test (design decision 5), and
 * ambiguity chooses a *path* here where it only chooses a *value* there. Read [CastOutcome]'s table before
 * changing any of them. **Nothing in this file calls `isInstanceOf`**: re-deriving the type test locally is
 * how a `ClassLoadFailure` escapes as `Aborted(HOST_FAILURE)` and how `Value.Top` becomes a
 * `NullPointerException` in the engine, both of which [CastOutcome.Unprovable]'s KDoc names.
 *
 * **Side effects: NONE; `recordSideEffect` is called nowhere in this file.** At most one register write,
 * which is covered by [registersAssigned] rather than by the effect channel -- effects are about state this
 * engine does not own, and dexlib2's `SETS_REGISTER` is a *static type* fact, not a runtime write. It is not
 * even a class initialisation, and the grounds are [ART]'s WHOLE `CHECK_CAST` handler body -- seventeen lines
 * at `runtime/interpreter/interpreter_switch_impl-inl.h:819-835` consisting of `ResolveVerifyAndClinit`, an
 * `if (UNLIKELY(c == nullptr)) return false;`, one `GetVRegReference(A())`, a conditional
 * `ThrowClassCastException`, and `return true`. Nothing allocates and nothing else is called, so there is
 * nothing that *could* initialise a class. ⚠️ **NOT the `can_run_clinit = false` at `:824`, which is what
 * this KDoc used to say and is a non-sequitur:** `NEW_INSTANCE` (`:862-894`) passes the same `false` at the
 * same argument position (`:868`) and DOES initialise, downstream in `AllocObjectFromCode`
 * (`runtime/entrypoints/entrypoint_utils-inl.h:236-252` -> `CheckObjectAlloc` -> `EnsureInitialized`). See
 * `docs/DALVIK-SEMANTICS.md#check-cast-21c`. Declared rather than left implicit, because a spurious
 * `STRONG` would silently stop every constant fold in any method containing a cast. Pinned by
 * `core/op/CheckCastOpTest.aCastDeclaresNoSideEffectOnEveryRow`.
 *
 * **THE TYPE OPERAND'S LOADABILITY IS DECIDED AT DECODE TIME AND THE THROW HAPPENS AT EXECUTE TIME**,
 * exactly as `instance-of`'s does and for the same two reasons: loadability is a static fact about the
 * instruction, and `core/op/` may not reach the class model (the two-tier dependency rule), so `OpBuilder`
 * hands over a plain [loadable] flag rather than a live handle. [ART] resolves the operand *before* reading
 * the register (`interpreter_switch_impl-inl.h:820-825`, the `nullptr` bail at `:826-828`, and only then
 * `GetVRegReference(A())` at `:829`), so an unresolvable operand raises `NoClassDefFoundError` **even for a
 * null register** -- the opposite of the [JVMS] ordering, and the reason the `!loadable` row below sits
 * ahead of `frame.read`. 🔴 **Plan 9 Task 15 replaced a decode-time `UnsupportedOp` refusal with that
 * throw** (ROW-23): the refusal made the run `Partial` where a device has a definite, measured answer.
 * [resolutionFailure] carries the family, the `[DEVICE]` tiering and the `Error`-not-`Exception`
 * consequence. Pinned by `core/op/CheckCastOpTest.anAbsentCastTypeThrowsNoClassDefFoundError` and
 * `core/OpBuilderTest.aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`.
 *
 * **The re-tag REPLACES the declared type rather than intersecting it, and that is grounded rather than
 * lenient.** At runtime `check-cast` writes nothing; dexlib2 nonetheless declares it `SETS_REGISTER`,
 * because [ART]'s verifier narrows the register's static type on the success path --
 * `work_line_->SetRegisterType<LockOp::kKeep>(inst->VRegA_21c(inst_data), res_type)`
 * (`runtime/verifier/method_verifier.cc:3165`), with the comment at `:3122-3128`: *"If this instruction
 * succeeds, we will \"downcast\" register vA to the type in vB. (This could be a \"upcast\" -- not expected,
 * so we don't try to address it.)"* That parenthetical is the evidence that it replaces: ART states it does
 * not address the widening case. **[ART]/UNSPECIFIED** -- documented in no published spec. Conflating
 * `SETS_REGISTER` with "writes a value" corrupts the lattice; ignoring it loses the refinement that makes
 * `check-cast` useful for deobfuscation, so [registersAssigned] is `setOf(targetRegister)` and three
 * migrated tests assert the tag.
 *
 * **One exception to the replacement, and it is a deliberate departure from `opcode/CheckCastOp.kt:28`'s
 * unconditional `assignRegister`:** a re-tag that would stop the register **reading as a null reference** is
 * not applied -- see [narrowedOrUnchanged].
 *
 * 🔴 **The fork is the divergence, and it closes BOTH halves of Correctness Defect 17**
 * (`docs/architecture/opcodes.md:172`, which is two defects in one line: *"Unknown failed casts omit
 * `ClassCastException`, while their normal path omits target-type refinement"*).
 *  - The oracle's unprovable exit is `finishOp(mayThrow = true)` (`opcode/CheckCastOp.kt:38`), and the flag
 *    is **inert for that op**: `finishOp` appends `defaultExceptions` (`UnresolvedChildProducer.kt:36-43`),
 *    `Op`'s constructor defaults it to `arrayOf()` (`opcode/Op.kt:12-13`), and `CheckCastOp` declares
 *    `: Op(location)` with no second argument (`:16-20`). Four sibling ops DO pass one, positionally, so the
 *    mechanism is live and this is the op that requests it without declaring it. The result is one
 *    fall-through child, no exception child, on a run reported complete -- spec §2.2's worst direction, filed
 *    independently at `exceptions.md:77`, `opcodes.md:128` and `state-model.md:193`.
 *  - The refinement half: `:38` performs no `assignRegister` at all, while `:27-29` refines only on the
 *    satisfying path, so the oracle drops the narrowing on exactly the path this op forks. Here the
 *    `Continue` arm carries it and the `Throw` arm does not, which needs
 *    [OpArm.Continue] update. The throw arm retains the pre-op frame.
 *
 * `toString` matches `opcode/CheckCastOp.kt:43` verbatim. Unlike `instance-of`, whose spelling is
 * Correctness Defect 19, there is no oracle defect here to decline to reproduce.
 */
internal class CheckCastOp(
    address: Int,
    override val name: String,
    private val targetRegister: Int,
    private val castType: VirtualType,
    private val types: InstanceOfTest,
    private val throwableHierarchy: ThrowableHierarchy,
    private val loadable: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(targetRegister)

    /**
     * Never wide: a cast operates on a reference, which is one register. Declared even though the runtime
     * writes nothing, because the *declared type* narrowing below is a real write to this engine's lattice
     * and `ExecutionGraph.availableRegisters` is purely static.
     */
    override val registersAssigned: Set<Int> get() = destinationRegisters(targetRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        // RESOLUTION PRECEDES THE NULL CHECK, so this row sits ahead of `frame.read` -- deliberately, and
        // measured rather than assumed: `Fx_CheckCastAbsentType` casts a NULL to an absent type and a device
        // still throws `NoClassDefFoundError`. Reversing these two lines would silently pass a null through
        // and keep the fall-through alive. [resolutionFailure] has the tiering.
        if (!loadable) return resolutionFailure(frame, castType)
        val frameValue = frame.readFrameValue(targetRegister)
        val value = frameValue.value
        val narrowed = narrowedOrUnchanged(value)
        if (frameValue is FrameValue.RefinedThrowable) {
            return castRefinedThrowable(frameValue, narrowed)
        }
        return when (castOutcome(value, castType, types) { frame.heapObject(it)?.type }) {
            // A null passes any cast, and the register is still re-tagged: the narrowing is the VERIFIER's
            // static fact, which does not depend on what the register held. INHERITED, [JVMS] `checkcast`:
            // "If objectref is null, then the operand stack is unchanged." [BC] never mentions null, and its
            // claim that a primitive type operand "will necessarily fail at runtime" is false for exactly
            // this row -- [ART]'s `obj != nullptr` conjunct short-circuits the cast test in the same
            // expression (`interpreter_switch_impl-inl.h:830`). `instance-of` reads this SAME outcome as a
            // definite `0`, which is why it is its own case rather than folded into `Never`.
            CastOutcome.ReadsAsNull -> narrowTo(narrowed)
            // Provably an instance: some type the value is KNOWN to have satisfies the target.
            CastOutcome.Definitely -> narrowTo(narrowed)
            // Provably null-or-an-instance: not a particular object, but every content the tag admits
            // passes. Design decision 5, and the row where this op and `instance-of` reach OPPOSITE
            // conclusions from one outcome -- for a cast the two disjuncts agree, for an instance test they
            // are `0` and `1`. Folding it into `Unprovable` would fork `check-cast v0, Ljava/lang/Object;`
            // on any unknown reference: a cast that cannot fail, i.e. an invented infeasible path.
            CastOutcome.NullOrInstance -> narrowTo(narrowed)
            // Provably NOT an instance and provably not null: a definite `ClassCastException`, no fork. The
            // fall-through is infeasible, so emitting one would keep a dead block alive.
            CastOutcome.Never -> OpExecution.single(
                OpArm.Throw(evidence = ThrowEvidence.Fresh(classCastException(value))),
            )
            // 🔴 THE FORK. Neither outcome is provable, so both are explored -- more paths, never fewer
            // (spec §2.2). The `Continue` arm carries the register NARROWED, because that arm is the one on
            // which the cast succeeded; the `Throw` arm carries it unchanged, because the throw is the proof
            // that it is not of the cast type. `Continue` is FIRST so it is the first path explored
            // (`ExecutionSession`'s `built.asReversed()`), matching the fall-through-first order every
            // other forking op in this build uses.
            CastOutcome.Unprovable -> OpExecution.of(
                OpArm.Continue(OpUpdate.assign(targetRegister, narrowed)),
                OpArm.Throw(evidence = ThrowEvidence.Fresh(classCastException(value))),
            )
        }
    }

    /** Partitions one carried throwable domain without reconstructing handler-routing logic. */
    private fun castRefinedThrowable(
        source: FrameValue.RefinedThrowable,
        narrowed: Value,
    ): OpExecution {
        if (!castType.isReference) {
            return OpExecution.single(
                OpArm.Throw(
                    update = OpUpdate.assign(targetRegister, source),
                    evidence = ThrowEvidence.Fresh(classCastException(source.value)),
                ),
            )
        }
        val partition = partitionThrowableDomain(source.domain, castType, throwableHierarchy)
        val arms = buildList {
            partition.matching?.let { domain ->
                add(
                    OpArm.Continue(
                        OpUpdate.assign(targetRegister, FrameValue.refined(narrowed, domain)),
                    ),
                )
            }
            partition.residual?.let { domain ->
                add(
                    OpArm.Throw(
                        update = OpUpdate.assign(
                            targetRegister,
                            FrameValue.refined(source.value, domain),
                        ),
                        evidence = ThrowEvidence.Fresh(classCastException(source.value)),
                    ),
                )
            }
        }
        check(arms.isNotEmpty()) { "A non-empty throwable domain produced no cast arm" }
        return OpExecution.of(arms)
    }

    override fun toString(): String = "$name r$targetRegister, $castType"

    /** The non-forking success rows: one transition, and the narrowing goes straight onto this frame. */
    private fun narrowTo(narrowed: Value): OpExecution =
        OpExecution.single(OpArm.Continue(OpUpdate.assign(targetRegister, narrowed)))

    /**
     * The register's post-success value: **the raw unchanged, the declared type replaced by the cast type**
     * -- except where that replacement would stop the register reading as a null reference, in which case
     * the value is returned unchanged.
     *
     * A [Value.Unknown] or [Value.Top] becomes `Unknown(castType)`, which for `Top` is a genuine refinement
     * rather than a formality: after a cast to `C` succeeds the register holds null or an instance of `C`,
     * and that is exactly what `Unknown(C)` means.
     *
     * **🔴 The exception is a deliberate departure from the oracle's unconditional re-tag
     * (`opcode/CheckCastOp.kt:28`), and the reason is a wrong answer one opcode downstream.** `Known(5, "I")`
     * passes a cast to `String` because [ART]'s **switch** interpreter reads its reference half as `nullptr`
     * -- and `check-cast` **writes nothing at runtime**: the whole `CHECK_CAST` handler
     * (`runtime/interpreter/interpreter_switch_impl-inl.h:819-835`) contains no `SetVReg*` call of any kind,
     * so that half is still cleared afterwards (`runtime/interpreter/shadow_frame.h:401-408`, and the
     * `References()[i].Clear()` ending each of the four `SetVReg*`). A laundered
     * `Known(5, "Ljava/lang/String;")` no longer satisfies [readsAsNullReference], so a following
     * `instance-of v1, v0, Ljava/lang/Object;` would fold `Known(true, Z)` where a device answers `0` --
     * which is registry D9 (`types-primitive-register-is-instance-of-object`) re-created downstream of the
     * op that closed it. Keying the *rule* on the predicate rather than on `type.isReference` is what keeps
     * the two rows where the re-tag IS safe: `Known(null, T)` reads as null under every tag, and Dalvik's
     * integral zero under a reference tag stays an integral zero ([BC]: *"`(Object) null == (int) 0`"*). So
     * the guard fires on exactly one shape, a **non-zero primitive raw under a primitive tag**.
     *
     * **And on that one shape the precision cost is provably ZERO, which is the fact that keeps this from
     * being a trade.** The verifier hard-fails a `check-cast` whose source register it types as a
     * non-reference, in the same shared `CHECK_CAST`/`INSTANCE_OF` case whose `:3122-3128` comment and
     * `:3165` write are cited in this class's KDoc for the re-tag itself --
     * `runtime/verifier/method_verifier.cc:3156-3158`, verbatim: *"`} else if
     * (!orig_type.IsReferenceTypes()) { Fail(VERIFY_ERROR_BAD_CLASS_HARD) << opcode << " on non-reference in
     * v" << orig_type_reg; return false;`"*. A device refuses to LINK such a method, so there is no runtime
     * refinement being discarded and nothing about this row is a device measurement. The oracle throws a
     * definite `ClassCastException` here (D11's first row), so there is no migrated expectation to lose
     * either.
     *
     * **And that verifier fact is now doing MORE work than it was: it is this row's whole grounding, because
     * the two ART interpreter tiers disagree about the shape the guard fires on** (2026-08-04). nterp -- the
     * default interpreter -- reads a `check-cast` operand out of the raw dex-register array
     * (`runtime/interpreter/mterp/x86_64ng/object.S:1-22`, `GET_VREG` then `testl`/`je`) and dereferences a
     * non-zero value as an object pointer, so there is no *"it reads `nullptr` and a following `instance-of`
     * stores `0`"* to appeal to for `Known(5, "I")`. Tier for this row: **VERIFIER**, spec §2.2, **not
     * [ART]** -- exactly what the paragraph above already argues on its own, now stated as the tier rather
     * than as a bonus. The two shapes the guard *preserves* (`Known(null, T)` and the integral zero under a
     * reference tag) are **[ART] under both tiers**, since nterp branches on those same zero bits. The rule
     * below is unchanged. See [readsAsNullReference]'s KDoc and `docs/DALVIK-SEMANTICS.md`'s *ART has TWO
     * interpreter tiers* subsection.
     *
     * The cost is that the *static* [registersAssigned] can claim a write this row does not make. That is
     * the conservative direction -- a register that might be written is not offered as free scratch space --
     * and it is the same one-sided disagreement `core/op/AGetOp`'s KDoc records for its own wide row.
     *
     * Pinned by `core/op/CheckCastOpTest.everySpellingOfReadingAsANullReferencePassesTheCast`, whose three
     * seeds are one row of each kind.
     */
    private fun narrowedOrUnchanged(value: Value): Value {
        val narrowed = if (value is Value.Known) Value.Known(value.raw, castType) else Value.Unknown(castType)
        if (value.readsAsNullReference && !narrowed.readsAsNullReference) return value
        return narrowed
    }

    /**
     * The exception, built from the **declared** type rather than from `raw.javaClass`, mirroring
     * `opcode/CheckCastOp.kt:32` (`ClassNameUtils.internalToBinary(target.type) + " cannot be cast to " +
     * castType.binaryName`).
     *
     * **The TYPE is SPEC and the MESSAGE is CORPUS, and the two tiers must not be conflated.** [BC] names
     * `ClassCastException` outright, which is why this op's exception is one of the very few in this rewrite
     * that is not inherited. The text is held only so `core/op/CheckCastOpTest`'s migration of
     * `objectNotOfCastTypeThrowsClassCastException` stays byte-identical: [RT] records that runtimes changed
     * exception text deliberately, so an asserted message is never evidence of Dalvik fidelity, and [ART]'s
     * own string for this condition is built from `obj->GetClass()` -- the *runtime* class -- not from the
     * declared tag.
     *
     * A value carrying **no type at all** ([Value.Top]) gets no message rather than a fabricated one, the
     * same rule `core/op/APutOp`'s speculative `ArrayStoreException` follows.
     */
    private fun classCastException(value: Value): ThrowableRecipe {
        val declared = value.type?.let { binaryNameOf(it) }
        return ThrowableRecipe.of(
            CLASS_CAST_EXCEPTION,
            if (declared == null) null else "$declared cannot be cast to ${binaryNameOf(castType)}",
        )
    }

    /**
     * A descriptor as Java's binary name, mirroring the oracle's `ClassNameUtils.internalToBinary` exactly
     * rather than importing it: `org.cf.util` is oracle-side plumbing and `core/op/` may not depend on it.
     * The same four lines `core/op/APutOp` carries, for the same reason and with the same `V` row -- without
     * it a `void` descriptor would take the `else` branch and raise `StringIndexOutOfBoundsException`,
     * turning impossible input into a host failure.
     *
     * A primitive descriptor is reachable here through the *declared* type of a value whose null-reading row
     * did not fire (a `Value.Unknown("I")` -- see [CastOutcome.Unprovable]'s recorded over-approximation),
     * so the primitive rows are live rather than decorative.
     */
    private fun binaryNameOf(type: VirtualType): String = when (val descriptor = type.descriptor) {
        "Z" -> "boolean"
        "B" -> "byte"
        "C" -> "char"
        "S" -> "short"
        "I" -> "int"
        "J" -> "long"
        "F" -> "float"
        "D" -> "double"
        "V" -> "void"
        else ->
            if (descriptor.startsWith("[")) descriptor.replace('/', '.')
            else descriptor.substring(1, descriptor.length - 1).replace('/', '.')
    }

    private companion object {
        val CLASS_CAST_EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/ClassCastException;")
    }
}
