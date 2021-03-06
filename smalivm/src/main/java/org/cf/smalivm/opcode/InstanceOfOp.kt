package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.InstanceOfOp
import org.cf.smalivm.type.VirtualType
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class InstanceOfOp internal constructor(
    location: MethodLocation,
    child: MethodLocation?,
    private val destRegister: Int,
    private val arg1Register: Int,
    private val referenceType: VirtualType,
    private val vm: VirtualMachine
) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        val item = mState.readRegister(arg1Register)
        val value = item.value
        if (value == null) {
            mState.assignRegister(destRegister, HeapItem(false, CommonTypes.BOOL))
            return
        }
        val classManager = vm.classManager
        for (typeName in Utils.getDeclaredAndValueTypeNames(item)) {
            val itemType = classManager.getVirtualType(typeName)
            if (itemType.instanceOf(referenceType)) {
                mState.assignRegister(destRegister, HeapItem(true, CommonTypes.BOOL))
                return
            }
        }
        if (item.isUnknown) {
            // Since value is unknown, we can't get the actual type of the value. Declared type isn't enough.
            mState.assignRegister(destRegister, HeapItem.newUnknown(CommonTypes.BOOL))
        } else {
            mState.assignRegister(destRegister, HeapItem(false, CommonTypes.BOOL))
        }
    }

    override fun toString(): String {
        return "$name r$destRegister, $arg1Register, $referenceType"
    }

    companion object {
        private val log = LoggerFactory.getLogger(InstanceOfOp::class.java.simpleName)
    }
}
