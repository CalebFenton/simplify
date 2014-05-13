package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestFilledNewArray {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfilled_new_array_test;";

    @Test
    public void TestFilledNewArrayOp() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 2, 1, 3, 2, 5);
        SparseArray<Object> expected = MethodContext.buildRegisterState(MethodContext.ResultRegister, new int[] { 2, 3,
                        5 });

        VMTester.testState(CLASS_NAME, "TestFilledNewArray()V", initial, expected);
    }
}
