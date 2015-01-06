package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalField;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class Test_java_lang_Class_getField {

    private VirtualMachine vm;
    private MethodStateMethod method;
    private SmaliClassManager classManager;
    private MethodState mState;
    private HeapItem classItem;
    private HeapItem fieldNameItem;
    private static final int CLASS_INSTANCE_REGISTER = 0;
    private static final int FIELD_NAME_REGISTER = 1;
    private static final String FIELD_TYPE = "Ljava/lang/reflect/Field;";

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        classManager = mock(SmaliClassManager.class);
        when(vm.getClassManager()).thenReturn(classManager);

        classItem = mock(HeapItem.class);
        fieldNameItem = mock(HeapItem.class);
        mState = mock(MethodState.class);
        when(mState.peekParameter(CLASS_INSTANCE_REGISTER)).thenReturn(classItem);
        when(mState.peekParameter(FIELD_NAME_REGISTER)).thenReturn(fieldNameItem);
        method = new java_lang_Class_getField();
    }

    @Test
    public void testNonLocalClassForExistantFieldNameGivesExpectedField() throws Exception {
        Class<?> klazz = Integer.class;
        String fieldName = "MAX_VALUE";
        Field field = klazz.getField(fieldName);
        when(classItem.getValue()).thenReturn(klazz);
        when(fieldNameItem.getValue()).thenReturn(fieldName);

        method.execute(vm, mState);

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(1)).assignReturnRegister(field, FIELD_TYPE);
    }

    @Test
    public void testNonLocalClassForNonExistantFieldNameThrowsExceptionAndReturnsUnknown() throws Exception {
        Class<?> klazz = Integer.class;
        String fieldName = "DOES_NOT_EXIST";
        when(classItem.getValue()).thenReturn(klazz);
        when(fieldNameItem.getValue()).thenReturn(fieldName);

        Exception exception = null;
        try {
            method.execute(vm, mState);
        } catch (NoSuchFieldException ex) {
            exception = ex;
        }
        assertEquals(NoSuchFieldException.class, exception.getClass());

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));

        ArgumentCaptor<HeapItem> returnItem = ArgumentCaptor.forClass(HeapItem.class);
        verify(mState, times(1)).assignReturnRegister(returnItem.capture());
        assertEquals(UnknownValue.class, returnItem.getValue().getValue().getClass());
        assertEquals(FIELD_TYPE, returnItem.getValue().getType());
    }

    @Test
    public void testLocalClassForExistantFieldNameReturnsExpectedField() throws Exception {
        String smaliClassName = "Landroid/app/Activity;";
        LocalClass klazz = new LocalClass(smaliClassName);
        String fieldName = "RESULT_OK";
        LocalField field = new LocalField(smaliClassName + "->" + fieldName + ":I");
        when(classManager.isLocalClass(smaliClassName)).thenReturn(true);
        List<String> classFields = new LinkedList<String>();
        classFields.add("RESULT_OK:I");
        when(classManager.getFieldNameAndTypes(smaliClassName)).thenReturn(classFields);

        when(classItem.getValue()).thenReturn(klazz);
        when(fieldNameItem.getValue()).thenReturn(fieldName);

        method.execute(vm, mState);

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(1)).assignReturnRegister(field, FIELD_TYPE);
    }

    @Test
    public void testLocalClassForNonExistantFieldNameThrowsExceptionAndReturnsUnknown() throws Exception {
        String smaliClassName = "Landroid/app/Activity;";
        LocalClass klazz = new LocalClass(smaliClassName);
        String fieldName = "DOES_NOT_EXIST";
        when(classManager.isLocalClass(smaliClassName)).thenReturn(true);
        when(classManager.getFieldNameAndTypes(smaliClassName)).thenReturn(new LinkedList<String>());

        when(classItem.getValue()).thenReturn(klazz);
        when(fieldNameItem.getValue()).thenReturn(fieldName);

        Exception exception = null;
        try {
            method.execute(vm, mState);
        } catch (NoSuchFieldException ex) {
            exception = ex;
        }
        assertEquals(NoSuchFieldException.class, exception.getClass());

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));

        ArgumentCaptor<HeapItem> returnItem = ArgumentCaptor.forClass(HeapItem.class);
        verify(mState, times(1)).assignReturnRegister(returnItem.capture());
        assertEquals(UnknownValue.class, returnItem.getValue().getValue().getClass());
        assertEquals(FIELD_TYPE, returnItem.getValue().getType());
    }

}
