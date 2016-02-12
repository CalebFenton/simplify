package org.cf.smalivm.opcode;

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
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
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

    public static class IntegrationTest {

        private static final String CLASS_NAME = "Lconst_test;";

        private VMState expected;

        @Test
        public void canConst() throws NoSuchFieldException, SecurityException {
            expected.setRegisters(0, 0x42424242, "D");

            VMTester.test(CLASS_NAME, "const()V", expected);
        }

        @Test
        public void canConst16() {
            expected.setRegisters(0, 0x4242, "I");

            VMTester.test(CLASS_NAME, "const16()V", expected);
        }

        @Test
        public void canConst4() {
            expected.setRegisters(0, -3, "I");

            VMTester.test(CLASS_NAME, "const4()V", expected);
        }

        @Test
        public void canConstClassLocal() throws ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> expectedClass = vm.getClassLoader().loadClass(ClassNameUtils.internalToBinary(CLASS_NAME));
            expected.setRegisters(0, expectedClass, "Ljava/lang/Class;");
            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "constClassLocal()V");

            VMTester.testState(graph, expected);
        }

        @Test
        public void canConstClassRemote() {
            expected.setRegisters(0, Object.class, "Ljava/lang/Class;");

            VMTester.test(CLASS_NAME, "constClassRemote()V", expected);
        }

        @Test
        public void canConstHigh16() {
            expected.setRegisters(0, 0x42420000, "I");

            VMTester.test(CLASS_NAME, "constHigh16()V", expected);
        }

        @Test
        public void canConstString() {
            expected.setRegisters(0, "When I need to identify rebels, I look for men with principles.",
                            "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "constString()V", expected);
        }

        @Test
        public void canConstStringJumbo() {
            expected.setRegisters(0,
                            "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles.",
                            "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "constStringJumbo()V", expected);
        }

        @Test
        public void canConstWide() {
            expected.setRegisters(0, 0x4242424242424242L, "J");

            VMTester.test(CLASS_NAME, "constWide()V", expected);
        }

        @Test
        public void canConstWide16() {
            expected.setRegisters(0, 0x4242L, "J");

            VMTester.test(CLASS_NAME, "constWide16()V", expected);
        }

        @Test
        public void canConstWide32() {
            expected.setRegisters(0, 0x1b7740L, "J");

            VMTester.test(CLASS_NAME, "constWide32()V", expected);
        }

        @Test
        public void canConstWideHigh16() {
            expected.setRegisters(0, 0x4242000000000000L, "J");

            VMTester.test(CLASS_NAME, "constWideHigh16()V", expected);
        }

        @Test
        public void canNegativeConst16() {
            expected.setRegisters(0, -0xf, "I");

            VMTester.test(CLASS_NAME, "const16Negative()V", expected);
        }

        @Before
        public void setUp() {
            expected = new VMState();
        }

    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;

        private TIntObjectMap<MethodLocation> addressToLocation;
        private Configuration configuration;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private ConstOp op;
        private ConstOpFactory opFactory;
        private VirtualMachine vm;

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

        private BuilderInstruction21c buildInstruction21c(Opcode opcode, String type) {
            BuilderInstruction21c instruction = mock(BuilderInstruction21c.class);
            setupInstruction(instruction, opcode);

            when(((Instruction21c) instruction).getRegisterA()).thenReturn(ARG1_REGISTER);
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
