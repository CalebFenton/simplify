package org.cf.smalivm.emulate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodEmulator {

    private static final Logger log = LoggerFactory.getLogger(MethodEmulator.class.getSimpleName());

    private final VirtualMachine vm;
    private final ExecutionContext ectx;
    private final String methodDescriptor;
    private final EmulatedMethod method;

    public MethodEmulator(VirtualMachine vm, ExecutionContext ectx, String methodDescriptor) {
        this.vm = vm;
        this.ectx = ectx;
        this.methodDescriptor = methodDescriptor;
        method = getMethod(methodDescriptor);
    }

    private static EmulatedMethod getMethod(String methodDescriptor) {
        Class<? extends EmulatedMethod> methodClass = emulatedMethods.get(methodDescriptor);
        EmulatedMethod em = null;
        try {
            em = methodClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return em;
    }

    private static Map<String, Class<? extends EmulatedMethod>> emulatedMethods = new HashMap<String, Class<? extends EmulatedMethod>>();
    static {
        addMethod("Lorg/cf/simplify/Utils;->breakpoint()V", org_cf_simplify_Utils_breakpoint.class);
        addMethod("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", java_lang_Class_forName.class);
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

        return (methodClass != null) && (UnknownValuesMethod.class.isAssignableFrom(methodClass));
    }

    public static void clearMethods() {
        emulatedMethods.clear();
    }

    public void emulate() {
        try {
            if (method instanceof MethodStateMethod) {
                ((MethodStateMethod) method).execute(vm, ectx.getMethodState());
            } else {
                ((ExecutionContextMethod) method).execute(vm, ectx);
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Unexpected real exception emulating " + methodDescriptor, e);
            }
        }
    }

    public SideEffect.Level getSideEffectLevel() {
        return method.getSideEffectLevel();
    }

    public Set<VirtualException> getExceptions() {
        return method.getExceptions();
    }

}
