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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClassBuilderTest {

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
    private static final String TEST_SMALI_PATH = "resources/test/class_builder";

    private ClassBuilder classBuilder;
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

    @Before
    public void setUp() throws IOException {
        classManager = new ClassManagerFactory().build(TEST_SMALI_PATH);
        classBuilder = new ClassBuilder(classManager);
    }

    @Test
    public void canBuildCircularReferences() throws Exception {
        String className1 = "org.cf.test.CircularReference1";
        String className2 = "org.cf.test.CircularReference2";
        Map<String, Class<?>> classes = classBuilder.build(className1, className2);
        Class<?> klazz1 = classes.get(className1);
        Class<?> klazz2 = classes.get(className2);

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
    public void canBuildComplexClass() throws Exception {
        String className = "org.cf.test.ComplexClass";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
        assertHasObjectMethods(klazz);

        Class<?> superKlazz = classes.get(klazz.getSuperclass().getName());
        assertEquals(superKlazz, klazz.getSuperclass());
        assertEquals("class org.cf.test.SuperClass", klazz.getSuperclass().toString());

        List<Method> methods = getFilteredMethods(klazz);
        assertEquals(6, methods.size());

        List<Field> fields = getFilteredFields(klazz);
        assertEquals(1, fields.size());
    }

    @Test
    public void canBuildInterfaceDoubleDependency() throws Exception {
        // TODO: easy make our own test case,
        // Class A implements interface C
        // Class B implements interface C
        // Interface C references class A and B
        // Making an unloaded reference does a lookup of declared methods for interfaces and super types
        // If, while building an interface, an implementing class is built, a latent type is returned
        // and you can't get declared methods of a latent type
        String className = "android.os.Bundle";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
    }

    @Test
    public void testy() throws ClassNotFoundException, IOException {
        classManager = new ClassManagerFactory().build("../simplify/obfuscated-example");
        classBuilder = new ClassBuilder(classManager);
        String className = "android.support.v4.media.TransportMediatorJellybeanMR2$2";
        className = "org.cf.obfuscated.Reflection";
        // android.support.v4.view.accessibility.AccessibilityNodeInfoCompatJellybeanMr2
        Map<String, Class<?>> classes = classBuilder.build(className);

    }

    @Test
    public void canBuildSelfReferencingClass() throws Exception {
        String className = "org.cf.test.SelfReference";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
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
    public void canBuildSimpleClass() throws Exception {
        String className = "org.cf.test.SimpleClass";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
        assertHasObjectMethods(klazz);

        List<Method> methods = getFilteredMethods(klazz);
        assertEquals(1, methods.size());
        assertEquals("public static void org.cf.test.SimpleClass.simpleLoop(int,java.lang.Object)", methods.get(0)
                        .toString());
    }

    @Test
    public void canBuildClassWhichReferencesNonExistantClass() throws Exception {
        String className = "org.cf.test.NonExistantReference";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
        assertHasObjectMethods(klazz);
    }

    @Test
    public void canBuildClassWithOverloadedFields() throws Exception {
        String className = "org.cf.test.OverloadedFields";
        Map<String, Class<?>> classes = classBuilder.build(className);
        Class<?> klazz = classes.get(className);
        assertHasObjectMethods(klazz);

        List<Field> fields = getFilteredFields(klazz);
        assertEquals(2, fields.size());

        assertEquals("public static int org.cf.test.OverloadedFields.field1", fields.get(0).toString());
        assertEquals("public static java.lang.Object org.cf.test.OverloadedFields.field1", fields.get(1).toString());
    }

}
