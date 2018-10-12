package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public class SwitchOp extends MethodStateOp {

    private final MethodLocation child;
    private final int register;

    SwitchOp(MethodLocation location, MethodLocation child, MethodLocation target, int register) {
        super(location, target);

        this.child = child;
        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // Use result register to store value to compare. Comparison is handled by payload op.
        HeapItem item = mState.readRegister(register);
        mState.assignResultRegister(item);

        // If switch "falls through", will need the immediate op after this.
        mState.setPseudoInstructionReturnLocation(child);
    }

    @Override
    public String toString() {
        return getName() + " r" + register + ", :addr_" + getChildren()[0].getCodeAddress();
    }

}
