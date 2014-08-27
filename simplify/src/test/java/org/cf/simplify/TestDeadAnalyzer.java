package org.cf.simplify;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cf.simplify.DeadDetector;
import org.cf.simplify.Main;
import org.junit.Test;

public class TestDeadAnalyzer {

    static final String CLASS_NAME = "Ldead_detector_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    @Test
    public void TestUnusedAssignmentIsRemoved() {
        String methodName = "UnusedAssignment()I";
        DeadDetector deadDetector = OptimizerTester.getDeadDetector(CLASS_NAME, methodName);
        TIntList found = deadDetector.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestDeadCodeIsRemoved() {
        String methodName = "DeadCode()V";
        DeadDetector deadDetector = OptimizerTester.getDeadDetector(CLASS_NAME, methodName);
        TIntList found = deadDetector.getDeadAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUselessGotoIsRemoved() {
        String methodName = "UselessGoto()V";
        DeadDetector deadDetector = OptimizerTester.getDeadDetector(CLASS_NAME, methodName);
        TIntList found = deadDetector.getDeadBranchAddresses();
        System.out.println(found);
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "UnusedResultWithSideEffects()I";
        DeadDetector deadDetector = OptimizerTester.getDeadDetector(CLASS_NAME, methodName);
        TIntList found = deadDetector.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultNoSideEffectsRemovesInvoke() {
        String methodName = "UnusedResultNoSideEffects()I";
        DeadDetector deadDetector = OptimizerTester.getDeadDetector(CLASS_NAME, methodName);
        TIntList found = deadDetector.getDeadResultAddresses();
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }
}
