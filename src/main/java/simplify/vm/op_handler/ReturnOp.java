package simplify.vm.op_handler;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction11x;

import simplify.Main;
import simplify.vm.context.MethodContext;

public class ReturnOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static ReturnOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        int register = Integer.MIN_VALUE;
        if (!opName.endsWith("-void")) {
            Instruction11x instr = (Instruction11x) instruction;
            register = instr.getRegisterA();
        }

        return new ReturnOp(address, opName, register);
    }

    private final int register;

    ReturnOp(int address, String opName, int register) {
        super(address, opName, new int[0]);

        this.register = register;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        if (!getOpName().endsWith("-void")) {
            mctx.assignReturnRegister(mctx.readRegister(register));
        }

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        if (register != Integer.MIN_VALUE) {
            sb.append(" r").append(register);
        }

        return sb.toString();
    }

}
