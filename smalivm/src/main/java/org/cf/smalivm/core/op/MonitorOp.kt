package org.cf.smalivm.core.op

import org.cf.smalivm.core.ExceptionPc
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * Shared behavior for `monitor-enter` and `monitor-exit`. Real ownership is outside the project scope.
 * A feasible operation therefore continues and records `WEAK`, while still reading its operand in a
 * reference context and throwing a guest NPE when that read is definitely null.
 *
 * An unknown operand keeps both feasible outcomes. `Known(5, "I")` takes only the null outcome because
 * [readsAsNullReference] applies the documented VERIFIER-tier spec 2.2 choice: switch reads a null reference,
 * nterp dereferences the raw value, and verified code cannot contain this input.
 */
internal sealed class MonitorOp(
    address: Int,
    final override val name: String,
    private val receiverRegister: Int,
    private val exceptionPc: ExceptionPc,
) : Op(address) {
    final override val registersRead: Set<Int> get() = setOf(receiverRegister)
    final override val registersAssigned: Set<Int> get() = emptySet()

    final override fun execute(frame: Frame): OpExecution {
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())

        val effect = OpUpdate.effect(SideEffectLevel.WEAK)
        return if (receiver is Value.Known) {
            OpExecution.single(OpArm.Continue(effect))
        } else {
            OpExecution.of(OpArm.Continue(effect), throwingNullReceiver())
        }
    }

    final override fun toString(): String = "$name r$receiverRegister"

    private fun throwingNullReceiver(): OpArm.Throw = OpArm.Throw(
        evidence = ThrowEvidence.Fresh(ThrowableRecipe.of(NULL_POINTER_EXCEPTION)),
        lookupPc = exceptionPc,
    )

    private companion object {
        val NULL_POINTER_EXCEPTION: VirtualType = VirtualType.of("Ljava/lang/NullPointerException;")
    }
}

internal class MonitorEnterOp(address: Int, receiverRegister: Int) :
    MonitorOp(address, "monitor-enter", receiverRegister, ExceptionPc.AT_INSTRUCTION)

internal class MonitorExitOp(address: Int, receiverRegister: Int) :
    MonitorOp(address, "monitor-exit", receiverRegister, ExceptionPc.AFTER_INSTRUCTION)
