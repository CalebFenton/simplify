package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestArrayLengthOp {

    private static final String CLASS_NAME = "Larray_length_test;";

    public static class TestPrimitiveArrays {
        @Test
        public void TestArrayLengthForIntArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 1, 2, 3 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForEmptyIntArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] {});
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForShortArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new short[] { 1, 2 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForLongArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new long[] { 1, 2, 3, 4 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 4);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

    }

    public static class TestObjectArrays {
        @Test
        public void TestArrayLengthForIntegerArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Integer[] { 1, 2, 3 });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForEmptyIntegerArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Integer[] {});
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForStringArray() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new String[] { "isn't", "this", "where" });
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3);

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForUnknownValueOfPrimitiveType() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("[I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }

        @Test
        public void TestArrayLengthForUnknownValueOfIntegerType() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("[Ljava/lang/Integer;"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "ArrayLength()V", initial, expected);
        }
    }

}
