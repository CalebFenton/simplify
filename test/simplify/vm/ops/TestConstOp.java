package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.VMTester;
import simplify.vm.types.UninitializedInstance;
import util.SparseArray;

public class TestConstOp {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void TestConst() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x42424242);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConst()V", registerState);
    }

    @Test
    public void TestConst16() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x4242);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConst16()V", registerState);
    }

    @Test
    public void TestConst4() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, -3);
        registerState.put(1, 7);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConst4()V", registerState);
    }

    @Test
    public void TestConstClass() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new UninitializedInstance("Lconst_test;"));

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstClass()V", registerState);
    }

    @Test
    public void TestConstHigh16() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x42420000);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstHigh16()V", registerState);
    }

    @Test
    public void TestConstString() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, "When I need to identify rebels, I look for men with principles");

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstString()V", registerState);
    }

    @Test
    public void TestConstStringJumbo() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles");

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstStringJumbo()V", registerState);
    }

    @Test
    public void TestConstWide() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x4242424242424242L);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstWide()V", registerState);
    }

    @Test
    public void TestConstWide16() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x4242L);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstWide16()V", registerState);
    }

    @Test
    public void TestConstWide32() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x1b7740L);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstWide32()V", registerState);
    }

    @Test
    public void TestConstWideHigh16() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x4242000000000000L);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestConstWideHigh16()V", registerState);
    }
}
