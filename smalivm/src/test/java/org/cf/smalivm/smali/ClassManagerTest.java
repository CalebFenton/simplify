package org.cf.smalivm.smali;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.smalivm.smali.ClassManager;
import org.cf.smalivm.smali.ClassManagerFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClassManagerTest {

    private static final String TEST_DIRECTORY = "resources/test";
    private static final String CHILD_CLASS = "Lchild_class;";
    private static final String GRANDPARENT_CLASS = "Lgrandparent_class;";
    private static final String NON_EXISTENT_CLASS = "Lthis_certainly_wont_exists;";
    private static final String PARENT_CLASS = "Lparent_class;";

    private static ClassManager manager;

    @Test
    public void testArrayIsInstanceOfObject() throws UnknownAncestors {
        boolean isInstance = manager.isInstance("[B", "Ljava/lang/Object;");

        assertTrue(isInstance);
    }

    @Test
    public void testCanUnderstandNativeMethods() throws UnknownAncestors {
        boolean isNative = manager.isNativeMethod("Lnative_method_class;->nativeMethod()V");

        assertTrue(isNative);
    }

    @Test
    public void testChildIsInstanceOfGrandParent() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(CHILD_CLASS, GRANDPARENT_CLASS);

        assertTrue(isInstance);
    }

    @Test
    public void testChildIsInstanceOfParent() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(CHILD_CLASS, PARENT_CLASS);

        assertTrue(isInstance);
    }

    @Test
    public void testGetFieldsAndTypesReturnsFieldsFromSuperClasses() {
        List<String> fieldNameAndTypes = manager.getFieldNameAndTypes("Lchild_class;");
        String[] actual = fieldNameAndTypes.toArray(new String[fieldNameAndTypes.size()]);
        String[] expected = new String[] { "childField:I", "parentField:I", "grandparentField:I" };
        Arrays.sort(actual);
        Arrays.sort(expected);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testIntPrimitiveIsInstanceIntPrimitive() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, int.class);

        assertTrue(isInstance);
    }

    @Test
    public void testIntPrimitiveIsInstanceOfInteger() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, Integer.class);

        assertTrue(isInstance);
    }

    @Test
    public void testIntPrimitiveIsInstanceOfNumber() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, Number.class);

        assertTrue(isInstance);
    }

    @Test
    public void testIntPrimitiveIsNotInstanceOfIntArray() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, int[].class);

        assertFalse(isInstance);
    }

    @Test
    public void testIntPrimitiveIsNotInstanceOfLong() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, Long.class);

        assertFalse(isInstance);
    }

    @Test
    public void testParentIsNotInstanceOfChild() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(PARENT_CLASS, CHILD_CLASS);

        assertFalse(isInstance);
    }

    @Test
    public void testStringArrayIsInstanceOf2DObjectArray() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(String[].class, Object[][].class);

        assertFalse(isInstance);
    }

    @Test
    public void testStringArrayIsInstanceOfObjectArray() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(String[].class, Object[].class);

        assertTrue(isInstance);
    }

    @Test
    public void testStringIsInstanceOfObject() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(String.class, Object.class);

        assertTrue(isInstance);
    }

    @Test(expected = UnknownAncestors.class)
    public void testUnknownChildThrowsUnknownAncestors() throws UnknownAncestors {
        manager.isInstance(NON_EXISTENT_CLASS, PARENT_CLASS);
    }

    @Test
    public void testUnknownParentDoesNotThrowUnknownAncestors() throws UnknownAncestors {
        manager.isInstance(CHILD_CLASS, NON_EXISTENT_CLASS);
    }

    @BeforeClass
    public static void getClassManager() throws IOException {
        manager = new ClassManagerFactory().build(TEST_DIRECTORY);
    }

}
