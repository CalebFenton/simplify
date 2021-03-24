package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class ReturnVoidOp internal constructor(location: MethodLocation) : Op(location) {
    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        // If you look long enough into the void, the void begins to look back through you.
        return collectChildren()
    }

    override fun toString() = name

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(ReturnVoidOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            return ReturnVoidOp(location)
        }
    }
}
