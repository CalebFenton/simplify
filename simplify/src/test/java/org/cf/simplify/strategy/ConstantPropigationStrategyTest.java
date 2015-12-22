package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import gnu.trove.map.TIntObjectMap;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class ConstantPropigationStrategyTest {

    public static class UnitTests {

        @Test
        public void testConstantizablesHandlesNull() {
            ExecutionGraphManipulator graph = mock(ExecutionGraphManipulator.class);
            ConstantBuilder builder = mock(ConstantBuilder.class);
            BuilderInstruction instruction = mock(BuilderInstruction.class,
                            withSettings().extraInterfaces(OneRegisterInstruction.class));

            ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(graph);
            strategy.setDependancies(builder);

            when(graph.getAddresses()).thenReturn(new int[] { 1 });
            when(graph.getInstruction(1)).thenReturn(null).thenReturn(instruction);
            when(graph.wasAddressReached(1)).thenReturn(true);
            when(graph.getOp(1)).thenReturn(null);
            when(((OneRegisterInstruction) instruction).getRegisterA()).thenReturn(2);
            when(graph.getRegisterConsensus(1, 2)).thenReturn(null);
            when(builder.canConstantizeOp(null)).thenReturn(true);

            boolean changesMade = strategy.perform();
            assertFalse(changesMade);
        }
    }

    public static class WithKnownValues {

        @Test
        public void testAddInt2AddrConstantizesToExpectedInstruction() {
            String methodName = "AddInt2Addr()V";
            ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName, 0, 3, "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(6, 0);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void testAGetIsConstable() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName, 0, new int[] { 0, 7 }, "[I", 1, 1,
                            "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(7, 0);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void testMoveOpIsWithConst16ConstantizesToExpectedInstruction() {
            int value = 0x42;
            String methodName = "MoveV0IntoV1()V";
            ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName, 0, value, "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(value, 1);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void testNonDeterministicallyExecuteConstableOpConstantizesToExpectedInstruction() {
            String methodName = "NonDeterministicallyStaticGetIntegerMaxValue(I)V";
            ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName, 1, new UnknownValue(), "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, manipulator, 2);
        }

        @Test
        public void testSGetIsConstable() {
            String methodName = "StaticGetIntegerMaxValue()V";
            ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName);
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, manipulator, 0);
        }
    }

    public static class WithUnknownValues {

        @Test
        public void testAddInt2AddrDoesNotConstantize() {
            String methodName = "AddInt2Addr()V";
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testAGetWithUnknownArrayDoesNotConstantize() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "[I", 1, 0, "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testAGetWithUnknownIndexDoesNotConstantize() {
            String methodName = "ArrayGetFromV0AtV1ToV0()V";
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new int[] { 0, 7 }, "[I", 1,
                            new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testMoveOpDoesNotConstantize() {
            String methodName = "MoveV0IntoV1()V";
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void testIntegerValueOfDoesNotConstantize() {
            String methodName = "IntegerValueOf()Ljava/lang/Integer;";
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 1, "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            assertEquals(before.toSmali(), after.toSmali());
        }
    }

    private static final String CLASS_NAME = "Lconstant_propigation_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantPropigationStrategyTest.class.getSimpleName());

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(args);

        return getOptimizedGraph(methodName, initial);
    }

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, TIntObjectMap<HeapItem> initial) {
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        ConstantPropigationStrategy strategy = new ConstantPropigationStrategy(manipulator);
        strategy.perform();

        return manipulator;
    }

    private static void testEquals(BuilderInstruction expected, ExecutionGraphManipulator manipulator, int address) {
        BuilderInstruction actual = manipulator.getInstruction(address);

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

}
