package org.cf.smalivm.smali;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SmaliClassLoaderTest {

    private SmaliClassLoader classLoader;
    private ClassManager classManager;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void loadsActualJVMClasses() throws ClassNotFoundException {
        Class<?> expected = String.class;
        Class<?> actual = classLoader.loadClass(expected.getName());

        assertEquals(expected, actual);
    }

    @Test
    public void loadsClassWhichReferencesNonExistantClass() throws ClassNotFoundException {
        String nonExistantClassName = "org.cf.test.NonExistantReference";
        classLoader.loadClass(nonExistantClassName);
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
