package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.TypeReference

class InstanceOfOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction22c?
        val destRegister = instr!!.registerA
        val arg1Register = instr.registerB
        val typeReference = instr.reference as TypeReference
        val referenceType = vm.classManager.getVirtualType(typeReference)
        return InstanceOfOp(location, child, destRegister, arg1Register, referenceType, vm)
    }
}
