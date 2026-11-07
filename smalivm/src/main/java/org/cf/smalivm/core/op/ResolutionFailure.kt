package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.VirtualType

/**
 * **One family, one answer: a type operand that names a class absent from the loaded set throws a guest
 * `NoClassDefFoundError`** -- a `LinkageError`, i.e. an **`Error`** and not an `Exception`.
 *
 * Settled and tiered once, in `docs/DALVIK-SEMANTICS.md`'s *"Resolution failure is one family, tiered
 * once"*; **cite it, do not re-derive it.** *That* the failure is a linking error is **INHERITED** ([JVMS]
 * §5.4.3.1, cited per instruction -- `checkcast`/`instanceof` carry the clause directly and `const-class`
 * inherits `ldc`'s). *Which* exception is **measured -- `[DEVICE](Tier A, -Xint, art_standalone
 * 0_git20251009-r2)`**, cross-validated `[DEVICE](Tier B, nterp default, API 36)`: all five resolving
 * opcodes raise `java.lang.NoClassDefFoundError`, and every present-type control resolves cleanly, so it
 * is the absence and not the classpath. Fixtures:
 * `tools/art-runner/fixtures/Fx_{ConstClass,CheckCast,InstanceOf,NewArray,FilledNewArray}AbsentType.smali`
 * against `Fx_*PresentType.smali`. Plan 9 Task 15 added `Fx_SGetAbsentType` and `Fx_InvokeAbsentType`, which
 * answer the same way, so `sget`/`sput`/`invoke` are no longer extrapolation either.
 *
 * **`Error`-versus-`Exception` is the whole point, and it is a deobfuscation-fidelity question rather than a
 * naming quibble.** Obfuscator output is full of `catch (Exception)` wrappers. A `catch (Exception)` does
 * **not** catch an `Error`, so answering `ClassNotFoundException` here -- which the old engine does for
 * `const-class` (`opcode/ConstOp.kt:68-81`) and `new-array` (`opcode/NewArrayOp.kt:38-49`), purely because
 * it resolved through a host `ClassLoader` and propagated *its* exception -- puts a handler on the
 * reachable side of the line when a device never enters it. That is spec §2.2's invented-infeasible-path
 * direction, and it makes a deobfuscator believe a dead block is live.
 *
 * **DEFINITE, never a fork.** Absence is a **fact** about this input, not an unknown: spec §3 makes
 * multidex a non-goal, so the loaded class set is the whole world ([org.cf.smalivm.core.ClassIndex.isLoadable]'s
 * KDoc carries the argument). Forking a normal path here would invent a successor for every absent class
 * in an obfuscated APK.
 *
 * **Thrown at EXECUTE time, before any register is read** -- which is not a convenience but the measured
 * ordering: [ART] resolves the type operand ahead of the null shortcut, so an absent type on a
 * `check-cast`/`instance-of` whose source register holds **null** still throws
 * (`Fx_{CheckCast,InstanceOf}AbsentType` both use a null source; same `[DEVICE]` run). An implementation
 * written from [JVMS], which puts the null check first, is wrong here.
 *
 * **Why this is one shared function and not a fourth copy of five lines.** The semantics doc's own reason
 * for tiering the family in a single subsection is *"so the three rows above cannot drift apart"* -- there
 * should be one place to be wrong. [ConstOp], [CheckCastOp] and [InstanceOfOp] all route here.
 * [NewArrayOp] and [FilledNewArrayOp] landed this behaviour in Plan 6/7 and still carry their own private
 * copies; they are deliberately left untouched by Task 15 (they are already correct, and their messages are
 * pinned by name), so folding them in is a cosmetic follow-up rather than a behaviour change.
 */
internal fun resolutionFailure(frame: Frame, absentType: VirtualType): OpExecution =
    OpExecution.single(
        OpArm.Throw(
            evidence = ThrowEvidence.Fresh(
                ThrowableRecipe.of(NO_CLASS_DEF_FOUND_ERROR, binaryBaseName(absentType)),
            ),
        ),
    )

/**
 * The guest error's MESSAGE, and it is **CORPUS tier at best -- held for byte-identity, never asserted as
 * evidence of Dalvik fidelity.** [RT] records that runtimes changed these texts deliberately; a device says
 * `"Failed resolution of: Lcom/example/DefinitelyMissing;"` (the DESCRIPTOR) while this is the binary base
 * name, which is the shape the old engine's host `ClassNotFoundException` had and the shape
 * `core/op/NewArrayOpTest.nonExistentClassNameThrowsException` already pins. Either is defensible; what is
 * not defensible is a test treating the string as proof of anything. Assert the throwable **class**.
 *
 * `[[Lfoo/Bar;` -> `foo.Bar`: the array wrappers are peeled, because the class that failed to resolve is
 * the component base, not the array. A primitive base cannot reach here (`isLoadable` answers `true` for
 * one, there being no class to find), so the `removePrefix`/`removeSuffix` pair is exact rather than
 * defensive.
 */
private fun binaryBaseName(type: VirtualType): String {
    var base = type
    while (base.isArray) base = base.componentType!!
    return base.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
}

/**
 * **One member along the same family: the class resolved and declares no such FIELD** -- a guest
 * `NoSuchFieldError`, which is a `LinkageError` and therefore an **`Error`**, so a guest `catch (Exception)`
 * does not catch it. Same handler-reachability stake as the absent-class row above, one resolution step later.
 *
 * **Tier: INHERITED, and weaker than its siblings on purpose.** [JVMS] §5.4.3.2 field resolution defers its
 * failure to §5.4.3.1, and `getstatic`/`putstatic`'s Linking Exceptions clause names `NoSuchFieldError`
 * outright. The **method** twin of this row IS measured -- `NoSuchMethodError`, `Fx_InvokeMissingMethod`,
 * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated `[DEVICE](Tier B, nterp default,
 * API 36)` (plan adjudication 7b) -- and the field twin has **no fixture**, so it is not claimed as a device
 * fact. Measuring it is a `Fx_SGetMissingField` away and would upgrade the row; Task 18 did not take it, and
 * says so rather than borrowing the method measurement.
 *
 * **What it replaces is not an imprecision but a host crash.** The oracle resolves the field at op-build time
 * with `fieldClass.getField(fieldReference.name)!!` (`opcode/SGetOp.kt:52`, `opcode/SPutOp.kt:55`), so a
 * reference to a field a loaded class does not declare kills the whole run with a host
 * `NullPointerException` -- the failure mode plan adjudication 7 exists to remove, and unremarkable input for
 * a tool whose diet is obfuscated APKs.
 *
 * **DEFINITE, never a fork**, for [resolutionFailure]'s reason: spec §3 makes the loaded class set the whole
 * world, so "this class declares no such field" is a fact about the input.
 *
 * [fieldDescriptor] is what the INSTRUCTION named (`Lfoo;->bar:I`) -- there is no resolved field to name -- and
 * like every message in this file it is **CORPUS tier at best**: assert the throwable CLASS, never the string.
 */
internal fun noSuchFieldFailure(frame: Frame, fieldDescriptor: String): OpExecution =
    OpExecution.single(
        OpArm.Throw(
            evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NO_SUCH_FIELD_ERROR, fieldDescriptor)),
        ),
    )

private val NO_CLASS_DEF_FOUND_ERROR: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")

private val NO_SUCH_FIELD_ERROR: VirtualType = VirtualType.of("Ljava/lang/NoSuchFieldError;")
