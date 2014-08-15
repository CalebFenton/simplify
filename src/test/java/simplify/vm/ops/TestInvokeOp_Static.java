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
    public void TestInvokeReturnsVoidReturnsVoid() {
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, null);

        VMTester.test(CLASS_NAME, "InvokeReturnsVoid()V", expected);
    }

    @Test
    public void TestInvokeReturnsIntReturnsInt() {
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x7);

        VMTester.test(CLASS_NAME, "InvokeReturnsInt()V", expected);
    }

    @Test
    public void TestInvokeReturnsParameterReturnsParameter() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, 0x5);
        SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x5);

        VMTester.testState(CLASS_NAME, "InvokeReturnsParameter()V", initial, expected);
    }

    @Test
    public void TestInvokeTryMutateStringDoesNotMutateParameter() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, "not mutated");
        SparseArray<Object> expected = VMTester.buildRegisterState(0, "not mutated");

        VMTester.testState(CLASS_NAME, "InvokeTryMutateString()V", initial, expected);
    }

    @Test
    public void InvokeTryMutateStringBuilderDoesMutateParameter() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new StringBuilder("i have been"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new StringBuilder("i have been mutated"));

        VMTester.testState(CLASS_NAME, "InvokeTryMutateStringBuilder()V", initial, expected);
    }

    @Test
    public void TestInvokeNonLocalMethodWithKnownAndUnknownMutableParametersMutatesBoth() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 3, 5, 7 }, 1, new UnknownValue("[I"));
        SparseArray<Object> expected = VMTester
                        .buildRegisterState(0, new UnknownValue("[I"), 1, new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "InvokeNonLocalMethodWithKnownAndUnknownMutableParameters()V", initial, expected);
    }

    @Test
    public void TestKnownMutableParametersAreMutatedWithDeterministicExecution() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, 0);
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[] { 0x0 }, 1, new int[] { 0x0 });

        VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                        expected);
    }

    @Test
    public void TestKnownMutableParametersAreMutatedWithNonDeterministicExecution() {
        SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, new UnknownValue("I"));
        SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), new UnknownValue("[I"));

        VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                        expected);
    }

}
