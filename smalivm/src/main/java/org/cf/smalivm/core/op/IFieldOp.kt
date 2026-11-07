package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.InstanceFieldLookup
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

private fun resolutionPreamble(frame: Frame, field: InstanceFieldLookup): OpExecution? = when (field) {
    is InstanceFieldLookup.NoSuchClass -> resolutionFailure(frame, field.type)
    is InstanceFieldLookup.NoSuchField -> noSuchFieldFailure(frame, field.descriptor)
    is InstanceFieldLookup.Resolved -> null
}

private fun nullReceiver(): ThrowableRecipe = ThrowableRecipe.of(NULL_POINTER_EXCEPTION)

private fun throwingNullReceiver(update: OpUpdate = OpUpdate.NONE): OpArm.Throw =
    OpArm.Throw(update, ThrowEvidence.Fresh(nullReceiver()))

/** Heap-backed instance-field read for all `iget*` forms. */
internal class IGetOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val receiverRegister: Int,
    private val field: InstanceFieldLookup,
) : Op(address) {
    override val registersRead: Set<Int> get() = setOf(receiverRegister)

    override val registersAssigned: Set<Int>
        get() = destinationRegisters(
            destRegister,
            isWide = (this.field as? InstanceFieldLookup.Resolved)?.type?.isWide == true,
        )

    override fun execute(frame: Frame): OpExecution {
        resolutionPreamble(frame, field)?.let { return it }
        val resolved = field as InstanceFieldLookup.Resolved
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())

        if (receiver !is Value.Known) {
            val update = OpUpdate.assign(destRegister, Value.Unknown(resolved.type)).let {
                if (resolved.isVolatile) it.then(OpUpdate.effect(SideEffectLevel.WEAK)) else it
            }
            return OpExecution.of(
                OpArm.Continue(update),
                throwingNullReceiver(),
            )
        }

        val ref = receiver.raw as? HeapRef
        val stored = ref?.let { frame.getField(it, resolved.descriptor) }
            ?.takeUnless { it === Value.Top }
            ?: Value.Unknown(resolved.type)
        val loaded = if (resolved.type.isReference && stored.readsAsNullReference) {
            Value.Known(null, resolved.type)
        } else {
            stored
        }
        val update = OpUpdate.assign(destRegister, loaded).let {
            if (resolved.isVolatile) it.then(OpUpdate.effect(SideEffectLevel.WEAK)) else it
        }
        return OpExecution.single(OpArm.Continue(update))
    }

    override fun toString(): String = "$name r$destRegister, r$receiverRegister, $field"
}

/**
 * Heap-backed instance-field write for all `iput*` forms. A successful tracked-cell write is `WEAK`.
 * An unknown receiver, a known non-[HeapRef], or a tracked object without the resolved cell has an
 * unmodelled write target and records `STRONG` while continuing conservatively. Those known malformed
 * rows are **VERIFIER-tier design choices under spec 2.2**, not runtime claims: no guest exception is
 * invented, but an unrecorded write must err upward (`Frame.recordSideEffect`'s contract).
 *
 * A `-boolean`/`-byte`/`-char`/`-short` store keeps only its field's width ([Coerce.narrowStore]).
 */
internal class IPutOp(
    address: Int,
    override val name: String,
    private val valueRegister: Int,
    private val receiverRegister: Int,
    private val field: InstanceFieldLookup,
    private val storesAReference: Boolean,
) : Op(address) {
    override val registersRead: Set<Int> get() = setOf(valueRegister, receiverRegister)
    override val registersAssigned: Set<Int> get() = emptySet()

    override fun execute(frame: Frame): OpExecution {
        resolutionPreamble(frame, field)?.let { return it }
        val resolved = field as InstanceFieldLookup.Resolved
        val value = frame.read(valueRegister)
        val receiver = frame.read(receiverRegister)
        if (receiver.readsAsNullReference) return OpExecution.single(throwingNullReceiver())

        if (receiver !is Value.Known) {
            return OpExecution.of(
                OpArm.Continue(OpUpdate.effect(SideEffectLevel.STRONG)),
                throwingNullReceiver(),
            )
        }

        val stored = if (storesAReference && value.readsAsNullReference) {
            Value.Known(null, resolved.type)
        } else {
            Coerce.narrowStore(value, resolved.type)
        }
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

private val NULL_POINTER_EXCEPTION = VirtualType.of("Ljava/lang/NullPointerException;")
