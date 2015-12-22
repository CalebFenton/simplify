package org.cf.smalivm.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cf.util.SmaliClassUtils;
import org.junit.Test;

public class SmaliClassUtilsTest {

    private static final String SMALI_CLASS = "Lsome/package/Class;";
    private static final String PACKAGE_NAME = "some.package";
    private static final Map<String, String> smaliClassToJavaClassName;
    private static final Map<String, Boolean> stringToIsPrimitive;
    private static final Map<String, Class<?>> primitiveTypeToWrapperClass;

    static {
        smaliClassToJavaClassName = new HashMap<String, String>();
        smaliClassToJavaClassName.put("Lthis/is/Test;", "this.is.Test");
        smaliClassToJavaClassName.put("[Lthis/is/Test;", "[Lthis.is.Test;");
        smaliClassToJavaClassName.put("I", "int");
        smaliClassToJavaClassName.put("B", "byte");
        smaliClassToJavaClassName.put("[I", "[I");
        smaliClassToJavaClassName.put("[[Z", "[[Z");

        stringToIsPrimitive = new HashMap<String, Boolean>();
        stringToIsPrimitive.put("Lsome/class;", false);
        stringToIsPrimitive.put("[Lsome/class;", false);
        stringToIsPrimitive.put("I", true);
        stringToIsPrimitive.put("Z", true);
        stringToIsPrimitive.put("C", true);
        stringToIsPrimitive.put("S", true);
        stringToIsPrimitive.put("D", true);
        stringToIsPrimitive.put("F", true);
        stringToIsPrimitive.put("J", true);
        stringToIsPrimitive.put("B", true);
        stringToIsPrimitive.put("[I", true);
        stringToIsPrimitive.put("[[I", true);

        primitiveTypeToWrapperClass = new HashMap<String, Class<?>>();
        primitiveTypeToWrapperClass.put("I", Integer.class);
        primitiveTypeToWrapperClass.put("Z", Boolean.class);
        primitiveTypeToWrapperClass.put("C", Character.class);
        primitiveTypeToWrapperClass.put("S", Short.class);
        primitiveTypeToWrapperClass.put("D", Double.class);
        primitiveTypeToWrapperClass.put("F", Float.class);
        primitiveTypeToWrapperClass.put("J", Long.class);
        primitiveTypeToWrapperClass.put("B", Byte.class);
        primitiveTypeToWrapperClass.put("[I", Integer[].class);
        primitiveTypeToWrapperClass.put("[[I", Integer[][].class);
    }

    @Test
    public void testGetPackageName() {
        String packageName = SmaliClassUtils.getPackageName(SMALI_CLASS);

        assertEquals(PACKAGE_NAME, packageName);
    }

    @Test
    public void testIsPrimitiveType() {
        for (Entry<String, Boolean> entry : stringToIsPrimitive.entrySet()) {
            String string = entry.getKey();
            boolean expected = entry.getValue();
            boolean actual = SmaliClassUtils.isPrimitiveType(string);

            assertEquals("string = " + string, expected, actual);
        }
    }

    @Test
    public void testJavaClassToSmali() {
        for (Entry<String, String> entry : smaliClassToJavaClassName.entrySet()) {
            String javaClass = entry.getValue();
            String expected = entry.getKey();
            String actual = SmaliClassUtils.javaClassToSmali(javaClass);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSmaliClassToJava() {
        for (Entry<String, String> entry : smaliClassToJavaClassName.entrySet()) {
            String smaliClass = entry.getKey();
            String expected = entry.getValue();
            String actual = SmaliClassUtils.smaliClassToJava(smaliClass);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void testSmaliPrimitiveToJavaWrapper() {
        for (Entry<String, Class<?>> entry : primitiveTypeToWrapperClass.entrySet()) {
            String className = entry.getKey();
            String expected = entry.getValue().getName();
            String actual = SmaliClassUtils.smaliPrimitiveToJavaWrapper(className);

            assertEquals(expected, actual);
        }
    }

}
