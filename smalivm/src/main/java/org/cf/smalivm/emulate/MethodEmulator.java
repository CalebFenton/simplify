package org.cf.smalivm.emulate;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodContext;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods = new HashMap<String, EmulatedMethod>();
    static {
        addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", new org_cf_simplify_Utils_breakpoint());
    }

    public static void clearMethods() {
        emulatedMethods.clear();
    }

    public static void addMethod(String methodDescriptor, EmulatedMethod method) {
        emulatedMethods.put(methodDescriptor, method);
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static SideEffect.Type emulate(MethodContext mctx, String methodDescriptor, int[] parameterRegisters) {
        EmulatedMethod em = emulatedMethods.get(methodDescriptor);
        em.execute(mctx);

        return em.getSideEffectType();
    }
}
