package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.opcode.SGetOp
import org.cf.smalivm.type.VirtualField
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class SGetOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val destRegister: Int, private val fieldReference: FieldReference,
    private val actualField: VirtualField?, private val vm: VirtualMachine
) : ExecutionContextOp(location, child) {
    override fun execute(node: ExecutionNode?, context: ExecutionContext) {
        val accessor = vm.staticFieldAccessor
        val item = accessor.getField(context, actualField)
        val mState = context.methodState
        mState.assignRegister(destRegister, item)
    }

    override fun toString(): String {
        return name + " r" + destRegister + ", " + ReferenceUtil.getFieldDescriptor(fieldReference)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SGetOp::class.java.simpleName)
    }
}
