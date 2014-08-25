package org.cf.smalivm.op_handler;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodContext;

public abstract class Op {

    private final int address;
    private final String opName;
    private final int[] childAddresses;

    Op(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    Op(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
    }

    public abstract int[] execute(MethodContext mctx);

    @Override
    public abstract String toString();

    public final int getAddress() {
        return address;
    }

    public final String getOpName() {
        return opName;
    }

    public final int[] getPossibleChildren() {
        return childAddresses;
    }

    public SideEffect.Type sideEffectType() {
        return SideEffect.Type.NONE;
    }

}
