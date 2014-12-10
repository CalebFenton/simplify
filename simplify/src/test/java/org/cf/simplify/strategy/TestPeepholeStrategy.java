package org.cf.simplify.strategy;

import static org.junit.Assert.assertTrue;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.simplify.Main;
import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: these tests are bad, even for bad tests.
// need to think about some test harness / framework for testing optimizers
// just initial + expected + visited isn't going to cut it
// also test string init
@RunWith(Enclosed.class)
public class TestPeepholeStrategy {

    private static final String CLASS_NAME = "Lpeephole_optimizer_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    public static class TestPeepClassForName {
        @Test
        public void testInvokeClassForNameForImaginaryClassIsReplaced() {
            String methodName = "ClassForName()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "com.funky.unknown.class");
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName, initial);
            PeepholeStrategy strategy = new PeepholeStrategy(mbgraph);
            TIntSet expected = new TIntHashSet(new int[] { 1 });
            for (int address : mbgraph.getAddresses()) {
                if (expected.contains(address)) {
                    assertTrue(address + " can be peeped", strategy.canPeepClassForName(address));
                }
            }

            strategy.perform();
        }

        // known class
        // local class
        // unknownvalue
    }

    public static class TestPeepMethodInvoke {

        // static
        // -unknown method - still know the name, make sure it's emulated correctly
        // -unknown target - still optimizes, still know the register
        // -unknown parameters - still optimizes, know the registers
        // _local private
        // -local non-private
        // -remote private - can't eliminate
        // -0 registers
        // -1 registers, possibly bump up .locals size
        // -2 registers
        // -6 registers (/range), all low enough
        // -6 registers, not all low enough, /wide?
        // virtual
        // -same as static
        // direct
        // -same as static
        @Test
        public void testInvokeMethodInvokeForKnownMethodAndTargetAndParameters() {
            String methodName = "MethodInvokeForKnownMethodAndTargetAndParameters()V";
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
            PeepholeStrategy strategy = new PeepholeStrategy(mbgraph);
            TIntSet expected = new TIntHashSet(new int[] { 28 });
            for (int address : mbgraph.getAddresses()) {
                if (expected.contains(address)) {
                    assertTrue(address + " can be peeped", strategy.canPeepMethodInvoke(address));
                }
            }
        }

    }

    // public static class TestStringInit {
    //
    // }

}
