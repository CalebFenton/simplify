package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.Reference;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestConstantPropigationStrategy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestConstantPropigationStrategy.class.getSimpleName());

    private static final String CLASS_NAME = "Lconstant_propigation_strategy_test;";

    private static MethodBackedGraph getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(args);
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName, initial);
        ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(mbgraph);
        strategy.perform();

        return mbgraph;
    }

    private static void testEquals(BuilderInstruction expected, MethodBackedGraph mbgraph, int address) {
        BuilderInstruction actual = mbgraph.getInstruction(address);

        assertEquals(expected.getOpcode(), actual.getOpcode());

        if (expected instanceof OneRegisterInstruction) {
            int expectedRegister = ((OneRegisterInstruction) expected).getRegisterA();
            int actualRegister = ((OneRegisterInstruction) actual).getRegisterA();

            assertEquals(expectedRegister, actualRegister);
        }

        if (expected instanceof NarrowLiteralInstruction) {
            int expectedLiteral = ((NarrowLiteralInstruction) expected).getNarrowLiteral();
            int actualLiteral = ((NarrowLiteralInstruction) actual).getNarrowLiteral();

            assertEquals(expectedLiteral, actualLiteral);
        }

        if (expected instanceof WideLiteralInstruction) {
            long expectedLiteral = ((WideLiteralInstruction) expected).getWideLiteral();
            long actualLiteral = ((WideLiteralInstruction) actual).getWideLiteral();

            assertEquals(expectedLiteral, actualLiteral);
        }

        if (expected instanceof ReferenceInstruction) {
            Reference expectedRef = ((ReferenceInstruction) expected).getReference();
            Reference actualRef = ((ReferenceInstruction) actual).getReference();

            assertEquals(expectedRef, actualRef);
        }
    }

    @RunWith(MockitoJUnitRunner.class)
    public static class UnitTests {
        @Test
        public void testConstantizablesHandlesNull() {

            // Mocks
            MethodBackedGraph mockmbGraph = mock(MethodBackedGraph.class);
            ConstantBuilder constantBuilder = mock(ConstantBuilder.class);
            BuilderInstruction mockBI = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(OneRegisterInstruction.class));

            // Object underTest
            ConstantPropigationStrategy underTest = new ConstantPropigationStrategy(mockmbGraph);
            underTest.setDependancies(constantBuilder);

            // Actions to mock to create previous bug
            when(mockmbGraph.getAddresses()).thenReturn(new int[] { 1 });
            when(mockmbGraph.getInstruction(1)).thenReturn(null).thenReturn(mockBI);
            when(mockmbGraph.wasAddressReached(1)).thenReturn(true);
            when(mockmbGraph.getOp(1)).thenReturn(null);
            when(((OneRegisterInstruction) mockBI).getRegisterA()).thenReturn(2);
            when(mockmbGraph.getRegisterConsensus(1, 2)).thenReturn(null);
            when(constantBuilder.canConstantizeOp(null)).thenReturn(true);

            // Should now return that no changes have been made opposed to crash
            assertFalse(underTest.perform());
        }
    }

    public static class WithKnownValues {
        @Test
        public void testAddInt2AddrConstantizesToExpectedInstruction() {
            String methodName = "AddInt2Addr()V";
            MethodBackedGraph mbgraph = getOptimizedGraph(methodName, 0, 3);
            BuilderInstruction expected = ConstantBuilder.buildConstant(6, 0);

            testEquals(expected, mbgraph, 0);
        }

        @Test
        public void testNonDeterministicallyExecuteConstableOpConstantizesToExpectedInstruction() {
            String methodName = "NonDeterministicallyStaticGetIntegerMaxValue(I)V";
            MethodBackedGraph mbgraph = getOptimizedGraph(methodName, 1, new UnknownValue("I"));
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, mbgraph, 2);
        }

        @Test
        public void testMoveOpIsWithConst16ConstantizesToExpectedInstruction() {
            int value = 0x42;
            String methodName = "MoveV0IntoV1()V";
            MethodBackedGraph mbgraph = getOptimizedGraph(methodName, 0, value);
            BuilderInstruction expected = ConstantBuilder.buildConstant(value, 1);

            testEquals(expected, mbgraph, 0);
        }

        @Test
        public void testSGetIsConstable() {
            String methodName = "StaticGetIntegerMaxValue()V";
            MethodBackedGraph mbgraph = getOptimizedGraph(methodName);
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, mbgraph, 0);
        }

        @Test
        public void testAGetIsConstable() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            MethodBackedGraph mbgraph = getOptimizedGraph(methodName, 0, new int[] { 0, 7 }, 1, 1);
            BuilderInstruction expected = ConstantBuilder.buildConstant(7, 0);

            testEquals(expected, mbgraph, 0);
        }
    }

    public static class WithUnknownValues {
        @Test
        public void testAddInt2AddrDoesNotConstantize() {
            String methodName = "AddInt2Addr()V";
            MethodBackedGraph before = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
            MethodBackedGraph after = getOptimizedGraph(methodName, 0, new UnknownValue("I"));

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testMoveOpDoesNotConstantize() {
            UnknownValue value = new UnknownValue("I");
            String methodName = "MoveV0IntoV1()V";
            MethodBackedGraph before = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
            MethodBackedGraph after = getOptimizedGraph(methodName, 0, value);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testAGetWithUnknownIndexDoesNotConstantize() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            MethodBackedGraph before = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
            MethodBackedGraph after = getOptimizedGraph(methodName, 0, new int[] { 0, 7 }, 1, new UnknownValue("I"));

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testAGetWithUnknownArrayDoesNotConstantize() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            MethodBackedGraph before = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
            MethodBackedGraph after = getOptimizedGraph(methodName, 0, new UnknownValue("[I"), 1, 0);

            testEquals(before.getInstruction(0), after, 0);
        }
    }

}
