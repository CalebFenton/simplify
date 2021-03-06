package org.cf.smalivm.opcode

import org.cf.smalivm.SideEffect
import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm.type.VirtualClass
import org.jf.dexlib2.builder.MethodLocation

class NewInstanceOp internal constructor(
    location: MethodLocation, child: MethodLocation?, private val destRegister: Int, private val virtualClass: VirtualClass,
    private val vm: VirtualMachine
) : ExecutionContextOp(location, child) {
    override var sideEffectLevel: SideEffect.Level
        private set

    override fun execute(node: ExecutionNode?, context: ExecutionContext) {
        val instance: Any = UninitializedInstance(virtualClass)
        sideEffectLevel = if (vm.isSafe(virtualClass)) {
            SideEffect.Level.NONE
        } else {
            // New-instance causes static initialization (but not new-array!)
            context.readClassState(virtualClass) // access will initialize if necessary
            context.getClassSideEffectLevel(virtualClass)
        }
        val mState = context.methodState
        val instanceItem = HeapItem(instance, virtualClass.name)
        mState.assignRegister(destRegister, instanceItem)
    }

    override fun toString(): String {
        return "$name r$destRegister, $virtualClass"
    }

    init {
        sideEffectLevel = SideEffect.Level.STRONG
    }
}
