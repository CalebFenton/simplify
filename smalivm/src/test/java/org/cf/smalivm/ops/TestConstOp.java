package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestConstOp {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void TestConst() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42424242);

        VMTester.testMethodState(CLASS_NAME, "TestConst()V", expected);
    }

    @Test
    public void TestConst16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242);

        VMTester.testMethodState(CLASS_NAME, "TestConst16()V", expected);
    }

    @Test
    public void TestNegativeConst16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0xf);

        VMTester.testMethodState(CLASS_NAME, "TestNegativeConst16()V", expected);
    }

    @Test
    public void TestConst4() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3);

        VMTester.testMethodState(CLASS_NAME, "TestConst4()V", expected);
    }

    @Test
    public void TestConstClassLocal() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));

        VMTester.testMethodState(CLASS_NAME, "TestConstClassLocal()V", expected);
    }

    @Test
    public void TestConstClassRemote() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, Object.class);

        VMTester.testMethodState(CLASS_NAME, "TestConstClassRemote()V", expected);
    }

    @Test
    public void TestConstClassUnknown() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("Lunknown/class;"));

        VMTester.testMethodState(CLASS_NAME, "TestConstClassUnknown()V", expected);
    }

    @Test
    public void TestConstHigh16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42420000);

        VMTester.testMethodState(CLASS_NAME, "TestConstHigh16()V", expected);
    }

    @Test
    public void TestConstString() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify rebels, I look for men with principles");

        VMTester.testMethodState(CLASS_NAME, "TestConstString()V", expected);
    }

    @Test
    public void TestConstStringJumbo() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles");

        VMTester.testMethodState(CLASS_NAME, "TestConstStringJumbo()V", expected);
    }

    @Test
    public void TestConstWide() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242424242424242L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide()V", expected);
    }

    @Test
    public void TestConstWide16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide16()V", expected);
    }

    @Test
    public void TestConstWide32() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x1b7740L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide32()V", expected);
    }

    @Test
    public void TestConstWideHigh16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242000000000000L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWideHigh16()V", expected);
    }
}
