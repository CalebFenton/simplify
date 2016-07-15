package org.cf.smalivm.type;

public abstract class Instance {

    private final VirtualGeneric type;

    protected Instance(VirtualGeneric type) {
        this.type = type;
    }

    public VirtualGeneric getType() {
        return type;
    }

    public String toString() {
        return getType().getName();
    }

}
