package org.cf.smalivm.type;

import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import java.util.List;

public abstract class VirtualMethod extends VirtualClassItem {

    VirtualMethod(Reference reference, VirtualType type) {
        super(reference, type);
    }

    public abstract MutableMethodImplementation getImplementation();

    public abstract BuilderMethod getMethodDefinition();

    public final String getDescriptor() {
        return getSignature().split("->")[1];
    }

    public abstract int getParameterSize();

    public abstract List<String> getParameterTypeNames();

    public abstract int getRegisterCount();

    public abstract String getReturnType();

    public abstract List<BuilderTryBlock> getTryBlocks();

    public abstract boolean hasImplementation();

    public abstract boolean isNative();

    public abstract boolean isStatic();

    public abstract boolean isFinal();

    public abstract boolean returnsVoid();

}
