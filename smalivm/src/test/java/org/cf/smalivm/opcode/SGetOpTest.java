package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SGetOpTest {

    private static final String CLASS_NAME = "Lsget_test;";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private VMState expected;
    private VMState initial;

    @Test
    public void getStaticBoolean() {
        initial.setFields(CLASS_NAME, "myBoolean:Z", true);
        expected.setRegisters(0, true, "Z");

        VMTester.test(CLASS_NAME, "getStaticBoolean()V", initial, expected);
    }

    @Test
    public void getStaticBooleanLiteral() {
        expected.setRegisters(0, true, "Z");

        VMTester.test(CLASS_NAME, "getStaticBooleanLiteral()V", initial, expected);
    }

    @Test
    public void getStaticBooleanUninitialized() {
        expected.setRegisters(0, false, "Z");

        VMTester.test(CLASS_NAME, "getStaticBoolean()V", initial, expected);
    }

    @Test
    public void getStaticByte() {
        initial.setFields(CLASS_NAME, "myByte:B", (byte) 0xf);
        expected.setRegisters(0, (byte) 0xf, "B");

        VMTester.test(CLASS_NAME, "getStaticByte()V", initial, expected);
    }

    @Test
    public void getStaticByteLiteral() {
        expected.setRegisters(0, (byte) 0xf, "B");

        VMTester.test(CLASS_NAME, "getStaticByteLiteral()V", initial, expected);
    }

    @Test
    public void getStaticByteUninitialized() {
        expected.setRegisters(0, (byte) 0, "B");

        VMTester.test(CLASS_NAME, "getStaticByte()V", initial, expected);
    }

    @Test
    public void getStaticChar() {
        initial.setFields(CLASS_NAME, "myChar:C", 'x');
        expected.setRegisters(0, 'x', "C");

        VMTester.test(CLASS_NAME, "getStaticChar()V", initial, expected);
    }

    @Test
    public void getStaticCharLiteral() {
        expected.setRegisters(0, 'x', "C");

        VMTester.test(CLASS_NAME, "getStaticCharLiteral()V", initial, expected);
    }

    @Test
    public void getStaticCharUninitialized() {
        expected.setRegisters(0, (char) '\0', "C");

        VMTester.test(CLASS_NAME, "getStaticChar()V", initial, expected);
    }

    @Test
    public void getStaticDoubleLiteral() {
        expected.setRegisters(0, 10000000000D, "D");

        VMTester.test(CLASS_NAME, "getStaticDoubleLiteral()V", initial, expected);
    }

    @Test
    public void getStaticDoubleUninitialized() {
        expected.setRegisters(0, 0D, "D");

        VMTester.test(CLASS_NAME, "getStaticDouble()V", initial, expected);
    }

    @Test
    public void getStaticFloatLiteral() {
        expected.setRegisters(0, 1.1F, "F");

        VMTester.test(CLASS_NAME, "getStaticFloatLiteral()V", initial, expected);
    }

    @Test
    public void getStaticFloatUninitialized() {
        expected.setRegisters(0, 0F, "F");

        VMTester.test(CLASS_NAME, "getStaticFloat()V", initial, expected);
    }

    @Test
    public void getStaticInt() {
        initial.setFields(CLASS_NAME, "myInt:I", 0x42);
        expected.setRegisters(0, 0x42, "I");

        VMTester.test(CLASS_NAME, "getStaticInt()V", initial, expected);
    }

    @Test
    public void getStaticIntLiteral() {
        expected.setRegisters(0, 0x42, "I");

        VMTester.test(CLASS_NAME, "getStaticIntLiteral()V", initial, expected);
    }

    @Test
    public void getStaticIntUninitialized() {
        expected.setRegisters(0, 0, "I");

        VMTester.test(CLASS_NAME, "getStaticInt()V", initial, expected);
    }

    @Test
    public void getStaticLong() {
        initial.setFields(CLASS_NAME, "myLong:J", 0xFFFFFFFFFL);
        expected.setRegisters(0, 0xFFFFFFFFFL, "J");

        VMTester.test(CLASS_NAME, "getStaticLong()V", initial, expected);
    }

    @Test
    public void getStaticLongLiteral() {
        expected.setRegisters(0, 0xFFFFFFFFFL, "L");

        VMTester.test(CLASS_NAME, "getStaticLongLiteral()V", initial, expected);
    }

    @Test
    public void getStaticLongUninitialized() {
        expected.setRegisters(0, 0L, "J");

        VMTester.test(CLASS_NAME, "getStaticLong()V", initial, expected);
    }

    @Test
    public void getStaticShort() {
        initial.setFields(CLASS_NAME, "myShort:S", (short) 0x100);
        expected.setRegisters(0, (short) 0x100, "S");

        VMTester.test(CLASS_NAME, "getStaticShort()V", initial, expected);
    }

    @Test
    public void getStaticShortLiteral() {
        expected.setRegisters(0, (short) 0x100, "S");

        VMTester.test(CLASS_NAME, "getStaticShortLiteral()V", initial, expected);
    }

    @Test
    public void getStaticShortUninitialized() {
        expected.setRegisters(0, (short) 0, "S");

        VMTester.test(CLASS_NAME, "getStaticShort()V", initial, expected);
    }

    @Test
    public void getStaticString() {
        initial.setFields(CLASS_NAME, "myString:Ljava/lang/String;", "They tried and died.");
        expected.setRegisters(0, "They tried and died.", "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "getStaticString()V", initial, expected);
    }

    @Test
    public void getStaticStringLiteral() {
        expected.setRegisters(0, "life, what's life?", "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "getStaticStringLiteral()V", initial, expected);
    }

    @Test
    public void getStaticStringUninitialized() {
        expected.setRegisters(0, null, "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "getStaticString()V", initial, expected);
    }

    @Test
    public void getStaticUnknownClassFieldThrowsException() {
        expected.setRegisters(0, new UnknownValue(), "I");

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Can't find Smali file for Lsome/unknown/classzzzzz;");

        VMTester.test(CLASS_NAME, "getStaticUnknownClassField()V", expected);
    }

    @Test
    public void getStaticWhitelistedClassField() {
        expected.setRegisters(0, Integer.MAX_VALUE, "I");

        VMTester.test(CLASS_NAME, "getStaticWhitelistedClassField()V", expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
