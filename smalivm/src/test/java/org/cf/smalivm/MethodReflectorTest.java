package org.cf.smalivm;

import org.cf.smalivm.dex.CommonTypes;
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
        expected.setRegisters(0, value, CommonTypes.BYTE_OBJ);

        VMTester.test(CLASS_NAME, "byteValueOfByte()V", initial, expected);
    }

    @Test
    public void canInitBooleanWithBoolean() {
        boolean value = true;
        initial.setRegisters(1, value, "Z");
        expected.setRegisters(0, value, CommonTypes.BOOLEAN_OBJ);

        VMTester.test(CLASS_NAME, "initBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void canInitCharacterWithChar() {
        char value = 'a';
        initial.setRegisters(1, value, "C");
        expected.setRegisters(0, value, CommonTypes.CHARACTER_OBJ);

        VMTester.test(CLASS_NAME, "initCharacterWithChar()V", initial, expected);
    }

    @Test
    public void canGetShortValueOfShort() {
        short value = 5;
        initial.setRegisters(0, value, "S");
        expected.setRegisters(0, value, CommonTypes.SHORT_OBJ);

        VMTester.test(CLASS_NAME, "shortValueOfShort()V", initial, expected);
    }

    @Test
    public void handlesNullArgument() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, System.class, CommonTypes.CLASS, 1, "currentTimeMillis", CommonTypes.STRING, 2, 0, "I");
        expected.setRegisters(0, System.class.getMethod("currentTimeMillis", (Class<?>[]) null),
                "Ljava/lang/reflect/Method;");

        VMTester.test(CLASS_NAME, "getClassMethod()V", initial, expected);
    }

    @Test
    public void handlesException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, null, CommonTypes.STRING);
        int[] expected = new int[] {0, 4};

        VMTester.testVisitation(CLASS_NAME, "stringLength()V", initial, expected);
    }

    @Test
    public void handlesNoException() throws NoSuchMethodException, SecurityException {
        initial.setRegisters(0, "four", CommonTypes.STRING);
        int[] expected = new int[] {0, 3, 4};

        VMTester.testVisitation(CLASS_NAME, "stringLength()V", initial, expected);
    }

}
