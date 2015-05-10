package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction11x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReturnOp.class.getSimpleName());

    static ReturnOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        Instruction11x instr = (Instruction11x) instruction;
        int register = instr.getRegisterA();

        return new ReturnOp(address, opName, register);
    }

    private final int register;

    ReturnOp(int address, String opName, int register) {
        super(address, opName, new int[0]);

        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(register);
        mState.assignReturnRegister(item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register);

        return sb.toString();
    }

}
