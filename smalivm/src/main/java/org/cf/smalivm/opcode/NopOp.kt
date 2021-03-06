package org.cf.smalivm.opcode

import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.MethodState
import org.jf.dexlib2.builder.MethodLocation

class NopOp internal constructor(location: MethodLocation, child: MethodLocation?) : MethodStateOp(location, child) {
    override fun execute(node: ExecutionNode, mState: MethodState) {
        // Yesterday, upon the stair,
        // I met an op who wasn't there.
        // It wasn't there again today,
        // I wish, I wish it'd go away...
        //
        // Last night I saw upon the stair,
        // A little op who wasn't there,
        // It wasn't there again today
        // Oh, how I wish it'd go away...
    }

    override fun toString(): String {
        return name
    }
}
