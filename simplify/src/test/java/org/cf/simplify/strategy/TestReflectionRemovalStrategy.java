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
import org.cf.smalivm.type.LocalMethod;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestReflectionRemovalStrategy {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestReflectionRemovalStrategy.class.getSimpleName());

    private static final String CLASS_NAME = "Lreflection_removal_strategy_test;";
    private static final String METHOD_TYPE = "Ljava/lang/reflect/Method;";
    private static final String METHOD_WITH_3_LOCALS_AND_0_AVAILABLE = "MethodInvokeWith3LocalsAnd0Available()V";
    private static final String METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE = "MethodInvokeWith10LocalsAnd7ContiguousAvailable()V";

    public static class TestNonLocalInterfaceMethod {
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

    public static class TestNonOptimizableScenarios {
        private static final UnknownValue UNKNOWN_METHOD = new UnknownValue();
        private static final Method PRIVATE_NON_LOCAL_METHOD = getMethod(System.class, "checkKey",
                        new Class<?>[] { String.class });
        private static final LocalMethod LOCAL_STATIC_METHOD = new LocalMethod(CLASS_NAME
                        + "->FourParameterMethod(IIII)V");

        private static final String[] EXPECTED_LINES = new String[] {
            "nop",
            "invoke-virtual {r0, r1, r2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
            "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
            "return-void", };

        @Test
        public void testPrivateNonLocalMethodIsNotOptimized() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0,
                            PRIVATE_NON_LOCAL_METHOD, METHOD_TYPE, 1, 0, "I");

            testSmali(mbgraph, EXPECTED_LINES);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testUnknownMethodIsNotOptimized() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, UNKNOWN_METHOD,
                            METHOD_TYPE, 1, 0, "I");

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

    public static class TestStaticNonLocalMethodWithNoParameters {
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
                            METHOD_TYPE, 1, 0, "I");
            String[] expectedLines = new String[] { "nop", "invoke-static {}, Ljava/lang/System;->gc()V",
                            "move-result-object r0", "return-void", };

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }
    }

    public static class TestStaticPrivateLocalMethod {
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

    public static class TestVirtualLocalMethodWithSixParameters {
        private static final LocalMethod METHOD = new LocalMethod(CLASS_NAME + "->SixParameterMethod(IIIIII)V");
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

    public static class TestVirtualPrivateLocalMethod {
        private static final LocalMethod METHOD = new LocalMethod(CLASS_NAME + "->PrivateVirtualMethod()V");
        private static final String[] expectedLines = new String[] {
                        "nop",
                        "invoke-direct {r1}, Lreflection_removal_strategy_test;->PrivateVirtualMethod()V",
                        "invoke-static {r0, r1, r2}, Li_need/these/registers;->mine(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",
                        "return-void", };

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0Available() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, 0, "I");

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void testOptimizesToExpectedLinesWith3LocalsAnd0AvailableAndUnknownTarget() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE,
                            1, new UnknownValue(), CLASS_NAME);

            testSmali(mbgraph, expectedLines);
            testRegisterCount(mbgraph, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }

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
