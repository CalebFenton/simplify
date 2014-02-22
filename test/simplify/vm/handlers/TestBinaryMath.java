package simplify.vm.handlers;

import org.junit.Test;

import simplify.vm.MethodContext;
import util.SparseArray;

public class TestBinaryMath {

    private static final String CLASS_NAME = "Lbinary_math_test;";

    @Test
    public void BinaryMath_TestAddInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 4);
        registerState.put(1, 7);
        registerState.put(MethodContext.ReturnRegister, 4);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestAddInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestSubInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 10);
        registerState.put(1, 7);
        registerState.put(MethodContext.ReturnRegister, 10);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestSubInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestMulInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, -21);
        registerState.put(1, 7);
        registerState.put(MethodContext.ReturnRegister, -21);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestMulInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestDivInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 3);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 3);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestDivInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestRemInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 2);
        registerState.put(1, 5);
        registerState.put(MethodContext.ReturnRegister, 2);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestRemInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestAndInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 2);
        registerState.put(1, 7);
        registerState.put(MethodContext.ReturnRegister, 2);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestAndInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestOrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 7);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 7);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestOrInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestXorInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 5);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 5);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestXorInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestShlInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 20);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 20);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestShlInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestShrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 1);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 1);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestShrInt()I", registerState);
    }

    @Test
    public void BinaryMath_TestUshrInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, 2);
        registerState.put(1, 2);
        registerState.put(MethodContext.ReturnRegister, 2);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestUshrInt()I", registerState);
    }

}
