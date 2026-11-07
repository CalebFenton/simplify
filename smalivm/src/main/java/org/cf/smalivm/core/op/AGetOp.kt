package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
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
 * Reads one element of a tracked array -- all seven `aget*` opcodes (44-4a, format 23x = TWO code units).
 *
 * **The suffix changes exactly ONE thing for the core: whether the destination is a wide pair.** The
 * element's value and declared type come from the tracked array, not from the mnemonic, which is why a
 * single op serves all seven. That is an **ENGINE CHOICE**, not an observation about Dalvik, and it is the
 * most load-bearing sentence here, so it is tiered in both directions:
 *
 *  - For **verified** input it costs nothing: the verifier proves the variant and the component agree
 *    (**VERIFIER** -- [CON] B1; [ART] `VerifyAGet`), so reading the element and trusting the array is
 *    exactly right. Note which pairs the verifier *allows*, because they are why a register tag is only a
 *    hint: bare `aget` accepts `int[]` **or** `float[]`, `aget-wide` accepts `long[]` **or** `double[]`, and
 *    the four narrow forms require exact matches. A mismatch is a **hard** verification failure, never a
 *    runtime exception. (The plan offers a `method_verifier.cc` line range for that last sentence, and it is
 *    deliberately not repeated here: `docs/DALVIK-SEMANTICS.md` -- this repo's verified-quote registry --
 *    carries the CLAIM but not that range. The operative test is **fetched vs. copied**, not range vs. no
 *    range: the registry does carry ART line ranges, three of them, for strings someone actually fetched.
 *    Cited the way the registry cites it.)
 *  - For **unverified** input -- this engine's target workload -- there is no behavior to copy at all. ART
 *    does `ObjPtr<ArrayType>::DownCast(a)` with **no** array-type check and then a **mis-scaled** stride,
 *    i.e. it reads the wrong bytes. So `aget-wide` off a narrow array, or bare `aget` off
 *    `[Ljava/lang/String;`, has no defined answer, and reading the element verbatim is *our* total answer,
 *    justified by spec §2.2 (it never invents a constant and never drops a feasible path) and **not** by
 *    "ART does this".
 *
 * **Both exceptions are INHERITED, not specified by Dalvik.** [BC]'s entire text for all fourteen
 * `aget*`/`aput*` opcodes is one shared sentence -- *"Perform the identified array operation at the
 * identified index of the given array, loading or storing into the value register."* -- and the whole
 * document names exactly two exceptions, neither of them an array exception. `NullPointerException` and
 * `ArrayIndexOutOfBoundsException` come from [JVMS] §6.5 `iaload`/`aaload`, legitimised by [VER]'s
 * *"generally the same set of checks as are described in The Java Virtual Machine Specification, Second
 * Edition, including the updates planned for the Third Edition"*. **Quote it through the edition, because
 * the bridge is same-DOCUMENT and not same-EDITION**: [VER] names the Second/Third Editions while every
 * [JVMS] citation here is SE21. The inheritance still holds for exactly these rules -- §6.5's
 * `iaload`/`aaload`/`aastore`/`arraylength` and §4.10.3 array subtyping are unchanged from the Second Edition
 * through SE21 -- and saying so is what stops this from implying a per-constraint, same-edition mapping that
 * does not exist. Their ORDER -- NPE strictly before AIOOBE -- is the same document's `Otherwise` chaining,
 * which is why the array rows below are all checked before the index rows. Quotes and tiers:
 * `docs/DALVIK-SEMANTICS.md#aget--aput-ops-4451-23x`.
 *
 * **The whole difficulty is WHICH exceptions are feasible**, so each row below is one early return with its
 * own reasoning and a reader can check them independently. Two directions, both wrong in their own way: an
 * unprovable array or index that does NOT fork leaves an unexplored feasible path on a run reporting
 * `Complete` (the worst outcome this engine produces), and a provable array that DOES fork invents a
 * reachable `catch` handler on a path Dalvik cannot take.
 *
 * **`component` comes from the OBJECT when there is one** -- `tracked.type.componentType`, established at
 * allocation from something that cannot lie. The register tag is consulted ONLY when there is no object,
 * and that is why `aget-object` off a `String[]` register tagged `Ljava/lang/String;` produces a correctly
 * typed element with no string surgery on the tag (object over tag; see [org.cf.smalivm.core.HeapObject]).
 *
 * KNOWN, and harmless in the one direction it can occur: on unverified input the static
 * [registersAssigned] can claim a pair the [Frame] does not reserve -- `aget-wide` off a narrow array
 * assigns a narrow element, and [Frame.assign] keys the high half off the VALUE's type. The decode-time
 * declaration is the conservative side of that disagreement ([org.cf.smalivm.core.ExecutionGraph]'s
 * `availableRegisters` offers one register fewer), which is the direction to be wrong in.
 */
