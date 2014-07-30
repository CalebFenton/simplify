package simplify.vm.ops;

import org.junit.Test;

import simplify.vm.VMTester;
import simplify.vm.types.LocalInstance;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestConstOp {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void TestConst() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x42424242);

        VMTester.test(CLASS_NAME, "TestConst()V", expected);
    }

    @Test
    public void TestConst16() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x4242);

        VMTester.test(CLASS_NAME, "TestConst16()V", expected);
    }

    @Test
    public void TestNegativeConst16() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -0xf);

        VMTester.test(CLASS_NAME, "TestNegativeConst16()V", expected);
    }

    @Test
    public void TestConst4() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, -3);

        VMTester.test(CLASS_NAME, "TestConst4()V", expected);
    }

    @Test
    public void TestConstClassLocal() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));

        VMTester.test(CLASS_NAME, "TestConstClassLocal()V", expected);
    }

    @Test
    public void TestConstClassRemote() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, Object.class);

        VMTester.test(CLASS_NAME, "TestConstClassRemote()V", expected);
    }

    @Test
    public void TestConstClassUnknown() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("Lunknown/class;"));

        VMTester.test(CLASS_NAME, "TestConstClassUnknown()V", expected);
    }

    @Test
    public void TestConstHigh16() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x42420000);

        VMTester.test(CLASS_NAME, "TestConstHigh16()V", expected);
    }

    @Test
    public void TestConstString() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify rebels, I look for men with principles");

        VMTester.test(CLASS_NAME, "TestConstString()V", expected);
    }

    @Test
    public void TestConstStringJumbo() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles");

        VMTester.test(CLASS_NAME, "TestConstStringJumbo()V", expected);
    }

    @Test
    public void TestConstWide() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x4242424242424242L);

        VMTester.test(CLASS_NAME, "TestConstWide()V", expected);
    }

    @Test
    public void TestConstWide16() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x4242L);

        VMTester.test(CLASS_NAME, "TestConstWide16()V", expected);
    }

    @Test
    public void TestConstWide32() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x1b7740L);

        VMTester.test(CLASS_NAME, "TestConstWide32()V", expected);
    }

    @Test
    public void TestConstWideHigh16() {
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0x4242000000000000L);

        VMTester.test(CLASS_NAME, "TestConstWideHigh16()V", expected);
    }
}
