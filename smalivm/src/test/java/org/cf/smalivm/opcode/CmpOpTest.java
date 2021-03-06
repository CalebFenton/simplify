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
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

public class CmpOpTest {

    private static final String CLASS_NAME = "Lcmp_test;";

    public static class CmpDouble {

        private VMState expected;
        private VMState initial;

        @Test
        public void canCmpgDoubleWithEqual() {
            initial.setRegisters(0, 4.5D, "D", 2, 4.5D, "D");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected);
        }

        @Test
        public void canCmpgDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected);
        }

        @Test
        public void canCmpgDoubleWithLessThan() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected);
        }

        @Test
        public void canCmpgDoubleWithNaN() {
            initial.setRegisters(0, 0.5D, "D", 2, Double.NaN, "D");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected);
        }

        @Test
        public void canCmpgDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5F, "D", 2, new UnknownValue(), "D");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "cmpgDouble()V", initial, expected);
        }

        @Test
        public void canCmplDoubleWithEqual() {
            initial.setRegisters(0, 4.5D, "D", 2, 4.5D, "D");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "cmplDouble()V", initial, expected);
        }

        @Test
        public void canCmplDoubleWithGreaterThan() {
            initial.setRegisters(0, 20.5D, "D", 2, 0.5D, "D");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmplDouble()V", initial, expected);
        }

        @Test
        public void canCmplDoubleWithLessThan() {
            initial.setRegisters(0, 0.5D, "D", 2, 20.5D, "D");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmplDouble()V", initial, expected);
        }

        @Test
        public void canCmplDoubleWithNaN() {
            initial.setRegisters(0, 0.5D, "D", 2, Double.NaN, "D");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmplDouble()V", initial, expected);
        }

        @Test
        public void canCmplDoubleWithUnknownArgment() {
            initial.setRegisters(0, 0.5F, "D", 2, new UnknownValue(), "D");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "cmplDouble()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }
    }

    public static class FloatCmp {

        private VMState expected;
        private VMState initial;

        @Test
        public void canCmpgFloatWithEqual() {
            initial.setRegisters(0, 4.5F, "F", 1, 4.5F, "F");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected);
        }

        @Test
        public void canCmpgFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected);
        }

        @Test
        public void canCmpgFloatWithLessThan() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected);
        }

        @Test
        public void canCmpgFloatWithNaN() {
            initial.setRegisters(0, 0.5F, "F", 1, Float.NaN, "F");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected);
        }

        @Test
        public void canCmpgFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5F, "F", 1, new UnknownValue(), "F");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "cmpgFloat()V", initial, expected);
        }

        @Test
        public void canCmplFloatWithEqual() {
            initial.setRegisters(0, 4.5F, "F", 1, 4.5F, "F");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "cmplFloat()V", initial, expected);
        }

        @Test
        public void canCmplFloatWithGreaterThan() {
            initial.setRegisters(0, 20.5F, "F", 1, 0.5F, "F");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmplFloat()V", initial, expected);
        }

        @Test
        public void canCmplFloatWithLessThan() {
            initial.setRegisters(0, 0.5F, "F", 1, 20.5F, "F");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmplFloat()V", initial, expected);
        }

        @Test
        public void canCmplFloatWithNaN() {
            initial.setRegisters(0, 0.5F, "F", 1, Float.NaN, "F");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmplFloat()V", initial, expected);
        }

        @Test
        public void canCmplFloatWithUnknownArgment() {
            initial.setRegisters(0, 0.5F, "F", 1, new UnknownValue(), "F");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "cmplFloat()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }
    }

    public static class LongCmp {

        private VMState expected;
        private VMState initial;

        @Test
        public void canCmplLongWithEqual() {
            initial.setRegisters(0, 0x100L, "J", 2, 0x100L, "J");
            expected.setRegisters(0, 0, "I");

            VMTester.test(CLASS_NAME, "cmpLong()V", initial, expected);
        }

        @Test
        public void canCmplLongWithGreaterThan() {
            initial.setRegisters(0, 0x100000L, "J", 2, 0x10L, "J");
            expected.setRegisters(0, 1, "I");

            VMTester.test(CLASS_NAME, "cmpLong()V", initial, expected);
        }

        @Test
        public void canCmplLongWithUnknownArgument() {
            initial.setRegisters(0, 0x100L, "J", 2, new UnknownValue(), "J");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "cmpLong()V", initial, expected);
        }

        @Test
        public void canCmpLongWithLessThan() {
            initial.setRegisters(0, 0x10L, "J", 2, 0x100000L, "J");
            expected.setRegisters(0, -1, "I");

            VMTester.test(CLASS_NAME, "cmpLong()V", initial, expected);
        }

        @BeforeEach
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }
    }

    @ExtendWith(MockitoExtension.class)
    @MockitoSettings(strictness = Strictness.LENIENT)
    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final String CMP_TYPE = "I";
        private static final int REGISTER_A = 0;
        private static final int REGISTER_B = 2;
        private static final int REGISTER_C = 4;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private BuilderInstruction instruction;
        private HeapItem itemB;
        private HeapItem itemC;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private CmpOp op;
        private CmpOpFactory opFactory;
        private ArgumentCaptor<HeapItem> setItem;
        private VirtualMachine vm;

        @Test
        public void canArg1IsUnknownReturnsUnknown() {
            Object value1 = new UnknownValue();
            when(itemB.isUnknown()).thenReturn(true);
            long value2 = 1149239296L;
            when(itemB.getValue()).thenReturn(value1);
            when(itemC.getValue()).thenReturn(value2);
            when(itemB.getType()).thenReturn("J");
            when(itemC.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG);

            op = (CmpOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            Object cmpValue = new UnknownValue();
            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue.getClass(), setItem.getValue().getValue().getClass());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
            assertEquals("cmp-long r0, r2, r4", op.toString());
        }

        @Test
        public void canCompareDouble() {
            double value1 = 11204.0345612345D;
            double value2 = 11492.3929612345D;
            int cmpValue = Double.compare(value1, value2);
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", cmpValue);
        }

        @Test
        public void canCompareDoubleGreaterWithArg1NaN() {
            double value1 = Double.NaN;
            double value2 = 11492.3929612345D;
            doTest(value1, value2, Opcode.CMPG_DOUBLE, "D", 1);
        }

        @Test
        public void canCompareDoubleLessWithArg1NaN() {
            double value1 = Double.NaN;
            double value2 = 11492.3929612345D;
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", -1);
        }

        @Test
        public void canCompareFloat() {
            float value1 = 11204.03456F;
            float value2 = 11492.39296F;
            int cmpValue = Float.compare(value1, value2);
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", cmpValue);
        }

        @Test
        public void canCompareFloatGreaterWithArg1NaN() {
            float value1 = Float.NaN;
            float value2 = 11492.39296F;
            doTest(value1, value2, Opcode.CMPG_FLOAT, "F", 1);
        }

        @Test
        public void canCompareFloatLessWithArg1NaN() {
            float value1 = Float.NaN;
            float value2 = 11492.39296F;
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", -1);
        }

        @Test
        public void canCompareLong() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            int cmpValue = Long.compare(value1, value2);
            doTest(value1, value2, Opcode.CMP_LONG, "J", cmpValue);
        }

        @BeforeEach
        public void setUp() {
            vm = mock(VirtualMachineImpl.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            setItem = ArgumentCaptor.forClass(HeapItem.class);
            itemB = mock(HeapItem.class);
            when(itemB.isUnknown()).thenReturn(false);
            when(mState.readRegister(REGISTER_B)).thenReturn(itemB);
            itemC = mock(HeapItem.class);
            when(itemC.isUnknown()).thenReturn(false);
            when(mState.readRegister(REGISTER_C)).thenReturn(itemC);

            location = mock(MethodLocation.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(REGISTER_A);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(REGISTER_B);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(REGISTER_C);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new CmpOpFactory();
        }

        @Test
        public void testArg2IsUnknownReturnsUnknown() {
            long value1 = 1149239296L;
            Object value2 = new UnknownValue();
            when(itemC.isUnknown()).thenReturn(true);
            when(itemB.getValue()).thenReturn(value1);
            when(itemC.getValue()).thenReturn(value2);
            when(itemB.getType()).thenReturn("J");
            when(itemC.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG);

            op = (CmpOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            Object cmpValue = new UnknownValue();
            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue.getClass(), setItem.getValue().getValue().getClass());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
        }

        private void doTest(Object value1, Object value2, Opcode opcode, String inputType, int cmpValue) {
            when(itemB.getValue()).thenReturn(value1);
            when(itemC.getValue()).thenReturn(value2);
            when(itemB.getType()).thenReturn(inputType);
            when(itemC.getType()).thenReturn(inputType);
            when(instruction.getOpcode()).thenReturn(opcode);

            op = (CmpOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue, setItem.getValue().getValue());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
        }
    }

}
