package org.cf.smalivm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.primitives.Ints;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.dex.SmaliParser;
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.mockito.ArgumentCaptor;

public class VMTester {

    public static final String TEST_CLASS_PATH = "src/test/resources/smali";

    private static ClassManager classManager;

    public static ExecutionGraph execute(String className, String methodDescriptor) {
        return execute(className, methodDescriptor, new VMState());
    }

    public static ExecutionGraph execute(String className, String methodDescriptor, VMState state) {
        return execute(spawnVM(), className, methodDescriptor, state);
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodDescriptor) {
        return execute(vm, className, methodDescriptor, new VMState());
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodDescriptor, VMState state) {
        String methodSignature = className + "->" + methodDescriptor;
        ExecutionContext context = buildInitializedContext(vm, methodSignature, state);
        ExecutionGraph graph = null;
        try {
            graph = vm.execute(context);
        } catch (VirtualMachineException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public static ClassManager getClassManager() {
        return classManager;
    }

    public static DexBuilder getDexBuilder() {
        return new DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL));
    }

    public static void setRegisterMock(MethodState mState, int register, Object value, String type) {
        HeapItem item = mock(HeapItem.class);
        when(item.getValue()).thenReturn(value);
        if (CommonTypes.INTEGER.equals(type) && value instanceof Number) {
            when(item.asInteger()).thenReturn((Integer) value);
        } else if (value instanceof UnknownValue) {
            when(item.isUnknown()).thenReturn(true);
        }
        when(item.getComponentBase()).thenReturn(ClassNameUtils.getComponentBase(type));
        when(item.getType()).thenReturn(type);
        when(mState.readRegister(eq(register))).thenReturn(item);
    }

    /**
     * Create a new {@link VirtualMachine} for testing. Since this is heavily used, it tries to avoid the main cost of creating a {@link
     * VirtualMachine} by reusing the same {@link ClassManager}.
     *
     * @return {@link VirtualMachine} for tests
     */
    public static VirtualMachine spawnVM() {
        return spawnVM(false);
    }

    /**
     * Create a new {@link VirtualMachine} for testing. Since this is heavily used, it tries to avoid the main cost of creating a {@link
     * VirtualMachine} by reusing the same {@link ClassManager} by default. If {@code reloadClasses} is true, a new {@link ClassManager} is created
     * and all classes are loaded again. This is necessary if method implementations are modified. For example, Simplify optimization strategy tests
     * modify method implementation and in order for each test to have the true method implementations, many of those tests set {@code reloadClasses}
     * to {@code true}.
     *
     * @param reloadClasses if true, rebuild {@link ClassManager}, otherwise reuse existing
     * @return {@link VirtualMachine} for tests
     */
    public static VirtualMachine spawnVM(boolean reloadClasses) {
        if ((null == classManager) || reloadClasses) {
            try {
                classManager = new ClassManagerFactory().build(TEST_CLASS_PATH, getDexBuilder());
            } catch (IOException e) {
                throw new RuntimeException("Exception building class manager for " + TEST_CLASS_PATH, e);
            }
        }

        return new VirtualMachineFactory().build(classManager);
    }

    public static void test(String className, String methodSignature, VMState initialState, VMState expectedState) {
        ExecutionGraph graph = execute(className, methodSignature, initialState);
        testState(graph, expectedState);
    }

    public static void test(String className, String methodSignature, VMState expectedState) {
        test(className, methodSignature, new VMState(), expectedState);
    }

    public static void testState(ExecutionGraph graph, VMState expectedState) {
        assertNotNull(graph, "Graph is null. Failed to execute method.");

        testRegisterState(graph, expectedState.getRegisters());
        testClassState(graph, expectedState.getFields());
    }

    public static void testVisitation(String className, String methodSignature, int[] expectedAddresses) {
        testVisitation(className, methodSignature, new VMState(), expectedAddresses);
    }

