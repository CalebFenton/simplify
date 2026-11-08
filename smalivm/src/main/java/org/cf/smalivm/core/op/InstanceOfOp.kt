package org.cf.smalivm.core.op

import org.cf.smalivm.core.CastOutcome
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.TypeHierarchy
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.castOutcome
import org.cf.smalivm.core.resolutionFailure

/**
 * `instance-of`: stores whether the source register holds an instance of the type operand. It decides a
 * value, never a path, so it never forks. ART resolves the type operand before reading the register, so an
 * absent type throws even for a null source.
 */
internal class InstanceOfOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val sourceRegister: Int,
    private val targetType: TypeOperand<Unit>,
    private val hierarchy: TypeHierarchy,
) : Op(address) {

    override val registersRead: Set<Int> = setOf(sourceRegister)
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        if (targetType is TypeOperand.Absent) return resolutionFailure(targetType.failure)
        val source = frame.read(sourceRegister)
        val result = when (castOutcome(source, targetType.type, hierarchy) { frame.heapObject(it)?.type }) {
            // Null is an instance of nothing.
            CastOutcome.ReadsAsNull, CastOutcome.Never -> Value.Known(false, BOOL)
            CastOutcome.Definitely -> Value.Known(true, BOOL)
            // An unknown may be null, so even a satisfying tag does not decide the answer.
            CastOutcome.NullOrInstance, CastOutcome.Unprovable -> Value.Unknown(BOOL)
        }
        return OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, result)))
    }

    override fun toString(): String = "$name r$destRegister, r$sourceRegister, ${targetType.type}"

    private companion object {
        val BOOL: VirtualType = VirtualType.of("Z")
    }
}
