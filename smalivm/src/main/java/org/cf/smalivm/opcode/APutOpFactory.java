package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class APutOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        Instruction23x instr = (Instruction23x) instruction;
        int putRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(instruction, child, putRegister, arrayRegister, indexRegister, vm.getClassManager());
    }

}
