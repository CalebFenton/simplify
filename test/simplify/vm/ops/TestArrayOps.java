package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.UninitializedInstance;
import util.SparseArray;

public class TestArrayOps {

    private static final String CLASS_NAME = "Larray_test;";

    @Test
    public void TestArrayPut() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new int[] { 4 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayPut()V", registerState);
    }

    @Test
    public void TestArrayPutWide() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new long[] { 5L });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayPutWide()V", registerState);
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
    public void TestNewArrayPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new int[1]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestNewArrayPrimitive()V", registerState);
    }

    @Test
    public void TestArrayGetUninitializedPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, (new int[1])[0]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetUninitializedPrimitive()V", registerState);
    }

    @Test
    public void TestNewArrayMultiDimensionalPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new int[2][][]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestNewArrayMultiDimensionalPrimitive()V", registerState);
    }

    @Test
    public void TestNewArrayMultiDimensionalLocal() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new UninitializedInstance[2][][]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestNewArrayMultiDimensionalLocal()V", registerState);
    }

    @Test
    public void TestFilledNewArrayLocal() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(MethodContext.ResultRegister, new int[1][2][3][4][5]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFilledNewArrayLocal()V", registerState);
    }

}
