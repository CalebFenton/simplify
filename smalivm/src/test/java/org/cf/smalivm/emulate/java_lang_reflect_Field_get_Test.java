package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.StaticFieldAccessor;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalField;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.writer.builder.BuilderField;
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

    private static final int FIELD_REGISTER = 0;
    private static final int INSTANCE_REGISTER = 1;

    private static final String LOCAL_CLASS = "Llocal/Klazz;";
    private static final String LOCAL_CLASS_JAVA = "local.Klazz";
    private static final String NO_ACCESS_LOCAL_CLASS = "Ljava/lang/Whatever;";
    private static final String NO_ACCESS_LOCAL_CLASS_JAVA = "java.lang.Whatever";
    private static final String LOCAL_PUBLIC_STATIC_FIELD_NAME = "publicStaticField";
    private static final String LOCAL_PUBLIC_STATIC_FIELD_TYPE = "I";
    private static final int LOCAL_PUBLIC_STATIC_FIELD_VALUE = 1;
    private static final String LOCAL_PUBLIC_STATIC_FIELD = LOCAL_CLASS + "->" + LOCAL_PUBLIC_STATIC_FIELD_NAME + ":" + LOCAL_PUBLIC_STATIC_FIELD_TYPE;

    private static final String LOCAL_PRIVATE_STATIC_FIELD_NAME = "privateStaticField";
    private static final String LOCAL_PRIVATE_STATIC_FIELD_TYPE = "I";
    private static final int LOCAL_PRIVATE_STATIC_FIELD_VALUE = 2;
    private static final String LOCAL_PRIVATE_STATIC_FIELD = LOCAL_CLASS + "->" + LOCAL_PRIVATE_STATIC_FIELD_NAME + ":" + LOCAL_PRIVATE_STATIC_FIELD_TYPE;

    private static final String LOCAL_PUBLIC_INSTANCE_FIELD_NAME = "publicInstanceField";
    private static final String LOCAL_PUBLIC_INSTANCE_FIELD_TYPE = "I";
    private static final int LOCAL_PUBLIC_INSTANCE_FIELD_VALUE = 3;
    private static final String LOCAL_PUBLIC_INSTANCE_FIELD = LOCAL_CLASS + "->" + LOCAL_PUBLIC_INSTANCE_FIELD_NAME + ":" + LOCAL_PUBLIC_INSTANCE_FIELD_TYPE;

    private static final String LOCAL_PRIVATE_INSTANCE_FIELD_NAME = "privateInstanceField";
    private static final String LOCAL_PRIVATE_INSTANCE_FIELD_TYPE = "I";
    private static final int LOCAL_PRIVATE_INSTANCE_FIELD_VALUE = 4;
    private static final String LOCAL_PRIVATE_INSTANCE_FIELD = LOCAL_CLASS + "->" + LOCAL_PRIVATE_INSTANCE_FIELD_NAME + ":" + LOCAL_PRIVATE_INSTANCE_FIELD_TYPE;

    private static final String MOCKED_CLASS = "Lmy/mocked/Class;";
    private static final String MOCKED_CLASS_JAVA = "my.mocked.Class";
    private static final String MOCKED_METHOD = "mockedMethod()V";
    private static final String MOCKED_METHOD_DESCRIPTOR = MOCKED_CLASS + "->" + MOCKED_METHOD;

    @Before
    public void setUp() {
        vm = mock(VirtualMachine.class);
        ectx = mock(ExecutionContext.class);
        setupFields(vm, ectx);

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

    private void setupFields(VirtualMachine vm, ExecutionContext ectx) {
        classManager = mock(ClassManager.class);
        when(vm.getClassManager()).thenReturn(classManager);
        when(classManager.isLocalClass(LOCAL_CLASS)).thenReturn(true);

        StaticFieldAccessor staticFieldAccessor = mock(StaticFieldAccessor.class);
        when(vm.getStaticFieldAccessor()).thenReturn(staticFieldAccessor);

        BuilderField publicStaticField = mock(BuilderField.class, withSettings().extraInterfaces(FieldReference.class));
        when(staticFieldAccessor.getField(ectx, LOCAL_PUBLIC_STATIC_FIELD)).thenReturn(
                        new HeapItem(LOCAL_PUBLIC_STATIC_FIELD_VALUE, "I"));
        when(publicStaticField.getDefiningClass()).thenReturn(LOCAL_CLASS);
        when(publicStaticField.getName()).thenReturn(LOCAL_PUBLIC_STATIC_FIELD_NAME);
        when(publicStaticField.getType()).thenReturn(LOCAL_PUBLIC_STATIC_FIELD_TYPE);
        when(publicStaticField.getAccessFlags()).thenReturn(
                        AccessFlags.PUBLIC.getValue() | AccessFlags.STATIC.getValue());

        BuilderField privateStaticField = mock(BuilderField.class, withSettings().extraInterfaces(FieldReference.class));
        when(staticFieldAccessor.getField(ectx, LOCAL_PRIVATE_STATIC_FIELD)).thenReturn(
                        new HeapItem(LOCAL_PRIVATE_STATIC_FIELD_VALUE, "I"));
        when(privateStaticField.getDefiningClass()).thenReturn(LOCAL_CLASS);
        when(privateStaticField.getName()).thenReturn(LOCAL_PRIVATE_STATIC_FIELD_NAME);
        when(privateStaticField.getType()).thenReturn(LOCAL_PRIVATE_STATIC_FIELD_TYPE);
        when(privateStaticField.getAccessFlags()).thenReturn(
                        AccessFlags.PRIVATE.getValue() | AccessFlags.STATIC.getValue());

        BuilderField publicInstanceField = mock(BuilderField.class, withSettings()
                        .extraInterfaces(FieldReference.class));
        when(publicInstanceField.getDefiningClass()).thenReturn(LOCAL_CLASS);
        when(publicInstanceField.getName()).thenReturn(LOCAL_PUBLIC_INSTANCE_FIELD_NAME);
        when(publicInstanceField.getType()).thenReturn(LOCAL_PUBLIC_INSTANCE_FIELD_TYPE);
        when(publicInstanceField.getAccessFlags()).thenReturn(AccessFlags.PUBLIC.getValue());

        BuilderField privateInstanceField = mock(BuilderField.class,
                        withSettings().extraInterfaces(FieldReference.class));
        when(privateInstanceField.getDefiningClass()).thenReturn(LOCAL_CLASS);
        when(privateInstanceField.getName()).thenReturn(LOCAL_PRIVATE_INSTANCE_FIELD_NAME);
        when(privateInstanceField.getType()).thenReturn(LOCAL_PRIVATE_INSTANCE_FIELD_TYPE);
        when(privateInstanceField.getAccessFlags()).thenReturn(AccessFlags.PRIVATE.getValue());

        List<BuilderField> fields = new LinkedList<BuilderField>();
        fields.add(publicStaticField);
        fields.add(privateStaticField);
        fields.add(publicInstanceField);
        fields.add(privateInstanceField);
        when(classManager.getFields(LOCAL_CLASS)).thenReturn(fields);
    }

    @Test
    public void nonLocalExistentStaticPublicFieldWorks() throws Exception {
        Integer value = Integer.MAX_VALUE;
        Object instance = null;
        Field field = Integer.class.getField("MAX_VALUE");

        testCase(field, instance, value, "I");
    }

    @Test
    public void nonLocalExistentStaticPackagePrivateFieldThrowsException() throws Exception {
        Object instance = null;
        Field field = Integer.class.getDeclaredField("digits");

        String expectedMessage = "Class " + MOCKED_CLASS_JAVA + " can not access a member of class java.lang.Integer with modifiers \"static final\"";
        testExceptionalCase(field, instance, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void nonLocalExistentInstancePublicFieldWorks() throws Exception {
        int value = 5;
        NonLocalClass instance = new NonLocalClass(value);
        Field field = NonLocalClass.class.getField("PublicField");

        testCase(field, instance, value, "I");
    }

    @Test
    public void nonLocalExistentInstancePrivateFieldThrowsException() throws Exception {
        NonLocalClass instance = new NonLocalClass(0);
        Field field = NonLocalClass.class.getDeclaredField("PrivateField");

        String expectedMessage = "Class " + MOCKED_CLASS_JAVA + " can not access a member of class org.cf.smalivm.emulate.NonLocalClass with modifiers \"private\"";
        testExceptionalCase(field, instance, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void localExistentStaticPublicFieldWorks() throws Exception {
        Object instance = null;
        LocalField field = new LocalField(LOCAL_PUBLIC_STATIC_FIELD);

        testLocalCase(field, instance, LOCAL_PUBLIC_STATIC_FIELD_VALUE, LOCAL_PUBLIC_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentStaticPrivateFieldFromExternalClassThrowsException() throws Exception {
        Object instance = null;
        LocalField field = new LocalField(LOCAL_PRIVATE_STATIC_FIELD);
        when(callerContext.getMethodDescriptor()).thenReturn(NO_ACCESS_LOCAL_CLASS);

        String expectedMessage = "Class " + NO_ACCESS_LOCAL_CLASS_JAVA + " can not access a member of class " + LOCAL_CLASS_JAVA + " with modifiers \"private static\"";
        testLocalExceptionalCase(field, instance, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void localExistentStaticPrivateFieldSetAccessibleFromExternalClassWorks() throws Exception {
        Object instance = null;
        LocalField field = new LocalField(LOCAL_PRIVATE_STATIC_FIELD);
        field.setAccessible(true);
        when(callerContext.getMethodDescriptor()).thenReturn(NO_ACCESS_LOCAL_CLASS);

        testLocalCase(field, instance, LOCAL_PRIVATE_STATIC_FIELD_VALUE, LOCAL_PRIVATE_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentStaticPrivateFieldFromInternalClassWorks() throws Exception {
        Object instance = null;
        LocalField field = new LocalField(LOCAL_PRIVATE_STATIC_FIELD);
        when(callerContext.getMethodDescriptor()).thenReturn(LOCAL_CLASS);

        testLocalCase(field, instance, LOCAL_PRIVATE_STATIC_FIELD_VALUE, LOCAL_PRIVATE_STATIC_FIELD_TYPE);
    }

    @Test
    public void localExistentInstancePublicFieldGivesUnknownValue() throws Exception {
        Object value = new UnknownValue();
        Object instance = new LocalClass(LOCAL_CLASS);
        LocalField field = new LocalField(LOCAL_PUBLIC_INSTANCE_FIELD);

        testLocalCase(field, instance, value, LOCAL_PUBLIC_INSTANCE_FIELD_TYPE);
    }

    @Test
    public void localExistentInstancePrivateFieldFromExternalClassThrowsException() throws Exception {
        Object instance = new LocalClass(LOCAL_CLASS);
        LocalField field = new LocalField(LOCAL_PRIVATE_INSTANCE_FIELD);
        when(callerContext.getMethodDescriptor()).thenReturn(NO_ACCESS_LOCAL_CLASS + "->someMethod()V");

        String expectedMessage = "Class " + NO_ACCESS_LOCAL_CLASS_JAVA + " can not access a member of class " + LOCAL_CLASS_JAVA + " with modifiers \"private\"";
        testLocalExceptionalCase(field, instance, IllegalAccessException.class, expectedMessage);
    }

    @Test
    public void localExistentInstancePrivateFieldSetAccessibleFromExternalClassGivesUnknownValue() throws Exception {
        Object value = new UnknownValue();
        Object instance = new LocalClass(LOCAL_CLASS);
        LocalField field = new LocalField(LOCAL_PRIVATE_INSTANCE_FIELD);
        field.setAccessible(true);

        testLocalCase(field, instance, value, LOCAL_PRIVATE_INSTANCE_FIELD_TYPE);
    }

    @Test
    public void localExistentInstancePrivateFieldFromInternalClassGivesUnknownValue() throws Exception {
        Object value = new UnknownValue();
        Object instance = new LocalClass(LOCAL_CLASS);
        LocalField field = new LocalField(LOCAL_PRIVATE_INSTANCE_FIELD);
        when(callerContext.getMethodDescriptor()).thenReturn(LOCAL_CLASS + "->someMethodV");

        testLocalCase(field, instance, value, LOCAL_PRIVATE_INSTANCE_FIELD_TYPE);
    }

    private void testLocalCase(LocalField field, Object instance, Object value, String type) throws Exception {
        testCase(field, instance, value, type);

        String className = field.getName().split("->")[0];
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(eq(className));
    }

    private void testLocalExceptionalCase(LocalField field, Object instance, Class<? extends Exception> exceptionClass,
                    String expectedMessage) throws Exception {
        testExceptionalCase(field, instance, exceptionClass, expectedMessage);

        String className = field.getName().split("->")[0];
        verify(ectx, times(1)).staticallyInitializeClassIfNecessary(eq(className));
    }

    private void testCase(Object field, Object instance, Object value, String type) throws Exception {
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
