package org.cf.simplify.strategy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
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
public class TestConstantPropigationStrategy {

    public static class WithKnownValues {
        @Test
        public void testBinaryMathOpIsConstable() {
            String methodSignature = "BinaryMathOp()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 0);
        }

        @Test
        public void testNonDeterministicallyExecuteConstableOpIsConstable() {
            String methodSignature = "NonDeterministicallyExecuteConstableOp()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 2); // sget
        }

        @Test
        public void testMoveOpIsWithConst16IsConstable() {
            String methodSignature = "MoveV0IntoV1()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x42);
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 0);
        }

        @Test
        public void testSGetIsConstable() {
            String methodSignature = "StaticGetWhitelistedClassMember()V";
            TIntObjectMap<Object> initial = new TIntObjectHashMap<Object>();
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 0);
        }

        @Test
        public void testAGetIsConstable() {
            String methodSignature = "ArrayGet()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[5], 1, 0);
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 0);
        }

        @Test
        public void testMoveOpIsWithConst4IsConstable() {
            String methodSignature = "MoveV0IntoV1()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x7);
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy, 0);
        }
    }

    public static class WithUnknownValues {
        @Test
        public void testBinaryMathOpIsNotConstable() {
            String methodSignature = "BinaryMathOp()V";
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature, initial);
            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);

            assertAddressesConstantizable(mbgraph, strategy);
        }
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestConstantPropigationStrategy.class.getSimpleName());

    static final String CLASS_NAME = "Lconstant_builder_test;";

    static void assertAddressesConstantizable(MethodBackedGraph mbgraph, ConstantPropigationStrategy strategy,
                    int... expectedAddresses) {
        TIntSet expected = new TIntHashSet(expectedAddresses);
        for (int address : mbgraph.getAddresses()) {
            boolean canConstantize = strategy.canConstantizeAddress(address);
            if (expected.contains(address)) {
                assertTrue("address " + address + " should be constantizable", canConstantize);
            } else {
                assertFalse("address " + address + " should NOT be constantizable", canConstantize);
            }
        }
    }

}
