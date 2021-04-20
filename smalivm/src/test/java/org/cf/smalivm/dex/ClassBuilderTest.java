//package org.cf.smalivm.dex;
//
//import java.io.IOException;
//import org.cf.smalivm.VMTester;
//import org.cf.smalivm.VirtualMachine;
//import org.cf.smalivm.exception.VirtualMachineException;
//import org.cf.smalivm.type.ClassManager;
//import org.cf.smalivm.type.ClassManagerFactory;
//import org.jf.dexlib2.iface.ClassDef;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class ClassBuilderTest {
//
//    private static final String TEST_SMALI_PATH = VMTester.TEST_CLASS_PATH + "/class_builder";
//
//    private ClassBuilder classBuilder;
//    private ClassManager classManager;
//
//    @BeforeEach
//    public void setUp() throws IOException {
//        classManager = new ClassManagerFactory().build(TEST_SMALI_PATH);
//        classBuilder = new ClassBuilder(classManager);
//    }
//
//    @Test
//    public void canBuildSimpleClass() throws Exception {
//        String className = "Lorg/cf/test/SimpleClass;";
//        ClassDef classDef = classManager.getVirtualClass(className).getClassDef();
//        classBuilder.build(classDef);
//    }
//
//    @Test
//    public void canCallHashCodeOfDynamicallyGeneratedClass() throws VirtualMachineException {
//        String className = "Lhash_code;";
//        String methodName = "createInstance()V";
//        VirtualMachine vm = VMTester.spawnVM();
//        vm.execute(className, methodName);
//    }
//}
