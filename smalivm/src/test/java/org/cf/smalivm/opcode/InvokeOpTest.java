package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.builder.instruction.BuilderInstruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(Enclosed.class)
public class InvokeOpTest {

    public static class InvokeReflected {

        private static final String CLASS_NAME = "Linvoke_reflected_test;";

        private VMState expected;
        private VMState initial;

        @Test
        public void intValueCorrectlyCoaxedToBoolean() {
            initial.setRegisters(0, new LinkedList<Boolean>(), "Ljava/util/LinkedList;", 1, 1, "I", 2, 0, "I");
            expected.setRegisters(0, 1, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToCharacter() {
            initial.setRegisters(0, new LinkedList<Character>(), "Ljava/util/LinkedList;", 1, (int) 'a', "I", 2, 0,
                    "I");
            expected.setRegisters(0, (int) 'a', "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToNull() {
            // If Dalvik sees an I type with a value of 0 as an argument for an object parameter class, it's null
            // There is no check-cast in addToListAndGet so result value is either null or an integer
            initial.setRegisters(0, new LinkedList<Integer>(), "Ljava/util/LinkedList;", 1, 0, "I", 2, 0, "I");
            expected.setRegisters(0, null, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyCoaxedToShort() {
            initial.setRegisters(0, new LinkedList<Short>(), "Ljava/util/LinkedList;", 1, 5, "I", 2, 0, "I");
            expected.setRegisters(0, 5, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "addToListAndGet()V", initial, expected);
        }

        @Test
        public void intValueCorrectlyNotCoaxedToNull() {
            initial.setRegisters(0, new LinkedList<Integer>(), "Ljava/util/LinkedList;", 1, 5, "Ljava/lang/Integer;", 2,
                    0, "I");
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
            VirtualMachine vm = VMTester.spawnVM(true);
            VirtualClass instanceType = vm.getClassManager().getVirtualClass("Ljava/lang/String;");
            initial.setRegisters(0, new UninitializedInstance(instanceType), "Ljava/lang/String;", 1,
                    new UnknownValue(), "[B");
            expected.setRegisters(0, new UnknownValue(), "Ljava/lang/String;", 1, new UnknownValue(), "[B");

            VMTester.test(CLASS_NAME, "initStringWithByteArray()V", initial, expected);
        }

        @Test
        public void testInvokeLongValueOfWithLongGetsCorrectParameters() {
            initial.setRegisters(0, 0x1000L, "J");
            expected.setRegisters(0, 0x1000L, "J");

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
        public void testInvokeMutateStaticClassFieldNonDeterministicallyPropagatesUnknown() {
            initial.setFields(CLASS_NAME, "mutable:[I", new int[] { 3, 3, 3 });
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setFields(CLASS_NAME, "mutable:[I", new UnknownValue());

            VMTester.test(CLASS_NAME, "invokeMutateStaticClassField2()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStaticClassFieldPropagatesChanges() {
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
        public void testInvokeReturnUninitializedFieldReturnsNull() {
            expected.setRegisters(MethodState.ResultRegister, null, "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeReturnUninitializedField()V", expected);
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V");
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull("Consensus should be null", consensus);
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
        public void testPossiblyUninitializedClassHasUnknownFieldValues() {
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", new UnknownValue());

            VMTester.test(CLASS_NAME, "initializeClassIfArgumentIsZero(I)V", initial, expected);
        }

        @Test
        public void alwaysThrownExceptionIsBubbledUp() {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeMethodWhichThrowsNullPointerException()V");

            HeapItem item = graph.getTerminatingRegisterConsensus(0);
            Class<?> exceptionClass = NullPointerException.class;
            Assert.assertEquals(exceptionClass, item.getValue().getClass());
            Assert.assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());

            int[] expectedAddresses = new int[] { 0, 4, 5 };
            VMTester.testVisitation(graph, expectedAddresses);

            HeapItem throwItem = graph.getTerminatingRegisterConsensus(MethodState.ThrowRegister);
            assertEquals(item, throwItem);
        }

        @Test
        public void sometimesThrownExceptionExecutesExceptionalAndNormalExecutionPaths() {
            initial.setRegisters(0, new UnknownValue(), "I");
            ExecutionGraph graph =
                    VMTester.execute(CLASS_NAME, "invokeMethodWhichMayThrowNullPointerException()V", initial);

            int[] expectedAddresses = new int[] { 0, 3, 4, 5 };
            VMTester.testVisitation(graph, expectedAddresses);

            String exceptionClass = "Ljava/lang/NullPointerException;";
            // Unknown type for register 0 because could be I or exceptionClass
            expected.setRegisters(MethodState.ThrowRegister, new UnknownValue(), exceptionClass, 0, new UnknownValue(),
                    "?");
            VMTester.testState(graph, expected);
        }
    }

    public static class InvokeVirtual {

        private static final String CLASS_NAME = "Linvoke_virtual_test;";
        private static final String CLASS_NAME_BINARY = "invoke_virtual_test";

        private VMState expected;
        private VMState initial;

        @Test
        public void instanceInitializerWorksAsExpected() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM(true);
            VirtualClass instanceType = vm.getClassManager().getVirtualClass(CLASS_NAME);
            initial.setRegisters(0, new UninitializedInstance(instanceType), CLASS_NAME);
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "<init>()V", initial);

            // Can't simply put instance in the expected state because mutable objects get cloned.
            // The object parse at consensus would be different than the initial parse.
            HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
            assertEquals(CLASS_NAME, consensus.getType());
            assertEquals(virtualClass, consensus.getValue().getClass());
        }

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testInvokeGetComponentTypeOnPrimitiveArrayReturnsExpectedValue() {
            initial.setRegisters(0, new int[0], "[I");
            expected.setRegisters(MethodState.ResultRegister, int.class, CommonTypes.CLASS);

            VMTester.test(CLASS_NAME, "invokeGetComponentType()V", initial, expected);
        }

        @Test
        public void testInvokeReturnIntReturnsInt() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME);
            expected.setRegisters(MethodState.ResultRegister, 0x7, "I");

            VMTester.test(CLASS_NAME, "invokeReturnInt()V", initial, expected);
        }

        @Test
        public void testInvokeReturnParameterReturnsParameter() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME, 1, 0x5, "I");
            expected.setRegisters(1, 0x5, "I", MethodState.ResultRegister, 0x5, "I");

            ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "invokeReturnParameter()V", initial);
            VMTester.testState(graph, expected);

            // Can't simply put instance in the expected state because mutable objects get cloned.
            // The object class at consensus would be different than the initial class.
            HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
            assertEquals(CLASS_NAME, consensus.getType());
            assertEquals(virtualClass.getName(), consensus.getValue().getClass().getName());
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM(true);
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME);
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V", initial);
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull(consensus);
        }

