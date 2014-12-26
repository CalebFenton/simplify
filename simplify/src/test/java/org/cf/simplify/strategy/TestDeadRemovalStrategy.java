package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import org.cf.simplify.MethodBackedGraph;
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

    private static MethodBackedGraph getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(args);
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName, initial);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        strategy.perform();

        return mbgraph;
    }

    @Test
    public void testDeadCodeIsRemoved() {
        String methodName = "DeadCode()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedAssignmentIsRemoved() {
        String methodName = "UnusedAssignment()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultNoSideEffectsRemovesInvoke() {
        String methodName = "UnusedResultNoSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "UnusedResultWithSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void testUselessGotoIsRemoved() {
        String methodName = "UselessGoto()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUselessBranchAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testDeadTryCatchBlockIsRemoved() {
        String methodName = "DeadTryCatchBlock()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testInstanceInitializerIsNotRemoved() {
        String methodName = "<init>()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        strategy.perform();

        BuilderInstruction instruction = mbgraph.getInstruction(0);
        assertEquals(Opcode.INVOKE_DIRECT, instruction.getOpcode());
    }

    @Test
    public void testMoveOpWithOnlyOneRegisterReassignedIsNotRemoved() {
        String methodName = "MoveP0IntoV0With30Locals(I)V";
        MethodBackedGraph mbgraph = getOptimizedGraph(methodName);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();

        assertEquals(0, found.size());
    }

}
