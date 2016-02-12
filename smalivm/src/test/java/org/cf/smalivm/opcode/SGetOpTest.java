package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

public class SGetOpTest {

    private static final String CLASS_NAME = "Lsget_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void getStaticBoolean() {
        initial.setFields(CLASS_NAME, "myBoolean:Z", true);
        expected.setRegisters(0, true, "Z");

        VMTester.test(CLASS_NAME, "getStaticBoolean()V", initial, expected);
    }

    @Test
    public void getStaticByte() {
        initial.setFields(CLASS_NAME, "myByte:B", (byte) 0xf);
        expected.setRegisters(0, (byte) 0xf, "B");

        VMTester.test(CLASS_NAME, "getStaticByte()V", initial, expected);
    }

    @Test
    public void getStaticChar() {
        initial.setFields(CLASS_NAME, "myChar:C", 'x');
        expected.setRegisters(0, 'x', "C");

        VMTester.test(CLASS_NAME, "getStaticChar()V", initial, expected);
    }

    @Test
    public void getStaticInt() {
        initial.setFields(CLASS_NAME, "myInt:I", 0x42);
        expected.setRegisters(0, 0x42, "I");

        VMTester.test(CLASS_NAME, "getStaticInt()V", initial, expected);
    }

    @Test
    public void getStaticObject() {
        initial.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "They tried and died.");
        expected.setRegisters(0, "They tried and died.", "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "getStaticObject()V", initial, expected);
    }

    @Test
    public void getStaticShort() {
        initial.setFields(CLASS_NAME, "myShort:S", (short) 0x100);
        expected.setRegisters(0, (short) 0x100, "S");

        VMTester.test(CLASS_NAME, "getStaticShort()V", initial, expected);
    }

    @Test
    public void getStaticUnknownClassField() {
        expected.setRegisters(0, new UnknownValue(), "I");

        VMTester.test(CLASS_NAME, "getStaticUnknownClassField()V", expected);
    }

    @Test
    public void getStaticWhitelistedClassField() {
        expected.setRegisters(0, Integer.MAX_VALUE, "I");

        VMTester.test(CLASS_NAME, "getStaticWhitelistedClassField()V", expected);
    }

    @Test
    public void getStaticWide() {
        initial.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL);
        expected.setRegisters(0, 0xFFFFFFFFFL, "J");

        VMTester.test(CLASS_NAME, "getStaticWide()V", initial, expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
