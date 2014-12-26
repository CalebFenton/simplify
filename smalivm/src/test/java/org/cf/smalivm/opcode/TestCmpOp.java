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
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestCmpOp {

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {
        BuilderInstruction instruction;
        OpFactory opFactory;
        MethodState mState;
        CmpOp opUnderTest;

        @Before
        public void setUp() {
            VirtualMachine mockVm = mock(VirtualMachine.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));
            opFactory = new OpFactory(mockVm);
            mState = mock(MethodState.class);
        }

        @Test
        public void canCompareLong() {
            long value1 = 1120403456L;
            long value2 = 1149239296L;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("J");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.CMP_LONG);

            opUnderTest = (CmpOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            int cmpValue = Long.compare(value1, value2);
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(new HeapItem(cmpValue, "I")));
        }

        @Test
        public void canCompareFloat() {
            float value1 = 11204.03456F;
            float value2 = 11492.39296F;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("F");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.CMPL_FLOAT);

            opUnderTest = (CmpOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            int cmpValue = Float.compare(value1, value2);
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(new HeapItem(cmpValue, "I")));
        }

        @Test
        public void canCompareDouble() {
            double value1 = 11204.0345612345D;
            double value2 = 11492.3929612345D;
            HeapItem mockItem = mock(HeapItem.class);
            when(mockItem.getValue()).thenReturn(value1).thenReturn(value2);
            when(mockItem.getType()).thenReturn("D");
            when(mState.readRegister(any(Integer.class))).thenReturn(mockItem);
            when(instruction.getOpcode()).thenReturn(Opcode.CMPL_DOUBLE);

            opUnderTest = (CmpOp) opFactory.create(instruction, 0);
            opUnderTest.execute(mState);

            int cmpValue = Double.compare(value1, value2);
            verify(mState, times(1)).assignRegister(any(Integer.class), eq(new HeapItem(cmpValue, "I")));
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
