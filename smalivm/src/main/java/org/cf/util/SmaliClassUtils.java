package org.cf.util;

import java.util.HashMap;
import java.util.Map;

public class SmaliClassUtils {

    private static Map<String, Class<?>> primitiveClassNameToWrapperClass;
    static {
        primitiveClassNameToWrapperClass = new HashMap<String, Class<?>>(8);
        primitiveClassNameToWrapperClass.put("I", Integer.class);
        primitiveClassNameToWrapperClass.put("S", Short.class);
        primitiveClassNameToWrapperClass.put("J", Long.class);
        primitiveClassNameToWrapperClass.put("B", Byte.class);
        primitiveClassNameToWrapperClass.put("D", Double.class);
        primitiveClassNameToWrapperClass.put("F", Float.class);
        primitiveClassNameToWrapperClass.put("Z", Boolean.class);
        primitiveClassNameToWrapperClass.put("C", Character.class);
    }

    public static boolean isPrimitiveType(String type) {
        return primitiveClassNameToWrapperClass.containsKey(getBaseClass(type));
    }

    public static String javaClassToSmali(Class<?> klazz) {
        return javaClassToSmali(klazz.getName());
    }

    public static String javaClassToSmali(String className) {
        if (className.startsWith("[")) {
            return className.replaceAll("\\.", "/");
        }

        Class<?> klazz = primitiveClassNameToWrapperClass.get(className);
        if (klazz != null) {
            return klazz.getName();
        }

        if (className.endsWith(";") || (1 == className.length())) {
            // Already Smali format
            return className;
        }

        return "L" + className.replaceAll("\\.", "/") + ";";
    }

    public static String smaliClassToJava(String className) {
        if (className.startsWith("[")) {
            return className.replaceAll("/", "\\.");
        }

        Class<?> klazz = primitiveClassNameToWrapperClass.get(className);
        if (null != klazz) {
            return klazz.getName(); // e.g. I becomes int
        }

        if (className.equals("?")) {
            // Probably lazy and didn't determine type.
            // This will probably result in a Class.forName exception.
            return className;
        }

        // Java doesn't like Lcom/package/class;, it wants "com.package.class"
        return className.substring(1, className.length() - 1).replaceAll("/", ".");
    }

    public static String smaliPrimitiveToJavaWrapper(String className) {
        Class<?> primitiveClass = primitiveClassNameToWrapperClass.get(getBaseClass(className));
        if (null == primitiveClass) {
            return null;
        }

        if (!className.startsWith("[")) {
            return primitiveClass.getName();
        }

        int lastIndex = className.lastIndexOf("[");
        String dimens = className.substring(0, lastIndex + 1);
        StringBuilder sb = new StringBuilder(dimens);
        sb.append("L").append(primitiveClass.getName()).append(";");

        return sb.toString();
    }

    public static String getPackageName(String smaliType) {
        String packageName = smaliType.substring(1, smaliType.lastIndexOf("/"));
        packageName = packageName.replaceAll("/", ".");

        return packageName;
    }

    public static String getBaseClass(String className) {
        // Remove any array qualifiers, e.g. [[B (2d byte array) becomes B
        return className.replace("[", "");
    }

}
