package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestIfOp {

    public static class TestCompareObjectReferences {
        @Test
        public void testDifferentObjectReferencesAreNotEqual() {
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
        public void testIdenticalObjectReferencesAreEqual() {
            String methodSignature = "IfEqual()V";
            String obj = "object";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, obj, 1, obj);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfFalseBooleanEqualZero() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Boolean(false));
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfZeroIntegerEqualZero() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Integer(0));
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
	    public void testIfZeroIntegerEqualZeroWithByteTrue() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 0x0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
	    public void testIfZeroIntegerEqualZeroWithByteFalse() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 0x7);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfNullEqualZero() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, null);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfObjectNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "object");
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfPrimitiveArrayNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new byte[] { 0x1 });
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfTrueBooleanNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new Boolean(true));
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }
    }

    public static class TestIdenticalPrimitiveValueTypes {
        @Test
        public void testIfEqualsFalse() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfEqualsTrue() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualZeroFalse() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfEqualZeroTrue() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfGreaterOrEqualFalse() {
            String methodSignature = "IfGreaterOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfGreaterOrEqualTrue() {
            String methodSignature = "IfGreaterOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfGreaterOrEqualZeroFalse() {
            String methodSignature = "IfGreaterOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfGreaterOrEqualZeroTrue() {
            String methodSignature = "IfGreaterOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfGreaterThanFalse() {
            String methodSignature = "IfGreaterThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfGreaterThanTrue() {
            String methodSignature = "IfGreaterThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfGreaterThanZeroFalse() {
            String methodSignature = "IfGreaterThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfGreaterThanZeroTrue() {
            String methodSignature = "IfGreaterThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfLessOrEqualFalse() {
            String methodSignature = "IfLessOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfLessOrEqualTrue() {
            String methodSignature = "IfLessOrEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfLessOrEqualZeroFalse() {
            String methodSignature = "IfLessOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfLessOrEqualZeroTrue() {
            String methodSignature = "IfLessOrEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });

            initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfLessThanFalse() {
            String methodSignature = "IfLessThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfLessThanTrue() {
            String methodSignature = "IfLessThan()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfLessThanZeroFalse() {
            String methodSignature = "IfLessThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfLessThanZeroTrue() {
            String methodSignature = "IfLessThanZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfNotEqualsFalse() {
            String methodSignature = "IfNotEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfNotEqualsTrue() {
            String methodSignature = "IfNotEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfNotEqualZeroFalse() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
        }

        @Test
        public void testIfNotEqualZeroTrue() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }
    }

    public static class TestValueTypeCombinations {
        @Test
        public void testIfEqualWithBooleanAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, true, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithByteAndShort() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (byte) 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithDoubleAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (double) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithDoubleAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (double) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithFloatAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (float) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithFloatAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (float) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithFloatAndDouble() {
            // 3.2 is tricky, 3.2F != 3.2D && Double.compareTo(3.2F, 3.2D)
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3.2F, 1, 3.2D);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndByte() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (byte) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (long) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithIntAndShort() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1, 1, (short) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithLongAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithLongAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithLongAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithLongAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (long) 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithShortAndBoolean() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithShortAndChar() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (char) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithShortAndDouble() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (double) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithShortAndFloat() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (float) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfEqualWithShortAndLong() {
            String methodSignature = "IfEqual()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, (short) 1, 1, (long) 1);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfFalseEqualZero() {
            String methodSignature = "IfEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, false);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }

        @Test
        public void testIfTrueNotEqualZero() {
            String methodSignature = "IfNotEqualZero()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, true);
            VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
        }
    }

    private static final String CLASS_NAME = "Lif_test;";

    private static final int IF = 0;

    private static final int NOP = 2;

    private static final int RETURN = 3;

}
