package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestUnaryMathOp {

    private static final String CLASS_NAME = "Lunary_math_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestDoubleToFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 220D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 220F);

        VMTester.testState(CLASS_NAME, "TestDoubleToFloat()V", initial, expected);
    }

    @Test
    public void TestDoubleToInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 200D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 200);

        VMTester.testState(CLASS_NAME, "TestDoubleToInt()V", initial, expected);
    }

    @Test
    public void TestDoubleToLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 210D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 210L);

        VMTester.testState(CLASS_NAME, "TestDoubleToLong()V", initial, expected);
    }

    @Test
    public void TestFloatToDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 120F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 120D);

        VMTester.testState(CLASS_NAME, "TestFloatToDouble()V", initial, expected);
    }

    @Test
    public void TestFloatToInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 100F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 100);

        VMTester.testState(CLASS_NAME, "TestFloatToInt()V", initial, expected);
    }

    @Test
    public void TestFloatToLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 110F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 110L);

        VMTester.testState(CLASS_NAME, "TestFloatToLong()V", initial, expected);
    }

    @Test
    public void TestIntToByte() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x5);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, (byte) 0x5);

        VMTester.testState(CLASS_NAME, "TestIntToByte()V", initial, expected);
    }

    @Test
    public void TestIntToChar() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x62);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, (char) 0x62);

        VMTester.testState(CLASS_NAME, "TestIntToChar()V", initial, expected);
    }

    @Test
    public void TestIntToDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 13);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 13D);

        VMTester.testState(CLASS_NAME, "TestIntToDouble()V", initial, expected);
    }

    @Test
    public void TestIntToFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 12);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 12F);

        VMTester.testState(CLASS_NAME, "TestIntToFloat()V", initial, expected);
    }

    @Test
    public void TestIntToLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 11);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 11L);

        VMTester.testState(CLASS_NAME, "TestIntToLong()V", initial, expected);
    }

    @Test
    public void TestIntToShort() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x10);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, (short) 0x10);

        VMTester.testState(CLASS_NAME, "TestIntToShort()V", initial, expected);
    }

    @Test
    public void TestLongToDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 23L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 23D);

        VMTester.testState(CLASS_NAME, "TestLongToDouble()V", initial, expected);
    }

    @Test
    public void TestLongToFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 22L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 22F);

        VMTester.testState(CLASS_NAME, "TestLongToFloat()V", initial, expected);
    }

    @Test
    public void TestLongToInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 21L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 21);

        VMTester.testState(CLASS_NAME, "TestLongToInt()V", initial, expected);
    }

    @Test
    public void TestNegDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 15.1D);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -15.1D);

        VMTester.testState(CLASS_NAME, "TestNegDouble()V", initial, expected);
    }

    @Test
    public void TestNegFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 10.5F);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -10.5F);

        VMTester.testState(CLASS_NAME, "TestNegFloat()V", initial, expected);
    }

    @Test
    public void TestNegInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x42);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -0x42);

        VMTester.testState(CLASS_NAME, "TestNegInt()V", initial, expected);
    }

    @Test
    public void TestNegLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x100L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -0x100L);

        VMTester.testState(CLASS_NAME, "TestNegLong()V", initial, expected);
    }

    @Test
    public void TestNegUnknownInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testState(CLASS_NAME, "TestNegInt()V", initial, expected);
    }

    @Test
    public void TestNotInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x42);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, ~0x42);

        VMTester.testState(CLASS_NAME, "TestNotInt()V", initial, expected);
    }

    @Test
    public void TestNotLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 100L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, ~100L);

        VMTester.testState(CLASS_NAME, "TestNotLong()V", initial, expected);
    }

    @Test
    public void TestNotUnknownInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testState(CLASS_NAME, "TestNotInt()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToDouble() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

        VMTester.testState(CLASS_NAME, "TestIntToDouble()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToFloat() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

        VMTester.testState(CLASS_NAME, "TestIntToFloat()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToLong() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

        VMTester.testState(CLASS_NAME, "TestIntToLong()V", initial, expected);
    }

}
