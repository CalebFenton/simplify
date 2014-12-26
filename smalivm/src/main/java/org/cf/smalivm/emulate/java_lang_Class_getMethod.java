package org.cf.smalivm.emulate;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getMethod implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getMethod.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/reflect/Method;";

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        HeapItem classItem = mState.peekParameter(0);
        Object classValue = classItem.getValue();
        String methodName = (String) mState.peekParameter(1).getValue();
        Object[] parameterTypesValue = (Object[]) mState.peekParameter(2).getValue();

        Object methodValue;
        if (classValue instanceof Class<?>) {
            // It's a real class. Try and return a real method.
            Class<?>[] parameterTypes = castToClassArray(parameterTypesValue);
            try {
                methodValue = getNonLocalMethod((Class<?>) classValue, methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException e) {
                // Assuming Android doesn't have this method since our JVM doesn't.
                mState.assignReturnRegister(HeapItem.newUnknown(RETURN_TYPE));
                // TODO: fill with convincing stacktrace
                throw e;
            }
        } else if (classValue instanceof LocalClass) {
            LocalClass localClass = (LocalClass) classValue;
            methodValue = getLocalMethod(vm, localClass, methodName, parameterTypesValue);
            if ((methodValue instanceof UnknownValue) || "<init>".equals(methodName) || "<clinit>".equals(methodName)) {
                mState.assignReturnRegister(HeapItem.newUnknown(RETURN_TYPE));
                // TODO: fill with convincing lies
                throw new NoSuchMethodException(methodName);
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Class.getMethod with " + classItem + " has unexpected type and confuses me.");
            }
            methodValue = new UnknownValue();
        }

        mState.assignReturnRegister(methodValue, RETURN_TYPE);
    }

    private Object getLocalMethod(VirtualMachine vm, LocalClass localClass, String methodName,
                    Object[] parameterTypesValue) {
        SmaliClassManager classManager = vm.getClassManager();
        String className = localClass.getName();
        if (!classManager.isLocalClass(className)) {
            return new UnknownValue();
        }

        String targetMethodSignature = buildMethodSignature(className, methodName, parameterTypesValue);
        Set<String> methodDescriptors = vm.getClassManager().getMethodDescriptors(className);
        for (String methodDescriptor : methodDescriptors) {
            if (methodDescriptor.startsWith(targetMethodSignature)) {
                return new LocalMethod(methodDescriptor);
            }
        }

        return new UnknownValue();
    }

    private String buildMethodSignature(String className, String methodName, Object[] parameterTypes) {
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodName).append('(');
        if (null != parameterTypes) {
            for (Object parameterType : parameterTypes) {
                String smaliClass;
                if (parameterType instanceof LocalInstance) {
                    smaliClass = ((LocalInstance) parameterType).getName();
                } else {
                    smaliClass = SmaliClassUtils.javaClassToSmali((Class<?>) parameterType);
                }
                sb.append(smaliClass);
            }
        }
        sb.append(')');

        return sb.toString();
    }

    private Method getNonLocalMethod(Class<?> klazz, String methodName, Class<?>[] parameterTypes)
                    throws NoSuchMethodException, SecurityException {
        return klazz.getMethod(methodName, parameterTypes);
    }

    private static Class<?>[] castToClassArray(Object objectArr) {
        int arrayLength = objectArr == null ? 0 : Array.getLength(objectArr);
        Class<?>[] classArray = new Class<?>[arrayLength];
        for (int i = 0; i < classArray.length; i++) {
            classArray[i] = (Class<?>) Array.get(objectArr, i);
        }

        return classArray;
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
