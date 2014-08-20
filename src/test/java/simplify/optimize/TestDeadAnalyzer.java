package simplify.optimize;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.context.ContextGraph;

public class TestDeadAnalyzer {

    private static final String CLASS_NAME = "Ldead_detector_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestUnusedAssignmentIsFound() {
        ContextGraph graph = VMTester.execute(CLASS_NAME, "UnusedAssignment()I");
        TIntList found = DeadDetector.getUnusedAssignmentAddresses(graph.getAddresses(), graph);
        TIntList expected = new TIntArrayList(new int[] { 0 });
        assertEquals(expected, found);
    }

    @Test
    public void TestDeadCodeIsFound() {
        ContextGraph graph = VMTester.execute(CLASS_NAME, "DeadCode()V");
        TIntList found = DeadDetector.getDeadAddresses(graph.getAddresses(), graph);
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });
        assertEquals(expected, found);
    }

    @Test
    public void TestUselessGotoIsFound() {
        ContextGraph graph = VMTester.execute(CLASS_NAME, "UselessGoto()V");
        TIntList found = DeadDetector.getDeadAddresses(graph.getAddresses(), graph);
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });
        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultWithSideEffectsDoesNotRemoveInvoke() {
        ContextGraph graph = VMTester.execute(CLASS_NAME, "UnusedResultWithSideEffects()I");
        TIntList found = DeadDetector.getUnusedAssignmentAddresses(graph.getAddresses(), graph);
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });
        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultNoSideEffectsRemovesInvoke() {
        ContextGraph graph = VMTester.execute(CLASS_NAME, "UnusedResultNoSideEffects()I");
        TIntList found = DeadDetector.getUnusedAssignmentAddresses(graph.getAddresses(), graph);
        TIntList expected = new TIntArrayList(new int[] { 1 });
        assertEquals(expected, found);
    }
}
