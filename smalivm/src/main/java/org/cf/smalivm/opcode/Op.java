package org.cf.smalivm.opcode;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;

public abstract class Op {

    private int address;
    private int[] childAddresses;
    private final String opName;
    private VirtualException thrownException;

    Op(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    Op(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
        thrownException = null;
    }

    public final int getAddress() {
        return address;
    }

    public final String getName() {
        return opName;
    }

    public final int[] getChildren() {
        return childAddresses;
    }

    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.NONE;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setChildren(int[] childAddresses) {
        this.childAddresses = childAddresses;
    }

    public VirtualException thrownException() {
        return thrownException;
    }

    protected void setException(VirtualException thrownException) {
        this.thrownException = thrownException;
    }

    @Override
    public abstract String toString();

}
