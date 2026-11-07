package org.cf.smalivm.core.op

import org.cf.smalivm.core.ClassLoadFailure
import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Superclasses
import org.cf.smalivm.core.TypeHierarchy
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.isDalvikNull
import org.cf.smalivm.core.isDirectHostImmutable
import org.cf.smalivm.core.isNull
import org.cf.smalivm.core.readsAsNullReference
import org.cf.smalivm.core.join
import org.cf.smalivm.core.virtualTypeOfHostClass

/**
 * Stores one element into a tracked array -- all seven `aput*` opcodes (4b-51, format 23x = TWO code units).
 *
 * **This is not [AGetOp] mirrored, and three differences matter.**
 *
 *  1. `vAA` is the **value** register, not a destination. Nothing here calls
 *     [Op.destinationRegisters]: there is no result and so no wide pair to declare, and `aput-wide`'s
 *     "wideness" is a property of the value being copied rather than of a register this op writes.
 *  2. All three operands are **read** and **no register is assigned** -- the store goes into the array
 *     OBJECT, so [execute] calls `frame.assign` nowhere and [registersAssigned] is `emptySet()`. That is a
 *     deliberate, documented **divergence** from the oracle's `registersAssignedCount = 1`, whose
 *     `node.state.assignRegister(arrayRegister, array)` is load-bearing only because the oracle keeps the
 *     live host array *in the register*; see [registersAssigned] for the full reason and for the false claim
 *     an earlier revision defended it with. **Do not mirror the oracle's write-back into Plan 9's `iput`.**
 *  3. The suffix drives **value coercion**, not just width. `aput-boolean` of the integer literal `0x1`
 *     stores `true`; `aput-char` of `36` stores `'$'`; `aput-object` of `0` stores `null`. That is a
 *     semantic difference the mnemonic is the only carrier of, which is why [StoreKind] is decoded from the
 *     opcode at build time and switched on exhaustively (a new kind is then a compile error rather than a
 *     silent fall-through to [StoreKind.PLAIN]).
 *
 * **Both exceptions are INHERITED, not specified by Dalvik.** [BC]'s entire text for all fourteen
 * `aget*`/`aput*` opcodes is one shared sentence -- *"Perform the identified array operation at the
 * identified index of the given array, loading or storing into the value register."* -- and the whole
 * document names exactly two exceptions, neither of them an array exception. `NullPointerException` and
 * `ArrayIndexOutOfBoundsException` come from [JVMS] §6.5 `iastore`/`aastore`, legitimised by [VER]'s
 * *"generally the same set of checks as are described in The Java Virtual Machine Specification, Second
 * Edition, including the updates planned for the Third Edition"*. **Quote it through the edition, because
 * the bridge is same-DOCUMENT and not same-EDITION**: [VER] names the Second/Third Editions while every
 * [JVMS] citation here is SE21, and the inheritance holds for exactly these rules because §6.5's
 * `iastore`/`aastore`/`iaload` and §4.10.3 array subtyping are unchanged from the Second Edition through
 * SE21. Their ORDER -- NPE strictly before AIOOBE -- is the same document's `Otherwise` chaining, which is
 * why the array rows below are all resolved before the index is looked at. Quotes and tiers:
 * `docs/DALVIK-SEMANTICS.md#aget--aput-ops-4451-23x`.
 *
 * **Two places this is deliberately sharper than the engine it replaces, and the oracle's own test calls the
 * first one non-ideal.** The oracle marks the ENTIRE array `Unknown` when the value or the index is unknown
 * (`opcode/APutOp.kt`'s `if (store.isUnknown || index.isUnknown) array = Value.unknown(...)`). With elements
 * as lattice points that is unnecessary: an unknown VALUE makes exactly the slot it wrote unknown, and an
 * unknown INDEX joins every slot with the stored value -- which is the precise statement of "the write landed
 * on one slot but we cannot say which". Both answers are strictly lower in the lattice than the oracle's and
 * neither invents a constant, so they are safe as well as sharper. Never reconcile them by making the core
 * coarser.
 *
 * **The two rows that cannot identify what they wrote declare `SideEffectLevel.STRONG`, and that is not
 * decoration.** When the array register is `Unknown`/`Top` or a `Known` non-array, the store lands on an
 * object the engine cannot name, so an element of some OTHER tracked array may have changed and no heap state
 * records it. `frame.recordSideEffect(STRONG)` is how the op says so; `ExecutionSession` transfers it to the
 * node and `ExecutionGraph.sideEffectLevel` maxes it over the pile (spec §4.5: effects live on the
 * node/graph, never on the op, which is why this is a `Frame` call and not an `Op` property).
 *
 * **Why it is load-bearing rather than tidy.** `Frame.entryFrame` seeds every parameter `Value.Unknown`, and
 * no opcode in this build turns a parameter into a tracked array -- so **every store into a caller-supplied
 * array takes row 1**, and `void f(int[] a) { a[0] = 5; }` is the commonest `aput` in real code rather than a
 * corner case. Without this declaration such a method reported `Complete` **and**
 * `methodSideEffectLevel() == NONE`, which is exactly the pair `AnalysisContracts`' own KDoc says would let a
 * strategy *"fold a constant, or delete a method"* -- here, delete a method whose entire observable behaviour
 * is a write its caller can see. While `aput` was an `UnsupportedOp` the engine answered `STRONG` and refused
 * trust altogether, so landing the opcode without this would have replaced a sound refusal with an unsound
 * answer.
 *
 * A store into a tracked array is `WEAK`: the heap models it exactly, but a caller can still observe the
 * mutation through an alias. A definite throw stays `NONE` -- nothing was stored.
 *
 * **`ArrayStoreException` is answered by [storeCheck] and lands in TWO different places**, because its two
 * refusals have different evidence tiers and therefore different positions in the exception order. See
 * [StoreCheck] for the rule set, the tiers and the ordering argument; the short version is that the
 * VERIFIER-tier shape refusal precedes the null and bounds rows while the INHERITED `aastore` assignability
 * refusal follows them. This is the one op in the build that needs a runtime subtype question answered, which
 * is why it is the only one holding a [TypeHierarchy].
 */
