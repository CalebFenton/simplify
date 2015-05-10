package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestCmpOp {

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int REGISTER_A = 0;
        private static final int REGISTER_B = 2;
        private static final int REGISTER_C = 4;
        private static final String CMP_TYPE = "I";

        private BuilderInstruction instruction;
        private OpFactory opFactory;
        private MethodState mState;
        private ExecutionNode node;
        private HeapItem itemB;
        private HeapItem itemC;
        private CmpOp op;
        private ArgumentCaptor<HeapItem> setItem;

        @Before
        public void setUp() {
            VirtualMachine vm = mock(VirtualMachine.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(REGISTER_A);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(REGISTER_B);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(REGISTER_C);
            opFactory = new OpFactory(vm);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            itemB = mock(HeapItem.class);
            when(mState.readRegister(REGISTER_B)).thenReturn(itemB);
            itemC = mock(HeapItem.class);
            when(mState.readRegister(REGISTER_C)).thenReturn(itemC);

            when(itemB.isUnknown()).thenReturn(false);
            when(itemC.isUnknown()).thenReturn(false);

            setItem = ArgumentCaptor.forClass(HeapItem.class);
        }

        private void doTest(Object value1, Object value2, Opcode opcode, String inputType, int cmpValue) {
            when(itemB.getValue()).thenReturn(value1);
            when(itemC.getValue()).thenReturn(value2);
            when(itemB.getType()).thenReturn(inputType);
            when(itemC.getType()).thenReturn(inputType);
            when(instruction.getOpcode()).thenReturn(opcode);

            op = (CmpOp) opFactory.create(instruction, ADDRESS);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue, setItem.getValue().getValue());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
        }

        @Test
        public void testArg1IsUnknownReturnsUnknown() {
            Object value1 = new UnknownValue();
            when(itemB.isUnknown()).thenReturn(true);
            long value2 = 1149239296L;
            when(itemB.getValue()).thenReturn(value1);
            when(itemC.getValue()).thenReturn(value2);
            when(itemB.getType()).thenReturn("J");
            when(itemC.getType()).thenReturn("J");
            when(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG);

            op = (CmpOp) opFactory.create(instruction, ADDRESS);
            op.execute(node, mState);

            Object cmpValue = new UnknownValue();
            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue.getClass(), setItem.getValue().getValue().getClass());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
            assertEquals("cmp-long r0, r2, r4", op.toString());
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

            op = (CmpOp) opFactory.create(instruction, ADDRESS);
            op.execute(node, mState);

            Object cmpValue = new UnknownValue();
            verify(mState, times(1)).assignRegister(eq(REGISTER_A), setItem.capture());
            assertEquals(cmpValue.getClass(), setItem.getValue().getValue().getClass());
            assertEquals(CMP_TYPE, setItem.getValue().getType());
        }

        @Test
        public void canCompareLong() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            int cmpValue = Long.compare(value1, value2);
            doTest(value1, value2, Opcode.CMP_LONG, "J", cmpValue);
        }

        @Test
        public void canCompareFloat() {
            float value1 = 11204.03456F;
            float value2 = 11492.39296F;
            int cmpValue = Float.compare(value1, value2);
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", cmpValue);
        }

        @Test
        public void canCompareFloatLessWithArg1NaN() {
            float value1 = Float.NaN;
            float value2 = 11492.39296F;
            doTest(value1, value2, Opcode.CMPL_FLOAT, "F", -1);
        }

        @Test
        public void canCompareFloatGreaterWithArg1NaN() {
            float value1 = Float.NaN;
            float value2 = 11492.39296F;
            doTest(value1, value2, Opcode.CMPG_FLOAT, "F", 1);
        }

        @Test
        public void canCompareDouble() {
            double value1 = 11204.0345612345D;
            double value2 = 11492.3929612345D;
            int cmpValue = Double.compare(value1, value2);
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", cmpValue);
        }

        @Test
        public void canCompareDoubleLessWithArg1NaN() {
            double value1 = Double.NaN;
            double value2 = 11492.3929612345D;
            doTest(value1, value2, Opcode.CMPL_DOUBLE, "D", -1);
        }

        @Test
        public void canCompareDoubleGreaterWithArg1NaN() {
            double value1 = Double.NaN;
            double value2 = 11492.3929612345D;
            doTest(value1, value2, Opcode.CMPG_DOUBLE, "D", 1);
        }
    }

    private static final String CLASS_NAME = "Lcmp_test;";

    public static class TestDouble {

        @Test
        public void testCmplDoubleWithLessThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithGreaterThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 20.5D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithEqual() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 4.5D, "D", 2, 4.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithNaN() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, Double.NaN, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithUnknownArgment() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "D", 2, new UnknownValue(), "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithLessThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, 20.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithGreaterThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 20.5D, "D", 2, 0.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithEqual() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 4.5D, "D", 2, 4.5D, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithNaN() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5D, "D", 2, Double.NaN, "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithUnknownArgment() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "D", 2, new UnknownValue(), "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }
    }

    public static class TestFloat {

        @Test
        public void testCmplFloatWithLessThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithGreaterThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 20.5F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithEqual() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 4.5F, "F", 1, 4.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithNaN() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, Float.NaN, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithUnknownArgment() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, new UnknownValue(), "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithLessThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, 20.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithGreaterThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 20.5F, "F", 1, 0.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithEqual() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 4.5F, "F", 1, 4.5F, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithNaN() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, Float.NaN, "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithUnknownArgment() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0.5F, "F", 1, new UnknownValue(), "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }
    }

    public static class TestLong {

        @Test
        public void testCmpLongWithLessThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x10L, "J", 2, 0x100000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplLongWithGreaterThan() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100000L, "J", 2, 0x10L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 1, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplLongWithEqual() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100L, "J", 2, 0x100L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplLongWithUnknownArgument() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x100L, "J", 2, new UnknownValue(), "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }
    }

}
