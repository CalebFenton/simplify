package org.cf.smalivm.dex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.VirtualArray;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualPrimitive;
import org.cf.smalivm.type.VirtualType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ClassManagerTest {

    private static ClassManager classManager;

    @BeforeAll
    public static void getClassManager() throws IOException {
        classManager = new ClassManagerFactory().build(VMTester.TEST_CLASS_PATH);
    }

    @Test
    public void canGetVirtualClassFromString() {
        VirtualClass virtualClass = classManager.getVirtualClass(CommonTypes.STRING);
        assertEquals(CommonTypes.STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualClassFromClass() {
        VirtualClass virtualClass = classManager.getVirtualClass(String.class);
        assertEquals(CommonTypes.STRING, virtualClass.getName());
    }

    @Test
    public void canGetVirtualPrimitiveFromString() {
        VirtualType virtualType = classManager.getVirtualType(CommonTypes.INTEGER);
        assertEquals(CommonTypes.INTEGER, virtualType.getName());
        assertTrue(virtualType instanceof VirtualPrimitive);
    }

    @Test
    public void canGetVirtualPrimitiveFromClass() {
        VirtualType virtualType = classManager.getVirtualType(CommonTypes.INTEGER);
        assertEquals(CommonTypes.INTEGER, virtualType.getName());
        assertTrue(virtualType instanceof VirtualPrimitive);
    }

    @Test
    public void canGetVirtualArrayFromString() {
        String typeSignature = "[" + CommonTypes.STRING;
        VirtualType virtualType = classManager.getVirtualType(typeSignature);
        assertEquals(typeSignature, virtualType.getName());
        assertTrue(virtualType instanceof VirtualArray);
    }

    @Test
    public void canGetVirtualArrayFromClass() {
        VirtualType virtualType = classManager.getVirtualType(String[].class);
        assertEquals("[" + CommonTypes.STRING, virtualType.getName());
        assertTrue(virtualType instanceof VirtualArray);
    }
}
