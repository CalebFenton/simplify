package org.cf.smalivm.emulate

import org.cf.smalivm.SideEffect
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2

abstract class EmulatedMethodCall {
    abstract fun execute(state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2): UnresolvedChild

    fun throwException(t: Throwable): UnresolvedChild {
        return throwException(t.javaClass, t.message)
    }

    fun throwException(klazz: Class<out Throwable>, message: String? = null): UnresolvedChild {
        return UnresolvedChild.build(klazz, message)
    }

    fun finish(sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE): UnresolvedChild {
        return UnresolvedChild.build(sideEffectLevel)
    }
}
