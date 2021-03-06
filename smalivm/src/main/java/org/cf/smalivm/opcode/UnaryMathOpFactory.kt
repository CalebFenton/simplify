package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction12x

class UnaryMathOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction12x
        val destRegister = instr.registerA
        val srcRegister = instr.registerB
        return UnaryMathOp(location, child, destRegister, srcRegister)
    }
}
