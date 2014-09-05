package org.cf.smalivm;

import gnu.trove.map.TIntObjectMap;

public class TestConstantPropigator {

    private static final String CLASS_NAME = "Lconstant_propigator_test;";

    public void TestArrayGet() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x42 }, 1, 0);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, 0x42);

        VMTester.testMethodState(CLASS_NAME, "TestArrayGet()V", initial, expected);
    }

}
