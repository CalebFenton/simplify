package org.cf.smalivm.opcode;

import java.util.List;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;

public abstract class Op {

    private int address;
    private int[] childAddresses;
    private final String opName;
    private VirtualException[] exceptions;

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

    public final int[] getChildren() {
        return childAddresses;
    }

    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.NONE;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setChildren(int... childAddresses) {
        this.childAddresses = childAddresses;
    }

    public void setNoChildren() {
        setChildren();
    }

    public void setNoExceptions() {
        setExceptions();
    }

    public boolean mayThrowException() {
        return exceptions != null && exceptions.length > 0;
    }

    public void setExceptions(VirtualException... exceptions) {
        this.exceptions = exceptions;
    }

    public void setExceptions(List<VirtualException> exceptions) {
        this.exceptions = exceptions.toArray(new VirtualException[exceptions.size()]);
    }

    public void setException(VirtualException exception) {
        exceptions = new VirtualException[] { exception };
    }

    public VirtualException[] getExceptions() {
        return exceptions;
    }

    @Override
    public abstract String toString();

}
