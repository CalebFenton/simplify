package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

abstract class MethodStateOp : Op {
    internal constructor(location: MethodLocation) : super(location, arrayOf<MethodLocation>())
    internal constructor(location: MethodLocation, child: MethodLocation) : super(location, child)
    internal constructor(location: MethodLocation, children: Array<MethodLocation>) : super(location, children)

    abstract fun execute(executionNode: ExecutionNode, mState: MethodState)
}
