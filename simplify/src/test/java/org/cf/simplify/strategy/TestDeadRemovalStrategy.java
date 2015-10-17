package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDeadRemovalStrategy {

    private static final String CLASS_NAME = "Ldead_removal_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestDeadRemovalStrategy.class.getSimpleName());

    @Test
    public void testDeadCodeIsRemoved() {
        String methodName = "DeadCode()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });

        assertEquals(expected, found);
    }

    @Test
    public void testDeadNopPaddingIsNotRemoved() {
        String methodName = "hasNopPadding()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void testDeadCodeWithStrongSideEffectIsRemoved() {
        String methodName = "DeadCodeWithStrongSideEffect()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void testAssignmentReassignedInOnlyOneMultiverseIsNotRemoved() {
        String methodName = "ReassignedInOnlyOneMultiverse(I)I";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[0]);

        assertEquals(expected, found);
    }

    @Test
    public void testDeadTryCatchBlockIsRemoved() {
        String methodName = "DeadTryCatchBlock()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testInstanceInitializerIsNotRemoved() {
        String methodName = "<init>()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        strategy.perform();

        BuilderInstruction instruction = mbgraph.getInstruction(0);
        assertEquals(Opcode.INVOKE_DIRECT, instruction.getOpcode());
    }

    @Test
    public void testMoveOpWithOnlyOneRegisterReassignedIsNotRemoved() {
        String methodName = "MoveP0IntoV0With30Locals(I)V";
        ExecutionGraphManipulator mbgraph = getOptimizedGraph(methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAssignmentAddresses();

        assertEquals(0, found.size());
    }

    @Test
    public void testUnusedAssignmentIsRemoved() {
        String methodName = "UnusedAssignment()I";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultNoSideEffectsRemovesInvoke() {
        String methodName = "UnusedResultNoSideEffects()I";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "UnusedResultWithSideEffects()I";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void testUselessGotoIsRemoved() {
        String methodName = "UselessGoto()V";
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUselessBranchAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(args);
        ExecutionGraphManipulator mbgraph = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        strategy.perform();

        return mbgraph;
    }

}
