package org.cf.smalivm.reference;

import org.jf.dexlib2.writer.builder.BuilderMethod;

public class MethodFactory {

    public LocalMethod build(BuilderMethod method) {
        return new LocalMethod(method);
    }

    public VirtualMethod build(String methodSignature, boolean isStatic) {
        return new VirtualMethod(methodSignature, isStatic);
    }

}
