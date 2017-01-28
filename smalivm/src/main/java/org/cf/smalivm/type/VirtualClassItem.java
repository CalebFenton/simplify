package org.cf.smalivm.type;

import org.jf.dexlib2.iface.reference.Reference;

public abstract class VirtualClassItem {

    private final Reference reference;
    private final VirtualType definingClass;

    protected VirtualClassItem(Reference reference, VirtualType definingClass) {
        this.reference = reference;
        this.definingClass = definingClass;
    }

    public final String getBinaryClassName() {
        return getDefiningClass().getBinaryName();
    }

    public final String getClassName() {
        return getDefiningClass().getName();
    }

    public VirtualType getDefiningClass() {
        return definingClass;
    }

    public abstract String getName();

    public abstract String getSignature();

    public final String getSourceClassName() {
        return getDefiningClass().getSourceName();
    }

    @Override
    public String toString() {
        return getSignature();
    }


}
