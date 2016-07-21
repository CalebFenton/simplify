package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.jf.dexlib2.iface.ClassDef;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

public class ClassBuilderTest {

    private static final String TEST_SMALI_PATH = "resources/test/smali/class_builder";
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private ClassBuilder classBuilder;
    private ClassManager classManager;

    @Before
    public void setUp() throws IOException {
        classManager = new ClassManagerFactory().build(TEST_SMALI_PATH);
        classBuilder = new ClassBuilder();
    }

    @Test
    public void canBuildSimpleClass() throws Exception {
        String className = "Lorg/cf/test/SimpleClass;";
        ClassDef classDef = classManager.getVirtualClass(className).getClassDef();
        classBuilder.build(classDef);
    }

}
