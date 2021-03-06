package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class FillArrayDataOp internal constructor(
    location: MethodLocation,
    child: MethodLocation?,
    private val returnLocation: MethodLocation,
    private val register: Int
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val item = mState.readRegister(register)

        // Mark register as assigned because next op will be payload, and it uses assigned register in this op to
        // determine target register for payload.
        mState.assignRegister(register, item)

        // It needs to know return address when finished since payload ops do not continue to next address.
        mState.setPseudoInstructionReturnLocation(returnLocation)
    }

    override fun toString(): String {
        return name + " r" + register + ", :addr_" + children[0].codeAddress
    }
}
