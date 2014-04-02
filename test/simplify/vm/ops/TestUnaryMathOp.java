package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestUnaryMathOp {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lunary_math_test;";

    @Test
    public void TestNegInt() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x42);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, -0x42);

        VMTester.test(CLASS_NAME, "TestNegInt()V", initial, expected);
    }

    @Test
    public void TestNegLong() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x100L);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, -0x100L);

        VMTester.test(CLASS_NAME, "TestNegLong()V", initial, expected);
    }

    @Test
    public void TestNotLong() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 100);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 200);

        VMTester.test(CLASS_NAME, "TestNotLong()V", initial, expected);
    }

    @Test
    public void TestNegFloat() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 10.5F);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, -10.5F);

        VMTester.test(CLASS_NAME, "TestNegFloat()V", initial, expected);
    }

    @Test
    public void TestNegDouble() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 15.1D);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, -15.1D);

        VMTester.test(CLASS_NAME, "TestNegDouble()V", initial, expected);
    }

    @Test
    public void TestIntToLong() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 11);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 11L);

        VMTester.test(CLASS_NAME, "TestIntToLong()V", initial, expected);
    }

    @Test
    public void TestIntToFloat() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 12);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 12F);

        VMTester.test(CLASS_NAME, "TestIntToFloat()V", initial, expected);
    }

    @Test
    public void TestIntToDouble() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 13);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 13D);

        VMTester.test(CLASS_NAME, "TestIntToDouble()V", initial, expected);
    }

    @Test
    public void TestLongToInt() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 21L);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 21);

        VMTester.test(CLASS_NAME, "TestLongToInt()V", initial, expected);
    }

    @Test
    public void TestLongToFloat() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 22L);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 22F);

        VMTester.test(CLASS_NAME, "TestLongToFloat()V", initial, expected);
    }

    @Test
    public void TestLongToDouble() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 23L);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 23D);

        VMTester.test(CLASS_NAME, "TestLongToDouble()V", initial, expected);
    }

    @Test
    public void TestFloatToInt() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 100F);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 100);

        VMTester.test(CLASS_NAME, "TestFloatToInt()V", initial, expected);
    }

    @Test
    public void TestFloatToLong() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 110F);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 110L);

        VMTester.test(CLASS_NAME, "TestFloatToLong()V", initial, expected);
    }

    @Test
    public void TestFloatToDouble() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 120F);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 120D);

        VMTester.test(CLASS_NAME, "TestFloatToDouble()V", initial, expected);
    }

    @Test
    public void TestDoubleToInt() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 200D);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 200);

        VMTester.test(CLASS_NAME, "TestDoubleToInt()V", initial, expected);
    }

    @Test
    public void TestDoubleToLong() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 210D);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 210L);

        VMTester.test(CLASS_NAME, "TestDoubleToLong()V", initial, expected);
    }

    @Test
    public void TestDoubleToFloat() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 220D);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 220F);

        VMTester.test(CLASS_NAME, "TestDoubleToFloat()V", initial, expected);
    }

    @Test
    public void TestIntToByte() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x5);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (byte) 0x5);

        VMTester.test(CLASS_NAME, "TestIntToByte()V", initial, expected);
    }

    @Test
    public void TestIntToChar() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x62);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (char) 0x62);

        VMTester.test(CLASS_NAME, "TestIntToChar()V", initial, expected);
    }

    @Test
    public void TestIntToShort() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x10);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (short) 0x10);

        VMTester.test(CLASS_NAME, "TestIntToShort()V", initial, expected);
    }

}
