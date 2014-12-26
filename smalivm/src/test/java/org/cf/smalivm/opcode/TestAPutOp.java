package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestAPutOp {

    private static final String CLASS_NAME = "Laput_test;";

    @Test
    public void testArrayPut() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutWithShortIndex() {
        Short index = 0;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, index, "S", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutWithIndexOutOfBoundsReturnsUnknownValue() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 5, "I", 2, 4, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutBoolean() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, 0x1, "Z");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutBoolean()V", initial, expected);
    }

    @Test
    public void testArrayPutBooleanWithShortValue() {
        Short value = 0x1;
        TIntObjectMap<HeapItem> initial = VMTester
                        .buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, value, "Z");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutBoolean()V", initial, expected);
    }

    @Test
    public void testArrayPutByte() {
        Byte value = 0xf;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { value }, "[B");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutByte()V", initial, expected);
    }

    @Test
    public void testArrayPutByteFromInt() {
        int value = 0xf;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { (byte) value }, "[B");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutByte()V", initial, expected);
    }

    @Test
    public void testArrayPutChar() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2, '$', "C");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutChar()V", initial, expected);
    }

    @Test
    public void testArrayPutCharFromInt() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2, (int) '$',
                        "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutChar()V", initial, expected);
    }

    @Test
    public void testArrayPutObject() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance[1], "[" + CLASS_NAME, 1, 0,
                        "I", 2, new LocalInstance(CLASS_NAME), CLASS_NAME);
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) }, "[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutObject()V", initial, expected);
    }

    @Test
    public void testArrayPutShort() {
        Short value = 0x42;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { value }, "[S");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutShort()V", initial, expected);
    }

    @Test
    public void testArrayPutShortWithIntegerValue() {
        int value = 0x42;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { (short) value }, "[S");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutShort()V", initial, expected);
    }

    @Test
    public void testArrayPutWithUnknownIndex() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, new UnknownValue(), "I",
                        2, 5, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutUnknownValue() {
        // TODO: Ideally, setting an element unknown shouldn't set the entire array unknown. See APutOp for more
        // details.
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2,
                        new UnknownValue(), "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void testArrayPutWideWithLong() {
        Long value = 10000000000L;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[1], "[J", 1, 0, "I", 2, value, "J");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new long[] { value }, "[J");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

    @Test
    public void testArrayPutWideWithDouble() {
        Double value = 100000000000D;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new double[1], "[D", 1, 0, "I", 2, value, "D");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new double[] { value }, "[D");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

    @Test
    public void testArrayPutWideWithFloat() {
        Float value = 10.45F;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new float[1], "[F", 1, 0, "I", 2, value, "F");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new float[] { value }, "[F");

        VMTester.testMethodState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

}
