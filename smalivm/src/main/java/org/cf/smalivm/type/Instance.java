package org.cf.smalivm.type;

public abstract class Instance {

    private final VirtualType type;

    protected Instance(VirtualType type) {
        this.type = type;
    }

    public VirtualType getType() {
        return type;
    }

    public String toString() {
        return getType().getName();
    }

}