    public static void testVisitation(String className, String methodSignature, VMState initialState, int[] expectedAddresses) {
        ExecutionGraph graph = VMTester.execute(className, methodSignature, initialState);
        testVisitation(graph, expectedAddresses);
    }

    public static void testVisitation(ExecutionGraph graph, int[] expectedAddresses) {
        int[] addresses = graph.getAddresses();
        List<Integer> visitedAddresses = new LinkedList<Integer>();
        for (int address : addresses) {
            if (graph.wasAddressReached(address)) {
                visitedAddresses.add(address);
            }
        }
        int[] actualAddresses = Ints.toArray(visitedAddresses);
        Arrays.sort(expectedAddresses);
        Arrays.sort(actualAddresses);

        assertArrayEquals(expectedAddresses, actualAddresses);
    }

    public static void verifyExceptionHandling(Set<Throwable> expectedExceptions, ExecutionNode node, MethodState mState) {
        verify(node).setExceptions(eq(expectedExceptions));
        verify(node).clearChildren();
        verify(node, times(0)).setChildLocations(any(MethodLocation[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

    public static void verifyExceptionHandling(Class<? extends Throwable> exceptionClass, ExecutionNode node, MethodState mState) {
        verifyExceptionHandling(exceptionClass, null, node, mState);
    }

    public static void verifyExceptionHandling(Class<? extends Throwable> exceptionClass, String message, ExecutionNode node, MethodState mState) {
        ArgumentCaptor<Throwable> argument = ArgumentCaptor.forClass(Throwable.class);
        verify(node).setException(argument.capture());
        assertEquals(exceptionClass, argument.getValue().getClass());
        assertEquals(message, argument.getValue().getMessage());
        verify(node).clearChildren();
        verify(node, times(0)).setChildLocations(any(MethodLocation[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

    private static ExecutionContext buildInitializedContext(VirtualMachine vm, String methodSignature, VMState state) {
        VirtualMethod method = vm.getClassManager().getMethod(methodSignature);
        ExecutionContext context = vm.spawnRootContext(method);
        int registerCount = context.getMethodState().getRegisterCount();
        setupMethodState(context, state.getRegisters(), registerCount);
        setupClassStates(context, vm, state.getFields());

        return context;
    }

    private static void setupClassStates(ExecutionContext context, VirtualMachine vm,
                                         Map<String, Map<String, HeapItem>> classNameToFieldDescriptorToItem) {
        ClassManager classManager = vm.getClassManager();
        for (Entry<String, Map<String, HeapItem>> entry : classNameToFieldDescriptorToItem.entrySet()) {
            String className = entry.getKey();
            VirtualClass virtualClass = classManager.getVirtualClass(className);
            Map<String, HeapItem> fieldDescriptorToItem = entry.getValue();
            ClassState cState = context.peekClassState(virtualClass);
            for (Entry<String, HeapItem> fieldNameAndTypeToItem : fieldDescriptorToItem.entrySet()) {
                String fieldNameAndType = fieldNameAndTypeToItem.getKey();
                String fieldName = fieldNameAndType.split(":")[0];
                VirtualField field = virtualClass.getField(fieldName);
                HeapItem item = fieldNameAndTypeToItem.getValue();
                cState.pokeField(field, item);
            }
            context.initializeClass(cState, SideEffect.Level.NONE);
        }
    }

    private static void setupMethodState(ExecutionContext context, Map<Integer, HeapItem> registerToItem, int registerCount) {
        MethodState mState = new MethodState(context, registerCount);
        for (Entry<Integer, HeapItem> entry : registerToItem.entrySet()) {
            Integer register = entry.getKey();
            HeapItem item = entry.getValue();
            mState.pokeRegister(register, item);
        }
        context.setMethodState(mState);
    }

    private static void testClassState(ExecutionGraph graph, Map<String, Map<String, HeapItem>> classNameToFieldDescriptorToItem) {
        for (Entry<String, Map<String, HeapItem>> fieldDescriptorMapEntry : classNameToFieldDescriptorToItem.entrySet()) {
            String className = fieldDescriptorMapEntry.getKey();
            VirtualClass virtualClass = graph.getVM().getClassManager().getVirtualClass(className);
            Map<String, HeapItem> fieldDescriptorToItem = fieldDescriptorMapEntry.getValue();
            for (Entry<String, HeapItem> entry : fieldDescriptorToItem.entrySet()) {
                String fieldDescriptor = entry.getKey();
                String fieldName = fieldDescriptor.split(":")[0];
                VirtualField field = virtualClass.getField(fieldName);
                HeapItem expected = entry.getValue();
                HeapItem actual = graph.getTerminatingFieldConsensus(field);
                testFieldEquals(expected, actual);
            }
        }
    }

    private static void testFieldEquals(HeapItem item, HeapItem consensus) {
        testValueEquals(item, consensus);
    }

    private static void testRegisterEquals(HeapItem item, HeapItem consensus) {
        testValueEquals(item, consensus);
    }

    private static void testRegisterState(ExecutionGraph graph, Map<Integer, HeapItem> registerToItem) {
        for (Entry<Integer, HeapItem> entry : registerToItem.entrySet()) {
            Integer register = entry.getKey();
            HeapItem expected = entry.getValue();
            HeapItem actual = graph.getTerminatingRegisterConsensus(register);
            testRegisterEquals(expected, actual);
        }
    }

    private static void testValueEquals(HeapItem expected, HeapItem consensus) {
        Object expectedValue = expected.getValue();
        Object consensusValue = consensus.getValue();

        if (expectedValue != null) {
            assertNotNull(consensusValue, "No consensus for value");
        }

        if (expectedValue == null) {
            assertEquals(expected, consensus);
        } else if (expected.isUnknown()) {
            // Normally unknown doesn't equal anything, including itself, but tests are more relaxed.
            assertEquals(expected.toString(), consensus.toString());
        } else if (expectedValue.getClass().isArray()) {
            assertEquals(expected.getType(), consensus.getType());
            assertEquals(expectedValue.getClass(), consensusValue.getClass());

            if (expectedValue instanceof Object[] && consensusValue instanceof Object[]) {
                assertArrayEquals((Object[]) expectedValue, (Object[]) consensusValue);
            } else if (expectedValue instanceof byte[] && consensusValue instanceof byte[]) {
                assertArrayEquals((byte[]) expectedValue, (byte[]) consensusValue);
            } else if (expectedValue instanceof short[] && consensusValue instanceof short[]) {
                assertArrayEquals((short[]) expectedValue, (short[]) consensusValue);
            } else if (expectedValue instanceof int[] && consensusValue instanceof int[]) {
                assertArrayEquals((int[]) expectedValue, (int[]) consensusValue);
            } else if (expectedValue instanceof long[] && consensusValue instanceof long[]) {
                assertArrayEquals((long[]) expectedValue, (long[]) consensusValue);
            } else if (expectedValue instanceof char[] && consensusValue instanceof char[]) {
                assertArrayEquals((char[]) expectedValue, (char[]) consensusValue);
            } else if (expectedValue instanceof float[] && consensusValue instanceof float[]) {
                assertArrayEquals((float[]) expectedValue, (float[]) consensusValue, 0.001F);
            } else if (expectedValue instanceof double[] && consensusValue instanceof double[]) {
                assertArrayEquals((double[]) expectedValue, (double[]) consensusValue, 0.001);
            } else if (expectedValue instanceof boolean[] && consensusValue instanceof boolean[]) {
                assertArrayEquals((boolean[]) expectedValue, (boolean[]) consensusValue);
            } else {
                assertEquals(expectedValue, consensusValue);
            }
        } else if (expectedValue instanceof StringBuilder) {
            assertEquals(expectedValue.toString(), consensusValue.toString());
        } else {
            assertEquals(expectedValue, consensusValue);
            assertEquals(expected.getType(), consensus.getType());
        }
    }

}
