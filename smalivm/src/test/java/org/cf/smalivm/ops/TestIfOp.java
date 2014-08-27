package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestIfOp {

    private static final String CLASS_NAME = "Lif_test;";

    private static final int IF = 0;
    private static final int NOP = 2;
    private static final int RETURN = 3;

    public static class TestIdenticalPrimitiveValueTypes {
        @Test
        public void TestIfEqualsTrue() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualsFalse() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfNotEqualsTrue() {
            String methodSignature = "IfNotEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfNotEqualsFalse() {
            String methodSignature = "IfNotEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfLessThanTrue() {
            String methodSignature = "IfLessThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfLessThanFalse() {
            String methodSignature = "IfLessThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfGreaterOrEqualTrue() {
            String methodSignature = "IfGreaterOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfGreaterOrEqualFalse() {
            String methodSignature = "IfGreaterOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfGreaterThanTrue() {
            String methodSignature = "IfGreaterThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfGreaterThanFalse() {
            String methodSignature = "IfGreaterThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfLessOrEqualTrue() {
            String methodSignature = "IfLessOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfLessOrEqualFalse() {
            String methodSignature = "IfLessOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfEqualZeroTrue() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualZeroFalse() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfNotEqualZeroTrue() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfNotEqualZeroFalse() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfLessThanZeroTrue() {
            String methodSignature = "IfLessThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfLessThanZeroFalse() {
            String methodSignature = "IfLessThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfGreaterOrEqualZeroTrue() {
            String methodSignature = "IfGreaterOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfGreaterOrEqualZeroFalse() {
            String methodSignature = "IfGreaterOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfGreaterThanZeroTrue() {
            String methodSignature = "IfGreaterThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfGreaterThanZeroFalse() {
            String methodSignature = "IfGreaterThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void TestIfLessOrEqualZeroTrue() {
            String methodSignature = "IfLessOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfLessOrEqualZeroFalse() {
            String methodSignature = "IfLessOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }
    }

    public static class TestValueTypeCombinations {
        @Test
        public void TestIfEqualWithIntAndByte() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (byte) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndShort() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (long) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithIntAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndShort() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithByteAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithShortAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (long) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithShortAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithShortAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithShortAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithShortAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithLongAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithLongAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithLongAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithLongAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithFloatAndDouble() {
            // 3.2 is tricky, 3.2F != 3.2D && Double.compareTo(3.2F, 3.2D)
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3.2F, 1, 3.2D);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithFloatAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (float) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithFloatAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (float) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithDoubleAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (double) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithDoubleAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (double) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfEqualWithBooleanAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, true, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }
    }

    public static class TestCompareObjectReferences {
        @Test
        public void TestIdenticalObjectReferencesAreEqual() {
            String methodSignature = "IfEqual()V";
            String obj = "object";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, obj, 1, obj);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestDifferentObjectReferencesAreNotEqual() {
            String methodSignature = "IfNotEqual()V";
            String obj1 = "object";
            // Need to get crafty or javac will be smart enough to use same literal for both objects
            String obj2 = new StringBuilder(obj1).toString();
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, obj1, 1, obj2);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, obj1, 1, new int[] { 1 });
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfNullEqualZero() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, null);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfObjectNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "object");
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void TestIfPrimitiveArrayNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[] { 0x1 });
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }
    }

}
