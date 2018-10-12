package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public class FillArrayDataOp extends MethodStateOp {

    private final int register;
    private final MethodLocation returnLocation;

    FillArrayDataOp(MethodLocation location, MethodLocation child, MethodLocation returnLocation, int register) {
        super(location, child);

        this.returnLocation = returnLocation;
        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(register);

        // Mark register as assigned because next op will be payload, and it uses assigned register in this op to
        // determine target register for payload.
        mState.assignRegister(register, item);

        // It needs to know return address when finished since payload ops do not continue to next address.
        mState.setPseudoInstructionReturnLocation(returnLocation);
    }

    @Override
    public String toString() {
        return getName() + " r" + register + ", :addr_" + getChildren()[0].getCodeAddress();
    }

}
