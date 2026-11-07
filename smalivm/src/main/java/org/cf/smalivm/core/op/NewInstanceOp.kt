package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.classInitTrigger
import org.cf.smalivm.core.resolutionFailure

/**
 * Allocates a fresh, uninitialized tracked object, after triggering its class's initialization. The trigger
 * is a prelude the scheduler runs before resuming at this address, so the second pass allocates. An absent
 * class throws before the trigger, because an absent class cannot be initialized.
 *
 * The present operand carries the instance-field defaults the object starts with.
 */
internal class NewInstanceOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val instance: TypeOperand<Map<String, Value>>,
) : Op(address) {

    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution = when (instance) {
        is TypeOperand.Absent -> resolutionFailure(instance.type)
        is TypeOperand.Present -> frame.classInitTrigger(instance.type)
            ?: OpExecution.single(OpArm.Continue(OpUpdate.allocateObject(destRegister, instance.type, instance.resolved)))
    }

    override fun toString(): String = "$name r$destRegister, ${instance.type}"
}
