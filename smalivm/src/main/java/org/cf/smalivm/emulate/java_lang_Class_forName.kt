package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory

internal class java_lang_Class_forName : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
        val binaryClassName = state.peekParameterOffset(0)!!.raw as String?
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
                val virtualClass = try {
                    vm.classManager.getVirtualClass(className)
                } catch (e: RuntimeException) {
                    return throwException(ClassNotFoundException::class.java, binaryClassName, emulatedOrReflected = true)
                }
                if (!state.isClassInitialized(virtualClass)) {
                    return staticInitClass(virtualClass)
                }
            }
            state.assignReturnRegister(value, RETURN_TYPE)
        } catch (e: ClassNotFoundException) {
            return throwException(ClassNotFoundException::class.java, binaryClassName, emulatedOrReflected = true)
        }
        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_Class_forName::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.CLASS
    }
}
