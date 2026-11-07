package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution

/**
 * Unconditional jump. The target is resolved from the label by `OpBuilder` at build time and never
 * recomputed, so no consumer can mistake a raw code *offset* for an address.
 */
internal class GotoOp(
    address: Int,
    override val name: String,
    private val targetAddress: Int,
) : Op(address) {
    override fun execute(frame: Frame): OpExecution = OpExecution.single(OpArm.Jump(targetAddress))
}
