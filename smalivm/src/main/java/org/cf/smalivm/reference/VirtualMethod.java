package org.cf.smalivm.reference;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;

public class VirtualMethod {

    private final String signature;
    private final String internalClassName;
    private final String binaryClassName;
    private final String sourceClassName;
    private final String descriptor;
    private final String methodName;
    private final List<String> parameterTypes;
    private final int parameterSize;
    private final boolean isStatic;
    private final String returnType;

    VirtualMethod(String methodSignature, boolean isStatic) {
        this.signature = methodSignature;

        int dashRocket = signature.indexOf("->", 2);
        internalClassName = signature.substring(0, dashRocket);
        binaryClassName = ClassNameUtils.internalToBinary(internalClassName);
        sourceClassName = ClassNameUtils.internalToSource(internalClassName);

        descriptor = signature.substring(dashRocket + 2);
        int openParen = descriptor.indexOf('(');
        methodName = descriptor.substring(0, openParen);
        returnType = descriptor.substring(descriptor.lastIndexOf(')') + 1);

        this.isStatic = isStatic;
        parameterTypes = parseParameterTypes(descriptor, internalClassName, isStatic);
        parameterSize = Utils.getRegisterSize(parameterTypes);
    }

    public final String getBinaryClassName() {
        return binaryClassName;
    }

    public final String getClassName() {
        return internalClassName;
    }

    public boolean returnsVoid() {
        return getReturnType().equals("V");
    }

    public final String getDescriptor() {
        return descriptor;
    }

    public final String getMethodName() {
        return methodName;
    }

    public final int getParameterSize() {
        return parameterSize;
    }

    /**
     * Get list of types for objects in parameter registers for a given method signature.
     * This is different {@code Utils.getParameterTypes} in that non-static methods also have
     * the 'this' (p0) reference type in the return value.
     * Note: For non-static method,
     * 
     * @return list of internal format parameter types
     */
    public final List<String> getParameterTypes() {
        return parameterTypes;
    }

    public final String getReturnType() {
        return returnType;
    }

    public final String getSignature() {
        return signature;
    }

    public final String getSourceClassName() {
        return sourceClassName;
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(551, 57).append(signature).hashCode();
    }

    public final boolean isStatic() {
        return isStatic;
    }

    @Override
    public String toString() {
        return getSignature();
    }

    private static List<String> parseParameterTypes(String descriptor, String className, boolean isStatic) {
        List<String> parameterTypes = new LinkedList<String>();
        if (!isStatic) {
            // First "parameter" for non-static methods is instance reference
            parameterTypes.add(0, className);
        }

        parameterTypes.addAll(Utils.getParameterTypes(descriptor));

        return parameterTypes;
    }

}
