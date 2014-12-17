package org.cf.smalivm.emulate;

import java.lang.reflect.Method;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getMethod implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getMethod.class.getSimpleName());

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        Object classValue = mState.peekParameter(0);
        String methodName = (String) mState.peekParameter(1);
        Object parameterTypesValue = mState.peekParameter(2);
        Class<?>[] parameterTypes = Utils.castToClassArray(parameterTypesValue);

        Object methodValue;
        if (classValue instanceof Class<?>) {
            try {
                methodValue = getNonLocalMethod((Class<?>) classValue, methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException e) {
                methodValue = new UnknownValue("Ljava/lang/reflect/Method;");
            }
        } else if (classValue instanceof LocalClass) {
            LocalClass localClass = (LocalClass) classValue;
            methodValue = getLocalMethod(vm, localClass, methodName, parameterTypes);
            if (null == methodValue) {
                methodValue = new UnknownValue("Ljava/lang/reflect/Method;");
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Class.getMethod with " + classValue + " has unexpected type and confuses me.");
            }
            methodValue = new UnknownValue("Ljava/lang/reflect/Method;");
        }

        mState.assignReturnRegister(methodValue);
    }

    private LocalMethod getLocalMethod(VirtualMachine vm, LocalClass localClass, String methodName,
                    Class<?>[] parameterTypes) {
        Set<String> methodDescriptors = vm.getClassManager().getMethodDescriptors(localClass.getName());
        String methodSignature = buildMethodSignature(localClass.getName(), methodName, parameterTypes);
        for (String methodDescriptor : methodDescriptors) {
            if (methodDescriptor.startsWith(methodSignature)) {
                return new LocalMethod(methodDescriptor);
            }
        }

        return null;
    }

    private String buildMethodSignature(String className, String methodName, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodName).append('(');
        for (Class<?> parameterType : parameterTypes) {
            String smaliClass = SmaliClassUtils.javaClassToSmali(parameterType);
            sb.append(smaliClass);
        }
        sb.append(')');

        return sb.toString();
    }

    private Method getNonLocalMethod(Class<?> klazz, String methodName, Class<?>[] parameterTypes)
                    throws NoSuchMethodException, SecurityException {
        return klazz.getMethod(methodName, parameterTypes);
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
