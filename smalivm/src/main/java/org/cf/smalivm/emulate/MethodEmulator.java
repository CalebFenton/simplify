package org.cf.smalivm.emulate;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodEmulator {

    private static final Logger log = LoggerFactory.getLogger(MethodEmulator.class.getSimpleName());

    private static Map<String, EmulatedMethod> emulatedMethods = new HashMap<String, EmulatedMethod>();
    static {
        addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", new org_cf_simplify_Utils_breakpoint());
        addMethod("Ljava/lang/Class;->getPackage()Ljava/lang/Package;", new java_lang_Class_getPackage());
        addMethod("Ljava/lang/Package;->getName()Ljava/lang/String;", new java_lang_Package_getName());
        addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", new java_lang_Class_forName());
        addMethod("Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        new java_lang_Class_getMethod());
        // For now, they work identically.
        addMethod("Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        new java_lang_Class_getMethod());
    }

    public static void addMethod(String methodDescriptor, EmulatedMethod method) {
        emulatedMethods.put(methodDescriptor, method);
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static boolean canHandleUnknownValues(String methodDescriptor) {
        EmulatedMethod method = emulatedMethods.get(methodDescriptor);

        return (method != null) && (method instanceof UnknownValuesMethod);
    }

    public static void clearMethods() {
        emulatedMethods.clear();
    }

    public static SideEffect.Level emulate(VirtualMachine vm, MethodState mState, String methodDescriptor,
                    int[] parameterRegisters) {
        EmulatedMethod em = emulatedMethods.get(methodDescriptor);
        try {
            em.execute(vm, mState);
        } catch (Exception e) {
            // TODO: exception handling
            if (log.isWarnEnabled()) {
                log.warn("Exception emulating method " + methodDescriptor, e);
            }
        }

        return em.getSideEffectLevel();
    }
}
