package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction21c
import org.jf.dexlib2.iface.reference.FieldReference

class SPutOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction21c?
        val destRegister = instr!!.registerA
        val fieldReference = instr.reference as FieldReference
        val fieldClass = vm.classManager.getVirtualClass(fieldReference.definingClass)
        val actualField = fieldClass.getField(fieldReference.name)
        return SPutOp(location, child, destRegister, fieldReference, actualField, vm)
    }
}
