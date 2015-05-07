package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;

public abstract class MethodStateOp extends Op {

    MethodStateOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    MethodStateOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    // TODO: don't return children, set children and look them up
    public abstract int[] execute(MethodState mState);

}
