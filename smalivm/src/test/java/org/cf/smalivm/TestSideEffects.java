package org.cf.smalivm;

import static org.junit.Assert.assertEquals;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.exception.VirtualMachineException;
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

    private VirtualMachine vm;

    @Before
    public void setupVM() throws Exception {
        vm = VMTester.getTestVM();
    }

    @Test
    public void testConstOpsHaveNoSideEffects() throws VirtualMachineException {
        String methodName = "ConstOps()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testEmptyMethodHasNoSideEffects() throws VirtualMachineException {
        String methodName = "EmptyMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeMethodThatInvokesUnknownMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "InvokeMethodThatInvokesUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeMethodWithNoSideEffectsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "InvokeMethodWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeOfNonAnalyzableMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "InvokeOfNonAnalyzableMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeSideEffectMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "InvokeSideEffectMethod(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeUnknownMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "InvokeUnknownMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testInvokeWhitelistedMethodsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "InvokeWhitelistedMethods()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testModifyInstanceMemberHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "ModifyInstanceMember()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceNonLocalNonWhitelistedClassHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "NewInstanceNonLocalNonWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceNonLocalWhitelistedClassHasNoSideEffects() throws VirtualMachineException {
        String methodName = "NewInstanceNonLocalWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects()
                    throws VirtualMachineException {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects()
                    throws VirtualMachineException {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() throws VirtualMachineException {
        String methodName = "NewInstanceOfMethodWithNoStaticInitializer()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testNewInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects()
                    throws VirtualMachineException {
        // Method names? Pssh, we have method PARAGRAPHS.
        String methodName = "NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void testWriteOutputStreamHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "WriteOutputStream(Ljava/lang/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

}
