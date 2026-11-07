package org.cf.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Conversions between internal names ({@code [Ljava/lang/Object;}, {@code I}) and JVM binary names
 * ({@code [Ljava.lang.Object;}, {@code int}).
 */
public final class ClassNameUtils {

    private static final Map<String, String> internalPrimitiveToBinaryName = new HashMap<>();
    private static final Map<String, String> binaryNameToInternalPrimitive = new HashMap<>();
    private static final Map<String, Class<?>> internalPrimitiveToClass = new HashMap<>();

    static {
        addPrimitive("I", int.class);
        addPrimitive("S", short.class);
        addPrimitive("J", long.class);
        addPrimitive("B", byte.class);
        addPrimitive("D", double.class);
        addPrimitive("F", float.class);
        addPrimitive("Z", boolean.class);
        addPrimitive("C", char.class);
        internalPrimitiveToBinaryName.put("V", void.class.getName());
        binaryNameToInternalPrimitive.put(void.class.getName(), "V");
    }

    private ClassNameUtils() {
    }

    private static void addPrimitive(String internalName, Class<?> type) {
        internalPrimitiveToBinaryName.put(internalName, type.getName());
        binaryNameToInternalPrimitive.put(type.getName(), internalName);
        internalPrimitiveToClass.put(internalName, type);
    }

    /**
     * Convert a class name in binary format into a class name in the internal or Smali format.
     * For example,
     * "[Ljava.lang.Object;" becomes "[Ljava/lang/Object;"
     * "java.lang.Object" becomes "Ljava/lang/Object;"
     * "int" becomes "I"
     * "[Z" becomes "[Z"
     *
     * @return the class name in the internal format
     */
    public static String binaryToInternal(String binaryName) {
        int dimensionCount = binaryName.length() - binaryName.replace("[", "").length();
        String baseName = binaryName.replace("[", "").replace("]", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimensionCount; i++) {
            sb.append('[');
        }

        String internalPrimitive = binaryNameToInternalPrimitive.get(baseName);
        if (internalPrimitive != null) {
            return sb.append(internalPrimitive).toString();
        }

        if (dimensionCount > 0 && internalPrimitiveToBinaryName.containsKey(baseName)) {
            return sb.append(baseName).toString();
        }

        if (baseName.endsWith(";")) {
            sb.append(baseName.replace('.', '/'));
        } else {
            sb.append('L').append(baseName.replace('.', '/')).append(';');
        }

        return sb.toString();
    }

    /**
     * Get the class for a primitive className in the internal format.
     *
     * @return internal format primitive class or null if not found
     */
    public static Class<?> getPrimitiveClass(String className) {
        return internalPrimitiveToClass.get(className);
    }

    /**
     * Convert an internal class name format to binary format.
     *
     * @return binary format class name
     */
    public static String internalToBinary(String internalName) {
        String internalPrimitive = internalPrimitiveToBinaryName.get(internalName);
        if (internalPrimitive != null) {
            return internalPrimitive;
        }

        if (internalName.startsWith("[")) {
            return internalName.replace("/", ".");
        } else {
            return internalName.substring(1, internalName.length() - 1).replace("/", ".");
        }
    }

    /**
     * Get the internal format class name for a given Java class.
     *
     * @return internal format class name
     */
    public static String toInternal(Class<?> klazz) {
        return binaryToInternal(klazz.getName());
    }
}
