package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class MonitorEnterOp internal constructor(location: MethodLocation, child: MethodLocation?, private val destRegister: Int) :
    MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {}
    override fun toString(): String {
        return "$name r$destRegister"
    }
}
