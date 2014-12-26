package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestSGetOp {

    private static final String CLASS_NAME = "Lsget_test;";

    @Test
    public void testStaticGetBoolean() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myBoolean:Z", true);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z");

        VMTester.testMethodState(CLASS_NAME, "StaticGetBoolean()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetByte() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myByte:B", (byte) 0xf);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (byte) 0xf, "B");

        VMTester.testMethodState(CLASS_NAME, "StaticGetByte()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetChar() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myChar:C", 'x');
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 'x', "C");

        VMTester.testMethodState(CLASS_NAME, "StaticGetChar()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetInt() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myInt:I", 0x42);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x42, "I");

        VMTester.testMethodState(CLASS_NAME, "StaticGetInt()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetObject() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myString:Ljava/lang/String;", "They tried and died.");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, "They tried and died.", "Ljava/lang/String;");

        VMTester.testMethodState(CLASS_NAME, "StaticGetObject()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetShort() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myShort:S", (short) 0x100);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (short) 0x100, "S");

        VMTester.testMethodState(CLASS_NAME, "StaticGetShort()V", expected, classNameToFieldValue);
    }

    @Test
    public void testStaticGetUnknownClassField() {
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

        VMTester.testMethodState(CLASS_NAME, "StaticGetUnknownClassField()V", expected);
    }

    @Test
    public void testStaticGetWhitelistedClassField() {
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, Integer.MAX_VALUE, "I");

        VMTester.testMethodState(CLASS_NAME, "StaticGetWhitelistedClassField()V", expected);
    }

    @Test
    public void testStaticGetWide() {
        Map<String, Map<String, HeapItem>> classNameToFieldValue = VMTester.buildClassNameToFieldItem(CLASS_NAME,
                        "myLong:J", 0xFFFFFFFFFL);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0xFFFFFFFFFL, "J");

        VMTester.testMethodState(CLASS_NAME, "StaticGetWide()V", expected, classNameToFieldValue);
    }

}
