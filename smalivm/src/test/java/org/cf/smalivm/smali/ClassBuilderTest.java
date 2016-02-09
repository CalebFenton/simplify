package org.cf.smalivm.smali;

import java.io.IOException;

import org.jf.dexlib2.iface.ClassDef;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClassBuilderTest {

    private static final String TEST_SMALI_PATH = "resources/test/class_builder";

    private ClassBuilder classBuilder;
    private ClassManager classManager;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws IOException {
        classManager = new ClassManagerFactory().build(TEST_SMALI_PATH);
        classBuilder = new ClassBuilder();
    }

    @Test
    public void canBuildSimpleClass() throws Exception {
        String className = "Lorg/cf/test/SimpleClass;";
        ClassDef classDef = classManager.getClass(className);
        classBuilder.build(classDef);
    }

}
