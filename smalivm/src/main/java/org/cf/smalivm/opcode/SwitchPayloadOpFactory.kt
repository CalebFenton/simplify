package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.SwitchPayload

class SwitchPayloadOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val instr = location.instruction as SwitchPayload
        val targetKeyToOffset: HashMap<Int, Int> = HashMap()
        for (element in instr.switchElements) {
            targetKeyToOffset[element.key] = element.offset
        }
        return SwitchPayloadOp(location, addressToLocation, targetKeyToOffset)
    }
}
