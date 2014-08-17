package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplifier.Main;
import simplifier.vm.context.MethodContext;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestFilledNewArray {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfilled_new_array_test;";

    @Test
    public void TestFilledNewArrayOp() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 2, 1, 3, 2, 5);
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, new int[] { 2, 3,
                        5 });

        VMTester.testState(CLASS_NAME, "TestFilledNewArray()V", initial, expected);
    }
}
