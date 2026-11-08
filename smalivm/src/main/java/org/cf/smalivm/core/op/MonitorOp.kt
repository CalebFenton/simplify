package org.cf.smalivm.core.op

import org.cf.smalivm.core.ExceptionPc
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.readsAsNullReference

/**
 * Shared behavior for `monitor-enter` and `monitor-exit`. Lock ownership is not modelled, so a feasible
 * operation continues and records `WEAK`. The operand is read as a reference, so a register that reads as
 * null throws a guest `NullPointerException`, and an unknown one keeps both outcomes.
 */
internal sealed class MonitorOp(
    address: Int,
    final override val name: String,
    private val receiverRegister: Int,
    private val exceptionPc: ExceptionPc,
) : Op(address) {
    final override val registersRead: Set<Int> = setOf(receiverRegister)

    final override fun execute(frame: Frame): OpExecution {
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())
        val proceed = OpArm.Continue(OpUpdate.effect(SideEffectLevel.WEAK))
        return if (receiver is Value.Known) OpExecution.single(proceed) else OpExecution.of(proceed, throwingNullReceiver())
    }

    final override fun toString(): String = "$name r$receiverRegister"

    private fun throwingNullReceiver(): OpArm.Throw = OpArm.Throw(
        evidence = ThrowEvidence.Fresh(GuestThrowables.nullPointer(GuestThrowables.NullOperand.MONITOR)),
        lookupPc = exceptionPc,
    )
}

internal class MonitorEnterOp(address: Int, receiverRegister: Int) :
    MonitorOp(address, "monitor-enter", receiverRegister, ExceptionPc.AT_INSTRUCTION)

internal class MonitorExitOp(address: Int, receiverRegister: Int) :
    MonitorOp(address, "monitor-exit", receiverRegister, ExceptionPc.AFTER_INSTRUCTION)
