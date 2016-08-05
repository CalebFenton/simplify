package org.cf.smalivm;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class MethodReflector {

    private static Logger log = LoggerFactory.getLogger(MethodReflector.class.getSimpleName());
    private final VirtualMethod method;

    public MethodReflector(VirtualMachine vm, VirtualMethod method) {
        this.method = method;
    }

    public void reflect(MethodState mState) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting {} with context:\n{}", method, mState);
        }

        Object returnValue;
        try {
            returnValue = invoke(mState);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            //            e.printStackTrace();
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect {}: {}", method, e.getMessage());
            }
            if (log.isDebugEnabled()) {
                log.debug("Stack trace:", e);
            }
            throw e;
        }

        if (!method.returnsVoid()) {
            HeapItem returnItem = new HeapItem(returnValue, method.getReturnType());
            mState.assignReturnRegister(returnItem);
        }
    }

    @Override
    public String toString() {
        return "MethodReflector{" + method + ", static=" + method.isStatic() + "}";
    }

    private InvocationArguments getArguments(MethodState mState) throws ClassNotFoundException {
        int paramOffset = 0;
        if (!method.isStatic()) {
            // First parameter in method state for non-static methods is the virtual instance for that method
            // It's not needed to reflect the actual JVM method
            paramOffset = 1;
        }

        List<String> parameterTypeNames = method.getParameterTypeNames();
        int size = parameterTypeNames.size() - paramOffset;
        Object[] args = new Object[size];
        Class<?>[] parameterTypes = new Class<?>[size];
        int registerCount = mState.getRegisterCount();
        for (int i = paramOffset; i < registerCount; ) {
            HeapItem argItem = mState.peekParameter(i);
            args[i - paramOffset] = argItem.getValue();

            String parameterTypeName = parameterTypeNames.get(i);
            Class<?> parameterType;
            if (argItem.isPrimitive()) {
                parameterType = ClassNameUtils.getPrimitiveClass(parameterTypeName);
            } else {
                // Shouldn't need a VM class loader since these are all safe to reflect on the JVM
                // Also, some classes are arrays and loadClass only works for non-array, non-primitive types
                parameterType = Class.forName(ClassNameUtils.internalToBinary(parameterTypeName));
            }
            parameterTypes[i - paramOffset] = parameterType;

            // Long tried every diet but is still fat and takes 2 registers. Could be thyroid.
            i += Utils.getRegisterSize(parameterTypeName);
        }

        return new InvocationArguments(args, parameterTypes);
    }

    private Object invoke(MethodState mState) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object returnValue;
        Class<?> klazz = Class.forName(method.getBinaryClassName());
        InvocationArguments invocationArgs = getArguments(mState);
        Object[] args = invocationArgs.getArgs();
        Class<?>[] parameterTypes = invocationArgs.getParameterTypes();
        if (method.isStatic()) {
            if (log.isDebugEnabled()) {
                log.debug("Reflecting {}, clazz={} args={}", method, klazz, Arrays.toString(args));
            }
            returnValue = MethodUtils.invokeStaticMethod(klazz, method.getName(), args, parameterTypes);
        } else {
            if ("<init>".equals(method.getName())) {
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, class={} args={}", method, klazz, Arrays.toString(args));
                }
                returnValue = ConstructorUtils.invokeConstructor(klazz, args);
                mState.assignParameter(0, new HeapItem(returnValue, method.getClassName()));
            } else {
                HeapItem targetItem = mState.peekRegister(0);
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting {}, target={} args={}", method, targetItem, Arrays.toString(args));
                }
                Object value = targetItem.getValue();
                returnValue = MethodUtils.invokeMethod(value, method.getName(), args, parameterTypes);
            }
        }

        return returnValue;
    }

    private static class InvocationArguments {

        private Object[] args;
        private Class<?>[] parameterTypes;

        InvocationArguments(Object[] args, Class<?>[] parameterTypes) {
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

}
