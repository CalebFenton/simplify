package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Enclosed.class)
public class InvokeOpTest {

    public static class Reflected {

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
        public void initStringWithByteArrayWithUnknownParameter() {
            VirtualMachine vm = VMTester.spawnVM(true);
            VirtualClass instanceType = vm.getClassManager().getVirtualClass("Ljava/lang/String;");
            initial.setRegisters(0, new UninitializedInstance(instanceType), "Ljava/lang/String;", 1,
                    new UnknownValue(), "[B");
            expected.setRegisters(0, new UnknownValue(), "Ljava/lang/String;", 1, new UnknownValue(), "[B");

            VMTester.test(CLASS_NAME, "initStringWithByteArray()V", initial, expected);
        }

        @Test
        public void invokeLongValueOfWithLongGetsCorrectParameters() {
            initial.setRegisters(0, 0x1000L, "J");
            expected.setRegisters(0, 0x1000L, "J");

            VMTester.test(CLASS_NAME, "invokeLongValueOf()V", initial, expected);
        }

        @Test
        public void invokeStringBuilderAppendWithLong() {
            long value = 0x1234L;
            initial.setRegisters(0, new StringBuilder(), "Ljava/lang/StringBuilder;", 1, value, "J");
            StringBuilder sb = new StringBuilder().append(value);
            expected.setRegisters(0, sb, "Ljava/lang/StringBuilder;", 1, value, "J");

            VMTester.test(CLASS_NAME, "invokeStringBuilderAppendWithLong()V", initial, expected);
        }
    }

