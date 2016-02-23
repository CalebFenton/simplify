package org.cf.smalivm;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.reference.VirtualMethod;
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

    private final VirtualMethod virtualMethod;

    public MethodReflector(VirtualMachine vm, VirtualMethod virtualMethod) {
        this.virtualMethod = virtualMethod;

    }

    public void reflect(MethodState mState) {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting {} with context:\n{}", virtualMethod, mState);
        }

        Object returnValue = null;
        try {
            returnValue = invoke(mState);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect {}: {}", virtualMethod, e.getMessage());
            }

            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }

            returnValue = new UnknownValue();
        }

        if (!virtualMethod.returnsVoid()) {
            HeapItem returnItem = new HeapItem(returnValue, virtualMethod.getReturnType());
            mState.assignReturnRegister(returnItem);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MethodReflector{");
        sb.append(virtualMethod).append(", static=").append(virtualMethod.isStatic()).append("}");

        return sb.toString();
    }

    private InvocationArguments getArguments(MethodState mState) throws ClassNotFoundException {
        int offset = 0;
        if (!virtualMethod.isStatic()) {
            // First element in context will be instance reference if non-static method.
            offset = 1;
        }

        List<String> parameterTypeNames = virtualMethod.getParameterTypes();
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

    private Object invoke(MethodState mState) throws ClassNotFoundException, NoSuchMethodException,
                    IllegalAccessException, InvocationTargetException, InstantiationException {
        Object returnValue;
        Class<?> klazz = Class.forName(virtualMethod.getBinaryClassName());
        InvocationArguments invocationArgs = getArguments(mState);
        Object[] args = invocationArgs.getArgs();
        Class<?>[] parameterTypes = invocationArgs.getParameterTypes();
        if (virtualMethod.isStatic()) {
            if (log.isDebugEnabled()) {
                log.debug("Reflecting {}, clazz={} args={}", virtualMethod, klazz, Arrays.toString(args));
            }
            returnValue = MethodUtils.invokeStaticMethod(klazz, virtualMethod.getMethodName(), args, parameterTypes);
        } else {
            if ("<init>".equals(virtualMethod.getMethodName())) {
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, class={} args={}", virtualMethod, klazz, Arrays.toString(args));
                }
                returnValue = ConstructorUtils.invokeConstructor(klazz, args);
                mState.assignParameter(0, new HeapItem(returnValue, virtualMethod.getClassName()));
            } else {
                HeapItem targetItem = mState.peekRegister(0);
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, target={} args={}", virtualMethod, targetItem, Arrays.toString(args));
                }
                returnValue = MethodUtils.invokeMethod(targetItem.getValue(), virtualMethod.getMethodName(), args,
                                parameterTypes);
            }
        }

        return returnValue;
    }

}
