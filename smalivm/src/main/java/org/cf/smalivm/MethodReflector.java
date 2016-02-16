package org.cf.smalivm;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodReflector {

    private static Logger log = LoggerFactory.getLogger(MethodReflector.class.getSimpleName());

    private class InvocationArguments {

        private Object[] args;
        private Class<?>[] parameterTypes;

        public InvocationArguments(Object[] args, Class<?>[] parameterTypes) {
            this.args = args;
            this.parameterTypes = parameterTypes;
        }

        public Object[] getArgs() {
            return args;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

    }

    private final String classNameInternal;
    private final String classNameBinary;
    private final boolean isStatic;
    private final String methodDescriptor;
    private final String methodName;
    private final List<String> parameterTypeNames;
    private final String returnType;

    public MethodReflector(VirtualMachine vm, String methodDescriptor, String returnType, List<String> parameterTypes,
                    boolean isStatic) {
        this.methodDescriptor = methodDescriptor;
        this.returnType = returnType;
        this.parameterTypeNames = parameterTypes;
        this.isStatic = isStatic;

        String[] parts = methodDescriptor.split("->");
        classNameInternal = parts[0];
        classNameBinary = classNameInternal.replaceAll("/", ".").substring(1, classNameInternal.length() - 1);
        methodName = parts[1].substring(0, parts[1].indexOf("("));
    }

    public void reflect(MethodState calleeContext) {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting {} with context:\n{}", methodDescriptor, calleeContext);
        }

        Object resultValue = null;
        try {
            // TODO: easy - add tests for array class method reflecting
            Class<?> klazz = Class.forName(classNameBinary);
            InvocationArguments invocationArgs = getArguments(calleeContext);
            Object[] args = invocationArgs.getArgs();
            Class<?>[] parameterTypes = invocationArgs.getParameterTypes();
            if (isStatic) {
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, clazz={} args={}", methodDescriptor, klazz, Arrays.toString(args));
                }
                resultValue = MethodUtils.invokeStaticMethod(klazz, methodName, args, parameterTypes);
            } else {
                if ("<init>".equals(methodName)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting {}, class={} args={}", methodDescriptor, klazz, Arrays.toString(args));
                    }
                    resultValue = ConstructorUtils.invokeConstructor(klazz, args);
                    calleeContext.assignParameter(0, new HeapItem(resultValue, classNameInternal));
                } else {
                    HeapItem targetItem = calleeContext.peekRegister(0);
                    if (log.isDebugEnabled()) {
                        log.debug("Reflecting {}, target={} args={}", methodDescriptor, targetItem,
                                        Arrays.toString(args));
                    }
                    resultValue = MethodUtils.invokeMethod(targetItem.getValue(), methodName, args, parameterTypes);
                }
            }
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            resultValue = new UnknownValue();
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect {}: {}", methodDescriptor, e.getMessage());
            }

            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
        }

        if (!"V".equals(returnType)) {
            HeapItem resultItem = new HeapItem(resultValue, returnType);
            calleeContext.assignReturnRegister(resultItem);
        }
    }

    private InvocationArguments getArguments(MethodState mState) throws ClassNotFoundException {
        int offset = 0;
        if (!isStatic) {
            // First element in context will be instance reference if non-static method.
            offset = 1;
        }

        int size = parameterTypeNames.size() - offset;
        Object[] args = new Object[size];
        Class<?>[] parameterTypes = new Class<?>[size];
        for (int i = offset; i < mState.getRegisterCount(); i++) {
            HeapItem argItem = mState.peekParameter(i);
            args[i - offset] = argItem.getValue();

            String parameterTypeName = parameterTypeNames.get(i);
            Class<?> parameterType;
            if (argItem.isPrimitive()) {
                parameterType = ClassNameUtils.getPrimitiveClass(parameterTypeName);
            } else {
                // Shouldn't need a VM class loader since these are all safe to reflect on the JVM
                // Also, some type names are arrays and loadClass only works for component types
                parameterType = Class.forName(ClassNameUtils.internalToBinary(parameterTypeName));
            }
            parameterTypes[i - offset] = parameterType;

            if (Utils.getRegisterSize(parameterTypeName) == 2) {
                // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
                i++;
            }
        }

        return new InvocationArguments(args, parameterTypes);
    }

}
