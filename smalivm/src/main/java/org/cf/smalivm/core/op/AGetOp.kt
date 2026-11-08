package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * Reads one element of a tracked array: all seven `aget*` opcodes. The element's value and type come from
 * the array object, so the suffix decides only whether the destination is a wide pair.
 *
 * `NullPointerException` precedes `ArrayIndexOutOfBoundsException` ([JVMS] §6.5 `iaload`). On unverified
 * input `aget-wide` off a narrow array has no defined device behavior; the element is read as it is.
 *
 * The static [registersAssigned] follows the opcode, so `aget-wide` off a narrow array claims a pair the
 * frame does not reserve; that errs toward offering one scratch register fewer.
 */
internal class AGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val arrayRegister: Int,
    private val indexRegister: Int,
    isWide: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(arrayRegister, indexRegister)
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide)

    override fun execute(frame: Frame): OpExecution = when (val array = frame.readArrayOperand(arrayRegister)) {
        is ArrayOperand.Unprovable -> OpExecution.of(
            OpArm.Continue(OpUpdate.assign(destRegister, unknownElementOf(array.tag))),
            GuestThrowables.throwing(NULL_ARRAY),
            GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(null, null)),
        )
        ArrayOperand.Null -> OpExecution.single(GuestThrowables.throwing(NULL_ARRAY))
        is ArrayOperand.NotTracked ->
            OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, unknownElementOf(array.tag))))
        is ArrayOperand.Tracked -> when (val index = array.array.indexOf(frame.read(indexRegister))) {
            ArrayIndex.Unprovable -> OpExecution.of(
                OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(array.component))),
                GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(array.array.length, null)),
            )
            is ArrayIndex.OutOfBounds -> OpExecution.single(
                GuestThrowables.throwing(GuestThrowables.arrayIndexOutOfBounds(array.array.length, index.index)),
            )
            is ArrayIndex.InBounds ->
                OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, array.array.element(index.index))))
        }
    }

    /**
     * The element type when there is no array object to ask: the tag's component, or the tag itself when it
     * is not an array descriptor. `Top` carries no type, so its element is `Top`.
     */
    private fun unknownElementOf(tag: VirtualType?): Value =
        if (tag == null) Value.Top else Value.Unknown(tag.componentType ?: tag)

    override fun toString(): String = "$name r$destRegister, r$arrayRegister, r$indexRegister"

    private companion object {
        val NULL_ARRAY = GuestThrowables.nullPointer(GuestThrowables.NullOperand.ARRAY_READ)
    }
}
