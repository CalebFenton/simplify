package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * Loads a constant into a register. The value *and* its declared type are decoded once, at build time,
 * by `OpBuilder` -- which is also where the type tags that must match the old engine's live. For every
 * `const*` form but one this op is value-agnostic: it assigns whatever [Value] the decoder handed it and
 * falls through, so the same class serves them all.
 *
 * **`const-class` is the one, and it is the only form with a throw row** (Plan 8 Task 10; the throw is
 * Plan 9 Task 15): `OpBuilder.buildConstClass` resolves the type operand at decode time into
 * `Known(hostClass, "Ljava/lang/Class;")` -- a HOST `Class` object, not a [HeapRef], because a `Class` is
 * immutable and [org.cf.smalivm.core.Value]'s rule only forces *mutable* VM-tracked objects onto the heap
 * (decision 2). It is resolved WITHOUT initializing the class (decision 3), so this op is faithful in
 * reporting no effect.
 *
 * **An operand naming a class absent from the loaded set throws a guest `NoClassDefFoundError` at execute
 * time** -- see [resolutionFailure] for the family, the tiering and why `Error`-not-`Exception` is the
 * whole point. It arrives as [unresolvedClassOperand], a nullable **type** rather than a `loadable`
 * boolean, on purpose: only `const-class` can be unresolvable, so a flag would need a default that every
 * other `const*` form silently inherits, and the type is also what the message is built from. Non-null
 * means *"this is a `const-class` whose operand is absent"* and nothing else can say it.
 *
 * **Side effects: NONE; `recordSideEffect` is called nowhere in this file.** A `const*` writes one register
 * (covered by [registersAssigned]) and touches nothing this engine does not own -- and for `const-class`,
 * *not even a class initialisation*, because ART's whole `CONST_CLASS` handler body is a resolve, a `nullptr`
 * check and a `SetVRegReference` (`runtime/interpreter/interpreter_switch_impl-inl.h:755-767`;
 * `docs/DALVIK-SEMANTICS.md#const-class-21c`). ⚠️ **NOT because ART passes `can_run_clinit = false`, which is
 * what this KDoc used to say:** `NEW_INSTANCE` (`:862-894`) passes the same `false` at the same argument
 * position and initialises the class downstream in `AllocObjectFromCode`, so the flag proves nothing about an
 * opcode. (The initializing load is oracle Correctness Defect 21,
 * `docs/architecture/opcodes.md:176`.) Declared rather than left implicit because a spurious `STRONG` would
 * silently stop every constant fold in any method containing a `const*`
 * (`ExecutionGraph.methodSideEffectLevelOverExploredPaths` maxes over addresses). Pinned by
 * `core/op/ConstOpTest.aConstClassLoadReportsNoSideEffect`.
 */
internal class ConstOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val value: Value,
    private val unresolvedClassOperand: VirtualType? = null,
) : Op(address) {
    // A `const-wide*` literal occupies TWO registers, so both halves are assigned -- see
    // Op.destinationRegisters for why naming only the low one is a silent unsoundness rather than
    // an imprecision. The width comes from the literal's own decoded type, which is the same fact
    // Frame.assign uses to reserve the high half.
    //
    // STATIC, so it still answers `{destRegister}` on the throwing const-class row, where nothing is
    // written. That is the conservative direction (a register that MIGHT be written is not offered as
    // scratch space) and the same one-sided disagreement `core/op/NewArrayOp`'s throwing rows carry --
    // `core/op/NewArrayOpTest.nonExistentClassNameThrowsException` asserts `setOf(0)` there and records
    // why. The declared width is the resolvable case's: a `Class` reference is narrow.
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, value.type?.isWide == true)

    override fun execute(frame: Frame): OpExecution {
        // BEFORE the assign, and for `const-class` there is no register to read at all, so this is the
        // first thing the op does. A guest `NoClassDefFoundError` -- an Error, so a `catch (Exception)`
        // cannot catch it. [resolutionFailure] carries the tiering and the argument.
        if (unresolvedClassOperand != null) return resolutionFailure(frame, unresolvedClassOperand)
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, value)))
    }
}
