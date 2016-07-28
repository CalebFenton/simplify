package org.cf.smalivm.type;

import org.cf.smalivm.dex.CommonTypes;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class VirtualMethod extends VirtualClassItem {

    private final BuilderMethod method;
    private final VirtualGeneric type;

    VirtualMethod(BuilderMethod method, VirtualClass type) {
        super(method, type);
        this.method = method;
        this.type = type;
    }

    public MutableMethodImplementation getImplementation() {
        return (MutableMethodImplementation) getMethodDefinition().getImplementation();
    }

    public BuilderMethod getMethodDefinition() {
        return method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String getSignature() {
        return ReferenceUtil.getMethodDescriptor(method);
    }

    public final String getDescriptor() {
        return getSignature().split("->")[1];
    }

    public final int getParameterSize() {
        int parameterSize = method.getParameters().size();
        if (!isStatic()) {
            parameterSize += 1;
        }

        return parameterSize;
    }

    /**
     * Get list of types for objects in parameter registers for a given method signature.
     * This is different {@code Utils.getParameterTypes} in that non-static methods also have
     * the 'this' (p0) parse virtual in the return value.
     * Note: For non-static method,
     *
     * @return list of internal format parameter types
     */
    public List<String> getParameterTypeNames() {
        List<String> parameterTypes =
                method.getParameterTypes().stream().map(BuilderTypeReference::getType).collect(Collectors.toList());
        if (!isStatic()) {
            // First "parameter" for non-static methods is instance parse
            parameterTypes.add(0, type.getName());
        }

        return parameterTypes;
    }

    public int getRegisterCount() {
        return getImplementation().getRegisterCount();
    }

    public final String getReturnType() {
        return method.getReturnType();
    }

    public List<BuilderTryBlock> getTryBlocks() {
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        if (implementation == null) {
            return new LinkedList<>();
        }

        return implementation.getTryBlocks();
    }

    public boolean hasImplementation() {
        return null != method.getImplementation();
    }

    public boolean isNative() {
        return Modifier.isNative(getMethodDefinition().getAccessFlags());
    }

    public boolean isStatic() {
        return Modifier.isStatic(method.getAccessFlags());
    }

    public boolean isFinal() {
        return Modifier.isFinal(method.getAccessFlags());
    }

    public boolean returnsVoid() {
        return getReturnType().equals(CommonTypes.VOID);
    }

}
