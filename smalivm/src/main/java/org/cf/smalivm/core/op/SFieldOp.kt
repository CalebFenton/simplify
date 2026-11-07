package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.StaticCellProvenance
import org.cf.smalivm.core.StaticFieldLookup
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.classInitTrigger
import org.cf.smalivm.core.readsAsNullReference

/**
 * The shared preamble of `sget*` and `sput*`, in the order a device performs it -- **resolution, then class
 * initialization, then the access.** Returns the transitions that must happen INSTEAD of the access, or null
 * when the op may proceed.
 *
 * **The order is not a stylistic choice.** Resolution is first because an absent class cannot be initialized:
 * asking [classInitTrigger] for one would send `ClassInitPlanner` walking `superclassOf` on it, which raises
 * `ClassLoadFailure` and aborts the PATH (`Partial`) instead of throwing the guest error a device throws --
 * the same ordering `NewInstanceOp` states and for the same reason. Class init is second because
 * `sget`/`sput` are triggers ([JLS] §12.4.1; measured for `sget` -- see [initialize]).
 *
 * One function rather than two copies, because "which failure, in which order" is exactly the kind of thing
 * two copies drift on -- and because the two ops must agree: a `sput` that resolved a field a `sget` refused
 * would write a cell nothing can read.
 */
private fun preamble(frame: Frame, field: StaticFieldLookup): OpExecution? = when (field) {
    // The measured resolution-failure family (`Fx_SGetAbsentType`), routed through the ONE shared
    // implementation so the seven opcodes in it cannot drift apart -- see [resolutionFailure].
    is StaticFieldLookup.NoSuchClass -> resolutionFailure(frame, field.type)
    // One member along: the class resolved and declares no such field. INHERITED, not measured -- see
    // [noSuchFieldFailure].
    is StaticFieldLookup.NoSuchField -> noSuchFieldFailure(frame, field.descriptor)
    is StaticFieldLookup.Resolved -> initialize(frame, field)
}

/**
 * `Transition.InitClass` when [field]'s **declaring** class still needs initializing on this path, else null.
 *
 * **The class initialized is the one that DECLARES the field, not the one the instruction named** -- [JVMS]
 * §5.5, and [StaticFieldLookup.Resolved.definingClass] is that class. `sget v0, Lchild;->parentField:I`
 * initializes `Lparent;` (and `ClassInitPlanner` walks the rest of the chain from there, so `Lchild;` is
 * initialized too if the reference reached it -- but the field's own class is what must be prepared before its
 * cell can be read).
 *
 * **Task 16's two-pass protocol, unchanged** (`ClassInitTest.InitClassStub` is the canonical model, and
 * `NewInstanceOp` the first real user): ask [classInitTrigger], return its action arm, and the scheduler
 * runs the `<clinit>` prelude -- or decision 7's framework-conservative marking -- and resumes at THIS op's
 * own address, so the second pass finds the class past `UNINITIALIZED` and does the real access. An op that
 * emitted the transition unconditionally would re-execute forever, which `ExecutionSession.planInitClass`
 * treats as a loud invariant rather than a hang.
 *
 * **That `sget` is a trigger is MEASURED, not inherited**: `[DEVICE](Tier A, -Xint, art_standalone
 * 0_git20251009-r2)` and `[DEVICE](Tier B, nterp default, API 36)`, fixture `Fx_StaticFieldDoesInitialize`
 * (`docs/DALVIK-SEMANTICS.md`'s trigger table) -- which is also the positive CONTROL for `const-class`'s
 * measured NON-initialization, so the two rows must never drift apart. `sput` is **INHERITED** ([JVMS] §5.5's
 * trigger list via `putstatic`) and the oracle agrees (`execution-engine.md:151-153`); that row has no fixture
 * of its own and the semantics doc says so rather than glossing it.
 *
 * ⚠️ **A consequence for anyone asserting against a trigger's address**: the first pass assigns nothing, so it
 * contributes `Value.Top` to `ExecutionGraph.consensusOverExploredPaths(address, register)` for this address
 * (`ExecutionSession.planInitClass`'s note). Read TERMINATING consensus -- which is what `CoreTester.test`
 * does -- or the pile's last node. Nothing here can be assumed away by the op.
 */
private fun initialize(frame: Frame, field: StaticFieldLookup.Resolved): OpExecution? =
    frame.classInitTrigger(field.definingClass)

