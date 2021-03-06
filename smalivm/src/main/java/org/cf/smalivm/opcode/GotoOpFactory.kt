package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.BuilderOffsetInstruction
import org.jf.dexlib2.builder.MethodLocation

class GotoOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        val instruction = location.instruction as BuilderInstruction?
        val target = (instruction as BuilderOffsetInstruction?)!!.target
        val targetAddress = target.codeAddress
        val child = addressToLocation[targetAddress]
        return GotoOp(location, child)
    }
}
