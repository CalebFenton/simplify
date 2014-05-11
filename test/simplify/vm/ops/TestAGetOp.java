package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestAGetOp {

    private static final String CLASS_NAME = "Laget_test;";

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestArrayGet() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new int[] { 0x42 }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 0x42);

        VMTester.test(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetBoolean() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new boolean[] { true }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, true);

        VMTester.test(CLASS_NAME, "TestArrayGetBoolean()V", initial, expected);
    }

    @Test
    public void TestArrayGetByte() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new byte[] { 0xe }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (byte) 0xe);

        VMTester.test(CLASS_NAME, "TestArrayGetByte()V", initial, expected);
    }

    @Test
    public void TestArrayGetChar() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new char[] { 'a' }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 'a');

        VMTester.test(CLASS_NAME, "TestArrayGetChar()V", initial, expected);
    }

    @Test
    public void TestArrayGetObject() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new LocalInstance[] { new LocalInstance(
                        CLASS_NAME) }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new LocalInstance(CLASS_NAME));

        VMTester.test(CLASS_NAME, "TestArrayGetObject()V", initial, expected);
    }

    @Test
    public void TestArrayGetShort() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new short[] { 0x42 }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (short) 0x42);

        VMTester.test(CLASS_NAME, "TestArrayGetShort()V", initial, expected);
    }

    @Test
    public void TestArrayGetUninitializedPrimitive() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new int[1], 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, (new int[1])[0]);

        VMTester.test(CLASS_NAME, "TestArrayGetUninitializedInt()V", initial, expected);
    }

    @Test
    public void TestArrayGetUnknownArray() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new UnknownValue("[int"), 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new UnknownValue("[int"));

        VMTester.test(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetUnknownElement() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new Object[] { new UnknownValue("int"), 5 },
                        1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new UnknownValue("int"));

        VMTester.test(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

    @Test
    public void TestArrayGetWide() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new long[] { 0x10000000000L }, 1, 0);
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, 0x10000000000L);

        VMTester.test(CLASS_NAME, "TestArrayGetWide()V", initial, expected);
    }

}
