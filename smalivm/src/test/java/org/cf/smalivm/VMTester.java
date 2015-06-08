package org.cf.smalivm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.MaxAddressVisitsExceeded;
import org.cf.smalivm.exception.MaxCallDepthExceeded;
import org.cf.smalivm.exception.MaxMethodVisitsExceeded;
import org.cf.smalivm.exception.UnhandledVirtualException;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.jf.dexlib2.writer.builder.DexBuilder;

import com.google.common.collect.Sets;

public class VMTester {

    private static final String TEST_DIRECTORY = "resources/test";

    private static ClassManager classManager;

    public static Map<String, Map<String, HeapItem>> buildClassNameToFieldItem(String className, Object... params) {
        Map<String, Map<String, HeapItem>> result = new HashMap<String, Map<String, HeapItem>>(1);
        Map<String, HeapItem> fieldReferenceToValue = new HashMap<String, HeapItem>(params.length / 2);
        result.put(className, fieldReferenceToValue);
        for (int i = 0; i < params.length; i += 2) {
            String fieldNameAndType = (String) params[i];
            String type = fieldNameAndType.split(":")[1];
            HeapItem item = new HeapItem(params[i + 1], type);
            fieldReferenceToValue.put(fieldNameAndType, item);
        }

        return result;
    }

    public static Map<String, HeapItem> buildFieldToValue(Object... params) {
        Map<String, HeapItem> result = new HashMap<String, HeapItem>(params.length / 3);
        for (int i = 0; i < params.length; i += 3) {
            HeapItem item = new HeapItem(params[i + 1], (String) params[i + 2]);
            result.put((String) params[i], item);
        }

        return result;
    }

    public static TIntObjectMap<HeapItem> buildRegisterState(Object... params) {
        TIntObjectMap<HeapItem> result = new TIntObjectHashMap<HeapItem>();
        for (int i = 0; i < params.length; i += 3) {
            HeapItem item = new HeapItem(params[i + 1], (String) params[i + 2]);
            result.put((int) params[i], item);
        }

        return result;
    }

