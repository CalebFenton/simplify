package simplify.vm.ops;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import simplifier.Main;
import simplifier.vm.context.MethodContext;
import simplifier.vm.type.LocalInstance;
import simplifier.vm.type.UnknownValue;
import simplify.vm.VMTester;
import util.SparseArray;

@RunWith(Enclosed.class)
public class TestInvokeOp {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static class TestInvokeVirtual {
        private static final String CLASS_NAME = "Linvoke_virtual_test;";

        @Test
        public void TestInvokeReturnsVoidReturnsVoid() {
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));
            SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, null);

            VMTester.testState(CLASS_NAME, "InvokeReturnsVoid()V", initial, expected);
        }

        @Test
        public void TestInvokeReturnsIntReturnsInt() {
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));
            SparseArray<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x7);

            VMTester.testState(CLASS_NAME, "InvokeReturnsInt()V", initial, expected);
        }

        @Test
        public void TestInvokeReturnsParameterReturnsParameter() {
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), 1, 0x5);
            SparseArray<Object> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), 1, 0x5,
                            MethodContext.ResultRegister, 0x5);

            VMTester.testState(CLASS_NAME, "InvokeReturnsParameter()V", initial, expected);
        }
    }

    public static class TestInvokeStatic {
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
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 3, 5, 7 }, 1, new UnknownValue(
                            "[I"));
            SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1, new UnknownValue(
                            "[I"));

            VMTester.testState(CLASS_NAME, "InvokeNonLocalMethodWithKnownAndUnknownMutableParameters()V", initial,
                            expected);
        }

        @Test
        public void TestKnownMutableParametersAreMutatedWithDeterministicExecution() {
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, 0);
            SparseArray<Object> expected = VMTester.buildRegisterState(0, new int[] { 0x0 }, 1, 0);

            VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }

        @Test
        public void TestKnownMutableParametersAreMutatedWithNonDeterministicExecution() {
            SparseArray<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, new UnknownValue("I"));
            SparseArray<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1, new UnknownValue(
                            "I"));

            VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }
    }
}
