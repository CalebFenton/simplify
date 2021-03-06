package org.cf.smalivm.opcode

import org.cf.smalivm.opcode.IfOp.IfType
import org.cf.smalivm.type.ClassManager
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction22t

class IfOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager): Op {
        val instruction = location.instruction as BuilderInstruction

        val address = instruction.location.codeAddress
        val branchOffset = (instruction as OffsetInstruction?)!!.codeOffset
        val targetAddress = address + branchOffset
        val child = Utils.getNextLocation(location, addressToLocation)
        val target = addressToLocation[targetAddress]!!
        val opName = instruction.getOpcode().name
        val ifType = getIfType(opName)
        val register1 = (instruction as OneRegisterInstruction?)!!.registerA
        return if (instruction is Instruction22t) {
            // if-* vA, vB, :label
            IfOp(location, child, ifType, target, register1, instruction.registerB)
        } else {
            // if-*z vA, :label (Instruction 21t)
            IfOp(location, child, ifType, target, register1)
        }
    }

    companion object {
        private fun getIfType(opName: String): IfType {
            return when {
                opName.contains("-eq") -> IfType.EQUAL
                opName.contains("-ne") -> IfType.NOT_EQUAL
                opName.contains("-lt") -> IfType.LESS
                opName.contains("-le") -> IfType.LESS_OR_EQUAL
                opName.contains("-gt") -> IfType.GREATER
                opName.contains("-ge") -> IfType.GREATER_OR_EQUAL
                else -> throw RuntimeException("Unrecognized IF opcode type: $opName")
            }
        }
    }
}
