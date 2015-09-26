package org.cf.simplify.strategy;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.LocalClass;
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestReflectionRemovalStrategy {

    public static class TestFieldLookups {

        private static final String[] EXPECTED_SHARED = {
                        "invoke-virtual {r0, r1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;",
                        "move-result-object r0", };
        private static final String LOCAL_INSTANCE_FIELD = "someInt";
        private static final String LOCAL_INSTANCE_FIELD_TYPE = "I";

        private static final String LOCAL_STATIC_FIELD = "someStaticObject";
        private static final String LOCAL_STATIC_FIELD_TYPE = "Ljava/lang/Object;";
        private static final String METHOD_WITH_MOVE_RESULT = "FieldLookupWithMoveResult()V";
        private static final String METHOD_WITHOUT_MOVE_RESULT_AND_NO_AVAILABLE_REGISTERS = "FieldLookupWithoutMoveResultWithNoAvailableRegisters()V";

        private static final String METHOD_WITHOUT_MOVE_RESULT_AND_ONE_AVAILABLE_REGISTER = "FieldLookupWithoutMoveResultWithOneAvailableRegister()V";

        @Test
        public void testLocalInstanceFieldWithMoveResult() {
            LocalClass fieldClass = new LocalClass(CLASS_NAME);
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_MOVE_RESULT, 0, fieldClass, "Ljava/lang/Class;",
                            1, LOCAL_INSTANCE_FIELD, "Ljava/lang/String;", 2, null, null);
            String[] endLines = new String[] {
                            "iget r0, r2, " + CLASS_NAME + "->" + LOCAL_INSTANCE_FIELD + ":" + LOCAL_INSTANCE_FIELD_TYPE,
                            "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
        }

        @Test
        public void testLocalStaticFieldWithMoveResult() {
            LocalClass fieldClass = new LocalClass(CLASS_NAME);
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_MOVE_RESULT, 0, fieldClass, "Ljava/lang/Class;",
                            1, LOCAL_STATIC_FIELD, "Ljava/lang/String;", 2, null, null);
            String[] endLines = new String[] {
                            "sget-object r0, " + CLASS_NAME + "->" + LOCAL_STATIC_FIELD + ":" + LOCAL_STATIC_FIELD_TYPE,
                            "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
        }

        @Test
        public void testLocalStaticFieldWithMoveResultAndWithOneAvailableRegister() {
            LocalClass fieldClass = new LocalClass(CLASS_NAME);
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITHOUT_MOVE_RESULT_AND_ONE_AVAILABLE_REGISTER, 0,
                            fieldClass, "Ljava/lang/Class;", 1, LOCAL_STATIC_FIELD, "Ljava/lang/String;", 2, null, null);
            String[] endLines = new String[] {
                            "sget-object r2, " + CLASS_NAME + "->" + LOCAL_STATIC_FIELD + ":" + LOCAL_STATIC_FIELD_TYPE,
                            "invoke-static {r0, r1}, Llolmoney/moneylol;->lol(II)V", "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
        }

        @Test
        public void testLocalStaticFieldWithMoveResultAndWithoutAvailableRegistersDoesNotOptimize() {
            LocalClass fieldClass = new LocalClass(CLASS_NAME);
            String fieldName = "someStaticObject";
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITHOUT_MOVE_RESULT_AND_NO_AVAILABLE_REGISTERS, 0,
                            fieldClass, "Ljava/lang/Class;", 1, fieldName, "Ljava/lang/String;", 2, null, null);
            String[] endLines = new String[] {
                            "invoke-virtual {r0, r2}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;",
                            "invoke-static {r0, r1, r2}, Llolmoney/moneylol;->lol(III)V", "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
        }

        @Test
        public void testNonLocalStaticFieldWithMoveResult() {
            Class<?> fieldClass = Integer.class;
            String fieldName = "MAX_VALUE";
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_MOVE_RESULT, 0, fieldClass, "Ljava/lang/Class;",
                            1, fieldName, "Ljava/lang/String;", 2, null, null);
            String[] endLines = new String[] { "sget r0, Ljava/lang/Integer;->MAX_VALUE:I", "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
        }
    }

    public static class TestInvokeNonLocalInterfaceMethod {

        private static final Method METHOD = getMethod(List.class, "isEmpty", new Class<?>[0]);

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, new ArrayList<String>(), "Ljava/lang/ArrayList;", 2, 0, "I");
            String[] expectedLines = new String[] {
                            "nop",
                            "invoke-interface {r1}, Ljava/util/List;->isEmpty()Z",
                            "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                            "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }
    public static class TestInvokeNonOptimizableScenarios {

        private static final String[] EXPECTED_LINES = new String[] {
                        "nop",
                        "invoke-virtual {r0, r1, r2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
                        "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                        "return-void", };
        private static final LocalMethod LOCAL_STATIC_METHOD = new LocalMethod(
                        CLASS_NAME + "->FourParameterMethod(IIII)V");
        private static final Method PRIVATE_NON_LOCAL_METHOD = getMethod(System.class, "checkKey",
                        new Class<?>[] { String.class });

        private static final UnknownValue UNKNOWN_METHOD = new UnknownValue();

        @Test
        public void testPrivateNonLocalMethodIsNotOptimized() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0,
                            PRIVATE_NON_LOCAL_METHOD, METHOD_TYPE, 1, 0, "I", 2, null, "null");

            testSmali(mbgraph, EXPECTED_LINES);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testUnknownMethodIsNotOptimized() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, UNKNOWN_METHOD,
                            METHOD_TYPE, 1, 0, "I", 2, null, "null");

            testSmali(mbgraph, EXPECTED_LINES);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testUnknownParametersIsNotOptimized() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, LOCAL_STATIC_METHOD,
                            METHOD_TYPE, 1, 0, "I", 2, new UnknownValue(), "[Ljava/lang/Object");

            testSmali(mbgraph, EXPECTED_LINES);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }
    public static class TestInvokeStaticNonLocalMethodWithNoParameters {

        private static final Method METHOD = getMethod(System.class, "gc", new Class<?>[0]);

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, 0, "I", 2, 0, "I");
            String[] expectedLines = new String[] {
                            "nop",
                            "invoke-static {}, Ljava/lang/System;->gc()V",
                            "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                            "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testOptimizesToExpectedLinesWith7ContiguousAvailable() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD,
                            METHOD_TYPE, 1, 0, "I", 2, null, "null");
            // TODO: should remove move-result after invoke if method returns void, replace if not object!
            String[] expectedLines = new String[] {
                            "nop", "invoke-static {}, Ljava/lang/System;->gc()V", "move-result-object r0",
                            "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }
    }
    public static class TestInvokeStaticPrivateLocalMethod {

        private static final LocalMethod METHOD = new LocalMethod(CLASS_NAME + "->FourParameterMethod(IIII)V");

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, 0, "I", 2, new Object[] { 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] expectedLines = new String[] {
                            "nop",
                            "const/4 r3, 0x0",
                            "aget-object r3, r2, r3",
                            "check-cast r3, Ljava/lang/Integer;",
                            "const/4 r4, 0x1",
                            "aget-object r4, r2, r4",
                            "check-cast r4, Ljava/lang/Integer;",
                            "const/4 r5, 0x2",
                            "aget-object r5, r2, r5",
                            "check-cast r5, Ljava/lang/Integer;",
                            "const/4 r6, 0x3",
                            "aget-object r6, r2, r6",
                            "check-cast r6, Ljava/lang/Integer;",
                            "invoke-static {r3, r4, r5, r6}, Lreflection_removal_strategy_test;->FourParameterMethod(IIII)V",
                            "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                            "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 7);
        }

        @Test
        public void testOptimizesToExpectedLinesWith7ContiguousAvailable() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD,
                            METHOD_TYPE, 1, 0, "I", 2, new Object[] { 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] expectedLines = new String[] {
                            "nop",
                            "const/4 r0, 0x0",
                            "aget-object r0, r2, r0",
                            "check-cast r0, Ljava/lang/Integer;",
                            "const/4 r1, 0x1",
                            "aget-object r1, r2, r1",
                            "check-cast r1, Ljava/lang/Integer;",
                            "const/4 r3, 0x2",
                            "aget-object r3, r2, r3",
                            "check-cast r3, Ljava/lang/Integer;",
                            "const/4 r4, 0x3",
                            "aget-object r4, r2, r4",
                            "check-cast r4, Ljava/lang/Integer;",
                            "invoke-static {r0, r1, r3, r4}, Lreflection_removal_strategy_test;->FourParameterMethod(IIII)V",
                            "move-result-object r0", "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }
    }

    public static class TestInvokeVirtualLocalMethodWithSixParameters {

        private static final String[] EXPECTED_SHARED = new String[] {
                        "nop",
                        "move-object/16 r3, r1",
                        "const/4 r4, 0x0",
                        "aget-object r4, r2, r4",
                        "check-cast r4, Ljava/lang/Integer;",
                        "const/4 r5, 0x1",
                        "aget-object r5, r2, r5",
                        "check-cast r5, Ljava/lang/Integer;",
                        "const/4 r6, 0x2",
                        "aget-object r6, r2, r6",
                        "check-cast r6, Ljava/lang/Integer;",
                        "const/4 r7, 0x3",
                        "aget-object r7, r2, r7",
                        "check-cast r7, Ljava/lang/Integer;",
                        "const/4 r8, 0x4",
                        "aget-object r8, r2, r8",
                        "check-cast r8, Ljava/lang/Integer;",
                        "const/4 r9, 0x5",
                        "aget-object r9, r2, r9",
                        "check-cast r9, Ljava/lang/Integer;",
                        "invoke-virtual/range {r3 .. r9}, Lreflection_removal_strategy_test;->SixParameterMethod(IIIIII)V", };
        private static final LocalMethod METHOD = new LocalMethod(CLASS_NAME + "->SixParameterMethod(IIIIII)V");

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, 0, "I", 2, new Object[] { 6, 5, 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] endLines = new String[] {
                            "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                            "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 10);
        }

        @Test
        public void testOptimizesToExpectedLinesWith7ContiguousAvailable() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD,
                            METHOD_TYPE, 1, 0, "I", 2, new Object[] { 6, 5, 4, 3, 2, 1 }, "Ljava/lang/Object;");
            String[] endLines = new String[] { "move-result-object r0", "return-void", };
            String[] expectedLines = ArrayUtils.addAll(EXPECTED_SHARED, endLines);

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }

    }

    public static class TestInvokeVirtualPrivateLocalMethod {

        private static final String[] expectedLines = new String[] {
                        "nop",
                        "invoke-direct {r1}, Lreflection_removal_strategy_test;->PrivateVirtualMethod()V",
                        "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                        "return-void", };
        private static final LocalMethod METHOD = new LocalMethod(CLASS_NAME + "->PrivateVirtualMethod()V");

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, 0, "I", 2, null, "null");

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0AvailableAndUnknownTarget() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, new UnknownValue(), CLASS_NAME, 2, null, "null");

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }

    public static class TestProtectedMethods {

        @Test
        public void testInstanceGetOpcodes() {
            boolean isStatic = false;
            assertEquals(Opcode.IGET, ReflectionRemovalStrategy.getGetOpcode("I", isStatic));
            assertEquals(Opcode.IGET_BOOLEAN, ReflectionRemovalStrategy.getGetOpcode("Z", isStatic));
            assertEquals(Opcode.IGET_BYTE, ReflectionRemovalStrategy.getGetOpcode("B", isStatic));
            assertEquals(Opcode.IGET_CHAR, ReflectionRemovalStrategy.getGetOpcode("C", isStatic));
            assertEquals(Opcode.IGET_OBJECT, ReflectionRemovalStrategy.getGetOpcode("Ljava/lang/Object;", isStatic));
            assertEquals(Opcode.IGET_SHORT, ReflectionRemovalStrategy.getGetOpcode("S", isStatic));
            assertEquals(Opcode.IGET_WIDE, ReflectionRemovalStrategy.getGetOpcode("J", isStatic));
            assertEquals(Opcode.IGET_WIDE, ReflectionRemovalStrategy.getGetOpcode("D", isStatic));
        }

        @Test
        public void testStaticGetOpcodes() {
            boolean isStatic = true;
            assertEquals(Opcode.SGET, ReflectionRemovalStrategy.getGetOpcode("I", isStatic));
            assertEquals(Opcode.SGET_BOOLEAN, ReflectionRemovalStrategy.getGetOpcode("Z", isStatic));
            assertEquals(Opcode.SGET_BYTE, ReflectionRemovalStrategy.getGetOpcode("B", isStatic));
            assertEquals(Opcode.SGET_CHAR, ReflectionRemovalStrategy.getGetOpcode("C", isStatic));
            assertEquals(Opcode.SGET_OBJECT, ReflectionRemovalStrategy.getGetOpcode("Ljava/lang/Object;", isStatic));
            assertEquals(Opcode.SGET_SHORT, ReflectionRemovalStrategy.getGetOpcode("S", isStatic));
            assertEquals(Opcode.SGET_WIDE, ReflectionRemovalStrategy.getGetOpcode("J", isStatic));
            assertEquals(Opcode.SGET_WIDE, ReflectionRemovalStrategy.getGetOpcode("D", isStatic));
        }

    }

    private static final String CLASS_NAME = "Lreflection_removal_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestReflectionRemovalStrategy.class.getSimpleName());

    private static final String METHOD_TYPE = "Ljava/lang/reflect/Method;";

    private static final String METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE = "MethodInvokeWith10LocalsAnd7ContiguousAvailable()V";

    private static final String METHOD_WITH_3_LOCALS_AND_0_AVAILABLE = "MethodInvokeWith3LocalsAnd0Available()V";

    private static Method getMethod(Class<?> klazz, String methodName, Class<?>[] parameterTypes) {
        try {
            return klazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static MethodBackedGraph getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(args);
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName, initial);
        ReflectionRemovalStrategy strategy = new ReflectionRemovalStrategy(mbgraph);
        strategy.perform();

        return mbgraph;
    }

    private static void testRegisterCount(MethodBackedGraph mbgraph, String methodSignature, int expectedRegisterCount) {
        BuilderMethod method = mbgraph.getVM().getClassManager().getMethod(CLASS_NAME, methodSignature);
        int actualRegisterCount = method.getImplementation().getRegisterCount();

        assertEquals(expectedRegisterCount, actualRegisterCount);
    }

    private static void testSmali(MethodBackedGraph mbgraph, String[] expectedLines) {
        String actualLines[] = StringUtils.split(mbgraph.toSmali(), "\n");
        assertArrayEquals(expectedLines, actualLines);
    }

}
