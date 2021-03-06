package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.opcode.IGetOp
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class IGetOp(
    location: MethodLocation, child: MethodLocation?, private val destRegister: Int, private val instanceRegister: Int,
    private val fieldDescriptor: String, private val vm: VirtualMachine
) : ExecutionContextOp(location, child) {
    override fun execute(node: ExecutionNode?, context: ExecutionContext) {
        // TODO: https://github.com/CalebFenton/simplify/issues/22
        val mState = context.methodState
        val instanceItem = mState.readRegister(instanceRegister)
        val type = fieldDescriptor.split(":").toTypedArray()[1]
        mState.assignRegister(destRegister, HeapItem.newUnknown(type))
    }

    override fun toString(): String {
        return "$name r$destRegister, r$instanceRegister, $fieldDescriptor"
    }

    companion object {
        private val log = LoggerFactory.getLogger(IGetOp::class.java.simpleName)
    }
}
