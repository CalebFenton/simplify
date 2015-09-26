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
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestUnaryMathOp {

    public static class TestStartDouble {

        @Test
        public void testDoubleToFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 220D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 220F, "F");

            VMTester.testMethodState(CLASS_NAME, "DoubleToFloat()V", initial, expected);
        }

        @Test
        public void testDoubleToInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 200D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 200, "I");

            VMTester.testMethodState(CLASS_NAME, "DoubleToInt()V", initial, expected);
        }

        @Test
        public void testDoubleToLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 210D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 210L, "J");

            VMTester.testMethodState(CLASS_NAME, "DoubleToLong()V", initial, expected);
        }

        @Test
        public void testNegDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 15.1D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -15.1D, "D");

            VMTester.testMethodState(CLASS_NAME, "NegDouble()V", initial, expected);
        }

    }

    public static class TestStartFloat {

        @Test
        public void testFloatToDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 120F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 120D, "D");

            VMTester.testMethodState(CLASS_NAME, "FloatToDouble()V", initial, expected);
        }

        @Test
        public void testFloatToInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 100F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 100, "I");

            VMTester.testMethodState(CLASS_NAME, "FloatToInt()V", initial, expected);
        }

        @Test
        public void testFloatToLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 110F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 110L, "J");

            VMTester.testMethodState(CLASS_NAME, "FloatToLong()V", initial, expected);
        }

        @Test
        public void testNegFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 10.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -10.5F, "F");

            VMTester.testMethodState(CLASS_NAME, "NegFloat()V", initial, expected);
        }
    }

    public static class TestStartInt {

        @Test
        public void testIntToByte() {
            Integer value = 128;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, value.byteValue(), "B");

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testIntToByteWithShort() {
            Short value = 1000;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, value.byteValue(), "B");

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testIntToChar() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x62, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (char) 0x62, "C");

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testIntToCharWithShort() {
            Short value = 0x62;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (char) value.intValue(), "C");

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testIntToDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 13, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 13D, "D");

            VMTester.testMethodState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void testIntToFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 12, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 12F, "F");

            VMTester.testMethodState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void testIntToLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 11, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 11L, "J");

            VMTester.testMethodState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void testIntToShort() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (short) 0x10, "S");

            VMTester.testMethodState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

        @Test
        public void testNegInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x42, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -0x42, "I");

            VMTester.testMethodState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void testNegIntWithShort() {
            Short value = 0x42;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -value, "S");

            VMTester.testMethodState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void testNegUnknownInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "NegInt()V", initial, expected);
        }

        @Test
        public void testNotInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x42, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, ~0x42, "I");

            VMTester.testMethodState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void testNotIntWithShort() {
            Short value = 0x42;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, ~value, "S");

            VMTester.testMethodState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void testNotUnknownInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "NotInt()V", initial, expected);
        }

        @Test
        public void testUnknownIntToByte() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "B");

            VMTester.testMethodState(CLASS_NAME, "IntToByte()V", initial, expected);
        }

        @Test
        public void testUnknownIntToChar() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "C");

            VMTester.testMethodState(CLASS_NAME, "IntToChar()V", initial, expected);
        }

        @Test
        public void testUnknownIntToDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "D");

            VMTester.testMethodState(CLASS_NAME, "IntToDouble()V", initial, expected);
        }

        @Test
        public void testUnknownIntToFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "F");

            VMTester.testMethodState(CLASS_NAME, "IntToFloat()V", initial, expected);
        }

        @Test
        public void testUnknownIntToLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "J");

            VMTester.testMethodState(CLASS_NAME, "IntToLong()V", initial, expected);
        }

        @Test
        public void testUnknownIntToShort() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "S");

            VMTester.testMethodState(CLASS_NAME, "IntToShort()V", initial, expected);
        }

    }

    public static class TestStartLong {

        @Test
        public void testLongToDouble() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 23L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 23D, "D");

            VMTester.testMethodState(CLASS_NAME, "LongToDouble()V", initial, expected);
        }

        @Test
        public void testLongToFloat() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 22L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 22F, "F");

            VMTester.testMethodState(CLASS_NAME, "LongToFloat()V", initial, expected);
        }

        @Test
        public void testLongToInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 21L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 21, "I");

            VMTester.testMethodState(CLASS_NAME, "LongToInt()V", initial, expected);
        }

        @Test
        public void testNegLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -0x100L, "J");

            VMTester.testMethodState(CLASS_NAME, "NegLong()V", initial, expected);
        }

        @Test
        public void testNotLong() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 100L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, ~100L, "J");

            VMTester.testMethodState(CLASS_NAME, "NotLong()V", initial, expected);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int REGISTER_A = 0;
        private static final int REGISTER_B = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private BuilderInstruction instruction;
        private HeapItem item;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private UnaryMathOp op;
        private UnaryMathOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void canDoubleToFloat() {
            Double value = 11204.0345612345D;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("D");
            when(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_FLOAT);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.floatValue(), "F")));
            assertEquals("double-to-float r" + REGISTER_A + ", r" + REGISTER_B, op.toString());
        }

        @Test
        public void canDoubleToInt() {
            Double value = 11204.0345612345D;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("D");
            when(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_INT);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.intValue(), "I")));
        }

        @Test
        public void canDoubleToLong() {
            Double value = 11204.0345612345D;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("D");
            when(instruction.getOpcode()).thenReturn(Opcode.DOUBLE_TO_LONG);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.longValue(), "J")));
        }

        @Test
        public void canFloatToDouble() {
            Float value = 11204.0345F;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("F");
            when(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_DOUBLE);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.doubleValue(), "D")));
        }

        @Test
        public void canFloatToInt() {
            Float value = 11204.0345F;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("F");
            when(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_INT);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.intValue(), "I")));
        }

        @Test
        public void canFloatToLong() {
            Float value = 11204.0345F;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("F");
            when(instruction.getOpcode()).thenReturn(Opcode.FLOAT_TO_LONG);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.longValue(), "J")));
        }

        @Test
        public void canIntToByte() {
            Integer value = 10;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("I");
            when(instruction.getOpcode()).thenReturn(Opcode.INT_TO_BYTE);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.byteValue(), "B")));
        }

        @Test
        public void canLongToDouble() {
            Long value = 112040345L;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_DOUBLE);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.doubleValue(), "D")));
        }

        @Test
        public void canLongToFloat() {
            Long value = 112040345L;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_FLOAT);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.floatValue(), "F")));
        }

        @Test
        public void canLongToInt() {
            Long value = 112040345L;
            when(item.getValue()).thenReturn(value);
            when(item.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.LONG_TO_INT);

            op = (UnaryMathOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), eq(new HeapItem(value.intValue(), "I")));
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            item = mock(HeapItem.class);
            when(mState.readRegister(REGISTER_B)).thenReturn(item);

            location = mock(MethodLocation.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction12x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((Instruction12x) instruction).getRegisterA()).thenReturn(REGISTER_A);
            when(((Instruction12x) instruction).getRegisterB()).thenReturn(REGISTER_B);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new UnaryMathOpFactory();
        }

    }

    private static final String CLASS_NAME = "Lunary_math_test;";

}
