package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestAPutOp {

    private static final String CLASS_NAME = "Laput_test;";

    @Test
    public void testArrayPut() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, 4);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[] { 4 });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutBoolean() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new boolean[1], 1, 0, 2, 0x1);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new boolean[] { true });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutBoolean()V", initial, expected);
    }

    @Test
    public void testArrayPutByte() {
        Byte b = 0xf;
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[1], 1, 0, 2, b);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new byte[] { 0xf });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutByte()V", initial, expected);
    }

    @Test
    public void testArrayPutChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new char[1], 1, 0, 2, (int) '$');
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new char[] { '$' });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutChar()V", initial, expected);
    }

    @Test
    public void testArrayPutObject() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance[1], 1, 0, 2,
                        new LocalInstance(CLASS_NAME));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutObject()V", initial, expected);
    }

    @Test
    public void testArrayPutShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[1], 1, 0, 2, 0x42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new short[] { 0x42 });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutShort()V", initial, expected);
    }

    @Test
    public void testArrayPutUnkownIndex() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, new UnknownValue("I"), 2, 5);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutUnkownValue() {
        // Ideally, setting an element unknown shouldn't set the entire array unknown. See APutOp for more details.
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutWide() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[1], 1, 0, 2, 10000000000L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new long[] { 10000000000L });

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

}
