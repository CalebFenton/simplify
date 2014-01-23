package simplify;

import java.util.HashMap;
import java.util.Map;

public class SmaliClassUtils {

    private static Map<String, Class<?>> PrimitiveTypes;
    static {
        PrimitiveTypes = new HashMap<String, Class<?>>(8);
        PrimitiveTypes.put("I", Integer.TYPE);
        PrimitiveTypes.put("S", Short.TYPE);
        PrimitiveTypes.put("J", Long.TYPE);
        PrimitiveTypes.put("B", Byte.TYPE);
        PrimitiveTypes.put("D", Double.TYPE);
        PrimitiveTypes.put("F", Float.TYPE);
        PrimitiveTypes.put("Z", Boolean.TYPE);
        PrimitiveTypes.put("C", Character.TYPE);
    }

    public static boolean isPrimitiveType(String type) {
        // Remove any array qualifiers, e.g. [[B (2d byte array) becomes B
        String baseType = type.replaceAll("\\[", "");
        return PrimitiveTypes.containsKey(baseType);
    }

    public static String smaliClassToJava(String className) {
        if (isPrimitiveType(className)) {
            return className;
        }

        if (className.equals("?")) {
            // Probably lazy and didn't determine type.
            // This will probably result in a Class.forName exception.
            return className;
        }

        return className.substring(1, className.length() - 1).replaceAll("/", ".");
    }

    public static String javaClassToSmali(String className) {
        for (String key : PrimitiveTypes.keySet()) {
            String typeName = PrimitiveTypes.get(key).getName();
            if (typeName.equals(className)) {
                return key;
            }
        }

        return "L" + className.replaceAll("\\.", "/") + ";";
    }

}
