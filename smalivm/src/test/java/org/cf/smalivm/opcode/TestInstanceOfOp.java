package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestInstanceOfOp {

    private static final String CLASS_NAME = "Linstanceof_test;";

    public static class IntegrationTest {

        @Test
        public void testStringIsInstanceOfObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, false, "Z", 1, "great maker",
                            "Ljava/lang/String;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z", 1, "great maker",
                            "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "InstanceOfObject()V", initial, expected);
        }

        @Test
        public void testUnknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, false, "Z", 1, new UnknownValue(),
                            "Ljava/lang/Object;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "Z", 1,
                            new UnknownValue(), "Ljava/lang/Object;");

            VMTester.testMethodState(CLASS_NAME, "InstanceOfString()V", initial, expected);
        }

        @Test
        public void testUnknownValueWithStringAsDeclaredTypeIsObject() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, false, "Z", 1, new UnknownValue(),
                            "Ljava/lang/String;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z", 1, new UnknownValue(),
                            "Ljava/lang/String;");

            VMTester.testMethodState(CLASS_NAME, "InstanceOfObject()V", initial, expected);
        }

        @Test
        public void testObjectIsNotInstanceOfString() {
            // Starting true just to see that it changes.
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, true, "Z", 1, null, "Ljava/lang/Object;");
            TIntObjectMap<HeapItem> expected = VMTester
                            .buildRegisterState(0, false, "Z", 1, null, "Ljava/lang/Object;");

            VMTester.testMethodState(CLASS_NAME, "InstanceOfString()V", initial, expected);
        }

        @Test
        public void testIntArrayWithObjectAsDeclaredTypeIsInstanceOfIntArray() {
            // Starting true just to see that it changes.
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, false, "Z", 1, new int[0],
                            "Ljava/lang/Object;");
            TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, true, "Z", 1, new int[0],
                            "Ljava/lang/Object;");

            VMTester.testMethodState(CLASS_NAME, "InstanceOfIntArray()V", initial, expected);
        }

    }

}
