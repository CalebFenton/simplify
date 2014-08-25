package org.cf.simplify;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Logger;

import org.cf.simplify.Main;
import org.cf.simplify.PeepholeOptimizer;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Test;

public class TestPeepholeOptimizer {

    private static final String CLASS_NAME = "Lpeephole_optimizer_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestInvokeClassForNameWithUnknownClassIsReplaced() {
        String methodName = "InvokeClassForNameWithUnknownClass()V";
        PeepholeOptimizer po = OptimizerTester.getPeepholeOptimizer(CLASS_NAME, methodName);
        TIntObjectMap<BuilderInstruction> found = po.getClassForNameAddressToReplacementMap();
        TIntSet expected = new TIntHashSet(new int[] { 2 });

        assertEquals(found.keySet(), expected);
    }

    @Test
    public void TestInvokeClassForNameWithKnownClassIsReplaced() {
        String methodName = "InvokeClassForNameWithKnownClass()V";
        PeepholeOptimizer po = OptimizerTester.getPeepholeOptimizer(CLASS_NAME, methodName);
        TIntObjectMap<BuilderInstruction> found = po.getClassForNameAddressToReplacementMap();
        TIntSet expected = new TIntHashSet(new int[] { 2 });

        assertEquals(found.keySet(), expected);
    }
}
