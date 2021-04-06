package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.type.VirtualClass
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory

internal class java_lang_Class_forName : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2): UnresolvedChild {
        val binaryClassName = state.peekParameter(0)!!.value as String?
        val className = ClassNameUtils.binaryToInternal(binaryClassName)
        val value: Class<*>
        try {
            if (vm.configuration.isSafe(className)) {
                value = Class.forName(binaryClassName)
            } else {
                val classLoader: ClassLoader = vm.classLoader
                value = classLoader.loadClass(binaryClassName)
                /*
                 * While the VM's class loader provides actual Java classes for local Smali classes, the field values
                 * shouldn't be used. To this end, static initialization is done locally as well as when the class
                 * is loaded and only the local values are used.
                 * Note: this is done *after* trying to load the class in case there's an exception
                 */
                val virtualClass: VirtualClass
                virtualClass = try {
                    vm.classManager.getVirtualClass(className)
                } catch (e: RuntimeException) {
                    throw ClassNotFoundException()
                }
                if (!state.isClassInitialized(virtualClass)) {
                    // TODO: should create a state pass the class state to the VM to execute
//                    state.staticallyInitializeClassIfNecessary(virtualClass);
//                    vm.execute()
//                    level = state.getClassSideEffectLevel(virtualClass);
                }
            }
            callerNode.state.assignReturnRegister(value, RETURN_TYPE)
        } catch (e: ClassNotFoundException) {
            throwException(ClassNotFoundException::class.java, binaryClassName)
        }
        return finish()
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Class_forName::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.CLASS
    }
}
