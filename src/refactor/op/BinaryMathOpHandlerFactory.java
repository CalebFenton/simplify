package refactor.op;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22b;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

final class BinaryMathOpHandlerFactory {

    static BinaryMathOpHandler create(Instruction instruction, int index) {
        String opName = instruction.getOpcode().name;
        TwoRegisterInstruction instr = (TwoRegisterInstruction) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();

        BinaryMathOpHandler result = null;
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            int arg2Register = ((Instruction23x) instruction).getRegisterC();
            result = new BinaryMathOpHandler(index, opName, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            int arg2Register = ((Instruction12x) instruction).getRegisterB();
            result = new BinaryMathOpHandler(index, opName, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof Instruction22b) {
            // add-int/lit8 vAA, vBB, #CC
            int arg2Literal = ((Instruction22b) instruction).getNarrowLiteral();
            result = new BinaryMathOpHandler(index, opName, destRegister, arg1Register, arg2Literal, true);
        } else if (instruction instanceof Instruction22s) {
            // add-int/lit16 vAA, vBB, #CCCC
            long arg2Literal = ((Instruction22s) instruction).getWideLiteral();
            result = new BinaryMathOpHandler(index, opName, destRegister, arg1Register, arg2Literal);
        }

        return result;
    }

}
