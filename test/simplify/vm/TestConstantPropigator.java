package simplify.vm;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import util.SparseArray;

public class TestConstantPropigator {

    private static final String CLASS_NAME = "Lconstant_propigator_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestArrayGet() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new int[] { 0x42 }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 0x42);

        VMTester.testState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

}
