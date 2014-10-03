package org.cf.smalivm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.context.ClassState;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.junit.Assert;

import com.google.common.collect.Sets;

public class VMTester {

    private static final Map<String, BuilderClassDef> classNameToDef = buildClassNameToBuilderClassDef();
    private static DexBuilder dexBuilder;
    private static final int MAX_CALL_DEPTH = 10;
    private static final int MAX_NODE_VISITS = 100;
    private static final String TEST_DIRECTORY = "resources/test";

    public static Map<String, BuilderClassDef> buildClassNameToBuilderClassDef() {
        return getClassNameToBuilderClassDef(TEST_DIRECTORY);
    }

    public static Map<String, Map<String, Object>> buildClassNameToFieldValue(String className, Object... params) {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>(1);
        Map<String, Object> fieldReferenceToValue = new HashMap<String, Object>(params.length / 2);
        result.put(className, fieldReferenceToValue);
        for (int i = 0; i < params.length; i += 2) {
            fieldReferenceToValue.put((String) params[i], params[i + 1]);
        }

        return result;
    }

    public static Map<String, Object> buildFieldToValue(Object... params) {
        Map<String, Object> result = new HashMap<String, Object>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            result.put((String) params[i], params[i + 1]);
        }

        return result;
    }

    public static TIntObjectMap<Object> buildRegisterState(Object... params) {
        TIntObjectMap<Object> result = new TIntObjectHashMap<Object>();
        for (int i = 0; i < params.length; i += 2) {
            result.put((int) params[i], params[i + 1]);
        }

        return result;
    }

    public static ExecutionGraph execute(String className, String methodSignature) {
        return execute(className, methodSignature, new TIntObjectHashMap<Object>(),
                        new HashMap<String, Map<String, Object>>(0));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue) {
        return execute(className, methodSignature, initialRegisterToValue, new HashMap<String, Map<String, Object>>(0));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<Object> registerToValue, Map<String, Map<String, Object>> classNameToFieldValue) {
        return execute(className, methodSignature, registerToValue, classNameToFieldValue, Sets.newHashSet(className));
    }

    public static ExecutionGraph execute(String className, String methodSignature,
                    TIntObjectMap<Object> registerToValue, Map<String, Map<String, Object>> classNameToFieldValue,
                    Set<String> classNamesToStaticInit) {
        List<BuilderClassDef> classDefs = new ArrayList<BuilderClassDef>();
        for (String currentClassName : classNamesToStaticInit) {
            BuilderClassDef classDef = getBuilderClassDef(currentClassName);
            classDefs.add(classDef);
        }
        VirtualMachine vm = new VirtualMachine(classDefs);

        return execute(vm, className, methodSignature, registerToValue, classNameToFieldValue);
    }

    public static ExecutionGraph execute(VirtualMachine vm, String className, String methodSignature,
                    TIntObjectMap<Object> registerToValue, Map<String, Map<String, Object>> classNameToFieldValue) {
        String methodDescriptor = className + "->" + methodSignature;
        ExecutionContext ectx = getSetupExecutionContext(vm, methodDescriptor, registerToValue, classNameToFieldValue);
        ExecutionGraph graph = vm.execute(methodDescriptor, ectx);

        return graph;
    }

    public static BuilderClassDef getBuilderClassDef(String className) {
        return classNameToDef.get(className);
    }

    public static BuilderMethod getBuilderMethod(String className, String methodSignature) {
        String methodDescriptor = className + "->" + methodSignature;
        for (BuilderMethod method : getBuilderClassDef(className).getMethods()) {
            String currentDescriptor = ReferenceUtil.getMethodDescriptor(method);
            if (methodDescriptor.equals(currentDescriptor)) {
                return method;
            }
        }

        return null;
    }

    public static Map<String, BuilderClassDef> getClassNameToBuilderClassDef(String path) {
        File testDir = new File(TEST_DIRECTORY);
        String[] extensions = new String[] { "smali" };
        List<File> smaliFiles = new ArrayList<File>(FileUtils.listFiles(testDir, extensions, true));

        dexBuilder = DexBuilder.makeDexBuilder(Dexifier.DEFAULT_API_LEVEL);
        Map<String, BuilderClassDef> result = new HashMap<String, BuilderClassDef>();
        List<BuilderClassDef> classDefs;
        try {
            classDefs = Dexifier.dexifySmaliFiles(smaliFiles, dexBuilder);
            for (BuilderClassDef classDef : classDefs) {
                result.put(classDef.getType(), classDef);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1); // do not pass go, do not collect 200 monies
        }

        return result;
    }

    public static DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public static void testClassState(String className, String methodSignature,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        TIntObjectMap<Object> initialRegisterToValue = new TIntObjectHashMap<Object>();
        TIntObjectMap<Object> expectedRegisterToValue = new TIntObjectHashMap<Object>();
        testMethodAndClassState(className, methodSignature, initialRegisterToValue, expectedRegisterToValue,
                        classNameToInitialFieldValue, classNameToExpectedFieldValue, new HashSet<String>());
    }

    public static void testClassState(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        TIntObjectMap<Object> expectedRegisterToValue = new TIntObjectHashMap<Object>();
        Map<String, Map<String, Object>> classNameToInitialFieldValue = new HashMap<String, Map<String, Object>>(0);
        testMethodAndClassState(className, methodSignature, initialRegisterToValue, expectedRegisterToValue,
                        classNameToInitialFieldValue, classNameToExpectedFieldValue, new HashSet<String>());
    }

    public static void testClassState(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        TIntObjectMap<Object> expectedRegisterToValue = new TIntObjectHashMap<Object>();
        testMethodAndClassState(className, methodSignature, initialRegisterToValue, expectedRegisterToValue,
                        classNameToInitialFieldValue, classNameToExpectedFieldValue, new HashSet<String>());
    }

    public static void testMethodAndClassState(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue, TIntObjectMap<Object> expectedRegisterToValue,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue,
                    Set<String> classNamesToMakeAvailable) {
        Set<String> classNames = new HashSet<String>();
        classNames.add(className);
        classNames.addAll(classNameToInitialFieldValue.keySet());
        classNames.addAll(classNameToExpectedFieldValue.keySet());
        classNames.addAll(classNamesToMakeAvailable);
        ExecutionGraph graph = execute(className, methodSignature, initialRegisterToValue,
                        classNameToInitialFieldValue, classNames);
        assertNotNull(graph);

        for (int register : expectedRegisterToValue.keys()) {
            Object value = expectedRegisterToValue.get(register);
            Object consensus = graph.getTerminatingRegisterConsensus(register);

            testRegisterEquals(register, value, consensus);
        }

        for (String currentClassName : classNameToExpectedFieldValue.keySet()) {
            Map<String, Object> check = classNameToExpectedFieldValue.get(currentClassName);
            for (String fieldReference : check.keySet()) {
                Object checkValue = check.get(fieldReference);
                String fieldDescriptor = currentClassName + "->" + fieldReference;
                Object actualValue = graph.getTerminatingFieldConsensus(fieldDescriptor);

                testFieldEquals(fieldDescriptor, checkValue, actualValue);
            }
        }
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<Object> expectedRegisterToValue) {
        testMethodState(className, methodSignature, new TIntObjectHashMap<Object>(), expectedRegisterToValue);
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<Object> expectedRegisterToValue,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        TIntObjectMap<Object> initialRegisterToValue = new TIntObjectHashMap<Object>();
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = new HashMap<String, Map<String, Object>>(0);

        testMethodAndClassState(className, methodSignature, initialRegisterToValue, expectedRegisterToValue,
                        classNameToInitialFieldValue, classNameToExpectedFieldValue, new HashSet<String>());
    }

    public static void testMethodState(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue, TIntObjectMap<Object> expectedRegisterToValue) {
        testMethodAndClassState(className, methodSignature, initialRegisterToValue, expectedRegisterToValue,
                        new HashMap<String, Map<String, Object>>(0), new HashMap<String, Map<String, Object>>(0),
                        new HashSet<String>());
    }

    public static void testVisitation(String className, String methodSignature, int[] expectedVisitations) {
        testVisitation(className, methodSignature, new TIntObjectHashMap<Object>(), expectedVisitations);
    }

    public static void testVisitation(String className, String methodSignature,
                    TIntObjectMap<Object> initialRegisterToValue, int[] expectedVisitations) {
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
        // Note: order IS important!
        actualVisits.reverse();

        assertEquals(expectedVisits, actualVisits);
    }

    private static String getClassName(Object obj) {
        String result = "null";
        if (obj != null) {
            result = obj.getClass().getName();
        }

        return result;
    }

    private static ExecutionContext getSetupExecutionContext(VirtualMachine vm, String methodDescriptor,
                    TIntObjectMap<Object> registerToValue, Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        ExecutionContext ectx = vm.getRootExecutionContext(methodDescriptor);
        setupMethodState(ectx, registerToValue);
        setupClassStates(ectx, classNameToInitialFieldValue);

        return ectx;
    }

    private static void setupClassStates(ExecutionContext ectx, Map<String, Map<String, Object>> classNameToFieldValue) {
        for (String className : classNameToFieldValue.keySet()) {
            Map<String, Object> fieldToValue = classNameToFieldValue.get(className);
            ClassState cState = ectx.peekClassState(className);
            for (String fieldNameAndType : fieldToValue.keySet()) {
                Object value = fieldToValue.get(fieldNameAndType);
                cState.pokeField(fieldNameAndType, value);
            }
            ectx.initializeClass(className, cState, SideEffect.Level.NONE);
        }
    }

    private static void setupMethodState(ExecutionContext ectx, TIntObjectMap<Object> registerToValue) {
        MethodState mState = new MethodState(ectx, registerToValue.size());
        for (int register : registerToValue.keys()) {
            Object value = registerToValue.get(register);
            mState.assignRegister(register, value);
        }
        ectx.setMethodState(mState);
    }

    private static void testFieldEquals(String fieldDescriptor, Object value, Object consensus) {
        StringBuilder sb = new StringBuilder();
        sb.append(fieldDescriptor).append(" class(expected=").append(getClassName(value)).append(", consensus=")
        .append(getClassName(consensus)).append(")");
        String msg = sb.toString();

        testValueEquals(value, consensus, msg);
    }

    private static void testRegisterEquals(int register, Object value, Object consensus) {
        StringBuilder sb = new StringBuilder();
        sb.append("r").append(register).append(" class(expected=").append(getClassName(value)).append(", consensus=")
        .append(getClassName(consensus)).append(")");
        String msg = sb.toString();

        testValueEquals(value, consensus, msg);
    }

    private static void testValueEquals(Object value, Object consensus, String msg) {
        if (value != null) {
            assertNotNull(consensus);
        }

        if (value == null) {
            Assert.assertEquals(msg, value, consensus);
        } else if (value instanceof UnknownValue) {
            // Only checks type and value
            Assert.assertEquals(msg, value.toString(), consensus.toString());
        } else if (value.getClass().isArray()) {
            // If array, type is "Object", so need to use isArray() instead of instanceof
            boolean result = Objects.deepEquals(value, consensus);
            Assert.assertTrue(msg, result);
        } else if (value instanceof StringBuilder) {
            assertEquals(msg, value.toString(), consensus.toString());
        } else {
            Assert.assertEquals(msg, value, consensus);
        }
    }

}
