package org.cf.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SmaliClassUtils {

    private static BiMap<String, String> smaliPrimitiveToJavaWrapper;
    private static BiMap<String, String> smaliPrimitiveToJavaName;
    static {
        smaliPrimitiveToJavaWrapper = HashBiMap.create();
        smaliPrimitiveToJavaWrapper.put("I", Integer.class.getName());
        smaliPrimitiveToJavaWrapper.put("S", Short.class.getName());
        smaliPrimitiveToJavaWrapper.put("J", Long.class.getName());
        smaliPrimitiveToJavaWrapper.put("B", Byte.class.getName());
        smaliPrimitiveToJavaWrapper.put("D", Double.class.getName());
        smaliPrimitiveToJavaWrapper.put("F", Float.class.getName());
        smaliPrimitiveToJavaWrapper.put("Z", Boolean.class.getName());
        smaliPrimitiveToJavaWrapper.put("C", Character.class.getName());

        smaliPrimitiveToJavaName = HashBiMap.create();
        smaliPrimitiveToJavaName.put("I", int.class.getName());
        smaliPrimitiveToJavaName.put("S", short.class.getName());
        smaliPrimitiveToJavaName.put("J", long.class.getName());
        smaliPrimitiveToJavaName.put("B", byte.class.getName());
        smaliPrimitiveToJavaName.put("D", double.class.getName());
        smaliPrimitiveToJavaName.put("F", float.class.getName());
        smaliPrimitiveToJavaName.put("Z", boolean.class.getName());
        smaliPrimitiveToJavaName.put("C", char.class.getName());

    }

    public static boolean isPrimitiveType(String type) {
        return smaliPrimitiveToJavaName.containsKey(getBaseClass(type));
    }

    public static String javaClassToSmali(Class<?> klazz) {
        return javaClassToSmali(klazz.getName());
    }

    public static String javaClassToSmali(String className) {
        if (className.startsWith("[")) {
            return className.replaceAll("\\.", "/");
        }

        String javaName = smaliPrimitiveToJavaName.inverse().get(className);
        if (null != javaName) {
            return javaName;
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

        String javaName = smaliPrimitiveToJavaName.get(className);
        if (null != javaName) {
            // e.g. I becomes int
            return javaName;
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
        String javaWrapper = smaliPrimitiveToJavaWrapper.get(getBaseClass(className));
        if (null == javaWrapper) {
            return null;
        }

        if (!className.startsWith("[")) {
            return javaWrapper;
        }

        int lastIndex = className.lastIndexOf("[");
        String dimens = className.substring(0, lastIndex + 1);
        StringBuilder sb = new StringBuilder(dimens);
        sb.append("L").append(javaWrapper).append(";");

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
