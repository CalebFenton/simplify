package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArrayLengthOpTest {

    private static final String CLASS_NAME = "Larray_length_test;";

    public static class ObjectArrays {

        private VMState expected;
        private VMState initial;

        @Test
        public void canGetLengthForEmptyIntegerArray() {
            initial.setRegisters(0, new Integer[] {}, "[I");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForIntegerArray() {
            initial.setRegisters(0, new Integer[] { 1, 2, 3 }, "[I");
            expected.setRegisters(0, 3, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForStringArray() {
            initial.setRegisters(0, new String[] { "isn't", "this", "where" }, "[I");
            expected.setRegisters(0, 3, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForUnknownValueOfIntegerType() {
            initial.setRegisters(0, new UnknownValue(), "[Ljava/lang/Integer;");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForUnknownValueOfPrimitiveType() {
            initial.setRegisters(0, new UnknownValue(), "[I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }
    }

    public static class PrimitiveArrays {

        private VMState expected;
        private VMState initial;

        private static void testException(String methodDescriptor, Class<?> exceptionClass, String exceptionMessage,
                                          VMState initial) {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial);

            HeapItem item = graph.getTerminatingRegisterConsensus(0);
            assertEquals(exceptionClass, item.getValue().getClass());
            assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
            assertEquals(exceptionMessage, ((Throwable) item.getValue()).getMessage());

            assertFalse(graph.wasAddressReached(1), "Should not reach next instruction in non-exception execution path");

            MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
            assertEquals(0, mState.getRegistersAssigned().length);
        }

        @Test
        public void canGetLengthForEmptyIntArray() {
            initial.setRegisters(0, new int[] {}, "[I");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForIntArray() {
            initial.setRegisters(0, new int[] { 1, 2, 3 }, "[I");
            expected.setRegisters(0, 3, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForLongArray() {
            initial.setRegisters(0, new long[] { 1, 2, 3, 4 }, "[J");
            expected.setRegisters(0, 4, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void canGetLengthForShortArray() {
            initial.setRegisters(0, new short[] { 1, 2 }, "[S");
            expected.setRegisters(0, 2, "I");

            VMTester.test(CLASS_NAME, "getLength()V", initial, expected);
        }

        @Test
        public void nullArrayThrowsExpectedException() {
            initial.setRegisters(0, null, "[S");
            testException("getLengthWithCatch()V", NullPointerException.class, "Attempt to get length of null array",
                    initial);
        }

        @BeforeEach
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }
    }

}
