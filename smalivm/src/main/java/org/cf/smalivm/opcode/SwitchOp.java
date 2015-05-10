package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class SwitchOp extends MethodStateOp {

    static SwitchOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;

        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new SwitchOp(address, opName, childAddress, targetAddress, register);
    }

    private final int childAddress;
    private final int register;

    private SwitchOp(int address, String opName, int childAddress, int targetAddress, int register) {
        super(address, opName, targetAddress);

        this.childAddress = childAddress;
        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // Use result register to store value to compare. Comparison is handled by payload op.
        HeapItem item = mState.readRegister(register);
        mState.assignResultRegister(item);

        // If switch "falls through", will need the immediate op after this.
        mState.setPseudoInstructionReturnAddress(childAddress);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register).append(", #").append(getChildren()[0]);

        return sb.toString();
    }

}
