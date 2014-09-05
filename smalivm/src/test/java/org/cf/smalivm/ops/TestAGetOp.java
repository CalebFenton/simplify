package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestAGetOp {

    private static final String CLASS_NAME = "Laget_test;";

    @Test
    public void TestArrayGet() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetBoolean() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new boolean[] { true }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, true);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetBoolean()V", initial, expected);
    }

    @Test
    public void TestArrayGetByte() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[] { 0xe }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (byte) 0xe);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetByte()V", initial, expected);
    }

    @Test
    public void TestArrayGetChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new char[] { 'a' }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 'a');

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetChar()V", initial, expected);
    }

    @Test
    public void TestArrayGetObject() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetObject()V", initial, expected);
    }

    @Test
    public void TestArrayGetShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[] { 0x42 }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (short) 0x42);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetShort()V", initial, expected);
    }

    @Test
    public void TestArrayGetUninitializedPrimitive() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (new int[1])[0]);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetUninitializedInt()V", initial, expected);
    }

    @Test
    public void TestArrayGetUnknownArray() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetUnknownElement() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Object[] { new UnknownValue("I"), 5 }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetUnknownIndex() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, 1, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetWide() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[] { 0x10000000000L }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000000000L);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetWide()V", initial, expected);
    }

}
