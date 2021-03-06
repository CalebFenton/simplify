package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class GotoOp internal constructor(location: MethodLocation, childInstruction: MethodLocation?) : MethodStateOp(location, childInstruction) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        // https://xkcd.com/292/
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        val childAddress = children[0].codeAddress
        sb.append(" :addr_").append(childAddress)
        return sb.toString()
    }
}
