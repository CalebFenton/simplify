package simplify;

import gnu.trove.list.TIntList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;

import simplify.vm.types.LocalInstance;

public class Utils {

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

    public static <E> Collection<E> makeCollection(Iterable<E> iter) {
        Collection<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    private static String getClassForNameFromSmaliClass(String className, int dimensionCount) {
        // Apache's ClassUtils.forName expects someArray[] instead of [someArray
        StringBuilder sb = new StringBuilder(className);
        for (int i = 0; i < dimensionCount; i++) {
            sb.append("[]");
        }

        return sb.toString();
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
        int dimensionCount = getDimensionCount(typeReference) - 1;
        String baseClassName = typeReference.replace("[", "");

        String javaClassName = null;
        if (isLocalClass) {
            javaClassName = LocalInstance.class.getName();
        } else {
            javaClassName = SmaliClassUtils.smaliClassToJava(baseClassName);
        }

        String massagedClassName = getClassForNameFromSmaliClass(javaClassName, dimensionCount);
        Class<?> klazz = ClassUtils.getClass(massagedClassName);
        Object result = Array.newInstance(klazz, dimensions);

        if (isLocalClass) {
            populateLocalInstanceArray(result, baseClassName);
        }

        return result;
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

    public static int getDimensionCount(String typeReference) {
        String baseClassName = typeReference.replace("[", "");

        return typeReference.length() - baseClassName.length();
    }

}
