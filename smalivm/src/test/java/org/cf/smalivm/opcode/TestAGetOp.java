package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestAGetOp {

    private static final String CLASS_NAME = "Laget_test;";

    @Test
    public void testArrayGet() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x42, "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetWithIndexOutOfBoundsReturnsUnknownValue() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, 1, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetWithShortIndex() {
        Short index = 0;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, index, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(index.intValue(), 0x42, "S");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetBoolean() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetBoolean()V", initial, expected);
    }

    @Test
    public void testArrayGetByte() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[] { 0xe }, "[B", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (byte) 0xe, "B");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetByte()V", initial, expected);
    }

    @Test
    public void testArrayGetChar() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[] { 'a' }, "[C", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 'a', "C");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetChar()V", initial, expected);
    }

    @Test
    public void testArrayGetObject() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) }, CLASS_NAME, 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetObject()V", initial, expected);
    }

    @Test
    public void testArrayGetShort() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[] { 0x42 }, "[S", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (short) 0x42, "S");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetShort()V", initial, expected);
    }

    @Test
    public void testArrayGetUninitializedPrimitive() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (new int[1])[0], "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetUninitializedInt()V", initial, expected);
    }

    @Test
    public void testArrayGetUnknownArray() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "[I", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetUnknownElement() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new Object[] { new UnknownValue(), 5 }, "[I",
                        1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetUnknownIndex() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1,
                        new UnknownValue(), "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void testArrayGetWide() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[] { 0x10000000000L }, "J", 1, 0, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000000000L, "J");

        VMTester.testMethodState(CLASS_NAME, "TestArrayGetWide()V", initial, expected);
    }

}
