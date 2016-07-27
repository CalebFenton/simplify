package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class AGetOpTest {

    private static final String CLASS_NAME = "Laget_test;";

    private VMState expected;
    private VMState initial;

    private static void testException(String methodDescriptor, Class<?> exceptionClass, VMState initial) {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial);

        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(exceptionClass, item.getValue().getClass());
        Assert.assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());

        assertFalse("Should not reach next instruction in non-exception execution path",
                graph.wasAddressReached(2));

        MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
        Assert.assertEquals(0, mState.getRegistersAssigned().length);
    }

    @Test
    public void canGet() {
        initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, 0, "I");
        expected.setRegisters(0, 0x42, "I");

        VMTester.test(CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetBoolean() {
        initial.setRegisters(0, new boolean[] { true }, "[Z", 1, 0, "I");
        expected.setRegisters(0, true, "Z");

        VMTester.test(CLASS_NAME, "getBoolean()V", initial, expected);
    }

    @Test
    public void canGetByte() {
        initial.setRegisters(0, new byte[] { 0xe }, "[B", 1, 0, "I");
        expected.setRegisters(0, (byte) 0xe, "B");

        VMTester.test(CLASS_NAME, "getByte()V", initial, expected);
    }

    @Test
    public void canGetChar() {
        initial.setRegisters(0, new char[] { 'a' }, "[C", 1, 0, "I");
        expected.setRegisters(0, 'a', "C");

        VMTester.test(CLASS_NAME, "getChar()V", initial, expected);
    }

    @Test
    public void canGetObject() {
        String objectValue = "stringy";
        String[] objectArray = new String[] { objectValue };
        String objectType = "Ljava/lang/String;";
        initial.setRegisters(0, objectArray, objectType, 1, 0, "I");
        expected.setRegisters(0, objectValue, objectType);

        VMTester.test(CLASS_NAME, "getObject()V", initial, expected);
    }

    @Test
    public void canGetShort() {
        initial.setRegisters(0, new short[] { 0x42 }, "[S", 1, 0, "I");
        expected.setRegisters(0, (short) 0x42, "S");

        VMTester.test(CLASS_NAME, "getShort()V", initial, expected);
    }

    @Test
    public void canGetUninitializedPrimitive() {
        initial.setRegisters(0, new int[1], "[I", 1, 0, "I");
        expected.setRegisters(0, (new int[1])[0], "I");

        VMTester.test(CLASS_NAME, "getUninitializedInt()V", initial, expected);
    }

    @Test
    public void canGetUnknownArray() {
        initial.setRegisters(0, new UnknownValue(), "[I", 1, 0, "I");
        expected.setRegisters(0, new UnknownValue(), "I");

        VMTester.test(CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetUnknownElement() {
        initial.setRegisters(0, new Object[] { new UnknownValue(), 5 }, "[I", 1, 0, "I");
        expected.setRegisters(0, new UnknownValue(), "I");

        VMTester.test(CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetUnknownIndex() {
        initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, new UnknownValue(), "I");
        expected.setRegisters(0, new UnknownValue(), "I");

        VMTester.test(CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void canGetWide() {
        initial.setRegisters(0, new long[] { 0x10000000000L }, "J", 1, 0, "I");
        expected.setRegisters(0, 0x10000000000L, "J");

        VMTester.test(CLASS_NAME, "getWide()V", initial, expected);
    }

    @Test
    public void canGetWithShortIndex() {
        Short index = 0;
        initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, index, "S");
        expected.setRegisters(index.intValue(), 0x42, "S");

        VMTester.test(CLASS_NAME, "get()V", initial, expected);
    }

    @Test
    public void nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, null, "[I", 1, 0, "I");
        testException("getWithCatch()V", NullPointerException.class, initial);
    }

    @Test
    public void outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
        initial.setRegisters(0, new int[5], "[I", 1, 10, "I");
        testException("getWithCatch()V", ArrayIndexOutOfBoundsException.class, initial);
    }

    @Before
    public void setUp() {
        initial = new VMState();
        expected = new VMState();
    }

}
