package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ThrowEvidence

/**
 * Throws the register's value. The guest throwable is carried as a [org.cf.smalivm.core.Value] --
 * never reconstructed from a class and a message -- so its identity survives to the handler
 * (spec §7). Handler routing belongs to the session, not here.
 *
 * **SPEC** for the opcode itself is four words ([BC], `throw`): *"Throw the indicated exception."*
 * Everything below is the operand cases that sentence does not cover, and the boundary between the two
 * groups is the whole of this op's semantics. `docs/DALVIK-SEMANTICS.md`'s `throw` section carries the
 * quoted sources and the tiers.
 *
 * **PROVABLE -- a definite, fresh, message-less `NullPointerException`, synthesized here.** Dalvik does not
 * throw the null reference:
 *  - **INHERITED**, [JVMS] §6.5 `athrow`, *"Run-time Exceptions"*: *"If objectref is null, athrow throws
 *    a NullPointerException instead of objectref."* Inheritance is available here because `throw` has a
 *    JVM counterpart with a documented run-time exception -- which is **not** true of every opcode in
 *    this plan (`fill-array-data` has no counterpart at all), so it is checked rather than assumed.
 *  - and **[ART]** agrees, unconditionally: `runtime/interpreter/interpreter_switch_impl-inl.h`'s `THROW()`
 *    (lines 933-950 at `refs/heads/main`) opens with
 *    `if (UNLIKELY(exception == nullptr)) { ThrowNullPointerException(); }` at line 939, outside any build-
 *    or verification-dependent gate. `ThrowNullPointerException()`'s no-argument overload is
 *    `ThrowException("Ljava/lang/NullPointerException;")` (`runtime/common_throws.cc:689-691`), i.e. **no
 *    message** -- which is why the synthesized throwable below has none, unlike `ArrayLengthOp`'s.
 *
 * **What counts as null is [org.cf.smalivm.core.readsAsNullReference], and it is wider than it looks: EVERY
 * primitive-typed `Known`, not only zero.** ART's **switch** interpreter's `THROW()` fetches its operand with
 * `GetVRegReference(A())`, which reads `ShadowFrame`'s **parallel reference array** -- and every primitive
 * write clears the matching slot (`SetVReg`/`SetVRegFloat`/`SetVRegLong`/`SetVRegDouble` all end in
 * `References()[i].Clear()`, `shadow_frame.h:190-228`; ART states the invariant in its own prose at
 * `:401-408`). So `const/4 v0, 0x5` then `throw v0` reaches the **same** definite NPE as `const/4 v0, 0x0`
 * then `throw v0`: the register's value is irrelevant to this engine, only the fact that its last write was
 * primitive. The predicate's KDoc has the full chain.
 *
 * ⚠️ **NAME THE TIER, because the two halves of that sentence do NOT carry the same evidence.** ART's
 * *default* interpreter on a modern device is `nterp`, whose `op_throw`
 * (`runtime/interpreter/mterp/x86_64ng/control_flow.S:171-176`) reads the **raw** register with `GET_VREG`
 * and hands it to `art_quick_deliver_exception`. For a **zero** raw that is the identical NPE
 * (`quick_throw_entrypoints.cc:51-69`: *"exception may be null, in which case this routine should throw
 * NPE"*), so the zero rows are corroborated by **both** tiers -- a strengthening. For a **non-zero**
 * primitive the two tiers **contradict** each other: nterp delivers the integer `5` as though it were a
 * `Throwable*`. That row is therefore **VERIFIER** tier -- this engine's spec §2.2 choice, and unreachable on
 * verified input (`runtime/verifier/method_verifier.cc:3253-3255` hard-fails *"thrown value of non-reference
 * type"*, which rejects the whole class) -- and **not** an [ART] fact. The behaviour below is unchanged and
 * must stay: it is the sound direction and it matches one real tier exactly. See
 * `docs/DALVIK-SEMANTICS.md`'s *ART has TWO interpreter tiers* subsection, which is the general instrument.
 *
 * Three inputs therefore reach one outcome, and the first two are ordinary smali:
 *  - `Known(0, "I")` -- Dalvik's null idiom, also **SPEC** on its own ([BC], "General design": *"In terms of
 *    bitwise representation, `(Object) null == (int) 0`"*);
 *  - `Known(5, "I")` and every other primitive-typed `Known`, including `Known(false, "Z")` and
 *    `Known(0.0f, "F")`, which `isDalvikNull` deliberately excludes and which this op nonetheless resolves,
 *    because `SetVRegFloat` clears the reference slot exactly as `SetVReg` does;
 *  - `Known(null, T)` -- a definite null reference.
 *
 * **CORRECTS THIS FILE'S OWN EARLIER CLAIM.** Task 12's first round wrote that ART *"reinterprets the 5 as
 * an object pointer and dereferences it"*, called the case undefined behavior, and left it on the ambiguous
 * fork. The **conclusion** it drew was wrong in the direction that invents reachable handlers: a
 * `catch (IOException)` over `throw` of `const/4 v0, 0x5` was reported live, an `I`-typed escape was reported
 * feasible for a throw that is definitely caught, and the surviving correct handler bound the int 5 where the
 * switch interpreter binds an NPE. **Its description of the mechanism, however, turns out to be what nterp
 * actually does** (2026-08-04) -- which does not resurrect the old fork, because the input is
 * verifier-rejected and a wild-pointer dereference is not an outcome this engine can model, but it does mean
 * *"ART does not reinterpret the bits"* was never true of ART as a whole. That is why the row's tier is now
 * VERIFIER: the answer stands on spec §2.2, not on a claim about every ART interpreter.
 *
 * **NOT PROVABLE -- still partitioned by the handler resolver.**
 *  - [org.cf.smalivm.core.Value.Unknown] and [org.cf.smalivm.core.Value.Top]: nothing is known, so nothing
 *    is proved. This is the majority case in a real APK -- every throw of a parameter, a field or a call
 *    result -- and answering "definitely null" here would replace a fork with a definite throw on unproven
 *    grounds, which is the narrowing spec §2.2 forbids. An `Unknown(I)`'s *tag* is not evidence: it is an
 *    upper bound, and the register may hold a reference the engine simply could not track.
 *  - a `Known` **reference** that is not a `Throwable` (`Known(hostString, "Ljava/lang/String;")`): this is
 *    the input ART's `InternalError` actually covers -- *"Throwing '%s' that is not instance of Throwable"*,
 *    thrown at `interpreter_switch_impl-inl.h:943-945` behind the `DoAssignabilityChecks()` gate at `:940`,
 *    which is **on** for any method that skipped or failed verification. Normalization converts it to one
 *    guest `InternalError` before the resolver runs, so `catch (Error)` handles it and `catch (Exception)` does not.
 *
 * **Side effects: NONE, and `recordSideEffect` is called nowhere in this file.** Allocating the NPE is a
 * fully modelled write to the run's own heap: the object is fresh, its identity is a new
 * [org.cf.smalivm.core.HeapRef], and no *other* tracked object's contents change -- which is the test
 * `Frame.recordSideEffect`'s KDoc states ("call it whenever a write's TARGET cannot be identified"). A
 * spurious `STRONG` here would silently stop every constant fold in any method containing a `throw`,
 * since `methodSideEffectLevelOverExploredPaths` maxes over addresses.
 *
 * **[registersAssigned] is `emptySet()`, on `throw`'s own terms rather than by analogy with the array
 * ops.** Format `11x` has one register operand and [BC] specifies it as the value to throw, so there is
 * no destination field to name. More decisively, `throw` never continues: there is no successor in *this*
 * frame for an assignment to be visible to. The value a handler eventually sees is delivered through the
 * handler frame's `caught` slot and lands in a register only when `move-exception` -- a separate
 * instruction at the handler's own address -- runs, so the assignment is correctly attributed there.
 */
internal class ThrowOp(address: Int, private val register: Int) : Op(address) {
    override val name: String = "throw"
    override val registersRead: Set<Int> get() = setOf(register)

    override fun execute(frame: Frame): OpExecution = OpExecution.single(
        OpArm.Throw(evidence = ThrowEvidence.Existing(frame.readFrameValue(register))),
    )
}
