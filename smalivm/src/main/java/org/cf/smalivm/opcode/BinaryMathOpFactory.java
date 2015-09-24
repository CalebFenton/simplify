package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class BinaryMathOpFactory implements OpFactory {

    @Override
    public Op create(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    VirtualMachine vm) {
        BuilderInstruction child = Utils.getNextInstruction(instruction, addressToInstruction);
        TwoRegisterInstruction instr = (TwoRegisterInstruction) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            int arg2Register = ((Instruction23x) instruction).getRegisterC();
            return new BinaryMathOp(instruction, child, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            arg1Register = instr.getRegisterA();
            int arg2Register = ((Instruction12x) instruction).getRegisterB();
            return new BinaryMathOp(instruction, child, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof NarrowLiteralInstruction) {
            // Instruction22b - add-int/lit8 vAA, vBB, #CC
            // Instruction22s - add-int/lit16 vAA, vBB, #CCCC
            int arg2Literal = ((NarrowLiteralInstruction) instruction).getNarrowLiteral();
            return new BinaryMathOp(instruction, child, destRegister, arg1Register, arg2Literal, true);
        } else {
            return null;
        }
    }

}
