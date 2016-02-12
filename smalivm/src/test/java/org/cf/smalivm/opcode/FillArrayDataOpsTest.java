package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.junit.Before;
import org.junit.Test;

public class FillArrayDataOpsTest {

    private static final String CLASS_NAME = "Lfill_array_data_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void canFillArrayDataBoolean() {
        initial.setRegisters(0, new boolean[4], "[Z");
        expected.setRegisters(0, new boolean[] { true, true, false, true }, "[Z");

        VMTester.test(CLASS_NAME, "fillArrayDataBoolean()V", initial, expected);
    }

    @Test
    public void canFillArrayDataByte() {
        initial.setRegisters(0, new byte[4], "[B");
        expected.setRegisters(0, new byte[] { 0xa, 0xb, 0xc, 0xd }, "[B");

        VMTester.test(CLASS_NAME, "fillArrayDataByte()V", initial, expected);
    }

    @Test
    public void canFillArrayDataChar() {
        initial.setRegisters(0, new char[3], "[C");
        expected.setRegisters(0, new char[] { 'a', 'b', 'c' }, "[C");

        VMTester.test(CLASS_NAME, "fillArrayDataChar()V", initial, expected);
    }

    @Test
    public void canFillArrayDataDouble() {
        initial.setRegisters(0, new double[2], "[D");
        expected.setRegisters(0, new double[] { 0.1, 1.5 }, "[D");

        VMTester.test(CLASS_NAME, "fillArrayDataDouble()V", initial, expected);
    }

    @Test
    public void canFillArrayDataFloat() {
        initial.setRegisters(0, new float[2], "[F");
        expected.setRegisters(0, new float[] { 1, 2 }, "[F");

        VMTester.test(CLASS_NAME, "fillArrayDataFloat()V", initial, expected);
    }

    @Test
    public void canFillArrayDataInt() {
        initial.setRegisters(0, new int[5], "[I");
        expected.setRegisters(0, new int[] { 1, 2, 3, 4, 5 }, "[I");

        VMTester.test(CLASS_NAME, "fillArrayDataInt()V", initial, expected);
    }

    @Test
    public void canFillArrayDataLong() {
        initial.setRegisters(0, new long[3], "[J");
        expected.setRegisters(0, new long[] { 0x1000000000L, 0x2000000000L, 0x3L }, "[J");

        VMTester.test(CLASS_NAME, "fillArrayDataLong()V", initial, expected);
    }

    @Test
    public void canFillArrayDataShort() {
        initial.setRegisters(0, new short[3], "[S");
        expected.setRegisters(0, new short[] { 100, 200, 5 }, "[S");

        VMTester.test(CLASS_NAME, "fillArrayDataShort()V", initial, expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
