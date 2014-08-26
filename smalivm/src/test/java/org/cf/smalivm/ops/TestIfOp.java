package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.junit.Test;

public class TestIfOp {

    private static final String CLASS_NAME = "Lif_test;";

    private static final int IF = 0;
    private static final int NOP = 2;
    private static final int RETURN = 3;

    @Test
    public void TestIfEqualsTrue() {
        String methodSignature = "IfEquals()V";
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
        VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
    }

    @Test
    public void TestIfEqualsFalse() {
        String methodSignature = "IfEquals()V";
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
        VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
    }

    @Test
    public void TestIfNotEqualsTrue() {
        String methodSignature = "IfNotEquals()V";
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
        VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
    }

    @Test
    public void TestIfNotEqualsFalse() {
        String methodSignature = "IfNotEquals()V";
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

    @Test
    public void TestIfEqIntToByteTrue() {
        String methodSignature = "IfEqIntToByte()V";
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 0);
        // VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, RETURN });
    }

    @Test
    public void TestIfEqByteToIntFalse() {
        String methodSignature = "IfEqByteToInt()V";
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0, 1, 1);
        // VMTester.testVisitation(CLASS_NAME, methodSignature, initial, new int[] { IF, NOP, RETURN });
    }

}
