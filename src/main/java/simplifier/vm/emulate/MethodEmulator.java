package simplifier.vm.emulate;

import java.util.HashMap;
import java.util.Map;

import simplifier.vm.context.MethodContext;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods;
    static {
        emulatedMethods = new HashMap<String, EmulatedMethod>();
        emulatedMethods.put("Lsimplifier/Utils;->breakpoint()V", new Breakpoint());
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
