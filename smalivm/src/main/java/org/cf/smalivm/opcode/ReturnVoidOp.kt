package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class ReturnVoidOp internal constructor(location: MethodLocation) : MethodStateOp(location) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        //
    }

    override fun toString(): String {
        return name
    }

    companion object {
        private val log = LoggerFactory.getLogger(ReturnVoidOp::class.java.simpleName)
    }
}
