package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm2.ExecutionState
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction31t
import org.slf4j.LoggerFactory

class FillArrayDataOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val returnLocation: MethodLocation,
    private val register: Int
) : Op(location, child) {
    override fun execute(node: ExecutionNode, state: ExecutionState) {
        val item = state.readRegister(register)

        // Assign register here so payload op can determine target register for payload.
        state.assignRegister(register, item)

        // It needs to know return address when finished since payload ops do not continue to next address.
        state.setPseudoInstructionReturnLocation(returnLocation)
    }

    override fun getRegistersReadCount(): Int {
        return 1
    }

    override fun getRegistersAssignedCount(): Int {
        return 0
    }

    override fun toString(): String {
        return name + " r" + register + ", :addr_" + children[0].codeAddress
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(FillArrayDataOp::class.java.simpleName)
        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            exceptionFactory: ExceptionFactory,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val instruction = location.instruction as BuilderInstruction
            val address = instruction.location.codeAddress
            val returnAddress = address + instruction.codeUnits
            val returnLocation = addressToLocation[returnAddress]
            val branchOffset = (instruction as OffsetInstruction).codeOffset
            val childAddress = address + branchOffset
            val child = addressToLocation[childAddress]
            val instr = location.instruction as Instruction31t
            val register = instr.registerA
            return FillArrayDataOp(location, child, returnLocation, register)
        }
    }
}
