package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestSideEffects {

    /*
     * A side-effect is any modification of state that persists outside the method, e.g. changing class static or
     * instance variables, file and network IO, etc. To determine with 100% accuracy is tricky, and a lot of work, so we
     * take the shortcut of white listing certain classes and methods as not causing side effects. Knowing that a method
     * has no side effects lets the optimizer remove the invocation if the result is not used.
     */

    private static final String CLASS_NAME = "Lside_effects_test;";

    private VirtualMachine vm;

    @Test
    public void constOpsHaveNoSideEffects() throws VirtualMachineException {
        String methodName = "constOps()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void emptyMethodHasNoSideEffects() throws VirtualMachineException {
        String methodName = "emptyMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeMethodWithNoSideEffectsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "invokeMethodWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeOfNonAnalyzableMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "invokeOfNonAnalyzableMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeSideEffectMethodHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "invokeSideEffectMethod(Ljava/io/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void invokeWhitelistedMethodsHasNoSideEffects() throws VirtualMachineException {
        String methodName = "invokeWhitelistedMethods()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void modifyInstanceMemberHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "modifyInstanceMember()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceNonLocalWhitelistedClassHasNoSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceNonLocalWhitelistedClass()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfClassWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() throws
            VirtualMachineException {
        String methodName = "newInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfClassWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() throws
            VirtualMachineException {
        String methodName = "newInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.WEAK, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() throws VirtualMachineException {
        String methodName = "newInstanceOfMethodWithNoStaticInitializer()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Test
    public void newInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() throws
            VirtualMachineException {
        String methodName = "newInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.NONE, graph.getHighestSideEffectLevel());
    }

    @Before
    public void setupVM() throws Exception {
        vm = VMTester.spawnVM();
    }

    @Test
    public void writeOutputStreamHasStrongSideEffects() throws VirtualMachineException {
        String methodName = "writeOutputStream(Ljava/io/OutputStream;[B)V";
        ExecutionGraph graph = vm.execute(CLASS_NAME, methodName);

        assertEquals(SideEffect.Level.STRONG, graph.getHighestSideEffectLevel());
    }

}
