package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import java.util.Map;

import org.cf.smalivm.VMTester;
import org.junit.Test;

public class TestSPutOp {

    private static final String CLASS_NAME = "Lsput_test;";

    @Test
    public void TestStaticPutBoolean() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, true);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myBoolean:Z", true);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutBoolean()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutByte() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 0xFF);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myByte:B", (byte) 0xFF);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutByte()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, '!');
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myChar:C", '!');

        VMTester.testClassState(CLASS_NAME, "TestStaticPutChar()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myInt:I", 0x42);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutInt()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutObject() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "Do not pray for an easy life");
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life");

        VMTester.testClassState(CLASS_NAME, "TestStaticPutObject()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 0x4242);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myShort:S", (short) 0x4242);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutShort()V", initial, classNameToExpectedFieldValue);
    }

    @Test
    public void TestStaticPutWide() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0xFFFFFFFFFL);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldValue(
                        CLASS_NAME, "myLong:J", 0xFFFFFFFFFL);

        VMTester.testClassState(CLASS_NAME, "TestStaticPutWide()V", initial, classNameToExpectedFieldValue);
    }

}
