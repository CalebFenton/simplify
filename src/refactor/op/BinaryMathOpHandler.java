package refactor.op;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22b;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import simplify.exec.MethodExecutionContext;

public class BinaryMathOpHandler {

    public BinaryMathOpHandler() {
        // TODO Auto-generated constructor stub
    }

    protected static Object[] getLeftAndRight(MethodExecutionContext ectx, Instruction instruction, int index) {
        Object lhs = null;
        Object rhs = null;
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            Instruction23x instr = (Instruction23x) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = ectx.getRegisterValue(instr.getRegisterC(), index);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            Instruction12x instr = (Instruction12x) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterA(), index);
            rhs = ectx.getRegisterValue(instr.getRegisterB(), index);
        } else if (instruction instanceof Instruction22s) {
            // add-int/lit16 vAA, vBB, #CCCC
            Instruction22s instr = (Instruction22s) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = instr.getWideLiteral();
        } else if (instruction instanceof Instruction22b) {
            // add-int/lit8 vAA, vBB, #CC
            Instruction22b instr = (Instruction22b) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = instr.getNarrowLiteral();
        }

        return new Object[] { lhs, rhs };
    }

}
