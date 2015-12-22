package org.cf.smalivm.emulate;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class java_lang_Class_getMethod_Test {

    private static final String METHOD_TYPE = "Ljava/lang/reflect/Method;";

    private static MethodState getMethodState(VirtualMachine vm, Object klazz, String methodName, Object parameterTypes) {
        ExecutionContext ectx = new ExecutionContext(vm, "Ljava/lang/Class;->getMethod()Ljava/lang/reflect/Method;");
        int registerCount = 3;
        MethodState mState = new MethodState(ectx, registerCount);
        mState.assignRegister(0, klazz, "Ljava/lang/Class;");
        mState.assignRegister(1, methodName, "Ljava/lang/String;");
        mState.assignRegister(2, parameterTypes, "[Ljava/lang/Class;");

        return mState;
    }

    public static class TestLocalClass {

        private static final String CLASS_NAME = "Lchild_class;";
        private static final String CLASS_NAME_JAVA = "child_class";
        private static final LocalClass CLASS = new LocalClass(CLASS_NAME);

        private VirtualMachine vm;
        private MethodStateMethod emulatedMethod;

        @Before
        public void setUp() {
            vm = VMTester.getTestVM();
            emulatedMethod = new java_lang_Class_getMethod();
        }

        @Test
        public void testGetExistentMethodWithNoParameterTypesReturnsExpectedLocalMethod() throws Exception {
            MethodState mState = getMethodState(vm, CLASS, "someString", null);
            emulatedMethod.execute(vm, mState);

            LocalMethod expectedValue = new LocalMethod(CLASS_NAME + "->someString()Ljava/lang/String;");
            HeapItem expected = new HeapItem(expectedValue, METHOD_TYPE);
            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(expected, actual);
        }

        @Test
        public void testGetExistentMethodWithNoParameterTypesUsingZeroAsNullReturnsExpectedLocalMethod()
                        throws Exception {
            MethodState mState = getMethodState(vm, CLASS, "someString", 0);
            emulatedMethod.execute(vm, mState);

            LocalMethod expectedValue = new LocalMethod(CLASS_NAME + "->someString()Ljava/lang/String;");
            HeapItem expected = new HeapItem(expectedValue, METHOD_TYPE);
            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(expected, actual);
        }

        @Test
        public void testGetExistentMethodWithOneParameterTypesReturnsExpectedLocalMethod() throws Exception {
            MethodState mState = getMethodState(vm, CLASS, "takesParameter", new Class<?>[] { int.class });
            emulatedMethod.execute(vm, mState);

            LocalMethod expectedValue = new LocalMethod(CLASS_NAME + "->takesParameter(I)V");
            HeapItem expected = new HeapItem(expectedValue, METHOD_TYPE);
            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(expected, actual);
        }

        @Test
        public void testGetNonExistentMethodReturnsUnknownValue() throws Exception {
            String methodName = "yoDawgThisMethodWillNeverExist";
            String fullMethodName = CLASS_NAME_JAVA + "." + methodName + "()";
            MethodState mState = getMethodState(vm, CLASS, methodName, null);
            emulatedMethod.execute(vm, mState);

            Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
            expectedExceptions.add(new VirtualException(NoSuchMethodException.class, fullMethodName));
            assertEquals(expectedExceptions, emulatedMethod.getExceptions());

            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(null, actual);
        }
    }

    public static class TestNonLocalClass {

        private static final Class<?> CLASS = System.class;

        private VirtualMachine vm;
        private MethodStateMethod emulatedMethod;

        @Before
        public void setUp() {
            vm = VMTester.getTestVM();
            emulatedMethod = new java_lang_Class_getMethod();
        }

        @Test
        public void testGetExistentMethodWithNoParameterTypesReturnsExpectedMethod() throws Exception {
            MethodState mState = getMethodState(vm, CLASS, "gc", null);
            emulatedMethod.execute(vm, mState);

            HeapItem expected = new HeapItem(System.class.getMethod("gc"), METHOD_TYPE);
            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(expected, actual);
        }

        @Test
        public void testGetExistentMethodWithOneParameterTypesReturnsExpectedLocalMethod() throws Exception {
            MethodState mState = getMethodState(vm, CLASS, "getProperty", new Class<?>[] { String.class });
            emulatedMethod.execute(vm, mState);

            Method expectedValue = System.class.getMethod("getProperty", new Class<?>[] { String.class });
            HeapItem expected = new HeapItem(expectedValue, METHOD_TYPE);
            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(expected, actual);
        }

        @Test
        public void testGetNonExistentMethodThrowsExceptionAndHasNoReturnValue() throws Exception {
            String methodName = "yoDawgThisMethodWillNeverExist";
            String fullMethodName = CLASS.getName() + "." + methodName + "()";
            MethodState mState = getMethodState(vm, CLASS, methodName, null);
            emulatedMethod.execute(vm, mState);

            Set<VirtualException> expectedExceptions = new HashSet<VirtualException>();
            expectedExceptions.add(new VirtualException(NoSuchMethodException.class, fullMethodName));
            assertEquals(expectedExceptions, emulatedMethod.getExceptions());

            HeapItem actual = mState.readRegister(MethodState.ReturnRegister);
            assertEquals(null, actual);
        }
    }

}
