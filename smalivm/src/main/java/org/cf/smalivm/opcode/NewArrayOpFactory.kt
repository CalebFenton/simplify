package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.opcode.NewArrayOpFactory
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.util.ReferenceUtil
import org.slf4j.LoggerFactory

class NewArrayOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): NewArrayOp {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction22c
        val destRegister = instr.registerA
        val sizeRegister = instr.registerB
        val arrayType = ReferenceUtil.getReferenceString(instr.reference)
        return NewArrayOp(location, child, destRegister, sizeRegister, arrayType, vm.classLoader)
    }

    companion object {
        private val log = LoggerFactory.getLogger(NewArrayOpFactory::class.java.simpleName)
    }
}
