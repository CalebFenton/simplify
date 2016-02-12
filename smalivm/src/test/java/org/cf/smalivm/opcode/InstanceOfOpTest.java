package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class InstanceOfOpTest {

    private static final String CLASS_NAME = "Linstanceof_test;";

    public static class IntegrationTest {

        private VMState expected;
        private VMState initial;

        @Before
        public void setUp() {
            expected = new VMState();
            initial = new VMState();
        }

        @Test
        public void testIntArrayWithObjectAsDeclaredTypeIsInstanceOfIntArray() {
            initial.setRegisters(0, false, "Z", 1, new int[0], "Ljava/lang/Object;");
            expected.setRegisters(0, true, "Z", 1, new int[0], "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "instanceOfIntArray()V", initial, expected);
        }

        @Test
        public void testObjectIsNotInstanceOfString() {
            initial.setRegisters(0, true, "Z", 1, null, "Ljava/lang/Object;");
            expected.setRegisters(0, false, "Z", 1, null, "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "instanceOfString()V", initial, expected);
        }

        @Test
        public void testStringIsInstanceOfObject() {
            initial.setRegisters(0, false, "Z", 1, "great maker", "Ljava/lang/String;");
            expected.setRegisters(0, true, "Z", 1, "great maker", "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "instanceOfObject()V", initial, expected);
        }

        @Test
        public void testUnknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType() {
            initial.setRegisters(0, false, "Z", 1, new UnknownValue(), "Ljava/lang/Object;");
            expected.setRegisters(0, new UnknownValue(), "Z", 1, new UnknownValue(), "Ljava/lang/Object;");

            VMTester.test(CLASS_NAME, "instanceOfString()V", initial, expected);
        }

        @Test
        public void testUnknownValueWithStringAsDeclaredTypeIsObject() {
            initial.setRegisters(0, false, "Z", 1, new UnknownValue(), "Ljava/lang/String;");
            expected.setRegisters(0, true, "Z", 1, new UnknownValue(), "Ljava/lang/String;");

            VMTester.test(CLASS_NAME, "instanceOfObject()V", initial, expected);
        }

    }

}
