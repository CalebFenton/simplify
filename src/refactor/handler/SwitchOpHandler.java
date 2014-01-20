package refactor.handler;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

import refactor.vm.MethodContext;
import refactor.vm.RegisterStore;

public class SwitchOpHandler extends OpHandler {

    static SwitchOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;

        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new SwitchOpHandler(address, opName, targetAddress, register);
    }

    private final int address;
    private final String opName;
    private final int targetAddress;
    private final int register;

    private SwitchOpHandler(int address, String opName, int targetAddress, int register) {
        this.address = address;
        this.opName = opName;
        this.targetAddress = targetAddress;
        this.register = register;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        // Use result register to store target register value. The payload handler will examine.
        RegisterStore registerStore = mctx.getRegister(register, address);

        mctx.setResultRegister(registerStore);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" r").append(register).append(", #").append(targetAddress);

        return sb.toString();
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { targetAddress };
    }

    @Override
    public int getAddress() {
        return address;
    }

}
