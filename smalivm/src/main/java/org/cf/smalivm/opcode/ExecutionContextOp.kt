package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionContext
import org.cf.smalivm.context.ExecutionNode
import org.jf.dexlib2.builder.MethodLocation

abstract class ExecutionContextOp : Op {
    internal constructor(location: MethodLocation, child: MethodLocation?) : super(location, child) {}
    internal constructor(location: MethodLocation, children: Array<MethodLocation?>?) : super(location, children) {}

    abstract fun execute(executionNode: ExecutionNode?, context: ExecutionContext)
}
