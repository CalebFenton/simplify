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
import java.util.Set;

import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class java_lang_reflect_Field_get_Test {

    private VirtualMachine vm;
    private ExecutionContextMethod method;
    private ClassManager classManager;
    private ExecutionContext ectx;
    private ExecutionContext callerContext;
    private MethodState mState;
    private HeapItem fieldItem;
    private HeapItem instanceItem;
    private Configuration configuration;
    private StaticFieldAccessor staticFieldAccessor;

    private static final int FIELD_REGISTER = 0;
    private static final int INSTANCE_REGISTER = 1;

    private static final String DUMMY_CLASS_NAME_INTERNAL = ClassNameUtils.toInternal(DummyClass.class);
    private static final String DUMMY_CLASS_NAME_BINARY = ClassNameUtils.internalToBinary(DUMMY_CLASS_NAME_INTERNAL);
    private static final String DUMMER_INNER_CLASS_NAME_INTERNAL = ClassNameUtils
                    .toInternal(DummyClass.DummyInnerClass.class);
    private static final String DUMMER_INNER_CLASS_NAME_BINARY = ClassNameUtils
                    .internalToBinary(DUMMER_INNER_CLASS_NAME_INTERNAL);

    private static final String DUMMY_CHILD_CLASS_NAME_INTERNAL = "Ldummy/Child;";
    private static final String DUMMY_CHILD_CLASS_NAME_BINARY = "dummy.Child";
    private static final String NON_DUMMY_CLASS_NAME_INTERNAL = "Lnondummy/Klazz;";
    private static final String NON_DUMMY_CLASS_NAME_BINARY = "nondummy.Klazz";

    private static final String DUMMY_PUBLIC_STATIC_FIELD_NAME = "publicStaticField";
    private static final String DUMMY_PUBLIC_STATIC_FIELD_TYPE = "I";
    private static final String DUMMY_PUBLIC_STATIC_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PUBLIC_STATIC_FIELD_NAME + ":" + DUMMY_PUBLIC_STATIC_FIELD_TYPE;
    private static final int DUMMY_PUBLIC_STATIC_FIELD_VALUE = 0x42;

    private static final String DUMMY_PRIVATE_STATIC_FIELD_NAME = "privateStaticField";
    private static final String DUMMY_PRIVATE_STATIC_FIELD_TYPE = "I";
    private static final String DUMMY_PRIVATE_STATIC_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PRIVATE_STATIC_FIELD_NAME + ":" + DUMMY_PRIVATE_STATIC_FIELD_TYPE;
    private static final int DUMMY_PRIVATE_STATIC_FIELD_VALUE = 0x43;

    private static final String DUMMY_PROTECTED_STATIC_FIELD_NAME = "protectedStaticField";
    private static final String DUMMY_PROTECTED_STATIC_FIELD_TYPE = "I";
    private static final String DUMMY_PROTECTED_STATIC_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PROTECTED_STATIC_FIELD_NAME + ":" + DUMMY_PROTECTED_STATIC_FIELD_TYPE;
    private static final int DUMMY_PROTECTED_STATIC_FIELD_VALUE = 0x33;

    private static final String DUMMY_PUBLIC_INSTANCE_FIELD_NAME = "publicInstanceField";
    private static final String DUMMY_PUBLIC_INSTANCE_FIELD_TYPE = "I";
    private static final String DUMMY_PUBLIC_INSTANCE_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PUBLIC_INSTANCE_FIELD_NAME + ":" + DUMMY_PUBLIC_INSTANCE_FIELD_TYPE;
    private static final int DUMMY_PUBLIC_INSTANCE_FIELD_VALUE = 0x44;

    private static final String DUMMY_PROTECTED_INSTANCE_FIELD_NAME = "protectedInstanceField";
    private static final String DUMMY_PROTECTED_INSTANCE_FIELD_TYPE = "I";
    private static final String DUMMY_PROTECTED_INSTANCE_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PROTECTED_INSTANCE_FIELD_NAME + ":" + DUMMY_PROTECTED_INSTANCE_FIELD_TYPE;
    private static final int DUMMY_PROTECTED_INSTANCE_FIELD_VALUE = 0x35;

    private static final String DUMMY_PRIVATE_INSTANCE_FIELD_NAME = "privateInstanceField";
    private static final String DUMMY_PRIVATE_INSTANCE_FIELD_TYPE = "I";
    private static final String DUMMY_PRIVATE_INSTANCE_FIELD_DESCRIPTOR = DUMMY_CLASS_NAME_INTERNAL + "->" + DUMMY_PRIVATE_INSTANCE_FIELD_NAME + ":" + DUMMY_PRIVATE_INSTANCE_FIELD_TYPE;
    private static final int DUMMY_PRIVATE_INSTANCE_FIELD_VALUE = 0x45;

    // TODO: medium - add package-private tests

    private static final String MOCKED_CLASS = "Lmy/mocked/Class;";
    private static final String MOCKED_CLASS_JAVA = "my.mocked.Class";
    private static final String MOCKED_METHOD = "mockedMethod()V";
    private static final String MOCKED_METHOD_DESCRIPTOR = MOCKED_CLASS + "->" + MOCKED_METHOD;

    @Before
    public void setUp() throws UnknownAncestors {
        vm = mock(VirtualMachine.class);
        ectx = mock(ExecutionContext.class);

        setupClassManager();

        configuration = mock(Configuration.class);
        when(configuration.isSafe(DUMMY_CLASS_NAME_INTERNAL)).thenReturn(true);
        when(vm.getConfiguration()).thenReturn(configuration);

        setupFieldAccessor();

        fieldItem = mock(HeapItem.class);
        instanceItem = mock(HeapItem.class);
        mState = mock(MethodState.class);
        when(mState.peekParameter(FIELD_REGISTER)).thenReturn(fieldItem);
        when(mState.peekParameter(INSTANCE_REGISTER)).thenReturn(instanceItem);

        when(ectx.getMethodState()).thenReturn(mState);
        callerContext = mock(ExecutionContext.class);
        when(callerContext.getMethodDescriptor()).thenReturn(MOCKED_METHOD_DESCRIPTOR);
        when(ectx.getCallerContext()).thenReturn(callerContext);
        method = new java_lang_reflect_Field_get();
    }

    private void setupClassManager() throws UnknownAncestors {
        classManager = mock(ClassManager.class);
        when(classManager.isInstance(DUMMY_CHILD_CLASS_NAME_INTERNAL, DUMMY_CLASS_NAME_INTERNAL)).thenReturn(true);
        when(classManager.isInstance(NON_DUMMY_CLASS_NAME_INTERNAL, DUMMY_CLASS_NAME_INTERNAL)).thenReturn(false);
        when(classManager.isInnerClass(DUMMER_INNER_CLASS_NAME_BINARY, DUMMY_CLASS_NAME_BINARY)).thenCallRealMethod();
        when(vm.getClassManager()).thenReturn(classManager);

    }

    private void setupFieldAccessor() {
        staticFieldAccessor = mock(StaticFieldAccessor.class);
        when(vm.getStaticFieldAccessor()).thenReturn(staticFieldAccessor);
        when(staticFieldAccessor.getField(ectx, DUMMY_PUBLIC_STATIC_FIELD_DESCRIPTOR)).thenReturn(
                        new HeapItem(DUMMY_PUBLIC_STATIC_FIELD_VALUE, DUMMY_PUBLIC_STATIC_FIELD_TYPE));
        when(staticFieldAccessor.getField(ectx, DUMMY_PRIVATE_STATIC_FIELD_DESCRIPTOR)).thenReturn(
                        new HeapItem(DUMMY_PRIVATE_STATIC_FIELD_VALUE, DUMMY_PRIVATE_STATIC_FIELD_TYPE));
        when(staticFieldAccessor.getField(ectx, DUMMY_PUBLIC_INSTANCE_FIELD_DESCRIPTOR)).thenReturn(
                        new HeapItem(DUMMY_PUBLIC_INSTANCE_FIELD_VALUE, DUMMY_PUBLIC_INSTANCE_FIELD_TYPE));
        when(staticFieldAccessor.getField(ectx, DUMMY_PRIVATE_INSTANCE_FIELD_DESCRIPTOR)).thenReturn(
                        new HeapItem(DUMMY_PRIVATE_INSTANCE_FIELD_VALUE, DUMMY_PRIVATE_INSTANCE_FIELD_TYPE));
        when(staticFieldAccessor.getField(ectx, DUMMY_PROTECTED_STATIC_FIELD_DESCRIPTOR)).thenReturn(
                        new HeapItem(DUMMY_PROTECTED_STATIC_FIELD_VALUE, DUMMY_PROTECTED_STATIC_FIELD_TYPE));
    }

    @Test
    public void nonLocalExistentPublicInstanceFieldWorks() throws Exception {
        DummyClass instance = new DummyClass();
        Field field = DummyClass.class.getField("publicInstanceField");

        testCase(field, instance, DUMMY_PUBLIC_INSTANCE_FIELD_VALUE, DUMMY_PUBLIC_INSTANCE_FIELD_TYPE);
    }

    @Test
    public void nonLocalExistentPrivateInstanceFieldThrowsException() throws Exception {
        DummyClass instance = new DummyClass();
        Field field = DummyClass.class.getDeclaredField("privateInstanceField");

        String expectedMessage = "Class " + MOCKED_CLASS_JAVA + " can not access a member of class " + DUMMY_CLASS_NAME_BINARY + " with modifiers \"private final\"";
        testExceptionalCase(field, instance, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void nonLocalExistentPrivateInstanceFieldSetAccessibleWorks() throws Exception {
        DummyClass instance = new DummyClass();
        Field field = DummyClass.class.getDeclaredField("privateInstanceField");
        field.setAccessible(true);

        testCase(field, instance, DUMMY_PRIVATE_INSTANCE_FIELD_VALUE, DUMMY_PRIVATE_INSTANCE_FIELD_TYPE);
    }

    private class DummyClass {

        private class DummyInnerClass {

        }

        public static final int publicStaticField = DUMMY_PUBLIC_STATIC_FIELD_VALUE;
        private static final int privateStaticField = DUMMY_PRIVATE_STATIC_FIELD_VALUE;
        protected static final int protectedStaticField = DUMMY_PROTECTED_STATIC_FIELD_VALUE;

        public final int publicInstanceField;
        private final int privateInstanceField;
        final int protectedInstanceField;

        DummyClass() {
            publicInstanceField = DUMMY_PUBLIC_INSTANCE_FIELD_VALUE;
            protectedInstanceField = DUMMY_PROTECTED_INSTANCE_FIELD_VALUE;
            privateInstanceField = DUMMY_PRIVATE_INSTANCE_FIELD_VALUE;
        }
    }

    @Test
    public void localExistentPublicStaticFieldWorks() throws Exception {
        Field field = DummyClass.class.getField(DUMMY_PUBLIC_STATIC_FIELD_NAME);

        testLocalCasePerformsLookup(field, null, DUMMY_PUBLIC_STATIC_FIELD_VALUE, DUMMY_PUBLIC_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentPrivateStaticFieldFromExternalClassThrowsException() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(NON_DUMMY_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_STATIC_FIELD_NAME);

        String expectedMessage = "Class " + NON_DUMMY_CLASS_NAME_BINARY + " can not access a member of class " + DUMMY_CLASS_NAME_BINARY + " with modifiers \"private static final\"";
        testLocalExceptionalCase(field, null, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void localExistentPrivateStaticFieldSetAccessibleFromExternalClassWorks() throws Exception {
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_STATIC_FIELD_NAME);
        field.setAccessible(true);

        testLocalCasePerformsLookup(field, null, DUMMY_PRIVATE_STATIC_FIELD_VALUE, DUMMY_PRIVATE_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentPrivateStaticFieldFromInternalClassWorks() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(DUMMER_INNER_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_STATIC_FIELD_NAME);

        testLocalCasePerformsLookup(field, null, DUMMY_PRIVATE_STATIC_FIELD_VALUE, DUMMY_PRIVATE_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentProtectedStaticFieldFromChildClassWorks() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(DUMMY_CHILD_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PROTECTED_STATIC_FIELD_NAME);

        testLocalCasePerformsLookup(field, null, DUMMY_PROTECTED_STATIC_FIELD_VALUE, DUMMY_PROTECTED_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentPublicInstanceFieldGivesUnknownValue() throws Exception {
        Field field = DummyClass.class.getDeclaredField(DUMMY_PUBLIC_INSTANCE_FIELD_NAME);
        Object instance = new DummyClass();
        Object value = new UnknownValue();

        testLocalCase(field, instance, value, DUMMY_PUBLIC_INSTANCE_FIELD_TYPE);
    }

    @Test
    public void localExistentPrivateInstanceFieldFromExternalClassThrowsException() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(NON_DUMMY_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_INSTANCE_FIELD_NAME);

        String expectedMessage = "Class " + NON_DUMMY_CLASS_NAME_BINARY + " can not access a member of class " + DUMMY_CLASS_NAME_BINARY + " with modifiers \"private final\"";
        testLocalExceptionalCase(field, null, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void localExistentPrivateInstanceFieldSetAccessibleFromExternalClassGivesUnknownValue() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(NON_DUMMY_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_INSTANCE_FIELD_NAME);
        field.setAccessible(true);
        Object instance = new DummyClass();
        Object value = new UnknownValue();

        testLocalCase(field, instance, value, DUMMY_PRIVATE_INSTANCE_FIELD_TYPE);
    }

    @Test
    public void localExistentPrivateInstanceFieldFromInternalClassGivesUnknownValue() throws Exception {
        when(callerContext.getMethodDescriptor()).thenReturn(DUMMER_INNER_CLASS_NAME_INTERNAL);
        Field field = DummyClass.class.getDeclaredField(DUMMY_PRIVATE_INSTANCE_FIELD_NAME);
        Object instance = new DummyClass();
        Object value = new UnknownValue();

        testLocalCase(field, instance, value, DUMMY_PRIVATE_INSTANCE_FIELD_TYPE);
    }

    private void testLocalCase(Field field, Object instance, Object value, String type) throws Exception {
        when(configuration.isSafe(DUMMY_CLASS_NAME_INTERNAL)).thenReturn(false);

        testCase(field, instance, value, type);
    }

    private void testLocalCasePerformsLookup(Field field, Object instance, Object value, String type) throws Exception {
        testLocalCase(field, instance, value, type);

        String fieldDescriptor = Utils.buildFieldDescriptor(field);
        verify(staticFieldAccessor, times(1)).getField(eq(ectx), eq(fieldDescriptor));
    }

    private void testLocalExceptionalCase(Field field, Object instance, Class<? extends Exception> exceptionClass,
                    String expectedMessage) throws Exception {
        testExceptionalCase(field, instance, exceptionClass, expectedMessage);

        String fieldDescriptor = Utils.buildFieldDescriptor(field);
        verify(staticFieldAccessor, times(0)).getField(eq(ectx), eq(fieldDescriptor));
    }

    private void testCase(Field field, Object instance, Object value, String type) throws Exception {
        when(fieldItem.getValue()).thenReturn(field);
        when(instanceItem.getValue()).thenReturn(instance);

        method.execute(vm, ectx);

        verify(mState, times(1)).peekParameter(eq(FIELD_REGISTER));
        verify(mState, times(1)).peekParameter(eq(INSTANCE_REGISTER));
        ArgumentCaptor<HeapItem> returnItem = ArgumentCaptor.forClass(HeapItem.class);
        verify(mState, times(1)).assignReturnRegister(returnItem.capture());
        assertEquals(type, returnItem.getValue().getType());
        if (value instanceof UnknownValue) {
            assertEquals(UnknownValue.class, returnItem.getValue().getValue().getClass());
        } else {
            assertEquals(value, returnItem.getValue().getValue());
        }
    }

    private void testExceptionalCase(Object field, Object instance, Class<? extends Exception> exceptionClass,
                    String message) throws Exception {
        when(fieldItem.getValue()).thenReturn(field);
        when(instanceItem.getValue()).thenReturn(instance);

        method.execute(vm, ectx);

        Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
        expectedExceptions.add(new VirtualException(exceptionClass, message));
        assertEquals(expectedExceptions, method.getExceptions());

        verify(mState, times(1)).peekParameter(eq(FIELD_REGISTER));
        verify(mState, times(1)).peekParameter(eq(INSTANCE_REGISTER));
        verify(mState, times(0)).assignReturnRegister(any(HeapItem.class));
        verify(mState, times(0)).assignReturnRegister(any(Object.class), any(String.class));
    }

}
