package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodContext;

public abstract class MethodContextOp extends Op {

    MethodContextOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    MethodContextOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    public abstract int[] execute(MethodContext mctx);

}
