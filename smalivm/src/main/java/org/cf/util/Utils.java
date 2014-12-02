package org.cf.util;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.type.LocalInstance;

public class Utils {

    private static final Pattern ParameterIndividuator = Pattern.compile("(\\[*(?:[BCDFIJSZ]|L[^;]+;))");
    private static final Pattern ParameterIsolator = Pattern.compile("\\([^\\)]+\\)");

    public static void deDuplicate(TIntList list) {
        for (int i = 0; i < list.size(); i++) {
            int item = list.get(i);
            int firstIndex = list.indexOf(item);
            while (firstIndex != list.lastIndexOf(item)) {
                int lastIndex = list.lastIndexOf(item);
                list.removeAt(lastIndex);
            }
        }
    }

    public static String getArrayDimensionString(Object array) {
        if (!array.getClass().isArray()) {
            return "";
        }

        StringBuilder sb = new StringBuilder("");

        Object current = array;
        int len = Array.getLength(current);
        sb.append("[").append(len).append("]");

        while (len > 0) {
            current = Array.get(current, 0);
            if ((current == null) || !current.getClass().isArray()) {
                break;
            }
            len = Array.getLength(current);
            sb.append("[").append(len).append("]");
        }

        return sb.toString();
    }

    public static Object getArrayInstanceFromSmaliTypeReference(String typeReference, int dimension,
                    boolean isLocalClass) throws ClassNotFoundException {
        return getArrayInstanceFromSmaliTypeReference(typeReference, new int[] { dimension }, isLocalClass);
    }

    public static Object getArrayInstanceFromSmaliTypeReference(String typeReference, int[] dimensions,
                    boolean isLocalClass) throws ClassNotFoundException {
        // int dimensionCount = getDimensionCount(typeReference) - 1;
        String baseClassName = SmaliClassUtils.getBaseClass(typeReference);
        String javaClassName = null;
        if (isLocalClass) {
            javaClassName = LocalInstance.class.getName();
        } else {
            javaClassName = SmaliClassUtils.smaliClassToJava(baseClassName);
        }

        Class<?> klazz = ClassUtils.getClass(javaClassName);
        Object result = Array.newInstance(klazz, dimensions);

        if (isLocalClass) {
            populateLocalInstanceArray(result, baseClassName);
        }

        return result;
    }

    public static int getDimensionCount(String typeReference) {
        String baseClassName = typeReference.replace("[", "");

        return typeReference.length() - baseClassName.length();
    }

    public static List<String> getParameterTypes(String methodDescriptor) {
        // Only use this for non-local methods.
        // For local methods, there's VirtualMachine#getParameterTypes.
        Matcher m = ParameterIsolator.matcher(methodDescriptor);
        List<String> result = new ArrayList<String>();
        if (m.find()) {
            String params = m.group();
            m = ParameterIndividuator.matcher(params);
            while (m.find()) {
                result.add(m.group());
            }
        }

        return result;
    }

    public static <E> Collection<E> makeCollection(Iterable<E> iter) {
        Collection<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    public static <T> void shiftIntegerMapKeys(int startAddress, int shift, TIntObjectMap<T> intToObject) {
        if (shift == 0) {
            return;
        }

        TIntList addressesToShift = new TIntArrayList(intToObject.keys());
        // Exclude anything before startAddress
        for (int currentAddress : addressesToShift.toArray()) {
            if (currentAddress <= startAddress) {
                addressesToShift.remove(currentAddress);
            }
        }

        addressesToShift.sort();
        if (shift > 0) {
            // Shifting keys up, so start at the end to avoid overwriting keys.
            addressesToShift.reverse();
        }

        for (int currentAddress : addressesToShift.toArray()) {
            T obj = intToObject.get(currentAddress);
            intToObject.remove(currentAddress);
            intToObject.put(currentAddress + shift, obj);
        }
    }

    private static String getClassForNameFromSmaliClass(String className, int dimensionCount) {
        // Apache's ClassUtils.forName expects someArray[] instead of [someArray
        StringBuilder sb = new StringBuilder(className);
        for (int i = 0; i < dimensionCount; i++) {
            sb.append("[]");
        }

        return sb.toString();
    }

    private static void populateLocalInstanceArray(Object array, String className) {
        for (int i = 0; i < Array.getLength(array); i++) {
            Object element = Array.get(array, i);
            if (element == null) {
                if (array.getClass().getName().startsWith("[[")) {
                    // Uninitialized inner array
                    break;
                } else {
                    for (int j = 0; j < Array.getLength(array); j++) {
                        Array.set(array, j, new LocalInstance(className));
                    }
                }
            } else if (element.getClass().isArray()) {
                populateLocalInstanceArray(element, className);
            }
        }
    }
}
