package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction23x

class APutOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction23x
        val putRegister = instr.registerA
        val arrayRegister = instr.registerB
        val indexRegister = instr.registerC
        return APutOp(location, child, putRegister, arrayRegister, indexRegister, vm.classManager, vm.exceptionFactory)
    }
}
