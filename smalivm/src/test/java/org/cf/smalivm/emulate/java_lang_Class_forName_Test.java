package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.smali.SmaliClassLoader;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

public class java_lang_Class_forName_Test {

    private VirtualMachine vm;
    private ClassManager classManager;
    private SmaliClassLoader classLoader;
    private ExecutionContextMethod method;
    private MethodState mState;
    private ExecutionContext ectx;

    private static final int CLASS_NAME_REGISTER = 0;
    private static final String CLASS_TYPE = "Ljava/lang/Class;";
    private static final String PARAMETER_TYPE = "Ljava/lang/String;";

    private static final Class<?> ARBITRARY_CLASS = Object.class;
    private static final Class<?> SAFE_CLASS = String.class;

    private static final String SAFE_CLASS_NAME_INTERNAL = "Ljava/lang/String;";
    private static final String SAFE_CLASS_NAME_BINARY = "java.lang.String";

    private static final String UNSAFE_CLASS_NAME_INTERNAL = "Landroid/util/SparseArray;";
    private static final String UNSAFE_CLASS_NAME_BINARY = "android.util.SparseArray";

    private static final String STRONG_SIDE_EFFECTS_CLASS_NAME_INTERNAL = "Landroid/util/Base64;";
    private static final String STRONG_SIDE_EFFECTS_CLASS_NAME_BINARY = "android.util.Base64";

    private static final String NON_LOCAL_CLASS_NAME_INTERNAL = "Lsome/unlocal;";

    private static final String UNKNOWN_CLASS_NAME_INTERNAL = "Lsome/random123/class;";
    private static final String UNKNOWN_CLASS_NAME_BINARY = "some.random123.class";

    @Before
    public void setUp() throws ClassNotFoundException {
        vm = mock(VirtualMachine.class);
        classManager = mock(ClassManager.class);
        when(classManager.isLocalClass(SAFE_CLASS_NAME_INTERNAL)).thenReturn(true);
        when(classManager.isLocalClass(UNSAFE_CLASS_NAME_INTERNAL)).thenReturn(true);
        when(classManager.isLocalClass(STRONG_SIDE_EFFECTS_CLASS_NAME_BINARY)).thenReturn(true);
        when(classManager.isLocalClass(NON_LOCAL_CLASS_NAME_INTERNAL)).thenReturn(false);
        when(vm.getClassManager()).thenReturn(classManager);

        classLoader = mock(SmaliClassLoader.class);
        doReturn(ARBITRARY_CLASS).when(classLoader).loadClass(UNSAFE_CLASS_NAME_BINARY);
        doReturn(ARBITRARY_CLASS).when(classLoader).loadClass(STRONG_SIDE_EFFECTS_CLASS_NAME_BINARY);
        when(classLoader.loadClass(UNKNOWN_CLASS_NAME_BINARY)).thenThrow(
                        new ClassNotFoundException(UNKNOWN_CLASS_NAME_BINARY));
        when(vm.getClassLoader()).thenReturn(classLoader);

        Configuration configuration = mock(Configuration.class);
        when(configuration.isSafe(SAFE_CLASS_NAME_INTERNAL)).thenReturn(true);
        when(configuration.isSafe(UNSAFE_CLASS_NAME_INTERNAL)).thenReturn(false);
        when(configuration.isSafe(STRONG_SIDE_EFFECTS_CLASS_NAME_BINARY)).thenReturn(false);
        when(vm.getConfiguration()).thenReturn(configuration);

        ectx = mock(ExecutionContext.class);
        mState = mock(MethodState.class);
        when(ectx.getMethodState()).thenReturn(mState);

        when(ectx.getClassSideEffectLevel(SAFE_CLASS_NAME_INTERNAL)).thenReturn(SideEffect.Level.NONE);
        when(ectx.getClassSideEffectLevel(UNSAFE_CLASS_NAME_INTERNAL)).thenReturn(SideEffect.Level.NONE);
        when(ectx.getClassSideEffectLevel(STRONG_SIDE_EFFECTS_CLASS_NAME_INTERNAL)).thenReturn(SideEffect.Level.STRONG);

        method = new java_lang_Class_forName();
    }

    @Test
    public void testSafeClassNameThatExistsReturnsActualClassAndHasNoSideEffects() throws Exception {
        HeapItem item = new HeapItem(SAFE_CLASS_NAME_BINARY, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(SAFE_CLASS), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testUnsafeClassNameReturnsClassAndHasNoSideEffects() throws Exception {
        HeapItem item = new HeapItem(UNSAFE_CLASS_NAME_BINARY, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(Object.class), eq(CLASS_TYPE));
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(UNSAFE_CLASS_NAME_INTERNAL);
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testStrongSideEffectsClassNameReturnsClassAndHasStrongSideEffects() throws Exception {
        HeapItem item = new HeapItem(STRONG_SIDE_EFFECTS_CLASS_NAME_BINARY, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(Object.class), eq(CLASS_TYPE));
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(STRONG_SIDE_EFFECTS_CLASS_NAME_INTERNAL);
        assertEquals(SideEffect.Level.STRONG, method.getSideEffectLevel());
    }

    @Test
    public void testUnknownClassNameThrowsExceptionAndAssignsNothing() throws Exception {
        HeapItem item = new HeapItem(UNKNOWN_CLASS_NAME_BINARY, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);

        method.execute(vm, ectx);

        Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
        expectedExceptions.add(new VirtualException(ClassNotFoundException.class, UNKNOWN_CLASS_NAME_BINARY));
        assertEquals(expectedExceptions, method.getExceptions());
        verify(mState, times(0)).assignReturnRegister(any(UnknownValue.class), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

}
