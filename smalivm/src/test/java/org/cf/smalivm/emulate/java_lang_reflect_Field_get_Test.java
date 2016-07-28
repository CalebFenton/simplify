package org.cf.smalivm.emulate;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualGeneric;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Enclosed.class)
public class java_lang_reflect_Field_get_Test {

    private static final String DUMMY_CLASS_NAME = "Lorg/cf/smalivm/DummyClass;";
    private static final String DUMMY_CLASS_NAME_BINARY = "org.cf.smalivm.DummyClass";

    private static final Object PUBLIC_STATIC_VALUE = 0x10;
    private static final Object PROTECTED_STATIC_VALUE = 0x11;
    private static final Object PACKAGE_PRIVATE_STATIC_VALUE = 0x12;
    private static final Object PRIVATE_STATIC_VALUE = 0x13;

    private static VirtualMachine vm = null;

    private static void spawnVM() {
        if (vm == null) {
            // Confession: no idea why but if we don't tell classmanager to perform an expensive recache of classes,
            // it doesn't think the dummy class exists.
            vm = VMTester.spawnVM(true);
        }
    }

    private static void testPositiveCase(String fieldName, String callingMethodSignature, Object fieldValue,
                                         String fieldType, boolean setAccessible) throws Exception {
        spawnVM();
        ExecutionContext context = buildContext(vm);
        test(vm, context, fieldName, callingMethodSignature, setAccessible);
        HeapItem returnItem = context.getMethodState().peekReturnRegister();

        assertNotNull("Emulated method should set return register", returnItem);
        assertEquals(fieldType, returnItem.getType());
        if (fieldValue instanceof UnknownValue) {
            assertEquals(UnknownValue.class, returnItem.getValue().getClass());
        } else {
            assertEquals(fieldValue, returnItem.getValue());
        }
    }

    private static void testPositiveCase(String fieldName, String callingMethodSignature, Object fieldValue,
                                         String fieldType) throws Exception {
        testPositiveCase(fieldName, callingMethodSignature, fieldValue, fieldType, false);
    }

    private static void testNegativeCase(String fieldName, String callingMethodSignature) throws Exception {
        spawnVM();
        ExecutionContext context = buildContext(vm);
        ExecutionContextMethod fieldGet = test(vm, context, fieldName, callingMethodSignature, false);

        Set<Throwable> exceptions = fieldGet.getExceptions();
        assertEquals("Should have one exception", 1, exceptions.size());

        VirtualGeneric callingClass = context.getCallerContext().getMethod().getDefiningClass();
        VirtualField field = context.getMethod().getDefiningClass().getField(fieldName);
        String error = callingClass.getBinaryName() + " can't access a member of " + DUMMY_CLASS_NAME_BINARY +
                       " with modifiers \"" + Modifier.toString(field.getField().getAccessFlags()) + "\"";

        Throwable actualException = exceptions.iterator().next();
        assertEquals(IllegalAccessException.class, actualException.getClass());
        assertEquals(error, actualException.getMessage());
    }

    private static ExecutionContext buildContext(VirtualMachine vm) {
        VirtualClass dummyClass = vm.getClassManager().getVirtualClass(DUMMY_CLASS_NAME);
        VirtualMethod inertStatic = dummyClass.getMethod("dummyMethod()V");

        return vm.spawnRootContext(inertStatic);
    }

    private static ExecutionContextMethod test(VirtualMachine vm, ExecutionContext context, String fieldName,
                                               String callingMethodSignature, boolean setAccessible) throws Exception {
        // Setup caller context
        ExecutionContext callerContext = vm.spawnRootContext(callingMethodSignature);
        Field callerContextField = ExecutionContext.class.getDeclaredField("callerContext");
        callerContextField.setAccessible(true);
        callerContextField.set(context, callerContext);

        // Setup actual field to use as parameter
        ClassLoader classLoader = vm.getClassLoader();
        Class<?> klazz = classLoader.loadClass(DUMMY_CLASS_NAME_BINARY);
        Constructor<?> ctor = klazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(setAccessible);

        // Setup method state and arguments
        HeapItem fieldItem = new HeapItem(field, "Ljava/lang/reflect/Field;");
        HeapItem instanceItem = new HeapItem(instance, DUMMY_CLASS_NAME);
        MethodState methodState = context.getMethodState();
        methodState.pokeRegister(0, fieldItem);
        methodState.pokeRegister(1, instanceItem);

        ExecutionContextMethod fieldGet = new java_lang_reflect_Field_get();
        fieldGet.execute(vm, null, context);

        return fieldGet;
    }

