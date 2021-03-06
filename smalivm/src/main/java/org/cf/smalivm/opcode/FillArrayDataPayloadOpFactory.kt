package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload

class FillArrayDataPayloadOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine?): Op {
        val instr = location.instruction as ArrayPayload?
        val elementWidth = instr!!.elementWidth
        val arrayElements = instr.arrayElements
        return FillArrayDataPayloadOp(location, elementWidth, arrayElements)
    }
}
