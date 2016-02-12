package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class SwitchOpTest {

    private static final String CLASS_NAME = "Lswitch_test;";

    public static class PackedSwitch {

        private static final String METHOD_NAME = "packedSwitch()V";

        private VMState initial;

        @Before
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "Z");
            int[] expectedAddresses = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }

        @Test
        public void testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "S");
            int[] expectedAddresses = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }

        @Test
        public void testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegisters(0, new UnknownValue(), "I");
            int[] expectedAddresses = new int[] { 0, 1, 4, 5, 6, 7, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }
    }

    public static class SparseSwitch {

        private static final String METHOD_NAME = "sparseSwitch()V";

        private VMState initial;

        @Before
        public void setUp() {
            initial = new VMState();
        }

        @Test
        public void testKnownPredicateVisitsExpectedLabel() {
            initial.setRegisters(0, 1, "B");
            int[] expectedAddresses = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }

        @Test
        public void testUnhandledPredicateVisitsNextOp() {
            initial.setRegisters(0, 100, "I");
            int[] expectedAddresses = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }

        @Test
        public void testUnknownPredicateVisitsAllLabelsAndNextOp() {
            initial.setRegisters(0, new UnknownValue(), "I");
            int[] expectedAddresses = new int[] { 0, 1, 4, 5, 6, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expectedAddresses);
        }
    }

}
