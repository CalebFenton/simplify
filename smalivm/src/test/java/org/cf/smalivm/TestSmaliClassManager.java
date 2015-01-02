package org.cf.smalivm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.cf.smalivm.exception.UnknownAncestors;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSmaliClassManager {

    private static final String TEST_DIRECTORY = "resources/test";

    private static SmaliClassManager manager;

    @BeforeClass
    public static void getClassManager() throws IOException {
        manager = new SmaliClassManager(TEST_DIRECTORY);
    }

    @Test
    public void testChildIsInstanceOfParent() throws UnknownAncestors {
        boolean isInstance = manager.isInstance("Lchild_class;", "Lparent_class;");

        assertTrue(isInstance);
    }

    @Test
    public void testCanUnderstandNativeMethods() throws UnknownAncestors {
        boolean isNative = manager.methodIsNative("Lnative_method_class;->nativeMethod()V");

        assertTrue(isNative);
    }

    @Test
    public void testChildIsInstanceOfGrandParent() throws UnknownAncestors {
        boolean isInstance = manager.isInstance("Lchild_class;", "Lgrandparent_class;");

        assertTrue(isInstance);
    }

    @Test
    public void testParentIsNotInstanceOfChild() throws UnknownAncestors {
        boolean isInstance = manager.isInstance("Lparent_class;", "Lchild_class;");

        assertFalse(isInstance);
    }

    @Test
    public void testStringIsInstanceOfObject() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(String.class, Object.class);

        assertTrue(isInstance);
    }

    @Test
    public void testStringArrayIsInstanceOfObjectArray() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(String[].class, Object[].class);

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
    public void testIntPrimitiveIsNotInstanceOfLong() throws UnknownAncestors {
        boolean isInstance = manager.isInstance(int.class, Long.class);

        assertFalse(isInstance);
    }

    @Test(expected = UnknownAncestors.class)
    public void testUnknownChildThrowsUnknownAncestors() throws UnknownAncestors {
        manager.isInstance("Lthis_certainly_wont_exists;", "Lparent_class;");
    }

    @Test
    public void testUnknownParentDoesNotThrowUnknownAncestors() throws UnknownAncestors {
        manager.isInstance("Lchild_class;", "Lthis_certainly_wont_exists;");
    }

}
