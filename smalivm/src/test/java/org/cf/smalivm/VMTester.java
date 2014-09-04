package org.cf.smalivm;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.cf.smalivm.context.ClassContext;
import org.cf.smalivm.context.ContextGraph;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Dexifier;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.junit.Assert;

public class VMTester {

    private static final String TEST_DIRECTORY = "resources/test";
    private static final int MAX_NODE_VISITS = 100;
    private static final int MAX_CALL_DEPTH = 10;

    private static final Map<String, BuilderClassDef> classNameToDef = buildClassNameToBuilderClassDef();

    private static DexBuilder dexBuilder;

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

    public static Map<String, BuilderClassDef> buildClassNameToBuilderClassDef() {
        return getClassNameToBuilderClassDef(TEST_DIRECTORY);
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

    public static ContextGraph execute(String className, String methodSignature) {
        return execute(className, methodSignature, new TIntObjectHashMap<Object>(),
                        new HashMap<String, Map<String, Object>>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, TIntObjectMap<Object> initial) {
        return execute(className, methodSignature, initial, new HashMap<String, Map<String, Object>>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, TIntObjectMap<Object> initial,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        BuilderClassDef classDef = getBuilderClassDef(className);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        vm.setupClassContext(classNameToInitialFieldValue);

        String methodDescriptor = className + "->" + methodSignature;
        MethodContext ctx = MethodContext.build(initial);
        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        return graph;
    }

    public static void testVisitation(String className, String methodSignature, int[] expected) {
        testVisitation(className, methodSignature, new TIntObjectHashMap<Object>(), expected);
    }

    public static void testVisitation(String className, String methodSignature, TIntObjectMap<Object> initial,
                    int[] expected) {
        ContextGraph graph = VMTester.execute(className, methodSignature, initial);
        int[] addresses = graph.getAddresses().toArray();
        TIntList expectedVisits = new TIntArrayList(expected);
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

    public static void testState(String className, String methodSignature, TIntObjectMap<Object> initial,
                    TIntObjectMap<Object> expected) {
        testState(className, methodSignature, initial, expected, new HashMap<String, Map<String, Object>>(0),
                        new HashMap<String, Map<String, Object>>(0));
    }

    public static void testExpectedMethodState(String className, String methodSignature,
                    TIntObjectMap<Object> expected, Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        TIntObjectMap<Object> initial = new TIntObjectHashMap<Object>();
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = new HashMap<String, Map<String, Object>>(0);
        testState(className, methodSignature, initial, expected, classNameToInitialFieldValue,
                        classNameToExpectedFieldValue);
    }

    public static void testExpectedClassState(String className, String methodSignature, TIntObjectMap<Object> initial,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        TIntObjectMap<Object> expected = new TIntObjectHashMap<Object>();
        Map<String, Map<String, Object>> classNameToInitialFieldValue = new HashMap<String, Map<String, Object>>(0);
        testState(className, methodSignature, initial, expected, classNameToInitialFieldValue,
                        classNameToExpectedFieldValue);
    }

    public static void testState(String className, String methodSignature, TIntObjectMap<Object> initial,
                    TIntObjectMap<Object> expected, Map<String, Map<String, Object>> classNameToInitialFieldValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        BuilderClassDef classDef = getBuilderClassDef(className);
        MethodContext ctx = MethodContext.build(initial);
        String methodDescriptor = className + "->" + methodSignature;

        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), MAX_NODE_VISITS, MAX_CALL_DEPTH);
        for (String contextClassName : classNameToInitialFieldValue.keySet()) {
            ClassContext cctx = vm.getStaticClassContext(contextClassName);
            Map<String, Object> fieldNameToValue = classNameToInitialFieldValue.get(contextClassName);
            for (String fieldReference : fieldNameToValue.keySet()) {
                Object value = fieldNameToValue.get(fieldReference);
                cctx.pokeField(fieldReference, value);
            }
        }

        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        // TODO: use getTerminatingRegisterConsensus
        TIntList terminalAddresses = graph.getConnectedTerminatingAddresses();
        for (int register : expected.keys()) {
            Object value = expected.get(register);
            Object consensus = graph.getRegisterConsensus(terminalAddresses, register);

            testEquals(value, consensus, methodDescriptor, register);
        }

        for (String contextClassName : classNameToExpectedFieldValue.keySet()) {
            Map<String, Object> check = classNameToExpectedFieldValue.get(contextClassName);
            ClassContext actual = vm.getStaticClassContext(contextClassName);

            for (String fieldReference : check.keySet()) {
                Object checkValue = check.get(fieldReference);
                Object actualValue = actual.peekField(fieldReference);
                Assert.assertEquals(fieldReference, checkValue, actualValue);
            }
        }
    }

    private static String getClassName(Object obj) {
        String result;
        if (obj == null) {
            result = "null";
        } else {
            result = obj.getClass().getName();
        }
        return result;
    }

    private static void testEquals(Object value, Object consensus, String methodDescriptor, int register) {
        StringBuilder sb = new StringBuilder();
        sb.append("r").append(register).append(" class(expected=").append(getClassName(value)).append(", consensus=")
                        .append(getClassName(consensus)).append(")");
        String msg = sb.toString();

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

    public static void test(String className, String methodSignature, TIntObjectMap<Object> expected) {
        testState(className, methodSignature, new TIntObjectHashMap<Object>(), expected);
    }

    public static Map<String, Object> buildFieldToValue(Object... params) {
        Map<String, Object> result = new HashMap<String, Object>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            result.put((String) params[i], params[i + 1]);
        }

        return result;
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

    public static TIntObjectMap<Object> buildRegisterState(Object... params) {
        TIntObjectMap<Object> result = new TIntObjectHashMap<Object>();
        for (int i = 0; i < params.length; i += 2) {
            result.put((int) params[i], params[i + 1]);
        }

        return result;
    }

    public static DexBuilder getDexBuilder() {
        return dexBuilder;
    }
}
