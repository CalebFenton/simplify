package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.TypeReference;

public class InstanceOfOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        Instruction22c instr = (Instruction22c) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new InstanceOfOp(instruction, child, destRegister, arg1Register, className, vm);
    }

}
