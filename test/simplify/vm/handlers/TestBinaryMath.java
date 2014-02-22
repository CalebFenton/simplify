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

}
