package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.VirtualArray;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualPrimitive;
import org.cf.smalivm.type.VirtualType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassManagerTest {

    private static final String TEST_DIRECTORY = "resources/test/smali";

    private static ClassManager manager;

    @BeforeClass
    public static void getClassManager() throws IOException {
        manager = new ClassManagerFactory().build(TEST_DIRECTORY);
    }

    @Test
    public void canGetVirtualClassFromString() {
        VirtualClass virtualClass = manager.getVirtualClass(CommonTypes.STRING);
        assertEquals(CommonTypes.STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualClassFromClass() {
        VirtualClass virtualClass = manager.getVirtualClass(String.class);
        assertEquals(CommonTypes.STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualPrimitiveFromString() {
        VirtualType virtualType = manager.getVirtualType(CommonTypes.INTEGER);
        assertEquals(CommonTypes.INTEGER, virtualType.getName());
        assertTrue(virtualType instanceof VirtualPrimitive);
    }

    @Test
    public void canGetVirtualPrimitiveFromClass() {
        VirtualType virtualType = manager.getVirtualType(CommonTypes.INTEGER);
        assertEquals(CommonTypes.INTEGER, virtualType.getName());
        assertTrue(virtualType instanceof VirtualPrimitive);
    }

    @Test
    public void canGetVirtualArrayFromString() {
        String typeSignature = "[" + CommonTypes.STRING;
        VirtualType virtualType = manager.getVirtualType(typeSignature);
        assertEquals(typeSignature, virtualType.getName());
        assertTrue(virtualType instanceof VirtualArray);
    }

    @Test
    public void canGetVirtualArrayFromClass() {
        VirtualType virtualType = manager.getVirtualType(String[].class);
        assertEquals("[" + CommonTypes.STRING, virtualType.getName());
        assertTrue(virtualType instanceof VirtualArray);
    }
}
