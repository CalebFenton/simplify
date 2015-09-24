package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class FillArrayDataOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        int address = instruction.getLocation().getCodeAddress();
        int returnAddress = address + instruction.getCodeUnits();
        BuilderInstruction returnInstruction = addressToInstruction.get(returnAddress);
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int childAddress = address + branchOffset;
        BuilderInstruction child = addressToInstruction.get(childAddress);
        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new FillArrayDataOp(instruction, child, returnInstruction, register);
    }

}
