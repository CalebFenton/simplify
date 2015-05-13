package org.cf.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class SmaliClassUtils {

    private static BiMap<String, String> smaliPrimitiveToJavaWrapper;
    private static BiMap<String, String> smaliPrimitiveToJavaName;
    private static Map<String, Class<?>> javaPrimitiveToType;
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
        smaliPrimitiveToJavaWrapper.put("V", Void.class.getName());

        smaliPrimitiveToJavaName = HashBiMap.create();
        smaliPrimitiveToJavaName.put("I", int.class.getName());
        smaliPrimitiveToJavaName.put("S", short.class.getName());
        smaliPrimitiveToJavaName.put("J", long.class.getName());
        smaliPrimitiveToJavaName.put("B", byte.class.getName());
        smaliPrimitiveToJavaName.put("D", double.class.getName());
        smaliPrimitiveToJavaName.put("F", float.class.getName());
        smaliPrimitiveToJavaName.put("Z", boolean.class.getName());
        smaliPrimitiveToJavaName.put("C", char.class.getName());
        smaliPrimitiveToJavaName.put("V", void.class.getName());

        javaPrimitiveToType = new HashMap<String, Class<?>>(9);
        javaPrimitiveToType.put("int", Integer.TYPE);
        javaPrimitiveToType.put("long", Long.TYPE);
        javaPrimitiveToType.put("double", Double.TYPE);
        javaPrimitiveToType.put("float", Float.TYPE);
        javaPrimitiveToType.put("bool", Boolean.TYPE);
        javaPrimitiveToType.put("char", Character.TYPE);
        javaPrimitiveToType.put("byte", Byte.TYPE);
        javaPrimitiveToType.put("void", Void.TYPE);
        javaPrimitiveToType.put("short", Short.TYPE);
    }

    public static String smaliWrapperToSmaliPrimitive(String type) {
        String javaType = smaliClassToJava(type);

        return smaliPrimitiveToJavaWrapper.inverse().get(javaType);
    }

    public static boolean isPrimitiveType(String type) {
        return smaliPrimitiveToJavaName.containsKey(getBaseClass(type));
    }

    public static boolean isWrapperType(String type) {
        return smaliWrapperToSmaliPrimitive(type) != null;
    }

    public static boolean isPrimitiveOrWrapperType(String type) {
        return isPrimitiveType(type) || isWrapperType(type);
    }

    public static String javaClassToSmali(Class<?> klazz) {
        return javaClassToSmali(klazz.getName());
    }

    public static List<String> javaClassToSmali(Class<?>... classes) {
        List<String> smaliNames = new LinkedList<String>();
        for (Class<?> klazz : classes) {
            smaliNames.add(javaClassToSmali(klazz));
        }

        return smaliNames;
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

        StringBuilder sb = new StringBuilder("L");
        sb.append(className.replaceAll("\\.", "/")).append(';');

        return sb.toString();
    }

    public static String smaliClassToJava(String className) {
        if (className.startsWith("[")) {
            return className.replaceAll("/", "\\.");
        }

        String javaName = smaliPrimitiveToJavaName.get(className);
        if (null != javaName) {
            // e.g. "I" -> "int"
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

        int lastIndex = className.lastIndexOf('[');
        String dimens = className.substring(0, lastIndex + 1);
        StringBuilder sb = new StringBuilder(dimens);
        sb.append('L').append(javaWrapper).append(';');

        return sb.toString();
    }

    public static String getPackageName(String smaliType) {
        String packageName = smaliType.substring(1, smaliType.lastIndexOf('/'));
        packageName = packageName.replaceAll("/", ".");

        return packageName;
    }

    // Remove any array qualifiers, e.g. [[B becomes B
    public static String getBaseClass(String className) {
        return className.replace("[", "");
    }

    // Similar to Array.getComponentType
    public static String getComponentType(String arrayType) {
        assert arrayType.startsWith("[");

        return arrayType.replaceFirst("\\[", "");
    }

    public static Class<?> getPrimitiveType(String javaClassName) {
        return javaPrimitiveToType.get(javaClassName);
    }

}
