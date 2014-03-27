package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestAPutOp {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Laput_test;";

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

}
