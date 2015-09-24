package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

public class CheckCastOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        Instruction21c instr = (Instruction21c) instruction;
        int targetRegister = instr.getRegisterA();
        TypeReference typeRef = (TypeReference) instr.getReference();
        String className = typeRef.getType();

        return new CheckCastOp(instruction, child, targetRegister, className, vm);
    }

}
