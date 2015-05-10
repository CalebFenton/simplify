package org.cf.smalivm.opcode;

import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.SideEffect;

public abstract class Op {

    // These should be final, but when graphs are modified, these values need to change.
    private int address;
    private int[] childAddresses;
    private String opName;
    private List<String> exceptionNames;

    Op(int address, String opName, int childAddress) {
        this(address, opName, new int[] { childAddress });
    }

    Op(int address, String opName, int[] childAddresses) {
        this.address = address;
        this.opName = opName;
        this.childAddresses = childAddresses;
        exceptionNames = new LinkedList<String>();
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

    public void setExceptionNames(List<String> exceptionNames) {
        this.exceptionNames = exceptionNames;
    }

    public List<String> getExceptionNames() {
        return exceptionNames;
    }

    @Override
    public abstract String toString();

}
