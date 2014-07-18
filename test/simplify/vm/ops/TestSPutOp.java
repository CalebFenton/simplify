package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestSPutOp {

    private static final String CLASS_NAME = "Laput_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestArrayPut() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, 4);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[] { 4 });

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutBoolean() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new boolean[1], 1, 0, 2, 0x1);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new boolean[] { true });

        VMTester.testState(CLASS_NAME, "TestArrayPutBoolean()V", initial, expected);
    }

    @Test
    public void TestArrayPutByte() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new byte[1], 1, 0, 2, 0xf);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new byte[] { 0xf });

        VMTester.testState(CLASS_NAME, "TestArrayPutByte()V", initial, expected);
    }

    @Test
    public void TestArrayPutChar() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new char[1], 1, 0, 2, (int) '$'); // get
                                                                                                            // money.
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new char[] { '$' });

        VMTester.testState(CLASS_NAME, "TestArrayPutChar()V", initial, expected);
    }

    @Test
    public void TestArrayPutObject() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new LocalInstance[1], 1, 0, 2,
                        new LocalInstance(CLASS_NAME));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) });

        VMTester.testState(CLASS_NAME, "TestArrayPutObject()V", initial, expected);
    }

    @Test
    public void TestArrayPutShort() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new short[1], 1, 0, 2, 0x42);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new short[] { 0x42 });

        VMTester.testState(CLASS_NAME, "TestArrayPutShort()V", initial, expected);
    }

    @Test
    public void TestArrayPutUnkownValue() {
        // Ideally, setting an element unknown shouldn't set the entire array unknown. See APutOp for more details.
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, 0, 2, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutUnkownIndex() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[1], 1, new UnknownValue("I"), 2, 5);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "TestArrayPut()V", initial, expected);
    }

    @Test
    public void TestArrayPutWide() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new long[1], 1, 0, 2, 10000000000L);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new long[] { 10000000000L });

        VMTester.testState(CLASS_NAME, "TestArrayPutWide()V", initial, expected);
    }

}
