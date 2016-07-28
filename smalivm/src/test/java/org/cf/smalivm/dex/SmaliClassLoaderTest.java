package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SmaliClassLoaderTest {

    private static final String[] OBJECT_METHODS =
            new String[] { "public boolean java.lang.Object.equals(java.lang.Object)",
                           "public final native java.lang.Class java.lang.Object.getClass()",
                           "public final native void java.lang.Object.notify()",
                           "public final native void java.lang.Object.notifyAll()",
                           "public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException",
                           "public final void java.lang.Object.wait() throws java.lang.InterruptedException",
                           "public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException",
                           "public java.lang.String java.lang.Object.toString()",
                           "public native int java.lang.Object.hashCode()" };
    private static final String TEST_SMALI_PATH = "resources/test/smali/class_builder";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private SmaliClassLoader classLoader;
    private ClassManager classManager;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void canLoadEnumClassAndGetEnumValue() throws ClassNotFoundException {
        Class<?> klazz = classLoader.loadClass("android.annotation.SdkConstant$SdkConstantType");

        Object enumConstant = Enum.valueOf((Class<? extends Enum>) klazz, "ACTIVITY_INTENT_ACTION");
        assertEquals(klazz, enumConstant.getClass());

        String[] enumStrings = Arrays.asList(klazz.getEnumConstants()).stream().map(c -> c.toString())
                                       .toArray(size -> new String[size]);
        String[] expectedEnumStrings =
                new String[] { "ACTIVITY_INTENT_ACTION", "BROADCAST_INTENT_ACTION", "FEATURE", "INTENT_CATEGORY",
                               "SERVICE_ACTION", "$shadow_instance" };
        assertArrayEquals(expectedEnumStrings, enumStrings);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void canLoadEnumClassWithNonEnumField() throws ClassNotFoundException {
        Class<?> klazz = classLoader.loadClass("android.net.wifi.SupplicantState");

        Object enumConstant = Enum.valueOf((Class<? extends Enum>) klazz, "ASSOCIATED");
        assertEquals(klazz, enumConstant.getClass());
    }

    @Test
    public void loadsClassThatReferencesNonExistentClassAndThrowsExceptionWhenVerifyingIt() throws ClassNotFoundException {
        String className = "org.cf.test.NonExistentReference";
        Class<?> klazz = classLoader.loadClass(className);

        exception.expect(NoClassDefFoundError.class);
        exception.expectMessage("does/not/exist");
        klazz.getMethods();
    }

    @Test
    public void loadsClassWithCircularReferences() throws Exception {
        String className1 = "org.cf.test.CircularReference1";
        Class<?> klazz1 = classLoader.loadClass(className1);

        String className2 = "org.cf.test.CircularReference2";
        Class<?> klazz2 = classLoader.loadClass(className2);

        assertHasObjectMethods(klazz1);
        assertHasObjectMethods(klazz2);

        List<Method> methods1 = getFilteredMethods(klazz1);
        assertEquals(1, methods1.size());

        List<Method> methods2 = getFilteredMethods(klazz2);
        assertEquals(1, methods2.size());

        assertEquals(methods1.get(0).getReturnType(), klazz2);
        assertEquals(methods2.get(0).getReturnType(), klazz1);
    }

    @Test
    public void loadsClassWithOverloadedFields() throws Exception {
        String className = "org.cf.test.OverloadedFields";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);

        List<Field> fields = getFilteredFields(klazz);
        assertEquals(2, fields.size());

        assertEquals("public static int org.cf.test.OverloadedFields.field1", fields.get(0).toString());
        assertEquals("public static java.lang.Object org.cf.test.OverloadedFields.field1", fields.get(1).toString());
    }

    @Test
    public void loadsClassWithIllegalFieldModifiers() throws Exception {
        String className = "org.cf.test.IllegalFieldModifiers";
        Class<?> klazz = classLoader.loadClass(className);
    }

    @Test
    public void loadsComplexClass() throws Exception {
        String className = "org.cf.test.ComplexClass";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);

        String superName = "org.cf.test.SuperClass";
        Class<?> superClass = classLoader.loadClass(superName);
        assertEquals(superName, superClass.getName());
        assertEquals(superClass, klazz.getSuperclass());

        List<Method> methods = getFilteredMethods(klazz);
        assertEquals(6, methods.size());

        List<Field> fields = getFilteredFields(klazz);
        assertEquals(1, fields.size());
    }

    @Test
    public void loadsJVMClasses() throws ClassNotFoundException {
        Class<?> expected = String.class;
        Class<?> actual = classLoader.loadClass(expected.getName());

        assertEquals(expected, actual);
    }

    @Test
    public void loadsSelfReferencingClass() throws Exception {
        String className = "org.cf.test.SelfReference";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);

        List<Field> fields = getFilteredFields(klazz);
        assertEquals(1, fields.size());
        Class<?> arrayClass = Array.newInstance(klazz, 1).getClass();
        assertEquals(arrayClass, fields.get(0).getType());

        List<Method> methods = getFilteredMethods(klazz);
        assertEquals(1, methods.size());

        Class<?>[] parameterTypes = methods.get(0).getParameterTypes();
        assertEquals(1, parameterTypes.length);
        assertEquals(klazz, parameterTypes[0]);
    }

    @Test
    public void loadsSimpleClass() throws ClassNotFoundException {
        String className = "org.cf.test.SimpleClass";
        Class<?> klazz = classLoader.loadClass(className);
        assertHasObjectMethods(klazz);

        List<Method> methods = getFilteredMethods(klazz);
        assertEquals(1, methods.size());
        assertEquals("public static void " + className + ".simpleLoop(int,java.lang.Object)",
                methods.get(0).toString());
    }

    @Before
    public void setUp() throws IOException {
        ClassManagerFactory managerFactory = new ClassManagerFactory();
        classManager = managerFactory.build(TEST_SMALI_PATH);
        classLoader = new SmaliClassLoader(classManager);
    }

    @Test
    public void throwsExceptionWhenLoadingNonExistentAndReferencedClass() throws ClassNotFoundException {
        String nonExistentClassName = "does.not.exist";
        exception.expect(ClassNotFoundException.class);
        exception.expectMessage(nonExistentClassName);
        classLoader.loadClass(nonExistentClassName);
    }


    @Test
    public void throwsExceptionWhenLoadingNonExistentAndUnreferencedClass() throws ClassNotFoundException {
        String nonExistentClassName = "asdfasdf";
        exception.expect(ClassNotFoundException.class);
        exception.expectMessage(nonExistentClassName);
        classLoader.loadClass(nonExistentClassName);
    }

    private void assertHasObjectMethods(Class<?> klazz) {
        Stream<String> methods = Arrays.stream(klazz.getMethods()).map(n -> n.toString());
        List<String> methodDescriptors = methods.collect(Collectors.toList());
        for (String objectMethod : OBJECT_METHODS) {
            assertTrue("Must have method: " + objectMethod, methodDescriptors.contains(objectMethod));
        }
    }

    private List<Field> getFilteredFields(Class<?> klazz) {
        List<Field> fields = new LinkedList<Field>();
        for (Field f : klazz.getDeclaredFields()) {
            if (f.getName().equals("$jacocoData")) {
                // This is added by JaCoCo when testing from Gradle.
                continue;
            }
            fields.add(f);
        }

        return fields;
    }

    private List<Method> getFilteredMethods(Class<?> klazz) {
        List<Method> methods = new LinkedList<Method>();
        for (Method m : klazz.getDeclaredMethods()) {
            if (m.getName().equals("$jacocoInit")) {
                // This is the "probe" method added by JaCoCo when running from Gradle
                continue;
            }
            methods.add(m);
        }

        return methods;
    }

}
