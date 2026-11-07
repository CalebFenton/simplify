package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution

/**
 * Ends the method. `return-void` carries no value ([register] is null); the others carry the
 * register's value, which the session records as [org.cf.smalivm.core.Outcome.Returned]. Which of
 * the two shapes this is was decided at build time, so nothing here needs the opcode.
 */
internal class ReturnOp(
    address: Int,
    override val name: String,
    private val register: Int?,
) : Op(address) {
    override val registersRead: Set<Int> get() = setOfNotNull(register)

    override fun execute(frame: Frame): OpExecution =
        OpExecution.single(OpArm.Return(register?.let { frame.readFrameValue(it) }))
}
