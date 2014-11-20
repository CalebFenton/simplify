package org.cf.smalivm;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Before;
import org.junit.Test;

public class TestSideEffects {
    /*
     * A side-effect is any modification of state that persists outside the method, e.g. changing class static or
     * instance variables, file and network IO, etc. To determine with 100% accuracy is tricky, and a lot of work, so we
     * take the shortcut of white listing certain classes and methods as not causing side effects. Knowing that a method
     * has no side effects lets the optimizer remove the invocation if the result is not used.
     */

    private static final String CLASS_NAME = "Lside_effects_test;";
    private static Map<String, BuilderClassDef> classNameToDef;

    private VirtualMachine vm;

    @Before
    public void setupVM() throws Exception {
        vm = VMTester.getTestVM();
    }

    @Test
    public void TestConstOpsHaveNoSideEffects() {
        String methodName = "ConstOps()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestEmptyMethodHasNoSideEffects() {
        String methodName = "EmptyMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeMethodThatInvokesUnknownMethodHasStrongSideEffects() {
        String methodName = "InvokeMethodThatInvokesUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeMethodWithNoSideEffectsHasNoSideEffects() {
        String methodName = "InvokeMethodWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeOfNonAnalyzableMethodHasStrongSideEffects() {
        String methodName = "InvokeOfNonAnalyzableMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeSideEffectMethodHasStrongSideEffects() {
        String methodName = "InvokeSideEffectMethod(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeUnknownMethodHasStrongSideEffects() {
        String methodName = "InvokeUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestInvokeWhitelistedMethodsHasNoSideEffects() {
        String methodName = "InvokeWhitelistedMethods()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestModifyInstanceMemberHasStrongSideEffects() {
        String methodName = "ModifyInstanceMember()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceNonLocalNonWhitelistedClassHasStrongSideEffects() {
        String methodName = "NewInstanceNonLocalNonWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceNonLocalWhitelistedClassHasNoSideEffects() {
        String methodName = "NewInstanceNonLocalWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() {
        String methodName = "NewInstanceOfMethodWithNoStaticInitializer()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestNewInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() {
        // Method names? Pssh, we have method PARAGRAPHS.
        String methodName = "NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void TestWriteOutputStreamHasStrongSideEffects() {
        String methodName = "WriteOutputStream(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

}
