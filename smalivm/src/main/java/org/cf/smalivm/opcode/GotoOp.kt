package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.UnresolvedChild
import org.jf.dexlib2.builder.BuilderOffsetInstruction
import org.jf.dexlib2.builder.MethodLocation


class GotoOp internal constructor(location: MethodLocation, val targetAddress: Int) : Op(location) {

    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        // https://xkcd.com/292/
        return finishOp(targetAddress)
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
            val target = (location.instruction as BuilderOffsetInstruction).target
            val targetAddress = target.codeAddress
            return GotoOp(location, targetAddress)
        }
    }
}
