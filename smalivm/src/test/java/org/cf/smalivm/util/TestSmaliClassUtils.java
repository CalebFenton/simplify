package org.cf.smalivm.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.cf.util.SmaliClassUtils;
import org.junit.Test;

public class TestSmaliClassUtils {

    private static final String SMALI_CLASS = "Lsome/package/Class;";
    private static final String PACKAGE_NAME = "some.package";
    private static final Map<String, String> smaliClassToJavaClass;
    private static final Map<String, Boolean> stringToIsPrimitive;

    static {
        smaliClassToJavaClass = new HashMap<String, String>();
        smaliClassToJavaClass.put("Lthis/is/Test;", "this.is.Test");
        smaliClassToJavaClass.put("[Lthis/is/Test;", "[Lthis.is.Test;");
        smaliClassToJavaClass.put("I", "I");
        smaliClassToJavaClass.put("[I", "[I");
        smaliClassToJavaClass.put("[[Z", "[[Z");

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
    }

    @Test
    public void TestGetPackageName() {
        String packageName = SmaliClassUtils.getPackageName(SMALI_CLASS);

        assertEquals(PACKAGE_NAME, packageName);
    }

    @Test
    public void TestIsPrimitiveType() {
        for (Entry<String, Boolean> entry : stringToIsPrimitive.entrySet()) {
            String string = entry.getKey();
            boolean expected = entry.getValue();
            boolean actual = SmaliClassUtils.isPrimitiveType(string);

            assertEquals("string = " + string, expected, actual);
        }
    }

    @Test
    public void TestJavaClassToSmali() {
        for (Entry<String, String> entry : smaliClassToJavaClass.entrySet()) {
            String javaClass = entry.getValue();
            String expected = entry.getKey();
            String actual = SmaliClassUtils.javaClassToSmali(javaClass);

            assertEquals(expected, actual);
        }
    }

    @Test
    public void TestSmaliClassToJava() {
        for (Entry<String, String> entry : smaliClassToJavaClass.entrySet()) {
            String smaliClass = entry.getKey();
            String expected = entry.getValue();
            String actual = SmaliClassUtils.smaliClassToJava(smaliClass);

            assertEquals(expected, actual);
        }
    }

}
