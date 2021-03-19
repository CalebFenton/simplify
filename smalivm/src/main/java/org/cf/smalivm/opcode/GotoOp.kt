package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionState
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.BuilderOffsetInstruction
import org.jf.dexlib2.builder.MethodLocation

class GotoOp internal constructor(location: MethodLocation, childInstruction: MethodLocation) : Op(location, childInstruction) {
    override fun execute(node: ExecutionNode, state: ExecutionState) {
        // https://xkcd.com/292/
    }

    override fun getRegistersReadCount(): Int {
        return 0
    }

    override fun getRegistersAssignedCount(): Int {
        return 0
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        val childAddress = children[0].codeAddress
        sb.append(" :addr_").append(childAddress)
        return sb.toString()
    }

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
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
