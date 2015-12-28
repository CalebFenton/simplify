package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.HashMap;
import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ConstOpTest {

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private ConstOp op;
        private ConstOpFactory opFactory;
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

            opFactory = new ConstOpFactory();
        }

        private void setupInstruction(BuilderInstruction instruction, Opcode opcode) {
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
        }

        private BuilderInstruction21c buildInstruction21c(Opcode opcode, String type) {
            BuilderInstruction21c instruction = mock(BuilderInstruction21c.class);
            setupInstruction(instruction, opcode);

            when(((Instruction21c) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);
            TypeReference reference = mock(TypeReference.class);
            when(reference.getType()).thenReturn(type);
            when(instruction.getReference()).thenReturn(reference);

            return instruction;
        }

        @Test
        public void canConstClass() throws ClassNotFoundException {
            String type = "Ljava/lang/String;";
            buildInstruction21c(Opcode.CONST_CLASS, type);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            Class<?> expected = String.class;
            verify(mState, times(1)).assignRegister(eq(ARG1_REGISTER), eq(expected), eq("Ljava/lang/Class;"));
        }

        @Test
        public void nonExistantClassNameThrowsException() throws ClassNotFoundException {
            String type = "Ldoes/not/123Exist;";
            buildInstruction21c(Opcode.CONST_CLASS, type);
            op = opFactory.create(location, addressToLocation, vm);
            op.execute(node, mState);

            // TODO: ClassUtils.getClass returns exceptions with "/", but Class.forName uses "."
            // should make object which handles getting classes for dependency injection
            VirtualException expectedException = new VirtualException(ClassNotFoundException.class, "does/not/123Exist");
            VMTester.verifyExceptionHandling(expectedException, node, mState);
        }

    }

    public static class IntegrationTest {

        private static final String CLASS_NAME = "Lconst_test;";

        @Test
        public void testConst() throws NoSuchFieldException, SecurityException {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x42424242, "D");

            VMTester.testMethodState(CLASS_NAME, "Const()V", expected);
        }

        @Test
        public void testConst16() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x4242, "I");

            VMTester.testMethodState(CLASS_NAME, "Const16()V", expected);
        }

        @Test
        public void testConst4() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -3, "I");

            VMTester.testMethodState(CLASS_NAME, "Const4()V", expected);
        }

        @Test
        public void testConstClassLocal() throws ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> expectedClass = vm.getClassLoader().loadClass(ClassNameUtils.internalToBinary(CLASS_NAME));
            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "ConstClassLocal()V",
                            new TIntObjectHashMap<HeapItem>(), new HashMap<String, Map<String, HeapItem>>());
            HeapItem item = graph.getRegisterConsensus(0, 0);
            assertEquals("Ljava/lang/Class;", item.getType());
            assertEquals(expectedClass, item.getValue());
        }

        @Test
        public void testConstClassRemote() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, Object.class, "Ljava/lang/Class;");

            VMTester.testMethodState(CLASS_NAME, "ConstClassRemote()V", expected);
        }

        @Test
        public void testConstHigh16() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x42420000, "I");

            VMTester.testMethodState(CLASS_NAME, "ConstHigh16()V", expected);
        }

        @Test
        public void testConstString() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0,
                            "When I need to identify rebels, I look for men with principles.", "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "ConstString()V", expected);
        }

        @Test
        public void testConstStringJumbo() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0,
                            "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
                            "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "ConstStringJumbo()V", expected);
        }

        @Test
        public void testConstWide() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x4242424242424242L, "J");

            VMTester.testMethodState(CLASS_NAME, "ConstWide()V", expected);
        }

        @Test
        public void testConstWide16() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x4242L, "J");

            VMTester.testMethodState(CLASS_NAME, "ConstWide16()V", expected);
        }

        @Test
        public void testConstWide32() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x1b7740L, "J");

            VMTester.testMethodState(CLASS_NAME, "ConstWide32()V", expected);
        }

        @Test
        public void testConstWideHigh16() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 0x4242000000000000L, "J");

            VMTester.testMethodState(CLASS_NAME, "ConstWideHigh16()V", expected);
        }

        @Test
        public void testNegativeConst16() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, -0xf, "I");

            VMTester.testMethodState(CLASS_NAME, "NegativeConst16()V", expected);
        }

    }

}
