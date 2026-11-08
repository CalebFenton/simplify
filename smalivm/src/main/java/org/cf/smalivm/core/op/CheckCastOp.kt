package org.cf.smalivm.core.op

import org.cf.smalivm.core.CastOutcome
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.FrameValue
import org.cf.smalivm.core.GuestThrowables
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.HierarchySnapshot
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.castOutcome
import org.cf.smalivm.core.partitionThrowableDomain
import org.cf.smalivm.core.readsAsNullReference
import org.cf.smalivm.core.resolutionFailure
import org.cf.smalivm.core.runtimeTypeOf

/**
 * `check-cast`: throws `ClassCastException` when the register's reference is not an instance of the type
 * operand, and narrows the register's declared type on success. The semantics are [castOutcome]'s; this op
 * maps its outcomes.
 *
 * ART resolves the type operand before reading the register, so an absent type throws even for a null
 * register. At runtime the cast writes nothing; the narrowing mirrors the verifier's re-typing of the
 * register on the success path, which is why [registersAssigned] names it.
 */
internal class CheckCastOp(
    address: Int,
    override val name: String,
    private val targetRegister: Int,
    private val castType: TypeOperand<Unit>,
    private val hierarchy: HierarchySnapshot,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(targetRegister)
    override val registersAssigned: Set<Int> = destinationRegisters(targetRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        if (castType is TypeOperand.Absent) return resolutionFailure(castType.type)
        val target = castType.type
        val frameValue = frame.readFrameValue(targetRegister)
        val value = frameValue.value
        val narrowed = narrowedOrUnchanged(value, target)
        if (frameValue is FrameValue.RefinedThrowable) return castRefinedThrowable(frameValue, narrowed, target)
        val heapObjectTypeOf = { ref: HeapRef -> frame.heapObject(ref)?.type }
        return when (castOutcome(value, target, hierarchy, heapObjectTypeOf)) {
            // Null passes every cast, and every content of a satisfying tag passes too.
            CastOutcome.ReadsAsNull, CastOutcome.Definitely, CastOutcome.NullOrInstance ->
                OpExecution.single(OpArm.Continue(OpUpdate.assign(targetRegister, narrowed)))
            CastOutcome.Never -> OpExecution.single(GuestThrowables.throwing(classCast(value, target, heapObjectTypeOf)))
            // The success arm carries the narrowing; the throw arm leaves the register as it was.
            CastOutcome.Unprovable -> OpExecution.of(
                OpArm.Continue(OpUpdate.assign(targetRegister, narrowed)),
                GuestThrowables.throwing(classCast(value, target, heapObjectTypeOf)),
            )
        }
    }

    /** Partitions one carried throwable domain into the part that passes and the part that throws. */
    private fun castRefinedThrowable(source: FrameValue.RefinedThrowable, narrowed: Value, target: VirtualType): OpExecution {
        if (!target.isReference) {
            return OpExecution.single(
                GuestThrowables.throwing(classCast(source), OpUpdate.assign(targetRegister, source)),
            )
        }
        val partition = partitionThrowableDomain(source.domain, target, hierarchy)
        val arms = buildList<OpArm> {
            partition.matching?.let { add(OpArm.Continue(OpUpdate.assign(targetRegister, FrameValue.refined(narrowed, it)))) }
            partition.residual?.let {
                add(
                    GuestThrowables.throwing(
                        classCast(source),
                        OpUpdate.assign(targetRegister, FrameValue.refined(source.value, it)),
                    ),
                )
            }
        }
        check(arms.isNotEmpty()) { "A non-empty throwable domain produced no cast arm" }
        return OpExecution.of(arms)
    }

    /**
     * The register after a successful cast: the raw unchanged and the declared type replaced. A re-tag that
     * would stop the register reading as a null reference is not applied: `Known(5, "I")` passes the cast
     * because it reads as null, and re-tagged as a reference it would no longer.
     */
    private fun narrowedOrUnchanged(value: Value, target: VirtualType): Value {
        val narrowed = if (value is Value.Known) Value.Known(value.raw, target) else Value.Unknown(target)
        return if (value.readsAsNullReference && !narrowed.readsAsNullReference) value else narrowed
    }

    /** ART names the object's runtime class, so a value whose class the engine cannot determine gets no message. */
    private fun classCast(value: Value, target: VirtualType, heapObjectTypeOf: (HeapRef) -> VirtualType?): ThrowableRecipe =
        GuestThrowables.classCast((value as? Value.Known)?.let { runtimeTypeOf(it, heapObjectTypeOf) }, target)

    private fun classCast(source: FrameValue.RefinedThrowable): ThrowableRecipe =
        GuestThrowables.classCast(source.domain.exactRuntimeType, castType.type)

    override fun toString(): String = "$name r$targetRegister, ${castType.type}"
}
