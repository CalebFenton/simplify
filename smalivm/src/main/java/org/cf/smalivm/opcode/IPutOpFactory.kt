package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.Instruction22c
import org.jf.dexlib2.iface.reference.FieldReference
import org.jf.dexlib2.util.ReferenceUtil

class IPutOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instr = location.instruction as Instruction22c?
        val valueRegister = instr!!.registerA
        val instanceRegister = instr.registerB
        val reference = instr.reference as FieldReference
        val fieldDescriptor = ReferenceUtil.getFieldDescriptor(reference)
        return IPutOp(location, child, valueRegister, instanceRegister, fieldDescriptor, vm)
    }
}
