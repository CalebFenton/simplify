package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution

/** Does nothing and falls through. Also the padding before a switch/array payload. */
internal class NopOp(address: Int) : Op(address) {
    override val name: String = "nop"
    override fun execute(frame: Frame): OpExecution = OpExecution.single(OpArm.Continue())
}
