package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.VMTester;
import util.SparseArray;

public class TestBinaryMathOp {

    private static final String CLASS_NAME = "Lbinary_math_test;";

    @Test
    public void TestAddInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 4);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestAddInt()V", registerState);
    }

    @Test
    public void TestSubInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 10);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestSubInt()V", registerState);
    }

    @Test
    public void TestMulInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, -21);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestMulInt()V", registerState);
    }

    @Test
    public void TestDivInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 3);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestDivInt()V", registerState);
    }

    @Test
    public void TestRemInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 2);
        registerState.put(1, 5);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestRemInt()V", registerState);
    }

    @Test
    public void TestAndInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 2);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestAndInt()V", registerState);
    }

    @Test
    public void TestOrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 7);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestOrInt()V", registerState);
    }

    @Test
    public void TestXorInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 5);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestXorInt()V", registerState);
    }

    @Test
    public void TestShlInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 20);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestShlInt()V", registerState);
    }

    @Test
    public void TestShrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 1);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestShrInt()V", registerState);
    }

    @Test
    public void TestUshrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 2);
        registerState.put(1, 2);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestUshrInt()V", registerState);
    }

}
