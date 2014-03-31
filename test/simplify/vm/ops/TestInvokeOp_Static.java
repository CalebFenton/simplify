package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.VMTester;
import simplify.vm.types.UnknownValue;
import util.SparseArray;

public class TestInvokeOp_Static {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final String CLASS_NAME = "Linvoke_static_test;";

    @Test
    public void TestNoParametersReturnsVoid() {
        SparseArray<Object> expected = MethodContext.buildRegisterState(MethodContext.ResultRegister, null);

        VMTester.test(CLASS_NAME, "TestNoParametersReturnsVoid()V", expected);
    }

    @Test
    public void TestNoParametersReturnsInt() {
        SparseArray<Object> expected = MethodContext.buildRegisterState(MethodContext.ResultRegister, 0x7);

        VMTester.test(CLASS_NAME, "TestNoParametersReturnsInt()V", expected);
    }

    @Test
    public void TestKnownParametersReturnsInt() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, 0x5);
        SparseArray<Object> expected = MethodContext.buildRegisterState(MethodContext.ResultRegister, 0x5);

        VMTester.test(CLASS_NAME, "TestKnownParametersReturnsInt()V", initial, expected);
    }

    @Test
    public void TestUnknownParameterReturnsUnknown() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new UnknownValue("I"));
        SparseArray<Object> expected = MethodContext.buildRegisterState(MethodContext.ResultRegister, new UnknownValue(
                        "I"));

        VMTester.test(CLASS_NAME, "TestUnknownParameterReturnsUnknown()V", initial, expected);
    }

    @Test
    public void TestKnownMutableParametersMutate() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, new int[] { 0x5 });
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, new int[] { 0x0 });

        VMTester.test(CLASS_NAME, "TestKnownMutableParametersMutate()V", initial, expected);
    }

    @Test
    public void TestKnownImmutableParametersNotMutate() {
        SparseArray<Object> initial = MethodContext.buildRegisterState(0, "not mutated");
        SparseArray<Object> expected = MethodContext.buildRegisterState(0, "not mutated");

        VMTester.test(CLASS_NAME, "TestKnownImmutableParametersNotMutate()V", initial, expected);
    }
}
