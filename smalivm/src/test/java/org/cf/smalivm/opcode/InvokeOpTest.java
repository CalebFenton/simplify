package org.cf.smalivm.opcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class InvokeOpTest {

    public static class InvokeReflected {

        private static final String CLASS_NAME = "Linvoke_reflected_test;";

        private VMState expected;
        private VMState initial;

        @Test
        public void intValueCorrectlyCoaxedToBoolean() {
            initial.setRegisters(0, new LinkedList<Boolean>(), "Ljava/lang/LinkedList;", 1, 1, "I", 2, 0, "I");
            expected.setRegisters(0, 1, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToCharacter() {
            initial.setRegisters(0, new LinkedList<Character>(), "Ljava/lang/LinkedList;", 1, (int) 'a', "I", 2, 0, "I");
            expected.setRegisters(0, (int) 'a', "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToNull() {
            // If Dalvik sees an I type with a value of 0 as an argument for an object parameter type, it's null
            // There is no check-cast in addToListAndGet so result value is either null or an integer
            initial.setRegisters(0, new LinkedList<Integer>(), "Ljava/lang/LinkedList;", 1, 0, "I", 2, 0, "I");
            expected.setRegisters(0, null, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToShort() {
            initial.setRegisters(0, new LinkedList<Short>(), "Ljava/lang/LinkedList;", 1, 5, "I", 2, 0, "I");
            expected.setRegisters(0, 5, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyNotCoaxedToNull() {
            initial.setRegisters(0, new LinkedList<Integer>(), "Ljava/lang/LinkedList;", 1, 5, "Ljava/lang/Integer;",
                            2, 0, "I");
            expected.setRegisters(0, 5, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueWhichLooksLikeNullNotCoaxedToNullWhenUsedAsPrimitive() {
            initial.setRegisters(0, 0, "I");
            expected.setRegisters(MethodState.ResultRegister, Boolean.FALSE, "Ljava/lang/Boolean;");

            VMTester.test(CLASS_NAME, "invokeBooleanValueOf()V", initial, expected);
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testInitStringWithByteArrayWithUnknownParameter() {
            initial.setRegisters(0, new UninitializedInstance("Ljava/lang/String;"), "Ljava/lang/String;", 1,
                            new UnknownValue(), "[B");
            expected.setRegisters(0, new UnknownValue(), "Ljava/lang/String;", 1, new UnknownValue(), "[B");

            VMTester.test(CLASS_NAME, "initStringWithByteArray()V", initial, expected);
        }

        @Test
        public void testInvokeLongValueOfWithLongGetsCorrectParameters() {
            initial.setRegisters(0, 0x1000L, "J");
            expected.setRegisters(0, new Long(0x1000L), "J");

            VMTester.test(CLASS_NAME, "invokeLongValueOf()V", initial, expected);
        }

        @Test
        public void testInvokeStringBuilderAppendWithLong() {
            long value = 0x1234L;
            initial.setRegisters(0, new StringBuilder(), "Ljava/lang/StringBuilder;", 1, value, "J");
            StringBuilder sb = new StringBuilder().append(value);
            expected.setRegisters(0, sb, "Ljava/lang/StringBuilder;", 1, value, "J");

            VMTester.test(CLASS_NAME, "invokeStringBuilderAppendWithLong()V", initial, expected);
        }
    }

    public static class InvokeStatic {

        private static final String CLASS_NAME = "Linvoke_static_test;";
        private static final String CLASS_WITH_STATIC_INIT = "Lclass_with_static_init;";

        private VMState expected;
        private VMState initial;

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testInvokeMethodOutsideClassThatAccessesThisClassReturnsExpectedValue() {
            String value = "i have been initialized";
            initial.setFields(CLASS_NAME, "sometimes_initialized:Ljava/lang/String;", value);
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", "Uhhh, about 11, sir.");
            expected.setRegisters(MethodState.ResultRegister, value, "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeMethodOutsideClassThatAccessesThisClass()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStaticClassFieldNonDeterministicallyPropigatesUnknown() {
            initial.setFields(CLASS_NAME, "mutable:[I", new int[] { 3, 3, 3 });
            expected.setFields(CLASS_NAME, "mutable:[I", new UnknownValue());

            VMTester.test(CLASS_NAME, "invokeMutateStaticClassFieldNonDeterministically()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStaticClassFieldPropigatesChanges() {
            initial.setFields(CLASS_NAME, "mutable:[I", new int[] { 3, 3, 3 });
            expected.setFields(CLASS_NAME, "mutable:[I", new int[] { 0, 3, 3 });

            VMTester.test(CLASS_NAME, "invokeMutateStaticClassField()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStringBuilderDoesMutateParameter() {
            initial.setRegisters(0, new StringBuilder("i have been"), "Ljava/lang/StringBuilder;");
            expected.setRegisters(0, new StringBuilder("i have been mutated"), "Ljava/lang/StringBuilder;");

            VMTester.test(CLASS_NAME, "invokeMutateStringBuilder()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStringDoesNotMutateParameter() {
            initial.setRegisters(0, "not mutated", "Ljava/lang/String;");
            expected.setRegisters(0, "not mutated", "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeMutateString()V", initial, expected);
        }

        @Test
        public void testInvokeNonExistantMethodWithTwoArrayParametersWithUnknownParameterMutatesAllParameters() {
            initial.setRegisters(0, new int[] { 3, 5, 7 }, "[I", 1, new UnknownValue(), "[I");
            expected.setRegisters(0, new UnknownValue(), "[I", 1, new UnknownValue(), "[I");

            VMTester.test(CLASS_NAME, "invokeNonExistantMethodWithTwoArrayParameters()V", initial, expected);
        }

        @Test
        public void testInvokeReturnIntReturnsInt() {
            expected.setRegisters(MethodState.ResultRegister, 0x7, "I");

            VMTester.test(CLASS_NAME, "invokeReturnInt()V", expected);
        }

        @Test
        public void testInvokeReturnParameterReturnsParameter() {
            initial.setRegisters(0, 0x5, "I");
            expected.setRegisters(MethodState.ResultRegister, 0x5, "I");

            VMTester.test(CLASS_NAME, "invokeReturnParameter()V", initial, expected);
        }

        @Test
        public void testInvokeReturnUninitializedFieldDoesNotReturnNull() {
            expected.setRegisters(MethodState.ResultRegister, new UnknownValue(), "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeReturnUninitializedField()V", expected);
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V");
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull(consensus);
        }

        @Test
        public void testKnownMutableParametersAreMutatedWithDeterministicExecution() {
            initial.setRegisters(0, new int[] { 0x5 }, "[I", 1, 0, "I");
            expected.setRegisters(0, new int[] { 0x0 }, "[I", 1, 0, "I");

            VMTester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }

        @Test
        public void testKnownMutableParametersAreMutatedWithNonDeterministicExecution() {
            initial.setRegisters(0, new int[] { 0x5 }, "[I", 1, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "[I", 1, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }

        @Test
        public void testNonDeterministicallyInitializedClassHasUnknownFieldValues() {
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", new UnknownValue());

            VMTester.test(CLASS_NAME, "nonDeterministicallyInitializeClassWithStaticInit()V", expected);
        }
    }

    public static class InvokeVirtual {

        private static final String CLASS_NAME = "Linvoke_virtual_test;";
        private static final String CLASS_NAME_BINARY = "invoke_virtual_test";

        private VMState expected;
        private VMState initial;

        @Test
        public void instanceInitializerWorksAsExpected() throws InstantiationException, IllegalAccessException,
                        ClassNotFoundException {
            initial.setRegisters(0, new UninitializedInstance(CLASS_NAME), CLASS_NAME);
            VirtualMachine vm = VMTester.spawnVM(true);
            Class<?> localClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "<init>()V", initial);

            // Can't simply put localInstance in the expected state because mutable objects get cloned.
            // The object reference at consensus would be different than the initial reference.
            HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
            assertEquals(CLASS_NAME, consensus.getType());
            assertEquals(localClass, consensus.getValue().getClass());
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testInvokeGetComponentTypeOnPrimitiveArrayReturnsExpectedValue() {
            initial.setRegisters(0, new int[0], "[I");
            expected.setRegisters(MethodState.ResultRegister, int.class, "Ljava/lang/Class;");

            VMTester.test(CLASS_NAME, "invokeGetComponentType()V", initial, expected);
        }

        @Test
        public void testInvokeReturnIntReturnsInt() throws InstantiationException, IllegalAccessException,
                        ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> localClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object localInstance = localClass.newInstance();
            initial.setRegisters(0, localInstance, CLASS_NAME);
            expected.setRegisters(MethodState.ResultRegister, 0x7, "I");

            VMTester.test(CLASS_NAME, "invokeReturnInt()V", initial, expected);
        }

        @Test
        public void testInvokeReturnParameterReturnsParameter() throws InstantiationException, IllegalAccessException,
                        ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> localClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object localInstance = localClass.newInstance();
            initial.setRegisters(0, localInstance, CLASS_NAME, 1, 0x5, "I");
            expected.setRegisters(1, 0x5, "I", MethodState.ResultRegister, 0x5, "I");

            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "invokeReturnParameter()V", initial);
            VMTester.testState(graph, expected);

            // Can't simply put localInstance in the expected state because mutable objects get cloned.
            // The object reference at consensus would be different than the initial reference.
            HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
            assertEquals(CLASS_NAME, consensus.getType());
            assertEquals(localClass, consensus.getValue().getClass());
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() throws InstantiationException, IllegalAccessException,
                        ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM(true);
            Class<?> localClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object localInstance = localClass.newInstance();
            initial.setRegisters(0, localInstance, CLASS_NAME);
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V", initial);
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull(consensus);
        }

        @Test
        public void testInvokeVirtualManyParametersThrowsNoExceptions() throws InstantiationException,
                        IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> localClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object localInstance = localClass.newInstance();
            initial.setRegisters(0, localInstance, CLASS_NAME, 1, 0x100L, "J", 3, 0x200L, "J", 5, 0x300L, "J", 7, 0x3,
                            "I");
            expected.setRegisters(MethodState.ResultRegister, 0x3, "I");

            VMTester.test(CLASS_NAME, "invokeRangeManyParameters()V", initial, expected);
        }
    }

    public static class MethodStateProperties {

        private static final String CLASS_NAME = "Linvoke_static_test;";

        private VMState initial;

        @Before
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testInvokeNonExistantMethodWithTwoArrayParametersWithUnknownParameterMutatesAllParameters() {
            initial.setRegisters(0, new int[] { 0 }, "[I", 1, new int[] { 0 }, "[I");
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeNonExistantMethodWithTwoArrayParameters()V",
                            initial);
            List<ExecutionNode> invokeNodes = graph.getNodePile(0);
            assertEquals(1, invokeNodes.size());

            MethodState mState = invokeNodes.get(0).getContext().getMethodState();
            assertTrue(mState.wasRegisterRead(0));
            assertTrue(mState.wasRegisterRead(1));
            assertTrue(mState.wasRegisterAssigned(0));
            assertTrue(mState.wasRegisterAssigned(1));
        }

    }

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;
        private static final int ARG2_REGISTER = 4;

        private static final String METHOD_CLASS = "Lsome/class;";
        private static final String METHOD_NAME = "someMethod";
        private static final String METHOD_RETURN = "V";
        private static final String METHOD_SIGNATURE = METHOD_CLASS + "->" + METHOD_NAME + "(I)" + METHOD_RETURN;
        private static final String[] METHOD_PARAMS = { "I" };

        MethodReference methodRef;
        private TIntObjectMap<MethodLocation> addressToLocation;
        private ClassManager classManager;
        private ExecutionContext ectx;
        private BuilderInstruction instruction;
        private Configuration configuration;
        private MethodLocation location;
        private MethodState mState;
        private ExecutionNode node;
        private InvokeOp op;
        private InvokeOpFactory opFactory;
        private VirtualMachine vm;

        @Test
        public void hasExpectedToString() {
            int value = 0;
            VMTester.setRegisterMock(mState, ARG1_REGISTER, value, "I");
            VMTester.setRegisterMock(mState, ARG2_REGISTER, value, "I");

            instruction = buildInstruction35c(Opcode.INVOKE_STATIC);
            op = (InvokeOp) opFactory.create(location, addressToLocation, vm);

            String expected = "invoke-static {r" + ARG1_REGISTER + "}, " + METHOD_SIGNATURE;
            assertEquals(expected, op.toString());
        }

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);

            classManager = mock(ClassManager.class);
            when(classManager.isLocalClass(METHOD_CLASS)).thenReturn(true);
            when(classManager.isFrameworkClass(METHOD_SIGNATURE)).thenReturn(false);
            when(classManager.isSafeFrameworkClass(METHOD_SIGNATURE)).thenReturn(false);
            when(vm.getClassManager()).thenReturn(classManager);

            configuration = mock(Configuration.class);
            when(vm.getConfiguration()).thenReturn(configuration);

            ectx = mock(ExecutionContext.class);
            mState = mock(MethodState.class);
            when(ectx.getMethodState()).thenReturn(mState);
            node = mock(ExecutionNode.class);
            when(node.getContext()).thenReturn(ectx);
            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            methodRef = mock(MethodReference.class);
            when(methodRef.getDefiningClass()).thenReturn(METHOD_CLASS);
            when(methodRef.getName()).thenReturn(METHOD_NAME);
            List<String> params = new LinkedList<String>();
            for (String param : METHOD_PARAMS) {
                params.add(param);
            }
            doReturn(params).when(methodRef).getParameterTypes();
            when(methodRef.getReturnType()).thenReturn(METHOD_RETURN);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            opFactory = new InvokeOpFactory();
        }

        private BuilderInstruction35c buildInstruction35c(Opcode opcode) {
            BuilderInstruction35c instruction = mock(BuilderInstruction35c.class,
                            withSettings().extraInterfaces(Instruction35c.class));
            when(location.getInstruction()).thenReturn(instruction);
            when(instruction.getLocation()).thenReturn(location);
            when(instruction.getCodeUnits()).thenReturn(0);
            when(instruction.getOpcode()).thenReturn(opcode);
            when(instruction.getRegisterC()).thenReturn(ARG1_REGISTER);
            when(instruction.getRegisterD()).thenReturn(ARG2_REGISTER);
            when(instruction.getRegisterCount()).thenReturn(2);
            when(((Instruction35c) instruction).getReference()).thenReturn(methodRef);

            return instruction;
        }
    }

}
