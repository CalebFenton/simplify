package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import util.SparseArray;

public class TestFillArrayDataOps {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Lfill_array_data_test;";

    @Test
    public void TestFillArrayDataInt() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new int[] { 1, 2, 3, 4, 5 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFillArrayDataInt()V", registerState);
    }

    @Test
    public void TestFillArrayDataChar() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new char[] { 'a', 'b', 'c' });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFillArrayDataChar()V", registerState);
    }

    @Test
    public void TestFillArrayDataShort() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new short[] { 100, 200, 5 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFillArrayDataShort()V", registerState);
    }

    @Test
    public void TestFillArrayDataFloat() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new float[] { 1, 2 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFillArrayDataFloat()V", registerState);
    }

    @Test
    public void TestFillArrayDataDouble() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new double[] { 0.1, 1.5 });

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestFillArrayDataDouble()V", registerState);
    }

}
