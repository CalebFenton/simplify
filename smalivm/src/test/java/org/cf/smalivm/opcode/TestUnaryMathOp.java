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
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestUnaryMathOp {

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        // Mocks
        static BuilderInstruction mockBi;
        static OpFactory opFactory;

        UnaryMathOp underTest;

        @BeforeClass
        public static void setUp() {
            VirtualMachine mockVm = mock(VirtualMachine.class);
            mockBi = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction12x.class));

            opFactory = new OpFactory(mockVm);
        }

        @Test
        public void canDoubletoFloat() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345612345D);
            when(mockBi.getOpcode()).thenReturn(Opcode.DOUBLE_TO_FLOAT);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Double(11204.0345612345D).floatValue()));
        }

        @Test
        public void canDoubletoInt() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345612345D);
            when(mockBi.getOpcode()).thenReturn(Opcode.DOUBLE_TO_INT);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Double(11204.0345612345D).intValue()));
        }

        @Test
        public void canDoubletoLong() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345612345D);
            when(mockBi.getOpcode()).thenReturn(Opcode.DOUBLE_TO_LONG);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Double(11204.0345612345D).longValue()));
        }

        @Test
        public void canFloatToDouble() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345F);
            when(mockBi.getOpcode()).thenReturn(Opcode.FLOAT_TO_DOUBLE);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Float(11204.0345F).doubleValue()));
        }

        @Test
        public void canFloatToInt() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345F);
            when(mockBi.getOpcode()).thenReturn(Opcode.FLOAT_TO_INT);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Float(11204.0345F).intValue()));
        }

        @Test
        public void canFloatToLong() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345F);
            when(mockBi.getOpcode()).thenReturn(Opcode.FLOAT_TO_LONG);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Float(11204.0345F).longValue()));
        }

        @Test
        public void canLongToFloat() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(112040345L);
            when(mockBi.getOpcode()).thenReturn(Opcode.LONG_TO_FLOAT);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Long(112040345L).floatValue()));
        }

        @Test
        public void canLongToInt() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(112040345L);
            when(mockBi.getOpcode()).thenReturn(Opcode.LONG_TO_INT);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Long(112040345L).intValue()));
        }

        @Test
        public void canLongToDouble() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(112040345L);
            when(mockBi.getOpcode()).thenReturn(Opcode.LONG_TO_DOUBLE);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Long(112040345L).doubleValue()));
        }

        @Test
        public void canIntToByte() {

            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(10);
            when(mockBi.getOpcode()).thenReturn(Opcode.INT_TO_BYTE);

            underTest = (UnaryMathOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(new Integer(10).byteValue()));
        }

    }

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
            Integer value = 128;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, value.byteValue());

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testIntToByteWithShort() {
            Short value = 1000;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, value.byteValue());

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testIntToChar() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x62);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (char) 0x62);

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testIntToCharWithShort() {
            Short value = 0x62;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, (char) value.intValue());

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
        public void testNegIntWithShort() {
            Short value = 0x42;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -value);

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
        public void testNotIntWithShort() {
            Short value = 0x42;
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, value);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, ~value);

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
