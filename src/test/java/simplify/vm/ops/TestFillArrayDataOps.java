package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestFillArrayDataOps {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfill_array_data_test;";

    @Test
    public void TestFillArrayDataInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[5]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[] { 1, 2, 3, 4, 5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataInt()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new long[3]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new long[] { 0x1000000000L, 0x2000000000L,
                        0x3L });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataLong()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataBoolean() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new boolean[4]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new boolean[] { true, true, false, true });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataBoolean()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataByte() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new byte[4]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new byte[] { 0xa, 0xb, 0xc, 0xd });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataByte()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataChar() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new char[3]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new char[] { 'a', 'b', 'c' });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataChar()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataShort() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new short[3]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new short[] { 100, 200, 5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataShort()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new float[2]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new float[] { 1, 2 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataFloat()V", initial, expected);
    }

    @Test
    public void TestFillArrayDataDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new double[2]);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new double[] { 0.1, 1.5 });

        VMTester.testState(CLASS_NAME, "TestFillArrayDataDouble()V", initial, expected);
    }

}
