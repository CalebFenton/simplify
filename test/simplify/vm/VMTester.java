package simplify.vm;

import gnu.trove.list.TIntList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.ArrayUtils;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;

import simplify.Dexifier;
import util.SparseArray;

public class VMTester {

    private static final String TEST_DIRECTORY = "resources/test/vm";

    private static final Map<String, BuilderClassDef> classNameToDef;

    static {
        File testDir = new File(TEST_DIRECTORY);
        List<File> smaliFiles = new ArrayList<File>();
        smaliFiles.addAll(Arrays.asList(testDir.listFiles()));

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

    public static void executeAndEnsureContextState(String className, String methodSignature,
                    SparseArray<Object> registerState) {
        BuilderClassDef classDef = classNameToDef.get(className);
        VirtualMachine vm = new VirtualMachine(Arrays.asList(classDef), 100, 2);

        String methodDescriptor = className + "->" + methodSignature;

        ContextGraph graph = vm.execute(methodDescriptor);

        TIntList terminalAddresses = graph.getConnectedTerminatingAddresses();
        for (int i = 0; i < registerState.size(); i++) {
            int register = registerState.keyAt(i);
            Object value = registerState.get(register);
            Object consensus = graph.getRegisterConsensus(terminalAddresses, register);
            String msg = methodDescriptor + ", r" + register + " = " + consensus + "(" + consensus.getClass().getName()
                            + "), should be " + value + "(" + value.getClass().getName() + ")";

            // Type is "object" so can't use instanceof, but you knew that.
            if (value.getClass().isArray()) {
                boolean result = ArrayUtils.isEquals(value, consensus);
                Assert.assertTrue(msg, result);
            } else {
                Assert.assertTrue(msg, value.equals(consensus));
            }
        }
    }

}
