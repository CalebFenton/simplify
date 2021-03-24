package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.BuilderOffsetInstruction
import org.jf.dexlib2.builder.MethodLocation

class GotoOp internal constructor(location: MethodLocation, childInstruction: MethodLocation) : Op(location, childInstruction) {
    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        // https://xkcd.com/292/
        return collectChildren()
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        val childAddress = children[0].location.codeAddress
        sb.append(" :addr_").append(childAddress)
        return sb.toString()
    }

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instruction = location.instruction as BuilderInstruction
            val target = (instruction as BuilderOffsetInstruction).target
            val targetAddress = target.codeAddress
            val child = addressToLocation[targetAddress]!!
            return GotoOp(location, child)
        }
    }
}
