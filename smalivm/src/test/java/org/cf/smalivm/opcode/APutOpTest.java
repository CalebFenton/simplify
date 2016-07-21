package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualArray;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualPrimitive;
import org.cf.util.ClassNameUtils;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(Enclosed.class)
public class APutOpTest {

    private static final String CLASS_NAME = "Laput_test;";

    public static class IntegrationTest {

        private VMState initial;
        private VMState expected;

        @Test
        public void canInsertLocalClassAndClassIntoSameArray() throws ClassNotFoundException {
            String valueType = CommonTypes.CLASS;
            String arrayType = "[" + valueType;
            Object[] array = new Class<?>[2];
            int index1 = 0;
            int index2 = 1;
            Class<?> value1 = String.class;
            String binaryClassName = ClassNameUtils.internalToBinary(CLASS_NAME);
            ClassLoader classLoader = VMTester.spawnVM().getClassLoader();
            Class<?> value2 = classLoader.loadClass(binaryClassName);

            initial.setRegisters(0, array, arrayType, 1, index1, "I", 2, value1, valueType, 3, index2, "I", 4, value2,
                    valueType);
            expected.setRegisters(0, new Class<?>[] { value1, value2 }, arrayType);

            VMTester.test(CLASS_NAME, "putObjects()V", initial, expected);
        }

        @Test
        public void canPutBoolean() {
            initial.setRegisters(0, new boolean[1], "[Z", 1, 0, "I", 2, 0x1, "Z");
            expected.setRegisters(0, new boolean[] { true }, "[Z");

            VMTester.test(CLASS_NAME, "putBoolean()V", initial, expected);
        }

        @Test
        public void canPutBooleanWithShortValue() {
            Short value = 0x1;
            initial.setRegisters(0, new boolean[1], "[Z", 1, 0, "I", 2, value, "Z");
            expected.setRegisters(0, new boolean[] { true }, "[Z");

            VMTester.test(CLASS_NAME, "putBoolean()V", initial, expected);
        }

