package org.cf.smalivm.reference;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;

public class LocalMethod extends VirtualMethod {

    private final BuilderMethod method;

    LocalMethod(BuilderMethod method) {
        super(ReferenceUtil.getMethodDescriptor(method), Modifier.isStatic(method.getAccessFlags()));
        this.method = method;
    }

    public MutableMethodImplementation getImplementation() {
        return (MutableMethodImplementation) getMethodDefinition().getImplementation();
    }

    public BuilderMethod getMethodDefinition() {
        return method;
    }

    public int getRegisterCount() {
        return getImplementation().getRegisterCount();
    }

    public List<BuilderTryBlock> getTryBlocks() {
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        if (implementation == null) {
            return new LinkedList<BuilderTryBlock>();
        }

        return implementation.getTryBlocks();
    }

    public boolean hasImplementation() {
        return null != method.getImplementation();
    }

    public boolean isNative() {
        return Modifier.isNative(getMethodDefinition().getAccessFlags());
    }

}
