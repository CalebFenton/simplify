package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineImpl;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

public class UnaryMathOpTest {

    private static final String CLASS_NAME = "Lunary_math_test;";

    public static class DoubleValue {

        private VMState expected;
        private VMState initial;

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testDoubleToFloat() {
            initial.setRegisters(0, 220D, "D");
            expected.setRegisters(0, 220F, "F");

            VMTester.test(CLASS_NAME, "doubleToFloat()V", initial, expected);
        }

        @Test
        public void testDoubleToInt() {
            initial.setRegisters(0, 200D, "D");
            expected.setRegisters(0, 200, "I");

            VMTester.test(CLASS_NAME, "doubleToInt()V", initial, expected);
        }

        @Test
        public void testDoubleToLong() {
            initial.setRegisters(0, 210D, "D");
            expected.setRegisters(0, 210L, "J");

            VMTester.test(CLASS_NAME, "doubleToLong()V", initial, expected);
        }

        @Test
        public void testNegDouble() {
            initial.setRegisters(0, 15.1D, "D");
            expected.setRegisters(0, -15.1D, "D");

            VMTester.test(CLASS_NAME, "negDouble()V", initial, expected);
        }

    }

    public static class FloatValue {

        private VMState expected;
        private VMState initial;

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testFloatToDouble() {
            initial.setRegisters(0, 120F, "F");
            expected.setRegisters(0, 120D, "D");

            VMTester.test(CLASS_NAME, "floatToDouble()V", initial, expected);
        }

        @Test
        public void testFloatToInt() {
            initial.setRegisters(0, 100F, "F");
            expected.setRegisters(0, 100, "I");

            VMTester.test(CLASS_NAME, "floatToInt()V", initial, expected);
        }

        @Test
        public void testFloatToLong() {
            initial.setRegisters(0, 110F, "F");
            expected.setRegisters(0, 110L, "J");

            VMTester.test(CLASS_NAME, "floatToLong()V", initial, expected);
        }

        @Test
        public void testNegFloat() {
            initial.setRegisters(0, 10.5F, "F");
            expected.setRegisters(0, -10.5F, "F");

            VMTester.test(CLASS_NAME, "negFloat()V", initial, expected);
        }

    }

    public static class IntValue {

        private VMState expected;
        private VMState initial;

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testIntToByte() {
            Integer value = 128;
            initial.setRegisters(0, value, "I");
            expected.setRegisters(0, value.byteValue(), "B");

            VMTester.test(CLASS_NAME, "intToByte()V", initial, expected);
        }

        @Test
        public void testIntToByteWithShort() {
            Short value = 1000;
            initial.setRegisters(0, value, "S");
            expected.setRegisters(0, value.byteValue(), "B");

            VMTester.test(CLASS_NAME, "intToByte()V", initial, expected);
        }

        @Test
        public void testIntToChar() {
            initial.setRegisters(0, 0x62, "I");
            expected.setRegisters(0, (char) 0x62, "C");

            VMTester.test(CLASS_NAME, "intToChar()V", initial, expected);
        }

        @Test
        public void testIntToCharWithShort() {
            Short value = 0x62;
            initial.setRegisters(0, value, "S");
            expected.setRegisters(0, (char) value.intValue(), "C");

            VMTester.test(CLASS_NAME, "intToChar()V", initial, expected);
        }

        @Test
        public void testIntToDouble() {
            initial.setRegisters(0, 13, "I");
            expected.setRegisters(0, 13D, "D");

            VMTester.test(CLASS_NAME, "intToDouble()V", initial, expected);
        }

        @Test
        public void testIntToFloat() {
            initial.setRegisters(0, 12, "I");
            expected.setRegisters(0, 12F, "F");

            VMTester.test(CLASS_NAME, "intToFloat()V", initial, expected);
        }

        @Test
        public void testIntToLong() {
            initial.setRegisters(0, 11, "I");
            expected.setRegisters(0, 11L, "J");

            VMTester.test(CLASS_NAME, "intToLong()V", initial, expected);
        }

        @Test
        public void testIntToShort() {
            initial.setRegisters(0, 0x10, "I");
            expected.setRegisters(0, (short) 0x10, "S");

            VMTester.test(CLASS_NAME, "intToShort()V", initial, expected);
        }

        @Test
        public void testNegInt() {
            initial.setRegisters(0, 0x42, "I");
            expected.setRegisters(0, -0x42, "I");

            VMTester.test(CLASS_NAME, "negInt()V", initial, expected);
        }

        @Test
        public void testNegIntWithShort() {
            Short value = 0x42;
            initial.setRegisters(0, value, "S");
            expected.setRegisters(0, -value, "I");

            VMTester.test(CLASS_NAME, "negInt()V", initial, expected);
        }

        @Test
        public void testNegUnknownInt() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "negInt()V", initial, expected);
        }

        @Test
        public void testNotInt() {
            initial.setRegisters(0, 0x42, "I");
            expected.setRegisters(0, ~0x42, "I");

            VMTester.test(CLASS_NAME, "notInt()V", initial, expected);
        }

        @Test
        public void testNotIntWithShort() {
            Short value = 0x42;
            initial.setRegisters(0, value, "S");
            expected.setRegisters(0, ~value, "I");

            VMTester.test(CLASS_NAME, "notInt()V", initial, expected);
        }

        @Test
        public void testNotUnknownInt() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "notInt()V", initial, expected);
        }

        @Test
        public void testUnknownIntToByte() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "B");

            VMTester.test(CLASS_NAME, "intToByte()V", initial, expected);
        }

        @Test
        public void testUnknownIntToChar() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "C");

            VMTester.test(CLASS_NAME, "intToChar()V", initial, expected);
        }

        @Test
        public void testUnknownIntToDouble() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "D");

            VMTester.test(CLASS_NAME, "intToDouble()V", initial, expected);
        }

        @Test
        public void testUnknownIntToFloat() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "F");

            VMTester.test(CLASS_NAME, "intToFloat()V", initial, expected);
        }

        @Test
        public void testUnknownIntToLong() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "J");

            VMTester.test(CLASS_NAME, "intToLong()V", initial, expected);
        }

        @Test
        public void testUnknownIntToShort() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "S");

            VMTester.test(CLASS_NAME, "intToShort()V", initial, expected);
        }

    }

    public static class LongValue {

        private VMState expected;
        private VMState initial;

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testLongToDouble() {
            initial.setRegisters(0, 23L, "J");
            expected.setRegisters(0, 23D, "D");

            VMTester.test(CLASS_NAME, "longToDouble()V", initial, expected);
        }

        @Test
        public void testLongToFloat() {
            initial.setRegisters(0, 22L, "J");
            expected.setRegisters(0, 22F, "F");

            VMTester.test(CLASS_NAME, "longToFloat()V", initial, expected);
        }

        @Test
        public void testLongToInt() {
            initial.setRegisters(0, 21L, "J");
            expected.setRegisters(0, 21, "I");

            VMTester.test(CLASS_NAME, "longToInt()V", initial, expected);
        }

        @Test
        public void testNegLong() {
            initial.setRegisters(0, 0x100L, "J");
            expected.setRegisters(0, -0x100L, "J");

            VMTester.test(CLASS_NAME, "negLong()V", initial, expected);
        }

        @Test
        public void testNotLong() {
            initial.setRegisters(0, 100L, "J");
            expected.setRegisters(0, ~100L, "J");

            VMTester.test(CLASS_NAME, "notLong()V", initial, expected);
        }

    }

    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
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

        @BeforeEach
        public void setUp() {
            vm = mock(VirtualMachineImpl.class);
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

}
