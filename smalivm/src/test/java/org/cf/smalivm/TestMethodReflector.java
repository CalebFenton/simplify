package org.cf.smalivm;

import gnu.trove.map.TIntObjectMap;

import org.junit.Test;

public class TestMethodReflector {
    /*
     * A side-effect is any modification of state that persists outside the method, e.g. changing class static or
     * instance variables, file and network IO, etc. To determine with 100% accuracy is tricky, and a lot of work, so we
     * take the shortcut of white listing certain classes and methods as not causing side effects. Knowing that a method
     * has no side effects lets the optimizer remove the invocation if the result is not used.
     */

    private static final String CLASS_NAME = "Lmethod_reflector_test;";

    @Test
    public void TestCastsIntegerToCharacter() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(1, (int) 'a');
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new Character('a'));

        VMTester.testMethodState(CLASS_NAME, "InitCharacterWithChar()V", initial, expected);
    }

    @Test
    public void TestCastsIntegerToBooleanWithTrue() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(1, true);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new Boolean(true));

        VMTester.testMethodState(CLASS_NAME, "InitBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void TestCastsIntegerToBooleanWithFalse() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(1, false);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new Boolean(false));

        VMTester.testMethodState(CLASS_NAME, "InitBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void TestCastsIntegerToShort() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, Short.valueOf((short) 5));

        VMTester.testMethodState(CLASS_NAME, "GetShortWithShort()V", initial, expected);
    }

    @Test
    public void TestCastsIntegerToByte() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 6);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, Byte.valueOf((byte) 6));

        VMTester.testMethodState(CLASS_NAME, "GetByteWithByte()V", initial, expected);
    }

}
