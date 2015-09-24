package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;

public class SwitchOp extends MethodStateOp {

    private final BuilderInstruction child;
    private final int register;

    SwitchOp(BuilderInstruction instruction, BuilderInstruction child, BuilderInstruction target, int register) {
        super(instruction, target);

        this.child = child;
        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // Use result register to store value to compare. Comparison is handled by payload op.
        HeapItem item = mState.readRegister(register);
        mState.assignResultRegister(item);

        // If switch "falls through", will need the immediate op after this.
        mState.setPseudoInstructionReturnInstruction(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(register).append(", #").append(getChildren()[0].getLocation().getCodeAddress());

        return sb.toString();
    }

}
