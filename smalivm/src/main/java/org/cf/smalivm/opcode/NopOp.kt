package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation

class NopOp internal constructor(location: MethodLocation, child: MethodLocation) : Op(location, child) {

    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        // Yesterday, upon the stair,
        // I met an op who wasn't there.
        // It wasn't there again today,
        // I wish, I wish it'd go away...
        //
        // Last night I saw upon the stair,
        // A little op who wasn't there,
        // It wasn't there again today
        // Oh, how I wish it'd go away...
        return collectChildren()
    }

    override fun toString() = name

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            return NopOp(location, child)
        }
    }
}
