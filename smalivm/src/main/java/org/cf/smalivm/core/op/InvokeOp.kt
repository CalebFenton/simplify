package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.InvokeKind
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution

/** One decoded member of the standard `invoke-*` family. Resolution belongs to the Invoker. */
internal class InvokeOp(
    address: Int,
    override val name: String,
    private val target: MethodSignature,
    private val argumentRegisters: IntArray,
    private val kind: InvokeKind,
) : Op(address) {
    override val registersRead: Set<Int> = argumentRegisters.toSet()

    override fun execute(frame: Frame): OpExecution =
        OpExecution.single(OpArm.Invoke(target, argumentRegisters, kind))

    override fun toString(): String =
        "$name {${argumentRegisters.joinToString(", ") { "v$it" }}}, $target"
}
