package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.junit.Test;

public class TestSPutOp {

    private static final String CLASS_NAME = "Lsput_test;";

    @Test
    public void testStaticPutBoolean() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, true, "Z");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myBoolean:Z", true);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutBoolean()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutByte() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, (byte) 0xFF, "B");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myByte:B", (byte) 0xFF);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutByte()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutChar() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, '!', "C");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myChar:C", '!');

        VMTester.testClassState(CLASS_NAME, "TestStaticPutChar()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutInt() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x42, "I");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myInt:I", 0x42);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutInt()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutObject() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "Do not pray for an easy life",
                        "Ljava/lang/String;");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life");

        VMTester.testClassState(CLASS_NAME, "TestStaticPutObject()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutShort() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, (short) 0x4242, "S");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myShort:S", (short) 0x4242);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutShort()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void testStaticPutWide() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0xFFFFFFFFFL, "J");
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                        CLASS_NAME, "myLong:J", 0xFFFFFFFFFL);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutWide()V", initial, classNameToExpectedFieldValue);
    }

}
