package simplify.vm;

import static org.junit.Assert.assertEquals;

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
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestInvokeWhitelistedMethodsHasNoSideEffects() {
        String methodName = "InvokeWhitelistedMethods()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceNonLocalWhitelistedClassHasNoSideEffects() {
        String methodName = "NewInstanceNonLocalWhitelistedClass()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestInvokeMethodWithNoSideEffectsHasNoSideEffects() {
        String methodName = "InvokeMethodWithNoSideEffects()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestConstOpsHaveNoSideEffects() {
        String methodName = "ConstOps()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceOfMethodWithStaticInitializerWithNoSideEffectsHasNoSideEffects() {
        // Method names? Pssh, we have method PARAGRAPHS.
        String methodName = "NewInstanceOfMethodWithStaticInitializerWithNoSideEffects()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceOfMethodWithNoStaticInitializerHasNoSideEffects() {
        String methodName = "NewInstanceOfMethodWithNoStaticInitializer()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.NONE, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestWriteOutputStreamHasStrongSideEffects() {
        String methodName = "WriteOutputStream(Ljava/lang/OutputStream;[B)V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestInvokeUnknownMethodHasStrongSideEffects() {
        String methodName = "InvokeUnknownMethod()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestInvokeSideEffectMethodHasStrongSideEffects() {
        String methodName = "InvokeSideEffectMethod(Ljava/lang/OutputStream;[B)V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceNonLocalNonWhitelistedClassHasStrongSideEffects() {
        String methodName = "NewInstanceNonLocalNonWhitelistedClass()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceOfMethodWithStaticInitializerWithStrongSideEffectsHasStrongSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithStrongSideEffects()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestNewInstanceOfMethodWithStaticInitializerWithWeakSideEffectsHasWeakSideEffects() {
        String methodName = "NewInstanceOfClassWithStaticInitializerWithWeakSideEffects()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.WEAK, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestInvokeOfNonAnalyzableMethodHasStrongSideEffects() {
        String methodName = "InvokeOfNonAnalyzableMethod()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.STRONG, graph.getStrongestSideEffectType());
    }

    @Test
    public void TestModifyInstanceMemberHasStrongSideEffects() {
        String methodName = "ModifyInstanceMember()V";
        ContextGraph graph = vm.execute(CLASS_NAME + "->" + methodName);

        assertEquals(SideEffect.Type.WEAK, graph.getStrongestSideEffectType());
    }
}
