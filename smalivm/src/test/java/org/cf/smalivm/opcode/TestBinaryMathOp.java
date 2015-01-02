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

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        // Mocks
        BuilderInstruction mockBi;
        OpFactory opFactory;

        BinaryMathOp underTest;

        @Before
        public void setUp() {
            VirtualMachine mockVm = mock(VirtualMachine.class);
            mockBi = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(TwoRegisterInstruction.class, Instruction23x.class));

            opFactory = new OpFactory(mockVm);

        }

        @Test
        public void canDoLongDivision() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(1120403456L).thenReturn(1149239296L);
            // Causes getFactoryType to return BINARY_MATH and perform LONG math
            when(mockBi.getOpcode()).thenReturn(Opcode.DIV_LONG);

            underTest = (BinaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            // This will be zed since we lose precision if we're doing math/casting properly
            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(0L));
        }

        @Test
        public void canDoFloatDivision() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(1120403456.43F).thenReturn(1149239296.32F);
            // Causes getFactoryType to return BINARY_MATH and perform FLOAT math
            when(mockBi.getOpcode()).thenReturn(Opcode.DIV_FLOAT);

            underTest = (BinaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(0.97490877F));
        }

        @Test
        public void canDoIntDivision() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(Integer.parseInt("10")).thenReturn(
                            Integer.parseInt("4"));
            // Causes getFactoryType to return BINARY_MATH and perform INT math
            when(mockBi.getOpcode()).thenReturn(Opcode.DIV_INT);

            underTest = (BinaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            // If we're casting properly we drop everything and only retain the two
            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(Integer.parseInt("2")));
        }

        @Test
        public void canDoDoubleDivision() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(1586.2D).thenReturn(2536.9D);
            // Causes getFactoryType to return BINARY_MATH and perform DOUBLE math
            when(mockBi.getOpcode()).thenReturn(Opcode.DIV_DOUBLE);

            underTest = (BinaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            // If we're casting properly we drop everything and only retain the two
            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(0.6252512909456424D));
        }
    }

    public static class TestDouble {
        @Test
        public void testAddDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D);

            VMTester.testMethodState(CLASS_NAME, "AddDouble()V", initial, expected);
        }

        @Test
        public void testAddDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D + 20.5D);

            VMTester.testMethodState(CLASS_NAME, "AddDouble2Addr()V", initial, expected);
        }

        @Test
        public void testDivDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D);

            VMTester.testMethodState(CLASS_NAME, "DivDouble()V", initial, expected);
        }

        @Test
        public void testDivDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D / 0.5D);

            VMTester.testMethodState(CLASS_NAME, "DivDouble2Addr()V", initial, expected);
        }

        @Test
        public void testDivDoubleWithCatchWithDiv0Exception() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5D, 1, 0D);
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivDoubleWithCatch()V", initial, expected);
        }

        @Test
        public void testMulDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D);

            VMTester.testMethodState(CLASS_NAME, "MulDouble()V", initial, expected);
        }

        @Test
        public void testMulDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D * 20.5D);

            VMTester.testMethodState(CLASS_NAME, "MulDouble2Addr()V", initial, expected);
        }

        @Test
        public void testRemDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D);

            VMTester.testMethodState(CLASS_NAME, "RemDouble()V", initial, expected);
        }

        @Test
        public void testRemDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9D % 0.5D);

            VMTester.testMethodState(CLASS_NAME, "RemDouble2Addr()V", initial, expected);
        }

        @Test
        public void testSubDouble() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D);

            VMTester.testMethodState(CLASS_NAME, "SubDouble()V", initial, expected);
        }

        @Test
        public void testSubDouble2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5D - 20.5D);

            VMTester.testMethodState(CLASS_NAME, "SubDouble2Addr()V", initial, expected);
        }
    }

    public static class TestFloat {
        @Test
        public void testAddFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F);

            VMTester.testMethodState(CLASS_NAME, "AddFloat()V", initial, expected);
        }

        @Test
        public void testAddFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F + 20.5F);

            VMTester.testMethodState(CLASS_NAME, "AddFloat2Addr()V", initial, expected);
        }

        @Test
        public void testDivFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F);

            VMTester.testMethodState(CLASS_NAME, "DivFloat()V", initial, expected);
        }

        @Test
        public void testDivFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F / 0.5F);

            VMTester.testMethodState(CLASS_NAME, "DivFloat2Addr()V", initial, expected);
        }

        @Test
        public void testDivFloatWithCatchWithDiv0Exception() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5F, 1, 0F);
            // Floats and doubles do not throw exceptions for div0
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivFloatWithCatch()V", initial, expected);
        }

        @Test
        public void testMulFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F);

            VMTester.testMethodState(CLASS_NAME, "MulFloat()V", initial, expected);
        }

        @Test
        public void testMulFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F * 20.5F);

            VMTester.testMethodState(CLASS_NAME, "MulFloat2Addr()V", initial, expected);
        }

        @Test
        public void testRemFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F);

            VMTester.testMethodState(CLASS_NAME, "RemFloat()V", initial, expected);
        }

        @Test
        public void testRemFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 22.9F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 22.9F % 0.5F);

            VMTester.testMethodState(CLASS_NAME, "RemFloat2Addr()V", initial, expected);
        }

        @Test
        public void testSubFloat() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F);

            VMTester.testMethodState(CLASS_NAME, "SubFloat()V", initial, expected);
        }

        @Test
        public void testSubFloat2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0.5F - 20.5F);

            VMTester.testMethodState(CLASS_NAME, "SubFloat2Addr()V", initial, expected);
        }
    }

    public static class TestInteger {
        @Test
        public void testAddByteAndChar() {
            Byte b = 0xf;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 'a', 1, b);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 'a' + b);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 + 7);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 + 7);

            VMTester.testMethodState(CLASS_NAME, "AddInt2Addr()V", initial, expected);
        }

        @Test
        public void testAddIntAndByte() {
            Byte b = 0xf;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, b);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndChar() {
            // Compiler will actually produce something like this.
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "$".charAt(0), 1, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, "$".charAt(0) + 11);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndShort() {
            Short value = 5;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value, 1, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, value + 11);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndBoolean() {
            Boolean value = true;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value, 1, 11);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1 + 11);

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("C"), 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownCharAndByte() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("C"), 1, new UnknownValue(
                            "B"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntAndUnknownInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("I"));

            VMTester.testMethodState(CLASS_NAME, "AddInt()V", initial, expected);
        }

        @Test
        public void testAddIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -0xf);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (-0xf + 0xff));
            VMTester.testMethodState(CLASS_NAME, "AddIntLit16()V", initial, expected);
        }

        @Test
        public void testAddIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -0xf);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (-0xf + 0xf));
            VMTester.testMethodState(CLASS_NAME, "AddIntLit8()V", initial, expected);
        }

        @Test
        public void testAndInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 7);

            VMTester.testMethodState(CLASS_NAME, "AndInt()V", initial, expected);
        }

        @Test
        public void testAndInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 7);

            VMTester.testMethodState(CLASS_NAME, "AndInt2Addr()V", initial, expected);
        }

        @Test
        public void testAndIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 0xff);

            VMTester.testMethodState(CLASS_NAME, "AndIntLit16()V", initial, expected);
        }

        @Test
        public void testAndIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 2 & 0xf);

            VMTester.testMethodState(CLASS_NAME, "AndIntLit8()V", initial, expected);
        }

        @Test
        public void testDivInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12, 1, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12 / 3);

            VMTester.testMethodState(CLASS_NAME, "DivInt()V", initial, expected);
        }

        @Test
        public void testDivInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 12, 1, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 12 / 3);

            VMTester.testMethodState(CLASS_NAME, "DivInt2Addr()V", initial, expected);
        }

        @Test
        public void testDivIntWithCatchWithDiv0Exception() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 0);
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivIntWithCatch()V", initial, expected);
        }

        @Test
        public void testDivIntWithCatchWithNoException() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 5);
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivIntWithCatch()V", initial, expected);
        }

        @Test
        public void testDivIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "DivIntLit16()V", initial, expected);
        }

        @Test
        public void testDivIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "DivIntLit8()V", initial, expected);
        }

        @Test
        public void testMulInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 7);

            VMTester.testMethodState(CLASS_NAME, "MulInt()V", initial, expected);
        }

        @Test
        public void testMulInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 10);

            VMTester.testMethodState(CLASS_NAME, "MulInt2Addr()V", initial, expected);
        }

        @Test
        public void testMulIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 0xff);

            VMTester.testMethodState(CLASS_NAME, "MulIntLit16()V", initial, expected);
        }

        @Test
        public void testMulIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 * 0xf);

            VMTester.testMethodState(CLASS_NAME, "MulIntLit8()V", initial, expected);
        }

        @Test
        public void testOrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 2);

            VMTester.testMethodState(CLASS_NAME, "OrInt()V", initial, expected);
        }

        @Test
        public void testOrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 2);

            VMTester.testMethodState(CLASS_NAME, "OrInt2Addr()V", initial, expected);
        }

        @Test
        public void testOrIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 0xff);

            VMTester.testMethodState(CLASS_NAME, "OrIntLit16()V", initial, expected);
        }

        @Test
        public void testOrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 | 0xf);

            VMTester.testMethodState(CLASS_NAME, "OrIntLit8()V", initial, expected);
        }

        @Test
        public void testRemInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 % 5);

            VMTester.testMethodState(CLASS_NAME, "RemInt()V", initial, expected);
        }

        @Test
        public void testRemInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 % 5);

            VMTester.testMethodState(CLASS_NAME, "RemInt2Addr()V", initial, expected);
        }

        @Test
        public void testRemIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100 % 0xff);

            VMTester.testMethodState(CLASS_NAME, "RemIntLit16()V", initial, expected);
        }

        @Test
        public void testRemIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10 % 0xf);

            VMTester.testMethodState(CLASS_NAME, "RemIntLit8()V", initial, expected);
        }

        @Test
        public void testRSubInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0xff - 7);

            VMTester.testMethodState(CLASS_NAME, "RSubInt()V", initial, expected);
        }

        @Test
        public void testRSubIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0xf - 7);

            VMTester.testMethodState(CLASS_NAME, "RSubIntLit8()V", initial, expected);
        }

        @Test
        public void testShlInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 << 2);

            VMTester.testMethodState(CLASS_NAME, "ShlInt()V", initial, expected);
        }

        @Test
        public void testShlInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 5 << 2);

            VMTester.testMethodState(CLASS_NAME, "ShlInt2Addr()V", initial, expected);
        }

        @Test
        public void testShlIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 3 << 0x2);

            VMTester.testMethodState(CLASS_NAME, "ShlIntLit8()V", initial, expected);
        }

        @Test
        public void testShrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testMethodState(CLASS_NAME, "ShrInt()V", initial, expected);
        }

        @Test
        public void testShrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testMethodState(CLASS_NAME, "ShrInt2Addr()V", initial, expected);
        }

        @Test
        public void testShrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 >> 2);

            VMTester.testMethodState(CLASS_NAME, "ShrIntLit8()V", initial, expected);
        }

        @Test
        public void testSubInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 - 7);

            VMTester.testMethodState(CLASS_NAME, "SubInt()V", initial, expected);
        }

        @Test
        public void testSubInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -3, 1, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3 - 7);

            VMTester.testMethodState(CLASS_NAME, "SubInt2Addr()V", initial, expected);
        }

        @Test
        public void testUshrInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testMethodState(CLASS_NAME, "UshrInt()V", initial, expected);
        }

        @Test
        public void testUshrInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testMethodState(CLASS_NAME, "UshrInt2Addr()V", initial, expected);
        }

        @Test
        public void testUshrIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, -14);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -14 >>> 2);

            VMTester.testMethodState(CLASS_NAME, "UshrIntLit8()V", initial, expected);
        }

        @Test
        public void testXorInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 2);

            VMTester.testMethodState(CLASS_NAME, "XorInt()V", initial, expected);
        }

        @Test
        public void testXorInt2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7, 1, 2);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 2);

            VMTester.testMethodState(CLASS_NAME, "XorInt2Addr()V", initial, expected);
        }

        @Test
        public void testXorIntLit16() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 0x10);

            VMTester.testMethodState(CLASS_NAME, "XorIntLit16()V", initial, expected);
        }

        @Test
        public void testXorIntLit8() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 7);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 7 ^ 3);

            VMTester.testMethodState(CLASS_NAME, "XorIntLit8()V", initial, expected);
        }

    }

    public static class TestLong {

        @Test
        public void testAddLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L);

            VMTester.testMethodState(CLASS_NAME, "AddLong()V", initial, expected);
        }

        @Test
        public void testAddLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L + 0x200000000L);

            VMTester.testMethodState(CLASS_NAME, "AddLong2Addr()V", initial, expected);
        }

        @Test
        public void testAndLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "AndLong()V", initial, expected);
        }

        @Test
        public void testAndLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL & 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "AndLong2Addr()V", initial, expected);
        }

        @Test
        public void testDivLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L);

            VMTester.testMethodState(CLASS_NAME, "DivLong()V", initial, expected);
        }

        @Test
        public void testDivLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L / 3L);

            VMTester.testMethodState(CLASS_NAME, "DivLong2Addr()V", initial, expected);
        }

        @Test
        public void testDivLongWithCatchWithDiv0Exception() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5L, 1, 0L);
            int[] expected = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "DivLongWithCatch()V", initial, expected);
        }

        @Test
        public void testDivLongWithCatchWithNoException() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5L, 1, 5);
            int[] expected = new int[] { 0, 2 };

            VMTester.testVisitation(CLASS_NAME, "DivLongWithCatch()V", initial, expected);
        }

        @Test
        public void testMulLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L);

            VMTester.testMethodState(CLASS_NAME, "MulLong()V", initial, expected);
        }

        @Test
        public void testMulLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000000L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000000L * 3L);

            VMTester.testMethodState(CLASS_NAME, "MulLong2Addr()V", initial, expected);
        }

        @Test
        public void testOrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "OrLong()V", initial, expected);
        }

        @Test
        public void testOrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL | 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "OrLong2Addr()V", initial, expected);
        }

        @Test
        public void testRemLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000123L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L);

            VMTester.testMethodState(CLASS_NAME, "RemLong()V", initial, expected);
        }

        @Test
        public void testRemLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x300000123L, 2, 3L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x300000123L % 3L);

            VMTester.testMethodState(CLASS_NAME, "RemLong2Addr()V", initial, expected);
        }

        @Test
        public void testShlLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L << 5L);

            VMTester.testMethodState(CLASS_NAME, "ShlLong()V", initial, expected);
        }

        @Test
        public void testShlLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L << 5L);

            VMTester.testMethodState(CLASS_NAME, "ShlLong2Addr()V", initial, expected);
        }

        @Test
        public void testShrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L);

            VMTester.testMethodState(CLASS_NAME, "ShrLong()V", initial, expected);
        }

        @Test
        public void testShrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >> 5L);

            VMTester.testMethodState(CLASS_NAME, "ShrLong2Addr()V", initial, expected);
        }

        @Test
        public void testSubLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L);

            VMTester.testMethodState(CLASS_NAME, "SubLong()V", initial, expected);
        }

        @Test
        public void testSubLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000000L, 2, 0x200000000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x100000000L - 0x200000000L);

            VMTester.testMethodState(CLASS_NAME, "SubLong2Addr()V", initial, expected);
        }

        @Test
        public void testUshrLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L);

            VMTester.testMethodState(CLASS_NAME, "UshrLong()V", initial, expected);
        }

        @Test
        public void testUshrLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10000L, 2, 5L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x10000L >>> 5L);

            VMTester.testMethodState(CLASS_NAME, "UshrLong2Addr()V", initial, expected);
        }

        @Test
        public void testXorLong() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "XorLong()V", initial, expected);
        }

        @Test
        public void testXorLong2Addr() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x12345abcdL, 2, 0x1234567890L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x12345abcdL ^ 0x1234567890L);

            VMTester.testMethodState(CLASS_NAME, "XorLong2Addr()V", initial, expected);
        }
    }

    private static final String CLASS_NAME = "Lbinary_math_test;";

}
