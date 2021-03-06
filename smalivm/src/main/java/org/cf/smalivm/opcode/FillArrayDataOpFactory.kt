package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction31t

class FillArrayDataOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation>, vm: VirtualMachine?): Op {
        val instruction = location.instruction as BuilderInstruction?
        val address = instruction!!.location.codeAddress
        val returnAddress = address + instruction.codeUnits
        val returnLocation = addressToLocation[returnAddress]
        val branchOffset = (instruction as OffsetInstruction?)!!.codeOffset
        val childAddress = address + branchOffset
        val child = addressToLocation[childAddress]
        val instr = location.instruction as Instruction31t?
        val register = instr!!.registerA
        return FillArrayDataOp(location, child, returnLocation, register)
    }
}
