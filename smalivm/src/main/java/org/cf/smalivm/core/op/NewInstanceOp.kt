package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.classInitTrigger

/**
 * Allocates a fresh, **uninitialized** tracked object (spec §4.2). The result is
 * `Known(HeapRef, instanceType)` -- never a live host instance, which is what would let two forked
 * siblings mutate one object -- and the heap object carries resolved decision 8's uninitialized flag,
 * SET here ([org.cf.smalivm.core.HeapStore.allocateObject]).
 *
 * **The flag's CLEARING is deliberately absent from this build.** Decision 8 clears it when an
 * `<init>` call whose receiver is this ref returns, which is observable only through `invoke-direct`
 * -- Task 20's opcode -- so the clearing (and adjudication 9's collapse-what-a-reference-points-to
 * rule for opaque framework constructors) lands there, where it can be driven and tested. Nothing in
 * Task 17's corpus calls `<init>`: both migrated fixtures are `new-instance` + `return-void`.
 *
 * **`new-instance` triggers its class's initialization** -- measured, not inherited:
 * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated
 * `[DEVICE](Tier B, nterp default, API 36)`, fixture `Fx_NewInstanceDoesInitialize` (Task 16 Step 0).
 * The protocol is Task 16's two-pass one (`ClassInitTest.InitClassStub` is the canonical model): ask
 * [classInitTrigger] and return its explicit action arm; the scheduler runs the `<clinit>` prelude --
 * including decision 7's framework-conservative marking, where `new-instance Ljava/lang/Integer;`
 * marks without running -- and resumes at THIS address, so the second pass sees the class past
 * `UNINITIALIZED` and allocates. An op that skipped the action query would re-execute forever
 * (`ExecutionSession.planInitClass` treats a no-op plan as a loud invariant).
 *
 * **An absent class throws a guest `NoClassDefFoundError`** -- Task 15's resolution-failure family,
 * which the plan's Task 15 Step 4 explicitly deferred `new-instance` into this task, and measured on
 * both tiers by `Fx_NewInstanceAbsentType` (Task 16 Step 0). [loadable] is decided at DECODE time in
 * `OpBuilder`, exactly as `new-array`'s is, because loadability is a static fact about the
 * instruction's type reference and `core/op/` may not reach the class model (the two-tier dependency
 * rule). The check comes BEFORE the class-init ask, necessarily: an absent class cannot be
 * initialized, and asking would send `ClassInitPlanner` walking `superclassOf` on it, which raises
 * `ClassLoadFailure` and aborts the path instead of throwing the guest error.
 *
 * Side effects: NONE, and `recordSideEffect` is called nowhere in this file -- the
 * [FilledNewArrayOp]/[ThrowOp] precedent: a fresh allocation is modelled exactly, so there is nothing
 * unaccounted for. The STRONG a skipped framework `<clinit>` costs is the SCHEDULER's declaration
 * (`ExecutionSession.declareSkippedInitializerEffect`), not this op's.
 */
internal class NewInstanceOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val instanceType: VirtualType,
    private val loadable: Boolean,
    private val fields: Map<String, Value>,
) : Op(address) {

    // A reference result is narrow, so isWide is false -- stated at decode time per
    // Op.destinationRegisters' no-default rule rather than inferred from the type.
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        if (!loadable) {
            // NoClassDefFoundError, NOT ClassNotFoundException -- the family's shared ruling (design
            // decision 4, [org.cf.smalivm.core.op.NewArrayOp]'s KDoc has the argument) with new-instance's
            // own row measured: Fx_NewInstanceAbsentType -> NoClassDefFoundError on both interpreter
            // tiers. It is an Error, so `catch (Exception)` cannot catch it. The MESSAGE is the binary
            // base name, NewArrayOp's shape -- ours to pick (no migrated expectation constrains it, and
            // an exception message is never evidence of Dalvik fidelity), asserted so it cannot drift.
            return OpExecution.single(
                OpArm.Throw(evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NO_CLASS_DEF_FOUND, binaryBaseName()))),
            )
        }
        frame.classInitTrigger(instanceType)?.let {
            // First pass: initialization is a prelude to this instruction. The scheduler resumes at this
            // op's own address, so the allocation below is the SECOND pass's work -- which is why the
            // node pile at a triggering address holds one extra node (see `planInitClass`'s KDoc; the
            // migrated tests read terminating consensus, which the extra node cannot perturb).
            return it
        }
        return OpExecution.single(
            OpArm.Continue(OpUpdate.allocateObject(destRegister, instanceType, fields)),
        )
    }

    /** The guest error for an unresolvable class; see [NewArrayOp.unresolvedError], whose shape this is. */
    /**
     * `Lfoo/Bar;` -> `foo.Bar`. No component-unwrapping loop like [NewArrayOp]'s, because this operand
     * is a class reference, not an array one; were a verifier-invalid `new-instance [I` ever decoded,
     * the strips below are no-ops and the message is the descriptor -- harmless, and a message is
     * never evidence of fidelity anyway.
     */
    private fun binaryBaseName(): String =
        instanceType.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')

    override fun toString(): String = "$name r$destRegister, $instanceType"

    private companion object {
        val NO_CLASS_DEF_FOUND: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
    }
}
