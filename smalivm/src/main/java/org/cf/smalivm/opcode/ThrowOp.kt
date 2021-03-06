package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.opcode.ThrowOp
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class ThrowOp internal constructor(location: MethodLocation, private val register: Int) : MethodStateOp(location) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val item = mState.readRegister(register)
        mState.assignThrowRegister(item)
    }

    override fun toString(): String {
        return "$name r$register"
    }

    companion object {
        private val log = LoggerFactory.getLogger(ThrowOp::class.java.simpleName)
    }
}
