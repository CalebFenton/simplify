package org.cf.smalivm.opcode

import org.cf.smalivm.ExceptionFactory
import org.cf.smalivm.type.ClassManager
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction23x

class AGetOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager, exceptionFactory: ExceptionFactory): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction23x
        val valueRegister = instr.registerA
        val arrayRegister = instr.registerB
        val indexRegister = instr.registerC
        return AGetOp(location, child, valueRegister, arrayRegister, indexRegister, vm.exceptionFactory)
    }
}
