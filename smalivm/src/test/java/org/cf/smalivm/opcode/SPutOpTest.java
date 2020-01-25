package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SPutOpTest {

    private static final String CLASS_NAME = "Lsput_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void putStaticBoolean() {
        initial.setRegisters(0, true, "Z");
        expected.setFields(CLASS_NAME, "myBoolean:Z", true);

        VMTester.test(CLASS_NAME, "putStaticBoolean()V", initial, expected);
    }

    @Test
    public void putStaticByte() {
        initial.setRegisters(0, (byte) 0xFF, "B");
        expected.setFields(CLASS_NAME, "myByte:B", (byte) 0xFF);

        VMTester.test(CLASS_NAME, "putStaticByte()V", initial, expected);
    }

    @Test
    public void putStaticChar() {
        initial.setRegisters(0, '!', "C");
        expected.setFields(CLASS_NAME, "myChar:C", '!');

        VMTester.test(CLASS_NAME, "putStaticChar()V", initial, expected);
    }

    @Test
    public void putStaticInt() {
        initial.setRegisters(0, 0x42, "I");
        expected.setFields(CLASS_NAME, "myInt:I", 0x42);

        VMTester.test(CLASS_NAME, "putStaticInt()V", initial, expected);
    }

    @Test
    public void putStaticObject() {
        initial.setRegisters(0, "Do not pray for an easy life", "Ljava/lang/String;");
        expected.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "Do not pray for an easy life");

        VMTester.test(CLASS_NAME, "putStaticObject()V", initial, expected);
    }

    @Test
    public void putStaticShort() {
        initial.setRegisters(0, (short) 0x4242, "S");
        expected.setFields(CLASS_NAME, "myShort:S", (short) 0x4242);

        VMTester.test(CLASS_NAME, "putStaticShort()V", initial, expected);
    }

    @Test
    public void putStaticWide() {
        initial.setRegisters(0, 0xFFFFFFFFFL, "J");
        expected.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL);

        VMTester.test(CLASS_NAME, "putStaticWide()V", initial, expected);
    }

    @BeforeEach
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
