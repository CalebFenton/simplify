package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestSGetOp {

    private static final String CLASS_NAME = "Lsget_test;";

    @Test
    public void testStaticGetBoolean() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myBoolean:Z", true);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, true);

        VMTester.testMethodState(CLASS_NAME, "StaticGetBoolean()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetByte() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myByte:B", (byte) 0xf);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (byte) 0xf);

        VMTester.testMethodState(CLASS_NAME, "StaticGetByte()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetChar() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myChar:C", 'x');
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 'x');

        VMTester.testMethodState(CLASS_NAME, "StaticGetChar()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetInt() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myInt:I", 0x42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42);

        VMTester.testMethodState(CLASS_NAME, "StaticGetInt()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetObject() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myString:Ljava/lang/String;", "They tried and died.");
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, "They tried and died.");

        VMTester.testMethodState(CLASS_NAME, "StaticGetObject()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetShort() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myShort:S", (short) 0x100);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (short) 0x100);

        VMTester.testMethodState(CLASS_NAME, "StaticGetShort()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetUnknownClassField() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testMethodState(CLASS_NAME, "StaticGetUnknownClassField()V", expected);
    }

    @Test
    public void testStaticGetWhitelistedClassField() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, Integer.MAX_VALUE);

        VMTester.testMethodState(CLASS_NAME, "StaticGetWhitelistedClassField()V", expected);
    }

    @Test
    public void testStaticGetWide() {
        Map<String, Map<String, Object>> classNameToFieldValue = VMTester.buildClassNameToFieldValue(CLASS_NAME,
                        "myLong:J", 0xFFFFFFFFFL);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0xFFFFFFFFFL);

        VMTester.testMethodState(CLASS_NAME, "StaticGetWide()V", expected, classNameToFieldValue);
    }

}
