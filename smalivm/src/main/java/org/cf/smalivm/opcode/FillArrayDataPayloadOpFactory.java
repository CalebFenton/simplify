package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.List;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload;

public class FillArrayDataPayloadOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        ArrayPayload instr = (ArrayPayload) instruction;
        int elementWidth = instr.getElementWidth();
        List<Number> arrayElements = instr.getArrayElements();

        return new FillArrayDataPayloadOp(instruction, elementWidth, arrayElements);
    }

}
