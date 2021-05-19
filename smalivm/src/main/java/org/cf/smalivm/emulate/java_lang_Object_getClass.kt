package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.slf4j.LoggerFactory

internal class java_lang_Object_getClass : EmulatedMethodCall(true) {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
        val argumentType = state.peekParameterOffset(0)!!.type
        val virtualType = vm.classManager.getVirtualType(argumentType)
        return try {
            val value = vm.classLoader.loadClass(virtualType.binaryName)
            state.assignReturnRegister(value, RETURN_TYPE)
            finishMethod()
        } catch (e: ClassNotFoundException) {
            throwException(e.javaClass, "Class not found: $argumentType", emulatedOrReflected = true)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Object_getClass::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.CLASS
    }
}
