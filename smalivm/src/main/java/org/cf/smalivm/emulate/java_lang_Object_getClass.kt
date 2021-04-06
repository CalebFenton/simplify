package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2
import org.slf4j.LoggerFactory

internal class java_lang_Object_getClass : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2): UnresolvedChild {
        val argumentType = state.peekParameter(0)!!.type
        val virtualType = vm.classManager.getVirtualType(argumentType)
        try {
            val value = vm.classLoader.loadClass(virtualType.binaryName)
            state.assignReturnRegister(value, RETURN_TYPE)
            return finish()
        } catch (e: ClassNotFoundException) {
            return throwException(e.javaClass, "Class not found: $argumentType")
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Object_getClass::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.CLASS
    }
}
