package org.cf.util;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalField;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.writer.builder.BuilderTypeList;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;

public class Utils {

    private static final Pattern PARAMETER_INDIVIDUATOR = Pattern.compile("(\\[*(?:[BCDFIJSZ]|L[^;]+;))");
    private static final Pattern PARAMETER_ISOLATOR = Pattern.compile("\\([^\\)]+\\)");

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

    public static Object buildArray(String typeReference, int length, boolean useLocalClass)
                    throws ClassNotFoundException {
        String baseClassName = SmaliClassUtils.getBaseClass(typeReference);
        String javaClassName;
        if (useLocalClass) {
            javaClassName = LocalInstance.class.getName();
        } else {
            javaClassName = SmaliClassUtils.smaliClassToJava(baseClassName);
        }

        int dimensionCount = getDimensionCount(typeReference) - 1;
        String classNameWithDimensions = addDimensionsToClassName(javaClassName, dimensionCount);
        // Building generic arrays at this point allows us to add stuff like LocalInstances
        // But it creates other problems like how are you going to ensure you're passing
        // arrays a reflected method knows how to handle? I.e. it doesn't include our own types
        // classNameWithDimensions = addDimensionsToClassName("java.lang.Object", dimensionCount);
        Class<?> klazz = ClassUtils.getClass(classNameWithDimensions);
        Object array = Array.newInstance(klazz, length);

        return array;
    }

    public static int getDimensionCount(String typeReference) {
        // A fancy word for "number of dimensions" is "rank".
        // But getRank() only makes sense if you're a total nerd.
        String baseClassName = typeReference.replace("[", "");

        return typeReference.length() - baseClassName.length();
    }

    public static List<String> getParameterTypes(String methodDescriptor) {
        // Only use this for non-local methods.
        // For local methods, there's VirtualMachine#getParameterTypes.
        Matcher m = PARAMETER_ISOLATOR.matcher(methodDescriptor);
        List<String> result = new ArrayList<String>();
        if (m.find()) {
            String params = m.group();
            m = PARAMETER_INDIVIDUATOR.matcher(params);
            while (m.find()) {
                result.add(m.group());
            }
        }

        return result;
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

    private static String addDimensionsToClassName(String className, int dimensionCount) {
        // Apache's ClassUtils.forName expects someArray[] instead of [someArray
        StringBuilder sb = new StringBuilder(className);
        for (int i = 0; i < dimensionCount; i++) {
            sb.append("[]");
        }

        return sb.toString();
    }

    public static int getRegisterSize(List<String> typeNames) {
        int size = 0;
        for (String typeName : typeNames) {
            size += getRegisterSize(typeName);
        }

        return size;
    }

    public static int getRegisterSize(String typeName) {
        return "J".equals(typeName) || "D".equals(typeName) ? 2 : 1;
    }

    public static int getRegisterSize(Class<?>[] parameterTypes) {
        return getRegisterSize(SmaliClassUtils.javaClassToSmali(parameterTypes));
    }

    public static List<String> builderTypeListToTypeNames(BuilderTypeList typeList) {
        List<String> typeNames = new LinkedList<String>();
        for (BuilderTypeReference type : typeList) {
            typeNames.add(type.getType());
        }

        return typeNames;
    }

    public static int getRegisterSize(BuilderTypeList typeList) {
        return getRegisterSize(builderTypeListToTypeNames(typeList));
    }

    public static Integer getIntegerValue(Object obj) {
        Integer intValue = (Integer) castToPrimitive(obj, "Ljava/lang/Integer;");

        return intValue;
    }

    public static Float getFloatValue(Object obj) {
        Float floatValue = (Float) castToPrimitive(obj, "Ljava/lang/Float;");

        return floatValue;
    }

    public static Double getDoubleValue(Object obj) {
        Double doubleValue = (Double) castToPrimitive(obj, "Ljava/lang/Double;");

        return doubleValue;
    }

    public static Long getLongValue(Object obj) {
        Long longValue = (Long) castToPrimitive(obj, "Ljava/lang/Long;");

        return longValue;
    }

    public static Object castToPrimitive(Object value, String targetType) {
        // TODO: add tests for this + confirm dalvik works this way

        // Type information is not always available beyond "const" because Dalvik handles multiple types like integers.
        // This is to make easier the casting of that number to the correct type.
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
                return castValue.intValue() != 0 ? true : false;
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
                return (int) castValue != 0 ? true : false;
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

    public static Set<String> getTypes(HeapItem item) {
        Set<String> types = new HashSet<String>();

        String declaredType = item.getType();
        types.add(declaredType);

        Object value = item.getValue();
        if (value instanceof UnknownValue) {
            // Can't imply type from value
        } else if (value instanceof LocalClass) {
            types.add(SmaliClassUtils.javaClassToSmali(Class.class));
        } else if (value instanceof LocalField) {
            types.add(SmaliClassUtils.javaClassToSmali(Field.class));
        } else if (value instanceof LocalMethod) {
            types.add(SmaliClassUtils.javaClassToSmali(Method.class));
        } else if (value != null) {
            // All other value classes should be the actual classes
            types.add(SmaliClassUtils.javaClassToSmali(value.getClass()));
        }

        return types;
    }

    public static MethodLocation getNextLocation(MethodLocation location,
                    TIntObjectMap<MethodLocation> addressToLocation) {
        int address = location.getCodeAddress();
        int nextAddress = address + location.getInstruction().getCodeUnits();

        return addressToLocation.get(nextAddress);
    }

    public static final MethodLocation[] getLocations(BuilderInstruction... instructions) {
        MethodLocation[] locations = new MethodLocation[instructions.length];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = instructions[i].getLocation();
        }

        return locations;
    }

}
