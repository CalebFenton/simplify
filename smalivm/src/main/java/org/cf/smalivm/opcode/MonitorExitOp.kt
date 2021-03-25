package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

class MonitorExitOp internal constructor(location: MethodLocation, child: MethodLocation, private val destRegister: Int) :
    Op(location, child) {
    override val registersReadCount = 0
    override val registersAssignedCount = 0

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        return finishOp()
    }

    override fun toString() = "$name r$destRegister"

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instruction = location.instruction as BuilderInstruction
            val destRegister = (instruction as OneRegisterInstruction).registerA
            return MonitorExitOp(location, child, destRegister)
        }
    }
}
