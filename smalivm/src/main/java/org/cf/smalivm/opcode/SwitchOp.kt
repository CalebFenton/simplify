package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.OpChild
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction31t

class SwitchOp internal constructor(location: MethodLocation, private val child: MethodLocation, target: MethodLocation, private val register: Int) :
    Op(location, target) {

    override val registersReadCount = 1
    override val registersAssignedCount = 2

    override fun execute(node: ExecutionNode): Array<out OpChild> {
        // Use result register to store value to compare. Comparison is handled by payload op.
        val item = node.state.readRegister(register)
        node.state.assignResultRegister(item)
        // If switch "falls through", will need the immediate op after this.
        node.state.setPseudoInstructionReturnLocation(child)
        return collectChildren()
    }

    override fun toString() = "$name  r$register, :addr_${children[0].location.codeAddress}"

    companion object : OpFactory {
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val address = location.codeAddress
            val branchOffset = (location.instruction as OffsetInstruction).codeOffset
            val targetAddress = address + branchOffset
            val target = addressToLocation[targetAddress]!!
            val instr = location.instruction as Instruction31t
            val register = instr.registerA
            return SwitchOp(location, child, target, register)
        }
    }
}
