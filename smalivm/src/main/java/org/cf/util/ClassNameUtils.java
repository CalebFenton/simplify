package org.cf.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class ClassNameUtils {

    private static final Map<String, Class<?>> binaryNameToType;

    private static final BiMap<String, String> internalPrimitiveToBinaryName;
    private static final BiMap<String, String> internalPrimitiveToWrapper;
    private static final Map<String, Class<?>> internalPrimitiveToClass;

    static {
        internalPrimitiveToWrapper = HashBiMap.create();
        internalPrimitiveToWrapper.put("I", Integer.class.getName());
        internalPrimitiveToWrapper.put("S", Short.class.getName());
        internalPrimitiveToWrapper.put("J", Long.class.getName());
        internalPrimitiveToWrapper.put("B", Byte.class.getName());
        internalPrimitiveToWrapper.put("D", Double.class.getName());
        internalPrimitiveToWrapper.put("F", Float.class.getName());
        internalPrimitiveToWrapper.put("Z", Boolean.class.getName());
        internalPrimitiveToWrapper.put("C", Character.class.getName());
        internalPrimitiveToWrapper.put("V", Void.class.getName());

        internalPrimitiveToClass = new HashMap<>(8);
        internalPrimitiveToClass.put("I", int.class);
        internalPrimitiveToClass.put("S", short.class);
        internalPrimitiveToClass.put("J", long.class);
        internalPrimitiveToClass.put("B", byte.class);
        internalPrimitiveToClass.put("D", double.class);
        internalPrimitiveToClass.put("F", float.class);
        internalPrimitiveToClass.put("Z", boolean.class);
        internalPrimitiveToClass.put("C", char.class);

        internalPrimitiveToBinaryName = HashBiMap.create();
        internalPrimitiveToBinaryName.put("I", int.class.getName());
        internalPrimitiveToBinaryName.put("S", short.class.getName());
        internalPrimitiveToBinaryName.put("J", long.class.getName());
        internalPrimitiveToBinaryName.put("B", byte.class.getName());
        internalPrimitiveToBinaryName.put("D", double.class.getName());
        internalPrimitiveToBinaryName.put("F", float.class.getName());
        internalPrimitiveToBinaryName.put("Z", boolean.class.getName());
        internalPrimitiveToBinaryName.put("C", char.class.getName());

        // Note: Void is not technically a primitive.
        internalPrimitiveToBinaryName.put("V", void.class.getName());

        binaryNameToType = new HashMap<>(9);
        binaryNameToType.put("int", Integer.TYPE);
        binaryNameToType.put("short", Short.TYPE);
        binaryNameToType.put("long", Long.TYPE);
        binaryNameToType.put("byte", Byte.TYPE);
        binaryNameToType.put("float", Float.TYPE);
        binaryNameToType.put("double", Double.TYPE);
        binaryNameToType.put("boolean", Boolean.TYPE);
        binaryNameToType.put("char", Character.TYPE);
        binaryNameToType.put("void", Void.TYPE);
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
        String baseName = getComponentBase(binaryName);
        StringBuilder sb = new StringBuilder();
        int dimensionCount = getDimensionCount(binaryName);
        for (int i = 0; i < dimensionCount; i++) {
            sb.append('[');
        }

        String internalPrimitive = internalPrimitiveToBinaryName.inverse().get(baseName);
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
     * Get the base component of an array of any dimension. Works with binary, internal, and source formats.
     *
     * For example,
     * "[[B" becomes "B"
     * "[[Ljava.lang.Object;" becomes "Ljava.lang.Object;"
     * "java.lang.Object[][]" becomes "java.lang.Object"
     *
     * @return base component class
     */
    public static String getComponentBase(String className) {
        return className.replace("[", "").replace("]", "");
    }

    /**
     * Similar to {@link Class#getComponentType()} for array classes. Works with binary, internal, and source formats.
     *
     * @return component class
     */
    public static String getComponentType(String className) {
        return className.replaceFirst("\\[\\]?", "");
    }

    /**
     * Get the dimension count or rank. Works with binary, internal, and source formats.
     *
     * @return dimension count
     */
    public static int getDimensionCount(String className) {
        // A fancy word for "number of dimensions" is "rank".
        // But getRank() only makes sense if you're a total nerd.
        String baseClassName = className.replace("[", "");

        return className.length() - baseClassName.length();
    }

    /**
     * Get the package name for a given class. Works with all formats.
     * For example,
     * "Lorg/cf/Klazz" gives "org.cf"
     * "org.cf.Klazz" gives "org.cf"
     *
     * @return package name of class
     */
    public static String getPackageName(String className) {
        String sourceName = toFormat(className, TypeFormat.SOURCE);
        int lastIndex = sourceName.lastIndexOf('.');
        if (lastIndex < 0) {
            return "";
        }

        return sourceName.substring(0, lastIndex);
    }

    /**
     * Get the internal format primitive class name for a given primitive wrapper.
     *
     * @return internal format primitive class name or null if not found
     */
    public static
    @Nullable
    String getPrimitive(String className) {
        String internalName = toFormat(className, TypeFormat.SOURCE);
        String primitiveName = internalPrimitiveToWrapper.inverse().get(getComponentBase(internalName));
        if (null == primitiveName) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int dimensionCount = getDimensionCount(internalName);
        if (dimensionCount > 0) {
            for (int i = 0; i < dimensionCount; i++) {
                sb.append('[');
            }
            sb.append(primitiveName);

            return sb.toString();
        }

        return primitiveName;
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
     * Get the binary format wrapper class name for a given primitive.
     *
     * @return wrapper class name or null if not found
     */
    public static
    @Nullable
    String getWrapper(String className) {
        String internalName = toFormat(className, TypeFormat.INTERNAL);
        String wrapperName = internalPrimitiveToWrapper.get(getComponentBase(internalName));
        if (null == wrapperName) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int dimensionCount = getDimensionCount(internalName);
        if (dimensionCount > 0) {
            for (int i = 0; i < dimensionCount; i++) {
                sb.append('[');
            }
            sb.append('L').append(wrapperName).append(';');

            return sb.toString();
        }

        return wrapperName;
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
     * Convert an internal class name format to source format.
     *
     * @return source format class name
     */
    public static String internalToSource(String internalName) {
        // E.g. [Ljava/lang/Object; -> java.lang.Object[]
        StringBuilder sourceName = new StringBuilder();
        String baseClass = getComponentBase(internalName);
        String binaryPrimitive = internalPrimitiveToBinaryName.get(baseClass);
        if (binaryPrimitive != null) {
            sourceName.append(binaryPrimitive);
        } else {
            sourceName.append(baseClass.substring(1, baseClass.length() - 1).replace('/', '.'));
        }

        int dimensions = ClassNameUtils.getDimensionCount(internalName);
        for (int i = 0; i < dimensions; i++) {
            sourceName.append("[]");
        }

        return sourceName.toString();
    }

    /**
     * Determines if the type can be represented by an {@link Object}. For example, a String or an int[].
     * Works with internal or binary type formats.
     *
     * @return true if type is an object, false otherwise
     */
    public static boolean isObject(String type) {
        char c = type.charAt(0);

        return c == 'L' || c == '[';
    }

    /**
     * Works with all class formats.
     *
     * @return true if class is primitive, otherwise false
     */
    public static boolean isPrimitive(String className) {
        String internalName = toFormat(className, TypeFormat.INTERNAL);

        return internalPrimitiveToBinaryName.containsKey(internalName);
    }

    /**
     * Works with all class formats.
     *
     * @return true if class is primitive of wrapper, otherwise false
     */
    public static boolean isPrimitiveOrWrapper(String className) {
        return isPrimitive(className) || isWrapper(className);
    }

    /**
     * Works with all class formats.
     *
     * @return true if class is primitive wrapper, otherwise false
     */
    public static boolean isWrapper(String className) {
        return getWrapper(className) != null;
    }

    /**
     * Convert source format class name to binary format.
     *
     * @return binary format class name
     */
    public static String sourceToBinary(String sourceName) {
        String sourceBaseName = sourceName.replace("[]", "");
        StringBuilder sb = new StringBuilder();
        int dimensionCount = (sourceName.length() - sourceBaseName.length()) / 2;
        for (int i = 0; i < dimensionCount; i++) {
            sb.append('[');
        }

        String internalPrimitive = internalPrimitiveToBinaryName.inverse().get(sourceBaseName);
        if (internalPrimitive != null) {
            if (dimensionCount > 0) {
                sb.append(internalPrimitive);
            } else {
                sb.append(internalPrimitiveToBinaryName.get(internalPrimitive));
            }
        } else {
            if (dimensionCount > 0) {
                sb.append('L').append(sourceBaseName).append(';');
            } else {
                sb.append(sourceBaseName);
            }

        }

        return sb.toString();
    }

    /**
     * Convert source format class name to internal format.
     *
     * @return internal format class name
     */
    public static String sourceToInternal(String sourceName) {
        String sourceBaseName = sourceName.replace("[]", "");
        StringBuilder sb = new StringBuilder();
        int dimensionCount = (sourceName.length() - sourceBaseName.length()) / 2;
        for (int i = 0; i < dimensionCount; i++) {
            sb.append('[');
        }

        String internalPrimitive = internalPrimitiveToBinaryName.inverse().get(sourceBaseName);
        if (internalPrimitive != null) {
            sb.append(internalPrimitive);
        } else {
            sb.append('L').append(sourceBaseName.replace('.', '/')).append(';');
        }

        return sb.toString();
    }

    /**
     * Converts a class name of arbitrary format into any other format.
     *
     * @return class name in the given {@link TypeFormat}
     */
    public static String toFormat(String className, TypeFormat format) {
        /*
         * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.2
         * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.2.1
         * local / internal = [Ljava/lang/Object; or Ljava/lang/Object;
         * binary = [Ljava.lang.Object; or java.lang.Object
         * source = java.lang.Object[] or java.lang.Object
         */

        String baseName = getComponentBase(className);
        if (baseName.contains("/") || internalPrimitiveToBinaryName.containsKey(baseName)) {
            // Internal / Smali format, e.g. [Ljava/lang/Object;, I, J, [Z
            switch (format) {
                case INTERNAL:
                    return className;
                case BINARY:
                    return internalToBinary(className);
                case SOURCE:
                    return internalToSource(className);
            }
        } else {
            if (className.endsWith(";")) {
                // Binary format, e.g. [Ljava.lang.Object;, java.lang.Object, int, long, [Z
                switch (format) {
                    case INTERNAL:
                        return binaryToInternal(className);
                    case BINARY:
                        return className;
                    case SOURCE:
                        return internalToSource(binaryToInternal(className));
                }
            } else {
                // Source format, e.g. java.lang.Object[], java.lang.Object, int, long, boolean[]
                // E.g. int, long, boolean
                switch (format) {
                    case INTERNAL:
                        return sourceToInternal(className);
                    case BINARY:
                        return sourceToBinary(className);
                    case SOURCE:
                        return className;
                }
            }
        }

        return className;
    }

    /**
     * Get the internal format class name for a given Java class.
     *
     * @return internal format class name
     */
    public static String toInternal(Class<?> klazz) {
        return binaryToInternal(klazz.getName());
    }

    /**
     * Get the internal format class name for an array of Java classes.
     *
     * @return list of internal format class names in the same order as arguments
     */
    public static List<String> toInternal(Class<?>... classes) {
        List<String> names = new LinkedList<>();
        for (Class<?> klazz : classes) {
            names.add(toInternal(klazz));
        }

        return names;
    }

    static String addDimensionsToBinaryClassName(String className, int dimensionCount) {
        StringBuilder sb = new StringBuilder(className);
        for (int i = 0; i < dimensionCount; i++) {
            sb.append("[]");
        }

        return sb.toString();
    }

    static String addDimensionsToInternalClassName(String className, int dimensionCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimensionCount; i++) {
            sb.append('[');
        }
        sb.append(className);

        return sb.toString();
    }

    public enum TypeFormat {
        BINARY,
        INTERNAL,
        SOURCE
    }

}
