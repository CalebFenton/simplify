package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.VMTester;
import simplify.vm.types.UninitializedInstance;
import util.SparseArray;

public class TestArrayOps {

    private static final String CLASS_NAME = "Larray_test;";

    @Test
    public void TestNewArrayPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new Integer[1]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestNewArrayPrimitive()V", registerState);
    }

    @Test
    public void TestNewArrayLocal() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        UninitializedInstance[] instances = new UninitializedInstance[1];
        instances[0] = new UninitializedInstance(CLASS_NAME);
        registerState.put(0, instances);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestNewArrayLocal()V", registerState);
    }

    @Test
    public void TestArrayPut() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new Integer[] { 4 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayPut()V", registerState);
    }

    @Test
    public void TestArrayPutWide() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new Long[] { 5L });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayPutWide()V", registerState);
    }

}
