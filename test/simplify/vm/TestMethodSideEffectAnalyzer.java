package simplify.vm;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import simplify.Main;

public class TestMethodSideEffectAnalyzer {
    private static final String CLASS_NAME = "Lmethod_side_effects_test;";
    private static final int MAX_NODE_VISITS = 100;
    private static final int MAX_CALL_DEPTH = 10;

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private VirtualMachine vm;
    private static Map<String, BuilderClassDef> classNameToDef;

    @BeforeClass
    public static void setupBeforeClass() {
        classNameToDef = VMTester.getClassNameToBuilderClassDef();
    }

    @Before
    public void setupVM() throws Exception {
        List<BuilderClassDef> classDefs = new ArrayList<BuilderClassDef>(classNameToDef.values());
        vm = new VirtualMachine(classDefs, MAX_NODE_VISITS, MAX_CALL_DEPTH);
    }

    @Test
    public void TestEmptyMethodHasNoSideEffects() {
        String methodName = "EmptyMethod()V";
        String methodDescriptor = CLASS_NAME + "->" + methodName;
        boolean hasSideEffects = MethodSideEfectAnalyzer.analyzeMethod(vm, methodDescriptor);

        assertFalse(hasSideEffects);
    }

    @Test
    public void TestConstOpsHaveNoSideEffects() {
        String methodName = "ConstOps()V";
        String methodDescriptor = CLASS_NAME + "->" + methodName;
        boolean hasSideEffects = MethodSideEfectAnalyzer.analyzeMethod(vm, methodDescriptor);

        assertFalse(hasSideEffects);
    }

    @Test
    public void TestNewInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() {
        // Method names? Pssh, we have method PARAGRAPHS.
        String methodName = "NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        String methodDescriptor = CLASS_NAME + "->" + methodName;
        boolean hasSideEffects = MethodSideEfectAnalyzer.analyzeMethod(vm, methodDescriptor);

        assertFalse(hasSideEffects);
    }

    @Test
    public void TestNewInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() {
        String methodName = "NewInstanceOfMethodWithNoStaticInitializer()V";
        String methodDescriptor = CLASS_NAME + "->" + methodName;
        boolean hasSideEffects = MethodSideEfectAnalyzer.analyzeMethod(vm, methodDescriptor);

        assertFalse(hasSideEffects);
    }

}