        @Test
        public void canPutByte() {
            Byte value = 0xf;
            initial.setRegisters(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
            expected.setRegisters(0, new byte[] { value }, "[B");

            VMTester.test(CLASS_NAME, "putByte()V", initial, expected);
        }

        @Test
        public void canPutByteFromInt() {
            int value = 0xf;
            initial.setRegisters(0, new byte[1], "[B", 1, 0, "I", 2, value, "B");
            expected.setRegisters(0, new byte[] { (byte) value }, "[B");

            VMTester.test(CLASS_NAME, "putByte()V", initial, expected);
        }

        @Test
        public void canPutChar() {
            initial.setRegisters(0, new char[1], "[C", 1, 0, "I", 2, '$', "C");
            expected.setRegisters(0, new char[] { '$' }, "[C");

            VMTester.test(CLASS_NAME, "putChar()V", initial, expected);
        }

        @Test
        public void canPutCharFromInt() {
            initial.setRegisters(0, new char[1], "[C", 1, 0, "I", 2, (int) '$', "I");
            expected.setRegisters(0, new char[] { '$' }, "[C");

            VMTester.test(CLASS_NAME, "putChar()V", initial, expected);
        }

        @Test
        public void canPutConstZeroNullObject() {
            String valueType = "I";
            String arrayType = "[" + valueType;
            Object[] array = new String[1];
            int index = 0;
            int value = 0;

            initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
            expected.setRegisters(0, new String[] { null }, arrayType);

            VMTester.test(CLASS_NAME, "putObject()V", initial, expected);
        }

        @Test
        public void canPutIntegerWithShortIndex() {
            Short index = 0;
            initial.setRegisters(0, new int[1], "[I", 1, index, "S", 2, 4, "I");
            expected.setRegisters(0, new int[] { 4 }, "[I");

            VMTester.test(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void canPutObject() {
            String valueType = "Ljava/lang/String;";
            String arrayType = "[" + valueType;
            Object[] array = new String[1];
            int index = 0;
            String value = "Arrakis, Dune, desert planet...";

            initial.setRegisters(0, array, arrayType, 1, index, "I", 2, value, valueType);
            expected.setRegisters(0, new String[] { value }, arrayType);

            VMTester.test(CLASS_NAME, "putObject()V", initial, expected);
        }

        @Test
        public void canPutShort() {
            Short value = 0x42;
            initial.setRegisters(0, new short[1], "[S", 1, 0, "I", 2, value, "S");
            expected.setRegisters(0, new short[] { value }, "[S");

            VMTester.test(CLASS_NAME, "putShort()V", initial, expected);
        }

        @Test
        public void canPutShortWithIntegerValue() {
            int value = 0x42;
            initial.setRegisters(0, new short[1], "[S", 1, 0, "I", 2, value, "I");
            expected.setRegisters(0, new short[] { (short) value }, "[S");

            VMTester.test(CLASS_NAME, "putShort()V", initial, expected);
        }

        @Test
        public void canPutUnknownValue() {
            // TODO: Ideally, setting an element unknown shouldn't set entire array unknown.
            // This is tricky to handle gracefully. See APutOp for more details.
            initial.setRegisters(0, new int[1], "[I", 1, 0, "I", 2, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "[I");

            VMTester.test(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void canPutWideWithDouble() {
            Double value = 100000000000D;
            initial.setRegisters(0, new double[1], "[D", 1, 0, "I", 2, value, "D");
            expected.setRegisters(0, new double[] { value }, "[D");

            VMTester.test(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void canPutWideWithFloat() {
            Float value = 10.45F;
            initial.setRegisters(0, new float[1], "[F", 1, 0, "I", 2, value, "F");
            expected.setRegisters(0, new float[] { value }, "[F");

            VMTester.test(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void canPutWideWithLong() {
            Long value = 10000000000L;
            initial.setRegisters(0, new long[1], "[J", 1, 0, "I", 2, value, "J");
            expected.setRegisters(0, new long[] { value }, "[J");

            VMTester.test(CLASS_NAME, "putWide()V", initial, expected);
        }

        @Test
        public void canPutWithInteger() {
            initial.setRegisters(0, new int[1], "[I", 1, 0, "I", 2, 4, "I");
            expected.setRegisters(0, new int[] { 4 }, "[I");

            VMTester.test(CLASS_NAME, "put()V", initial, expected);
        }

        @Test
        public void canPutWithUnknownIndex() {
            initial.setRegisters(0, new int[1], "[I", 1, new UnknownValue(), "I", 2, 5, "I");
            expected.setRegisters(0, new UnknownValue(), "[I");

            VMTester.test(CLASS_NAME, "put()V", initial, expected);
        }

        @Before
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
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

        @Test
        public void incompatibleValueTypeThrowsArrayStoreExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 1, 2, 3 };
            int indexValue = 0;
            String value = "wont work";

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

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

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(NullPointerException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void
        outOfBoundsIndexAndIncompatibleValueTypeThrowsArrayStoreExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;
            String value = "wont work";

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

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

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayIndexOutOfBoundsException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            classManager = mock(ClassManager.class);
            when(vm.getClassManager()).thenReturn(classManager);

            VirtualArray intArrayType = mock(VirtualArray.class);
            VirtualClass stringType = mock(VirtualClass.class);
            VirtualPrimitive intType = mock(VirtualPrimitive.class);

            when(intArrayType.getName()).thenReturn("[I");
            when(stringType.getName()).thenReturn(CommonTypes.STRING);
            when(intType.getName()).thenReturn("I");

            when(intArrayType.getComponentType()).thenReturn(intType);
            when(stringType.instanceOf(intArrayType)).thenReturn(false);
            when(intType.instanceOf(intType)).thenReturn(true);

            when(classManager.getVirtualType("I")).thenReturn(intType);
            when(classManager.getVirtualType("[I")).thenReturn(intArrayType);
            when(classManager.getVirtualType(CommonTypes.STRING)).thenReturn(stringType);

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

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "Ljava/lang/String;");

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

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");
            VMTester.setRegisterMock(mState, VALUE_REGISTER, value, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.APUT);

            op = (APutOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            verify(mState, times(1)).assignRegister(eq(ARRAY_REGISTER), setItem.capture());
            assertEquals(UnknownValue.class, setItem.getValue().getValue().getClass());
            verify(node, times(0)).clearExceptions();
        }
    }

}
