package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.opcode.MoveOp.MoveType
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction

class MoveOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine?): Op? {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as BuilderInstruction?
        val opName = instruction!!.opcode.name
        val toRegister = (instruction as OneRegisterInstruction?)!!.registerA
        val moveType = getMoveType(opName)
        return when (moveType) {
            MoveType.RESULT, MoveType.EXCEPTION -> MoveOp(location, child, toRegister, moveType)
            MoveType.REGISTER -> {
                val targetRegister = (instruction as TwoRegisterInstruction?)!!.registerB
                MoveOp(location, child, toRegister, targetRegister)
            }
            else -> null
        }
    }

    companion object {
        private fun getMoveType(opName: String): MoveType {
            return if (opName.contains("-result")) {
                MoveType.RESULT
            } else if (opName.contains("-exception")) {
                MoveType.EXCEPTION
            } else {
                MoveType.REGISTER
            }
        }
    }
}