internal class AGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val arrayRegister: Int,
    private val indexRegister: Int,
    private val isWide: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(arrayRegister, indexRegister)

    // isWide is decided at DECODE time from the opcode (`AGET_WIDE` alone), per Op.destinationRegisters'
    // no-default rule. Deriving it from the component type here would be a guess about a register whose
    // declared tag the corpus deliberately loosens -- and the array may not even be tracked.
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide)

    override fun execute(frame: Frame): OpExecution {
        val array = frame.read(arrayRegister)

        // ROW 1: the array is not provable at all (Unknown or Top). Checked BEFORE the index deliberately:
        // with nothing known about the array, nothing is known about the element no matter what the index
        // is, and BOTH exceptions are feasible -- the array may be null, and if it is not, any index may be
        // out of range. Dropping either would leave an unexplored feasible path on a Complete run (spec
        // §2.2). This is the row that needs the multi-escape `commit`: in a method with no try block both
        // throws escape from ONE node.
        if (array !is Value.Known) {
            return OpExecution.of(
                OpArm.Continue(OpUpdate.assign(destRegister, unknownElementFromTag(array.type))),
                throwing(nullArray()),
                throwing(outOfBounds(index = null)),
            )
        }

        // ROW 2: the array register READS AS NULL -> a DEFINITE NPE, no fork. THREE spellings of one thing,
        // and the third was added in Plan 7 Task 12's fix round: a null raw; an integral zero ([BC], SPEC:
        // "In terms of bitwise representation, (Object) null == (int) 0", so `const/4 v0, 0x0` then `aget` is
        // a definite NPE even though `Value.isNull` cannot see it); and ANY primitive-typed `Known`, whatever
        // its value. The third is [ART]'s SWITCH interpreter: `HandleAGet` fetches the array with
        // `GetVRegReference(B())` and throws an NPE when it is null (interpreter_switch_impl-inl.h:508-522,
        // the fetch and the throw at :510-514), and `GetVRegReference` reads
        // ShadowFrame's PARALLEL reference array, which every primitive write clears (shadow_frame.h:190-228;
        // ART's own prose at :401-408). So `const/4 v0, 0x5` then `aget v1, v0, v2` is a definite NPE under
        // `-Xint`, and testing only `isDalvikNull` DROPPED it -- an unexplored feasible path on a
        // Complete run, which is the worst outcome this engine can have. See `readsAsNullReference`.
        // TIER, corrected 2026-08-04: this line used to say "on a real device", and a DEFAULT device runs
        // nterp, whose op_aget (mterp/x86_64ng/array.S:1-38) reads the RAW register and would dereference the
        // 5. The zero spellings hold under BOTH tiers (nterp's `testl`/`je common_errNullObject`); the
        // non-zero primitive is a VERIFIER-tier choice (VerifyAGet hard-fails "not array type ... with aget",
        // method_verifier.cc:5054-5055), NOT an [ART] behaviour. The answer does not change.
        if (array.readsAsNullReference) return OpExecution.single(throwing(nullArray()))

        // PROVABLY NON-NULL from here down -- and now for a sound reason rather than the wrong one this
        // comment used to give ("a Known non-zero raw"). What makes it provable is that every primitive-typed
        // `Known` was diverted above, so a `Known` reaching here is a REFERENCE with a non-null raw. The NPE
        // is INFEASIBLE in every row below and must not be forked by any of them.
        val tracked = (array.raw as? HeapRef)?.let { frame.arrayAt(it) }
        if (tracked == null) {
            // ROW 6 (last in the plan's table, but its condition is disjoint from the index rows, which all
            // presuppose a tracked array -- so it is resolved here, before the index is even read): the
            // value is Known and non-zero but there is no array behind it. A raw that is not a HeapRef at
            // all, or a HeapRef to something else -- `frame.arrayAt` answers null for a GuestThrowable,
            // which is NOT the same as a null array, and collapsing the two is the trap ArrayLengthOp
            // documents. NO NPE (provably non-null) and NO AIOOBE (a bounds exception needs a length this
            // input does not have): both are infeasible, so forking them would invent two reachable
            // handlers. VERIFIER -> engine's choice, and the choice is total-but-uninformative; see the
            // class KDoc for why ART's unchecked DownCast is not a behavior to reproduce.
            return OpExecution.single(
                OpArm.Continue(OpUpdate.assign(destRegister, unknownElementFromTag(array.type))),
            )
        }

        // Object over tag: the component is the ARRAY's own, never the register's. A tracked array's type is
        // always a real array descriptor (HeapObject.TrackedArray's KDoc), so this is an invariant rather
        // than a case to be lenient about. There is deliberately no `?: tag` fallback here: deriving a
        // component from the tag where an object was available is how a check passes for the wrong reason.
        //
        // ONE cause, and Plan 7 Task 8 MEASURED that the second cause this comment used to name does not
        // exist. It said `new-array v0, v1, Ljava/lang/Object;` -- decodable, `isLoadable`, verifier-rejected
        // -- reaches here because `NewArrayOp` has no `require(arrayType.isArray)`. It does not: the
        // invariant is enforced one level down, at the allocation site, by `HeapStore.allocateArray(type,
        // length)`'s own `requireNotNull(type.componentType)`. Measured end to end on that exact
        // instruction: a KNOWN length (1 and 0 both) gives `Partial(HOST_FAILURE)` from inside `new-array`
        // with NO object allocated, and an UNPROVABLE length gives `Complete` with
        // `Unknown(Ljava/lang/Object;)` and no allocation at all. So no component-less TrackedArray can
        // reach this op from `new-array`, and no `require` was added there (it would have been a second
        // check for an invariant already held, on another task's op).
        //
        // What remains is the HARNESS path, which is the only caller that bypasses that check:
        // `Frame.allocateArrayObject` -> `HeapStore.allocateArray(type, elements)`, driven by
        // `CoreState.trueTypeOf`. Plans 10/11's `fill-array-data` and `filled-new-array` will use the same
        // elements-taking overload and must resolve a real array type before calling it. If this ever does
        // fire, the resulting IllegalArgumentException becomes `Aborted(HOST_FAILURE)` for this path through
        // `ExecutionSession`'s boundary -- the sanctioned direction (spec §2.2), not unsoundness.
        val component = requireNotNull(tracked.type.componentType) {
            "Tracked array ${tracked.type} at r$arrayRegister is not an array type: either the allocation " +
                "lost the component type, or a `new-array` named a non-array type operand"
        }
        val index = Coerce.toArrayIndex((frame.read(indexRegister) as? Value.Known)?.raw)

        // ROW 3: the array is provable, the index is not (Unknown, Top, or a raw `Coerce.toArrayIndex`
        // refuses). Dalvik's tags are ambiguous, so an index register may legitimately hold a Short, a
        // Character or a Boolean, which is exactly why the raw goes through `Coerce` rather than an `as? Int`.
        //
        // WHAT IT REFUSES, and this row is where the refusal lands: a `Float` or `Double` raw. Plan 7 Task 7
        // used `Coerce.toInt`, which is `is Number -> raw.toInt()` and therefore truncates BY VALUE -- an
        // index register holding `Known(2.9f, "F")` yielded a DEFINITE 2, took row 4 or row 5, and forked
        // nothing, where ART reads the register's 32 BITS as an int (`2.9f` is `0x4039999A`, i.e.
        // 1077516698 -- the decimal was wrong by a digit transposition until Plan 7's closeout, in this
        // comment and in two other places). That is VERIFIER-tier input (no verified dex puts a float in an
        // index register), so the engine chooses, and spec §2.2 decides the choice: truncating by value
        // invented a constant no runtime produces AND dropped a feasible bounds exception.
        // `Coerce.toArrayIndex` -- decided in Task 8 for `aget` and `aput` at once, and since Plan 7's
        // closeout a one-line delegation to `Coerce.toProvableInt`, which is the ONE implementation the
        // index, the `new-array` length, the `aput` value and the `filled-new-array` elements all share --
        // refuses those two and lets them fall into this row's fork. Its KDoc carries the full tiering.
        //
        // The bounds exception is feasible; the NPE is NOT, because the array was just proved
        // non-null, and forking it here would tell a deobfuscator a catch(NullPointerException) handler is
        // reachable on a path Dalvik cannot take.
        //
        // ...and on a ZERO-LENGTH array the `Continue` is infeasible too, which is the mirror defect in its
        // milder form: EVERY index is out of range for a length-0 array, so the read cannot succeed. Forking
        // an infeasible fall-through keeps a dead block alive rather than inventing a live `catch`, so spec
        // §2.2 permits it -- but it is still a path Dalvik cannot take, and `new-array v0, v(0), [I` then
        // `aget` with an unprovable index is fully expressible with the opcodes this build has. `APutOp`
        // carries the identical guard on its own equivalent row, added in the same change, because the array
        // ops must agree about feasibility. Pinned by
        // `AGetOpTest.aZeroLengthArrayCannotBeReadAtAnyIndexSoTheFallThroughIsInfeasible`.
        if (index == null) {
            if (tracked.length == 0) return OpExecution.single(throwing(outOfBounds(index = null)))
            return OpExecution.of(
                OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(component))),
                throwing(outOfBounds(index = null)),
            )
        }

        // ROW 4: a provable index outside the bounds -> a DEFINITE AIOOBE. Negative counts as out of range
        // and is not a distinct exception: ART compares unsigned (`Array::CheckIsValidIndex`), so
        // `index < 0` and `index >= length` are one check (INHERITED, [JVMS] §6.5). A bare `index >= length`
        // test would let -1 through to `element(-1)` and turn a guest exception into a HOST failure.
        if (index < 0 || index >= tracked.length) return OpExecution.single(throwing(outOfBounds(index)))

        // ROW 5: the element, VERBATIM -- including `Unknown` for a single unknown element, which is the
        // element-wise precision the oracle cannot express (it marks the whole array unknown). The load
        // reads the array OBJECT, not any static type: INHERITED, and [BC] uses that same wording for
        // `array-length`.
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, tracked.element(index))))
    }

    /**
     * No message, and that is deliberate: the corpus asserts none for this one. The contrast with
     * `ArrayLengthOp`'s NPE, which DOES carry one, is not an accident -- there the corpus asserts the string
     * *and* [ART] emits exactly it (`common_throws.cc:639-667`). [ART] also has per-opcode texts for these
     * two (*"Attempt to read from null array"* / *"…write to null array"*), so a later plan may adopt them;
     * that would be an improvement rather than a fix, and no expectation anywhere blocks it.
     */
    private fun nullArray(): ThrowableRecipe = ThrowableRecipe.of(NPE)

    /**
     * [index] is the offending index for a DEFINITE out-of-range read, and null for a SPECULATIVE fork
     * (rows 1 and 3), where no offending index exists -- naming one there would fabricate a constant, which
     * is the one thing spec §2.2 forbids outright.
     *
     * **The definite form's message is the index in decimal, and that is an arbitrary PINNED ENGINE CHOICE
     * -- it is NOT "Dalvik's shape", and it does not match any runtime.** Nothing *specifies* an AIOOBE
     * message: [BC] names no array exception at all and [JVMS] specifies no text. The one real runtime string
     * is **[ART]**'s `"length=%d; index=%d"` (`runtime/common_throws.cc:131-134`, quoted verbatim in
     * `docs/DALVIK-SEMANTICS.md`'s `aget*`/`aput*` section) -- so this engine's bare index is a *divergence*
     * from ART rather than a match, deliberately, because the corpus asserted no message and nothing
     * downstream reads one. [RT] additionally records that the text CHANGED between runtimes, which is why
     * matching any particular string would be false precision. `AGetOpTest` asserts it anyway, because an
     * unasserted message drifts silently -- but a test asserting an exception *message* is never evidence of
     * Dalvik fidelity, per `docs/DALVIK-SEMANTICS.md`'s "compare types, not strings". Adopting ART's exact
     * string would be a defensible improvement; it needs the two migrated expectations updated, nothing more.
     */
    private fun outOfBounds(index: Int?): ThrowableRecipe =
        ThrowableRecipe.of(
            ARRAY_INDEX_OUT_OF_BOUNDS,
            index?.toString(),
        )

    private fun throwing(recipe: ThrowableRecipe): OpArm.Throw =
        OpArm.Throw(evidence = ThrowEvidence.Fresh(recipe))

    /**
     * The element type when there is no array OBJECT to ask: the register tag's component, or the tag itself
     * when it is not an array descriptor (the corpus tags array registers loosely on purpose). A tag of null
     * -- `Value.Top`, an unassigned register -- means there is no type information at all, so the answer is
     * `Top` rather than an `Unknown` of some invented type.
     *
     * For the Unknown/Top and not-an-array rows ONLY. A tracked array uses its own component type.
     */
    private fun unknownElementFromTag(tag: VirtualType?): Value =
        if (tag == null) Value.Top else Value.Unknown(tag.componentType ?: tag)

    // Matches the oracle's `opcode/AGetOp.kt:56` so Plan 10's instruction listing does not diverge.
    override fun toString(): String = "$name r$destRegister, r$arrayRegister, r$indexRegister"

    private companion object {
        val NPE: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")
        val ARRAY_INDEX_OUT_OF_BOUNDS: VirtualType =
            VirtualType.of("Ljava/lang/ArrayIndexOutOfBoundsException;")
    }
}
