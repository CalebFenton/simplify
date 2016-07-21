package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(Enclosed.class)
public class AGetOpTest {

    private static final String CLASS_NAME = "Laget_test;";

    public static class IntegrationTest {

        private VMState expected;
        private VMState initial;

        @Test
        public void canGet() {
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, 0, "I");
            expected.setRegisters(0, 0x42, "I");

            VMTester.test(CLASS_NAME, "get()V", initial, expected);
        }

        @Test
        public void canGetBoolean() {
            initial.setRegisters(0, new boolean[] { true }, "[Z", 1, 0, "I");
            expected.setRegisters(0, true, "Z");

            VMTester.test(CLASS_NAME, "getBoolean()V", initial, expected);
        }

        @Test
        public void canGetByte() {
            initial.setRegisters(0, new byte[] { 0xe }, "[B", 1, 0, "I");
            expected.setRegisters(0, (byte) 0xe, "B");

            VMTester.test(CLASS_NAME, "getByte()V", initial, expected);
        }

        @Test
        public void canGetChar() {
            initial.setRegisters(0, new char[] { 'a' }, "[C", 1, 0, "I");
            expected.setRegisters(0, 'a', "C");

            VMTester.test(CLASS_NAME, "getChar()V", initial, expected);
        }

        @Test
        public void canGetObject() {
            String objectValue = "stringy";
            String[] objectArray = new String[] { objectValue };
            String objectType = "Ljava/lang/String;";
            initial.setRegisters(0, objectArray, objectType, 1, 0, "I");
            expected.setRegisters(0, objectValue, objectType);

            VMTester.test(CLASS_NAME, "getObject()V", initial, expected);
        }

        @Test
        public void canGetShort() {
            initial.setRegisters(0, new short[] { 0x42 }, "[S", 1, 0, "I");
            expected.setRegisters(0, (short) 0x42, "S");

            VMTester.test(CLASS_NAME, "getShort()V", initial, expected);
        }

        @Test
        public void canGetUninitializedPrimitive() {
            initial.setRegisters(0, new int[1], "[I", 1, 0, "I");
            expected.setRegisters(0, (new int[1])[0], "I");

            VMTester.test(CLASS_NAME, "getUninitializedInt()V", initial, expected);
        }

        @Test
        public void canGetUnknownArray() {
            initial.setRegisters(0, new UnknownValue(), "[I", 1, 0, "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "get()V", initial, expected);
        }

        @Test
        public void canGetUnknownElement() {
            initial.setRegisters(0, new Object[] { new UnknownValue(), 5 }, "[I", 1, 0, "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "get()V", initial, expected);
        }

        @Test
        public void canGetUnknownIndex() {
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "get()V", initial, expected);
        }

        @Test
        public void canGetWide() {
            initial.setRegisters(0, new long[] { 0x10000000000L }, "J", 1, 0, "I");
            expected.setRegisters(0, 0x10000000000L, "J");

            VMTester.test(CLASS_NAME, "getWide()V", initial, expected);
        }

        @Test
        public void canGetWithCatchAndUnknownArrayVisitsExceptionHandlerAndChild() {
            initial.setRegisters(0, new UnknownValue(), "[I", 1, 1, "I");
            int[] expectedVisitations = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "getWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void canGetWithCatchAndUnknownIndexVisitsExceptionHandlerAndChild() {
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, new UnknownValue(), "I");
            int[] expectedVisitations = new int[] { 0, 2, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "getWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void canGetWithIndexOutOfBoundsDoesNotChangeRegisterState() {
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, 1, "I");
            expected = initial;

            VMTester.test(CLASS_NAME, "getWithCatch()V", initial, expected);
        }

        @Test
        public void canGetWithIndexOutOfBoundsVisitsExceptionHandler() {
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, 1, "I");
            int[] expectedVisitations = new int[] { 0, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "getWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void canGetWithNullArrayDoesNotChangeRegisterState() {
            initial.setRegisters(0, null, "[I", 1, 1, "I");
            expected = initial;

            VMTester.test(CLASS_NAME, "getWithCatch()V", initial, expected);
        }

        @Test
        public void canGetWithNullArrayVisitsExceptionHandler() {
            initial.setRegisters(0, null, "[I", 1, 1, "I");
            int[] expectedVisitations = new int[] { 0, 3, 4 };

            VMTester.testVisitation(CLASS_NAME, "getWithCatch()V", initial, expectedVisitations);
        }

        @Test
        public void canGetWithShortIndex() {
            Short index = 0;
            initial.setRegisters(0, new int[] { 0x42 }, "[I", 1, index, "S");
            expected.setRegisters(index.intValue(), 0x42, "S");

            VMTester.test(CLASS_NAME, "get()V", initial, expected);
        }

        @Before
        public void setUp() {
            initial = new VMState();
            expected = new VMState();
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARRAY_REGISTER = 1;
        private static final int INDEX_REGISTER = 2;
        private static final int VALUE_REGISTER = 0;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private BuilderInstruction instruction;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private AGetOp op;
        private AGetOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void nullArrayValueThrowsNullPointerExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = null;
            int indexValue = 2;

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.AGET);

            op = (AGetOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(NullPointerException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Test
        public void outOfBoundsIndexThrowsArrayIndexOutOfBoundsExceptionAndHasNoChildrenAndAssignsNoRegisters() {
            int[] arrayValue = new int[] { 5 };
            int indexValue = 2;

            VMTester.setRegisterMock(mState, ARRAY_REGISTER, arrayValue, "[I");
            VMTester.setRegisterMock(mState, INDEX_REGISTER, indexValue, "I");

            when(instruction.getOpcode()).thenReturn(Opcode.AGET);

            op = (AGetOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ArrayIndexOutOfBoundsException.class);
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);
            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);

            location = mock(MethodLocation.class);
            instruction = mock(BuilderInstruction.class, withSettings().extraInterfaces(Instruction23x.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(location.getCodeAddress()).thenReturn(ADDRESS);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(((Instruction23x) instruction).getRegisterA()).thenReturn(VALUE_REGISTER);
            when(((Instruction23x) instruction).getRegisterB()).thenReturn(ARRAY_REGISTER);
            when(((Instruction23x) instruction).getRegisterC()).thenReturn(INDEX_REGISTER);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new AGetOpFactory();
        }
    }

}
