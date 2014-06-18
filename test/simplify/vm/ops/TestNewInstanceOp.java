package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UninitializedInstance;
import util.SparseArray;

public class TestNewInstanceOp {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lnew_instance_test;";

    @Test
    public void TestLocalClass() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 1);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new LocalInstance(CLASS_NAME));

        VMTester.testState(CLASS_NAME, "TestLocalClass()V", initial, expected);
    }

    @Test
    public void TestNonLocalClass() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 1);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new UninitializedInstance(
                        "Ljava/lang/Integer;"));

        VMTester.testState(CLASS_NAME, "TestNonLocalClass()V", initial, expected);
    }

}
