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
    public void TestProperlyCastsIntegerToCharacter() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(1, (int) 'a');
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new Character('a'));

        VMTester.testMethodState(CLASS_NAME, "InitCharacterFromInt()V", initial, expected);
    }

}