/**
 * Reads a static field into a register -- all six `sget*` opcodes (60-66, format 21c).
 *
 * **What the value IS, and it is the whole of adjudication 5 plus resolved decision 7's cheap part.** By the
 * time the access happens the declaring class has left `UNINITIALIZED`, so `ClassInitStore` holds a cell for
 * every static it declares (`ClassInitPlan.staticSeeds`, applied at JVMS §5.4.2 preparation): the `<clinit>`'s
 * write if one ran, else the dex-encoded literal (`.field public static myInt:I = 0x42` -- which the oracle
 * discards for any class *with* a `<clinit>`, reading a constant sitting in the dex back as `Unknown`), else
 * the JVM type default, which after initialization is a **provable fact** rather than an unknown. A framework
 * class's non-literal static starts as `Value.Unknown` because its bundled `<clinit>` stays suppressed. That
 * unresolved prepared cell emits [Transition.ProjectStatic]. The scheduler alone authorizes and executes the
 * read. This op does not load a host class, inspect policy, call a service, or execute host code.
 *
 * **A REFERENCE-typed field's read goes through [readsAsNullReference], never an `isNull` or a `raw == null`
 * test** (`CARRY-FORWARD.md` Plan 9 item 13). A static cell holds bits, exactly as a Dalvik slot does, and
 * `sget-object` fetches them AS A REFERENCE -- ART's field ops use the same `GetVRegReference`-shaped fetch as
 * `throw`/`array-length`/`aget`, so a primitive-tagged value in a reference-typed field reads as `null`
 * whatever its bits. **NAME THE TIER, because ART has two interpreters and they disagree on exactly one row of
 * this predicate:**
 *
 * | cell | this op reads | tier |
 * |---|---|---|
 * | `Known(null, L…;)` -- the ordinary null | `null` | **[ART], BOTH TIERS** -- the switch interpreter's reference half is `nullptr` and `nterp` branches on the raw being zero and takes the null path; verifier-VALID input (`reg_type.h:167-169`) |
 * | `Known(0, "I")` -- Dalvik's integral zero under a reference tag | `null` | **[ART], BOTH TIERS**, same rows; **SPEC** corroborates ([BC]: *"(Object) null == (int) 0"*) |
 * | `Known(5, "I")` -- a NON-ZERO primitive under a reference tag | `null` | **VERIFIER -> the engine's choice under spec §2.2. NOT [ART]** -- the two tiers CONTRADICT each other here (switch reads `nullptr`; `nterp` dereferences the raw `5` as an object pointer and the VM dies, measured), and `method_verifier` hard-fails the class, so no APK links it. See [org.cf.smalivm.core.readsAsNullReference] and `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers* |
 * | `Unknown(T)`, `Value.Top` | verbatim | the soundness line: `readsAsNullReference` answers `true` only for a PROVED state, so an unproven cell is never narrowed to a definite null |
 *
 * **Side effects: NONE in the op, and `recordSideEffect` is called nowhere in this class.** The scheduler marks
 * a skipped framework initializer or host projection request `STRONG`, independently of whether the value is
 * known. Thus
 * `sget Ljava/lang/Integer;->MAX_VALUE:I` is `STRONG` at its address while `sget` of an app class's static is
 * `NONE`, and neither number comes from here.
 */
internal class SGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val field: StaticFieldLookup,
) : Op(address) {

    /**
     * Width comes from the resolved FIELD's declared type, not from the `-wide` mnemonic -- deliberately the
     * opposite choice from [AGetOp], and the difference is in the quality of the decode-time information.
     * `aget`'s element width cannot be known at decode time (the array register's tag is loose by design and
     * the array may not be tracked), so it must trust the opcode; a field REFERENCE in dex carries its type,
     * so here the mnemonic is a redundant re-encoding of a fact this op already holds exactly.
     *
     * It also keeps the static declaration and [Frame.assign] in agreement, which the opcode cannot: `assign`
     * reserves the high half off the VALUE's type, so a `sget-wide` naming a narrow field (assemblable, and
     * `sget_test.smali`'s `getStaticFloat()V` is one) would declare a pair the frame never reserves -- and in a
     * `.locals 1` frame it would name a register the method does not have.
     *
     * An UNRESOLVED field declares the narrow set: there is no field type to ask, and the op throws before
     * assigning anything, so the set describes an assignment that never happens either way.
     */
    // `this.field`, NOT `field`: inside a property accessor `field` is the backing-field keyword, so the
    // unqualified name binds to this getter's own (non-existent) backing field rather than to the
    // constructor property, and the compiler answers "Property must be initialized."
    override val registersAssigned: Set<Int>
        get() = destinationRegisters(destRegister, isWide = (this.field as? StaticFieldLookup.Resolved)?.type?.isWide == true)

    override fun execute(frame: Frame): OpExecution {
        preamble(frame, field)?.let { return it }
        val resolved = field as StaticFieldLookup.Resolved
        val cell = frame.staticCell(resolved.descriptor)
        if (
            cell?.provenance == StaticCellProvenance.SEEDED_DEFAULT &&
            cell.value is Value.Unknown
        ) {
            return OpExecution.single(
                OpArm.ProjectStatic(
                    destRegister,
                    resolved.definingClass,
                    resolved.descriptor,
                    resolved.type,
                ),
            )
        }
        val stored = frame.getStaticField(resolved.definingClass, resolved.descriptor)
        val loaded = if (resolved.type.isReference && stored.readsAsNullReference) {
            // Tagged with the FIELD's type rather than the cell's: this IS a reference read, and the null it
            // produces is the field's null. See the tier table on the class.
            Value.Known(null, resolved.type)
        } else {
            stored
        }
        val update = OpUpdate.assign(destRegister, loaded).let {
            if (resolved.isVolatile) it.then(OpUpdate.effect(SideEffectLevel.WEAK)) else it
        }
        return OpExecution.single(OpArm.Continue(update))
    }

    override fun toString(): String = "$name r$destRegister, $field"
}

