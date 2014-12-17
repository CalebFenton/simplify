package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestUnaryMathOp {

    public static class TestStartDouble {

        @Test
        public void testDoubleToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 220D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 220F);

            VMTester.testMethodState(CLASS_NAME, "DoubleToFloat()V", initial, expected);
        }

        @Test
        public void testDoubleToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 200D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 200);

            VMTester.testMethodState(CLASS_NAME, "DoubleToInt()V", initial, expected);
        }

        @Test
        public void testDoubleToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 210D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 210L);

            VMTester.testMethodState(CLASS_NAME, "DoubleToLong()V", initial, expected);
        }

        @Test
        public void testNegDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 15.1D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -15.1D);

            VMTester.testMethodState(CLASS_NAME, "NegDouble()V", initial, expected);
        }

    }

    public static class TestStartFloat {

        @Test
        public void testFloatToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 120F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 120D);

            VMTester.testMethodState(CLASS_NAME, "FloatToDouble()V", initial, expected);
        }

        @Test
        public void testFloatToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 100);

            VMTester.testMethodState(CLASS_NAME, "FloatToInt()V", initial, expected);
        }

        @Test
        public void testFloatToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 110F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 110L);

            VMTester.testMethodState(CLASS_NAME, "FloatToLong()V", initial, expected);
        }

        @Test
        public void testNegFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 10.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -10.5F);

            VMTester.testMethodState(CLASS_NAME, "NegFloat()V", initial, expected);
        }

    }

    public static class TestStartInt {

        @Test
        public void testIntToByte() {
            Integer val = 128;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, val);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, val.byteValue());

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }


	/*
	 * int-to-* can actually be passed a 'Short's, so we need to specifically handle
	 * this, specifically this was identified in an obfuscation technique (where I think
	 * it is used by mistake as it will result in a loss of accuracy if the short goes
	 * over the bounds of a byte):
	 *
	 *    invoke-static {v0}, Ljava/lang/Short;->parseShort(Ljava/lang/String;)S
	 *    move-result v0
	 *    int-to-byte v0, v0
	 */
        @Test
	public void testIntToByteFromShort() {
	    Short val = 1000;
	    TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, val);
	    TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, val.byteValue());

	    VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }


        @Test
        public void testIntToChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x62);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (char) 0x62);

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testIntToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 13);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 13D);

            VMTester.testMethodState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void testIntToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12F);

            VMTester.testMethodState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void testIntToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 11L);

            VMTester.testMethodState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void testIntToShort() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (short) 0x10);

            VMTester.testMethodState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

        @Test
        public void testNegInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x42);

            VMTester.testMethodState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void testNegUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void testNotInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~0x42);

            VMTester.testMethodState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void testNotUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void testUnknownIntToByte() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("B"));

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testUnknownIntToChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("C"));

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testUnknownIntToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testMethodState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void testUnknownIntToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testMethodState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void testUnknownIntToLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

            VMTester.testMethodState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void testUnknownIntToShort() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("S"));

            VMTester.testMethodState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

    }

    public static class TestStartLong {

        @Test
        public void testLongToDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 23L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 23D);

            VMTester.testMethodState(CLASS_NAME, "LongToDouble()V", initial, expected);
        }

        @Test
        public void testLongToFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22F);

            VMTester.testMethodState(CLASS_NAME, "LongToFloat()V", initial, expected);
        }

        @Test
        public void testLongToInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 21L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 21);

            VMTester.testMethodState(CLASS_NAME, "LongToInt()V", initial, expected);
        }

        @Test
        public void testNegLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0x100L);

            VMTester.testMethodState(CLASS_NAME, "NegLong()V", initial, expected);
        }

        @Test
        public void testNotLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~100L);

            VMTester.testMethodState(CLASS_NAME, "NotLong()V", initial, expected);
        }

    }

    private static final String CLASS_NAME = "Lunary_math_test;";

}
