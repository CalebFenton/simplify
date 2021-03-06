package org.cf.util;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.dex.CommonTypes;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.writer.builder.BuilderTypeList;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utils {

    private static final Pattern PARAMETER_INDIVIDUATOR = Pattern.compile("(\\[*(?:[BCDFIJSZ]|L[^;]+;))");
    private static final Pattern PARAMETER_ISOLATOR = Pattern.compile("\\([^\\)]+\\)");
    private static final PathMatcher SMALI_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.smali");

    public static Object buildArray(String internalName, int length) throws ClassNotFoundException {
        return buildArray(internalName, length, Utils.class.getClassLoader());
    }

    public static Object buildArray(String internalName, int length, ClassLoader classLoader) throws ClassNotFoundException {
        String baseClassName = ClassNameUtils.getComponentBase(internalName);
        String binaryName = ClassNameUtils.internalToBinary(baseClassName);

        int dimensionCount = ClassNameUtils.getDimensionCount(internalName) - 1;
        String classNameWithDimensions = ClassNameUtils.addDimensionsToBinaryClassName(binaryName, dimensionCount);
        Class<?> klazz = ClassUtils.getClass(classLoader, classNameWithDimensions);
        Object array = Array.newInstance(klazz, length);

        return array;
    }

    public static List<String> builderTypeListToTypeNames(BuilderTypeList typeList) {
        return typeList.stream().map(BuilderTypeReference::getType).collect(Collectors.toCollection(LinkedList::new));
    }

    public static String buildFieldDescriptor(Field field) {
        String className = ClassNameUtils.toInternal(field.getDeclaringClass());
        String typeName = ClassNameUtils.toInternal(field.getType());

        return className + "->" + field.getName() + ':' + typeName;
    }

    public static Object castToPrimitive(Object value, String targetType) {
        // Type information is not always available beyond "const" because Dalvik handles multiple types like integers.
        // This is to make easier the casting of that number to the correct virtual.
        if (value instanceof Number) {
            Number castValue = (Number) value;
            if ("B".equals(targetType) || "Ljava/lang/Byte;".equals(targetType)) {
                return castValue.byteValue();
            } else if ("D".equals(targetType) || "Ljava/lang/Double;".equals(targetType)) {
                return castValue.doubleValue();
            } else if ("F".equals(targetType) || "Ljava/lang/Float;".equals(targetType)) {
                return castValue.floatValue();
            } else if ("I".equals(targetType) || "Ljava/lang/Integer;".equals(targetType)) {
                return castValue.intValue();
            } else if ("L".equals(targetType) || "Ljava/lang/Long;".equals(targetType)) {
                return castValue.longValue();
            } else if ("S".equals(targetType) || "Ljava/lang/Short;".equals(targetType)) {
                return castValue.shortValue();
            } else if ("C".equals(targetType) || "Ljava/lang/Character;".equals(targetType)) {
                return (char) castValue.intValue();
            } else if ("Z".equals(targetType) || "Ljava/lang/Boolean;".equals(targetType)) {
                return castValue.intValue() != 0;
            }
        } else if (value instanceof Boolean) {
            Boolean castValue = (Boolean) value;
            if ("Z".equals(targetType) || "Ljava/lang/Boolean;".equals(targetType)) {
                return castValue;
            } else if ("B".equals(targetType) || "Ljava/lang/Byte;".equals(targetType)) {
                return (byte) (castValue ? 1 : 0);
            } else if ("I".equals(targetType) || "Ljava/lang/Integer;".equals(targetType)) {
                return castValue ? 1 : 0;
            } else if ("S".equals(targetType) || "Ljava/lang/Short;".equals(targetType)) {
                return (short) (castValue ? 1 : 0);
            }
        } else if (value instanceof Character) {
            Character castValue = (Character) value;
            Integer intValue = (int) castValue;
            if ("Z".equals(targetType) || "Ljava/lang/Boolean;".equals(targetType)) {
                return (int) castValue != 0;
            } else if ("B".equals(targetType) || "Ljava/lang/Byte;".equals(targetType)) {
                return intValue.byteValue();
            } else if ("I".equals(targetType) || "Ljava/lang/Integer;".equals(targetType)) {
                return intValue;
            } else if ("S".equals(targetType) || "Ljava/lang/Short;".equals(targetType)) {
                return intValue.shortValue();
            }
        }

        return value;
    }

    public static String getArrayDimensionString(Object array) {
        if (!array.getClass().isArray()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Object current = array;
        int len = Array.getLength(current);
        sb.append('[').append(len).append(']');

        while (len > 0) {
            current = Array.get(current, 0);
            if ((current == null) || !current.getClass().isArray()) {
                break;
            }
            len = Array.getLength(current);
            sb.append('[').append(len).append(']');
        }

        return sb.toString();
    }

    public static Double getDoubleValue(Object obj) {
        Double doubleValue = (Double) castToPrimitive(obj, CommonTypes.DOUBLE_OBJ);

        return doubleValue;
    }

    public static List<File> getFilesWithSmaliExtension(File file) {
        final List<File> files = new LinkedList<>();
        if (file.isDirectory()) {
            try {
                java.nio.file.Files.walk(file.toPath()).forEach(filePath -> {
                    if (java.nio.file.Files.isRegularFile(filePath)) {
                        if (SMALI_MATCHER.matches(filePath)) {
                            files.add(filePath.toFile());
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.getAbsolutePath().toLowerCase().endsWith(".local")) {
            files.add(file);
        }

        return files;
    }

    public static Float getFloatValue(Object obj) {
        return (Float) castToPrimitive(obj, CommonTypes.FLOAT_OBJ);
    }

    public static Integer getIntegerValue(Object obj) {
        return (Integer) castToPrimitive(obj, CommonTypes.INTEGER_OBJ);
    }

    public static MethodLocation[] getLocations(BuilderInstruction... instructions) {
        MethodLocation[] locations = new MethodLocation[instructions.length];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = instructions[i].getLocation();
        }

        return locations;
    }

    public static Long getLongValue(Object obj) {
        return (Long) castToPrimitive(obj, CommonTypes.LONG_OBJ);
    }

    public static MethodLocation getNextLocation(MethodLocation location, Map<Integer, MethodLocation> addressToLocation) {
        int address = location.getCodeAddress();
        int nextAddress = address + location.getInstruction().getCodeUnits();

        return addressToLocation.get(nextAddress);
    }

    /**
     * Determine parameter types by parsing the method descriptor.
     * Note: For local methods, there's ClassManager#getParameterTypes.
     *
     * @return list of parameter types in internal form
     */
    public static List<String> getParameterTypes(String methodDescriptor) {
        Matcher m = PARAMETER_ISOLATOR.matcher(methodDescriptor);
        List<String> result = new ArrayList<>();
        if (m.find()) {
            String params = m.group();
            m = PARAMETER_INDIVIDUATOR.matcher(params);
            while (m.find()) {
                result.add(m.group());
            }
        }

        return result;
    }

    public static int getRegisterSize(BuilderTypeList typeList) {
        return getRegisterSize(builderTypeListToTypeNames(typeList));
    }

    public static int getRegisterSize(Class<?>[] parameterTypes) {
        return getRegisterSize(ClassNameUtils.toInternal(parameterTypes));
    }

    public static int getRegisterSize(List<String> typeNames) {
        int size = 0;
        for (String typeName : typeNames) {
            size += getRegisterSize(typeName);
        }

        return size;
    }

    public static int getRegisterSize(String typeName) {
        return CommonTypes.LONG.equals(typeName) || CommonTypes.DOUBLE.equals(typeName) ? 2 : 1;
    }

    public static Set<String> getDeclaredAndValueTypeNames(HeapItem item) {
        Set<String> types = new HashSet<>(3);
        types.add(item.getType());

        Object value = item.getValue();
        if (!item.isUnknown() && value != null) {
            types.add(ClassNameUtils.toInternal(value.getClass()));
        }

        return types;
    }

    public static <T> void shiftIntegerMapKeys(int startKey, int shift, TIntObjectMap<T> intToObject) {
        if (shift == 0) {
            return;
        }

        TIntList keysToShift = new TIntArrayList(intToObject.keys());
        // Exclude anything before and including startKey
        for (int currentKey : keysToShift.toArray()) {
            if (currentKey <= startKey) {
                keysToShift.remove(currentKey);
            }
        }

        keysToShift.sort();
        if (shift > 0) {
            // Shifting keys up, so start at the end to avoid overwriting keys.
            keysToShift.reverse();
        }

        for (int currentKey : keysToShift.toArray()) {
            T obj = intToObject.get(currentKey);
            intToObject.remove(currentKey);
            intToObject.put(currentKey + shift, obj);
        }
    }
}
