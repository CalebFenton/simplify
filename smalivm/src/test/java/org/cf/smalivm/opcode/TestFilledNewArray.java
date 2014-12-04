package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.MethodState;
import org.junit.Test;

public class TestFilledNewArray {

    private static final String CLASS_NAME = "Lfilled_new_array_test;";

    @Test
    public void testFilledNewArrayOp() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 2, 1, 3, 2, 5);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodState.ResultRegister,
                        new int[] { 2, 3, 5 });

        VMTester.testMethodState(CLASS_NAME, "TestFilledNewArray()V", initial, expected);
    }
}
