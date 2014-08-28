package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestBinaryMathOp {

    public static class TestDouble {
        @Test
        public void TestAddDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D);

            VMTester.testState(CLASS_NAME, "AddDouble()V", initial, expected);
        }

        @Test
        public void TestAddDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D);

            VMTester.testState(CLASS_NAME, "AddDouble2Addr()V", initial, expected);
        }

        @Test
        public void TestDivDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D);

            VMTester.testState(CLASS_NAME, "DivDouble()V", initial, expected);
        }

        @Test
        public void TestDivDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D);

            VMTester.testState(CLASS_NAME, "DivDouble2Addr()V", initial, expected);
        }

        @Test
        public void TestMulDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D);

            VMTester.testState(CLASS_NAME, "MulDouble()V", initial, expected);
        }

        @Test
        public void TestMulDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D);

            VMTester.testState(CLASS_NAME, "MulDouble2Addr()V", initial, expected);
        }

        @Test
        public void TestRemDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D);

            VMTester.testState(CLASS_NAME, "RemDouble()V", initial, expected);
        }

        @Test
        public void TestRemDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D);

            VMTester.testState(CLASS_NAME, "RemDouble2Addr()V", initial, expected);
        }

        @Test
        public void TestSubDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D);

            VMTester.testState(CLASS_NAME, "SubDouble()V", initial, expected);
        }

        @Test
        public void TestSubDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D);

            VMTester.testState(CLASS_NAME, "SubDouble2Addr()V", initial, expected);
        }
    }

    public static class TestFloat {
        @Test
        public void TestAddFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F);

            VMTester.testState(CLASS_NAME, "AddFloat()V", initial, expected);
        }

        @Test
        public void TestAddFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F);

            VMTester.testState(CLASS_NAME, "AddFloat2Addr()V", initial, expected);
        }

        @Test
        public void TestDivFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F);

            VMTester.testState(CLASS_NAME, "DivFloat()V", initial, expected);
        }

        @Test
        public void TestDivFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F);

            VMTester.testState(CLASS_NAME, "DivFloat2Addr()V", initial, expected);
        }

        @Test
        public void TestMulFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F);

            VMTester.testState(CLASS_NAME, "MulFloat()V", initial, expected);
        }

        @Test
        public void TestMulFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F);

            VMTester.testState(CLASS_NAME, "MulFloat2Addr()V", initial, expected);
        }

        @Test
        public void TestRemFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F);

            VMTester.testState(CLASS_NAME, "RemFloat()V", initial, expected);
        }

        @Test
        public void TestRemFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F);

            VMTester.testState(CLASS_NAME, "RemFloat2Addr()V", initial, expected);
        }

        @Test
        public void TestSubFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F);

            VMTester.testState(CLASS_NAME, "SubFloat()V", initial, expected);
        }

        @Test
        public void TestSubFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F);

            VMTester.testState(CLASS_NAME, "SubFloat2Addr()V", initial, expected);
        }
    }

    public static class TestInteger {
        @Test
        public void TestAddInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 + 7);

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 + 7);

            VMTester.testState(CLASS_NAME, "AddInt2Addr()V", initial, expected);
        }

        @Test
        public void TestAddIntAndByte() {
            Byte b = 0xf;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, b);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12);

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddByteAndChar() {
            Byte b = 0xf;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 'a', 1, b);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 'a' + b);

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddIntAndChar() {
            // Compiler will actually produce something like this.
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "$".charAt(0), 1, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, "$".charAt(0) + 11);

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddIntAndUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddIntAndUnknownChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("C"), 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddIntAndUnknownCharAndByte() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("C"), 1, new UnknownValue(
                            "B"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void TestAddIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -0xf);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (-0xf + 0xff));
            VMTester.testState(CLASS_NAME, "AddIntLit16()V", initial, expected);
        }

        @Test
        public void TestAddIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -0xf);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (-0xf + 0xf));
            VMTester.testState(CLASS_NAME, "AddIntLit8()V", initial, expected);
        }

        @Test
        public void TestAndInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 7);

            VMTester.testState(CLASS_NAME, "AndInt()V", initial, expected);
        }

        @Test
        public void TestAndInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 7);

            VMTester.testState(CLASS_NAME, "AndInt2Addr()V", initial, expected);
        }

        @Test
        public void TestAndIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 0xf);

            VMTester.testState(CLASS_NAME, "AndIntLit8()V", initial, expected);
        }

        @Test
        public void TestAndIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 0xff);

            VMTester.testState(CLASS_NAME, "AndIntLit16()V", initial, expected);
        }

        @Test
        public void TestDivInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12, 1, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12 / 3);

            VMTester.testState(CLASS_NAME, "DivInt()V", initial, expected);
        }

        @Test
        public void TestDivInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12, 1, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12 / 3);

            VMTester.testState(CLASS_NAME, "DivInt2Addr()V", initial, expected);
        }

        @Test
        public void TestDivIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testState(CLASS_NAME, "DivIntLit16()V", initial, expected);
        }

        @Test
        public void TestDivIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testState(CLASS_NAME, "DivIntLit8()V", initial, expected);
        }

        @Test
        public void TestMulInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 7);

            VMTester.testState(CLASS_NAME, "MulInt()V", initial, expected);
        }

        @Test
        public void TestMulInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 10);

            VMTester.testState(CLASS_NAME, "MulInt2Addr()V", initial, expected);
        }

        @Test
        public void TestMulIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 0xff);

            VMTester.testState(CLASS_NAME, "MulIntLit16()V", initial, expected);
        }

        @Test
        public void TestMulIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 0xf);

            VMTester.testState(CLASS_NAME, "MulIntLit8()V", initial, expected);
        }

        @Test
        public void TestOrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 2);

            VMTester.testState(CLASS_NAME, "OrInt()V", initial, expected);
        }

        @Test
        public void TestOrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 2);

            VMTester.testState(CLASS_NAME, "OrInt2Addr()V", initial, expected);
        }

        @Test
        public void TestOrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 0xf);

            VMTester.testState(CLASS_NAME, "OrIntLit8()V", initial, expected);
        }

        @Test
        public void TestOrIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 0xff);

            VMTester.testState(CLASS_NAME, "OrIntLit16()V", initial, expected);
        }

        @Test
        public void TestRemInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 % 5);

            VMTester.testState(CLASS_NAME, "RemInt()V", initial, expected);
        }

        @Test
        public void TestRemInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 % 5);

            VMTester.testState(CLASS_NAME, "RemInt2Addr()V", initial, expected);
        }

        @Test
        public void TestRemIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10 % 0xf);

            VMTester.testState(CLASS_NAME, "RemIntLit8()V", initial, expected);
        }

        @Test
        public void TestRemIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100 % 0xff);

            VMTester.testState(CLASS_NAME, "RemIntLit16()V", initial, expected);
        }

        @Test
        public void TestShlInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 << 2);

            VMTester.testState(CLASS_NAME, "ShlInt()V", initial, expected);
        }

        @Test
        public void TestShlInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 << 2);

            VMTester.testState(CLASS_NAME, "ShlInt2Addr()V", initial, expected);
        }

        @Test
        public void TestShlIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3 << 0x2);

            VMTester.testState(CLASS_NAME, "ShlIntLit8()V", initial, expected);
        }

        @Test
        public void TestShrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testState(CLASS_NAME, "ShrInt()V", initial, expected);
        }

        @Test
        public void TestShrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testState(CLASS_NAME, "ShrInt2Addr()V", initial, expected);
        }

        @Test
        public void TestShrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testState(CLASS_NAME, "ShrIntLit8()V", initial, expected);
        }

        @Test
        public void TestSubInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 - 7);

            VMTester.testState(CLASS_NAME, "SubInt()V", initial, expected);
        }

        @Test
        public void TestSubInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 - 7);

            VMTester.testState(CLASS_NAME, "SubInt2Addr()V", initial, expected);
        }

        @Test
        public void TestRSubInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0xff - 7);

            VMTester.testState(CLASS_NAME, "RSubInt()V", initial, expected);
        }

        @Test
        public void TestRSubIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0xf - 7);

            VMTester.testState(CLASS_NAME, "RSubIntLit8()V", initial, expected);
        }

        @Test
        public void TestUshrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testState(CLASS_NAME, "UshrInt()V", initial, expected);
        }

        @Test
        public void TestUshrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testState(CLASS_NAME, "UshrInt2Addr()V", initial, expected);
        }

        @Test
        public void TestUshrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testState(CLASS_NAME, "UshrIntLit8()V", initial, expected);
        }

        @Test
        public void TestXorInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 2);

            VMTester.testState(CLASS_NAME, "XorInt()V", initial, expected);
        }

        @Test
        public void TestXorInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 2);

            VMTester.testState(CLASS_NAME, "XorInt2Addr()V", initial, expected);
        }

        @Test
        public void TestXorIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 3);

            VMTester.testState(CLASS_NAME, "XorIntLit8()V", initial, expected);
        }

        @Test
        public void TestXorIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 0x10);

            VMTester.testState(CLASS_NAME, "XorIntLit16()V", initial, expected);
        }

    }

    public static class TestLong {

        @Test
        public void TestAddLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L);

            VMTester.testState(CLASS_NAME, "AddLong()V", initial, expected);
        }

        @Test
        public void TestAddLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L);

            VMTester.testState(CLASS_NAME, "AddLong2Addr()V", initial, expected);
        }

        @Test
        public void TestAndLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L);

            VMTester.testState(CLASS_NAME, "AndLong()V", initial, expected);
        }

        @Test
        public void TestAndLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L);

            VMTester.testState(CLASS_NAME, "AndLong2Addr()V", initial, expected);
        }

        @Test
        public void TestDivLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L);

            VMTester.testState(CLASS_NAME, "DivLong()V", initial, expected);
        }

        @Test
        public void TestDivLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L);

            VMTester.testState(CLASS_NAME, "DivLong2Addr()V", initial, expected);
        }

        @Test
        public void TestMulLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L);

            VMTester.testState(CLASS_NAME, "MulLong()V", initial, expected);
        }

        @Test
        public void TestMulLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L);

            VMTester.testState(CLASS_NAME, "MulLong2Addr()V", initial, expected);
        }

        @Test
        public void TestOrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L);

            VMTester.testState(CLASS_NAME, "OrLong()V", initial, expected);
        }

        @Test
        public void TestOrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L);

            VMTester.testState(CLASS_NAME, "OrLong2Addr()V", initial, expected);
        }

        @Test
        public void TestRemLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000123L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L);

            VMTester.testState(CLASS_NAME, "RemLong()V", initial, expected);
        }

        @Test
        public void TestRemLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000123L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L);

            VMTester.testState(CLASS_NAME, "RemLong2Addr()V", initial, expected);
        }

        @Test
        public void TestShlLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L << 5L);

            VMTester.testState(CLASS_NAME, "ShlLong()V", initial, expected);
        }

        @Test
        public void TestShlLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L << 5L);

            VMTester.testState(CLASS_NAME, "ShlLong2Addr()V", initial, expected);
        }

        @Test
        public void TestShrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L);

            VMTester.testState(CLASS_NAME, "ShrLong()V", initial, expected);
        }

        @Test
        public void TestShrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L);

            VMTester.testState(CLASS_NAME, "ShrLong2Addr()V", initial, expected);
        }

        @Test
        public void TestSubLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L);

            VMTester.testState(CLASS_NAME, "SubLong()V", initial, expected);
        }

        @Test
        public void TestSubLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L);

            VMTester.testState(CLASS_NAME, "SubLong2Addr()V", initial, expected);
        }

        @Test
        public void TestUshrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L);

            VMTester.testState(CLASS_NAME, "UshrLong()V", initial, expected);
        }

        @Test
        public void TestUshrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L);

            VMTester.testState(CLASS_NAME, "UshrLong2Addr()V", initial, expected);
        }

        @Test
        public void TestXorLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L);

            VMTester.testState(CLASS_NAME, "XorLong()V", initial, expected);
        }

        @Test
        public void TestXorLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L);

            VMTester.testState(CLASS_NAME, "XorLong2Addr()V", initial, expected);
        }
    }

    private static final String CLASS_NAME = "Lbinary_math_test;";

}
