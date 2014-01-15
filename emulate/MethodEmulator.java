package simplify.emulate;

import java.util.HashMap;
import java.util.Map;

import simplify.exec.MethodExecutionContext;

public class MethodEmulator {

    private static Map<String, EmulatedMethod> emulatedMethods;
    static {
        emulatedMethods = new HashMap<String, EmulatedMethod>();

        emulatedMethods.put("Ljava/lang/Integer;-><init>(I)V", new java_lang_Integer_init());
        emulatedMethods.put("Ljava/lang/Integer;->intValue()I", new java_lang_Integer_intValue());

        emulatedMethods.put("Ljava/lang/Boolean;->booleanValue()Z", new java_lang_Boolean_booleanValue());
        emulatedMethods.put("Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;", new java_lang_Boolean_valueOf());

        emulatedMethods.put("Ljava/lang/Class;->forName()Ljava/lang/Class;", new java_lang_Integer_intValue());

        emulatedMethods.put("Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;",
                        new java_lang_String_valueOf());
        emulatedMethods.put("Ljava/lang/String;->charAt(I)C", new java_lang_String_charAt());

        emulatedMethods.put("Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V",
                        new java_lang_StringBuilder_init());
        emulatedMethods.put("Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;",
                        new java_lang_StringBuilder_append());
        emulatedMethods.put("Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                        new java_lang_StringBuilder_append());
        emulatedMethods.put("Ljava/lang/StringBuilder;->toString()Ljava/lang/String;",
                        new java_lang_StringBuilder_toString());
    }

    public static boolean canEmulate(String methodDescriptor) {
        return emulatedMethods.containsKey(methodDescriptor);
    }

    public static void emulate(MethodExecutionContext ectx, String methodDescriptor) {
        EmulatedMethod em = emulatedMethods.get(methodDescriptor);
        if (em instanceof EmulatedMethod) {
            em.execute(ectx);
        }
    }
}
