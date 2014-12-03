package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestCmpOp {

    private static final String CLASS_NAME = "Lcmp_test;";

    public static class TestDouble {
        @Test
        public void TestCmplDoubleWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5D, 2, 4.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, Double.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, new UnknownValue("D"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void TestCmpgDoubleWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void TestCmpgDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void TestCmpgDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5D, 2, 4.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void TestCmpgDoubleWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, Double.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void TestCmpgDoubleWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, new UnknownValue("D"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }
    }

    public static class TestFloat {
        @Test
        public void TestCmplFloatWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void TestCmplFloatWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void TestCmplFloatWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5F, 1, 4.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void TestCmplFloatWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, Float.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void TestCmplFloatWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, new UnknownValue("F"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void TestCmpgFloatWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void TestCmpgFloatWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void TestCmpgFloatWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5F, 1, 4.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void TestCmpgFloatWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, Float.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void TestCmpgFloatWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, new UnknownValue("F"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }
    }

    public static class TestLong {
        @Test
        public void TestCmpLongWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10L, 2, 0x100000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000L, 2, 0x10L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L, 2, 0x100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void TestCmplDoubleWithUnknownArgument() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L, 2, new UnknownValue("J"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }
    }

}
