package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class Test_java_lang_Class_forName {

    private VirtualMachine vm;
    private SmaliClassManager classManager;
    private ExecutionContextMethod method;
    private MethodState mState;
    private ExecutionContext ectx;

    private static final int CLASS_NAME_REGISTER = 0;
    private static final String CLASS_TYPE = "Ljava/lang/Class;";
    private static final String PARAMETER_TYPE = "Ljava/lang/String;";

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        classManager = mock(SmaliClassManager.class);
        when(vm.getClassManager()).thenReturn(classManager);
        ectx = mock(ExecutionContext.class);
        mState = mock(MethodState.class);
        when(ectx.getMethodState()).thenReturn(mState);

        method = new java_lang_Class_forName();
    }

    @Test
    public void testSafeClassNameThatExistsReturnsActualClass() throws Exception {
        String className = "java.lang.String";
        HeapItem item = new HeapItem(className, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(String.class), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testLocalClassNameReturnsLocalClass() throws Exception {
        String smaliClassName = "Landroid/app/Activity;";
        when(classManager.isLocalClass(smaliClassName)).thenReturn(true);

        String className = "android.app.Activity";
        HeapItem item = new HeapItem(className, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);
        method.execute(vm, ectx);

        verify(mState, times(1)).assignReturnRegister(eq(new LocalClass(smaliClassName)), eq(CLASS_TYPE));
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(smaliClassName);
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

    @Test
    public void testUnknownClassNameThrowsExceptionAndReturnsUnknown() throws Exception {
        String smaliClassName = "Lsome/madeup/Class;";
        when(classManager.isLocalClass(smaliClassName)).thenReturn(false);

        String className = "some.madeup.Class";
        HeapItem item = new HeapItem(className, PARAMETER_TYPE);
        when(mState.peekParameter(CLASS_NAME_REGISTER)).thenReturn(item);

        Exception exception = null;
        try {
            method.execute(vm, ectx);
        } catch (Exception ex) {
            exception = ex;
        }
        assertNotNull(exception);
        assertEquals(ClassNotFoundException.class, exception.getClass());

        verify(mState, times(1)).assignReturnRegister(any(UnknownValue.class), eq(CLASS_TYPE));
        assertEquals(SideEffect.Level.NONE, method.getSideEffectLevel());
    }

}
