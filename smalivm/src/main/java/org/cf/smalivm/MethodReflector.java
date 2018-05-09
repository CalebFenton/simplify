package org.cf.smalivm;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.cf.util.EnumAnalyzer;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class MethodReflector {

    private static Logger log = LoggerFactory.getLogger(MethodReflector.class.getSimpleName());

    private static final String ENUM_INIT_SIGNATURE = "Ljava/lang/Enum;-><init>(Ljava/lang/String;";

    private final VirtualMachine vm;
    private final VirtualMethod method;
    private final EnumAnalyzer enumAnalyzer;

    public MethodReflector(VirtualMachine vm, VirtualMethod method) {
        this.vm = vm;
        this.method = method;
        enumAnalyzer = new EnumAnalyzer(vm);
    }

    public void reflect(MethodState mState) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Reflecting {} with context:\n{}", method, mState);
        }

        Object returnValue;
        try {
            returnValue = invoke(mState);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException |
                IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            //            e.printStackTrace();
            if (log.isWarnEnabled()) {
                log.warn("Failed to reflect {}: ", method, e);
            }
            if (log.isDebugEnabled()) {
                log.debug("Stack trace: ", e);
            }
            throw e;
        }

        if (!method.returnsVoid()) {
            HeapItem returnItem;
            if (returnValue != null && !ClassNameUtils.isPrimitive(method.getReturnType())) {
                returnItem = new HeapItem(returnValue, ClassNameUtils.toInternal(returnValue.getClass()));
            } else {
                returnItem = new HeapItem(returnValue, method.getReturnType());
            }
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

    @SuppressWarnings({ "unchecked" })
    private Object invoke(MethodState mState) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        Object returnValue;
        Class<?> klazz = Class.forName(method.getBinaryClassName());
        InvocationArguments invocationArgs = getArguments(mState);
        Object[] args = invocationArgs.getArgs();
        Class<?>[] parameterTypes = invocationArgs.getParameterTypes();
        if (method.isStatic()) {
            if (log.isDebugEnabled()) {
                log.debug("Reflecting static {}, clazz={} args={}", method, klazz, Arrays.toString(args));
            }
            returnValue = MethodUtils.invokeStaticMethod(klazz, method.getName(), args, parameterTypes);
        } else {
            if ("<init>".equals(method.getName())) {
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting construction {}, class={} args={}", method, klazz, Arrays.toString(args));
                }
                if (method.getSignature().startsWith(ENUM_INIT_SIGNATURE)) {
                    returnValue = invokeEnumInit(mState, (String) args[0], vm.getClassLoader());
                } else {
                    returnValue = ConstructorUtils.invokeConstructor(klazz, args);
                }
                HeapItem instanceItem = new HeapItem(returnValue, ClassNameUtils.toInternal(returnValue.getClass()));
                mState.assignParameter(0, instanceItem);
            } else {
                HeapItem targetItem = mState.peekRegister(0);
                if (log.isDebugEnabled()) {
                    log.debug("Reflecting virtual {}, target={} args={}", method, targetItem, Arrays.toString(args));
                }
                Object value = targetItem.getValue();
                returnValue = MethodUtils.invokeMethod(value, method.getName(), args, parameterTypes);
            }
        }

        return returnValue;
    }

    @SuppressWarnings({ "unchecked" })
    private Object invokeEnumInit(MethodState mState, String name, ClassLoader classLoader) throws ClassNotFoundException {
        /*
         * Enums can't be instantiated by calling newInstance() on the constructor,
         * even with setAccessible(true). It fails with InstantiationException.
         * http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.9
         */
        HeapItem instance = mState.peekParameter(mState.getParameterStart());
        String enumType = ClassNameUtils.internalToSource(instance.getType());

        Class<? extends Enum> enumClass = (Class<? extends Enum>) classLoader.loadClass(enumType);
        try {
            return Enum.valueOf(enumClass, name);
        } catch (IllegalArgumentException e) {
            enumAnalyzer.analyze(enumClass);
            name = enumAnalyzer.getObfuscatedName(name);
            return Enum.valueOf(enumClass, name);
        }
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
