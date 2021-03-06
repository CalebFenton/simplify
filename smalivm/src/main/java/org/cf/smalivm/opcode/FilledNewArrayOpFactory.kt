package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.VariableRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.jf.dexlib2.util.ReferenceUtil

class FilledNewArrayOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine?): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as BuilderInstruction?
        val reference = (instruction as ReferenceInstruction?)!!.reference
        val typeReference = ReferenceUtil.getReferenceString(reference)
        val registerCount = (instruction as VariableRegisterInstruction?)!!.registerCount
        val opName = instruction!!.getOpcode().name
        val dimensionRegisters = IntArray(registerCount)
        if (opName.endsWith("/range")) {
            val instr = location.instruction as Instruction3rc?
            val startRegister = instr!!.startRegister
            for (i in dimensionRegisters.indices) {
                dimensionRegisters[i] = startRegister + i
            }
        } else {
            val instr = location.instruction as Instruction35c?
            when (dimensionRegisters.size) {
                5 -> {
                    dimensionRegisters[4] = instr!!.registerG
                    dimensionRegisters[3] = instr.registerF
                    dimensionRegisters[2] = instr.registerE
                    dimensionRegisters[1] = instr.registerD
                    dimensionRegisters[0] = instr.registerC
                }
                4 -> {
                    dimensionRegisters[3] = instr!!.registerF
                    dimensionRegisters[2] = instr.registerE
                    dimensionRegisters[1] = instr.registerD
                    dimensionRegisters[0] = instr.registerC
                }
                3 -> {
                    dimensionRegisters[2] = instr!!.registerE
                    dimensionRegisters[1] = instr.registerD
                    dimensionRegisters[0] = instr.registerC
                }
                2 -> {
                    dimensionRegisters[1] = instr!!.registerD
                    dimensionRegisters[0] = instr.registerC
                }
                1 -> dimensionRegisters[0] = instr!!.registerC
                else -> {
                }
            }
        }
        return FilledNewArrayOp(location, child, dimensionRegisters, typeReference)
    }
}
