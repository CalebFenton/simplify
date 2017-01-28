package org.cf.smalivm.type;

import org.cf.smalivm.dex.CommonTypes;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VirtualArrayMethod extends VirtualMethod {

    private final Reference reference;
    private final VirtualType type;

    VirtualArrayMethod(Reference reference, VirtualType type) {
        super(reference, type);
        this.reference = reference;
        this.type = type;
    }

    @Override
    public MutableMethodImplementation getImplementation() {
        return null;
    }

    @Override
    public BuilderMethod getMethodDefinition() {
        return null;
    }

    @Override
    public final int getParameterSize() {
        return 1;
    }

    @Override
    public List<String> getParameterTypeNames() {
        List<String> parameterTypes = new LinkedList<>();
        parameterTypes.add(0, type.getName());

        return parameterTypes;
    }

    @Override
    public int getRegisterCount() {
        return 0;
    }

    @Override
    public final String getReturnType() {
        return CommonTypes.OBJECT;
    }

    @Override
    public List<BuilderTryBlock> getTryBlocks() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasImplementation() {
        return false;
    }

    @Override
    public boolean isNative() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public boolean returnsVoid() {
        return false;
    }

    @Override
    public String getName() {
        String signature = getSignature();
        int dashRocket = signature.indexOf('>');
        int firstParen = signature.indexOf('(');

        return signature.substring(dashRocket + 1, firstParen);
    }

    @Override
    public String getSignature() {
        return reference.toString();
    }

}
