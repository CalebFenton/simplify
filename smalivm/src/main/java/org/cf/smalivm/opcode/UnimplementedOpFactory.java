package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

public class UnimplementedOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);

        Opcode op = instruction.getOpcode();
        if (instruction instanceof OneRegisterInstruction) {
            OneRegisterInstruction instr = (OneRegisterInstruction) instruction;
            int registerA = instr.getRegisterA();

            return new UnimplementedOp(instruction, child, op.canContinue(), op.canThrow(), op.setsResult(),
                            op.setsRegister(), registerA);
        } else {
            return new UnimplementedOp(instruction, child, op.canContinue(), op.canThrow(), op.setsResult());
        }
    }

}
