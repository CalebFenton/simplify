package simplify.optimize;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Logger;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.junit.Test;

import simplify.Main;

public class TestPeepholeOptimizer {

    private static final String CLASS_NAME = "Lpeephole_optimizer_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestUnusedResultWithSideEffectsIsNotValidForRemoval() {
        String methodName = "InvokeClassForNameWithUnkownClass()V";
        PeepholeOptimizer po = OptimizerTester.getPeepholeOptimizer(CLASS_NAME, methodName);
        TIntObjectMap<BuilderInstruction> found = po.getClassForNameAddressToReplacementMap();
        TIntSet expected = new TIntHashSet(new int[] { 2 });

        assertEquals(found.keySet(), expected);
    }
}
