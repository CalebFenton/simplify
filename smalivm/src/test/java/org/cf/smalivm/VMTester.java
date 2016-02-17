package org.cf.smalivm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.cf.smalivm.exception.VirtualMachineException;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.smali.ClassManagerFactory;
import org.cf.smalivm.smali.Dexifier;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.writer.builder.DexBuilder;

import com.google.common.primitives.Ints;

public class VMTester {

    private static final String TEST_CLASS_PATH = "resources/test";

    private static ClassManager classManager;

    public static void setRegisterMock(MethodState mState, int register, Object value, String type) {
        HeapItem item = mock(HeapItem.class);
        when(item.getValue()).thenReturn(value);
        if ("I".equals(type) && value instanceof Number) {
            when(item.getIntegerValue()).thenReturn((Integer) value);
        } else if (value instanceof UnknownValue) {
            when(item.isUnknown()).thenReturn(true);
        }
        when(item.getComponentBase()).thenReturn(ClassNameUtils.getComponentBase(type));
        when(item.getType()).thenReturn(type);
        when(mState.readRegister(eq(register))).thenReturn(item);
    }

    public static ExecutionGraph execute(String className, String methodSignature) {
        return execute(className, methodSignature, new VMState());
    }

    public static ExecutionGraph execute(String className, String methodSignature, VMState state) {
        VirtualMachine vm = spawnVM();
        return execute(vm, className, methodSignature, state);
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodSignature) {
        return execute(vm, className, methodSignature, new VMState());
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodSignature, VMState state) {
        String methodDescriptor = className + "->" + methodSignature;
        ExecutionContext ectx = buildInitializedContext(vm, methodDescriptor, state);
        ExecutionGraph graph = null;
        try {
            graph = vm.execute(methodDescriptor, ectx);
        } catch (VirtualMachineException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public static ClassManager getClassManager() {
        return classManager;
    }

    public static DexBuilder getDexBuilder() {
        return DexBuilder.makeDexBuilder(Opcodes.forApi(Dexifier.DEFAULT_API_LEVEL));
    }

    public static VirtualMachine spawnVM() {
        return spawnVM(false);
    }

    /**
     * Create a new virtual machine and class manager. This is necessary when class implementations are changing, such
     * as testing the optimization strategies and when dealing with class loading and dynamic creation.
     * 
     * @param reloadClasses
     * @return
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
        assertNotNull(graph);

        testRegisterState(graph, expectedState.getRegisters());
        testClassState(graph, expectedState.getFields());
    }

    public static void testVisitation(String className, String methodSignature, int[] expectedAddresses) {
        testVisitation(className, methodSignature, new VMState(), expectedAddresses);
    }

    public static void testVisitation(String className, String methodSignature, VMState initialState,
                    int[] expectedAddresses) {
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

    public static void verifyExceptionHandling(Set<VirtualException> expectedExceptions, ExecutionNode node,
                    MethodState mState) {
        verify(node).setExceptions(eq(expectedExceptions));
        verify(node).clearChildren();
        verify(node, times(0)).setChildLocations(any(MethodLocation[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

    public static void verifyExceptionHandling(VirtualException expectedException, ExecutionNode node,
                    MethodState mState) {
        verify(node).setException(eq(expectedException));
        verify(node).clearChildren();
        verify(node, times(0)).setChildLocations(any(MethodLocation[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

    private static ExecutionContext buildInitializedContext(VirtualMachine vm, String methodDescriptor, VMState state) {
        ExecutionContext ectx = vm.spawnRootExecutionContext(methodDescriptor);
        int registerCount = ectx.getMethodState().getRegisterCount();
        setupMethodState(ectx, state.getRegisters(), registerCount);
        setupClassStates(ectx, state.getFields());

        return ectx;
    }

    private static void setupClassStates(ExecutionContext ectx,
                    Map<String, Map<String, HeapItem>> classNameToFieldDescriptorToItem) {
        for (Entry<String, Map<String, HeapItem>> entry : classNameToFieldDescriptorToItem.entrySet()) {
            String className = entry.getKey();
            Map<String, HeapItem> fieldDescriptorToItem = entry.getValue();
            ClassState cState = ectx.peekClassState(className);
            for (Entry<String, HeapItem> fieldNameToItem : fieldDescriptorToItem.entrySet()) {
                String fieldNameAndType = fieldNameToItem.getKey();
                HeapItem item = fieldNameToItem.getValue();
                cState.pokeField(fieldNameAndType, item);
            }
            ectx.initializeClass(className, cState, SideEffect.Level.NONE);
        }
    }

    private static void setupMethodState(ExecutionContext ectx, Map<Integer, HeapItem> registerToItem, int registerCount) {
        MethodState mState = new MethodState(ectx, registerCount);
        for (Entry<Integer, HeapItem> entry : registerToItem.entrySet()) {
            Integer register = entry.getKey();
            HeapItem item = entry.getValue();
            mState.assignRegister(register, item);
        }
        ectx.setMethodState(mState);
    }

    private static void testClassState(ExecutionGraph graph,
                    Map<String, Map<String, HeapItem>> classNameToFieldDescriptorToItem) {
        for (Entry<String, Map<String, HeapItem>> fieldDescriptorMapEntry : classNameToFieldDescriptorToItem.entrySet()) {
            String className = fieldDescriptorMapEntry.getKey();
            Map<String, HeapItem> fieldDescriptorToItem = fieldDescriptorMapEntry.getValue();
            for (Entry<String, HeapItem> fieldEntry : fieldDescriptorToItem.entrySet()) {
                String fieldReference = fieldEntry.getKey();
                String fieldDescriptor = className + "->" + fieldReference;
                HeapItem expected = fieldEntry.getValue();
                HeapItem actual = graph.getTerminatingFieldConsensus(fieldDescriptor);
                testFieldEquals(fieldDescriptor, expected, actual);
            }
        }
    }

    private static void testFieldEquals(String fieldDescriptor, HeapItem item, HeapItem consensus) {
        testValueEquals(item, consensus);
    }

    private static void testRegisterEquals(int register, HeapItem item, HeapItem consensus) {
        testValueEquals(item, consensus);
    }

    private static void testRegisterState(ExecutionGraph graph, Map<Integer, HeapItem> registerToItem) {
        for (Entry<Integer, HeapItem> entry : registerToItem.entrySet()) {
            Integer register = entry.getKey();
            HeapItem expected = entry.getValue();
            HeapItem actual = graph.getTerminatingRegisterConsensus(register);
            testRegisterEquals(register, expected, actual);
        }
    }

    private static void testValueEquals(HeapItem expected, HeapItem consensus) {
        Object expectedValue = expected.getValue();
        Object consensusValue = consensus.getValue();

        if (expectedValue != null) {
            assertNotNull(consensusValue);
        }

        if (expectedValue == null) {
            assertEquals(expected, consensus);
        } else if (expected.isUnknown()) {
            // Normally unknown doesn't equal anything, including itself, but tests are more relaxed.
            assertEquals(expected.toString(), consensus.toString());
        } else if (expectedValue.getClass().isArray()) {
            assertEquals(expected.getType(), consensus.getType());

            // TODO: why does an array of local instances equal an array of actual instances?
            assertEquals(expectedValue.getClass(), consensusValue.getClass());

            if (expectedValue instanceof Object[] && consensusValue instanceof Object[])
                assertArrayEquals((Object[]) expectedValue, (Object[]) consensusValue);
            else if (expectedValue instanceof byte[] && consensusValue instanceof byte[])
                assertArrayEquals((byte[]) expectedValue, (byte[]) consensusValue);
            else if (expectedValue instanceof short[] && consensusValue instanceof short[])
                assertArrayEquals((short[]) expectedValue, (short[]) consensusValue);
            else if (expectedValue instanceof int[] && consensusValue instanceof int[])
                assertArrayEquals((int[]) expectedValue, (int[]) consensusValue);
            else if (expectedValue instanceof long[] && consensusValue instanceof long[])
                assertArrayEquals((long[]) expectedValue, (long[]) consensusValue);
            else if (expectedValue instanceof char[] && consensusValue instanceof char[])
                assertArrayEquals((char[]) expectedValue, (char[]) consensusValue);
            else if (expectedValue instanceof float[] && consensusValue instanceof float[])
                assertArrayEquals((float[]) expectedValue, (float[]) consensusValue, 0.001F);
            else if (expectedValue instanceof double[] && consensusValue instanceof double[])
                assertArrayEquals((double[]) expectedValue, (double[]) consensusValue, 0.001);
            else if (expectedValue instanceof boolean[] && consensusValue instanceof boolean[])
                assertArrayEquals((boolean[]) expectedValue, (boolean[]) consensusValue);
            else {
                assertEquals(expectedValue, consensusValue);
            }
        } else if (expectedValue instanceof StringBuilder) {
            assertEquals(expectedValue.toString(), consensusValue.toString());
        } else {
            assertEquals(expectedValue, consensusValue);
        }
    }

}
