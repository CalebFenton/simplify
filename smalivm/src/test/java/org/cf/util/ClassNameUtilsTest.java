package org.cf.util;

import org.cf.util.ClassNameUtils.TypeFormat;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class ClassNameUtilsTest {

    private static final Map<String, String> internalNameToBinaryName;
    private static final Map<String, Boolean> internalNameToIsPrimitive;
    private static final Map<String, String> internalNameToSourceName;
    private static final Map<String, Class<?>> internalPrimitiveToWrapperClass;

    static {
        internalNameToBinaryName = new HashMap<String, String>();
        internalNameToBinaryName.put("Lthis/is/Test;", "this.is.Test");
        internalNameToBinaryName.put("[Lthis/is/Test;", "[Lthis.is.Test;");
        internalNameToBinaryName.put("I", "int");
        internalNameToBinaryName.put("B", "byte");
        internalNameToBinaryName.put("V", "void");
        internalNameToBinaryName.put("[I", int[].class.getName()); // [I
        internalNameToBinaryName.put("[[Z", boolean[][].class.getName()); // [[Z

        internalNameToSourceName = new HashMap<String, String>();
        internalNameToSourceName.put("Lthis/is/Test;", "this.is.Test");
        internalNameToSourceName.put("[Lthis/is/Test;", "this.is.Test[]");
        internalNameToSourceName.put("I", "int");
        internalNameToSourceName.put("B", "byte");
        internalNameToSourceName.put("V", "void");
        internalNameToSourceName.put("[I", "int[]");
        internalNameToSourceName.put("[[Z", "boolean[][]");

        internalNameToIsPrimitive = new HashMap<String, Boolean>();
        internalNameToIsPrimitive.put("Lsome/class;", false);
        internalNameToIsPrimitive.put("[Lsome/class;", false);
        internalNameToIsPrimitive.put("I", true);
        internalNameToIsPrimitive.put("Z", true);
        internalNameToIsPrimitive.put("C", true);
        internalNameToIsPrimitive.put("S", true);
        internalNameToIsPrimitive.put("D", true);
        internalNameToIsPrimitive.put("F", true);
        internalNameToIsPrimitive.put("J", true);
        internalNameToIsPrimitive.put("B", true);
        internalNameToIsPrimitive.put("V", true);

        // Arrays are mutable objects, not primitives
        internalNameToIsPrimitive.put("[I", false);
        internalNameToIsPrimitive.put("[[I", false);

        internalPrimitiveToWrapperClass = new HashMap<String, Class<?>>();
        internalPrimitiveToWrapperClass.put("I", Integer.class);
        internalPrimitiveToWrapperClass.put("Z", Boolean.class);
        internalPrimitiveToWrapperClass.put("C", Character.class);
        internalPrimitiveToWrapperClass.put("S", Short.class);
        internalPrimitiveToWrapperClass.put("D", Double.class);
        internalPrimitiveToWrapperClass.put("F", Float.class);
        internalPrimitiveToWrapperClass.put("J", Long.class);
        internalPrimitiveToWrapperClass.put("B", Byte.class);
        internalPrimitiveToWrapperClass.put("[I", Integer[].class);
        internalPrimitiveToWrapperClass.put("[[I", Integer[][].class);
    }

    @Test
    public void canConvertBinaryToInternal() {
        for (Entry<String, String> entry : internalNameToBinaryName.entrySet()) {
            String className = entry.getValue();
            String expected = entry.getKey();
            String actual = ClassNameUtils.binaryToInternal(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertInternalToBinary() {
        for (Entry<String, String> entry : internalNameToBinaryName.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue();
            String actual = ClassNameUtils.internalToBinary(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertInternalToSource() {
        for (Entry<String, String> entry : internalNameToSourceName.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue();
            String actual = ClassNameUtils.internalToSource(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertSourceToBinary() {
        for (Entry<String, String> entry : internalNameToSourceName.entrySet()) {
            String className = entry.getValue();
            String expected = internalNameToBinaryName.get(entry.getKey());
            String actual = ClassNameUtils.sourceToBinary(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void canConvertSurceToInternal() {
        for (Entry<String, String> entry : internalNameToSourceName.entrySet()) {
            String className = entry.getValue();
            String expected = entry.getKey();
            String actual = ClassNameUtils.sourceToInternal(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void canDetermineIsPrimitive() {
        for (Entry<String, Boolean> entry : internalNameToIsPrimitive.entrySet()) {
            String className = entry.getKey();
            boolean expected = entry.getValue();
            boolean actual = ClassNameUtils.isPrimitive(className);

            assertEquals("class: " + className, expected, actual);
        }
    }

    @Test
    public void canGetComponentBase() {
        String componentBase;

        componentBase = ClassNameUtils.getComponentBase("[[[[[[Lsome/class;");
        assertEquals("Lsome/class;", componentBase);

        componentBase = ClassNameUtils.getComponentBase("[[[Lsome.class;");
        assertEquals("Lsome.class;", componentBase);

        componentBase = ClassNameUtils.getComponentBase("some.class[][][]");
        assertEquals("some.class", componentBase);
    }

    @Test
    public void canGetComponentType() {
        String componentType;

        componentType = ClassNameUtils.getComponentType("[[[[[[Lsome/class;");
        assertEquals("[[[[[Lsome/class;", componentType);

        componentType = ClassNameUtils.getComponentType("[[[Lsome.class;");
        assertEquals("[[Lsome.class;", componentType);

        componentType = ClassNameUtils.getComponentType("some.class[][][]");
        assertEquals("some.class[][]", componentType);
    }

    @Test
    public void canGetPackageName() {
        assertEquals("some.package", ClassNameUtils.getPackageName("Lsome/package/Class;"));
        assertEquals("", ClassNameUtils.getPackageName("LSomeClass;"));
        assertEquals("", ClassNameUtils.getPackageName("SomeClass"));
        assertEquals("", ClassNameUtils.getPackageName("SomeClass[]"));
        assertEquals("", ClassNameUtils.getPackageName("[LSomeClass;"));
    }

    @Test
    public void canGetPrimitive() {
        String primitive;
        primitive = ClassNameUtils.getPrimitive("Ljava/lang/Long;");
        assertEquals("J", primitive);

        primitive = ClassNameUtils.getPrimitive("Ljava.lang.Long;");
        assertEquals("J", primitive);

        primitive = ClassNameUtils.getPrimitive("java.lang.Long");
        assertEquals("J", primitive);

        primitive = ClassNameUtils.getPrimitive("[[[Ljava.lang.Integer;");
        assertEquals("[[[I", primitive);
    }

    @Test
    public void canGetPrimitiveWrapper() {
        for (Entry<String, Class<?>> entry : internalPrimitiveToWrapperClass.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue().getName();
            String actual = ClassNameUtils.getWrapper(className);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void getDimensionCountOfRandomRankArrayReturnsExpected() {
        Random rng = new Random();

        for (int run = 0; run < 100; run++) {
            StringBuilder sb = new StringBuilder();
            int expected = rng.nextInt(10);
            for (int i = 0; i < expected; i++) {
                sb.append('[');
            }
            sb.append("Lsome/Type;");
            String typeReference = sb.toString();
            int actual = ClassNameUtils.getDimensionCount(typeReference);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void getDimensionCountWithSourceFormat() {
        int actual = ClassNameUtils.getDimensionCount("java.lang.Object[][][]");
        assertEquals(3, actual);
    }

    @Test
    public void toFormatTest() {
        assertEquals("java.lang.Object", ClassNameUtils.toFormat("Ljava/lang/Object;", TypeFormat.SOURCE));
        assertEquals("java.lang.Object", ClassNameUtils.toFormat("java.lang.Object", TypeFormat.SOURCE));
        assertEquals("java.lang.Object[]", ClassNameUtils.toFormat("[Ljava.lang.Object;", TypeFormat.SOURCE));
        assertEquals("SomeClass", ClassNameUtils.toFormat("LSomeClass;", TypeFormat.SOURCE));
        assertEquals("SomeClass", ClassNameUtils.toFormat("SomeClass", TypeFormat.SOURCE));
        assertEquals("SomeClass[]", ClassNameUtils.toFormat("[LSomeClass;", TypeFormat.SOURCE));

        assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("java.lang.Object[]", TypeFormat.INTERNAL));
        assertEquals("I", ClassNameUtils.toFormat("int", TypeFormat.INTERNAL));
        assertEquals("[I", ClassNameUtils.toFormat("int[]", TypeFormat.INTERNAL));
        assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("[Ljava.lang.Object;", TypeFormat.INTERNAL));
        assertEquals("[Ljava/lang/Object;", ClassNameUtils.toFormat("[Ljava/lang/Object;", TypeFormat.INTERNAL));

        assertEquals("[Ljava.lang.Object;", ClassNameUtils.toFormat("java.lang.Object[]", TypeFormat.BINARY));
        ;
        assertEquals("java.lang.Object", ClassNameUtils.toFormat("java.lang.Object", TypeFormat.BINARY));
        ;
        assertEquals("java.lang.Object", ClassNameUtils.toFormat("Ljava/lang/Object;", TypeFormat.BINARY));
        ;
        assertEquals("int", ClassNameUtils.toFormat("I", TypeFormat.BINARY));
        assertEquals("[I", ClassNameUtils.toFormat("[I", TypeFormat.BINARY));
    }

}
