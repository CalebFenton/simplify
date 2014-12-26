package org.cf.smalivm.opcode;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestBinaryMathOp {

    private static final String CLASS_NAME = "Lbinary_math_test;";

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {
        private BuilderInstruction instruction;
        private OpFactory opFactory;
        private MethodState mState;
        private BinaryMathOp opUnderTest;

        @Before
        public void setUp() {
            VirtualMachine vm = mock(VirtualMachine.class);
            instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(TwoRegisterInstruction.class, Instruction23x.class));
            opFactory = new OpFactory(vm);
            mState = mock(MethodState.class);
        }

        @Test
        public void canDoLongDivision() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("J");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.DIV_LONG);

            opUnderTest = (BinaryMathOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            // Division result is zero since long division drops decimal value
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("J"));
        }

        @Test
        public void canDoFloatDivision() {
            float value1 = 1120403456.43F;
            float value2 = 1149239296.32F;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("F");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.DIV_FLOAT);

            opUnderTest = (BinaryMathOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("F"));
        }

        @Test
        public void canDoIntDivision() {
            int value1 = 10;
            int value2 = 4;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("I");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.DIV_INT);

            opUnderTest = (BinaryMathOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            // If we're casting properly we drop everything and only retain the two
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("I"));
        }

        @Test
        public void canDoDoubleDivision() {
            double value1 = 1586.2D;
            double value2 = 2536.9D;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("D");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.DIV_DOUBLE);

            opUnderTest = (BinaryMathOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            // If we're casting properly we drop everything and only retain the two
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("D"));
        }
    }

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
        public void testDivDoubleWithCatchWithDiv0Exception() {
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
        public void testDivFloatWithCatchWithDiv0Exception() {
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
        public void testAddIntAndBoolean() {
            Boolean value = true;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "Z", 1, 11, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1 + 11, "I");

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
        public void testDivIntWithCatchWithDiv0Exception() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 0, "I");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivIntWithCatch()V", initial, expected);
        }

        @Test
        public void testDivIntWithCatchWithNoException() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I", 1, 5, "I");
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivIntWithCatch()V", initial, expected);
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
        public void testDivLongWithCatchWithDiv0Exception() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5L, "J", 1, 0L, "J");
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivLongWithCatch()V", initial, expected);
        }

        @Test
        public void testDivLongWithCatchWithNoException() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5L, "J", 1, 5, "J");
            int[] expected = new int[] { 0, 2 };

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

}
