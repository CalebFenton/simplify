package org.cf.smalivm.emulate

import org.cf.smalivm2.*
import org.jf.dexlib2.builder.MethodLocation

abstract class EmulatedMethodCall(open val canHandleUnknownValues: Boolean = false) : UnresolvedChildProducer() {
    abstract fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild>
}
