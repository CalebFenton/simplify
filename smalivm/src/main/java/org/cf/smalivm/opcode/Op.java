package org.cf.smalivm.opcode;

import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;

public abstract class Op {

    // These should be final, but when graphs are modified, these values need to change.
    private int address;
    private int[] childAddresses;

    private final String opName;
    private final List<VirtualException> exceptions;

    Op(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    Op(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
        exceptions = new LinkedList<VirtualException>();
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

    public void setExceptions(List<VirtualException> exceptions) {
        this.exceptions.clear();
        this.exceptions.addAll(exceptions);
    }

    public List<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public abstract String toString();

}
