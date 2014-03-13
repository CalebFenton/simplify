package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.VMTester;
import util.SparseArray;

public class TestConstOp {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void Const_TestConst4() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>(2);
        registerState.put(0, -3);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConst4()V", registerState);
    }

}