        @Test
        public void testInvokeVirtualManyParametersThrowsNoExceptions() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME, 1, 0x100L, "J", 3, 0x200L, "J", 5, 0x300L, "J", 7, 0x3, "I");
            expected.setRegisters(MethodState.ResultRegister, 0x3, "I");

            VMTester.test(CLASS_NAME, "invokeRangeManyParameters()V", initial, expected);
        }
    }

    public static class MethodStateProperties {

        private static final String CLASS_NAME = "Linvoke_static_test;";

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        private VMState initial;

        @Before
        public void setUp() {
            initial = new VMState();
        }

        //WithTwoArrayParametersWithUnknownParameterMutatesAllParameters
        @Test
        public void testInvokeNonExistentMethodThrowsException() {
            thrown.expect(RuntimeException.class);
            thrown.expectMessage("Can't find Smali file containing Lim_not_your_friend_buddy;");

            VMTester.execute(CLASS_NAME, "invokeNonExistentMethod()V");
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
        private static final String[] METHOD_PARAM_TYPE_NAMES = { "I" };

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
            when(vm.getClassManager()).thenReturn(classManager);

            VirtualClass methodClass = mock(VirtualClass.class);
            when(classManager.getVirtualClass(eq(METHOD_CLASS))).thenReturn(methodClass);
            when(classManager.isFrameworkClass(methodClass)).thenReturn(false);
            when(classManager.isSafeFrameworkClass(methodClass)).thenReturn(false);

            VirtualMethod method = mock(VirtualMethod.class);
            when(method.getName()).thenReturn(METHOD_SIGNATURE);
            when(method.toString()).thenCallRealMethod();
            String descriptor = METHOD_SIGNATURE.split("->")[1];
            when(methodClass.getMethod(eq(descriptor))).thenReturn(method);
            when(method.getSignature()).thenReturn(METHOD_SIGNATURE);
            List<String> params = new LinkedList<String>();
            Collections.addAll(params, METHOD_PARAM_TYPE_NAMES);
            when(method.getParameterTypeNames()).thenReturn(params);
            configuration = mock(Configuration.class);
            when(vm.getConfiguration()).thenReturn(configuration);

            node = mock(ExecutionNode.class);
            ectx = mock(ExecutionContext.class);
            when(node.getContext()).thenReturn(ectx);

            mState = mock(MethodState.class);
            when(ectx.getMethodState()).thenReturn(mState);

            location = mock(MethodLocation.class);
            when(location.getCodeAddress()).thenReturn(ADDRESS);

            addressToLocation = new TIntObjectHashMap<MethodLocation>();
            addressToLocation.put(ADDRESS, location);

            methodRef = mock(MethodReference.class);
            when(methodRef.getDefiningClass()).thenReturn(METHOD_CLASS);
            when(methodRef.getName()).thenReturn(METHOD_NAME);
            doReturn(params).when(methodRef).getParameterTypes();
            when(methodRef.getReturnType()).thenReturn(METHOD_RETURN);

            opFactory = new InvokeOpFactory();
        }

        private BuilderInstruction35c buildInstruction35c(Opcode opcode) {
            BuilderInstruction35c instruction =
                    mock(BuilderInstruction35c.class, withSettings().extraInterfaces(Instruction35c.class));
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
