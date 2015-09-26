package org.cf.smalivm.opcode;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;

public class SwitchPayloadOpFactory implements OpFactory {

    @Override
    public Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm) {
        SwitchPayload instr = (SwitchPayload) location.getInstruction();
        TIntIntMap targetKeyToOffset = new TIntIntHashMap();
        for (SwitchElement element : instr.getSwitchElements()) {
            targetKeyToOffset.put(element.getKey(), element.getOffset());
        }

        return new SwitchPayloadOp(location, addressToLocation, targetKeyToOffset);
    }

}
