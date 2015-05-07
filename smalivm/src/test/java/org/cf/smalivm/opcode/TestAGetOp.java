package org.cf.smalivm.opcode;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.SmaliClassUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(Enclosed.class)
public class TestAGetOp {

    private static final String CLASS_NAME = "Laget_test;";

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int VALUE_REGISTER = 0;
        private static final int ARRAY_REGISTER = 1;
        private static final int INDEX_REGISTER = 2;

        private BuilderInstruction instruction;
        private OpFactory opFactory;
        private MethodState mState;
        private AGetOp op;

        @Before
        public void setUp() {
            VirtualMachine vm = mock(VirtualMachine.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(VALUE_REGISTER);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(ARRAY_REGISTER);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(INDEX_REGISTER);

            opFactory = new OpFactory(vm);
            mState = mock(MethodState.class);
        }

        @Test
        public void arrayIndexOutOfBoundsExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;

            HeapItem arrayItem = mock(HeapItem.class);
            when(arrayItem.getValue()).thenReturn(arrayValue);
            when(arrayItem.getType()).thenReturn("[I");
            when(mState.readRegister(eq(ARRAY_REGISTER))).thenReturn(arrayItem);

            HeapItem indexItem = mock(HeapItem.class);
            when(indexItem.getValue()).thenReturn(indexValue);
            when(indexItem.getIntegerValue()).thenReturn(indexValue);
            when(indexItem.getType()).thenReturn("I");
            when(mState.readRegister(eq(INDEX_REGISTER))).thenReturn(indexItem);

            when(instruction.getOpcode()).thenReturn(Opcode.AGET);

            op = (AGetOp) opFactory.create(instruction, ADDRESS);
            int[] children = op.execute(mState);

            VirtualException[] expectedExceptions = new VirtualException[] { new VirtualException(
                            SmaliClassUtils.javaClassToSmali(ArrayIndexOutOfBoundsException.class)) };
            assertArrayEquals(expectedExceptions, op.getExceptions());

            assertArrayEquals(new int[0], children);
            verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
        }

        @Test
        public void nullPointerExceptionIsCaughtAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = null;
            int indexValue = 2;

            HeapItem arrayItem = mock(HeapItem.class);
            when(arrayItem.getValue()).thenReturn(arrayValue);
            when(arrayItem.getType()).thenReturn("[I");
            when(mState.readRegister(eq(ARRAY_REGISTER))).thenReturn(arrayItem);

            HeapItem indexItem = mock(HeapItem.class);
            when(indexItem.getValue()).thenReturn(indexValue);
            when(indexItem.getIntegerValue()).thenReturn(indexValue);
            when(indexItem.getType()).thenReturn("I");
            when(mState.readRegister(eq(INDEX_REGISTER))).thenReturn(indexItem);

            when(instruction.getOpcode()).thenReturn(Opcode.AGET);

            op = (AGetOp) opFactory.create(instruction, ADDRESS);
            int[] children = op.execute(mState);

            VirtualException[] expectedExceptions = new VirtualException[] { new VirtualException(
                            SmaliClassUtils.javaClassToSmali(ArrayIndexOutOfBoundsException.class)) };
            assertArrayEquals(expectedExceptions, op.getExceptions());

            assertArrayEquals(new int[0], children);
            verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
        }

    }

    public static class IntegrationTest {

        @Test
        public void testArrayGet() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x42, "I");

            VMTester.testMethodState(CLASS_NAME, "ArrayGet()V", initial, expected);
        }

        @Test
        public void testArrayGetWithIndexOutOfBoundsDoesNotChangeRegisterState() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, 1, "I");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "ArrayGetWithCatch()V", initial, expected);
        }

        @Test
        public void testArrayGetWithIndexOutOfBoundsVisitsExceptionHandler() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, 1, "I");
            int[] expectedVisitations = new int[] { 0, 2, 3 };

            VMTester.testVisitation(CLASS_NAME, "ArrayGetWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void testArrayGetWithNullArrayDoesNotChangeRegisterState() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, null, "[I", 1, 1, "I");
            TIntObjectMap<HeapItem> expected = initial;

            VMTester.testMethodState(CLASS_NAME, "ArrayGetWithCatch()V", initial, expected);
        }

        @Test
        public void testArrayGetWithNullArrayVisitsExceptionHandler() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, null, "[I", 1, 1, "I");
            int[] expectedVisitations = new int[] { 0, 2, 3 };

            VMTester.testVisitation(CLASS_NAME, "ArrayGetWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void testArrayGetWithShortIndex() {
            Short index = 0;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1, index, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(index.intValue(), 0x42, "S");

            VMTester.testMethodState(CLASS_NAME, "ArrayGet()V", initial, expected);
        }

        @Test
        public void testArrayGetBoolean() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetBoolean()V", initial, expected);
        }

        @Test
        public void testArrayGetByte() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[] { 0xe }, "[B", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (byte) 0xe, "B");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetByte()V", initial, expected);
        }

        @Test
        public void testArrayGetChar() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[] { 'a' }, "[C", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 'a', "C");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetChar()V", initial, expected);
        }

        @Test
        public void testArrayGetObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                            CLASS_NAME) }, CLASS_NAME, 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester
                            .buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME);

            VMTester.testMethodState(CLASS_NAME, "ArrayGetObject()V", initial, expected);
        }

        @Test
        public void testArrayGetShort() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[] { 0x42 }, "[S", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (short) 0x42, "S");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetShort()V", initial, expected);
        }

        @Test
        public void testArrayGetUninitializedPrimitive() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, (new int[1])[0], "I");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetUninitializedInt()V", initial, expected);
        }

        @Test
        public void testArrayGetUnknownArray() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "[I", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "ArrayGet()V", initial, expected);
        }

        @Test
        public void testArrayGetUnknownElement() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new Object[] { new UnknownValue(), 5 },
                            "[I", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "ArrayGet()V", initial, expected);
        }

        @Test
        public void testArrayGetUnknownIndex() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, "[I", 1,
                            new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "ArrayGet()V", initial, expected);
        }

        @Test
        public void testArrayGetWide() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[] { 0x10000000000L }, "J", 1, 0,
                            "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x10000000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "ArrayGetWide()V", initial, expected);
        }
    }

}
