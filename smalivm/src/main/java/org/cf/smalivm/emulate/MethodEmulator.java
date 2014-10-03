package org.cf.smalivm.emulate;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodEmulator {

    private static final Logger log = LoggerFactory.getLogger(MethodEmulator.class.getSimpleName());

    private static Map<String, EmulatedMethod> emulatedMethods = new HashMap<String, EmulatedMethod>();
    static {
        addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", new org_cf_simplify_Utils_breakpoint());
        addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", new java_lang_Class_forName());
        addMethod("Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        new java_lang_Class_getMethod());
    }

    public static void addMethod(String methodDescriptor, EmulatedMethod method) {
        emulatedMethods.put(methodDescriptor, method);
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static void clearMethods() {
        emulatedMethods.clear();
    }

    public static SideEffect.Level emulate(MethodState mState, String methodDescriptor, int[] parameterRegisters) {
        EmulatedMethod em = emulatedMethods.get(methodDescriptor);
        try {
            em.execute(mState);
        } catch (Exception e) {
            // TODO: try/catch handling :D
            if (log.isWarnEnabled()) {
                log.warn("Exception while emulating method " + methodDescriptor, e);
            }
        }

        return em.getSideEffectLevel();
    }
}