    public static class Static {

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
        public void invokeMethodOutsideClassThatAccessesThisClassReturnsExpectedValue() {
            String value = "i have been initialized";
            initial.setFields(CLASS_NAME, "sometimes_initialized:Ljava/lang/String;", value);
            expected.setFields(CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", "Uhhh, about 11, sir.");
            expected.setRegisters(MethodState.ResultRegister, value, "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeMethodOutsideClassThatAccessesThisClass()V", initial, expected);
        }

        @Test
        public void invokeMutateStaticClassFieldNonDeterministicallyPropagatesUnknown() {
            initial.setFields(CLASS_NAME, "mutable:[I", new int[] { 3, 3, 3 });
            initial.setRegisters(0, new UnknownValue(), "I");
            expected.setFields(CLASS_NAME, "mutable:[I", new UnknownValue());

            VMTester.test(CLASS_NAME, "invokeMutateStaticClassField2()V", initial, expected);
        }

        @Test
        public void invokeMutateStaticClassFieldPropagatesChanges() {
            initial.setFields(CLASS_NAME, "mutable:[I", new int[] { 3, 3, 3 });
            expected.setFields(CLASS_NAME, "mutable:[I", new int[] { 0, 3, 3 });

            VMTester.test(CLASS_NAME, "invokeMutateStaticClassField()V", initial, expected);
        }

        @Test
        public void invokeMutateStringBuilderDoesMutateParameter() {
            initial.setRegisters(0, new StringBuilder("i have been"), "Ljava/lang/StringBuilder;");
            expected.setRegisters(0, new StringBuilder("i have been mutated"), "Ljava/lang/StringBuilder;");

            VMTester.test(CLASS_NAME, "invokeMutateStringBuilder()V", initial, expected);
        }

        @Test
        public void invokeMutateStringDoesNotMutateParameter() {
            initial.setRegisters(0, "not mutated", "Ljava/lang/String;");
            expected.setRegisters(0, "not mutated", "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeMutateString()V", initial, expected);
        }

        @Test
        public void invokeReturnIntReturnsInt() {
            expected.setRegisters(MethodState.ResultRegister, 0x7, "I");

            VMTester.test(CLASS_NAME, "invokeReturnInt()V", expected);
        }

        @Test
        public void invokeReturnParameterReturnsParameter() {
            initial.setRegisters(0, 0x5, "I");
            expected.setRegisters(MethodState.ResultRegister, 0x5, "I");

            VMTester.test(CLASS_NAME, "invokeReturnParameter()V", initial, expected);
        }

        @Test
        public void invokeReturnUninitializedFieldReturnsNull() {
            expected.setRegisters(MethodState.ResultRegister, null, "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "invokeReturnUninitializedField()V", expected);
        }

        @Test
        public void invokeReturnVoidReturnsVoid() {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V");
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull("Consensus should be null", consensus);
        }

        @Test
        public void knownMutableParametersAreMutatedWithDeterministicExecution() {
            initial.setRegisters(0, new int[] { 0x5 }, "[I", 1, 0, "I");
            expected.setRegisters(0, new int[] { 0x0 }, "[I", 1, 0, "I");

            VMTester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                    expected);
        }

        @Test
        public void knownMutableParametersAreMutatedWithNonDeterministicExecution() {
            initial.setRegisters(0, new int[] { 0x5 }, "[I", 1, new UnknownValue(), "I");
            expected.setRegisters(0, new UnknownValue(), "[I", 1, new UnknownValue(), "I");

            VMTester.test(CLASS_NAME, "invokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                    expected);
        }

        @Test
        public void possiblyUninitializedClassHasUnknownFieldValues() {
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

    public static class Virtual {

        private static final String CLASS_NAME = "Linvoke_virtual_test;";
        private static final String CLASS_NAME_BINARY = "invoke_virtual_test";

        private VMState expected;
        private VMState initial;

        @Test
        public void canInvokeArrayCloneMethod() throws Exception {
            int[] array = new int[] { 1, 2, 3 };
            //            System.out.println("orig: " + Arrays.toString(array));
            //            System.out.println("clone: " + Arrays.toString(array.clone()));
            //            Method m = Object[].class.getMethod("clone");
            //            System.out.println("reflect: " + m.invoke(array));

            initial.setRegisters(0, array, "[I");
            expected.setRegisters(0, array, "[I", 1, array.clone(), "[I");

            VMTester.test(CLASS_NAME, "invokeArrayClone()V", initial, expected);

        }

        @Test
        public void instanceInitializerWorksAsExpected() throws ClassNotFoundException {
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
        public void invokeGetComponentTypeOnPrimitiveArrayReturnsExpectedValue() {
            initial.setRegisters(0, new int[0], "[I");
            expected.setRegisters(MethodState.ResultRegister, int.class, CommonTypes.CLASS);

            VMTester.test(CLASS_NAME, "invokeGetComponentType()V", initial, expected);
        }

        @Test
        public void invokeReturnIntReturnsInt() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            VirtualMachine vm = VMTester.spawnVM();
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME);
            expected.setRegisters(MethodState.ResultRegister, 0x7, "I");

            VMTester.test(CLASS_NAME, "invokeReturnInt()V", initial, expected);
        }

        @Test
        public void invokeReturnParameterReturnsParameter() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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
        public void invokeReturnVoidReturnsVoid() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            VirtualMachine vm = VMTester.spawnVM(true);
            Class<?> virtualClass = vm.getClassLoader().loadClass(CLASS_NAME_BINARY);
            Object instance = virtualClass.newInstance();
            initial.setRegisters(0, instance, CLASS_NAME);
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "invokeReturnVoid()V", initial);
            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);

            assertNull(consensus);
        }

        @Test
        public void invokeVirtualManyParametersThrowsNoExceptions() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
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
        public void invokeNonExistentMethodThrowsException() {
            thrown.expect(RuntimeException.class);
            thrown.expectMessage("Can't find Smali file for Lim_not_your_friend_buddy;");

            VMTester.execute(CLASS_NAME, "invokeNonExistentMethod()V");
        }

        @Test
        public void hasExpectedToString() {
            VirtualMachine vm = VMTester.spawnVM();
            VirtualClass virtualClass = vm.getClassManager().getVirtualClass(CLASS_NAME);
            VirtualMethod method = virtualClass.getMethod("invokeReturnVoid()V");
            ExecutionGraph graph = vm.spawnInstructionGraph(method);
            Op op = graph.getOp(0);

            assertEquals("invoke-static {}, " + CLASS_NAME + "->returnVoid()V", op.toString());
        }
    }
}
