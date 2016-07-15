package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReturnOp.class.getSimpleName());

    private final int register;

    ReturnOp(MethodLocation location, int register) {
        super(location);

        this.register = register;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem item = mState.readRegister(register);
        mState.assignReturnRegister(item);
    }

    @Override
    public String toString() {
        return getName() + " r" + register;
    }

}
