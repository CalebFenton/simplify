package simplify.vm;

import gnu.trove.list.TIntList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Dexifier;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class VMTester {

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
            System.exit(-1); // should really not happen
        }
    }

    public static void test(String className, String methodSignature, SparseArray<Object> initial,
                    SparseArray<Object> expected) {
        BuilderClassDef classDef = classNameToDef.get(className);
        MethodContext ctx = MethodContext.buildFromRegisterState(initial);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);
        String methodDescriptor = className + "->" + methodSignature;

        ContextGraph graph = vm.execute(methodDescriptor, ctx);

        TIntList terminalAddresses = graph.getConnectedTerminatingAddresses();
        for (int i = 0; i < expected.size(); i++) {
            int register = expected.keyAt(i);
            Object value = expected.get(register);
            Object consensus = graph.getRegisterConsensus(terminalAddresses, register);

            testEquals(value, consensus, methodDescriptor, register);
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
            Assert.assertTrue(msg, value.toString().equals(consensus.toString()));
        } else if (value.getClass().isArray()) {
            // Type is "object" so can't use instanceof, but you knew that.
            boolean result = ArrayUtils.isEquals(value, consensus);
            Assert.assertTrue(msg, result);
        } else {
            Assert.assertTrue(msg, value.equals(consensus));
        }
    }

    public static void test(String className, String methodSignature, SparseArray<Object> expected) {
        test(className, methodSignature, new SparseArray<Object>(), expected);
    }
}
