package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;

public class FillArrayDataOp extends MethodStateOp {

    private final int register;
    private final BuilderInstruction returnAddress;

    FillArrayDataOp(BuilderInstruction instruction, BuilderInstruction child, BuilderInstruction returnInstruction,
                    int register) {
        super(instruction, child);

        this.returnAddress = returnInstruction;
        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(register);

        // Mark register as assigned because next op will be payload, and it uses assigned register in this op to
        // determine target register for payload.
        mState.assignRegister(register, item);

        // It needs to know return address when finished since payload ops do not continue to next address.
        mState.setPseudoInstructionReturnInstruction(returnAddress);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register).append(", #").append(getChildren()[0].getLocation().getCodeAddress());

        return sb.toString();
    }

}
