package simplify;

import java.util.HashMap;
import java.util.Map;

public class SmaliClassUtils {

    // TODO: arrays!
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
        return PrimitiveTypes.containsKey(type);
    }

    public static Class<?> getPrimitiveClass(String type) {
        return PrimitiveTypes.get(type);
    }

    public static String smaliClassToJava(String className) {
        if (isPrimitiveType(className)) {
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

    public static Class<?> getClass(String className) throws ClassNotFoundException {
        String javaClassName = smaliClassToJava(className);

        if (isPrimitiveType(javaClassName)) {
            return getPrimitiveClass(javaClassName);
        }

        return Class.forName(javaClassName);
    }
}
