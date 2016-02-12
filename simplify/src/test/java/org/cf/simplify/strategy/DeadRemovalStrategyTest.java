package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMState;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadRemovalStrategyTest {

    private static final String CLASS_NAME = "Ldead_removal_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DeadRemovalStrategyTest.class.getSimpleName());

    @Test
    public void assignmentReassignedInOnlyOneMultiverseIsNotRemoved() {
        String methodName = "reassignedInOnlyOneMultiverse(I)I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[0]);

        assertEquals(expected, found);
    }

    @Test
    public void deadCodeIsRemoved() {
        String methodName = "deadCode()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });

        assertEquals(expected, found);
    }

    @Test
    public void deadCodeWithStrongSideEffectIsRemoved() {
        String methodName = "deadCodeWithStrongSideEffect()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void deadNopPaddingIsNotRemoved() {
        String methodName = "hasNopPadding()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void deadTryCatchBlockIsRemoved() {
        String methodName = "deadTryCatchBlock()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void instanceInitializerIsNotRemoved() {
        String methodName = "<init>()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.perform();

        BuilderInstruction instruction = manipulator.getInstruction(0);
        assertEquals(Opcode.INVOKE_DIRECT, instruction.getOpcode());
    }

    @Test
    public void moveOpWithOnlyOneRegisterReassignedIsNotRemoved() {
        String methodName = "moveP0IntoV0With30Locals(I)V";
        ExecutionGraphManipulator manipulator = getOptimizedGraph(methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAssignmentAddresses();

        assertEquals(0, found.size());
    }

    @Test
    public void unusedAssignmentIsRemoved() {
        String methodName = "unusedAssignment()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void unusedResultNoSideEffectsRemovesInvoke() {
        String methodName = "unusedResultNoSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void unusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "unusedResultWithSideEffects()I";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUnusedAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void uselessGotoIsRemoved() {
        String methodName = "uselessGoto()V";
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        TIntList found = strategy.getUselessBranchAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(manipulator);
        strategy.perform();

        return manipulator;
    }

}