/**
 * Writes a register into a static field -- all six `sput*` opcodes (67-6d, format 21c).
 *
 * **The mirror of [SGetOp] with the same preamble** (resolution, then class init, then the access), so a
 * `sput` cannot write a cell a `sget` would refuse to resolve, and `sput` triggers its declaring class's
 * initialization exactly as `sget` does -- which matters for ordering, not just for marks: the `<clinit>`
 * prelude runs BEFORE this write, so a `sput` in a method that touches an uninitialized class does not have its
 * value overwritten by preparation.
 *
 * **Side effect: [SideEffectLevel.WEAK], unconditionally, and it is a claim about the CONSUMER rather than
 * about precision.** Three grounds, none of them "the oracle did it" alone:
 *  1. spec §4.5 -- *"host array/field mutation is at least `WEAK`"*;
 *  2. the consumer consequence, which is the real one: a static write **outlives the frame**. A method whose
 *     only observable act is a `sput` must not read as deletable, and `ExecutionGraph.methodSideEffectLevel`
 *     is what Plan 14's dead-code strategy asks. `NONE` here would authorize deleting the write;
 *  3. oracle parity (`opcode/SPutOp.kt:36` sets exactly this level), so a migrated effect expectation cannot
 *     move.
 *
 * **`WEAK` and not `STRONG`, and the distinction is load-bearing.** `STRONG` means *unmodelled* -- spec §2.2's
 * err-upward answer for a write whose target the engine could not identify (`Frame.recordSideEffect`'s
 * worked examples are all of that shape, and `ExecutionGraph.sideEffectLevel` derives `STRONG` from
 * `UNSUPPORTED_OP`). A `sput` into a resolved field is modelled **exactly**: the cell is named, per path, and
 * readable. Declaring `STRONG` would stop every fold in the method for a write the engine tracks perfectly.
 *
 * **The stored value is the register as the field's type reads it.**
 *  - `sput-object` reads its value register AS A REFERENCE, so [readsAsNullReference] makes a primitive-tagged
 *    register store `null` -- `aput-object`'s rule 1 at a different call site, gated on the mnemonic for the
 *    same reason (only the `-object` form's value operand sits in a reference context). Tiers are
 *    [SGetOp]'s table: zero corroborated by both interpreter tiers, a non-zero primitive **VERIFIER**. Without
 *    this row `sput-object v0` of `const/4 v0, 0x5` reports a non-null `Integer` to
 *    `terminatingStaticFieldConsensus`, which is a wrong answer to a public query rather than an imprecision.
 *  - `sput-boolean`/`-byte`/`-char`/`-short` keep only the field's width ([Coerce.narrowStore]), so
 *    `sput-byte` of `0x100` stores `0`.
 */
internal class SPutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val field: StaticFieldLookup,
    /**
     * True for `sput-object` alone -- the one form whose value operand sits in a reference context. Decoded
     * from the opcode in `OpBuilder`: a semantic fact the mnemonic carries is resolved once at decode time,
     * never re-derived from `name.endsWith(...)` at execution time.
     */
    private val storesAReference: Boolean,
) : Op(address) {

    /**
     * A wide source names only its LOW half, which is `ExecutionGraphView.availableRegisters`' stated
     * convention (*"a wide source is reported by its low half only"*) and what `frame.read` records.
     */
    override val registersRead: Set<Int> get() = setOf(valueRegister)

    /**
     * **Nothing**, and that is a deliberate divergence from the oracle's `registersAssignedCount = 1`
     * (`opcode/SPutOp.kt:22`) -- the same one [APutOp.registersAssigned] documents. The store goes into class
     * state; `execute` calls `frame.assign` nowhere, so claiming a destination would tell
     * `ExecutionGraph.availableRegisters` that a register was written when it was not. The value register is
     * named by [registersRead], so it is never offered as scratch either way.
     */
    override val registersAssigned: Set<Int> get() = emptySet()

    override fun execute(frame: Frame): OpExecution {
        preamble(frame, field)?.let { return it }
        val resolved = field as StaticFieldLookup.Resolved
        val value = frame.read(valueRegister)
        val stored = if (storesAReference && value.readsAsNullReference) {
            Value.Known(null, resolved.type)
        } else {
            Coerce.narrowStore(value, resolved.type)
        }
        val update = OpUpdate.writeStatic(resolved.descriptor, stored)
            .then(OpUpdate.effect(SideEffectLevel.WEAK))
        return OpExecution.single(OpArm.Continue(update))
    }

    override fun toString(): String = "$name r$valueRegister, $field"
}
