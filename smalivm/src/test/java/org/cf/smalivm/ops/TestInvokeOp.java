package org.cf.smalivm.ops;

import gnu.trove.map.TIntObjectMap;

import java.util.Map;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.type.LocalInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TestInvokeOp {

    public static class TestInvokeVirtual {
        private static final String CLASS_NAME = "Linvoke_virtual_test;";

        @Test
        public void TestInvokeReturnsVoidReturnsVoid() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, null);

            VMTester.testState(CLASS_NAME, "InvokeReturnsVoid()V", initial, expected);
        }

        @Test
        public void TestInvokeReturnsIntReturnsInt() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x7);

            VMTester.testState(CLASS_NAME, "InvokeReturnsInt()V", initial, expected);
        }

        @Test
        public void TestInvokeReturnsParameterReturnsParameter() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), 1, 0x5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new LocalInstance(CLASS_NAME), 1, 0x5,
                            MethodContext.ResultRegister, 0x5);

            VMTester.testState(CLASS_NAME, "InvokeReturnsParameter()V", initial, expected);
        }
    }

    public static class TestInvokeStatic {
        private static final String CLASS_NAME = "Linvoke_static_test;";

        @Test
        public void TestInvokeReturnsVoidReturnsVoid() {
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, null);

            VMTester.test(CLASS_NAME, "InvokeReturnsVoid()V", expected);
        }

        @Test
        public void TestInvokeReturnsIntReturnsInt() {
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x7);

            VMTester.test(CLASS_NAME, "InvokeReturnsInt()V", expected);
        }

        @Test
        public void TestInvokeReturnsParameterReturnsParameter() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, 0x5);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(MethodContext.ResultRegister, 0x5);

            VMTester.testState(CLASS_NAME, "InvokeReturnsParameter()V", initial, expected);
        }

        @Test
        public void TestInvokeTryMutateStringDoesNotMutateParameter() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, "not mutated");
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, "not mutated");

            VMTester.testState(CLASS_NAME, "InvokeTryMutateString()V", initial, expected);
        }

        @Test
        public void InvokeTryMutateStringBuilderDoesMutateParameter() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new StringBuilder("i have been"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new StringBuilder("i have been mutated"));

            VMTester.testState(CLASS_NAME, "InvokeTryMutateStringBuilder()V", initial, expected);
        }

        @Test
        public void TestInvokeNonLocalMethodWithKnownAndUnknownMutableParametersMutatesBoth() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 3, 5, 7 }, 1, new UnknownValue(
                            "[I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1,
                            new UnknownValue("[I"));

            VMTester.testState(CLASS_NAME, "InvokeNonLocalMethodWithKnownAndUnknownMutableParameters()V", initial,
                            expected);
        }

        @Test
        public void TestKnownMutableParametersAreMutatedWithDeterministicExecution() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, 0);
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new int[] { 0x0 }, 1, 0);

            VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }

        @Test
        public void TestKnownMutableParametersAreMutatedWithNonDeterministicExecution() {
            TIntObjectMap<Object> initial = VMTester.buildRegisterState(0, new int[] { 0x5 }, 1, new UnknownValue("I"));
            TIntObjectMap<Object> expected = VMTester.buildRegisterState(0, new UnknownValue("[I"), 1,
                            new UnknownValue("I"));

            VMTester.testState(CLASS_NAME, "InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V", initial,
                            expected);
        }

        @Test
        public void TestInvokeMutateStaticClassFieldPropigatesChanges() {
            Map<String, Map<String, Object>> initial = VMTester.buildClassNameToFieldValue(CLASS_NAME, "mutable:[I",
                            new int[] { 3, 3, 3 });
            Map<String, Map<String, Object>> expected = VMTester.buildClassNameToFieldValue(CLASS_NAME, "mutable:[I",
                            new int[] { 0, 3, 3 });

            VMTester.testExpectedClassState(CLASS_NAME, "InvokeMutateStaticClassField()V", initial, expected);
        }

        @Test
        public void TestInvokeMutateStaticClassFieldNonDeterministicallyPropigatesUnknown() {
            Map<String, Map<String, Object>> initial = VMTester.buildClassNameToFieldValue(CLASS_NAME, "mutable:[I",
                            new int[] { 3, 3, 3 });
            Map<String, Map<String, Object>> expected = VMTester.buildClassNameToFieldValue(CLASS_NAME, "mutable:[I",
                            new UnknownValue("[I"));

            VMTester.testExpectedClassState(CLASS_NAME, "InvokeMutateStaticClassFieldNonDeterministically()V", initial,
                            expected);
        }
    }
}
