package org.cf.simplify.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestConstantBuilder {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestConstantBuilder.class.getSimpleName());

    static final String CLASS_NAME = "Lconstant_builder_test;";

    public static class WithKnownValues {
        @Test
        public void TestBinaryMathOpIsConstantized() {
            String methodSignature = "BinaryMathOp()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);
            assertAddressesConstantizable(mbgraph, strategy, 0);
        }
    }

    public static class WithUnknownValues {
        @Test
        public void TestBinaryMathOpIsNotConstantized() {
            String methodSignature = "BinaryMathOp()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);
            assertAddressesConstantizable(mbgraph, strategy);
        }
    }

    static void assertAddressesConstantizable(MethodBackedGraph mbgraph, ConstantPropigationStrategy strategy,
                    int... expectedAddresses) {
        TIntSet expected = new TIntHashSet(expectedAddresses);
        for (int address : mbgraph.getAddresses().toArray()) {
            boolean canConstantize = strategy.canConstantizeAddress(address);
            if (expected.contains(address)) {
                assertTrue("address " + address + " should be constantizable", canConstantize);
            } else {
                assertFalse("address " + address + " should NOT be constantizable", canConstantize);
            }
        }
    }
}
