package org.cf.smalivm.emulate;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodEmulator {

    private static final Logger log = LoggerFactory.getLogger(MethodEmulator.class.getSimpleName());

    private static Map<String, Class<? extends EmulatedMethod>> emulatedMethods = new HashMap<String, Class<? extends EmulatedMethod>>();
    static {
        addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", org_cf_simplify_Utils_breakpoint.class);
        addMethod("Ljava/lang/Class;->getPackage()Ljava/lang/Package;", java_lang_Class_getPackage.class);
        addMethod("Ljava/lang/Package;->getName()Ljava/lang/String;", java_lang_Package_getName.class);
        addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", java_lang_Class_forName.class);
        addMethod("Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        java_lang_Class_getMethod.class);
        addMethod("Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;",
                        java_lang_Class_getMethod.class);
        addMethod("Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
                        java_lang_Class_getField.class);
        addMethod("Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;",
                        java_lang_reflect_Field_get.class);
    }

    public static void addMethod(String methodDescriptor, Class<? extends EmulatedMethod> methodClass) {
        emulatedMethods.put(methodDescriptor, methodClass);
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static boolean canHandleUnknownValues(String methodDescriptor) {
        Class<? extends EmulatedMethod> methodClass = emulatedMethods.get(methodDescriptor);

        return (methodClass != null) && (methodClass.isAssignableFrom(UnknownValuesMethod.class));
    }

    public static void clearMethods() {
        emulatedMethods.clear();
    }

    public static SideEffect.Level emulate(VirtualMachine vm, ExecutionContext ectx, String methodDescriptor,
                    int[] parameterRegisters) {
        Class<? extends EmulatedMethod> methodClass = emulatedMethods.get(methodDescriptor);
        EmulatedMethod em = null;
        try {
            em = methodClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            if (em instanceof MethodStateMethod) {
                ((MethodStateMethod) em).execute(vm, ectx.getMethodState());
            } else {
                ((ExecutionContextMethod) em).execute(vm, ectx);
            }
        } catch (Exception e) {
            // TODO: exception handling
            if (log.isWarnEnabled()) {
                log.warn("Unexpected real excetion emulating " + methodDescriptor, e);
            }
        }

        return em.getSideEffectLevel();
    }
}
