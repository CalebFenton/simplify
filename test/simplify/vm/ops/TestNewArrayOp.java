package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import util.SparseArray;

public class TestNewArrayOp {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lnew_array_test;";

    @Test
    public void TestNewArrayLocal() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        LocalInstance[] instances = new LocalInstance[1];
        instances[0] = new LocalInstance(CLASS_NAME);
        registerState.put(0, instances);

        VMTester.test(CLASS_NAME, "TestNewArrayLocal()V", registerState);
    }

    @Test
    public void TestNewArrayPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new int[1]);

        VMTester.test(CLASS_NAME, "TestNewArrayPrimitive()V", registerState);
    }

}
