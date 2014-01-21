package simplify.emulate;

import java.util.HashMap;
import java.util.Map;

import refactor.vm.MethodContext;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods;
    static {
        emulatedMethods = new HashMap<String, EmulatedMethod>();

        // emulatedMethods.put("Ljava/lang/Integer;-><init>(I)V", new java_lang_Integer_init());
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static void emulate(MethodContext mctx, String methodDescriptor) {
        EmulatedMethod em = emulatedMethods.get(methodDescriptor);
        if (em instanceof EmulatedMethod) {
            em.execute(mctx);
        }
    }
}
