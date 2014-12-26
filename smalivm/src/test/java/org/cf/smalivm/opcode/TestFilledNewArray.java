package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestFilledNewArray {

    private static final String CLASS_NAME = "Lfilled_new_array_test;";
    private static final String METHOD_NAME = "TestFilledNewArray()V";

    @Test
    public void testIntegerParametersCreatesArrayWithExpectedContents() {
        int[] elements = new int[] { 2, 3, 5 };
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, elements[0], "I", 1, elements[1], "I", 2,
                        elements[2], "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, elements, "[I");

        VMTester.testMethodState(CLASS_NAME, METHOD_NAME, initial, expected);
    }

    @Test
    public void testShortParametersCreatesArrayWithExpectedContents() {
        Short[] elements = new Short[] { 2, 3, 5 };
        int[] intElements = new int[] { 2, 3, 5 };
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, elements[0], "S", 1, elements[1], "S", 2,
                        elements[2], "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, intElements, "[I");

        VMTester.testMethodState(CLASS_NAME, METHOD_NAME, initial, expected);
    }

    @Test
    public void testUnknownElementParameterReturnsUnknownValueOfIntegerArrayType() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 2, "I", 1, 3, "I", 2, new UnknownValue(), "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(MethodState.ResultRegister, new UnknownValue(),
                        "[I");

        VMTester.testMethodState(CLASS_NAME, METHOD_NAME, initial, expected);
    }

}
