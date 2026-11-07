package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.AllocationTarget
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * Allocates a tracked array (spec §4.2). The result is `Known(HeapRef, arrayType)` -- never a live host
 * array, which is what would let two forked siblings mutate one object.
 *
 * [loadable] is decided at DECODE time, in `OpBuilder`, because it is a static fact about the
 * instruction's type reference and because `core/op/` may not reach the class model (the two-tier
 * dependency rule). See the plan's design decision 4 for why an unloadable component throws definitely
 * rather than forking.
 */
internal class NewArrayOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val lengthRegister: Int,
    private val arrayType: VirtualType,
    private val loadable: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(lengthRegister)
    // A reference result is narrow, so isWide is false -- stated at decode time per
    // Op.destinationRegisters' no-default rule rather than inferred from the type.
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        // `toProvableInt`, NOT `Coerce.toInt`, and this is the one line in the op that had to be fixed rather
        // than written: `toInt` truncates BY VALUE, so a length register holding `Known(-0.5f, "F")` answered
        // a definite **0**, the `length < 0` row below was skipped, and this op allocated a fully `Known`
        // ZERO-LENGTH array and reported `Complete` -- where a device reads the register's 32 bits
        // (`0xBF000000`, i.e. -1090519040) and throws NegativeArraySizeException. VERIFIER-tier input, so
        // spec §2.2 decides it and "not provable" is the answer; `Coerce.toProvableInt`'s KDoc carries the
        // tiering and names the other three sites that ask the same question.
        //
        // A refused length lands on the `length == null` row below, i.e. it behaves EXACTLY like an unknown
        // length -- which deliberately does not fork the NegativeArraySizeException. So this removes an
        // invented constant; it does not add a path. That dropped throw is a filed, pinned liability
        // (`NewArrayOpTest.anUnknownLengthYieldsAnUnknownArrayWithoutForking`), not something this line
        // decides.
        val length = Coerce.toProvableInt(readKnownLength(frame))
        // LENGTH before class. This inverts TWO orders and only one of them is the oracle's:
        //  - Utils.buildArray resolves then calls Array.newInstance, so the oracle checks the class first;
        //  - [JVMS] 6.5 anewarray puts its "Linking Exceptions" block BEFORE its "Run-time Exceptions:
        //    Otherwise, if count is less than zero" block, so the INHERITED order also resolves first.
        // ART disobeys the inherited order: CheckArrayAlloc
        // (runtime/entrypoints/entrypoint_utils-inl.h:292-322) tests `component_count < 0` before
        // GetResolvedType/ResolveType. We follow ART, because no device runs the JVMS order, and this is
        // therefore a LABELLED DIVERGENCE from an inherited rule -- not a free pick between two blessed
        // orders. Its observable consequence is which catch clause is reachable on a both-bad new-array.
        // See the plan's design decision 4 and docs/DALVIK-SEMANTICS.md#new-array-op-23-22c.
        //
        // TIER-DIVERGENCE(nterp): nterp resolves the CLASS first and answers NoClassDefFoundError, where the
        // switch interpreter answers NegativeArraySizeException. Measured Fx_NewArrayAbsentTypeNegativeLength.
        // We model -Xint (see DALVIK-SEMANTICS.md "THE MODELLED TIER"). Consequence: on a both-bad new-array
        // this decides which guest catch clause is reachable, so a handler this engine reports as live is dead
        // under a device's default interpreter, and vice versa.
        //
        // 🔴 THE TIER ON THAT CLAIM CHANGED, AND THE ORDER IS NOT UNIVERSAL (Plan 9 Task 15 fix round,
        // 2026-08-05). The source reading above was measured, and it is CONFIRMED for the switch interpreter
        // and CONTRADICTED for nterp -- Fx_NewArrayAbsentTypeNegativeLength.smali, one instruction with both
        // conditions:
        //     [DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2) -> NegativeArraySizeException("-1")
        //     [DEVICE](Tier B, -Xint switch, API 36)                   -> NegativeArraySizeException("-1")
        //     [DEVICE](Tier B, nterp default, API 36)                  -> NoClassDefFoundError
        // An interpreter-TIER split, not a version one (both -Xint runs agree across the whole version gap),
        // and the first semantic nterp-vs-switch divergence measured here. nterp -- what actually runs on a
        // device -- resolves the class FIRST, i.e. it does not route through CheckArrayAlloc.
        //
        // The lines below are UNCHANGED and still implement the switch order. Whether this engine should model
        // nterp instead is OPEN and larger than one opcode (the standing "which tier does the engine model?"
        // question); flipping it would move a pinned migrated expectation
        // (NewArrayOpTest.aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst) and a
        // registered differential entry (createBothBadArrayClass()V). Surfaced for the maintainer, recorded in
        // docs/DALVIK-SEMANTICS.md's new-array and resolution-failure sections and in the plan's adjudication
        // 7b(b). DO NOT re-cite the source reading above without naming the tier it holds for.
        if (length != null && length < 0) {
            // The MESSAGE is the size in decimal, and it is [ART]-tier rather than spec: neither [BC] nor
            // [JVMS] specifies any message for this exception. ART's `CheckArrayAlloc` calls the `int`
            // overload of `ThrowNegativeArraySizeException`, which formats `StringPrintf("%d", size)`
            // (runtime/common_throws.cc:389-392). Both quotes are in docs/DALVIK-SEMANTICS.md's new-array
            // section, which is this repo's verified-quote registry -- a citation not backed there is a
            // citation nobody has checked.
            return OpExecution.single(
                OpArm.Throw(evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NEGATIVE_ARRAY_SIZE, length.toString()))),
            )
        }
        if (!loadable) {
            // NoClassDefFoundError, NOT ClassNotFoundException. INHERITED from JVMS 6.5 anewarray, whose
            // resolution failures are JVMS 5.4.3.1 linking errors. The oracle raises CNFE only because
            // Utils.buildArray propagates the HOST loader's exception. It is an Error, so `catch (Exception)`
            // -- which obfuscators emit constantly -- cannot catch it, and getting that wrong makes the
            // engine explore a handler the runtime never enters. Design decision 4 has the argument.
            return OpExecution.single(
                OpArm.Throw(evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NO_CLASS_DEF_FOUND, binaryBaseName()))),
            )
        }
        if (length == null) {
            // Nothing known about the length, so nothing known about the array. Deliberately does NOT fork
            // NegativeArraySizeException -- see the plan's Task 6 table for why, and NewArrayOpTest's
            // anUnknownLengthYieldsAnUnknownArrayWithoutForking, which pins the choice.
            //
            // TWO inputs arrive here and they are not the same kind of ignorance: an Unknown/Top length
            // register, and a `Known` one whose raw `Coerce.toProvableInt` refuses (a Float/Double). The
            // second is `Known`, so the bits ARE available -- the engine declines to reinterpret them,
            // because doing so would reopen every migrated arithmetic expectation for verifier-rejected input
            // (the rejected-but-available `toRawInt32`, recorded in CARRY-FORWARD.md). Both are therefore
            // "nothing provable about the length", and answering them differently would be a second answer to
            // one question. `NewArrayOpTest.aFloatLengthIsNotProvableRatherThanTruncatedToZero` pins the
            // second input on this row.
            return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(arrayType))))
        }
        // NOTE: a second negative-length check here would be UNREACHABLE -- the check at the top already
        // returned for every `length != null && length < 0` -- so there is not one. A dead branch that
        // looks like the live one is how a later edit "fixes" the wrong copy.
        return OpExecution.single(
            OpArm.Continue(
                OpUpdate.allocateArray(
                    AllocationTarget.Register(destRegister),
                    arrayType,
                    length,
                ),
            ),
        )
    }

    /** The length's raw when it is provable, else null -- `Unknown`/`Top` both answer "not provable". */
    private fun readKnownLength(frame: Frame): Any? = (frame.read(lengthRegister) as? Value.Known)?.raw

    /**
     * The guest error for an unresolvable component type. The MESSAGE is ours to pick, because the
     * exception type is a deliberate divergence from the oracle, so no migrated expectation constrains it
     * -- and per docs/DALVIK-SEMANTICS.md an exception message is never evidence of Dalvik fidelity anyway
     * (ART changed these texts between runtimes). ART's shape is `"Failed resolution of: %s"` with the
     * DESCRIPTOR; the binary base name below is the oracle's shape and is kept only so the migrated test
     * reads familiarly. Either is defensible; pick one and assert it.
     */
    /** `[[Lfoo/Bar;` -> `foo.Bar`. */
    private fun binaryBaseName(): String {
        var base = arrayType
        while (base.isArray) base = base.componentType!!
        return base.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
    }

    override fun toString(): String = "$name r$destRegister, r$lengthRegister, $arrayType"

    private companion object {
        val NO_CLASS_DEF_FOUND: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
        val NEGATIVE_ARRAY_SIZE: VirtualType = VirtualType.of("Ljava/lang/NegativeArraySizeException;")
    }
}
