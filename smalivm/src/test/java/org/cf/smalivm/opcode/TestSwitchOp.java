package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestSwitchOp {

    public static class TestPackedSwitch {
        private static final String METHOD_NAME = "PackedSwitch()V";

        @Test
        public void testKnownPredicateVisitsExpectedLabel() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 1, "I");
            int[] expected = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void testUnhandledPredicateVisitsNextOp() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 100, "I");
            int[] expected = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void testUnknownPredicateVisitsAllLabelsAndNextOp() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            int[] expected = new int[] { 0, 1, 4, 5, 6, 7, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }
    }

    public static class TestSparseSwitch {
        private static final String METHOD_NAME = "SparseSwitch()V";

        @Test
        public void testKnownPredicateVisitsExpectedLabel() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 1, "I");
            int[] expected = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void testUnhandledPredicateVisitsNextOp() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 100, "I");
            int[] expected = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void testUnknownPredicateVisitsAllLabelsAndNextOp() {
            TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new UnknownValue(), "I");
            int[] expected = new int[] { 0, 1, 4, 5, 6, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }
    }

    private static final String CLASS_NAME = "Lswitch_test;";

}
