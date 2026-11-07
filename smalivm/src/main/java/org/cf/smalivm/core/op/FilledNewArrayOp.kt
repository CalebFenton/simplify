package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.AllocationTarget
import org.cf.smalivm.core.Frame
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
 * `filled-new-array` (op 24, format **35c**) and `filled-new-array/range` (op 25, **3rc**) -- both THREE
 * code units, so the successor of an op at 0 is address **3** and addresses 1 and 2 do not exist.
 *
 * **SPEC** [BC], quoted in `docs/DALVIK-SEMANTICS.md#filled-new-array-op-24-35c-and-range-op-25-3rc`:
 * *"Construct an array of the given type and size, filling it with the supplied contents. The type must be
 * an array type. The array's contents must be single-word (that is, no arrays of long or double, but
 * **reference types are acceptable**). The constructed instance is stored as a "result" … so the constructed
 * instance must be moved to a register with an immediately subsequent move-result-object instruction (if it
 * is to be used)."*
 *
 * It builds a **one-dimensional** array of the instruction's own type -- it does *not* build an
 * N-dimensional one. The engine being replaced said so in a comment and then contradicted itself in code:
 * `opcode/FilledNewArrayOp.kt` named its registers `dimensionRegisters`, hardcoded `[I` for the result
 * regardless of the declared type, and coerced every element through `toInt()`. Its own comment explains the
 * real use: building the argument for `java.lang.reflect.Array.newInstance`.
 *
 * ## The rows, and the tier of each
 *
 * | Component | Elements | Result | Tier |
 * |---|---|---|---|
 * | `I` | `Coerce.toProvableInt(raw)` under the component's type, or `Unknown(I)` for that slot | fresh tracked `[I`, `[Continue]` | **SPEC** for legality; the coercion is **ORACLE** parity for every raw the two agree on, and **VERIFIER** -> spec §2.2 for the `Float`/`Double` raw they do not (see [stored]) |
 * | a reference (`[Ljava/lang/String;`, `[[I`, …) | what `GetVRegReference` reads -- the raw verbatim for a register holding a reference, **`null`** for one whose last write was primitive ([org.cf.smalivm.core.readsAsNullReference]) | fresh tracked array, `[Continue]` | legality **SPEC** -- [BC]: *"reference types are acceptable"*; the null row **[ART], BOTH TIERS** for a zero raw (`interpreter_common.cc:1463-1464` and nterp's `mterp/nterp.cc:751-752`), **VERIFIER** for a non-zero primitive -- see [stored] |
 * | unresolvable component | `[Throw(NoClassDefFoundError)]`, definite | | **INHERITED** ([JVMS] §5.4.3.1) -- see below |
 * | `Z`/`B`/`C`/`S`/`F` | `[Throw(InternalError)]`, definite | | **[ART] only** |
 * | `J`/`D`, or a non-array type | never decoded: [org.cf.smalivm.core.OpBuilder] answers `UnsupportedOp` | | **SPEC** forbids the input; **VERIFIER** -> the engine's choice of refusal |
 *
 * ## Every exception, with the JVM-counterpart question answered explicitly
 *
 * The Global Constraints' *"every array exception is INHERITED from [JVMS] §6.5"* was corrected in `c0281bc`
 * because inheritance needs a JVM instruction to inherit **from**. `filled-new-array` is **not** an
 * instruction the JVM has: the JVM allocates with `anewarray`/`newarray` and then fills with a `dup` /
 * index / value / `*astore` sequence, so there is no single counterpart. Asked per exception:
 *
 *  - **A linking error while resolving the type reference (`NoClassDefFoundError` and friends) --
 *    INHERITED, but via the resolution PROCEDURE rather than via a counterpart instruction.** [JVMS]
 *    §5.4.3.1 governs resolution of *any* class/interface/array symbolic reference and is not
 *    instruction-specific; every instruction carrying one (`new`, `anewarray`, `checkcast`, `getfield`, …)
 *    documents that its exceptions can be thrown. `filled-new-array`'s operand is exactly such a reference,
 *    and its allocation half *does* have a counterpart -- `anewarray`, whose [JVMS] §6.5 entry says *"During
 *    resolution of the symbolic reference to the class, array, or interface type, any of the exceptions
 *    documented in §5.4.3.1 can be thrown"*. So this is the SAME ruling as `new-array`'s (design decision 4)
 *    for the same reason, deliberately not a second answer, and it consumes the same
 *    `ClassIndex.isLoadable`. [ART] agrees that this is where it happens: `DoFilledNewArray` calls
 *    `ResolveVerifyAndClinit` and returns with a pending exception if it answers null.
 *  - **`java.lang.InternalError` for `[Z`/`[B`/`[C`/`[S`/`[F` -- [ART] ONLY, and emphatically NOT
 *    inherited.** There is no JVM analogue at all: the JVM's `newarray` implements every primitive
 *    component. This is ART declining to implement something [BC] permits, undocumented anywhere, and a
 *    plausible anti-analysis trick. See [notImplementedMessage] for the message's grounding.
 *  - **`NegativeArraySizeException` -- NOT REACHABLE, and not carried over from `new-array` by analogy.**
 *    The element count is the instruction's own `A` field, which is 4 bits for `35c` (and the format defines
 *    only registers C..G, so ≤ 5) and 8 bits for `3rc` (≤ 255). An unsigned field cannot be negative, so
 *    there is no negative size to check for -- **SPEC**, from the instruction format. ART has a
 *    `ThrowNegativeArraySizeException` site in `DoFilledNewArray` guarded by `if (UNLIKELY(length < 0))`,
 *    immediately after `const int32_t length = is_range ? inst->VRegA_3rc() : inst->VRegA_35c()`; both
 *    accessors return unsigned fields, so that site is dead. **There is deliberately no check for it here**,
 *    and this paragraph exists so nobody adds one.
 *  - **`NullPointerException` -- NOT REACHABLE.** Unlike `aget`/`aput`/`array-length`/`fill-array-data`,
 *    this op has **no array operand**: it *creates* the array. There is nothing to be null. Forking an NPE
 *    here -- the shape a port of [APutOp] or [FillArrayDataOp] would reach for -- would invent a reachable
 *    handler, which is the mirror defect this plan has shipped twice.
 *  - **`ArrayIndexOutOfBoundsException` -- NOT REACHABLE.** No index operand, and the array is allocated at
 *    exactly the element count.
 *  - **`ArrayStoreException` -- NOT THROWN, and [ART] is explicit rather than merely silent.**
 *    `DoFilledNewArray`'s reference fill is
 *    `object_array->SetWithoutChecks</*kTransactionActive=*/ false, /*kCheckTransaction=*/ false>(i, …)`
 *    (the space before each `false` is the source's own; an earlier revision dropped it) --
 *    *without checks*, i.e. no assignability test at all. Element/component agreement is a **VERIFIER**
 *    matter ([ART] `MethodVerifierImpl::VerifyFilledNewArray` checks each argument register against
 *    `reg_types_.GetComponentType(res_type)`), so a reference element is stored whatever its type. This is why
 *    [APutOp]'s `storeCheck` has no counterpart here.
 *  - **`OutOfMemoryError` -- REAL in [ART] and deliberately NOT MODELLED here**, like every other allocation
 *    site in this engine. `DoFilledNewArray`'s `mirror::Array::Alloc` answering null is followed by
 *    `self->AssertPendingOOMException()`. Named rather than omitted so the throw-site list above is complete:
 *    the function has exactly FOUR (the dead `NegativeArraySizeException`, the linking error, the
 *    `InternalError`, and this), and this op models the middle two.
 *
 * **The `[J`/`[D` versus `[Z`/`[B`/`[C`/`[S`/`[F` split is ART's own, drawn in one `if`/`else`**, and it is why
 * one pair decodes to [org.cf.smalivm.core.UnsupportedOp] while the other five throw.
 * `CheckNewArray<kFilled>` (`runtime/verifier/method_verifier.cc`) sends `J`/`D` to
 * `FailBadFilledNewArray(descriptor)` -- a HARD failure, with a comment citing [BC] by URL -- and everything
 * else non-`int`/non-reference to `Fail(VERIFY_ERROR_FILLED_NEW_ARRAY)`, a SOFT failure commented *"Fall back
 * to interpreter to throw `InternalError`. Compiler does not handle this case."* A hard failure means no device
 * runs the method at all; a soft one means the interpreter is expected to throw. The same function's
 * `bracket_count == 0u` arm is `FailBadNewArrayNotArray`, which grounds the other decode refusal.
 * **`CheckNewArray<kFilled>` and `VerifyFilledNewArray` are two different functions** -- descriptor shape
 * versus argument registers -- and there is **no `VerifyNewArray` symbol in ART at all**; an earlier revision
 * of this KDoc named one, and a word-boundary grep over the raw source returns zero hits for it.
 *
 * `Opcode.canThrow()` happens to be **true** for both opcodes (`FILLED_NEW_ARRAY.flags == 0x000d`, verified
 * against dexlib2 2.5.2), but it is still not consulted: it was **false** for `fill-array-data` while ART
 * threw two different exceptions from it, so throw edges are decided from the op's own semantics either way.
 *
 * ## Nothing here forks, and nothing here declares a side effect
 *
 * Every row returns exactly ONE transition. Both throws are **definite**: `loadable` and the component type
 * are decode-time facts about the instruction, not runtime questions -- `loadable` because spec §3 makes
 * multidex a non-goal, so the loaded class set is the whole world (design decision 4's argument, unchanged).
 * An unprovable *element* is not an ambiguity about control flow at all; it is an [Value.Unknown] in one slot
 * of a fully tracked array.
 *
 * **[org.cf.smalivm.core.SideEffectLevel.NONE] on every row, and it is pinned rather than assumed.** This op
 * allocates a **fresh** array and writes it to the result slot, so nothing pre-existing is mutated and the
 * heap models the allocation exactly; the two throwing rows allocate nothing at all. There is no
 * unprovable-receiver row here -- the array is not an operand -- so unlike [APutOp] and [FillArrayDataOp]
 * this op has **no** row that could legitimately be `STRONG`, and it calls `Frame.recordSideEffect` nowhere.
 * A spurious `STRONG` would be silent and expensive: a fully provable constant array would report `Complete`
 * *and* `STRONG`, and every consumer strategy gated on `methodSideEffectLevel() == NONE` would refuse to fold
 * it -- which is the whole deobfuscation value of this opcode. `FilledNewArrayOpTest`'s
 * `aFreshlyAllocatedArrayDeclaresNoSideEffectOnEveryRow` is what kills that mutation.
 *
 * ## `registersAssigned` is EMPTY, decided on this op's own semantics
 *
 * This op **does** produce a result, unlike [APutOp] and [FillArrayDataOp] -- so the empty set is NOT their
 * precedent reapplied. The reason is different: the result goes to the [Frame]'s **result slot**, which is
 * not a register and which a `Set<Int>` structurally cannot name. There is no register number to report; the
 * op that assigns one is the `move-result-object` that follows, and [MoveOp] already reports it.
 * [org.cf.smalivm.core.ExecutionGraph.availableRegisters] derives "free scratch register" from
 * `registersRead ∪ registersAssigned`, and every element register is already in [registersRead], so nothing
 * is under-reported. The engine being replaced declared `registersAssignedCount = 1`, which is simply wrong
 * for that purpose -- registered as a divergence in prose (Task 14) because `registersAssigned` is not in the
 * differential projection, so prose is the only place it can be recorded.
 */
internal class FilledNewArrayOp(
    address: Int,
    override val name: String,
    /**
     * The element registers in **instruction order**, resolved at decode time from
     * `FiveRegisterInstruction`'s `registerC..registerG` or `RegisterRangeInstruction`'s `startRegister`.
     * Order is load-bearing: it is the order the elements land in the array.
     */
    private val elementRegisters: IntArray,
    /** The instruction's own type, always an array type -- `OpBuilder` refuses anything else. */
    private val arrayType: VirtualType,
    /** Decided at decode time by `ClassIndex.isLoadable`, exactly as [NewArrayOp]'s is. */
    private val loadable: Boolean,
    /**
     * Whether this is the `/range` opcode. Decided at decode time from the OPCODE rather than re-derived
     * from `name.endsWith("/range")` or from the register count -- the standing ruling
     * `OpBuilder.buildAPut`'s KDoc makes for [APutOp.StoreKind] and `Op.destinationRegisters`' `isWide`
     * makes for widths. It drives [toString] only, and it matters there: see that function.
     */
    private val isRange: Boolean,
) : Op(address) {

    /**
     * **All N of them, including every register after the first unprovable one.** The engine being replaced
     * deliberately did not break out of its read loop early *"since we want to signify that all registers
     * are read here"*, and that is right: [registersRead] feeds
     * [org.cf.smalivm.core.ExecutionGraph.availableRegisters], so a register this op reads but does not
     * report is a register a later consumer may hand out as scratch while it is still live.
     */
    override val registersRead: Set<Int> get() = elementRegisters.toSet()

    /** See the class KDoc: the result slot is not a register. */
    override val registersAssigned: Set<Int> get() = emptySet()

    override fun execute(frame: Frame): OpExecution {
        // Read EVERY element register, and read them BEFORE any throw decision, because a throwing path
        // still read its operands on a real device.
        //
        // DISCLOSURE: the PLACEMENT is inert and only the completeness is tested. Moving this line below both
        // throw rows leaves all 875 tests green (measured as mutation M20), because
        // `ExecutionGraph.registersRead` reads `op.registersRead` STATICALLY -- it never consults a frame --
        // so no query can see where `frame.read` happened. `registersRead`'s own completeness IS tested
        // (mutation M7 kills `everyElementRegisterIsReportedAsReadEvenAfterAnUnprovableOne`). Kept in this
        // position because it is the truthful shape, not because a test demands it.
        val elements = elementRegisters.map { frame.read(it) }

        // ROW 3: the component class is not in the loaded set. `NoClassDefFoundError`, an Error, so
        // `catch (Exception)` -- ubiquitous in obfuscator output -- cannot catch it. Definite, not a fork:
        // absence is a FACT here (spec §3 makes multidex a non-goal), which is the whole of design decision
        // 4 and is the same ruling `new-array` follows. Checked before the component-kind row below to match
        // ART's order (`ResolveVerifyAndClinit` precedes the InternalError block in `DoFilledNewArray`),
        // though the two rows are disjoint anyway: an unloadable component is a reference and every
        // InternalError component is a primitive.
        if (!loadable) {
            return OpExecution.single(
                OpArm.Throw(evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NO_CLASS_DEF_FOUND, binaryBaseName()))),
            )
        }

        // Non-null by construction: `OpBuilder` refuses a non-array type reference. Asserted rather than
        // defaulted, because a silent fallback would fill "the array" with a component nothing declared.
        val component = requireNotNull(arrayType.componentType) {
            "filled-new-array's type $arrayType is not an array type, which OpBuilder must already have " +
                "refused (see buildFilledNewArray)"
        }

        // ROW 4: a single-word primitive component that is not `int`. [BC] makes these look legal -- they
        // ARE single-word -- but ART does not implement them, and this row is the one an obfuscator would
        // use on purpose. `[J`/`[D` cannot arrive: SPEC forbids them and `OpBuilder` refuses them, so this
        // condition's reachable set is exactly {Z, B, C, S, F}.
        if (component.isPrimitive && component != INT) {
            return OpExecution.single(
                OpArm.Throw(
                    evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(INTERNAL_ERROR, notImplementedMessage(component))),
                ),
            )
        }

        // ROWS 1 and 2: the fill. A fresh tracked array, never a live host array -- two forked siblings must
        // not be able to mutate one object (spec §4.2).
        return OpExecution.single(
            OpArm.Continue(
                OpUpdate.allocateArray(
                    AllocationTarget.ResultSlot,
                    arrayType,
                    List(elements.size) { stored(elements[it], component) },
                ),
            ),
        )
    }

    /**
     * One element as it lands in its slot.
     *
     * **Per-slot, which is precision improvement #3.** An unprovable element makes THAT slot
     * `Unknown(component)` and leaves every other slot known, where the engine being replaced discarded the
     * whole array as one `Unknown("[I")` the moment any element was unknown. Same treatment as Task 8's two
     * improvements.
     *
     * **The declared type is the ARRAY's component, and the raw is `Coerce`d only for an `int` component.**
     *  - `I`: [org.cf.smalivm.core.Coerce.toProvableInt], because Dalvik's type tags are ambiguous -- a
     *    register tagged `I` legitimately holds a `Short`, a `Character` or a `Boolean`, which is exactly what
     *    the migrated `testShortParametersCreatesArrayWithExpectedContents` asserts. For all of those the
     *    answer is **ORACLE** parity with `castValue`, because for them `toProvableInt` and `Coerce.toInt`
     *    agree.
     *
     *    **A `Float`/`Double` raw makes the slot `Unknown(I)`, and that is now decided rather than
     *    inherited.** This line called `Coerce.toInt` until Plan 7's closeout, which truncates BY VALUE: an
     *    element register holding `Known(5.9f, "F")` became a confident `Known(5, I)`, where a device reads
     *    the register's 32 bits (`0x40BCCCCD`, i.e. 1086115021). **VERIFIER**-excluded input either way
     *    ([ART] `VerifyFilledNewArray` requires each argument to be assignable to the component type), so
     *    spec §2.2 decides it and the answer is the lattice's. What remains with `CARRY-FORWARD.md`'s
     *    cross-domain item is the ARITHMETIC half (`Coerce.toInt` itself, read by `BinaryMathOp`/
     *    `UnaryMathOp`), which is Plan 8's and is deliberately untouched.
     *  - a reference: whatever `GetVRegReference` reads, which is the raw **verbatim only when the register
     *    really holds a reference**. A reference is not an int and coercing one is a crash or a lie -- but
     *    neither is a primitive a reference, and this op is the SIXTH consumer of
     *    [org.cf.smalivm.core.readsAsNullReference] rather than an exception to it. See below.
     *
     * **The reference component reads the register AS A REFERENCE, so a primitive register contributes
     * `null`** -- the whole-branch review's C1, and the one row an earlier revision of this KDoc asserted
     * could not exist. [ART], fetched raw and counted with `awk` line numbering, `interpreter_common.cc:1463-1464`:
     * `object_array->SetWithoutChecks</*kTransactionActive=*/ false, /*kCheckTransaction=*/ false>(i,
     * shadow_frame.GetVRegReference(src_reg));`. That is the same `GetVRegReference` whose primitive-clearing
     * behaviour [org.cf.smalivm.core.readsAsNullReference] exists to express, so `const/4 v0, 0x0` /
     * `filled-new-array {v0}, [Ljava/lang/String;` -- i.e. `new String[]{null}`, verifier-VALID and the
     * ordinary spelling -- puts **`nullptr`** in the slot where this line used to put a boxed `Integer` 0.
     *
     * **This op carries the SHARPEST evidence in the codebase that ART's two interpreter tiers diverge, and
     * the split lands squarely on its own two sub-rows** (2026-08-04). nterp does not use
     * `interpreter_common.cc` for this opcode at all: `mterp/x86_64ng/array.S:134-148` calls
     * `NterpFilledNewArray` (`mterp/nterp.cc:758-764`), reaching nterp's **own** `DoFilledNewArray`
     * (`:683-756`) whose reference-component fill at `:751-752` is
     * `SetWithoutChecks(i, reinterpret_cast<mirror::Object*>(regs[src_reg]))` -- the **raw** register value
     * cast straight to an object pointer, with no reference array consulted.
     *  - For `const/4 v0, 0x0` that cast is `nullptr`, so **both tiers agree** and `new String[]{null}` --
     *    the row the whole-branch review's C1 was about -- is now doubly grounded rather than resting on one
     *    interpreter. A strengthening.
     *  - For `Known(5, "I")` nterp stores the integer **5 as a reference** into the object array. So the
     *    non-zero primitive row is **VERIFIER** tier, not [ART]: this engine's spec §2.2 choice on input the
     *    verifier rejects. `null` remains the right answer -- it is the sound direction, it matches the switch
     *    tier, and a corrupt reference is not a value this lattice can represent -- but do not cite [ART] for
     *    it. See [org.cf.smalivm.core.readsAsNullReference]'s KDoc and `docs/DALVIK-SEMANTICS.md`'s *ART has
     *    TWO interpreter tiers* subsection.
     *
     * **Why the predicate is `readsAsNullReference` and not [org.cf.smalivm.core.isDalvikNull], which is the
     * spelling `aput-object`'s rule 1 uses.** ART reads the source register with `GetVRegReference` in BOTH
     * ops (`aput-object` at `interpreter_switch_impl-inl.h:1099`), so the difference is not in ART -- it is in
     * which refusals each engine-side op HAS. [APutOp] asks the narrower zero-only question because its
     * `storeCheck` rule 4 (`REFUSED_BY_SHAPE`) already diverts every *non-zero* primitive into an
     * `ArrayStoreException`, so the wide rows never reach its `coerce`. This op has **no `storeCheck` and no
     * `ArrayStoreException` at all** (see the class KDoc: `SetWithoutChecks` performs no assignability test,
     * so element/component agreement is a VERIFIER matter), so there is no second refusal to catch
     * `Known(5, "I")` or `Known(5.9f, "F")` and the read-as-reference predicate has to answer the whole
     * question. One predicate, all four rows. **It agrees with a device on the rows a device can run** -- the
     * zero ones, under both interpreter tiers; the wide rows are verifier-rejected input where the tiers
     * disagree and the answer is this engine's choice, per the tier note above. (That sentence used to read
     * *"and it agrees with a device on every one of them"*, which claimed a measurement nobody has taken and
     * is false for nterp on `Known(5, "I")`.)
     *
     * Re-tagging with the component (rather than keeping the register's own tag) is not a departure from
     * "verbatim" -- it is the convention both shipped array-store ops already follow, `APutOp.storedValue`
     * (whose `coerce` likewise turns Dalvik's integral zero into a real `null` under an `OBJECT` suffix,
     * `APutOp.kt`'s `StoreKind.OBJECT` row) and [FillArrayDataOp]'s fill loop, and it is what
     * `CoreState.elementsOf` builds its expectations from. Keeping the register's tag would let two
     * `filled-new-array`s that agree about every raw disagree about every element's type.
     */
    private fun stored(element: Value, component: VirtualType): Value {
        if (element !is Value.Known) return Value.Unknown(component)
        if (component != INT) return Value.Known(if (element.readsAsNullReference) null else element.raw, component)
        return Coerce.toProvableInt(element.raw)?.let { Value.Known(it, component) } ?: Value.Unknown(component)
    }

    /**
     * ART's message, verbatim: *"Found type %s; filled-new-array not implemented for anything but 'int'"*.
     *
     * **The `%s` is the COMPONENT's `PrettyDescriptor()`, i.e. its Java source name -- not a Dalvik
     * descriptor and not the array's type.** `DoFilledNewArray`
     * (`runtime/interpreter/interpreter_common.cc`) calls
     * `self->ThrowNewExceptionF("Ljava/lang/InternalError;", "Found type %s; filled-new-array not
     * implemented for anything but 'int'", component_class->PrettyDescriptor().c_str())`, and
     * `AppendPrettyDescriptor` (`libdexfile/dex/descriptors_names.cc`) maps `B` -> `"byte"`, `Z` ->
     * `"boolean"`, `C` -> `"char"`, `S` -> `"short"`, `F` -> `"float"`. So `filled-new-array {…}, [B` on a
     * device says *"Found type byte; …"*. Both sources were fetched as raw base64 and decoded rather than
     * read through a summarizing tool, and the quotes are in `docs/DALVIK-SEMANTICS.md`.
     *
     * The message is asserted here even though `docs/DALVIK-SEMANTICS.md` says a message is never evidence of
     * Dalvik fidelity, and the reason is narrow: this whole exception is **[ART]-only** from top to bottom --
     * no [BC] or [JVMS] text mentions it -- so ART's text is the only thing that identifies the row at all.
     * Contrast [AGetOp]/[APutOp], whose AIOOBE message deliberately does *not* match ART's, because there the
     * exception itself has an independent, non-[ART] source.
     *
     * **Only the five REACHABLE components are named.** `J`/`D` never arrive (`OpBuilder` refuses them) and
     * `I` never takes this row, so their table rows would be dead code that looks live. The `else` is a
     * fallback for an unreachable-by-construction input and is disclosed as such rather than presented as
     * behaviour: it renders the descriptor, which is at least honest about what it could not name.
     */
    private fun notImplementedMessage(component: VirtualType): String {
        val pretty = when (component.descriptor) {
            "Z" -> "boolean"
            "B" -> "byte"
            "C" -> "char"
            "S" -> "short"
            "F" -> "float"
            else -> component.descriptor
        }
        return "Found type $pretty; filled-new-array not implemented for anything but 'int'"
    }

    /**
     * `[[Lfoo/Bar;` -> `foo.Bar`, the same shape [NewArrayOp] uses and for the same reason: the exception
     * TYPE is a deliberate divergence from the engine being replaced, so no migrated expectation constrains
     * the message, and the binary base name is kept only so the two array-allocation ops read alike.
     */
    private fun binaryBaseName(): String {
        var base = arrayType
        while (base.isArray) base = base.componentType!!
        return base.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
    }

    /**
     * `"filled-new-array {r0, r1}, [I"` and `"filled-new-array/range {r0 .. r5}, [I"` -- the debugger's
     * display surface, so the format matters to a consumer.
     *
     * **The brace spelling is chosen by the OPCODE, not by the register count, and the two rules genuinely
     * disagree.** The engine being replaced tested `registers.size > 5`, which is a proxy for "is this the
     * range opcode" that is wrong for `filled-new-array/range {v0 .. v4}, [I`: `3rc`'s `A` field is 8 bits,
     * so the range form's count is not required to exceed five, and for that input the size test prints the
     * NON-range brace spelling while the opcode prints the range one. `filledNewArrayRangeFive()V` exists to
     * pin exactly that input. The mnemonic a disassembler must print is a property of the opcode.
     *
     * The empty-register spelling is `{}`. No `35c`/`3rc` with `A == 0` is assemblable from smali, so that
     * branch is untested -- it exists so [toString] is total rather than throwing out of `first()`, and is
     * disclosed rather than presented as covered.
     */
    override fun toString(): String {
        val registers = when {
            elementRegisters.isEmpty() -> ""
            isRange -> "r${elementRegisters.first()} .. r${elementRegisters.last()}"
            else -> elementRegisters.joinToString(", ") { "r$it" }
        }
        return "$name {$registers}, $arrayType"
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
        val NO_CLASS_DEF_FOUND: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
        val INTERNAL_ERROR: VirtualType = VirtualType.of("Ljava/lang/InternalError;")
    }
}
