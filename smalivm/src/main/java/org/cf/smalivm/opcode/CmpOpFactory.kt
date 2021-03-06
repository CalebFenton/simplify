package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction23x

class CmpOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine?): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction23x?
        val destRegister = instr!!.registerA
        val lhsRegister = instr.registerB
        val rhsRegister = instr.registerC
        return CmpOp(location, child, destRegister, lhsRegister, rhsRegister)
    }
}
