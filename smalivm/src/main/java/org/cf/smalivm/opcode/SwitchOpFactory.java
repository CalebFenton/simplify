package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class SwitchOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        int address = instruction.getLocation().getCodeAddress();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;
        BuilderInstruction target = addressToInstruction.get(targetAddress);
        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new SwitchOp(instruction, child, target, register);
    }

}
