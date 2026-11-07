package org.cf.smalivm.type;

import org.cf.smalivm.dex.CommonTypes;
import com.android.tools.smali.dexlib2.builder.BuilderTryBlock;
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation;
import com.android.tools.smali.dexlib2.formatter.DexFormatter;
import com.android.tools.smali.dexlib2.writer.builder.BuilderMethod;
import com.android.tools.smali.dexlib2.writer.builder.BuilderTypeReference;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class VirtualRealMethod extends VirtualMethod {

    private final BuilderMethod method;
    private final VirtualType type;

    VirtualRealMethod(BuilderMethod method, VirtualClass type) {
        super(method, type);
        this.method = method;
        this.type = type;
    }

    @Override
    public MutableMethodImplementation getImplementation() {
        return (MutableMethodImplementation) getMethodDefinition().getImplementation();
    }

    @Override
    public BuilderMethod getMethodDefinition() {
        return method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String getSignature() {
        return DexFormatter.INSTANCE.getMethodDescriptor(method);
    }

    @Override
    public final int getParameterSize() {
        int parameterSize = method.getParameters().size();
        if (!isStatic()) {
            parameterSize += 1;
        }

        return parameterSize;
    }

    /**
     * Get the types in parameter registers, including the receiver (p0) for non-static methods.
     *
     * @return list of internal format parameter types
     */
    @Override
    public List<String> getParameterTypeNames() {
        // TODO: this should be generated once and returned as immutable. same with getParameterTypes
        List<String> parameterTypes = method.getParameterTypes().stream().map(BuilderTypeReference::getType).collect(Collectors.toList());
        if (!isStatic()) {
            // First "parameter" for non-static methods is instance parse
            parameterTypes.add(0, type.getName());
        }

        return parameterTypes;
    }

    @Override
    public int getRegisterCount() {
        return getImplementation().getRegisterCount();
    }

    @Override
    public final String getReturnType() {
        return method.getReturnType();
    }

    @Override
    public List<BuilderTryBlock> getTryBlocks() {
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        if (implementation == null) {
            return new LinkedList<>();
        }

        return implementation.getTryBlocks();
    }

    @Override
    public boolean hasImplementation() {
        return null != method.getImplementation();
    }

    @Override
    public boolean isNative() {
        return Modifier.isNative(getMethodDefinition().getAccessFlags());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(method.getAccessFlags());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(method.getAccessFlags());
    }

    @Override
    public boolean returnsVoid() {
        return getReturnType().equals(CommonTypes.VOID);
    }

}
