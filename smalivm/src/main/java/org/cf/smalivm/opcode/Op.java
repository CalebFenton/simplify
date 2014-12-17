package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;

public abstract class Op {

    private int address;
    private int[] childAddresses;
    private final String opName;

    Op(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    Op(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
    }

    public final int getAddress() {
        return address;
    }

    public final String getName() {
        return opName;
    }

    public final int[] getPossibleChildren() {
        return childAddresses;
    }

    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.NONE;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setPossibleChildren(int[] childAddresses) {
        this.childAddresses = childAddresses;
    }

    @Override
    public abstract String toString();

}
