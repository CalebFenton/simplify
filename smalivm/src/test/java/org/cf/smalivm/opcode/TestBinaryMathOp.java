package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestBinaryMathOp {

    public static class TestDouble {

        @Test
        public void testAddDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "AddDouble()V", initial, expected);
        }

        @Test
        public void testAddDouble2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "AddDouble2Addr()V", initial, expected);
        }

        @Test
        public void testDivDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "DivDouble()V", initial, expected);
        }

        @Test
        public void testDivDouble2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "DivDouble2Addr()V", initial, expected);
        }

        @Test
        public void testDivDoubleWithDivisionByZero() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5D, "D", 1, 0D, "D");
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivDoubleWithCatch()V", initial, expected);
        }

        @Test
        public void testMulDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "MulDouble()V", initial, expected);
        }

        @Test
        public void testMulDouble2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "MulDouble2Addr()V", initial, expected);
        }

        @Test
        public void testRemDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "RemDouble()V", initial, expected);
        }

        @Test
        public void testRemDouble2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "RemDouble2Addr()V", initial, expected);
        }

        @Test
        public void testSubDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "SubDouble()V", initial, expected);
        }

        @Test
        public void testSubDouble2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D, "D");

            VMTester.testMethodState(CLASS_NAME, "SubDouble2Addr()V", initial, expected);
        }
    }

    public static class TestFloat {

        @Test
        public void testAddFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "AddFloat()V", initial, expected);
        }

        @Test
        public void testAddFloat2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "AddFloat2Addr()V", initial, expected);
        }

        @Test
        public void testDivFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "DivFloat()V", initial, expected);
        }

        @Test
        public void testDivFloat2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "DivFloat2Addr()V", initial, expected);
        }

        @Test
        public void testDivFloatWithDivisionByZero() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5F, "F", 1, 0F, "F");
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivFloatWithCatch()V", initial, expected);
        }

        @Test
        public void testMulFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "MulFloat()V", initial, expected);
        }

        @Test
        public void testMulFloat2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "MulFloat2Addr()V", initial, expected);
        }

        @Test
        public void testRemFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "RemFloat()V", initial, expected);
        }

        @Test
        public void testRemFloat2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22.9F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "RemFloat2Addr()V", initial, expected);
        }

        @Test
        public void testSubFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "SubFloat()V", initial, expected);
        }

        @Test
        public void testSubFloat2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "SubFloat2Addr()V", initial, expected);
        }
    }

    public static class TestInteger {

        @Test
        public void testAddByteAndChar() {
            Byte b = 0xf;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 'a', "C", 1, b, "B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 'a' + b, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 + 7, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 + 7, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt2Addr()V", initial, expected);
        }

        @Test
        public void testAddIntAndBoolean() {
            Boolean value = true;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "Z", 1, 11, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1 + 11, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndByte() {
            Byte b = 0xf;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, b, "B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 12, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndChar() {
            // Compiler will actually produce something like this.
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "$".charAt(0), "C", 1, 11, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, "$".charAt(0) + 11, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndShort() {
            Short value = 5;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S", 1, 11, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, value + 11, "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownChar() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "C", 1, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownCharAndByte() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "C", 1,
                            new UnknownValue(), "B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -0xf, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (-0xf + 0xff), "I");
            VMTester.testMethodState(CLASS_NAME, "AddIntLit16()V", initial, expected);
        }

        @Test
        public void testAddIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -0xf, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (-0xf + 0xf), "I");
            VMTester.testMethodState(CLASS_NAME, "AddIntLit8()V", initial, expected);
        }

        @Test
        public void testAndInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 2, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 2 & 7, "I");

            VMTester.testMethodState(CLASS_NAME, "AndInt()V", initial, expected);
        }

        @Test
        public void testAndInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 2, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 2 & 7, "I");

            VMTester.testMethodState(CLASS_NAME, "AndInt2Addr()V", initial, expected);
        }

        @Test
        public void testAndIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 2 & 0xff, "I");

            VMTester.testMethodState(CLASS_NAME, "AndIntLit16()V", initial, expected);
        }

        @Test
        public void testAndIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 2 & 0xf, "I");

            VMTester.testMethodState(CLASS_NAME, "AndIntLit8()V", initial, expected);
        }

        @Test
        public void testDivInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 12, "I", 1, 3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 12 / 3, "I");

            VMTester.testMethodState(CLASS_NAME, "DivInt()V", initial, expected);
        }

        @Test
        public void testDivInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 12, "I", 1, 3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 12 / 3, "I");

            VMTester.testMethodState(CLASS_NAME, "DivInt2Addr()V", initial, expected);
        }

        @Test
        public void testDivIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "DivIntLit16()V", initial, expected);
        }

        @Test
        public void testDivIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "DivIntLit8()V", initial, expected);
        }

        @Test
        public void testDivIntWithCatchWithUnkownValueVisitsExceptionHandler() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I", 1, 5, "I");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivIntWithCatch()V", initial, expected);
        }

        @Test
        public void testMulInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 * 7, "I");

            VMTester.testMethodState(CLASS_NAME, "MulInt()V", initial, expected);
        }

        @Test
        public void testMulInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 10, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 * 10, "I");

            VMTester.testMethodState(CLASS_NAME, "MulInt2Addr()V", initial, expected);
        }

        @Test
        public void testMulIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 * 0xff, "I");

            VMTester.testMethodState(CLASS_NAME, "MulIntLit16()V", initial, expected);
        }

        @Test
        public void testMulIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 * 0xf, "I");

            VMTester.testMethodState(CLASS_NAME, "MulIntLit8()V", initial, expected);
        }

        @Test
        public void testOrInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 | 2, "I");

            VMTester.testMethodState(CLASS_NAME, "OrInt()V", initial, expected);
        }

        @Test
        public void testOrInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 | 2, "I");

            VMTester.testMethodState(CLASS_NAME, "OrInt2Addr()V", initial, expected);
        }

        @Test
        public void testOrIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 | 0xff, "I");

            VMTester.testMethodState(CLASS_NAME, "OrIntLit16()V", initial, expected);
        }

        @Test
        public void testOrIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 | 0xf, "I");

            VMTester.testMethodState(CLASS_NAME, "OrIntLit8()V", initial, expected);
        }

        @Test
        public void testRemInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 % 5, "I");

            VMTester.testMethodState(CLASS_NAME, "RemInt()V", initial, expected);
        }

        @Test
        public void testRemInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 % 5, "I");

            VMTester.testMethodState(CLASS_NAME, "RemInt2Addr()V", initial, expected);
        }

        @Test
        public void testRemIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x100 % 0xff, "I");

            VMTester.testMethodState(CLASS_NAME, "RemIntLit16()V", initial, expected);
        }

        @Test
        public void testRemIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10 % 0xf, "I");

            VMTester.testMethodState(CLASS_NAME, "RemIntLit8()V", initial, expected);
        }

        @Test
        public void testRSubInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0xff - 7, "I");

            VMTester.testMethodState(CLASS_NAME, "RSubInt()V", initial, expected);
        }

        @Test
        public void testRSubIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0xf - 7, "I");

            VMTester.testMethodState(CLASS_NAME, "RSubIntLit8()V", initial, expected);
        }

        @Test
        public void testShlInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 << 2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShlInt()V", initial, expected);
        }

        @Test
        public void testShlInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 5 << 2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShlInt2Addr()V", initial, expected);
        }

        @Test
        public void testShlIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 3 << 0x2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShlIntLit8()V", initial, expected);
        }

        @Test
        public void testShrInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 >> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShrInt()V", initial, expected);
        }

        @Test
        public void testShrInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 >> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShrInt2Addr()V", initial, expected);
        }

        @Test
        public void testShrIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 >> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "ShrIntLit8()V", initial, expected);
        }

        @Test
        public void testSubInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 - 7, "I");

            VMTester.testMethodState(CLASS_NAME, "SubInt()V", initial, expected);
        }

        @Test
        public void testSubInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -3, "I", 1, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3 - 7, "I");

            VMTester.testMethodState(CLASS_NAME, "SubInt2Addr()V", initial, expected);
        }

        @Test
        public void testUshrInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -14, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -14 >>> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "UshrInt()V", initial, expected);
        }

        @Test
        public void testUshrInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -14, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -14 >>> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "UshrInt2Addr()V", initial, expected);
        }

        @Test
        public void testUshrIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, -14, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -14 >>> 2, "I");

            VMTester.testMethodState(CLASS_NAME, "UshrIntLit8()V", initial, expected);
        }

        @Test
        public void testXorInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 ^ 2, "I");

            VMTester.testMethodState(CLASS_NAME, "XorInt()V", initial, expected);
        }

        @Test
        public void testXorInt2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I", 1, 2, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 ^ 2, "I");

            VMTester.testMethodState(CLASS_NAME, "XorInt2Addr()V", initial, expected);
        }

        @Test
        public void testXorIntLit16() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 ^ 0x10, "I");

            VMTester.testMethodState(CLASS_NAME, "XorIntLit16()V", initial, expected);
        }

        @Test
        public void testXorIntLit8() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 7, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 7 ^ 3, "I");

            VMTester.testMethodState(CLASS_NAME, "XorIntLit8()V", initial, expected);
        }

    }

    public static class TestLong {

        @Test
        public void testAddLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "AddLong()V", initial, expected);
        }

        @Test
        public void testAddLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "AddLong2Addr()V", initial, expected);
        }

        @Test
        public void testAndLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "AndLong()V", initial, expected);
        }

        @Test
        public void testAndLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "AndLong2Addr()V", initial, expected);
        }

        @Test
        public void testDivLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000000L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "DivLong()V", initial, expected);
        }

        @Test
        public void testDivLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000000L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "DivLong2Addr()V", initial, expected);
        }

        @Test
        public void testDivLongWithCatchWithUnkownValueVisitsExceptionHandler() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5L, "J", 1, new UnknownValue(), "J");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivLongWithCatch()V", initial, expected);
        }

        @Test
        public void testMulLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000000L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "MulLong()V", initial, expected);
        }

        @Test
        public void testMulLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000000L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "MulLong2Addr()V", initial, expected);
        }

        @Test
        public void testOrLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "OrLong()V", initial, expected);
        }

        @Test
        public void testOrLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "OrLong2Addr()V", initial, expected);
        }

        @Test
        public void testRemLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000123L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "RemLong()V", initial, expected);
        }

        @Test
        public void testRemLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x300000123L, "J", 2, 3L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L, "J");

            VMTester.testMethodState(CLASS_NAME, "RemLong2Addr()V", initial, expected);
        }

        @Test
        public void testShlLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L << 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "ShlLong()V", initial, expected);
        }

        @Test
        public void testShlLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L << 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "ShlLong2Addr()V", initial, expected);
        }

        @Test
        public void testShrLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "ShrLong()V", initial, expected);
        }

        @Test
        public void testShrLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "ShrLong2Addr()V", initial, expected);
        }

        @Test
        public void testSubLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "SubLong()V", initial, expected);
        }

        @Test
        public void testSubLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100000000L, "J", 2, 0x200000000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "SubLong2Addr()V", initial, expected);
        }

        @Test
        public void testUshrLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "UshrLong()V", initial, expected);
        }

        @Test
        public void testUshrLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10000L, "J", 2, 5L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L, "J");

            VMTester.testMethodState(CLASS_NAME, "UshrLong2Addr()V", initial, expected);
        }

        @Test
        public void testXorLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "XorLong()V", initial, expected);
        }

        @Test
        public void testXorLong2Addr() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x12345abcdL, "J", 2, 0x1234567890L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L, "J");

            VMTester.testMethodState(CLASS_NAME, "XorLong2Addr()V", initial, expected);
        }
    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;
        private static final int ARG2_REGISTER = 4;
        private static final int DEST_REGISTER = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;

        private BuilderInstruction instruction;

        private MethodLocation location;

        private MethodState mState;
        private ExecutionNode node;
        private BinaryMathOp op;
        private BinaryMathOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void doubleDivisionWithTwoRegistersEqualsExpected() {
            double value1 = 1586.2D;
            double value2 = 2536.9D;
            double expected = value1 / value2;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "D");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value2, "D");

            instruction = buildInstruction12x(Opcode.DIV_DOUBLE);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("D"));
        }

        @Test
        public void floatDivisionWithTwoRegistersEqualsExpected() {
            float value1 = 1120403456.43F;
            float value2 = 1149239296.32F;
            float expected = value1 / value2;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "F");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value2, "F");

            instruction = buildInstruction12x(Opcode.DIV_FLOAT);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("F"));
        }

        @Test
        public void intDivisionWithLiteralEqualsExpected() {
            int value1 = 10;
            int value2 = 4;
            int expected = value1 / value2;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "I");

            instruction = buildInstruction22s(Opcode.DIV_INT, value2);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("I"));
        }

        @Test
        public void integerDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int value1 = 10;
            int value2 = 0;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "I");

            instruction = buildInstruction22s(Opcode.DIV_INT_LIT16, value2);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArithmeticException.class, "/ by zero");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void integerModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int value1 = 10;
            int value2 = 0;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "I");

            instruction = buildInstruction22s(Opcode.REM_INT_LIT16, value2);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArithmeticException.class, "/ by zero");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void longDivisionByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            long value1 = 1120403456L;
            long value2 = 0L;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "J");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value2, "J");

            instruction = buildInstruction23x(Opcode.DIV_LONG);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArithmeticException.class, "/ by zero");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void longDivisionWithThreeArgumentsAndResultLessThanOneEqualsZero() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            long expected = value1 / value2; // 0 since long division drops decimal value
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "J");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value2, "J");

            instruction = buildInstruction23x(Opcode.DIV_LONG);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), eq(expected), eq("J"));
            assertEquals("div-long r" + DEST_REGISTER + ", r" + ARG1_REGISTER + ", r" + ARG2_REGISTER, op.toString());
        }

        @Test
        public void longModuloByZeroExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            long value1 = 1120403456L;
            long value2 = 0L;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value1, "J");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value2, "J");

            instruction = buildInstruction23x(Opcode.REM_LONG);
            op = (BinaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArithmeticException.class, "/ by zero");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new BinaryMathOpFactory();
        }

        private BuilderInstruction buildInstruction12x(Opcode opcode) {
            BuilderInstruction instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(TwoRegisterInstruction.class, Instruction12x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
            when(((Instruction12x) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);
            when(((Instruction12x) instruction).getRegisterB()).thenReturn(ARG2_REGISTER);

            return instruction;
        }

        private BuilderInstruction buildInstruction22s(Opcode opcode, int value) {
            BuilderInstruction instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(NarrowLiteralInstruction.class, Instruction22s.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
            when(((Instruction22s) instruction).getRegisterA()).thenReturn(DEST_REGISTER);
            when(((Instruction22s) instruction).getRegisterB()).thenReturn(ARG1_REGISTER);
            when(((Instruction22s) instruction).getNarrowLiteral()).thenReturn(value);

            return instruction;
        }

        private BuilderInstruction buildInstruction23x(Opcode opcode) {
            BuilderInstruction instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(TwoRegisterInstruction.class, Instruction23x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(DEST_REGISTER);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(ARG1_REGISTER);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(ARG2_REGISTER);

            return instruction;
        }

    }

    private static final String CLASS_NAME = "Lbinary_math_test;";

}
