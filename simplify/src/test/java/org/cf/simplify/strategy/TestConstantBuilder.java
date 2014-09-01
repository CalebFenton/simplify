package org.cf.simplify.strategy;

import org.cf.simplify.strategy.ConstantPropigationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestConstantBuilder {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestConstantBuilder.class.getSimpleName());

    static final String CLASS_NAME = "Lconstant_builder_test;";

    ConstantPropigationStrategy cb;

    public void setUp() {
        // cb = new ConstantPropigationStrategy(VMTester.getDexBuilder());
    }

    public void TestUnusedAssignmentIsRemoved() {
        // String methodSignature = "KnownIntForBinaryMathOp()I";
        // ContextGraph graph = VMTester.execute(CLASS_NAME, methodSignature);
        //
        // BuilderInstruction found = cb.buildConstantForAddress(0, graph, originalInstruction)
        // TIntList found = deadDetector.getDeadAssignmentAddresses();
        // TIntList expected = new TIntArrayList(new int[] { 0 });
        //
        // assertEquals(expected, found);
    }
}
