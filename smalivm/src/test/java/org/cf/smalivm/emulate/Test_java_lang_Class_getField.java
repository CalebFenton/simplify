package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.SmaliClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalField;
import org.junit.Before;
import org.junit.Test;

public class Test_java_lang_Class_getField {

    private static final int CLASS_INSTANCE_REGISTER = 0;
    private static final int FIELD_NAME_REGISTER = 1;
    private static final String FIELD_TYPE = "Ljava/lang/reflect/Field;";
    private static final String LOCAL_CLASS_NAME_SMALI = "Llocal/Class;";
    private static final String LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI = "Llocal/ClassWithStrong;";
    private static final LocalClass LOCAL_CLASS = new LocalClass(LOCAL_CLASS_NAME_SMALI);
    private static final LocalClass LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS = new LocalClass(
                    LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI);
    private static final String EXISTENT_FIELD_NAME = "EXISTENT_FIELD";
    private static final String EXISTENT_FIELD_NAME_AND_TYPE = EXISTENT_FIELD_NAME + ":I";
    private static final String NON_EXISTENT_FIELD_NAME = "DOES_NOT_EXIST";
    private static final LocalField LOCAL_FIELD = new LocalField(
                    LOCAL_CLASS_NAME_SMALI + "->" + EXISTENT_FIELD_NAME_AND_TYPE);
    private static final LocalField LOCAL_FIELD_FROM_STRONG_SIDE_EFFECTS_CLASS = new LocalField(
                    LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI + "->" + EXISTENT_FIELD_NAME_AND_TYPE);

    private VirtualMachine vm;
    private MethodStateMethod method;
    private SmaliClassManager classManager;
    private MethodState mState;
    private HeapItem classItem;
    private HeapItem fieldNameItem;

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        classManager = mock(SmaliClassManager.class);
        when(vm.getClassManager()).thenReturn(classManager);
        when(classManager.isLocalClass(LOCAL_CLASS_NAME_SMALI)).thenReturn(true);
        when(classManager.isLocalClass(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI)).thenReturn(true);

        List<String> classFields = new LinkedList<String>();
        classFields.add(EXISTENT_FIELD_NAME_AND_TYPE);
        when(classManager.getFieldNameAndTypes(LOCAL_CLASS_NAME_SMALI)).thenReturn(classFields);
        when(classManager.getFieldNameAndTypes(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS_NAME_SMALI))
                        .thenReturn(classFields);

        classItem = mock(HeapItem.class);
        fieldNameItem = mock(HeapItem.class);
        mState = mock(MethodState.class);
        when(mState.peekParameter(CLASS_INSTANCE_REGISTER)).thenReturn(classItem);
        when(mState.peekParameter(FIELD_NAME_REGISTER)).thenReturn(fieldNameItem);

        method = new java_lang_Class_getField();
    }

    @Test
    public void testNonLocalClassAndExistentFieldNameGivesExpectedField() throws Exception {
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
    public void testNonLocalClassAndNonExistentFieldNameThrowsExceptionAndAssignsNoReturnRegister() throws Exception {
        Class<?> klazz = Integer.class;
        when(classItem.getValue()).thenReturn(klazz);
        when(fieldNameItem.getValue()).thenReturn(NON_EXISTENT_FIELD_NAME);

        method.execute(vm, mState);

        Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
        expectedExceptions.add(new VirtualException(NoSuchFieldException.class, NON_EXISTENT_FIELD_NAME));
        assertEquals(expectedExceptions, method.getExceptions());
        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(0)).assignReturnRegister(any(HeapItem.class));
        verify(mState, times(0)).assignReturnRegister(any(Object.class), any(String.class));
    }

    @Test
    public void testLocalClassForExistantFieldNameReturnsExpectedField() throws Exception {
        when(classItem.getValue()).thenReturn(LOCAL_CLASS);
        when(fieldNameItem.getValue()).thenReturn(EXISTENT_FIELD_NAME);

        method.execute(vm, mState);

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(1)).assignReturnRegister(eq(LOCAL_FIELD), eq(FIELD_TYPE));
    }

    @Test
    public void testLocalClassForNonExistantFieldNameThrowsExceptionAndAssignsNoReturnRegister() throws Exception {
        when(classItem.getValue()).thenReturn(LOCAL_CLASS);
        when(fieldNameItem.getValue()).thenReturn(NON_EXISTENT_FIELD_NAME);

        method.execute(vm, mState);

        Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
        expectedExceptions.add(new VirtualException(NoSuchFieldException.class, NON_EXISTENT_FIELD_NAME));
        assertEquals(expectedExceptions, method.getExceptions());

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(0)).assignReturnRegister(any(HeapItem.class));
        verify(mState, times(0)).assignReturnRegister(any(Object.class), any(String.class));
    }

    @Test
    public void testLocalClassWithStrongSideEffectsForExistantFieldNameHasStrongSideEffectLevel() throws Exception {
        when(classItem.getValue()).thenReturn(LOCAL_CLASS_WITH_STRONG_SIDE_EFFECTS);
        when(fieldNameItem.getValue()).thenReturn(EXISTENT_FIELD_NAME);

        method.execute(vm, mState);

        verify(mState, times(1)).peekParameter(eq(CLASS_INSTANCE_REGISTER));
        verify(mState, times(1)).peekParameter(eq(FIELD_NAME_REGISTER));
        verify(mState, times(1)).assignReturnRegister(eq(LOCAL_FIELD_FROM_STRONG_SIDE_EFFECTS_CLASS), eq(FIELD_TYPE));
    }

}
