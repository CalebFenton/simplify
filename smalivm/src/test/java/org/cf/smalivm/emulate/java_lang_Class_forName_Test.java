package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

public class java_lang_Class_forName_Test {

    private VirtualMachine vm;
    private ClassManager classManager;
    private ExecutionContextMethod method;
    private MethodState mState;
    private ExecutionContext ectx;

    private static final int CLASS_NAME_REGISTER = 0;
    private static final String CLASS_TYPE = "Ljava/lang/Class;";
    private static final String PARAMETER_TYPE = "Ljava/lang/String;";

    private static final String SAFE_CLASS_NAME = "java.lang.String";
    private static final Class<?> SAFE_CLASS = String.class;
    private static final String LOCAL_CLASS_NAME_SMALI = "Landroid/app/Activity;";
    private static final String LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI = "Landroid/app/YUP;";
    private static final String LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_JAVA = "android.app.YUP";
    private static final String LOCAL_CLASS_NAME_JAVA = "android.app.Activity";
    private static final LocalClass LOCAL_CLASS = new LocalClass(LOCAL_CLASS_NAME_SMALI);
    private static final LocalClass LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS = new LocalClass(
                    LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI);

    private static final String UNKNOWN_CLASS_NAME_SMALI = "Lsome/random123/class;";
    private static final String UNKNOWN_CLASS_NAME_JAVA = "some.random123.class";

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        classManager = mock(ClassManager.class);
        when(vm.getClassManager()).thenReturn(classManager);
        ectx = mock(ExecutionContext.class);
        mState = mock(MethodState.class);
        when(ectx.getMethodState()).thenReturn(mState);

        when(classManager.isLocalClass(LOCAL_CLASS_NAME_SMALI)).thenReturn(true);
        when(classManager.isLocalClass(UNKNOWN_CLASS_NAME_SMALI)).thenReturn(false);
        when(classManager.isLocalClass(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI)).thenReturn(true);

        when(ectx.getClassSideEffectLevel(LOCAL_CLASS_NAME_SMALI)).thenReturn(SideEffect.Level.NONE);
        when(ectx.getClassSideEffectLevel(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI)).thenReturn(
                        SideEffect.Level.STRONG);

        method = new java_lang_Class_forName();
    }

    @Test
    public void testSafeClassNameThatExistsReturnsActualClassAndHasNoSideEffects() throws Exception {
        HeapItem item = new HeapItem(SAFE_CLASS_NAME, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(SAFE_CLASS), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testLocalClassNameReturnsLocalClassAndStaticallyInitializesClassAndHasNoSideEffects() throws Exception {
        HeapItem item = new HeapItem(LOCAL_CLASS_NAME_JAVA, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(LOCAL_CLASS), eq(CLASS_TYPE));
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(LOCAL_CLASS_NAME_SMALI);
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testLocalStrongSideEffectsClassNameReturnsLocalClassAndHasStrongSideEffects() throws Exception {
        HeapItem item = new HeapItem(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_JAVA, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS), eq(CLASS_TYPE));
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI);
        assertEquals(SideEffect.Level.STRONG, method.getSideEffectLevel());
    }

    @Test
    public void testUnknownClassNameThrowsExceptionAndAssignsNothing() throws Exception {
        HeapItem item = new HeapItem(UNKNOWN_CLASS_NAME_JAVA, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);

        method.execute(vm, ectx);

        Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
        expectedExceptions.add(new VirtualException(ClassNotFoundException.class, UNKNOWN_CLASS_NAME_SMALI));
        assertEquals(expectedExceptions, method.getExceptions());
        verify(mState, times(0)).assignReturnRegister(any(UnknownValue.class), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

}
