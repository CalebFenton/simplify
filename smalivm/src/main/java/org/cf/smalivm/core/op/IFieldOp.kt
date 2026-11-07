package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.InstanceFieldLookup
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.noSuchFieldFailure
import org.cf.smalivm.core.readsAsNullReference
import org.cf.smalivm.core.resolutionFailure

/** The resolution failure to throw instead of the access, or null when the field resolved. */
private fun resolutionPreamble(field: InstanceFieldLookup): OpExecution? = when (field) {
    is InstanceFieldLookup.NoSuchClass -> resolutionFailure(field.type)
    is InstanceFieldLookup.NoSuchField -> noSuchFieldFailure(field.descriptor)
    is InstanceFieldLookup.Resolved -> null
}

private fun throwingNullReceiver(): OpArm.Throw = GuestThrowables.throwing(GuestThrowables.nullPointer())

/** Heap-backed instance-field read for all `iget*` forms. */
internal class IGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val receiverRegister: Int,
    private val field: InstanceFieldLookup,
) : Op(address) {
    override val registersRead: Set<Int> = setOf(receiverRegister)

    /** An unresolved field throws before assigning, so it declares the narrow set. */
    override val registersAssigned: Set<Int> =
        destinationRegisters(destRegister, isWide = (field as? InstanceFieldLookup.Resolved)?.type?.isWide == true)

    override fun execute(frame: Frame): OpExecution {
        resolutionPreamble(field)?.let { return it }
        val resolved = field as InstanceFieldLookup.Resolved
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())
        if (receiver !is Value.Known) {
            return OpExecution.of(
                OpArm.Continue(fieldRead(destRegister, Value.Unknown(resolved.type), resolved.type, resolved.isVolatile)),
                throwingNullReceiver(),
            )
        }
        // `getField` answers `Top` for an object with no such cell.
        val cell = (receiver.raw as? HeapRef)?.let { frame.getField(it, resolved.descriptor) }
            ?.takeUnless { it === Value.Top }
            ?: Value.Unknown(resolved.type)
        return OpExecution.single(OpArm.Continue(fieldRead(destRegister, cell, resolved.type, resolved.isVolatile)))
    }

    override fun toString(): String = "$name r$destRegister, r$receiverRegister, $field"
}

/**
 * Heap-backed instance-field write for all `iput*` forms. A write into a tracked cell records `WEAK`. A write
 * whose target the engine cannot name (an unknown receiver, a non-heap reference, an object without the
 * cell) records `STRONG` and continues, inventing no guest exception for verifier-rejected input.
 */
internal class IPutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val receiverRegister: Int,
    private val field: InstanceFieldLookup,
    private val storesAReference: Boolean,
) : Op(address) {
    override val registersRead: Set<Int> = setOf(valueRegister, receiverRegister)

    override fun execute(frame: Frame): OpExecution {
        resolutionPreamble(field)?.let { return it }
        val resolved = field as InstanceFieldLookup.Resolved
        val value = frame.read(valueRegister)
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())
        if (receiver !is Value.Known) {
            return OpExecution.of(OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)), throwingNullReceiver())
        }
        val stored = fieldWrite(value, resolved.type, storesAReference)
        val ref = receiver.raw as? HeapRef
        val update = if (ref != null && frame.getField(ref, resolved.descriptor) !== Value.Top) {
            OpUpdate.writeField(ref, resolved.descriptor, stored).then(OpUpdate.effect(SideEffectLevel.WEAK))
        } else {
            OpUpdate.effect(SideEffectLevel.STRONG)
        }
        return OpExecution.single(OpArm.Continue(update))
    }

    override fun toString(): String = "$name r$valueRegister, r$receiverRegister, $field"
}
