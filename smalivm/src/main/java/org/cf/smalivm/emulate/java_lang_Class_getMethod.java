package org.cf.smalivm.emulate;

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

        Class<?> instance = (Class<?>) param0;
        String methodName = (String) mState.peekParameter(1);
        String className = SmaliClassUtils.javaClassToSmali(instance);

        Class<?>[] methodParams = (Class<?>[]) mState.peekParameter(2);
        Object method = null;
        try {
            method = instance.getMethod(methodName, methodParams);
            mState.assignReturnRegister(method);
        } catch (NoSuchMethodException | SecurityException e) {
            method = new UnknownValue("Ljava/lang/Reflect/Method;");
            mState.assignReturnRegister(method);
            throw e;
        }
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}
