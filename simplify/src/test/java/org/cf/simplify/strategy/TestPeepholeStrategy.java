package org.cf.simplify.strategy;

import static org.junit.Assert.assertTrue;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.simplify.Main;
import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPeepholeStrategy {

    private static final String CLASS_NAME = "Lpeephole_optimizer_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    @Test
    public void testInvokeClassForNameWithKnownClassIsReplaced() {
        String methodName = "InvokeClassForNameWithKnownClass()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        PeepholeStrategy strategy = new PeepholeStrategy(mbgraph);
        TIntSet expected = new TIntHashSet(new int[] { 2 });
        for (int address : mbgraph.getAddresses()) {
            if (expected.contains(address)) {
                assertTrue(address + " can be peeped", strategy.canPeepClassForName(address));
            }
        }
    }

    @Test
    public void testInvokeClassForNameWithUnknownClassIsReplaced() {
        String methodName = "InvokeClassForNameWithUnknownClass()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        PeepholeStrategy strategy = new PeepholeStrategy(mbgraph);
        TIntSet expected = new TIntHashSet(new int[] { 2 });
        for (int address : mbgraph.getAddresses()) {
            if (expected.contains(address)) {
                assertTrue(address + " can be peeped", strategy.canPeepClassForName(address));
            }
        }
    }
}
