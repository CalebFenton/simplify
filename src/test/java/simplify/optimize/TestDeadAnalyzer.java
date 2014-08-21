package simplify.optimize;

import static org.junit.Assert.assertEquals;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.context.ContextGraph;

public class TestDeadAnalyzer {

    private static final String CLASS_NAME = "Ldead_detector_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static DeadDetector getDeadDetector(String methodName) {
        BuilderMethod method = VMTester.getBuilderMethod(CLASS_NAME, methodName);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        List<BuilderInstruction> instructions = implementation.getInstructions();
        TIntObjectMap<BuilderInstruction> addressToInstruction = Optimizer.buildAddressToInstruction(instructions);
        List<BuilderTryBlock> tryBlocks = implementation.getTryBlocks();
        ContextGraph graph = VMTester.execute(CLASS_NAME, methodName);
        DeadDetector result = new DeadDetector(graph, addressToInstruction, tryBlocks);

        return result;

    }

    @Test
    public void TestUnusedAssignmentIsRemoved() {
        String methodName = "UnusedAssignment()I";
        DeadDetector deadDetector = getDeadDetector(methodName);
        TIntList found = deadDetector.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestDeadCodeIsRemoved() {
        String methodName = "DeadCode()V";
        DeadDetector deadDetector = getDeadDetector(methodName);
        TIntList found = deadDetector.getDeadAddresses();
        found.sort();
        TIntList expected = new TIntArrayList(new int[] { 2, 3, 4, 5 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUselessGotoIsRemoved() {
        String methodName = "UselessGoto()V";
        DeadDetector deadDetector = getDeadDetector(methodName);
        TIntList found = deadDetector.getDeadBranchAddresses();
        System.out.println(found);
        TIntList expected = new TIntArrayList(new int[] { 0 });

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "UnusedResultWithSideEffects()I";
        DeadDetector deadDetector = getDeadDetector(methodName);
        TIntList found = deadDetector.getDeadAssignmentAddresses();
        TIntList expected = new TIntArrayList(new int[] {});

        assertEquals(expected, found);
    }

    @Test
    public void TestUnusedResultNoSideEffectsRemovesInvoke() {
        String methodName = "UnusedResultNoSideEffects()I";
        DeadDetector deadDetector = getDeadDetector(methodName);
        TIntList found = deadDetector.getDeadResultAddresses();
        System.out.println(found);
        TIntList expected = new TIntArrayList(new int[] { 1 });

        assertEquals(expected, found);
    }
}