internal class APutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val arrayRegister: Int,
    private val indexRegister: Int,
    private val storeKind: StoreKind,
    /**
     * The subtype oracle, injected because `core/op/` may not reach the class model (the two-tier dependency
     * rule) and **this question genuinely cannot be answered at decode time**: `aastore` tests the actual
     * type of the value against the actual component type of the array, and both are runtime facts. Plan 2's
     * existing [TypeHierarchy] is reused rather than a new `Subtyping` interface being added: its shape is
     * exactly right, `ClassIndex` already implements it, and reusing it means `OpBuilder` passes the
     * `classIndex` it already holds while this file names only a core interface. Its KDoc's warning about not
     * inventing an answer for an unloadable class is load-bearing here too -- see [StoreCheck.UNANSWERABLE].
     */
    private val hierarchy: TypeHierarchy,
    /**
     * The class hierarchy the element JOINS consult, injected for the same reason [hierarchy] is: an op
     * under `core/op/` may not reach the class model. Two rows here reconcile an element's old value with
     * the stored one through [join] (the unprovable-index row and the feasible-`ArrayStoreException` row),
     * and Plan 8 Task 7 made that join hierarchy-aware -- so two `Known` references of different classes
     * now join to their nearest common superclass rather than always to `Ljava/lang/Object;`. A second
     * narrow interface rather than one widened [TypeHierarchy], whose KDoc forbids widening.
     */
    private val superclasses: Superclasses,
) : Op(address) {

    /**
     * Which coercion the mnemonic asks for, resolved at DECODE time. An enum rather than a
     * `name.endsWith("-boolean")` chain (the oracle's `castValue` does that) for two reasons: the suffix test
     * is order-sensitive in a way nothing enforces, and [coerce]'s `when` over this type is exhaustive, so
     * adding an eighth `aput*` opcode without deciding its coercion does not compile.
     */
    internal enum class StoreKind { PLAIN, WIDE, OBJECT, BOOLEAN, BYTE, CHAR, SHORT }

    /**
     * Whether this store throws `ArrayStoreException`, and -- for a refusal -- **WHICH rule said so**, because
     * the two refusals sit at different points in the exception order and carry different evidence tiers.
     * Four-valued rather than a `Boolean` on purpose: a boolean is exactly the shape that lets a later edit
     * read "cannot tell" as "no throw" and drop a feasible path, and a three-valued one is the shape that
     * lets it pick one exception order for both refusals.
     *
     * **The rule set [storeCheck] applies, in order, each with its tier.** `component` is derived
     * **object-over-tag** -- from the tracked array object when there is one, from the register tag otherwise
     * -- because [JVMS] §6.5 `aastore` tests the **actual** component type of the array and not a declared
     * one. That is a soundness requirement rather than tidiness: a `String[]` in a register tagged
     * `[Ljava/lang/Object;` is legal covariance ([JLS] §4.10.3), and a declared-types-only check reads the
     * component as `Ljava/lang/Object;`, permits an `Integer` store and DROPS a throw a real device makes.
     *
     *  1. **The value is null** -- `Value.isNull` or Dalvik's integral zero -- and the component is a
     *     reference or could not be determined -> [PERMITTED]. **SPEC** for the idiom ([BC]: *"In terms of
     *     bitwise representation, `(Object) null == (int) 0`"*) + **INHERITED** for the consequence ([JVMS]
     *     `aastore`: *"If value is null, then value is stored as the component of the array at index"* --
     *     storing null never throws ASE; [ART]'s `CheckAssignable` is guarded by `if (object != nullptr)`).
     *     First, because rule 4 would otherwise refuse it.
     *  2. **The component cannot be determined** and the value's shape is primitive -> [PERMITTED].
     *     **VERIFIER**, and an argument rather than a guess: a primitive value can only be stored by a
     *     non-`OBJECT` suffix, and every non-`OBJECT` suffix requires a primitive-component array, so rule 3
     *     would apply if the component were visible.
     *  3. **Value and component are both primitive** -> [PERMITTED], always, for **every** suffix --
     *     `aput-wide` of a `J` into a `[D` included. **INHERITED** + [ART]: [JVMS] §6.5 lists exactly NPE and
     *     AIOOBE for `iastore`/`bastore`, and `aastore` is the only array-store instruction that lists ASE at
     *     all; [ART]'s `APUT_OBJECT` is the only handler carrying an assignability check. So *no store whose
     *     value and component are both primitive throws ASE, whatever the mnemonic.* (What the stored VALUE
     *     becomes -- truncation of `aput 0x2` into a `[B` -- is a separate question this rule does not answer;
     *     [coerce] does.)
     *  4. **Exactly one of the two is primitive** -> [REFUSED_BY_SHAPE].
     *  5. **Both are references** -> the real `aastore` check, `isSubtypeOf(valueType, component)`:
     *     [PERMITTED] or [REFUSED_BY_ASSIGNABILITY], with [ClassLoadFailure] and a value carrying no type at
     *     all both answering [UNANSWERABLE].
     *
     * **The residual this rule used to carry is CLOSED (CARRY-FORWARD item 16, Plan 9 Task 17).** Rule 5 used
     * the value register's declared tag, and a tag is only ever a **supertype** of the runtime class
     * (**VERIFIER**: the verifier proves `runtime <: declared`, never equality) -- so a
     * `Known(instanceRef, "Ljava/lang/Object;")` stored into a `[Ljava/lang/String;` was refused where
     * `aastore` permits it: an **invented infeasible throw**, the "a handler is reachable when it is not"
     * half of spec §2.2. It is now derived **object over tag** on the value side too, exactly as this rule
     * already did on the array side, which became possible the moment `new-instance` made guest objects
     * tracked. See the `valueType` line in [storeCheck].
     *
     * **What is left, and it is not the same thing.** A value with no tracked object behind it -- a host
     * `String`, a `Class`, a seeded raw -- still contributes only its tag, so the supertype gap survives for
     * exactly those, and no fixture reaches it (every migrated value tag is exact). Closing that half needs
     * host values to be tracked, which is Plan 11's backends, not a tag decision here. Note the ARRAY side
     * never had this residual at all: the tag's component is a supertype of the real one, and a value not
     * assignable to a supertype is not assignable to any subtype of it either, so a refusal taken against
     * an array tag is sound.
     */
    private enum class StoreCheck {
        /** No ASE. Rules 1-3, and rule 5's assignable case. */
        PERMITTED,

        /**
         * Rule 4: exactly one of value and component is primitive. **VERIFIER -> the engine's choice**, and it
         * goes **FIRST**, ahead of the null and bounds rows.
         *
         * A reference register as the source of an int-like `aput` -- and a primitive-component array as the
         * target of an `aput-object` -- is `VERIFY_ERROR_BAD_CLASS_HARD` ([CON] B1; [ART] `VerifyAPut`), so
         * **no verified dex reaches this and there is no Dalvik behaviour to be faithful to**. Never write
         * this down as "Dalvik throws here". Given a free choice, refusing is the better one: letting the
         * store succeed would write (say) a `String` into an element the engine has typed `I`, a type lie
         * every later `aget` propagates and no consumer can detect, and spec §2.2 prefers the
         * over-approximating error where one must be made.
         *
         * Going first is likewise a choice and not an inherited order -- there is no inherited order for input
         * the verifier rejects -- and it is the one the engine being replaced made
         * (`opcode/APutOp.kt`'s check preceded its null and bounds tests), which is what keeps four corpus
         * expectations byte-identical: two of them seed an out-of-range index **and** an incompatible
         * reference type and expect ASE rather than AIOOBE, and a third expects a *definite* ASE on an
         * unprovable array rather than a three-way fork.
         */
        REFUSED_BY_SHAPE,

        /**
         * Rule 5: both references, provably not assignment-compatible. **INHERITED** ([JVMS] §6.5 `aastore`),
         * and it goes **LAST** -- after the null row and after the bounds row -- because `aastore`'s
         * *"Otherwise"* chaining makes **NPE -> AIOOBE -> ASE** normative. That is the same normative device
         * Plan 7's design decision 4 relies on to establish resolution-before-count for `new-array`; reading
         * it as normative here and absent there would not be a defensible reading of one document.
         */
        REFUSED_BY_ASSIGNABILITY,

        /**
         * The subtype question has no answer, so **both outcomes are feasible and both are explored** --
         * `[Continue, Throw(ArrayStoreException)]` -- and the run stays `Complete`.
         *
         * Two ways in. [ClassLoadFailure] from [hierarchy], i.e. a well-formed descriptor with no class
         * behind it, which is the normal case in an obfuscated APK; and a component that could not be
         * determined with a reference-shaped value.
         *
         * **Treated as ambiguity rather than as a host failure, the same adjudication Plan 3 made for an
         * unloadable CATCH type** (`ThrowRoutingTest.anUnloadableCatchTypeIsAmbiguityNotAHostFailure`) and for
         * the same reason: letting it reach the host-failure boundary would make the whole run `Partial` and
         * switch off every consumer's trust in the method because one descriptor was missing.
         *
         * **Deliberately different from Task 6's loadability ruling, and the difference is the point.** There
         * the question was *"does this class exist"*, which is a FACT under spec §3's no-multidex scope -- the
         * loaded set is the whole world -- so `new-array` on an absent component type throws definitely and
         * does not fork. Here the question is *"is this class a subtype of that one"*, which an absent
         * ancestor genuinely leaves unanswerable.
         */
        UNANSWERABLE;

        /**
         * Whether a path exists in which the store completes. False only where the refusal is definite: on the
         * unprovable-array and unprovable-index rows this is what stops an infeasible fall-through being
         * explored, which would keep a dead block alive (the milder half of the invents-a-reachable-path
         * defect -- review finding F4's shape).
         */
        val storeCanProceed: Boolean get() = when (this) {
            PERMITTED, UNANSWERABLE -> true
            REFUSED_BY_SHAPE, REFUSED_BY_ASSIGNABILITY -> false
        }

        /** Whether an ASE is feasible at all. Exhaustive `when`s, so a fifth case cannot be forgotten here. */
        val arrayStoreIsFeasible: Boolean get() = when (this) {
            PERMITTED -> false
            REFUSED_BY_SHAPE, REFUSED_BY_ASSIGNABILITY, UNANSWERABLE -> true
        }
    }

    override val registersRead: Set<Int> get() = setOf(valueRegister, arrayRegister, indexRegister)

    /**
     * **Empty. An `aput` stores into the array OBJECT and assigns no register** -- so [execute] calls
     * `frame.assign` nowhere, and this reports that faithfully. It is also a reader's default expectation for
     * the opcode, which is worth something on its own.
     *
     * **This diverges from the oracle's `registersAssignedCount = 1`, deliberately.** The oracle's
     * `node.state.assignRegister(arrayRegister, array)` (`opcode/APutOp.kt`) is load-bearing *there* because
     * the oracle keeps the live host array **in the register**, so a store has to write the register back.
     * This core keeps a [org.cf.smalivm.core.HeapRef] in the register and the elements in the heap, so
     * writing the register back is a no-op -- mirroring the oracle here would copy the syntax and drop the
     * reason. No migrated expectation reads liveness, and nothing in the differential projects it
     * (`SmaliVmUnderTest`'s `Fixture` KDoc), so this is not an observable divergence.
     *
     * **An earlier revision DID reassign the array register on four rows, and the reasoning is kept because
     * the reasoning was the defect.** It claimed rows 1 and 3 changed the value `Known` -> `Unknown`. Row 1
     * cannot: it is entered only when `array !is Value.Known`, so writing `Unknown(array.type)` reconstructs
     * the identical lattice point. Only row 3 changed anything, and that change was a precision loss dressed
     * in soundness language -- the unmodellable write goes to *memory*, while the array *register* is provably
     * unchanged, so degrading it discarded a provable constant and accounted for nothing. What accounts for it
     * is the `STRONG` effect level (see the class KDoc). The write-backs on the tracked-array rows were
     * measurably inert: deleting one killed 0 of 181 tests.
     *
     * No [org.cf.smalivm.core.ExecutionGraph] `availableRegisters` consequence either way -- `usedRegisters`
     * unions [registersRead], which already names the array register, so it is never offered as scratch.
     */
    override val registersAssigned: Set<Int> get() = emptySet()

    override fun execute(frame: Frame): OpExecution {
        val array = frame.read(arrayRegister)
        val value = frame.read(valueRegister)
        // Read here rather than lazily, so "all three operands are read" is structural rather than a claim
        // that holds only on the rows that get that far. `read` is pure with respect to values.
        val indexRaw = (frame.read(indexRegister) as? Value.Known)?.raw

        // The tracked array, hoisted above every row because the ArrayStore check needs it too: the component
        // is derived OBJECT OVER TAG and the object is only reachable through this lookup. Pure -- a heap read
        // -- so hoisting it changes nothing about the rows below, which use the same local.
        val trackedRef = (array as? Value.Known)?.raw as? HeapRef
        val tracked = trackedRef?.let { frame.arrayAt(it) }

        // The VALUE's own runtime type when it is a tracked object -- object over tag on the value side,
        // which is what closed CARRY-FORWARD item 16 (Plan 9 Task 17, the task that made guest objects
        // tracked). Looked up HERE rather than inside `storeCheck` because only `execute` holds the frame,
        // and the same heap read as `tracked` above: pure. Null for every value that is not a tracked
        // object, which leaves the tag as the only evidence exactly as before.
        val valueRuntimeType = (value as? Value.Known)?.let { known ->
            when (val raw = known.raw) {
                is HeapRef -> frame.heapObject(raw)?.type
                null -> null
                else -> if (known.type.isReference && raw.isDirectHostImmutable()) {
                    virtualTypeOfHostClass(raw.javaClass)
                } else {
                    null
                }
            }
        }

        // ARRAYSTORE, computed up front because it is pure and cheap -- but WHERE its throw sits depends on
        // which rule answered, which is the whole reason [StoreCheck] has four cases rather than being a
        // Boolean. Read its KDoc for the rule set, the tiers and both ordering arguments.
        val check = storeCheck(value, tracked?.type?.componentType ?: array.type?.componentType, valueRuntimeType)

        // The SHAPE refusal goes FIRST, ahead of the null and bounds rows -- VERIFIER-tier, so there is no
        // inherited order to obey and this is the engine's own choice (StoreCheck.REFUSED_BY_SHAPE says why,
        // and says which corpus expectations it keeps byte-identical). Definite: no fork, no store.
        if (check == StoreCheck.REFUSED_BY_SHAPE) return OpExecution.single(throwing(arrayStore(value)))

        // ROW 1: the array is not provable at all (Unknown or Top). Resolved BEFORE the index and before the
        // value, deliberately: with nothing known about the array, nothing is known about where the store
        // landed no matter what the other two operands say, and BOTH exceptions are feasible -- the array may
        // be null, and if it is not, any index may be out of range. Dropping either would leave an unexplored
        // feasible path on a `Complete` run (spec §2.2), which is the worst outcome this engine produces.
        // The store landed on an object this engine cannot name, so some other tracked object's contents may
        // have changed and no heap state records it -- that is what STRONG says, and it is the row that makes
        // the declaration necessary rather than decorative: `void f(int[] a) { a[0] = 5; }` arrives here,
        // because `Frame.entryFrame` seeds parameters `Unknown`. The array REGISTER is deliberately left
        // alone: writing `Unknown(array.type)` back would reconstruct the identical lattice point.
        //
        // The ASE composes with this row rather than replacing it, which is the insertion point easiest to
        // miss: the component here comes from the register TAG (there is no object), so an `aput-object`
        // through an unprovable array can still be refused or unanswerable, and that verdict is INDEPENDENT
        // of whether the array turns out to be null or the index out of range. Hence the store's own outcome
        // is appended AFTER both, which is also the inherited NPE -> AIOOBE -> ASE order.
        //
        // STRONG IS DECLARED UNCONDITIONALLY, INCLUDING WHERE EVERY TRANSITION THROWS -- an over-approximation
        // stated rather than tidied away (review finding m5). When the check definitely refuses, this row
        // returns no `Continue`, so by the engine's own model nothing is written and `NONE` would be the
        // precise answer -- which is what row 5's definite bounds exception declares, and the asymmetry is
        // real. It is kept because the two definiteness claims do NOT rest on the same evidence. Row 5's is
        // computed from a tracked array's actual length, so "nothing was written" is a fact. This row's rests
        // on a TAG-derived assignability judgement, whose remaining supertype gap (see [StoreCheck]: closed
        // for a tracked value in Task 17, still open for a host one) means a store this engine refuses may
        // in truth have succeeded -- and if it did, the write landed somewhere unnameable and STRONG is the
        // correct answer. Erring upward here hedges exactly that, and spec §2.2 makes the direction the safe
        // one. **The array side of this row is tag-derived NECESSARILY, not residually**: this row is entered
        // because there is no array object to ask, so no later plan closes it the way Task 17 closed the
        // value side.
        if (array !is Value.Known) {
            val effect = OpUpdate.effect(SideEffectLevel.STRONG)
            val arms = mutableListOf<OpArm>()
            if (check.storeCanProceed) arms += OpArm.Continue(effect)
            arms += throwing(nullArray(), effect)
            arms += throwing(outOfBounds(index = null), effect)
            if (check.arrayStoreIsFeasible) arms += throwing(arrayStore(value), effect)
            return OpExecution.of(arms)
        }

        // ROW 2: the ARRAY register READS AS NULL -> a DEFINITE NPE, no fork, no store. THREE spellings of
        // one thing, and the third was added in Plan 7 Task 12's fix round: a null raw; an integral zero
        // ([BC], SPEC: "In terms of bitwise representation, (Object) null == (int) 0", so `const/4 v0, 0x0`
        // then `aput` is a definite NPE even though `Value.isNull` cannot see it); and ANY primitive-typed
        // `Known`, whatever its value. The third is [ART]'s SWITCH interpreter: `HandleAPut`
        // (interpreter_switch_impl-inl.h:524-541) fetches the array with `GetVRegReference(B())` and throws an
        // NPE when it is null, both in its first five lines (:526-530), before any index work; and
        // `GetVRegReference` reads ShadowFrame's PARALLEL reference array, which every primitive write clears
        // (shadow_frame.h:190-228; ART's prose at :401-408). Testing only `isDalvikNull` DROPPED the non-zero
        // case. See `readsAsNullReference`.
        //
        // TIER, added 2026-08-04: nterp -- the DEFAULT interpreter -- reads this operand RAW
        // (mterp/x86_64ng/array.S:58-80, and `op_aput_object` at :97-109 reads its VALUE raw too at :107).
        // Zero agrees across both tiers (`testl`/`je common_errNullObject`); a NON-ZERO primitive diverges, so
        // that row is VERIFIER tier -- this engine's spec §2.2 choice on input `VerifyAGet`/`VerifyAPut`
        // hard-fail (method_verifier.cc:5054-5055) -- and NOT an [ART] fact. Behaviour unchanged; only the
        // citation. See `readsAsNullReference`'s KDoc and `docs/DALVIK-SEMANTICS.md`'s two-tiers subsection.
        //
        // NOTE the asymmetry with rule 1 below, which is deliberate and is NOT the same predicate: this is the
        // ARRAY operand, i.e. a DEREFERENCE, so primitiveness settles it; rule 1 is the VALUE operand, i.e. a
        // coercion question about the bits, so it stays on `isDalvikNull` gated by the `OBJECT` suffix.
        // Resolved before the index because the inherited order is NPE strictly before AIOOBE.
        // ...and the ASE does NOT compose with this row: NPE strictly precedes ASE ([JVMS] §6.5 `aastore`'s
        // "Otherwise" chaining), and a null array stores nothing, so there is no store left to refuse.
        if (array.readsAsNullReference) return OpExecution.single(throwing(nullArray()))

        // PROVABLY NON-NULL from here down -- and now for a sound reason rather than the wrong one this
        // comment used to give ("a Known non-zero raw"). What makes it provable is that every primitive-typed
        // `Known` was diverted above, so a `Known` reaching here is a REFERENCE with a non-null raw. The NPE
        // is INFEASIBLE in every row below and must not be forked by any of them.
        if (tracked == null) {
            // ROW 3: the value is Known and non-zero but there is no array behind it -- a raw that is not a
            // HeapRef at all, or a HeapRef to something else (`frame.arrayAt` answers null for a
            // GuestThrowable, which is NOT the same as a null array; collapsing the two is the trap
            // ArrayLengthOp documents). NO NPE (provably non-null) and NO AIOOBE (a bounds exception needs a
            // length this input does not have): both are infeasible, so forking them would invent two
            // reachable handlers. Keep this consistent with `ArrayLengthOp` and `AGetOp` -- a single
            // `tracked == null` fork in any of the three invents a handler for a value just proved non-null.
            //
            // VERIFIER -> engine's choice: no verified dex reaches this row ([CON] B1), and ART DownCasts the
            // array unchecked and then WRITES at a mis-scaled stride -- it corrupts the wrong bytes, so there
            // is no behavior to copy. What IS recorded is that the write happened somewhere unmodellable, and
            // that is an EFFECT rather than a value: STRONG below.
            //
            // The array REGISTER is deliberately left alone. An earlier revision degraded it to
            // `Unknown(tag)`, arguing that keeping a confident `Known` would claim knowledge the engine does
            // not have -- which is a category error, because the unmodellable write goes to *memory* while
            // `aput` does not write the array register at all, so the register's value is provably unchanged.
            // Degrading it discarded a provable constant and accounted for nothing.
            //
            // The store check still applies, and uniformly with every other row rather than being special-cased
            // away: the component here comes from the tag if the tag happens to be an array descriptor, so an
            // unassignable or unanswerable verdict is possible and is reported the same way.
            val effect = OpUpdate.effect(SideEffectLevel.STRONG)
            val arms = mutableListOf<OpArm>()
            if (check.storeCanProceed) arms += OpArm.Continue(effect)
            if (check.arrayStoreIsFeasible) arms += throwing(arrayStore(value), effect)
            return OpExecution.of(arms)
        }

        // Object over tag: the component is the ARRAY's own, never the register's, and it is what the stored
        // element's declared TYPE becomes. A tracked array's type is always a real array descriptor
        // (HeapObject.TrackedArray's KDoc, enforced at allocation by `HeapStore.allocateArray`'s own
        // `requireNotNull` on the component), so this is an invariant rather than a case to be lenient about.
        // There is deliberately no `?: tag` fallback: deriving a component from the tag where an object was
        // available is how a check passes for the wrong reason -- `canPutStringInObjectArray` is the fixture
        // that tells the two apart (tag `[Ljava/lang/Object;`, object `[Ljava/lang/String;`).
        val component = requireNotNull(tracked.type.componentType) {
            "Tracked array ${tracked.type} at r$arrayRegister is not an array type: the allocation lost the " +
                "component type (`CoreState.trueTypeOf` / `Frame.allocateArrayObject` -- the harness path is " +
                "the only one that bypasses `HeapStore.allocateArray`'s component check)"
        }
        val stored = storedValue(value, component)
        val index = Coerce.toArrayIndex(indexRaw)

        // ROW 4: the array is provable, the index is not (Unknown, Top, or a raw `Coerce.toArrayIndex`
        // refuses -- see its KDoc for why a Float/Double index is "not provable" rather than truncated).
        //
        // PRECISION IMPROVEMENT #2. The write landed on EXACTLY ONE slot; we simply cannot say which. So each
        // slot afterwards is either its old value or the stored one, and `join` is precisely that statement.
        // A blanket overwrite would be a confident wrong answer for every slot but one, and discarding the
        // whole array (the oracle's answer) throws away the slots that agree -- `join(Known(7,I),
        // Known(7,I))` is `Known(7,I)`, so an array whose old value already equals the stored one stays
        // fully known.
        //
        // The bounds exception is feasible; the NPE is NOT, because the array was just proved non-null, and
        // forking it here would tell a deobfuscator a catch(NullPointerException) handler is reachable on a
        // path Dalvik cannot take.
        //
        // ...and on a ZERO-LENGTH array the `Continue` is infeasible too, which is the mirror defect in its
        // milder form: EVERY index is out of range for a length-0 array, so the store cannot land and this
        // row's own "the write landed on exactly one slot" reasoning is false there. Forking an infeasible
        // fall-through keeps a dead block alive rather than inventing a live `catch`, so spec §2.2 permits it
        // -- but it is still a path Dalvik cannot take, and `new-array v0, v(0), [I` then `aput` with an
        // unprovable index is fully expressible with the opcodes this build has. `AGetOp` carries the
        // identical guard, added in the same change, because the three array ops must agree about
        // feasibility (see the class KDoc of either).
        //
        // The ASE composes here for the same reason it does on row 1, and the join is what makes the two
        // questions independent: "which slot" and "does the store type-check" are separate, and an element is
        // either its old value or the stored one whichever of them is unresolved. On a zero-length array the
        // store cannot land at all, so no ASE is feasible there either -- hence that guard returns first.
        if (index == null) {
            if (tracked.length == 0) return OpExecution.single(throwing(outOfBounds(index = null)))
            val arms = mutableListOf<OpArm>()
            if (check.storeCanProceed) {
                checkNotNull(trackedRef)
                arms += OpArm.Continue(
                    OpUpdate.writeArrayElements(
                        trackedRef,
                        0,
                        List(tracked.length) { i -> join(tracked.element(i), stored, superclasses) },
                    ).then(OpUpdate.effect(SideEffectLevel.WEAK)),
                )
            }
            arms += throwing(outOfBounds(index = null))
            if (check.arrayStoreIsFeasible) arms += throwing(arrayStore(value))
            return OpExecution.of(arms)
        }

        // ROW 5: a provable index outside the bounds -> a DEFINITE AIOOBE, and NO element changes. Negative
        // counts as out of range and is not a distinct exception: ART compares unsigned
        // (`Array::CheckIsValidIndex`), so `index < 0` and `index >= length` are one check (INHERITED,
        // [JVMS] §6.5). A bare `index >= length` test would let -1 through to `setElement(-1, …)` and turn a
        // guest exception into a HOST failure.
        //
        // No ASE composes here either, and this is the INHERITED half of the ordering rather than a choice:
        // AIOOBE strictly precedes ASE, so a verified `aput-object` with both a bad index and an unassignable
        // value throws the bounds exception. The `aastore`-tier refusal therefore sits BELOW this line while
        // the VERIFIER-tier one sat above row 1 -- the two refusals bracket the whole exception order.
        if (index < 0 || index >= tracked.length) return OpExecution.single(throwing(outOfBounds(index)))

        // ROW 6: the store itself, and the `aastore`-tier refusal's home -- after the null and bounds rows,
        // where `component` and `stored` are already locals and `stored` already has the coercion applied, so
        // Dalvik's zero idiom is visible as a real `null` and needs no special case ([JVMS] `aastore`: "If
        // value is null, then value is stored as the component of the array at index").
        //
        // PRECISION IMPROVEMENT #1 needs no special case here -- an `Unknown` value makes `stored` an
        // `Unknown(component)` and the write below puts it in the one slot it belongs in, leaving every other
        // element alone, which is exactly what the engine being replaced could not do (its own test called
        // marking the whole array unknown non-ideal; `APutOpTest.anUnknownValueMakesOnlyItsOwnElementUnknown`
        // is the surviving pin).
        // No `frame.assign` here or anywhere else in this function: the store goes into the array OBJECT, and
        // writing the array register back would be a no-op that only made `registersAssigned` non-empty.
        if (!check.storeCanProceed) return OpExecution.single(throwing(arrayStore(value)))
        checkNotNull(trackedRef)
        val store = OpArm.Continue(
            OpUpdate.writeArrayElement(trackedRef, index, stored)
                .then(OpUpdate.effect(SideEffectLevel.WEAK)),
        )
        if (!check.arrayStoreIsFeasible) return OpExecution.single(store)
        return OpExecution.of(store, throwing(arrayStore(value)))
    }

    /**
     * The `ArrayStoreException` decision. See [StoreCheck] for the rule set, the tier of each rule and both
     * ordering arguments; this function is only their transcription, in order, first match wins.
     *
     * [component] is already derived object-over-tag by the caller and is null when it could not be determined
     * at all -- no tracked array object *and* a register tag that is not an array descriptor.
     *
     * [valueRuntimeType] is the same move on the VALUE side: the tracked object's own type when the value is
     * one, else null. Rule 5 prefers it over the register tag, which is what closed CARRY-FORWARD item 16.
     */
    private fun storeCheck(value: Value, component: VirtualType?, valueRuntimeType: VirtualType?): StoreCheck {
        // RULE 1. Storing null never throws ASE, so this precedes rule 4 (which would otherwise refuse
        // Dalvik's integral zero into a reference component) and rule 5 (which would otherwise ask whether
        // the null's declared type is assignable). Permitted for an UNDETERMINABLE component too: "cannot
        // tell what the component is" must not turn a store that provably cannot throw into one that might --
        // `Object o = null; a[i] = o;` with `a` a parameter is ordinary code and reaches exactly that pair.
        //
        // THE INTEGRAL-ZERO SPELLING IS GATED ON THE `OBJECT` SUFFIX; the real-null spelling is not (review
        // finding m2). `Value.isNull` is a null whatever the mnemonic. `isDalvikNull` is not: it recognises
        // the integer 0, and [BC]'s "(Object) null == (int) 0" is a statement about a REFERENCE context --
        // which is why `isDalvikNull`'s own KDoc says it exists for `aput-object`'s null rule. Applying it
        // uniformly let `aput v2, v0, v1` with `Known(0, "I")` and a `[Ljava/lang/String;` component answer
        // PERMITTED and then store `Known(0, "Ljava/lang/String;")` -- an `Integer` raw under a reference
        // type, i.e. precisely the "type lie every later `aget` propagates" that rule 4's refusal exists to
        // prevent, produced by the rule meant to be the exception to it. Gated, that input reaches rule 4 and
        // is refused. Verifier-invalid either way ([CON] B1: an int-like `aput` needs a primitive-component
        // array), so no fidelity claim moves; this is the engine's own choice made consistent with itself.
        if ((value.isNull || (value.isDalvikNull && storeKind == StoreKind.OBJECT)) &&
            (component == null || component.isReference)
        ) {
            return StoreCheck.PERMITTED
        }

        // The value's SHAPE. `Value.Top` carries no type at all, and the MNEMONIC is then the only evidence
        // there is -- VERIFIER: `aput-object` requires a reference value and every other suffix requires a
        // primitive one ([CON] B1; [ART] `VerifyAPut`). This is NOT the rejected "gate the whole check on
        // `storeKind == OBJECT`": the suffix stands in for a type the register does not carry, and rule 4
        // still refuses a `Ljava/lang/String;`-typed register stored into an `[I` by a plain `aput`, which
        // gating on the suffix would have permitted.
        val valueIsPrimitive = value.type?.isPrimitive ?: (storeKind != StoreKind.OBJECT)

        if (component == null) {
            // RULE 2, and rule 5's second sub-case. A primitive value is permitted because every non-OBJECT
            // suffix requires a primitive-component array, so rule 3 would apply if the component were
            // visible. A reference value is UNANSWERABLE rather than permitted, because by the same verifier
            // argument the component is then a reference too and the subtype question is simply unavailable --
            // and "cannot tell" must not silently become "no throw".
            return if (valueIsPrimitive) StoreCheck.PERMITTED else StoreCheck.UNANSWERABLE
        }

        // RULE 3: both primitive -> permitted, for EVERY suffix. Not "both in {Z B C S I}", which is what the
        // engine being replaced tested and what this plan's first draft prescribed: that leaves `aput-wide` of
        // a `J` into a `[D` throwing, contradicting the INHERITED rule in [StoreCheck]'s KDoc that only
        // `aastore` type-checks at all.
        if (valueIsPrimitive && component.isPrimitive) return StoreCheck.PERMITTED

        // RULE 4: exactly one of the two is primitive.
        if (valueIsPrimitive != component.isPrimitive) return StoreCheck.REFUSED_BY_SHAPE

        // RULE 5: both references, so this is the real `aastore` check -- assignment-COMPATIBLE, never equal
        // (covariance is legal, [JLS] §4.10.3).
        //
        // OBJECT OVER TAG, and this line is CARRY-FORWARD item 16's fix rather than a tidy-up. `aastore`
        // tests the value's ACTUAL class; a register tag is only ever a SUPERTYPE of it (VERIFIER: the
        // verifier proves `runtime <: declared`, never equality), so reading the tag REFUSED stores
        // `aastore` permits -- an invented infeasible throw, the "a handler is reachable when it is not"
        // half of spec §2.2. It became fixable rather than merely filed the moment `new-instance` made
        // guest objects tracked (Plan 9 Task 17): a tracked value now carries its own type, exactly as the
        // ARRAY side already did. Pinned by
        // `APutOpTest.aTrackedObjectsOwnTypeBeatsItsSupertypeTagInTheStoreCheck`, whose fixture round-trips
        // an instance through a `[Ljava/lang/Object;` to get a supertype tag using verifier-valid opcodes
        // alone. Note the direction of safety is unchanged for everything else: where no object exists the
        // tag is all there is, and a refusal taken against a tag on the ARRAY side stays sound for the
        // reason [StoreCheck]'s KDoc gives.
        val valueType = valueRuntimeType ?: value.type ?: return StoreCheck.UNANSWERABLE
        return try {
            if (hierarchy.isSubtypeOf(valueType, component)) {
                StoreCheck.PERMITTED
            } else {
                StoreCheck.REFUSED_BY_ASSIGNABILITY
            }
        } catch (_: ClassLoadFailure) {
            // Ambiguity, not a host failure. [StoreCheck.UNANSWERABLE] carries the adjudication and the
            // reason it differs from Task 6's loadability ruling.
            StoreCheck.UNANSWERABLE
        }
    }

    /**
     * The lattice point that lands in the array slot.
     *
     * The declared type is always [component] -- the ARRAY's, never the value register's tag -- because the
     * slot's type is a property of the array. A value that is not [Value.Known] gives
     * `Unknown(component)`: the value is gone but the slot's type is not, and answering [Value.Top] would
     * discard type information the array itself carries.
     *
     * **A `Known` whose raw the int-like coercion refuses gives `Unknown(component)` too**, and that row was
     * added at Plan 7's closeout to fix a Critical. A `Float`/`Double` raw under a suffix that READS the value
     * as an int used to go through `Coerce.toInt`, which truncates BY VALUE: `aput-byte` of `Known(5.9f, "F")`
     * stored a confident `Known(5, B)`, where a device reads the register's 32 bits (`0x40BCCCCD`, i.e.
     * 1086115021) and stores `0xCD`. **VERIFIER** -> spec §2.2 decides it ([Coerce.toProvableInt] carries the
     * tiering and the other three sites asking the same question); `Unknown(component)` is the answer, matching
     * what [FilledNewArrayOp]'s own fill does with a refused element rather than inventing a second one.
     *
     * `Unknown(component)` rather than the raw stored verbatim, for a reason beyond consistency: storing it
     * would put `Known(5.9f, "B")` in the slot -- a `Float` raw under a `B` tag, a FRESH violation of
     * [org.cf.smalivm.core.agreesWith]'s precondition, and one whose wrong constant reappears anyway the moment
     * an arithmetic op reads the element through the unguarded [Coerce.toInt]. See [coerce]'s KDoc for the two
     * violations that are already disclosed.
     *
     * Pinned by `APutOpTest.aFloatValueIsNotProvableRatherThanTruncatedByValue`, with
     * `canPutWideWithFloat`/`canPutWideWithDouble` pinning the other direction (see [readsValueAsInt]).
     */
    private fun storedValue(value: Value, component: VirtualType): Value {
        if (value !is Value.Known) return Value.Unknown(component)
        // `is Number` is load-bearing and mirrors [coerce]'s own first line: `toProvableInt` also refuses a
        // String, a Character-as-reference, a HeapRef -- every raw a reference store legitimately carries --
        // and those must still be stored unchanged (`canPutStringInStringArray`, `canPutChar`). Conjoined with
        // it, the condition is exactly "a floating-point raw", expressed through the one function that decides
        // where that boundary is rather than by a second copy of its `is Float, is Double` row.
        if (readsValueAsInt && value.raw is Number && Coerce.toProvableInt(value.raw) == null) {
            return Value.Unknown(component)
        }
        return Value.Known(coerce(value.raw), component)
    }

    /**
     * Whether the suffix READS the value as an int, which is exactly the set of suffixes whose stored value a
     * floating-point raw makes unprovable.
     *
     * `PLAIN` and `WIDE` are **false**, and that keeps two migrated expectations alive rather than being a
     * concession: those two return the raw untouched (see [coerce]), so `aput-wide` of `Known(10.45f, "F")`
     * into a `[F` and of a `Double` into a `[D` are ORDINARY, verifier-VALID input, not cross-domain reads --
     * `canPutWideWithFloat` and `canPutWideWithDouble` go red the moment this answers true for them. The other
     * five genuinely narrow the value to an int before storing it, and that is where by-value truncation
     * invents a constant.
     *
     * An exhaustive `when` with no `else`, for the same reason [coerce] has one: an eighth [StoreKind] must be
     * a compile error here rather than silently inheriting whichever answer the fall-through gave.
     */
    private val readsValueAsInt: Boolean get() = when (storeKind) {
        StoreKind.BOOLEAN, StoreKind.BYTE, StoreKind.CHAR, StoreKind.SHORT, StoreKind.OBJECT -> true
        StoreKind.PLAIN, StoreKind.WIDE -> false
    }

    /**
     * The suffix's coercion, mirroring the oracle's `castValue` (`opcode/APutOp.kt`) exactly, including its
     * one structural rule: **a non-`Number` raw is stored unchanged in every kind.** That is what lets a
     * `String`, a `Character`, a `HeapRef` to a nested array, or a guest `java.lang.Class` be stored at all
     * (`canPutChar` seeds a `Character`, and `java.lang.Character` is not a `java.lang.Number`).
     *
     * Only the zero-to-`null` row is **SPEC**: [BC], *"In terms of bitwise representation,
     * `(Object) null == (int) 0`"* -- the same fact as [org.cf.smalivm.core.isDalvikNull], which is why
     * `const/4 v0, 0x0` / `aput-object` stores a real `null` rather than a boxed 0. The other four rows are
     * **CORPUS**: they are what the oracle computed and what the migrated expectations assert, and Dalvik
     * specifies nothing about them because the variant/component agreement is a VERIFIER matter ([CON] B1).
     *
     * **A KNOWN, VERIFIER-EXCLUDED violation of [org.cf.smalivm.core.agreesWith]'s stated precondition, named
     * here so a reader of that function does not have to rediscover it.** `PLAIN`/`WIDE` return the raw
     * untouched while the element's declared type comes from the ARRAY, so `aput-wide` of `Known(7L, "J")`
     * into an `[I` stores `Known(7L, I)` -- a `Long` raw under type `I`, exactly what `agreesWith`'s KDoc says
     * callers must never produce. It is unreachable on verified input (`aput-wide` requires a wide-component
     * array), the oracle's `castValue` does the same, and the failure direction is safe: `7L != 7` under host
     * `equals`, so two such paths join to `Unknown` rather than agreeing wrongly. Coercing to the component
     * would be the alternative and is NOT taken -- it would invent a value for verifier-rejected input and
     * break byte-identity with the `canPutWide*` migrations.
     *
     * `when` over [StoreKind] with no `else`, so an eighth kind is a compile error.
     *
     * **The `?: return raw` below is reachable only for `PLAIN`/`WIDE`, and only for a floating-point raw.**
     * [Coerce.toProvableInt] refuses those, and [storedValue] already diverted every int-reading suffix to
     * `Unknown(component)` before getting here -- so on this path the two rows that would consume the int are
     * the two that return `raw` anyway, and the fall-through agrees with them. Stated rather than left to be
     * re-derived, because "an unreachable branch that looks like the live one" is how a later edit fixes the
     * wrong copy: do not turn this into a `!!`, and do not read it as the refusal (that lives in [storedValue]).
     *
     * **DISCLOSURE: which function this line calls is therefore INERT, measured rather than argued.** Putting
     * `Coerce.toInt` back here kills 0 of 882 tests. It calls [Coerce.toProvableInt] anyway so that no
     * `Coerce.toInt` call survives in an array op -- a grep for one is how Plan 7's closeout found the three
     * real sites -- but the reachability argument above, not a test, is what makes it correct.
     */
    private fun coerce(raw: Any?): Any? {
        if (raw !is Number) return raw
        val int = Coerce.toProvableInt(raw) ?: return raw
        return when (storeKind) {
            // Dalvik has no boolean literal; `Z` values are the integers 1 and 0.
            //
            // **`== 1` and NOT `!= 0`, and the choice is SHARED with [FillArrayDataOp.elementValue]'s `Z`
            // row -- the two must move together.** That op's KDoc discloses it as a CHOICE with an untested
            // consequence and explicitly invites a later plan to switch to `!= 0`; this end presented it as
            // settled fact under a CORPUS tier, so only one of the two recorded that it is shared (the
            // whole-branch review's M3). Taking that invitation on one op alone would make `aput-boolean` and
            // `fill-array-data` disagree about the same byte, silently. They agree today, which is right.
            StoreKind.BOOLEAN -> int == 1
            StoreKind.BYTE -> int.toByte()
            // Characters, like booleans, arrive as integers.
            StoreKind.CHAR -> int.toChar()
            StoreKind.SHORT -> int.toShort()
            StoreKind.OBJECT -> if (int == 0) null else raw
            StoreKind.PLAIN, StoreKind.WIDE -> raw
        }
    }

    /**
     * No message, and that is deliberate: the corpus asserts none for this one. [ART] does have a per-opcode
     * text (*"Attempt to write to null array"*, `common_throws.cc:657`, quoted in
     * `docs/DALVIK-SEMANTICS.md`), so a later plan may adopt it -- an improvement rather than a fix, and no
     * expectation anywhere blocks it. The contrast with `ArrayLengthOp`'s NPE, which DOES carry a message, is
     * not an accident: there the corpus asserts the string *and* ART emits exactly it.
     */
    private fun nullArray(): ThrowableRecipe = ThrowableRecipe.of(NPE)

    /**
     * [index] is the offending index for a DEFINITE out-of-range store, and null for a SPECULATIVE fork
     * (rows 1 and 4), where no offending index exists -- naming one there would fabricate a constant.
     *
     * **The definite form's message is the index in decimal, and that is an arbitrary PINNED ENGINE CHOICE --
     * it is NOT "Dalvik's shape", and it does not match any runtime.** Nothing *specifies* an AIOOBE message:
     * [BC] names no array exception at all and [JVMS] specifies no text. The one real runtime string is
     * **[ART]**'s `"length=%d; index=%d"` (`runtime/common_throws.cc:131-134`, quoted verbatim in
     * `docs/DALVIK-SEMANTICS.md`'s `aget*`/`aput*` section -- this repo's verified-quote registry, and the
     * citation is via that row rather than copied out of a sibling op). So this engine's bare index is a
     * *divergence* from ART rather than a match, chosen because the corpus asserted no message and nothing
     * downstream reads one; [RT] additionally records that the text CHANGED between runtimes, so matching any
     * particular string would be false precision. Asserted anyway, because an unasserted message drifts
     * silently -- but a test asserting an exception *message* is never evidence of Dalvik fidelity.
     */
    private fun outOfBounds(index: Int?): ThrowableRecipe =
        ThrowableRecipe.of(ARRAY_INDEX_OUT_OF_BOUNDS, index?.toString())

    /**
     * **The message is the VALUE's declared type in binary form, and it is a PINNED ENGINE CHOICE rather than
     * anything specified.** [BC] names no array exception at all and [JVMS] specifies no text, so the only
     * runtime string is [ART]'s, which is a two-class *"... cannot be stored in an array of type ..."* form --
     * i.e. this is a **divergence** from ART and not a match. It is what the engine being replaced computed
     * (`ClassNameUtils.internalToBinary(store.type)`), which is the whole of its justification: nothing
     * downstream reads an exception message, `Tester.testSimpleException` never checked one, and [RT] records
     * that runtimes changed their array-exception text deliberately, so matching any particular string would be
     * false precision. Asserted anyway, in
     * `APutOpTest.incompatibleValueTypeThrowsArrayStoreExceptionHasNoChildrenAndAssignsNoRegisters` and
     * `…aCovariantlyTaggedArrayStillThrowsForAnIncompatibleComponent`, because an unasserted message drifts.
     *
     * A value carrying **no type at all** ([Value.Top]) gets no message rather than a fabricated one -- the
     * same rule [outOfBounds] follows for a speculative bounds exception.
     */
    private fun arrayStore(value: Value): ThrowableRecipe {
        val storedTypeName = value.type?.let { binaryNameOf(it) }
        return ThrowableRecipe.of(ARRAY_STORE, storedTypeName)
    }

    private fun throwing(recipe: ThrowableRecipe, update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
        OpArm.Throw(update, ThrowEvidence.Fresh(recipe))

    /**
     * A descriptor as Java's binary name, mirroring the oracle's `ClassNameUtils.internalToBinary` exactly
     * (the eight primitive codes by name, an array descriptor with its `/` replaced, a class descriptor
     * unwrapped) rather than importing it: `org.cf.util` is oracle-side plumbing, and reimplementing four
     * lines is cheaper than a dependency `core/op/` would then have to shed.
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
        // Unreachable -- no register is ever void-typed -- but present because the mirrored map has it and
        // because without it a `V` descriptor would take the `else` branch and raise
        // StringIndexOutOfBoundsException, turning an impossible input into a host failure rather than a
        // harmless string (review finding m6: the "mirrors it exactly" claim was false by exactly this row).
        "V" -> "void"
        else ->
            if (descriptor.startsWith("[")) descriptor.replace('/', '.')
            else descriptor.substring(1, descriptor.length - 1).replace('/', '.')
    }

    // Matches the oracle's `opcode/APutOp.kt:60` so Plan 10's instruction listing does not diverge. The
    // VALUE register renders first, which is the whole operand-order hazard in one line.
    override fun toString(): String = "$name r$valueRegister, r$arrayRegister, r$indexRegister"

    private companion object {
        val NPE: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")
        val ARRAY_INDEX_OUT_OF_BOUNDS: VirtualType =
            VirtualType.of("Ljava/lang/ArrayIndexOutOfBoundsException;")
        val ARRAY_STORE: VirtualType = VirtualType.of("Ljava/lang/ArrayStoreException;")
    }
}
