package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import java.util.logging.Logger;

import org.cf.simplify.Main;
import org.cf.smalivm.VMTester;
import org.junit.Test;

public class TestFillArrayDataOps {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfill_array_data_test;";

    @Test
    public void TestFillArrayDataInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[5]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[] { 1, 2, 3, 4, 5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataInt()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[3]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0,
                        new long[] { 0x1000000000L, 0x2000000000L, 0x3L });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataLong()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataBoolean() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new boolean[4]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new boolean[] { true, true, false, true });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataBoolean()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataByte() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[4]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new byte[] { 0xa, 0xb, 0xc, 0xd });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataByte()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new char[3]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new char[] { 'a', 'b', 'c' });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataChar()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[3]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new short[] { 100, 200, 5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataShort()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new float[2]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new float[] { 1, 2 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataFloat()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new double[2]);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new double[] { 0.1, 1.5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataDouble()V", initial, expected);
    }

}
