package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestInvokeOp_Static {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Linvoke_static_test;";

    @Test
    public void TestNoParametersReturnsVoid() {
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, null);

        VMTester.test(CLASS_NAME, "TestNoParametersReturnsVoid()V", expected);
    }

    @Test
    public void TestNoParametersReturnsInt() {
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x7);

        VMTester.test(CLASS_NAME, "TestNoParametersReturnsInt()V", expected);
    }

    @Test
    public void TestKnownParametersReturnsInt() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x5);
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x5);

        VMTester.testState(CLASS_NAME, "TestKnownParametersReturnsInt()V", initial, expected);
    }

    @Test
    public void TestUnknownMutableParameterIsMutated() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 3, 5, 7 }, 1, new UnknownValue(
                        "[I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1, new UnknownValue(
                        "[I"));

        VMTester.testState(CLASS_NAME, "TestNonLocalMethodWithUnknownMutableParameterIsMutated()V", initial, expected);
    }

    @Test
    public void TestKnownMutableParametersMutate() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 });
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[] { 0x0 });

        // TODO: This is probably where you left off.
        VMTester.testState(CLASS_NAME, "TestKnownMutableParametersMutate()V", initial, expected);
    }

    @Test
    public void TestKnownImmutableParametersNotMutate() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, "not mutated");
        SparseArray<Object> expected = VMTester.buildRegisterState(0, "not mutated");

        VMTester.testState(CLASS_NAME, "TestKnownImmutableParametersNotMutate()V", initial, expected);
    }

    @Test
    public void TestInfiniteRecursionExceedsCallDepth() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, 0);

        VMTester.testState(CLASS_NAME, "TestInfiniteRecursionExceedsCallDepth()V", initial, expected);
    }
}
