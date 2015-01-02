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
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestCmpOp {

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        // Mocks
        static BuilderInstruction mockBi;
        static OpFactory opFactory;

        CmpOp underTest;

        @BeforeClass
        public static void setUp() {
            VirtualMachine mockVm = mock(VirtualMachine.class);
            mockBi = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));

            opFactory = new OpFactory(mockVm);
        }

        @Test
        public void canCompareLong() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(1120403456L).thenReturn(1149239296L);
            when(mockBi.getOpcode()).thenReturn(Opcode.CMP_LONG);

            underTest = (CmpOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(Long.compare(1120403456L, 1149239296L)));
        }

        @Test
        public void canCompareFloat() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.03456F).thenReturn(11492.39296F);
            when(mockBi.getOpcode()).thenReturn(Opcode.CMPL_FLOAT);

            underTest = (CmpOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class), eq(Float.compare(11204.03456F, 11492.39296F)));
        }

        @Test
        public void canCompareDouble() {
            MethodState mockMs = mock(MethodState.class);

            when(mockMs.readRegister(any(Integer.class))).thenReturn(11204.0345612345D).thenReturn(11492.3929612345D);
            when(mockBi.getOpcode()).thenReturn(Opcode.CMPL_DOUBLE);

            underTest = (CmpOp) opFactory.create(mockBi, 0);
            underTest.execute(mockMs);

            verify(mockMs, times(1)).assignRegister(any(Integer.class),
                            eq(Double.compare(11204.0345612345D, 11492.3929612345D)));
        }
    }

    private static final String CLASS_NAME = "Lcmp_test;";

    public static class TestDouble {
        @Test
        public void testCmplDoubleWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5D, 2, 4.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, Double.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, new UnknownValue("D"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testMethodState(CLASS_NAME, "CmplDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, 20.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5D, 2, 0.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5D, 2, 4.5D);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5D, 2, Double.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }

        @Test
        public void testCmpgDoubleWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 2, new UnknownValue("D"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("D"));

            VMTester.testMethodState(CLASS_NAME, "CmpgDouble()V", initial, expected);
        }
    }

    public static class TestFloat {
        @Test
        public void testCmplFloatWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5F, 1, 4.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, Float.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmplFloatWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, new UnknownValue("F"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testMethodState(CLASS_NAME, "CmplFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, 20.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 20.5F, 1, 0.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 4.5F, 1, 4.5F);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithNaN() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, Float.NaN);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }

        @Test
        public void testCmpgFloatWithUnknownArgment() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0.5F, 1, new UnknownValue("F"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("F"));

            VMTester.testMethodState(CLASS_NAME, "CmpgFloat()V", initial, expected);
        }
    }

    public static class TestLong {
        @Test
        public void testCmpLongWithLessThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x10L, 2, 0x100000L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -1);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithGreaterThan() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100000L, 2, 0x10L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 1);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithEqual() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L, 2, 0x100L);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0);

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }

        @Test
        public void testCmplDoubleWithUnknownArgument() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x100L, 2, new UnknownValue("J"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("J"));

            VMTester.testMethodState(CLASS_NAME, "CmpLong()V", initial, expected);
        }
    }

}
