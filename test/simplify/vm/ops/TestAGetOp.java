package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import util.SparseArray;

public class TestAGetOp {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Laget_test;";

    @Test
    public void TestArrayGetUninitializedPrimitive() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, (new int[1])[0]);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetUninitializedInt()V", registerState);
    }

    @Test
    public void TestArrayGet() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 4);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGet()V", registerState);
    }

    @Test
    public void TestArrayGetWide() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(2, 0x1000000000L);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetWide()V", registerState);
    }

    @Test
    public void TestArrayGetObject() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, new LocalInstance("Laget_test;"));

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetObject()V", registerState);
    }

    @Test
    public void TestArrayGetBoolean() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, true);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetBoolean()V", registerState);
    }

    @Test
    public void TestArrayGetByte() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, (byte) 5);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetByte()V", registerState);
    }

    @Test
    public void TestArrayGetChar() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 'a');

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetChar()V", registerState);
    }

    @Test
    public void TestArrayGetShort() {
        SparseArray<Object> registerState;
        registerState = new SparseArray<Object>();
        registerState.put(0, 0x61);

        VMTester.executeAndEnsureContextState(CLASS_NAME, "TestArrayGetShort()V", registerState);
    }

}
