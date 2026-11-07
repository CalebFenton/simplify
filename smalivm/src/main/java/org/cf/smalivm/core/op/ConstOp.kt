package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.resolutionFailure

/** Loads a literal decoded at build time: the `const*` number forms and `const-string`. */
internal class ConstOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val value: Value,
) : Op(address) {
    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, value.type?.isWide == true)

    override fun execute(frame: Frame): OpExecution =
        OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, value)))
}

/**
 * `const-class`: the operand resolved at decode time to a host `Class`, without initializing it, as ART's
 * `CONST_CLASS` handler does. A host `Class` is immutable, so the raw is the object itself rather than a
 * heap reference.
 */
internal class ConstClassOp(
    address: Int,
    private val destRegister: Int,
    private val operand: TypeOperand<Class<*>>,
) : Op(address) {
    override val name: String get() = "const-class"

    override val registersAssigned: Set<Int> = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution = when (operand) {
        is TypeOperand.Absent -> resolutionFailure(operand.type)
        is TypeOperand.Present ->
            OpExecution.single(OpArm.Continue(OpUpdate.assign(destRegister, Value.Known(operand.resolved, CLASS))))
    }

    private companion object {
        val CLASS: VirtualType = VirtualType.of("Ljava/lang/Class;")
    }
}
