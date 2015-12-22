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

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.ThreeRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(Enclosed.class)
public class APutOpTest {

    public static class IntegrationTest {

        @Test
        public void testPutBoolean() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, 0x1,
                            "Z");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

            VMTester.testMethodState(CLASS_NAME, "putBoolean()V", initial, expected);
        }

        @Test
        public void testPutBooleanWithShortValue() {
            Short value = 0x1;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new boolean[1], "[Z", 1, 0, "I", 2, value,
                            "Z");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new boolean[] { true }, "[Z");

            VMTester.testMethodState(CLASS_NAME, "putBoolean()V", initial, expected);
        }

        @Test
        public void testPutByte() {
            Byte value = 0xf;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value,
                            "B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { value }, "[B");

            VMTester.testMethodState(CLASS_NAME, "putByte()V", initial, expected);
        }

        @Test
        public void testPutByteFromInt() {
            int value = 0xf;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new byte[1], "[B", 1, 0, "I", 2, value,
                            "B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new byte[] { (byte) value }, "[B");

            VMTester.testMethodState(CLASS_NAME, "putByte()V", initial, expected);
        }

        @Test
        public void testPutChar() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2, '$', "C");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

            VMTester.testMethodState(CLASS_NAME, "putChar()V", initial, expected);
        }

        @Test
        public void testPutCharFromInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new char[1], "[C", 1, 0, "I", 2,
                            (int) '$', "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new char[] { '$' }, "[C");

            VMTester.testMethodState(CLASS_NAME, "putChar()V", initial, expected);
        }

        @Test
        public void testPutIntegerWithShortIndex() {
            Short index = 0;
            TIntObjectMap<HeapItem> initial = VMTester
                            .buildRegisterState(0, new int[1], "[I", 1, index, "S", 2, 4, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

            VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void testPutObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance[1], "[" + CLASS_NAME, 1,
                            0, "I", 2, new LocalInstance(CLASS_NAME), CLASS_NAME);
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                            CLASS_NAME) }, "[" + CLASS_NAME);

            VMTester.testMethodState(CLASS_NAME, "putObject()V", initial, expected);
        }

        @Test
        public void testPutShort() {
            Short value = 0x42;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value,
                            "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { value }, "[S");

            VMTester.testMethodState(CLASS_NAME, "putShort()V", initial, expected);
        }

        @Test
        public void testPutShortWithIntegerValue() {
            int value = 0x42;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new short[1], "[S", 1, 0, "I", 2, value,
                            "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new short[] { (short) value }, "[S");

            VMTester.testMethodState(CLASS_NAME, "putShort()V", initial, expected);
        }

        @Test
        public void testPutUnknownValue() {
            // TODO: Ideally, setting an element unknown shouldn't set entire array unknown. See APutOp for more
            // details.
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2,
                            new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

            VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void testPutWideWithDouble() {
            Double value = 100000000000D;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new double[1], "[D", 1, 0, "I", 2, value,
                            "D");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new double[] { value }, "[D");

            VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void testPutWideWithFloat() {
            Float value = 10.45F;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new float[1], "[F", 1, 0, "I", 2, value,
                            "F");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new float[] { value }, "[F");

            VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void testPutWideWithLong() {
            Long value = 10000000000L;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new long[1], "[J", 1, 0, "I", 2, value,
                            "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new long[] { value }, "[J");

            VMTester.testMethodState(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void testPutWithInteger() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, 0, "I", 2, 4, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 4 }, "[I");

            VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void testPutWithUnknownIndex() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[1], "[I", 1, new UnknownValue(),
                            "I", 2, 5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

            VMTester.testMethodState(CLASS_NAME, "put()V", initial, expected);
        }
    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARRAY_REGISTER = 1;
        private static final int INDEX_REGISTER = 2;
        private static final int VALUE_REGISTER = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private ClassManager classManager;
        private BuilderInstruction instruction;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private APutOp op;
        private APutOpFactory opFactory;
        private ArgumentCaptor<HeapItem> setItem;
        private VirtualMachine vm;

        // @Test
        public void canInsertLocalClassAndClass() {
            // TODO: https://github.com/CalebFenton/simplify/issues/25
            // Need to change new-array to return [HeapItem and make everything aware of that
            // First part easy, second part hard
            when(mState.readRegister(VALUE_REGISTER)).thenReturn(
                            new HeapItem(new LocalClass("Landroid/app/Activity;"), "Ljava/lang/Class;"));
            when(mState.readRegister(ARRAY_REGISTER)).thenReturn(
                            new HeapItem(new LocalClass("Landroid/app/Activity;"), "Ljava/lang/Class;"));

            when(instruction.getOpcode()).thenReturn(Opcode.APUT_OBJECT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            // Division result is zero since long division drops decimal value
            // verify(mState, times(1)).assignRegister(any(Integer.class), eq(value1 / value2), eq("J"));
        }

        @Test
        public void incompatibleValueTypeThrowsArrayStoreExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 1, 2, 3 };
            int indexValue = 0;
            String value = "wont work";

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayStoreException.class, "java.lang.String");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = null;
            int indexValue = 2;
            int value = 0;

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(NullPointerException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;
            String value = "wont work";

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayStoreException.class, "java.lang.String");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;
            int value = 0;

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayIndexOutOfBoundsException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() throws UnknownAncestors {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            classManager = mock(ClassManager.class);
            when(vm.getClassManager()).thenReturn(classManager);
            when(classManager.isInstance("I", "[I")).thenReturn(true);
            when(classManager.isInstance("Ljava/lang/String;", "[I")).thenReturn(false);
            setItem = ArgumentCaptor.forClass(HeapItem.class);

            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(ThreeRegisterInstruction.class, Instruction23x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(VALUE_REGISTER);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(ARRAY_REGISTER);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(INDEX_REGISTER);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new APutOpFactory();
        }

        @Test
        public void unknownArrayWithIncompatibleTypeThrowsArrayStoreExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            Object arrayValue = new UnknownValue();
            int indexValue = 2;
            String value = "wont work";

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayStoreException.class, "java.lang.String");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void unknownValueItemMakesArrayUnknownAndDoesNotClearExceptions() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;
            Object value = new UnknownValue();

            VMTester.addHeapItem(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.addHeapItem(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.addHeapItem(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARRAY_REGISTER), setItem.capture());
            assertEquals(UnknownValue.class, setItem.getValue().getValue().getClass());
            verify(node, times(0)).clearExceptions();
        }
    }

    private static final String CLASS_NAME = "Laput_test;";

}
