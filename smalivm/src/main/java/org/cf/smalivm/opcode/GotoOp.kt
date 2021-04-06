package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.jf.dexlib2.builder.MethodLocation

class GotoOp internal constructor(location: MethodLocation) : Op(location) {

    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        // https://xkcd.com/292/
        return finish()
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" :addr_").append(nextAddress)
        return sb.toString()
    }

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            return GotoOp(location)
        }
    }
}
