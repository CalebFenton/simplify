package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDeadRemovalStrategy {

    private static final String CLASS_NAME = "Ldead_detector_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestDeadRemovalStrategy.class.getSimpleName());

    @Test
    public void TestDeadCodeIsRemoved() {
        String methodSignature = "DeadCode()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedAssignmentIsRemoved() {
        String methodSignature = "UnusedAssignment()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultNoSideEffectsRemovesInvoke() {
        String methodSignature = "UnusedResultNoSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodSignature = "UnusedResultWithSideEffects()I";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void TestUselessGotoIsRemoved() {
        String methodSignature = "UselessGoto()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getUselessBranchAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestDeadTryCatchBlockIsRemoved() {
        String methodSignature = "DeadTryCatchBlock()V";
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodSignature);
        DeadRemovalStrategy strategy = new DeadRemovalStrategy(mbgraph);
        TIntList found = strategy.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

}