    public static class FromWithinClass {

        private static final String METHOD_SIGNATURE = "Lorg/cf/smalivm/DummyClass;->dummyMethod()V";

        @Test
        public void canGetPublicInstanceField() throws Exception {
            testPositiveCase("publicInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetProtectedInstanceField() throws Exception {
            testPositiveCase("protectedInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPackagePrivateInstanceField() throws Exception {
            testPositiveCase("packagePrivateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPrivateInstanceField() throws Exception {
            testPositiveCase("privateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPublicStaticField() throws Exception {
            testPositiveCase("publicStaticField", METHOD_SIGNATURE, PUBLIC_STATIC_VALUE, "I");
        }

        @Test
        public void canGetProtectedStaticField() throws Exception {
            testPositiveCase("protectedStaticField", METHOD_SIGNATURE, PROTECTED_STATIC_VALUE, "I");
        }

        @Test
        public void canGetPackagePrivateStaticField() throws Exception {
            testPositiveCase("packagePrivateStaticField", METHOD_SIGNATURE, PACKAGE_PRIVATE_STATIC_VALUE, "I");
        }


        @Test
        public void canGetPrivateStaticField() throws Exception {
            testPositiveCase("privateStaticField", METHOD_SIGNATURE, PRIVATE_STATIC_VALUE, "I");
        }
    }

    public static class FromWithinInnerClass {

        private static final String METHOD_SIGNATURE = "Lorg/cf/smalivm/DummyClass$DummyInnerClass;->dummyMethod()V";

        @Test
        public void canGetPublicInstanceField() throws Exception {
            testPositiveCase("publicInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetProtectedInstanceField() throws Exception {
            testPositiveCase("protectedInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPackagePrivateInstanceField() throws Exception {
            testPositiveCase("packagePrivateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPrivateInstanceField() throws Exception {
            testPositiveCase("privateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetPublicStaticField() throws Exception {
            testPositiveCase("publicStaticField", METHOD_SIGNATURE, PUBLIC_STATIC_VALUE, "I");
        }

        @Test
        public void canGetProtectedStaticField() throws Exception {
            testPositiveCase("protectedStaticField", METHOD_SIGNATURE, PROTECTED_STATIC_VALUE, "I");
        }

        @Test
        public void canGetPackagePrivateStaticField() throws Exception {
            testPositiveCase("packagePrivateStaticField", METHOD_SIGNATURE, PACKAGE_PRIVATE_STATIC_VALUE, "I");
        }

        @Test
        public void canGetPrivateStaticField() throws Exception {
            testPositiveCase("privateStaticField", METHOD_SIGNATURE, PRIVATE_STATIC_VALUE, "I");
        }
    }

    public static class FromWithinChildClassInUnrelatedPackage {

        private static final String METHOD_SIGNATURE = "Lorg/cf/smalivm/unrelated/DummyChildClass;->dummyMethod()V";

        @Test
        public void canGetPublicInstanceField() throws Exception {
            testPositiveCase("publicInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canGetProtectedInstanceField() throws Exception {
            testPositiveCase("protectedInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canNotGetPackagePrivateInstanceField() throws Exception {
            testNegativeCase("packagePrivateInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPackagePrivateInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("packagePrivateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canNotGetPrivateInstanceField() throws Exception {
            testNegativeCase("privateInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPrivateInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canGetPublicStaticField() throws Exception {
            testPositiveCase("publicStaticField", METHOD_SIGNATURE, PUBLIC_STATIC_VALUE, "I");
        }

        @Test
        public void canGetProtectedStaticField() throws Exception {
            testPositiveCase("protectedStaticField", METHOD_SIGNATURE, PROTECTED_STATIC_VALUE, "I");
        }

        @Test
        public void canNotGetPackagePrivateStaticField() throws Exception {
            testNegativeCase("packagePrivateStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetGetPackagePrivateStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("packagePrivateStaticField", METHOD_SIGNATURE, PACKAGE_PRIVATE_STATIC_VALUE, "I", true);
        }

        @Test
        public void canNotGetPrivateStaticField() throws Exception {
            testNegativeCase("privateStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetGetPrivateStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateStaticField", METHOD_SIGNATURE, PRIVATE_STATIC_VALUE, "I", true);
        }
    }

    public static class FromSamePackageClass {

        private static final String METHOD_SIGNATURE = "Lorg/cf/smalivm/SamePackageClass;->dummyMethod()V";

        @Test
        public void canGetPublicInstanceField() throws Exception {
            testPositiveCase("publicInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canNotGetProtectedInstanceField() throws Exception {
            testNegativeCase("protectedInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetProtectedInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("protectedInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canGetPackagePrivateInstanceField() throws Exception {
            testPositiveCase("packagePrivateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canNotGetPrivateInstanceField() throws Exception {
            testNegativeCase("privateInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPrivateInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canGetPublicStaticField() throws Exception {
            testPositiveCase("publicStaticField", METHOD_SIGNATURE, PUBLIC_STATIC_VALUE, "I");
        }

        @Test
        public void canNotGetProtectedStaticField() throws Exception {
            testNegativeCase("protectedStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetProtectedStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("protectedStaticField", METHOD_SIGNATURE, PROTECTED_STATIC_VALUE, "I", true);
        }

        @Test
        public void canGetPackagePrivateStaticField() throws Exception {
            testPositiveCase("packagePrivateStaticField", METHOD_SIGNATURE, PACKAGE_PRIVATE_STATIC_VALUE, "I");
        }

        @Test
        public void canNotGetPrivateStaticField() throws Exception {
            testNegativeCase("privateStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPrivateStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateStaticField", METHOD_SIGNATURE, PRIVATE_STATIC_VALUE, "I", true);
        }
    }

    public static class FromWithinUnrelatedClass {

        private static final String METHOD_SIGNATURE = "Lorg/cf/smalivm/unrelated/UnrelatedClass;->dummyMethod()V";

        @Test
        public void canGetPublicInstanceField() throws Exception {
            testPositiveCase("publicInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I");
        }

        @Test
        public void canNotGetProtectedInstanceField() throws Exception {
            testNegativeCase("protectedInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetProtectedInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("protectedInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canNotGetPackagePrivateInstanceField() throws Exception {
            testNegativeCase("packagePrivateInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPackagePrivateInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("packagePrivateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canNotGetPrivateInstanceField() throws Exception {
            testNegativeCase("privateInstanceField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPrivateInstanceFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateInstanceField", METHOD_SIGNATURE, new UnknownValue(), "I", true);
        }

        @Test
        public void canGetPublicStaticField() throws Exception {
            testPositiveCase("publicStaticField", METHOD_SIGNATURE, PUBLIC_STATIC_VALUE, "I");
        }

        @Test
        public void canNotGetProtectedStaticField() throws Exception {
            testNegativeCase("protectedStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetProtectedStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("protectedStaticField", METHOD_SIGNATURE, PROTECTED_STATIC_VALUE, "I", true);
        }

        @Test
        public void canNotGetPackagePrivateStaticField() throws Exception {
            testNegativeCase("packagePrivateStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPackagePrivateStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("packagePrivateStaticField", METHOD_SIGNATURE, PACKAGE_PRIVATE_STATIC_VALUE, "I", true);
        }

        @Test
        public void canNotGetPrivateStaticField() throws Exception {
            testNegativeCase("privateStaticField", METHOD_SIGNATURE);
        }

        @Test
        public void canGetPrivateStaticFieldIfSetAccessible() throws Exception {
            testPositiveCase("privateStaticField", METHOD_SIGNATURE, PRIVATE_STATIC_VALUE, "I", true);
        }
    }
}
