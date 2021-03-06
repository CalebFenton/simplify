package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.TypeReference

class CheckCastOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction21c?
        val targetRegister = instr!!.registerA
        val reference = instr.reference as TypeReference
        val referenceType = vm.classManager.getVirtualType(reference)
        return CheckCastOp(location, child, targetRegister, referenceType, vm.classManager, vm.exceptionFactory)
    }
}
