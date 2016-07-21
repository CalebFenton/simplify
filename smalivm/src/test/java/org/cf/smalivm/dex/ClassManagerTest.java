package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.junit.BeforeClass;

import java.io.IOException;

public class ClassManagerTest {

    private static final String TEST_DIRECTORY = "resources/test/smali";

    private static ClassManager manager;

    @BeforeClass
    public static void getClassManager() throws IOException {
        manager = new ClassManagerFactory().build(TEST_DIRECTORY);
    }

}
