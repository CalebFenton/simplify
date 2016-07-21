package org.cf.smalivm.dex;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.junit.BeforeClass;

public class ClassManagerTest {

    private static final String TEST_DIRECTORY = "resources/test/smali";

    private static ClassManager manager;

    @BeforeClass
    public static void getClassManager() throws IOException {
        manager = new ClassManagerFactory().build(TEST_DIRECTORY);
    }

}
