package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.opcode.SPutOp
import org.cf.smalivm.type.VirtualField
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class SPutOp internal constructor(
    location: MethodLocation, child: MethodLocation?, val valueRegister: Int,
    val fieldReference: FieldReference, val actualField: VirtualField?, val vm: VirtualMachine
) : ExecutionContextOp(location, child) {
    override fun execute(node: ExecutionNode?, context: ExecutionContext) {
        val mState = context.methodState
        val item = mState.readRegister(valueRegister)
        // TODO: check if this is <clinit> and only allow static final fields to be initialized here
        vm.staticFieldAccessor.putField(context, actualField, item)
    }

    override val sideEffectLevel: SideEffect.Level
        get() = SideEffect.Level.WEAK

    override fun toString(): String {
        return "$name r$valueRegister, " + ReferenceUtil
            .getFieldDescriptor(fieldReference)
    }

    companion object {
        private val log = LoggerFactory.getLogger(SPutOp::class.java.simpleName)
    }
}
