package org.cf.smalivm.core.op

import org.cf.smalivm.core.AllocationTarget
import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value

/**
 * Allocates a tracked array. The result is `Known(HeapRef, arrayType)`, or `Unknown(arrayType)` when the
 * length is unprovable.
 *
 * The size is checked before the component class, as ART's switch interpreter does (`CheckArrayAlloc`); nterp
 * resolves the class first. The order decides which guest handler is reachable when both checks fail.
 */
internal class NewArrayOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val lengthRegister: Int,
    private val arrayType: TypeOperand<Unit>,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(lengthRegister)
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        val length = (frame.read(lengthRegister) as? Value.Known)?.let { Coerce.toInt(it.raw) }
        val type = arrayType.type
        if (length == null) {
            // An unprovable length may be negative, so NegativeArraySizeException is feasible too.
            val nonNegative = when (arrayType) {
                is TypeOperand.Present -> OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(type)))
                is TypeOperand.Absent -> GuestThrowables.throwing(arrayType.failure)
            }
            return OpExecution.of(nonNegative, GuestThrowables.throwing(GuestThrowables.negativeArraySize(null)))
        }
        if (length < 0) return OpExecution.single(GuestThrowables.throwing(GuestThrowables.negativeArraySize(length)))
        return when (arrayType) {
            is TypeOperand.Present -> OpExecution.single(
                OpArm.Continue(OpUpdate.allocateArray(AllocationTarget.Register(destRegister), type, length)),
            )
            is TypeOperand.Absent -> OpExecution.single(GuestThrowables.throwing(arrayType.failure))
        }
    }

    override fun toString(): String = "$name r$destRegister, r$lengthRegister, ${arrayType.type}"
}
