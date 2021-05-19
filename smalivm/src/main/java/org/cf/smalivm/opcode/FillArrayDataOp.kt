package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.UnresolvedChild
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction31t
import org.slf4j.LoggerFactory

class FillArrayDataOp internal constructor(
    location: MethodLocation,
    private val payloadAddress: Int,
    private val returnAddress: Int,
    private val register: Int
) : Op(location) {

    override val registersReadCount = 1
    override val registersAssignedCount = 2

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        val value = node.state.readRegister(register)
        // Assign register so payload op can determine target register for payload.
        node.state.assignRegister(register, value)
        // Payload op needs to know return address when finished.
        node.state.setPseudoInstructionReturnAddress(returnAddress)
        return finishOp(payloadAddress)
    }

    override fun toString(): String {
        return "$name r$register, :addr_${payloadAddress}"
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(FillArrayDataOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instruction = location.instruction as BuilderInstruction
            val address = instruction.location.codeAddress
            val returnAddress = address + instruction.codeUnits
            val branchOffset = (instruction as OffsetInstruction).codeOffset
            val payloadAddress = address + branchOffset
            val register = (location.instruction as Instruction31t).registerA
            return FillArrayDataOp(location, payloadAddress, returnAddress, register)
        }
    }
}
