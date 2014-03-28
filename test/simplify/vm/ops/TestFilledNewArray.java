package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import util.SparseArray;

public class TestFilledNewArray {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfilled_new_array_test;";

    @Test
    public void TestFilledNewArrayPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(MethodContext.ResultRegister, new int[1][2][3][4][5]);

        VMTester.test(CLASS_NAME, "TestFilledNewArrayPrimitive()V", registerState);
    }

    @Test
    public void TestFilledNewArrayLocal() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        LocalInstance[][][] expected = new LocalInstance[1][2][3];
        for (LocalInstance[][] inner1 : expected) {
            for (LocalInstance[] inner2 : inner1) {
                for (int i = 0; i < inner2.length; i++) {
                    inner2[i] = new LocalInstance(CLASS_NAME);
                }
            }
        }

        registerState.put(MethodContext.ResultRegister, expected);

        VMTester.test(CLASS_NAME, "TestFilledNewArrayLocal()V", registerState);
    }

}
