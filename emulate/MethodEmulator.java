package simplify.emulate;

import java.util.HashMap;
import java.util.Map;

import simplify.exec.MethodExecutionContext;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods;
    static {
        emulatedMethods = new HashMap<String, EmulatedMethod>();

        emulatedMethods.put("Ljava/lang/Integer;-><init>(I)", new java_lang_Integer_init());
        emulatedMethods.put("Ljava/lang/Integer;->intValue()", new java_lang_Integer_intValue());
    }

    public MethodEmulator() {
    }

    public static boolean canEmulate(String methodSignature) {
        return emulatedMethods.containsKey(methodSignature);
    }

    public static void emulate(MethodExecutionContext ectx, String methodSignature) {
        EmulatedMethod em = emulatedMethods.get(methodSignature);
        if (em instanceof EmulatedVirtualMethod) {
            EmulatedVirtualMethod evm = (EmulatedVirtualMethod) em;
            evm.execute(ectx);
        }
    }
}
