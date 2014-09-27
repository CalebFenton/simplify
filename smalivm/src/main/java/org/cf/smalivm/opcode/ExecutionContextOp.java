package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionContext;

public abstract class ExecutionContextOp extends Op {

    ExecutionContextOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    ExecutionContextOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    public abstract int[] execute(ExecutionContext ectx);

}
