package org.cf.smalivm.opcode;

import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
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

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int DEST_REGISTER = 2;
        private static final int SIZE_REGISTER = 4;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private NewArrayOp op;
        private NewArrayOpFactory opFactory;
        private VirtualMachine vm;
        private Configuration configuration;

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

        private void setupInstruction(BuilderInstruction instruction, Opcode opcode) {
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
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

    }

    public static class IntegrationTest {

        private static final String CLASS_NAME = "Lnew_array_test;";

        @Test
        public void TestNewInteger1DArray() {
            int length = 1;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length], "[I");

            VMTester.testMethodState(CLASS_NAME, "NewIntegerArray()V", initial, expected);
        }

        @Test
        public void TestNewInteger2DArray() {
            int length = 3;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length][], "[[I");

            VMTester.testMethodState(CLASS_NAME, "NewIntegerArrayArray()V", initial, expected);
        }

        @Test
        public void TestNewIntegerArrayUnkonwnLength() {
            Object length = new UnknownValue();
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, length, "[I");

            VMTester.testMethodState(CLASS_NAME, "NewIntegerArray()V", initial, expected);
        }

        @Test
        public void TestNewIntegerArrayWithShortDimension() {
            Short length = 1;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "S");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length.intValue()], "[I");

            VMTester.testMethodState(CLASS_NAME, "NewIntegerArray()V", initial, expected);
        }

        @Test
        public void TestNewLocalClass1DArray() {
            int length = 1;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
            LocalInstance[] instances = new LocalInstance[length];
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[" + CLASS_NAME);

            VMTester.testMethodState(CLASS_NAME, "NewLocalClassArray()V", initial, expected);
        }

        @Test
        public void TestNewLocalClass2DArray() {
            int length = 5;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
            LocalInstance[][] instances = new LocalInstance[length][];
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[[" + CLASS_NAME);

            VMTester.testMethodState(CLASS_NAME, "NewLocalClassArrayArray()V", initial, expected);
        }

    }

}
