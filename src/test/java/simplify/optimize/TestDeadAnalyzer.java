package simplify.optimize;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;

public class TestDeadAnalyzer {

    static final String CLASS_NAME = "Ldead_detector_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

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
