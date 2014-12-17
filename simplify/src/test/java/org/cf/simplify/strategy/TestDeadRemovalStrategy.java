package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
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
        String methodSignature = "DeadCode()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedAssignmentIsRemoved() {
        String methodSignature = "UnusedAssignment()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultNoSideEffectsRemovesInvoke() {
        String methodSignature = "UnusedResultNoSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void testUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodSignature = "UnusedResultWithSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void testUselessGotoIsRemoved() {
        String methodSignature = "UselessGoto()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUselessBranchAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testDeadTryCatchBlockIsRemoved() {
        String methodSignature = "DeadTryCatchBlock()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void testInstanceInitializerIsNotRemoved() {
        String methodSignature = "<init>()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        strategy.perform();

        BuilderInstruction instruction = mbgraph.getInstruction(0);
        assertEquals(Opcode.INVOKE_DIRECT, instruction.getOpcode());
    }

}
