package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestNewArrayOp {

    private static final String CLASS_NAME = "Lnew_array_test;";

    @Test
    public void testNewArrayLocal() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
        LocalInstance[] instances = new LocalInstance[] { new LocalInstance(CLASS_NAME) };
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testMethodState(CLASS_NAME, "TestNewArrayLocal()V", initial, expected);
    }

    @Test
    public void testNewArrayLocalMultidimensional() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 5);
        LocalInstance[][] instances = new LocalInstance[5][];
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, instances);

        VMTester.testMethodState(CLASS_NAME, "TestNewArrayLocalMultidimensional()V", initial, expected);
    }

    @Test
    public void testNewArrayPrimitive() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[1]);

        VMTester.testMethodState(CLASS_NAME, "TestNewArrayPrimitive()V", initial, expected);
    }

    @Test
    public void testNewArrayPrimitiveMultidimensional() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 3);
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[3][]);

        VMTester.testMethodState(CLASS_NAME, "TestNewArrayPrimitiveMultidimensional()V", initial, expected);
    }

    @Test
    public void testNewArrayPrimitiveUnkonwnDimension() {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
        TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"));

        VMTester.testMethodState(CLASS_NAME, "TestNewArrayPrimitive()V", initial, expected);
    }

}
