package simplify.vm;

import static org.junit.Assert.assertTrue;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.junit.Assert;

import simplify.Dexifier;
import simplify.Main;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class VMTester {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String TEST_DIRECTORY = "resources/test/vm";
    private static final int MAX_NODE_VISITS = 100;
    private static final int MAX_CALL_DEPTH = 10;

    private static final Map<String, BuilderClassDef> classNameToDef = getClassNameToBuilderClassDef();

    public static Map<String, BuilderClassDef> getClassNameToBuilderClassDef() {
        return getClassNameToBuilderClassDef(TEST_DIRECTORY);
    }

    public static Map<String, BuilderClassDef> getClassNameToBuilderClassDef(String path) {
        File testDir = new File(TEST_DIRECTORY);
        String[] extensions = new String[] { "smali" };
        List<File> smaliFiles = new ArrayList<File>(FileUtils.listFiles(testDir, extensions, true));

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(Dexifier.API_LEVEL);
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
        return execute(className, methodSignature, new SparseArray<Object>(0),
                        new HashMap<String, Map<String, Object>>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, SparseArray<Object> initial) {
        return execute(className, methodSignature, initial, new HashMap<String, Map<String, Object>>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, SparseArray<Object> initial,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        BuilderClassDef classDef = classNameToDef.get(className);
        MethodContext ctx = MethodContext.build(initial);
        String methodDescriptor = className + "->" + methodSignature;

        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        for (String contextClassName : classNameToInitialFieldValue.keySet()) {
            ClassContext cctx = vm.peekClassContext(contextClassName);
            Map<String, Object> fieldNameToValue = classNameToInitialFieldValue.get(contextClassName);
            for (String fieldReference : fieldNameToValue.keySet()) {
                Object value = fieldNameToValue.get(fieldReference);
                cctx.pokeField(fieldReference, value);
            }
        }

        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        return graph;
    }

    public static void testVisitation(String className, String methodSignature, int[] expected) {
        testVisitation(className, methodSignature, new SparseArray<Object>(), expected);
    }

    public static void testVisitation(String className, String methodSignature, SparseArray<Object> initial,
                    int[] expected) {
        ContextGraph graph = VMTester.execute(className, "TestPackedSwitch()V", initial);
        TIntList addresses = graph.getAddresses();
        TIntList expectedVisits = new TIntArrayList(expected);
        for (int i = 0; i < addresses.size(); i++) {
            int address = addresses.get(i);
            if (!graph.wasAddressReached(address)) {
                continue;
            }
            boolean wasExpected = expectedVisits.contains(address);
            // log.info("visited @" + address);
            assertTrue("Address @" + address + " was visited. Expected " + wasExpected, wasExpected);
        }

    }

    public static void testState(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected) {
        testState(className, methodSignature, initial, expected, new HashMap<String, Map<String, Object>>(0),
                        new HashMap<String, Map<String, Object>>(0));
    }

    // public static void testState(String className, String methodSignature, SparseArray<Object> initial,
    // SparseArray<Object> expected, Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
    // testState(className, methodSignature, initial, expected, new HashMap<String, Map<String, Object>>(0),
    // classNameToExpectedFieldValue);
    // }

    public static void testExpectedMethodState(String className, String methodSignature, SparseArray<Object> expected,
                    Map<String, Map<String, Object>> classNameToInitialFieldValue) {
        SparseArray<Object> initial = new SparseArray<Object>(0);
        Map<String, Map<String, Object>> classNameToExpectedFieldValue = new HashMap<String, Map<String, Object>>(0);
        testState(className, methodSignature, initial, expected, classNameToInitialFieldValue,
                        classNameToExpectedFieldValue);
    }

    public static void testExpectedClassState(String className, String methodSignature, SparseArray<Object> initial,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        SparseArray<Object> expected = new SparseArray<Object>(0);
        Map<String, Map<String, Object>> classNameToInitialFieldValue = new HashMap<String, Map<String, Object>>(0);
        testState(className, methodSignature, initial, expected, classNameToInitialFieldValue,
                        classNameToExpectedFieldValue);
    }

    public static void testState(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected, Map<String, Map<String, Object>> classNameToInitialFieldValue,
                    Map<String, Map<String, Object>> classNameToExpectedFieldValue) {
        BuilderClassDef classDef = classNameToDef.get(className);
        MethodContext ctx = MethodContext.build(initial);
        String methodDescriptor = className + "->" + methodSignature;

        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), MAX_NODE_VISITS, MAX_CALL_DEPTH);
        for (String contextClassName : classNameToInitialFieldValue.keySet()) {
            ClassContext cctx = vm.peekClassContext(contextClassName);
            Map<String, Object> fieldNameToValue = classNameToInitialFieldValue.get(contextClassName);
            for (String fieldReference : fieldNameToValue.keySet()) {
                Object value = fieldNameToValue.get(fieldReference);
                cctx.pokeField(fieldReference, value);
            }
        }

        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        // TODO: use getTerminatingRegisterConsensus
        TIntList terminalAddresses = graph.getConnectedTerminatingAddresses();
        for (int i = 0; i < expected.size(); i++) {
            int register = expected.keyAt(i);
            Object value = expected.get(register);
            Object consensus = graph.getRegisterConsensus(terminalAddresses, register);

            testEquals(value, consensus, methodDescriptor, register);
        }

        for (String contextClassName : classNameToExpectedFieldValue.keySet()) {
            Map<String, Object> check = classNameToExpectedFieldValue.get(contextClassName);
            ClassContext actual = vm.peekClassContext(contextClassName);

            for (String fieldReference : check.keySet()) {
                Object checkValue = check.get(fieldReference);
                Object actualValue = actual.peekField(fieldReference);
                Assert.assertTrue(fieldReference + "(" + checkValue + ") should equal " + actualValue,
                                checkValue.equals(actualValue));
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
        String msg = methodDescriptor + ", r" + register + " = " + consensus + "(" + getClassName(consensus)
                        + "), should be " + value + "(" + getClassName(value) + ")";

        if (value == null) {
            Assert.assertTrue(msg, value == consensus);
        } else if (value instanceof UnknownValue) {
            // Checking type and value should be enough.
            Assert.assertTrue(msg, value.toString().equals(consensus.toString()));
        } else if (value.getClass().isArray()) {
            // Type is "object" so can't use instanceof, but you knew that.
            boolean result = ArrayUtils.isEquals(value, consensus);
            Assert.assertTrue(msg, result);
        } else if (value instanceof StringBuilder) {
            assertTrue(msg, value.toString().equals(consensus.toString()));
        } else {
            Assert.assertTrue(msg, value.equals(consensus));
        }
    }

    public static void test(String className, String methodSignature, SparseArray<Object> expected) {
        testState(className, methodSignature, new SparseArray<Object>(), expected);
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

    // public static Map<String, ClassContext> buildClassNameToContext(Object... params) {
    // Map<String, ClassContext> result = new HashMap<String, ClassContext>(params.length / 2);
    // for (int i = 0; i < params.length; i += 2) {
    // @SuppressWarnings("unchecked")
    // ClassContext ctx = ClassContext.build((Map<String, Object>) params[i + 1]);
    // result.put((String) params[i], ctx);
    // }
    //
    // return result;
    // }

    public static SparseArray<Object> buildRegisterState(Object... params) {
        SparseArray<Object> result = new SparseArray<Object>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            result.append((int) params[i], params[i + 1]);
        }

        return result;
    }
}
