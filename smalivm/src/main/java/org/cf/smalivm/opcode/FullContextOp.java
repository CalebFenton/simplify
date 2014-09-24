package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ClassContextMap;
import org.cf.smalivm.context.MethodContext;

public abstract class FullContextOp extends Op {

    FullContextOp(int address, String opName, int childAddress) {
        super(address, opName, childAddress);
    }

    FullContextOp(int address, String opName, int[] childAddresses) {
        super(address, opName, childAddresses);
    }

    public abstract int[] execute(MethodContext mctx, ClassContextMap classContextMap);

}
