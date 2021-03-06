package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction11x

class ReturnOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val instr = location.instruction as Instruction11x
        val register = instr.registerA
        return ReturnOp(location, register)
    }
}
