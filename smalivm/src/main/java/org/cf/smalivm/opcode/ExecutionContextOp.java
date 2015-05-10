package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;

public abstract class ExecutionContextOp extends Op {

    ExecutionContextOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    ExecutionContextOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    public abstract void execute(ExecutionNode executionNode, ExecutionContext ectx);

}
