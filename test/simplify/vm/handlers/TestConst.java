package simplify.vm.handlers;

import org.junit.Test;

import simplify.vm.MethodContext;
import util.SparseArray;

public class TestConst {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void BinaryMath_TestConst4() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, -3);
        registerState.put(1, 7);
        registerState.put(MethodContext.ReturnRegister, -3);

        SmaliTester.executeAndEnsureContextState(CLASS_NAME, "TestConst4()I", registerState);
    }

}
