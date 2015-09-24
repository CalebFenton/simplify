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
    public void TestNewInteger1DArray() {
        int length = 1;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length], "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewInteger2DArray() {
        int length = 3;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length][], "[[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArrayArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayUnkonwnLength() {
        Object length = new UnknownValue();
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, length, "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewIntegerArrayWithShortDimension() {
        Short length = 1;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "S");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new int[length.intValue()], "[I");

        VMTester.testMethodState(CLASS_NAME, "TestNewIntegerArray()V", initial, expected);
    }

    @Test
    public void TestNewLocalClass1DArray() {
        int length = 1;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
        LocalInstance[] instances = new LocalInstance[length];
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArray()V", initial, expected);
    }

    @Test
    public void TestNewLocalClass2DArray() {
        int length = 5;
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, length, "I");
        LocalInstance[][] instances = new LocalInstance[length][];
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, instances, "[[" + CLASS_NAME);

        VMTester.testMethodState(CLASS_NAME, "TestNewLocalClassArrayArray()V", initial, expected);
    }

}
