package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

import simplify.vm.MethodContext;

public class SwitchOpHandler extends OpHandler {

    static SwitchOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;

        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new SwitchOpHandler(address, opName, targetAddress, register);
    }

    private final int register;

    private SwitchOpHandler(int address, String opName, int targetAddress, int register) {
        super(address, opName, targetAddress);

        this.register = register;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        // Use result register to store value to compare. Comparison will be done at payload handler.
        Object value = mctx.readRegister(register);

        mctx.assignResultRegister(value);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" r").append(register).append(", #").append(getPossibleChildren()[0]);

        return sb.toString();
    }

}