    public static ExecutionGraph execute(String className, String methodSignature) {
        return execute(className, methodSignature, new TIntObjectHashMap<HeapItem>(),
                        new HashMap<String, Map<String, HeapItem>>(0));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToItem) {
        return execute(className, methodSignature, initialRegisterToItem, new HashMap<String, Map<String, HeapItem>>(0));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<HeapItem> registerToItem, Map<String, Map<String, HeapItem>> classNameToFieldItem) {
        return execute(className, methodSignature, registerToItem, classNameToFieldItem, Sets.newHashSet(className));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<HeapItem> registerToItem, Map<String, Map<String, HeapItem>> classNameToFieldItem,
                    Set<String> classNamesToStaticInit) {
        VirtualMachine vm = getTestVM();

        return execute(vm, className, methodSignature, registerToItem, classNameToFieldItem);
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodSignature,
                    TIntObjectMap<HeapItem> registerToItem, Map<String, Map<String, HeapItem>> classNameToFieldItem) {
        String methodDescriptor = className + "->" + methodSignature;
        ExecutionContext ectx = getInitializedContext(vm, methodDescriptor, registerToItem, classNameToFieldItem);
        ExecutionGraph graph = null;
        try {
            graph = vm.execute(methodDescriptor, ectx);
        } catch (MaxAddressVisitsExceeded | MaxCallDepthExceeded | MaxMethodVisitsExceeded | UnhandledVirtualException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public static ClassManager getClassManager() {
        return classManager;
    }

    public static DexBuilder getDexBuilder() {
        return DexBuilder.makeDexBuilder(Dexifier.DEFAULT_API_LEVEL);
    }

    public static VirtualMachine getTestVM(boolean reloadClasses) {
        // Not reloading classes is an optimization to speed up tests
        if ((null == classManager) || reloadClasses) {
            try {
                classManager = new ClassManager(TEST_DIRECTORY, getDexBuilder());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        return new VirtualMachine(classManager);
    }

    public static VirtualMachine getTestVM() {
        return getTestVM(false);
    }

    public static void testClassState(String className, String methodSignature,
                    Map<String, Map<String, HeapItem>> classNameToInitialFieldItem,
                    Map<String, Map<String, HeapItem>> classNameToExpectedFieldItem) {
        TIntObjectMap<HeapItem> initialRegisterToItem = new TIntObjectHashMap<HeapItem>();
        TIntObjectMap<HeapItem> expectedRegisterToItem = new TIntObjectHashMap<HeapItem>();
        testMethodAndClassState(className, methodSignature, initialRegisterToItem, expectedRegisterToItem,
                        classNameToInitialFieldItem, classNameToExpectedFieldItem, new HashSet<String>());
    }

    public static void testClassState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToItem,
                    Map<String, Map<String, HeapItem>> classNameToExpectedFieldItem) {
        TIntObjectMap<HeapItem> expectedRegisterToItem = new TIntObjectHashMap<HeapItem>();
        Map<String, Map<String, HeapItem>> classNameToInitialFieldItem = new HashMap<String, Map<String, HeapItem>>(0);
        testMethodAndClassState(className, methodSignature, initialRegisterToItem, expectedRegisterToItem,
                        classNameToInitialFieldItem, classNameToExpectedFieldItem, new HashSet<String>());
    }

    public static void testClassState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToItem,
                    Map<String, Map<String, HeapItem>> classNameToInitialFieldItem,
                    Map<String, Map<String, HeapItem>> classNameToExpectedFieldItem) {
        TIntObjectMap<HeapItem> expectedRegisterToItem = new TIntObjectHashMap<HeapItem>();
        testMethodAndClassState(className, methodSignature, initialRegisterToItem, expectedRegisterToItem,
                        classNameToInitialFieldItem, classNameToExpectedFieldItem, new HashSet<String>());
    }

    public static void testMethodAndClassState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToItem, TIntObjectMap<HeapItem> expectedRegisterToItem,
                    Map<String, Map<String, HeapItem>> classNameToInitialFieldItem,
                    Map<String, Map<String, HeapItem>> classNameToExpectedFieldItem,
                    Set<String> classNamesToMakeAvailable) {
        Set<String> classNames = new HashSet<String>();
        classNames.add(className);
        classNames.addAll(classNameToInitialFieldItem.keySet());
        classNames.addAll(classNameToExpectedFieldItem.keySet());
        classNames.addAll(classNamesToMakeAvailable);
        ExecutionGraph graph = execute(className, methodSignature, initialRegisterToItem, classNameToInitialFieldItem,
                        classNames);
        assertNotNull(graph);

        for (int register : expectedRegisterToItem.keys()) {
            HeapItem item = expectedRegisterToItem.get(register);
            HeapItem consensus = graph.getTerminatingRegisterConsensus(register);

            testRegisterEquals(register, item, consensus);
        }

        for (String currentClassName : classNameToExpectedFieldItem.keySet()) {
            Map<String, HeapItem> check = classNameToExpectedFieldItem.get(currentClassName);
            for (String fieldReference : check.keySet()) {
                HeapItem checkItem = check.get(fieldReference);
                String fieldDescriptor = currentClassName + "->" + fieldReference;
                HeapItem actualItem = graph.getTerminatingFieldConsensus(fieldDescriptor);

                testFieldEquals(fieldDescriptor, checkItem, actualItem);
            }
        }
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> expectedRegisterToItem) {
        testMethodState(className, methodSignature, new TIntObjectHashMap<HeapItem>(), expectedRegisterToItem);
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> expectedRegisterToItem,
                    Map<String, Map<String, HeapItem>> classNameToInitialFieldItem) {
        TIntObjectMap<HeapItem> initialRegisterToItem = new TIntObjectHashMap<HeapItem>();
        Map<String, Map<String, HeapItem>> classNameToExpectedFieldItem = new HashMap<String, Map<String, HeapItem>>(0);

        testMethodAndClassState(className, methodSignature, initialRegisterToItem, expectedRegisterToItem,
                        classNameToInitialFieldItem, classNameToExpectedFieldItem, new HashSet<String>());
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToItem, TIntObjectMap<HeapItem> expectedRegisterToItem) {
        testMethodAndClassState(className, methodSignature, initialRegisterToItem, expectedRegisterToItem,
                        new HashMap<String, Map<String, HeapItem>>(0), new HashMap<String, Map<String, HeapItem>>(0),
                        new HashSet<String>());
    }

    public static void testVisitation(String className, String methodSignature, int[] expectedVisitations) {
        testVisitation(className, methodSignature, new TIntObjectHashMap<HeapItem>(), expectedVisitations);
    }

    public static void testVisitation(String className, String methodSignature,
                    TIntObjectMap<HeapItem> initialRegisterToValue, int[] expectedVisitations) {
        ExecutionGraph graph = VMTester.execute(className, methodSignature, initialRegisterToValue);
        int[] addresses = graph.getAddresses();
        TIntList expectedVisits = new TIntArrayList(expectedVisitations);
        TIntList actualVisits = new TIntArrayList();
        for (int address : addresses) {
            if (!graph.wasAddressReached(address)) {
                continue;
            }
            actualVisits.add(address);
        }
        actualVisits.reverse();

        assertEquals(expectedVisits, actualVisits);
    }

    private static ExecutionContext getInitializedContext(VirtualMachine vm, String methodDescriptor,
                    TIntObjectMap<HeapItem> registerToItem,
                    Map<String, Map<String, HeapItem>> classNameToInitialFieldItem) {
        ExecutionContext ectx = vm.spawnExecutionContext(methodDescriptor);
        int registerCount = ectx.getMethodState().getRegisterCount();
        setupMethodState(ectx, registerToItem, registerCount);
        setupClassStates(ectx, classNameToInitialFieldItem);

        return ectx;
    }

    private static void setupClassStates(ExecutionContext ectx, Map<String, Map<String, HeapItem>> classNameToFieldItem) {
        for (String className : classNameToFieldItem.keySet()) {
            Map<String, HeapItem> fieldToItem = classNameToFieldItem.get(className);
            ClassState cState = ectx.peekClassState(className);
            for (String fieldNameAndType : fieldToItem.keySet()) {
                HeapItem item = fieldToItem.get(fieldNameAndType);
                cState.pokeField(fieldNameAndType, item);
            }
            ectx.initializeClass(className, cState, SideEffect.Level.NONE);
        }
    }

    private static void setupMethodState(ExecutionContext ectx, TIntObjectMap<HeapItem> registerToItem,
                    int registerCount) {
        MethodState mState = new MethodState(ectx, registerCount);
        for (int register : registerToItem.keys()) {
            HeapItem item = registerToItem.get(register);
            mState.assignRegister(register, item);
        }
        ectx.setMethodState(mState);
    }

    private static void testFieldEquals(String fieldDescriptor, HeapItem item, HeapItem consensus) {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldDescriptor).append(" expected ").append(item).append(", consensus ").append(consensus);
        String msg = sb.toString();

        testValueEquals(item, consensus, msg);
    }

