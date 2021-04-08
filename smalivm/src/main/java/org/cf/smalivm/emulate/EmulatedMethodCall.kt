package org.cf.smalivm.emulate

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2

abstract class EmulatedMethodCall {
    abstract fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): UnresolvedChild

    fun throwException(t: Throwable): UnresolvedChild {
        return throwException(t.javaClass, t.message)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null): UnresolvedChild {
        return UnresolvedChild.build(klazz, message)
    }

    fun staticInitClass(
        virtualClass: VirtualType,
        classManager: ClassManager,
        classLoader: SmaliClassLoader,
        configuration: Configuration
    ): Array<out UnresolvedChild> {
        // TODO: Don't some classes not have clinit? May need to just set field literals here or allow null methods OR have UnresolvedStaticInit
        val staticInit = virtualClass.getMethod("<clinit>()V")!!
        val state = ExecutionState.build(staticInit, classManager, classLoader, configuration)
        return callMethod(staticInit, state, arrayOf())
    }

    fun callMethod(methodCall: VirtualMethod, state: ExecutionState, analyzedParameterTypes: Array<String>): Array<out UnresolvedChild> {
        return arrayOf(UnresolvedChild.build(methodCall, state, analyzedParameterTypes))
    }

    fun finish(): UnresolvedChild {
        return UnresolvedChild.build()
    }
}
