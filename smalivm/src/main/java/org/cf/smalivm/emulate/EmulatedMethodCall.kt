package org.cf.smalivm.emulate

import org.cf.smalivm.SideEffect
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.VirtualMachine2
import java.util.*

abstract class EmulatedMethodCall {
    val exceptions: MutableSet<Pair<Class<*>, String?>> = HashSet()
    var sideEffectLevel: SideEffect.Level = SideEffect.Level.NONE
        protected set

    protected abstract fun execute(state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2)

    fun addException(klazz: Class<*>, className: String?) {
        exceptions.add(Pair(klazz, className))
    }
}
