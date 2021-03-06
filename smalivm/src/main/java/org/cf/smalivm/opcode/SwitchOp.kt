package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class SwitchOp internal constructor(location: MethodLocation, private val child: MethodLocation, target: MethodLocation, private val register: Int) :
    MethodStateOp(location, target) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        // Use result register to store value to compare. Comparison is handled by payload op.
        val item = mState.readRegister(register)
        mState.assignResultRegister(item)

        // If switch "falls through", will need the immediate op after this.
        mState.setPseudoInstructionReturnLocation(child)
    }

    override fun toString(): String {
        return name + " r" + register + ", :addr_" + children[0].codeAddress
    }
}
