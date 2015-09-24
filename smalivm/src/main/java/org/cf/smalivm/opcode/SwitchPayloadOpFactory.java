package org.cf.smalivm.opcode;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;

public class SwitchPayloadOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        SwitchPayload instr = (SwitchPayload) instruction;
        TIntIntMap targetKeyToOffset = new TIntIntHashMap();
        for (SwitchElement element : instr.getSwitchElements()) {
            targetKeyToOffset.put(element.getKey(), element.getOffset());
        }

        return new SwitchPayloadOp(instruction, addressToInstruction, targetKeyToOffset);
    }

}
