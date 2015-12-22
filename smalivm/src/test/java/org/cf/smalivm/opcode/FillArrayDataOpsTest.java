package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.junit.Test;

public class FillArrayDataOpsTest {

    private static final String CLASS_NAME = "Lfill_array_data_test;";

    @Test
    public void testFillArrayDataBoolean() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[4], "[Z");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true, true, false, true },
                        "[Z");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataBoolean()V", initial, expected);
    }

    @Test
    public void testFillArrayDataByte() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[4], "[B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { 0xa, 0xb, 0xc, 0xd }, "[B");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataByte()V", initial, expected);
    }

    @Test
    public void testFillArrayDataChar() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[3], "[C");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { 'a', 'b', 'c' }, "[C");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataChar()V", initial, expected);
    }

    @Test
    public void testFillArrayDataDouble() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new double[2], "[D");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new double[] { 0.1, 1.5 }, "[D");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataDouble()V", initial, expected);
    }

    @Test
    public void testFillArrayDataFloat() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new float[2], "[F");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new float[] { 1, 2 }, "[F");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataFloat()V", initial, expected);
    }

    @Test
    public void testFillArrayDataInt() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[5], "[I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 1, 2, 3, 4, 5 }, "[I");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataInt()V", initial, expected);
    }

    @Test
    public void testFillArrayDataLong() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[3], "[J");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new long[] { 0x1000000000L, 0x2000000000L,
                        0x3L }, "[J");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataLong()V", initial, expected);
    }

    @Test
    public void testFillArrayDataShort() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[3], "[S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { 100, 200, 5 }, "[S");

        VMTester.testMethodState(CLASS_NAME, "TestFillArrayDataShort()V", initial, expected);
    }

}
