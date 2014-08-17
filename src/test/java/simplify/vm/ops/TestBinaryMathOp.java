package simplify.vm.ops;

import org.junit.Test;

import simplifier.vm.type.UnknownValue;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestBinaryMathOp {

    private static final String CLASS_NAME = "Lbinary_math_test;";

    @Test
    public void TestAddDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D);

        VMTester.testState(CLASS_NAME, "TestAddDouble()V", initial, expected);
    }

    @Test
    public void TestAddFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, 20.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F);

        VMTester.testState(CLASS_NAME, "TestAddFloat()V", initial, expected);
    }

    @Test
    public void TestAddInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -3 + 7);

        VMTester.testState(CLASS_NAME, "TestAddInt()V", initial, expected);
    }

    @Test
    public void TestAddIntAndUnknown() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -3, 1, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testState(CLASS_NAME, "TestAddInt()V", initial, expected);
    }

    @Test
    public void TestAddIntAndByte() {
        Byte b = 0xf;
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -3, 1, b);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 12);

        VMTester.testState(CLASS_NAME, "TestAddInt()V", initial, expected);
    }

    @Test
    public void TestAddIntAndChar() {
        // Compiler will actually produce something like this.
        SparseArray<Object> initial = VMTester.buildRegisterState(0, "$".charAt(0), 1, 11);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, "$".charAt(0) + 11);

        VMTester.testState(CLASS_NAME, "TestAddInt()V", initial, expected);
    }

    @Test
    public void TestAddLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L);

        VMTester.testState(CLASS_NAME, "TestAddLong()V", initial, expected);
    }

    @Test
    public void TestAndInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 2, 1, 7);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 2 & 7);

        VMTester.testState(CLASS_NAME, "TestAndInt()V", initial, expected);
    }

    @Test
    public void TestAndLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L);

        VMTester.testState(CLASS_NAME, "TestAndLong()V", initial, expected);
    }

    @Test
    public void TestDivDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D);

        VMTester.testState(CLASS_NAME, "TestDivDouble()V", initial, expected);
    }

    @Test
    public void TestDivFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 22.9F, 2, 0.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F);

        VMTester.testState(CLASS_NAME, "TestDivFloat()V", initial, expected);
    }

    @Test
    public void TestDivInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 12, 1, 3);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 12 / 3);

        VMTester.testState(CLASS_NAME, "TestDivInt()V", initial, expected);
    }

    @Test
    public void TestDivLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L);

        VMTester.testState(CLASS_NAME, "TestDivLong()V", initial, expected);
    }

    @Test
    public void TestMulDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D);

        VMTester.testState(CLASS_NAME, "TestMulDouble()V", initial, expected);
    }

    @Test
    public void TestMulFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, 20.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F);

        VMTester.testState(CLASS_NAME, "TestMulFloat()V", initial, expected);
    }

    @Test
    public void TestMulInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -3 * 7);

        VMTester.testState(CLASS_NAME, "TestMulInt()V", initial, expected);
    }

    @Test
    public void TestMulLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L);

        VMTester.testState(CLASS_NAME, "TestMulLong()V", initial, expected);
    }

    @Test
    public void TestOrInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 5 | 2);

        VMTester.testState(CLASS_NAME, "TestOrInt()V", initial, expected);
    }

    @Test
    public void TestOrLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L);

        VMTester.testState(CLASS_NAME, "TestOrLong()V", initial, expected);
    }

    @Test
    public void TestRemDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D);

        VMTester.testState(CLASS_NAME, "TestRemDouble()V", initial, expected);
    }

    @Test
    public void TestRemFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 22.9F, 2, 0.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F);

        VMTester.testState(CLASS_NAME, "TestRemFloat()V", initial, expected);
    }

    @Test
    public void TestRemInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 7, 1, 5);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 7 % 5);

        VMTester.testState(CLASS_NAME, "TestRemInt()V", initial, expected);
    }

    @Test
    public void TestRemLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x300000123L, 2, 3L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L);

        VMTester.testState(CLASS_NAME, "TestRemLong()V", initial, expected);
    }

    @Test
    public void TestShlInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 5 << 2);

        VMTester.testState(CLASS_NAME, "TestShlInt()V", initial, expected);
    }

    @Test
    public void TestShlLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x10000L << 5L);

        VMTester.testState(CLASS_NAME, "TestShlLong()V", initial, expected);
    }

    @Test
    public void TestShrInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

        VMTester.testState(CLASS_NAME, "TestShrInt()V", initial, expected);
    }

    @Test
    public void TestShrLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L);

        VMTester.testState(CLASS_NAME, "TestShrLong()V", initial, expected);
    }

    @Test
    public void TestSubDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D);

        VMTester.testState(CLASS_NAME, "TestSubDouble()V", initial, expected);
    }

    @Test
    public void TestSubFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, 20.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F);

        VMTester.testState(CLASS_NAME, "TestSubFloat()V", initial, expected);
    }

    @Test
    public void TestSubInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -3 - 7);

        VMTester.testState(CLASS_NAME, "TestSubInt()V", initial, expected);
    }

    @Test
    public void TestSubLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L);

        VMTester.testState(CLASS_NAME, "TestSubLong()V", initial, expected);
    }

    @Test
    public void TestUshrInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, -14, 1, 2);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

        VMTester.testState(CLASS_NAME, "TestUshrInt()V", initial, expected);
    }

    @Test
    public void TestUshrLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L);

        VMTester.testState(CLASS_NAME, "TestUshrLong()V", initial, expected);
    }

    @Test
    public void TestXorInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 7 ^ 2);

        VMTester.testState(CLASS_NAME, "TestXorInt()V", initial, expected);
    }

    @Test
    public void TestXorLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L);

        VMTester.testState(CLASS_NAME, "TestXorLong()V", initial, expected);
    }

}
