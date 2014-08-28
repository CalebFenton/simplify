package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestUnaryMathOp {

    private static final String CLASS_NAME = "Lunary_math_test;";

    public static class TestStartDouble {

        @Test
        public void TestDoubleToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 220D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 220F);

            VMTester.testState(CLASS_NAME, "DoubleToFloat()V", initial, expected);
        }

        @Test
        public void TestDoubleToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 200D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 200);

            VMTester.testState(CLASS_NAME, "DoubleToInt()V", initial, expected);
        }

        @Test
        public void TestDoubleToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 210D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 210L);

            VMTester.testState(CLASS_NAME, "DoubleToLong()V", initial, expected);
        }

        @Test
        public void TestNegDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 15.1D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -15.1D);

            VMTester.testState(CLASS_NAME, "NegDouble()V", initial, expected);
        }

    }

    public static class TestStartFloat {

        @Test
        public void TestFloatToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 120F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 120D);

            VMTester.testState(CLASS_NAME, "FloatToDouble()V", initial, expected);
        }

        @Test
        public void TestFloatToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 100);

            VMTester.testState(CLASS_NAME, "FloatToInt()V", initial, expected);
        }

        @Test
        public void TestFloatToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 110F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 110L);

            VMTester.testState(CLASS_NAME, "FloatToLong()V", initial, expected);
        }

        @Test
        public void TestNegFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 10.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -10.5F);

            VMTester.testState(CLASS_NAME, "NegFloat()V", initial, expected);
        }

    }

    public static class TestStartInt {

        @Test
        public void TestIntToByte() {
            Integer val = 128;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, val);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, val.byteValue());

            VMTester.testState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void TestIntToChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x62);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (char) 0x62);

            VMTester.testState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void TestIntToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 13);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 13D);

            VMTester.testState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void TestIntToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12F);

            VMTester.testState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void TestIntToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 11L);

            VMTester.testState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void TestIntToShort() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (short) 0x10);

            VMTester.testState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

        @Test
        public void TestNegInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x42);

            VMTester.testState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void TestNotInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~0x42);

            VMTester.testState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void TestNotUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

            VMTester.testState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToByte() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("B"));

            VMTester.testState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("C"));

            VMTester.testState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void TestUnknownIntToShort() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("S"));

            VMTester.testState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

        @Test
        public void TestNegUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "NegInt()V", initial, expected);
        }

    }

    public static class TestStartLong {

        @Test
        public void TestLongToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 23L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 23D);

            VMTester.testState(CLASS_NAME, "LongToDouble()V", initial, expected);
        }

        @Test
        public void TestLongToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22F);

            VMTester.testState(CLASS_NAME, "LongToFloat()V", initial, expected);
        }

        @Test
        public void TestLongToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 21L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 21);

            VMTester.testState(CLASS_NAME, "LongToInt()V", initial, expected);
        }

        @Test
        public void TestNegLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x100L);

            VMTester.testState(CLASS_NAME, "NegLong()V", initial, expected);
        }

        @Test
        public void TestNotLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~100L);

            VMTester.testState(CLASS_NAME, "NotLong()V", initial, expected);
        }

    }

}
