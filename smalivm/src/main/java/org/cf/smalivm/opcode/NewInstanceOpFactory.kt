package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.TypeReference

class NewInstanceOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction21c?
        val destRegister = instr!!.registerA
        val typeRef = instr.reference as TypeReference
        val className = typeRef.type
        val virtualClass = vm.classManager.getVirtualClass(className)
        return NewInstanceOp(location, child, destRegister, virtualClass, vm)
    }
}
