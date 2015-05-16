package org.cf.smalivm.emulate;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualException;
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

public class java_lang_Class_getMethod implements MethodStateMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getMethod.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/reflect/Method;";

    private final Set<VirtualException> exceptions;

    java_lang_Class_getMethod() {
        exceptions = new HashSet<VirtualException>();
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    @Override
    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        HeapItem classItem = mState.peekParameter(0);
        Object classValue = classItem.getValue();
        String methodName = (String) mState.peekParameter(1).getValue();
        Object[] parameterTypesValue = (Object[]) mState.peekParameter(2).getValue();

        Object methodValue = null;
        if (classValue instanceof Class<?>) {
            // It's a real class. Try and return a real method.
            Class<?>[] parameterTypes = castToClassArray(parameterTypesValue);
            try {
                methodValue = getNonLocalMethod((Class<?>) classValue, methodName, parameterTypes);
            } catch (NoSuchMethodException | SecurityException e) {
                // Assuming Android doesn't have this method since our JVM doesn't.
                setException(new VirtualException(e));
                return;
            }
        } else if (classValue instanceof LocalClass) {
            LocalClass localClass = (LocalClass) classValue;
            methodValue = getLocalMethod(vm.getClassManager(), localClass, methodName, parameterTypesValue);
            if (methodValue == null || "<init>".equals(methodName) || "<clinit>".equals(methodName)) {
                StringBuilder sb = new StringBuilder();
                sb.append(SmaliClassUtils.smaliClassToJava(localClass.getName()));
                sb.append('.').append(methodName).append("()");
                setException(new VirtualException(NoSuchMethodException.class, sb.toString()));
                return;
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Class.getMethod with {} has unexpected type and confuses me.", classItem);
            }
            methodValue = new UnknownValue();
        }

        mState.assignReturnRegister(methodValue, RETURN_TYPE);
    }

    private void setException(VirtualException exception) {
        exceptions.add(exception);
    }

    private static LocalMethod getLocalMethod(SmaliClassManager classManager, LocalClass localClass, String methodName,
                    Object[] parameterTypesValue) {
        String className = localClass.getName();
        if (!classManager.isLocalClass(className)) {
            return null;
        }

        // Only have the method signature, because the return type is unknown. Local methods are
        // indexed by method descriptor since it's usually available.
        String targetMethodSignature = buildMethodSignature(className, methodName, parameterTypesValue);
        Set<String> methodDescriptors = classManager.getMethodDescriptors(className);
        for (String methodDescriptor : methodDescriptors) {
            if (methodDescriptor.startsWith(targetMethodSignature)) {
                return new LocalMethod(methodDescriptor);
            }
        }

        return null;
    }

    private static String buildMethodSignature(String className, String methodName, Object[] parameterTypes) {
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

    private static Method getNonLocalMethod(Class<?> klazz, String methodName, Class<?>[] parameterTypes)
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

    @Override
    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
