package simplify.vm.ops;

import gnu.trove.map.TIntObjectMap;

import java.util.logging.Logger;

import org.junit.Test;

import simplifier.Main;
import simplifier.vm.type.UnknownValue;
import simplify.vm.VMTester;

public class TestUnaryMathOp {

    private static final String CLASS_NAME = "Lunary_math_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestDoubleToFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 220D);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 220F);

        VMTester.testState(CLASS_NAME, "TestDoubleToFloat()V", initial, expected);
    }

    @Test
    public void TestDoubleToInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 200D);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 200);

        VMTester.testState(CLASS_NAME, "TestDoubleToInt()V", initial, expected);
    }

    @Test
    public void TestDoubleToLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 210D);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 210L);

        VMTester.testState(CLASS_NAME, "TestDoubleToLong()V", initial, expected);
    }

    @Test
    public void TestFloatToDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 120F);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 120D);

        VMTester.testState(CLASS_NAME, "TestFloatToDouble()V", initial, expected);
    }

    @Test
    public void TestFloatToInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100F);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 100);

        VMTester.testState(CLASS_NAME, "TestFloatToInt()V", initial, expected);
    }

    @Test
    public void TestFloatToLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 110F);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 110L);

        VMTester.testState(CLASS_NAME, "TestFloatToLong()V", initial, expected);
    }

    @Test
    public void TestIntToByte() {
        Integer val = 128;
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, val);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, val.byteValue());

        VMTester.testState(CLASS_NAME, "TestIntToByte()V", initial, expected);
    }

    @Test
    public void TestIntToChar() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x62);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (char) 0x62);

        VMTester.testState(CLASS_NAME, "TestIntToChar()V", initial, expected);
    }

    @Test
    public void TestIntToDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 13);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 13D);

        VMTester.testState(CLASS_NAME, "TestIntToDouble()V", initial, expected);
    }

    @Test
    public void TestIntToFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12F);

        VMTester.testState(CLASS_NAME, "TestIntToFloat()V", initial, expected);
    }

    @Test
    public void TestIntToLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 11);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 11L);

        VMTester.testState(CLASS_NAME, "TestIntToLong()V", initial, expected);
    }

    @Test
    public void TestIntToShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (short) 0x10);

        VMTester.testState(CLASS_NAME, "TestIntToShort()V", initial, expected);
    }

    @Test
    public void TestLongToDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 23L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 23D);

        VMTester.testState(CLASS_NAME, "TestLongToDouble()V", initial, expected);
    }

    @Test
    public void TestLongToFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22F);

        VMTester.testState(CLASS_NAME, "TestLongToFloat()V", initial, expected);
    }

    @Test
    public void TestLongToInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 21L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 21);

        VMTester.testState(CLASS_NAME, "TestLongToInt()V", initial, expected);
    }

    @Test
    public void TestNegDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 15.1D);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -15.1D);

        VMTester.testState(CLASS_NAME, "TestNegDouble()V", initial, expected);
    }

    @Test
    public void TestNegFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 10.5F);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -10.5F);

        VMTester.testState(CLASS_NAME, "TestNegFloat()V", initial, expected);
    }

    @Test
    public void TestNegInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x42);

        VMTester.testState(CLASS_NAME, "TestNegInt()V", initial, expected);
    }

    @Test
    public void TestNegLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x100L);

        VMTester.testState(CLASS_NAME, "TestNegLong()V", initial, expected);
    }

    @Test
    public void TestNegUnknownInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testState(CLASS_NAME, "TestNegInt()V", initial, expected);
    }

    @Test
    public void TestNotInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~0x42);

        VMTester.testState(CLASS_NAME, "TestNotInt()V", initial, expected);
    }

    @Test
    public void TestNotLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100L);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~100L);

        VMTester.testState(CLASS_NAME, "TestNotLong()V", initial, expected);
    }

    @Test
    public void TestNotUnknownInt() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

        VMTester.testState(CLASS_NAME, "TestNotInt()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToDouble() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

        VMTester.testState(CLASS_NAME, "TestIntToDouble()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToFloat() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

        VMTester.testState(CLASS_NAME, "TestIntToFloat()V", initial, expected);
    }

    @Test
    public void TestUnknownIntToLong() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

        VMTester.testState(CLASS_NAME, "TestIntToLong()V", initial, expected);
    }

}
