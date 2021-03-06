package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OffsetInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction31t

class SwitchOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
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
