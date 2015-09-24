package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class CmpOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        Instruction23x instr = (Instruction23x) instruction;
        int destRegister = instr.getRegisterA();
        int lhsRegister = instr.getRegisterB();
        int rhsRegister = instr.getRegisterC();

        return new CmpOp(instruction, child, destRegister, lhsRegister, rhsRegister);
    }

}
