package org.cf.smalivm.opcode;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class NewArrayOpTest {

    public static class IntegrationTest {

        private static final String CLASS_NAME = "Lnew_array_test;";

        private VMState expected;
        private VMState initial;

        @Test
        public void canCreate2DIntegerArray() {
            int length = 3;
            initial.setRegisters(0, length, "I");
            expected.setRegisters(0, new int[length][], "[[I");

            VMTester.test(CLASS_NAME, "create2DIntegerArray()V", initial, expected);
        }

        @Test
        public void canCreate2DLocalInstanceArray() {
            int length = 5;
            initial.setRegisters(0, length, "I");
            LocalInstance[][] instances = new LocalInstance[length][];
            expected.setRegisters(0, instances, "[[" + CLASS_NAME);

            VMTester.test(CLASS_NAME, "create2DLocalInstanceArray()V", initial, expected);
        }

        @Test
        public void canCreateIntegerArray() {
            int length = 1;
            initial.setRegisters(0, length, "I");
            expected.setRegisters(0, new int[length], "[I");

            VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
        }

        @Test
        public void canCreateIntegerArrayWithShortTypeLengthValue() {
            Short length = 1;
            initial.setRegisters(0, length, "S");
            expected.setRegisters(0, new int[length.intValue()], "[I");

            VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
        }

        @Test
        public void canCreateIntegerArrayWithUnkonwnLengthValue() {
            Object length = new UnknownValue();
            initial.setRegisters(0, length, "I");
            expected.setRegisters(0, length, "[I");

            VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
        }

        @Test
        public void canCreateLocalInstanceArray() {
            int length = 1;
            initial.setRegisters(0, length, "I");
            LocalInstance[] instances = new LocalInstance[length];
            expected.setRegisters(0, instances, "[" + CLASS_NAME);

            VMTester.test(CLASS_NAME, "createLocalInstanceArray()V", initial, expected);
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int DEST_REGISTER = 2;
        private static final int SIZE_REGISTER = 4;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private Configuration configuration;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private NewArrayOp op;
        private NewArrayOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void canCreateNewArray() throws ClassNotFoundException {
            int size = 5;
            VMTester.addHeapItem(mState, SIZE_REGISTER, size, "I");

            String type = "[Ljava/lang/String;";
            buildInstruction22c(Opcode.NEW_ARRAY, type);

            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            String[] expected = new String[size];
            verify(mState, times(1)).assignRegister(eq(DEST_REGISTER), aryEq(expected), eq(type));
        }

        @Test
        public void nonExistantClassNameThrowsException() throws ClassNotFoundException {
            int size = 5;
            VMTester.addHeapItem(mState, SIZE_REGISTER, size, "I");

            String type = "Ldoes/not/123Exist;";
            buildInstruction22c(Opcode.NEW_ARRAY, type);

            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            VirtualException expectedException = new VirtualException(ClassNotFoundException.class, "does.not.123Exist");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);

            configuration = mock(Configuration.class);
            when(vm.getConfiguration()).thenReturn(configuration);

            mState = mock(MethodState.class);
            node = mock(ExecutionNode.class);
            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new NewArrayOpFactory();
        }

        private BuilderInstruction22c buildInstruction22c(Opcode opcode, String type) {
            BuilderInstruction22c instruction = mock(BuilderInstruction22c.class);
            setupInstruction(instruction, opcode);

            when(((Instruction22c) instruction).getRegisterA()).thenReturn(DEST_REGISTER);
            when(((Instruction22c) instruction).getRegisterB()).thenReturn(SIZE_REGISTER);
            TypeReference reference = mock(TypeReference.class);
            when(reference.getType()).thenReturn(type);
            when(instruction.getReference()).thenReturn(reference);

            return instruction;
        }

        private void setupInstruction(BuilderInstruction instruction, Opcode opcode) {
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
        }

    }

}
