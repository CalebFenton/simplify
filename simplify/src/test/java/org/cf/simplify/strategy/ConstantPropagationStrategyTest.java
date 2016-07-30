package org.cf.simplify.strategy;

import org.cf.simplify.ConstantBuilder;
import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.WideLiteralInstruction;
import org.jf.dexlib2.iface.reference.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(Enclosed.class)
public class ConstantPropagationStrategyTest {

    private static final String CLASS_NAME = "Lconstant_propigation_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ConstantPropagationStrategyTest.class.getSimpleName());

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }

        return getOptimizedGraph(methodName, initial);
    }

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, VMState initial) {
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        ConstantPropagationStrategy strategy = new ConstantPropagationStrategy(manipulator);
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

    public static class KnownValues {

        @Test
        public void addInt2AddrConstantizesToExpectedInstruction() {
            ExecutionGraphManipulator manipulator = getOptimizedGraph("addInt2Addr()V", 0, 3, "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(6, 0);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void canConstantizeAGet() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph("arrayGetFromV0AtV1ToV0()V", 0, new int[] { 0, 7 }, "[I", 1, 1, "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(7, 0);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void moveOpIsWithConst16ConstantizesToExpectedInstruction() {
            int value = 0x42;
            ExecutionGraphManipulator manipulator = getOptimizedGraph("moveV0IntoV1()V", 0, value, "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(value, 1);

            testEquals(expected, manipulator, 0);
        }

        @Test
        public void nonDeterministicallyExecuteConstableOpConstantizesToExpectedInstruction() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph("nonDeterministicallyStaticGetIntegerMaxValue(I)V", 1, new UnknownValue(), "I");
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, manipulator, 2);
        }

        @Test
        public void canConstantizeSGet() {
            ExecutionGraphManipulator manipulator = getOptimizedGraph("getStaticIntegerMaxValue()V");
            BuilderInstruction expected = ConstantBuilder.buildConstant(Integer.MAX_VALUE, 0);

            testEquals(expected, manipulator, 0);
        }
    }

    public static class UnitTests {

        @Test
        public void constantizableHandlesNull() {
            ExecutionGraphManipulator graph = mock(ExecutionGraphManipulator.class);
            ConstantBuilder builder = mock(ConstantBuilder.class);
            BuilderInstruction instruction =
                    mock(BuilderInstruction.class, withSettings().extraInterfaces(OneRegisterInstruction.class));

            ConstantPropagationStrategy strategy = new ConstantPropagationStrategy(graph);
            strategy.setDependencies(builder);

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

    public static class UnknownValues {

        private VMState initial;

        @Before
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void canNotConstantizeAddInt2Addr() {
            String methodName = "addInt2Addr()V";
            initial.setRegisters(0, new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void canNotConstantizeAGetWithUnknownArray() {
            String methodName = "arrayGetFromV0AtV1ToV0()V";
            initial.setRegisters(0, new UnknownValue(), "[I", 1, 0, "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void canNotConstantizeAGetWithUnknownIndex() {
            String methodName = "arrayGetFromV0AtV1ToV0()V";
            initial.setRegisters(0, new int[] { 0, 7 }, "[I", 1, new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }

        @Test
        public void canNotConstantizeIntegerValueOf() {
            String methodName = "integerValueOf()Ljava/lang/Integer;";
            initial.setRegisters(0, 1, "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            assertEquals(before.toSmali(), after.toSmali());
        }

        @Test
        public void canNotConstantizeMoveOp() {
            String methodName = "moveV0IntoV1()V";
            initial.setRegisters(0, new UnknownValue(), "I");
            ExecutionGraphManipulator before = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
            ExecutionGraphManipulator after = getOptimizedGraph(methodName, initial);

            testEquals(before.getInstruction(0), after, 0);
        }
    }

}
