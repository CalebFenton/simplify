package org.cf.smalivm;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.context.HeapItem;
import org.junit.Test;

public class MethodReflectorTest {

    /*
     * A side-effect is any modification of state that persists outside the method, e.g. changing class static or
     * instance variables, file and network IO, etc. To determine with 100% accuracy is tricky, and a lot of work, so we
     * take the shortcut of white listing certain classes and methods as not causing side effects. Knowing that a method
     * has no side effects lets the optimizer remove the invocation if the result is not used.
     */

    private static final String CLASS_NAME = "Lmethod_reflector_test;";

    @Test
    public void testInitCharacterWithChar() {
        char value = 'a';
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(1, value, "C");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, Character.valueOf(value),
                        "Ljava/lang/Character;");

        VMTester.testMethodState(CLASS_NAME, "InitCharacterWithChar()V", initial, expected);
    }

    @Test
    public void testInitBooleanWithBoolean() {
        boolean value = true;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(1, value, "Z");
        TIntObjectMap<HeapItem> expected = VMTester
                        .buildRegisterState(0, Boolean.valueOf(value), "Ljava/lang/Boolean;");

        VMTester.testMethodState(CLASS_NAME, "InitBooleanWithBoolean()V", initial, expected);
    }

    @Test
    public void testShrotValueOfShort() {
        short value = 5;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, Short.valueOf(value), "Ljava/lang/Short;");

        VMTester.testMethodState(CLASS_NAME, "ShortValueOfShort()V", initial, expected);
    }

    @Test
    public void testCastsIntegerToByte() {
        byte value = 6;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, value, "B");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, Byte.valueOf(value), "Ljava/lang/Byte;");

        VMTester.testMethodState(CLASS_NAME, "ByteValueOfByte()V", initial, expected);
    }

}
