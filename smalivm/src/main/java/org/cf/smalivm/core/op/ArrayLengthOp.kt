package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/** Reads an array's length; a null array throws ART's `"Attempt to get length of null array"`. */
internal class ArrayLengthOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val arrayRegister: Int,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(arrayRegister)
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution = when (val array = frame.readArrayOperand(arrayRegister)) {
        is ArrayOperand.Unprovable -> OpExecution.of(assigning(Value.Unknown(INT)), throwingNullArray())
        ArrayOperand.Null -> OpExecution.single(throwingNullArray())
        is ArrayOperand.NotTracked -> OpExecution.single(assigning(Value.Unknown(INT)))
        is ArrayOperand.Tracked -> OpExecution.single(assigning(Value.Known(array.array.length, INT)))
    }

    private fun assigning(value: Value): OpArm = OpArm.Continue(OpUpdate.assign(destRegister, value))

    private fun throwingNullArray(): OpArm =
        GuestThrowables.throwing(GuestThrowables.nullPointer(GuestThrowables.NullOperand.ARRAY_LENGTH))

    override fun toString(): String = "$name r$destRegister, r$arrayRegister"

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
