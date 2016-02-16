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
    public void canCastIntegerToByte() {
        byte value = 6;
        initial.setRegisters(0, value, "B");
        expected.setRegisters(0, Byte.valueOf(value), "Ljava/lang/Byte;");

        VMTester.test(CLASS_NAME, "byteValueOfByte()V", initial, expected);
    }

    @Test
    public void canInitBooleanWithBoolean() {
        boolean value = true;
        initial.setRegisters(1, value, "Z");
        expected.setRegisters(0, Boolean.valueOf(value), "Ljava/lang/Boolean;");

        VMTester.test(CLASS_NAME, "initBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void canInitCharacterWithChar() {
        char value = 'a';
        initial.setRegisters(1, value, "C");
        expected.setRegisters(0, Character.valueOf(value), "Ljava/lang/Character;");

        VMTester.test(CLASS_NAME, "initCharacterWithChar()V", initial, expected);
    }

    @Test
    public void canGetShortValueOfShort() {
        short value = 5;
        initial.setRegisters(0, value, "S");
        expected.setRegisters(0, Short.valueOf(value), "Ljava/lang/Short;");

        VMTester.test(CLASS_NAME, "shortValueOfShort()V", initial, expected);
    }

    @Test
    public void handlesNullArgumentProperly() throws NoSuchMethodException, SecurityException {
        // Dalvik doesn't have a null type, it uses 0x0
        initial.setRegisters(0, System.class, "Ljava/lang/Class;", 1, "currentTimeMillis", "Ljava/lang/String;", 2, 0,
                        "I");
        expected.setRegisters(0, System.class.getMethod("currentTimeMillis", (Class<?>[]) null),
                        "Ljava/lang/reflect/Method;");

        VMTester.test(CLASS_NAME, "getClassMethod()V", initial, expected);
    }

}
