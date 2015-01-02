package org.cf.smalivm;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.cf.smalivm.context.ExecutionGraph;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.BeforeClass;
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

    private static VirtualMachine vm;

    @BeforeClass
    public static void setupVM() throws Exception {
        vm = VMTester.getTestVM();
    }

    @Test
    public void testConstOpsHaveNoSideEffects() {
        String methodName = "ConstOps()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testEmptyMethodHasNoSideEffects() {
        String methodName = "EmptyMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeMethodThatInvokesUnknownMethodHasStrongSideEffects() {
        String methodName = "InvokeMethodThatInvokesUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeMethodWithNoSideEffectsHasNoSideEffects() {
        String methodName = "InvokeMethodWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeOfNonAnalyzableMethodHasStrongSideEffects() {
        String methodName = "InvokeOfNonAnalyzableMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeSideEffectMethodHasStrongSideEffects() {
        String methodName = "InvokeSideEffectMethod(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeUnknownMethodHasStrongSideEffects() {
        String methodName = "InvokeUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeWhitelistedMethodsHasNoSideEffects() {
        String methodName = "InvokeWhitelistedMethods()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testModifyInstanceMemberHasStrongSideEffects() {
        String methodName = "ModifyInstanceMember()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceNonLocalNonWhitelistedClassHasStrongSideEffects() {
        String methodName = "NewInstanceNonLocalNonWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceNonLocalWhitelistedClassHasNoSideEffects() {
        String methodName = "NewInstanceNonLocalWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() {
        String methodName = "NewInstanceOfMethodWithNoStaticInitializer()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() {
        // Method names? Pssh, we have method PARAGRAPHS.
        String methodName = "NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testWriteOutputStreamHasStrongSideEffects() {
        String methodName = "WriteOutputStream(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

}