    private static void testRegisterEquals(int register, HeapItem item, HeapItem consensus) {
        StringBuilder sb = new StringBuilder();
        sb.append('r').append(register).append(" expected ").append(item).append(", consensus ").append(consensus);
        String msg = sb.toString();

        testValueEquals(item, consensus, msg);
    }

    private static void testValueEquals(HeapItem expected, HeapItem consensus, String msg) {
        Object expectedValue = expected.getValue();
        Object consensusValue = consensus.getValue();

        if (expectedValue != null) {
            assertNotNull(msg, consensusValue);
        }

        if (expectedValue == null) {
            assertEquals(msg, expected, consensus);
        } else if (expected.isUnknown()) {
            // Normally, unknown doesn't equal anything, including itself, but tests are more relaxed.
            assertEquals(msg, expected.toString(), consensus.toString());
        } else if (expectedValue.getClass().isArray()) {
            // If array, type is "Object", so need to use isArray() instead of instanceof
            boolean result = Objects.deepEquals(expectedValue, consensusValue);
            assertEquals(expected.getType(), consensus.getType());
            assertTrue(msg, result);
        } else if (expectedValue instanceof StringBuilder) {
            assertEquals(msg, expectedValue.toString(), consensusValue.toString());
        } else {
            assertEquals(msg, expectedValue, consensusValue);
        }
    }

    public static void addHeapItem(MethodState mState, int register, Object value, String type) {
        HeapItem item = mock(HeapItem.class);
        when(item.getValue()).thenReturn(value);
        if ("I".equals(type) && value instanceof Number) {
            when(item.getIntegerValue()).thenReturn((Integer) value);
        } else if (value instanceof UnknownValue) {
            when(item.isUnknown()).thenReturn(true);
        }
        when(item.getType()).thenReturn(type);
        when(mState.readRegister(eq(register))).thenReturn(item);
    }

    public static void verifyExceptionHandling(VirtualException expectedException, ExecutionNode node,
                    MethodState mState) {
        verify(node).setException(eq(expectedException));
        verify(node).clearChildAddresses();
        verify(node, times(0)).setChildAddresses(any(int[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

    public static void verifyExceptionHandling(Set<VirtualException> expectedExceptions, ExecutionNode node,
                    MethodState mState) {
        verify(node).setExceptions(eq(expectedExceptions));
        verify(node).clearChildAddresses();
        verify(node, times(0)).setChildAddresses(any(int[].class));
        verify(mState, times(0)).assignRegister(any(Integer.class), any(HeapItem.class));
    }

}
