package org.cf.smalivm.emulate;

import org.cf.smalivm.MethodReflector;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Class_getMethod implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Class_getMethod.class.getSimpleName());

    public void execute(MethodState mState) throws Exception {
        Object param0 = mState.peekParameter(0);
        if (!(param0 instanceof Class<?>)) {
            // LocalInstance?
            log.warn("Emulated Class.getMethod of " + param0.getClass() + " not supported");
        }

        Class<?> instance = (Class<?>) mState.peekParameter(0);
        String className = SmaliClassUtils.javaClassToSmali(instance);
        if (!MethodReflector.isSafe(className)) {
            mState.assignReturnRegister(new UnknownValue("Ljava/lang/Class;"));
            return;
        }

        Class<?>[] parameterTypes = new Class<?>[mState.getParameterCount() - 1];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterTypes[i] = (Class<?>) mState.peekParameter(i);
        }

        java.lang.reflect.Method result = instance.getMethod(className, parameterTypes);
        System.out.println("return: " + result);
        mState.assignReturnRegister(result);
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
