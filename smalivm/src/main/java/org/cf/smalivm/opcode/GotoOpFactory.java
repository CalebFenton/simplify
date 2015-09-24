package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;

public class GotoOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = instruction.getLocation().getCodeAddress() + branchOffset;
        BuilderInstruction child = addressToInstruction.get(targetAddress);

        return new GotoOp(instruction, child);

    }

}
