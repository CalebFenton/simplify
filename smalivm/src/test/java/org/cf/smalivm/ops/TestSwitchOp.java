package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestSwitchOp {

    private static final String CLASS_NAME = "Lswitch_test;";

    public static class TestPackedSwitch {
        private static final String METHOD_NAME = "PackedSwitch()V";

        @Test
        public void TestKnownPredicateVisitsExpectedLabel() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            int[] expected = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void TestUnhandledPredicateVisitsNextOp() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100);
            int[] expected = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void TestUnknownPredicateVisitsAllLabelsAndNextOp() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            int[] expected = new int[] { 0, 1, 4, 5, 6, 7, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }
    }

    public static class TestSparseSwitch {
        private static final String METHOD_NAME = "SparseSwitch()V";

        @Test
        public void TestKnownPredicateVisitsExpectedLabel() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 1);
            int[] expected = new int[] { 0, 1, 5, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void TestUnhandledPredicateVisitsNextOp() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 100);
            int[] expected = new int[] { 0, 1, 4, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }

        @Test
        public void TestUnknownPredicateVisitsAllLabelsAndNextOp() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new UnknownValue("I"));
            int[] expected = new int[] { 0, 1, 4, 5, 6, 8 };
            VMTester.testVisitation(CLASS_NAME, METHOD_NAME, initial, expected);
        }
    }

}
