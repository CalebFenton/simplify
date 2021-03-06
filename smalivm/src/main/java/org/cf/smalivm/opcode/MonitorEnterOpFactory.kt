package org.cf.smalivm.opcode

import gnu.trove.map.TIntObjectMap
import org.cf.smalivm.VirtualMachine
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction

class MonitorEnterOpFactory : OpFactory {
    fun create(location: MethodLocation, addressToLocation: TIntObjectMap<MethodLocation?>?, vm: VirtualMachine?): Op {
        val child = Utils.getNextLocation(location, addressToLocation)
        val instruction = location.instruction as BuilderInstruction?
        val destRegister = (instruction as OneRegisterInstruction?)!!.registerA
        return MonitorEnterOp(location, child, destRegister)
    }
}
