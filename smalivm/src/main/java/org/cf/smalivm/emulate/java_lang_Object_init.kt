package org.cf.smalivm.emulate

import org.cf.smalivm.ObjectInstantiator
import org.cf.smalivm.type.UninitializedInstance
import org.cf.smalivm2.*
import org.slf4j.LoggerFactory

internal class java_lang_Object_init : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild> {
        val instance = state.peekParameterOffset(0)!!
        val uninitializedInstance = instance.raw as UninitializedInstance
        val instanceType = uninitializedInstance.type
        val newInstanceItem = try {
            // Create a Java class of the true type
            val klazz = vm.classLoader.loadClass(instanceType.binaryName)
            val newInstance = ObjectInstantiator.newInstance(klazz)
            Value.wrap(newInstance, instanceType.name)
        } catch (e: Exception) {
            log.error("Real exception initializing Object; returning unknown", e)
            Value.unknown(instanceType.name)
        }
        state.assignRegister(state.firstParameterRegister, newInstanceItem, updateIdentities = true)

        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Object_init::class.java.simpleName)
    }
}
