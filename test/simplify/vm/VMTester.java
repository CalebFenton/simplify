package simplify.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Dexifier;
import simplify.Main;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class VMTester {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String TEST_DIRECTORY = "resources/test/vm";
    private static final Map<String, BuilderClassDef> classNameToDef;

    static {
        File testDir = new File(TEST_DIRECTORY);
        String[] extensions = new String[] { "smali" };
        List<File> smaliFiles = new ArrayList<File>(FileUtils.listFiles(testDir, extensions, true));

        DexBuilder dexBuilder = DexBuilder.makeDexBuilder(Dexifier.API_LEVEL);
        classNameToDef = new HashMap<String, BuilderClassDef>();
        try {
            List<BuilderClassDef> classDefs = Dexifier.dexifySmaliFiles(smaliFiles, dexBuilder);
            for (BuilderClassDef classDef : classDefs) {
                classNameToDef.put(classDef.getType(), classDef);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1); // should really not happen (jinx!)
        }
    }

    public static ContextGraph execute(String className, String methodSignature) {
        return execute(className, methodSignature, new SparseArray<Object>(0), new HashMap<String, ClassContext>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, SparseArray<Object> initial) {
        return execute(className, methodSignature, initial, new HashMap<String, ClassContext>(0));
    }

    public static ContextGraph execute(String className, String methodSignature, SparseArray<Object> initial,
                    Map<String, ClassContext> classNameToContext) {
        BuilderClassDef classDef = classNameToDef.get(className);
        MethodContext ctx = MethodContext.build(initial);
        String methodDescriptor = className + "->" + methodSignature;

        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        for (String contextClassName : classNameToContext.keySet()) {
            ClassContext cctx = classNameToContext.get(contextClassName);
            vm.setClassContext(contextClassName, cctx);
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
            Assert.assertTrue("Address @" + address + " was visited. Expected " + wasExpected, wasExpected);
        }

    }

    public static void testState(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected) {
        testState(className, methodSignature, initial, expected, new HashMap<String, ClassContext>(0),
                        new HashMap<String, ClassContext>(0));
    }

    public static void testState(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected, Map<String, ClassContext> classNameToExpectedContext) {
        testState(className, methodSignature, initial, expected, new HashMap<String, ClassContext>(0),
                        classNameToExpectedContext);
    }

    public static void testState(String className, String methodSignature, SparseArray<Object> expected,
                    Map<String, ClassContext> classNameToInitialContext) {
        testState(className, methodSignature, expected, new SparseArray<Object>(0), classNameToInitialContext,
                        new HashMap<String, ClassContext>(0));
    }

    public static void testState(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected, Map<String, ClassContext> classNameToInitialContext,
                    Map<String, ClassContext> classNameToExpectedContext) {
        BuilderClassDef classDef = classNameToDef.get(className);
        MethodContext ctx = MethodContext.build(initial);
        String methodDescriptor = className + "->" + methodSignature;

        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        for (String contextClassName : classNameToInitialContext.keySet()) {
            ClassContext cctx = classNameToInitialContext.get(contextClassName);
            vm.setClassContext(contextClassName, cctx);
        }

        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        TIntList terminalAddresses = graph.getConnectedTerminatingAddresses();
        for (int i = 0; i < expected.size(); i++) {
            int register = expected.keyAt(i);
            Object value = expected.get(register);
            Object consensus = graph.getRegisterConsensus(terminalAddresses, register);

            testEquals(value, consensus, methodDescriptor, register);
        }

        for (String contextClassName : classNameToExpectedContext.keySet()) {
            ClassContext check = classNameToExpectedContext.get(contextClassName);
            ClassContext actual = vm.getClassContext(contextClassName);

            Assert.assertTrue(check + " should be " + actual, actual.equals(check));
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
            System.out.println("is array, so " + Arrays.toString((int[]) value) + " vs "
                            + Arrays.toString((int[]) consensus));
            // Type is "object" so can't use instanceof, but you knew that.
            boolean result = ArrayUtils.isEquals(value, consensus);
            Assert.assertTrue(msg, result);
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

    public static Map<String, ClassContext> buildClassNameToContext(Object... params) {
        Map<String, ClassContext> result = new HashMap<String, ClassContext>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            @SuppressWarnings("unchecked")
            ClassContext ctx = ClassContext.build((Map<String, Object>) params[i + 1]);
            result.put((String) params[i], ctx);
        }

        return result;
    }

    public static SparseArray<Object> buildRegisterState(Object... params) {
        SparseArray<Object> result = new SparseArray<Object>(params.length / 2);
        for (int i = 0; i < params.length; i += 2) {
            result.append((int) params[i], params[i + 1]);
        }

        return result;
    }
}
