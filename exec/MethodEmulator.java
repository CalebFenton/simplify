package simplify.exec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simplify.emulate.EmulatedMethod;
import simplify.emulate.EmulatedVirtualMethod;
import simplify.emulate.java_lang_Integer_init;
import simplify.emulate.java_lang_Integer_intValue;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods;
    static {
        emulatedMethods = new HashMap<String, EmulatedMethod>();

        emulatedMethods.put("Ljava/lang/Integer;-><init>(I)", new java_lang_Integer_init());
        emulatedMethods.put("Ljava/lang/Integer;->intValue()", new java_lang_Integer_intValue());
    }

    public MethodEmulator() {
    }

    public static String getMethodSignature(String className, String methodName,
                    List<? extends CharSequence> parameterTypes) {
        StringBuilder result = new StringBuilder();
        result.append(className).append("->").append(methodName).append("(");
        for (CharSequence type : parameterTypes) {
            result.append(type);
        }
        result.append(")");

        return result.toString();
    }

    public static boolean canEmulate(String methodSignature) {
        System.out.println("method signature: " + methodSignature);
        return emulatedMethods.containsKey(methodSignature);
    }

    public static void emulate(ExecutionContext ectx, String methodSignature) {
        EmulatedMethod em = emulatedMethods.get(methodSignature);
        if (em instanceof EmulatedVirtualMethod) {
            EmulatedVirtualMethod evm = (EmulatedVirtualMethod) em;
            evm.execute(ectx);
        }
    }
}
