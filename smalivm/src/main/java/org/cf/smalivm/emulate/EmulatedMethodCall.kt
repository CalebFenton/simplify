package org.cf.smalivm.emulate

import org.cf.smalivm.*

abstract class EmulatedMethodCall(open val canHandleUnknownValues: Boolean = false) : UnresolvedChildProducer() {
    abstract fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild>
}
