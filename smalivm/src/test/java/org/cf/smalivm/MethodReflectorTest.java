package org.cf.smalivm;

import org.junit.Before;
import org.junit.Test;

public class MethodReflectorTest {

    private static final String CLASS_NAME = "Lmethod_reflector_test;";
    private VMState expected;
    private VMState initial;

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

    @Test
    public void castsIntegerToByte() {
        byte value = 6;
        initial.setRegisters(0, value, "B");
        expected.setRegisters(0, Byte.valueOf(value), "Ljava/lang/Byte;");

        VMTester.test(CLASS_NAME, "ByteValueOfByte()V", initial, expected);
    }

    @Test
    public void initBooleanWithBoolean() {
        boolean value = true;
        initial.setRegisters(1, value, "Z");
        expected.setRegisters(0, Boolean.valueOf(value), "Ljava/lang/Boolean;");

        VMTester.test(CLASS_NAME, "InitBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void initCharacterWithChar() {
        char value = 'a';
        initial.setRegisters(1, value, "C");
        expected.setRegisters(0, Character.valueOf(value), "Ljava/lang/Character;");

        VMTester.test(CLASS_NAME, "InitCharacterWithChar()V", initial, expected);
    }

    @Test
    public void shortValueOfShort() {
        short value = 5;
        initial.setRegisters(0, value, "S");
        expected.setRegisters(0, Short.valueOf(value), "Ljava/lang/Short;");

        VMTester.test(CLASS_NAME, "ShortValueOfShort()V", initial, expected);
    }

}
