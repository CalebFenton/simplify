package org.cf.smalivm.smali;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.cf.util.ClassNameUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SmaliClassLoaderTest {

    private static final String[] OBJECT_METHODS = new String[] {
                    "public boolean java.lang.Object.equals(java.lang.Object)",
                    "public final native java.lang.Class java.lang.Object.getClass()",
                    "public final native void java.lang.Object.notify()",
                    "public final native void java.lang.Object.notifyAll()",
                    "public final native void java.lang.Object.wait(long) throws java.lang.InterruptedException",
                    "public final void java.lang.Object.wait() throws java.lang.InterruptedException",
                    "public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException",
                    "public java.lang.String java.lang.Object.toString()",
                    "public native int java.lang.Object.hashCode()" };

    private SmaliClassLoader classLoader;
    private ClassManager classManager;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private void assertHasObjectMethods(Class<?> klazz) {
        Stream<String> methods = Arrays.stream(klazz.getMethods()).map(n -> n.toString());
        List<String> methodDescriptors = methods.collect(Collectors.toList());
        for (String objectMethod : OBJECT_METHODS) {
            assertTrue(methodDescriptors.contains(objectMethod));
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

    // @Test
    public void breakStuff() throws ClassNotFoundException {
        for (String className : classManager.getFrameworkClassNames()) {
            if (className.startsWith("Ljava/")) {
                continue;
            }

            String binaryName = ClassNameUtils.internalToBinary(className);
            Class<?> klazz = classLoader.loadClass(binaryName);
            System.out.println("loaded: " + klazz);

            // This causes additional validation (errors):
            // klazz.getDeclaredMethods();
            // klazz.getDeclaredFields();
        }
    }

    @Test
    public void loadsJVMClasses() throws ClassNotFoundException {
        Class<?> expected = String.class;
        Class<?> actual = classLoader.loadClass(expected.getName());

        assertEquals(expected, actual);
    }

    @Test
    public void loadsClassThatReferencesNonExistantClassAndThrowsExceptionWhenVerifyingIt()
                    throws ClassNotFoundException {
        String className = "org.cf.test.NonExistantReference";
        Class<?> klazz = classLoader.loadClass(className);

        exception.expect(NoClassDefFoundError.class);
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
        assertEquals("public static void " + className + ".simpleLoop(int,java.lang.Object)", methods.get(0).toString());
    }

    @Before
    public void setUp() throws IOException {
        ClassManagerFactory managerFactory = new ClassManagerFactory();
        classManager = managerFactory.build("resources/test/class_builder");
        classLoader = new SmaliClassLoader(classManager);
    }

    @Test
    public void throwsExceptionWhenLoadingNonExistantAndReferencedClass() throws ClassNotFoundException {
        String nonExistantClassName = "does.not.exist";
        exception.expect(ClassNotFoundException.class);
        classLoader.loadClass(nonExistantClassName);
    }

    @Test
    public void throwsExceptionWhenLoadingNonExistantAndUnreferencedClass() throws ClassNotFoundException {
        String nonExistantClassName = "asdf";
        exception.expect(ClassNotFoundException.class);
        classLoader.loadClass(nonExistantClassName);
    }

}
