package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestArrayLengthOp {

    public static class TestObjectArrays {
        @Test
        public void testArrayLengthForEmptyIntegerArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Integer[] {});
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForIntegerArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Integer[] { 1, 2, 3 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForStringArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new String[] { "isn't", "this", "where" });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForUnknownValueOfIntegerType() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("[Ljava/lang/Integer;"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForUnknownValueOfPrimitiveType() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("[I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }
    }

    public static class TestPrimitiveArrays {
        @Test
        public void testArrayLengthForEmptyIntArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] {});
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForIntArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 1, 2, 3 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForLongArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[] { 1, 2, 3, 4 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 4);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void testArrayLengthForShortArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[] { 1, 2 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2);

            VMTester.testMethodState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

    }

    private static final String CLASS_NAME = "Larray_length_test;";

}
