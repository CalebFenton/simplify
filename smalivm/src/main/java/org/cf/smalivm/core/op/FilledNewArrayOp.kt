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
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference
import org.cf.smalivm.core.resolutionFailure

/**
 * `filled-new-array` and `filled-new-array/range`: a fresh one-dimensional array of the instruction's type,
 * filled from the element registers, written to the result slot (so no register is assigned).
 *
 * `OpBuilder` refuses a non-array type and a `J`/`D` component. ART implements only `int` and reference
 * components and throws `InternalError` for `Z`/`B`/`C`/`S`/`F`. Nothing else is feasible: there is no array
 * operand to be null, no index, and ART stores reference elements without an assignability check. A fresh
 * allocation is modelled exactly, so no row records a side effect.
 */
internal class FilledNewArrayOp(
    address: Int,
    override val name: String,
    /** The element registers in instruction order, which is the order the elements land in the array. */
    private val elementRegisters: IntArray,
    private val arrayType: TypeOperand<Unit>,
    /** Decides only how [toString] spells the register list. */
    private val isRange: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> = elementRegisters.toSet()

    override fun execute(frame: Frame): OpExecution {
        val elements = elementRegisters.map { frame.read(it) }
        // ART resolves the type before it checks the component kind.
        if (arrayType is TypeOperand.Absent) return resolutionFailure(arrayType.failure)
        val type = arrayType.type
        val component = checkNotNull(type.componentType) { "OpBuilder decodes filled-new-array only for an array type, not $type" }
        if (component.isPrimitive && component != INT) {
            return OpExecution.single(GuestThrowables.throwing(GuestThrowables.internalError(notImplementedMessage(component))))
        }
        return OpExecution.single(
            OpArm.Continue(
                OpUpdate.allocateArray(AllocationTarget.ResultSlot, type, elements.map { stored(it, component) }),
            ),
        )
    }

    /**
     * One element as it lands in its slot, typed with the component. An `int` element is read by its 32 bits;
     * a reference element is read the way ART's `GetVRegReference` reads it, so a primitive register gives
     * `null`.
     */
    private fun stored(element: Value, component: VirtualType): Value {
        if (element !is Value.Known) return Value.Unknown(component)
        if (component != INT) return Value.Known(if (element.readsAsNullReference) null else element.raw, component)
        return Coerce.toInt(element.raw)?.let { Value.Known(it, component) } ?: Value.Unknown(component)
    }

    /** ART's `DoFilledNewArray` text, naming the component by `PrettyDescriptor`. */
    private fun notImplementedMessage(component: VirtualType): String {
        val pretty = when (component.descriptor) {
            "Z" -> "boolean"
            "B" -> "byte"
            "C" -> "char"
            "S" -> "short"
            "F" -> "float"
            else -> component.descriptor
        }
        return "Found type $pretty; filled-new-array not implemented for anything but 'int'"
    }

    override fun toString(): String {
        val registers = when {
            elementRegisters.isEmpty() -> ""
            isRange -> "r${elementRegisters.first()} .. r${elementRegisters.last()}"
            else -> elementRegisters.joinToString(", ") { "r$it" }
        }
        return "$name {$registers}, ${arrayType.type}"
    }

    private companion object {
        val INT: VirtualType = VirtualType.of("I")
    }
}
