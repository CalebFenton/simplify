package org.cf.smalivm.opcode

import org.cf.smalivm.VirtualMachine
import org.jf.dexlib2.builder.MethodLocation

class ReturnVoidOpFactory : OpFactory {
    override fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, vm: VirtualMachine): Op {
        return ReturnVoidOp(location)
    }
}
