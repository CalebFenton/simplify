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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.type.LocalInstance;
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

    public static class UnitTest {

        private static final int ADDRESS = 0;
        private static final int ARG1_REGISTER = 2;
        private static final int ARG2_REGISTER = 4;

        private static final String METHOD_CLASS = "Lsome/class;";
        private static final String METHOD_NAME = "someMethod";
        private static final String METHOD_RETURN = "V";
        private static final String METHOD_DESCRIPTOR = METHOD_CLASS + "->" + METHOD_NAME + "(I)" + METHOD_RETURN;
        private static final String[] METHOD_PARAMS = { "I" };

        private TIntObjectMap<MethodLocation> addressToLocation;
        private BuilderInstruction instruction;
        private MethodLocation location;

        private ExecutionContext ectx;
        private MethodState mState;
        private ExecutionNode node;
        private InvokeOp op;
        private InvokeOpFactory opFactory;
        private ClassManager classManager;
        private VirtualMachine vm;
        MethodReference methodRef;

        @Before
        public void setUp() {
            vm = mock(VirtualMachine.class);

            classManager = mock(ClassManager.class);
            when(classManager.isLocalClass(METHOD_CLASS)).thenReturn(true);
            when(classManager.isFrameworkClass(METHOD_DESCRIPTOR)).thenReturn(false);
            when(classManager.isSafeFrameworkClass(METHOD_DESCRIPTOR)).thenReturn(false);
            when(vm.getClassManager()).thenReturn(classManager);

            Configuration configuration = mock(Configuration.class);
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

        @Test
        public void hasExpectedToString() {
            int value = 0;
            VMTester.addHeapItem(mState, ARG1_REGISTER, value, "I");
            VMTester.addHeapItem(mState, ARG2_REGISTER, value, "I");

            instruction = buildInstruction35c(Opcode.INVOKE_STATIC);

            op = (InvokeOp) opFactory.create(location, addressToLocation, vm);
            op.execute(node, ectx);

            String expected = "invoke-static {r" + ARG1_REGISTER + "}, " + METHOD_DESCRIPTOR;
            assertEquals(expected, op.toString());
        }
    }

    public static class TestInvokeReflectedMethod {

        private static final String CLASS_NAME = "Linvoke_reflected_test;";

        @Test
        public void testInitStringWithByteArrayWithUnknownParameter() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UninitializedInstance(
                            "Ljava/lang/String;"), "Ljava/lang/String;", 1, new UnknownValue(), "[B");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "Ljava/lang/String;",
                            1, new UnknownValue(), "[B");

            VMTester.testMethodState(CLASS_NAME, "InitStringWithByteArray()V", initial, expected);
        }

        @Test
        public void testInvokeLongValueOfWithLongGetsCorrectParameters() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x1000L, "J");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new Long(0x1000L), "J");

            VMTester.testMethodState(CLASS_NAME, "InvokeLongValueOf()V", initial, expected);
        }

        @Test
        public void testInvokeStringBuilderAppendWithLong() {
            long value = 0x1234L;
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new StringBuilder(),
                            "Ljava/lang/StringBuilder;", 1, value, "J");
            StringBuilder sb = new StringBuilder().append(value);
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, sb, "Ljava/lang/StringBuilder;", 1,
                            value, "J");

            VMTester.testMethodState(CLASS_NAME, "InvokeStringBuilderAppendWithLong()V", initial, expected);
        }
    }

    public static class TestInvokeStatic {

        private static final String CLASS_NAME = "Linvoke_static_test;";

        @Test
        public void testInvokeMethodOutsideClassThatAccessesThisClassReturnsExpectedValue() {
            String value = "i have been initialized";
            TIntObjectMap<HeapItem> initialRegisterToValue = new TIntObjectHashMap<HeapItem>();
            TIntObjectMap<HeapItem> expectedRegisterToValue = VMTester.buildRegisterState(MethodState.ResultRegister,
                            value, "Ljava/lang/String;");
            Map<String, Map<String, HeapItem>> classNameToInitialFieldValue = VMTester.buildClassNameToFieldItem(
                            CLASS_NAME, "sometimes_initialized:Ljava/lang/String;", value);
            Map<String, Map<String, HeapItem>> classNameToExpectedFieldValue = VMTester.buildClassNameToFieldItem(
                            CLASS_WITH_STATIC_INIT, "string:Ljava/lang/String;", "Uhhh, about 11, sir.");

            VMTester.testMethodAndClassState(CLASS_NAME, "InvokeMethodOutsideClassThatAccessesThisClass()V",
                            initialRegisterToValue, expectedRegisterToValue, classNameToInitialFieldValue,
                            classNameToExpectedFieldValue, new HashSet<String>());
        }

        @Test
        public void testInvokeMutateStaticClassFieldNonDeterministicallyPropigatesUnknown() {
            Map<String, Map<String, HeapItem>> initial = VMTester.buildClassNameToFieldItem(CLASS_NAME, "mutable:[I",
                            new int[] { 3, 3, 3 });
            Map<String, Map<String, HeapItem>> expected = VMTester.buildClassNameToFieldItem(CLASS_NAME, "mutable:[I",
                            new UnknownValue());

            VMTester.testClassState(CLASS_NAME, "InvokeMutateStaticClassFieldNonDeterministically()V", initial,
                            expected);
        }

        @Test
        public void testInvokeMutateStaticClassFieldPropigatesChanges() {
            Map<String, Map<String, HeapItem>> initial = VMTester.buildClassNameToFieldItem(CLASS_NAME, "mutable:[I",
                            new int[] { 3, 3, 3 });
            Map<String, Map<String, HeapItem>> expected = VMTester.buildClassNameToFieldItem(CLASS_NAME, "mutable:[I",
                            new int[] { 0, 3, 3 });

            VMTester.testClassState(CLASS_NAME, "InvokeMutateStaticClassField()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStringBuilderDoesMutateParameter() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new StringBuilder("i have been"),
                            "Ljava/lang/StringBuilder;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new StringBuilder("i have been mutated"),
                            "Ljava/lang/StringBuilder;");

            VMTester.testMethodState(CLASS_NAME, "InvokeMutateStringBuilder()V", initial, expected);
        }

        @Test
        public void testInvokeMutateStringDoesNotMutateParameter() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, "not mutated", "Ljava/lang/String;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, "not mutated", "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "InvokeMutateString()V", initial, expected);
        }

        @Test
        public void testInvokeNonExistantMethodWithTwoArrayParametersWithUnknownParameterMutatesAllParameters() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 3, 5, 7 }, "[I", 1,
                            new UnknownValue(), "[I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I", 1,
                            new UnknownValue(), "[I");

            VMTester.testMethodState(CLASS_NAME, "InvokeNonExistantMethodWithTwoArrayParameters()V", initial, expected);
        }

        @Test
        public void testInvokeReturnIntReturnsInt() {
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, 0x7, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeReturnInt()V", expected);
        }

        @Test
        public void testInvokeReturnParameterReturnsParameter() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 0x5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, 0x5, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeReturnParameter()V", initial, expected);
        }

        @Test
        public void testInvokeReturnUninitializedFieldDoesNotReturnNull() {
            TIntObjectMap<HeapItem> initial = new TIntObjectHashMap<HeapItem>();
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister,
                            new UnknownValue(), "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "InvokeReturnUninitializedField()V", initial, expected);
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() {
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "InvokeReturnVoid()V");

            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);
            assertNull(consensus);
        }

        @Test
        public void testKnownMutableParametersAreMutatedWithDeterministicExecution() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, "[I", 1, 0, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[] { 0x0 }, "[I", 1, 0, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V",
                            initial, expected);
        }

        @Test
        public void testKnownMutableParametersAreMutatedWithNonDeterministicExecution() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, "[I", 1,
                            new UnknownValue(), "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I", 1,
                            new UnknownValue(), "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V",
                            initial, expected);
        }

        @Test
        public void testNonDeterministicallyInitializedClassHasUnknownFieldValues() {
            Map<String, Map<String, HeapItem>> initial = new HashMap<String, Map<String, HeapItem>>();
            Map<String, Map<String, HeapItem>> expected = VMTester.buildClassNameToFieldItem(CLASS_WITH_STATIC_INIT,
                            "string:Ljava/lang/String;", new UnknownValue());

            VMTester.testClassState(CLASS_NAME, "NonDeterministicallyInitializeClassWithStaticInit()V", initial,
                            expected);
        }
    }

    public static class TestInvokeVirtual {

        private static final String CLASS_NAME = "Linvoke_virtual_test;";

        @Test
        public void testInvokeGetComponentTypeOnPrimitiveArrayReturnsExpectedValue() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[0], "[I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, int.class,
                            "Ljava/lang/Class;");

            VMTester.testMethodState(CLASS_NAME, "InvokeGetComponentType()V", initial, expected);
        }

        @Test
        public void testInvokeReturnIntReturnsInt() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME);
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, 0x7, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeReturnInt()V", initial, expected);
        }

        @Test
        public void testInvokeReturnParameterReturnsParameter() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME,
                            1, 0x5, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME),
                            CLASS_NAME, 1, 0x5, "I", MethodState.ResultRegister, 0x5, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeReturnParameter()V", initial, expected);
        }

        @Test
        public void testInvokeReturnVoidReturnsVoid() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME);
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "InvokeReturnVoid()V", initial);

            HeapItem consensus = graph.getTerminatingRegisterConsensus(MethodState.ResultRegister);
            assertNull(consensus);
        }

        @Test
        public void testInvokeVirtualManyParametersThrowsNoExceptions() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), CLASS_NAME,
                            1, 0x100L, "J", 3, 0x200L, "J", 5, 0x300L, "J", 7, 0x3, "I");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, 0x3, "I");

            VMTester.testMethodState(CLASS_NAME, "InvokeRangeManyParameters()V", initial, expected);
        }
    }

    public static class TestMethodStateProperties {

        private static final String CLASS_NAME = "Linvoke_static_test;";

        @Test
        public void testInvokeNonExistantMethodWithTwoArrayParametersWithUnknownParameterMutatesAllParameters() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0 }, "[I", 1, new int[] { 0 },
                            "[I");
            ExecutionGraph graph = VMTester.execute(CLASS_NAME, "InvokeNonExistantMethodWithTwoArrayParameters()V",
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

    private static final String CLASS_WITH_STATIC_INIT = "Lclass_with_static_init;";

}
