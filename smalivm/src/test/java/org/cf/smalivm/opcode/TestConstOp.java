package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalClass;
import org.junit.Test;

public class TestConstOp {

    private static final String CLASS_NAME = "Lconst_test;";

    @Test
    public void testConst() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42424242);

        VMTester.testMethodState(CLASS_NAME, "TestConst()V", expected);
    }

    @Test
    public void testConst16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242);

        VMTester.testMethodState(CLASS_NAME, "TestConst16()V", expected);
    }

    @Test
    public void testConst4() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -3);

        VMTester.testMethodState(CLASS_NAME, "TestConst4()V", expected);
    }

    @Test
    public void testConstClassLocal() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalClass(CLASS_NAME));

        VMTester.testMethodState(CLASS_NAME, "TestConstClassLocal()V", expected);
    }

    @Test
    public void testConstClassRemote() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, Object.class);

        VMTester.testMethodState(CLASS_NAME, "TestConstClassRemote()V", expected);
    }

    @Test
    public void testConstClassOfUnknownClassReturnsLocalClass() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalClass("Lunknown/class;"));

        VMTester.testMethodState(CLASS_NAME, "TestConstClassUnknown()V", expected);
    }

    @Test
    public void testConstHigh16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42420000);

        VMTester.testMethodState(CLASS_NAME, "TestConstHigh16()V", expected);
    }

    @Test
    public void testConstString() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify rebels, I look for men with principles");

        VMTester.testMethodState(CLASS_NAME, "TestConstString()V", expected);
    }

    @Test
    public void testConstStringJumbo() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0,
                        "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles");

        VMTester.testMethodState(CLASS_NAME, "TestConstStringJumbo()V", expected);
    }

    @Test
    public void testConstWide() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242424242424242L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide()V", expected);
    }

    @Test
    public void testConstWide16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide16()V", expected);
    }

    @Test
    public void testConstWide32() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x1b7740L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWide32()V", expected);
    }

    @Test
    public void testConstWideHigh16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x4242000000000000L);

        VMTester.testMethodState(CLASS_NAME, "TestConstWideHigh16()V", expected);
    }

    @Test
    public void testNegativeConst16() {
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, -0xf);

        VMTester.testMethodState(CLASS_NAME, "TestNegativeConst16()V", expected);
    }
}
