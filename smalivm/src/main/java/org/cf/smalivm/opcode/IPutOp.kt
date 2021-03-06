package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.opcode.IPutOp
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class IPutOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val valueRegister: Int, private val instanceRegister: Int,
    private val fieldDescriptor: String, private val vm: VirtualMachine
) : ExecutionContextOp(location, child) {
    override fun execute(node: ExecutionNode?, context: ExecutionContext) {
        val mState = context.methodState
        val valueItem = mState.readRegister(valueRegister)
        val instanceItem = mState.readRegister(instanceRegister)
        mState.assignRegister(instanceRegister, instanceItem)
    }

    override val sideEffectLevel: SideEffect.Level
        get() = SideEffect.Level.WEAK

    override fun toString(): String {
        return "$name r$valueRegister, r$instanceRegister, $fieldDescriptor"
    }

    companion object {
        private val log = LoggerFactory.getLogger(IPutOp::class.java.simpleName)
    }
}
