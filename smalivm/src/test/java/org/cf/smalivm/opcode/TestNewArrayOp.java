package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestNewArrayOp {

    private static final String CLASS_NAME = "Lnew_array_test;";

    @Test
    public void TewNewLocalClassArray() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 1, "I");
        LocalInstance[] instances = new LocalInstance[] { new LocalInstance(CLASS_NAME) };
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArray()V", initial, expected);
    }

    @Test
    public void TestNewLocalClassArrayArray() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 5, "I");
        LocalInstance[][] instances = new LocalInstance[5][];
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArrayArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArray() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 1, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[1], "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayWithShortDimension() {
        Short dimension = 1;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, dimension, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[dimension.intValue()], "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayArray() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 3, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[3][], "[[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArrayArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayUnkonwnDimension() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

}
