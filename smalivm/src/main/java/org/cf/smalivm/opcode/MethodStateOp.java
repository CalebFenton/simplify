package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;

public abstract class MethodStateOp extends Op {

    MethodStateOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    MethodStateOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    public abstract void execute(ExecutionNode executionNode, MethodState mState);

}
