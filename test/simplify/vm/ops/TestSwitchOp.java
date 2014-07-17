package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestSwitchOp {

    private static final String CLASS_NAME = "Lswitch_test;";

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    @Test
    public void TestPackedSwitchWithKnownPredicateVisitsExpectedLabel() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 1);
        int[] expected = new int[] { 0, 1, 5, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestPackedSwitch()V", initial, expected);
    }

    @Test
    public void TestPackedSwitchWithUnhandledPredicateVisitsNextOp() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 100);
        int[] expected = new int[] { 0, 1, 4, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestPackedSwitch()V", initial, expected);
    }

    @Test
    public void TestPackedSwitchWithUnknownPredicateVisitsAllLabelsAndNextOp() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new UnknownValue("I"));
        int[] expected = new int[] { 0, 1, 4, 5, 6, 7, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestPackedSwitch()V", initial, expected);
    }

    @Test
    public void TestSparseSwitchWithKnownPredicateVisitsExpectedLabel() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 1);
        int[] expected = new int[] { 0, 1, 5, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestSparseSwitch()V", initial, expected);
    }

    @Test
    public void TestSparseSwitchWithUnhandledPredicateVisitsNextOp() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 100);
        int[] expected = new int[] { 0, 1, 4, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestSparseSwitch()V", initial, expected);
    }

    @Test
    public void TestSparseSwitchWithUnknownPredicateVisitsAllLabelsAndNextOp() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new UnknownValue("I"));
        int[] expected = new int[] { 0, 1, 4, 5, 6, 7, 8 };

        VMTester.testVisitation(CLASS_NAME, "TestSparseSwitch()V", initial, expected);
    }

}
