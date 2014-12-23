package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestNewArrayOp {

    private static final String CLASS_NAME = "Lnew_array_test;";

    @Test
    public void TewNewLocalClassArray() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
        LocalInstance[] instances = new LocalInstance[] { new LocalInstance(CLASS_NAME) };
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArray()V", initial, expected);
    }

    @Test
    public void TestNewLocalClassArrayArray() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
        LocalInstance[][] instances = new LocalInstance[5][];
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArrayArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArray() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[1]);

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayWithShortDimension() {
        Short dimension = 1;
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, dimension);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[dimension.intValue()]);

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayArray() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[3][]);

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArrayArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayUnkonwnDimension() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

}
