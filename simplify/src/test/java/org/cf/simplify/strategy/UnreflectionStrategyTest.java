package org.cf.simplify.strategy;

import com.google.common.collect.ObjectArrays;

import org.cf.simplify.ExecutionGraphManipulator;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcode;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class UnreflectionStrategyTest {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UnreflectionStrategyTest.class.getSimpleName());
    private static final String CLASS_NAME = "Lunreflection_strategy_test;";
    private static final Class<?> CLASS = getTestClass();
    private static final String METHOD_TYPE = "Ljava/lang/reflect/Method;";
    private static final String METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE =
            "methodInvokeWith10LocalsAnd7ContiguousAvailable()V";
    private static final String METHOD_WITH_3_LOCALS_AND_0_AVAILABLE = "methodInvokeWith3LocalsAnd0Available()V";

    private static java.lang.reflect.Method getMethod(Class<?> klazz, String methodName, Class<?>... parameterTypes) {
        try {
            return klazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ExecutionGraphManipulator getOptimizedGraph(String methodName, Object... args) {
        VMState initial = new VMState();
        if (args.length > 0) {
            initial.setRegisters(args);
        }
        ExecutionGraphManipulator manipulator = OptimizerTester.getGraphManipulator(CLASS_NAME, methodName, initial);
        UnreflectionStrategy strategy = new UnreflectionStrategy(manipulator);
        strategy.perform();

        return manipulator;
    }

    private static Class<?> getTestClass() {
        try {
            String binaryName = ClassNameUtils.internalToBinary(CLASS_NAME);
            return VMTester.spawnVM().getClassLoader().loadClass(binaryName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void testRegisterCount(ExecutionGraphManipulator manipulator, String methodDescriptor,
                                          int expectedRegisterCount) {
        ClassManager classManager = manipulator.getVM().getClassManager();
        VirtualMethod method = classManager.getMethod(CLASS_NAME, methodDescriptor);
        int actualRegisterCount = method.getImplementation().getRegisterCount();

        assertEquals(expectedRegisterCount, actualRegisterCount);
    }

    private static void testSmali(ExecutionGraphManipulator manipulator, String[] expectedLines) {
        String actualLines[] = manipulator.toSmali().split("\n");

        assertArrayEquals(expectedLines, actualLines);
    }

    public static class FieldUnreflection {

        private static final String[] EXPECTED_SHARED_SMALI =
                { "invoke-virtual {r0, r1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)" +
                  "Ljava/lang/reflect/Field;", "move-result-object r0", };

        private static final String INSTANCE_FIELD_NAME = "privateInstanceInt";
        private static final String INSTANCE_FIELD_TYPE = "I";

        private static final String METHOD_WITH_MOVE_RESULT = "fieldLookupWithMoveResult()V";
        private static final String METHOD_WITHOUT_MOVE_RESULT_AND_NO_AVAILABLE_REGISTERS =
                "fieldLookupWithoutMoveResultWithNoAvailableRegisters()V";

        private static final String METHOD_WITHOUT_MOVE_RESULT_AND_ONE_AVAILABLE_REGISTER =
                "fieldLookupWithoutMoveResultWithOneAvailableRegister()V";
        private static final String STATIC_FIELD_NAME = "privateStaticObject";
        private static final String STATIC_FIELD_TYPE = "Ljava/lang/Object;";

        @Test
        public void instanceFieldWithMoveResultOptimizesAsExpected() {
            ExecutionGraphManipulator manipulator =
                     getOptimizedGraph(METHOD_WITH_MOVE_RESULT, 0, CLASS, "Ljava/lang/Class;", 1, INSTANCE_FIELD_NAME,
                            "Ljava/lang/String;", 2, null, "Ljava/lang/Object;");
            String[] endLines = new String[] {
                    "iget r0, r2, " + CLASS_NAME + "->" + INSTANCE_FIELD_NAME + ":" + INSTANCE_FIELD_TYPE,
                    "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
        }

        @Test
        public void staticFieldWithMoveResultAndWithOneAvailableRegister() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITHOUT_MOVE_RESULT_AND_ONE_AVAILABLE_REGISTER, 0, CLASS,
                            "Ljava/lang/Class;", 1, STATIC_FIELD_NAME, "Ljava/lang/String;", 2, null,
                            "Ljava/lang/Object;");
            String[] endLines =
                    new String[] { "sget-object r2, " + CLASS_NAME + "->" + STATIC_FIELD_NAME + ":" + STATIC_FIELD_TYPE,
                                   "invoke-static {r0, r1}, Lunreflection_strategy_test;->useRegisters(II)V",
                                   "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
        }

        @Test
        public void staticFieldWithMoveResultAndWithoutAvailableRegistersDoesNotOptimize() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITHOUT_MOVE_RESULT_AND_NO_AVAILABLE_REGISTERS, 0, CLASS,
                            "Ljava/lang/Class;", 1, STATIC_FIELD_NAME, "Ljava/lang/String;", 2, null,
                            "Ljava/lang/Object;");
            String[] endLines = new String[] {
                    "invoke-virtual {r0, r2}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;",
                    "invoke-static {r0, r1, r2}, Lunreflection_strategy_test;->useRegisters(III)V", "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
        }

        @Test
        public void staticFieldWithMoveResultOptimizesAsExpected() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_MOVE_RESULT, 0, CLASS, "Ljava/lang/Class;", 1, STATIC_FIELD_NAME,
                            "Ljava/lang/String;", 2, null, "Ljava/lang/Object;");
            String[] endLines =
                    new String[] { "sget-object r0, " + CLASS_NAME + "->" + STATIC_FIELD_NAME + ":" + STATIC_FIELD_TYPE,
                                   "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
        }
    }

    public static class MethodUnreflectionWithImpossibleScenarios {

        private static final String[] EXPECTED_SHARED_SMALI = new String[] { "nop", "invoke-virtual {r0, r1, r2}, " +
                                                                                    "Ljava/lang/reflect/Method;->invoke" +
                                                                                    "(Ljava/lang/Object;[Ljava/lang/Object;)" +
                                                                                    "Ljava/lang/Object;",
                                                                             "invoke-static {r0, r1, r2}, " +
                                                                             "Lunreflection_strategy_test;" +
                                                                             "->useRegisters(Ljava/lang/Object;" +
                                                                             "Ljava/lang/Object;Ljava/lang/Object;)V",
                                                                             "return-void", };
        private static final java.lang.reflect.Method METHOD =
                getMethod(CLASS, "privateInstanceOneParameter", Object.class);
        private static final UnknownValue UNKNOWN_METHOD = new UnknownValue();

        @Test
        public void unknownMethodIsNotOptimized() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, UNKNOWN_METHOD, METHOD_TYPE, 1, 0, "I",
                            2, null, "Ljava/lang/Object;");

            testSmali(manipulator, EXPECTED_SHARED_SMALI);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void unknownParametersOfPublccMethodIsNotOptimized() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0, "I", 2,
                            new UnknownValue(), "[Ljava/lang/Object;");

            testSmali(manipulator, EXPECTED_SHARED_SMALI);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }

    public static class MethodUnreflectionWithInterfaceMethod {

        private static final java.lang.reflect.Method METHOD = getMethod(List.class, "isEmpty", new Class<?>[0]);

        @Test
        public void optimizesToExpectedLinesWith3LocalsAnd0Available() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1,
                            new ArrayList<String>(), "Ljava/util/ArrayList;", 2, 0, "I");
            String[] expectedLines = new String[] { "nop", "invoke-interface {r1}, Ljava/util/List;->isEmpty()Z",
                                                    "invoke-static {r0, r1, r2}, Lunreflection_strategy_test;" +
                                                    "->useRegisters(Ljava/lang/Object;Ljava/lang/Object;" +
                                                    "Ljava/lang/Object;)V", "return-void", };

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

    }

    public static class MethodUnreflectionWithPrivateInstanceMethod {

        private static final String[] EXPECTED_SMALI = new String[] { "nop", "invoke-direct {r1}, " +
                                                                             "Lunreflection_strategy_test;" +
                                                                             "->privateInstanceNoParameters()V",
                                                                      "invoke-static {r0, r1, r2}, " +
                                                                      "Lunreflection_strategy_test;->useRegisters" +
                                                                      "(Ljava/lang/Object;Ljava/lang/Object;" +
                                                                      "Ljava/lang/Object;)V", "return-void", };
        private static final java.lang.reflect.Method METHOD = getMethod(CLASS, "privateInstanceNoParameters");

        @Test
        public void optimizesToExpectedLinesWith3LocalsAnd0Available() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0, "I", 2, null,
                            "Ljava/lang/Object;");

            testSmali(manipulator, EXPECTED_SMALI);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void optimizesToExpectedLinesWith3LocalsAnd0AvailableAndUnknownTarget() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1,
                            new UnknownValue(), CLASS_NAME, 2, null, "Ljava/lang/Object;");

            testSmali(manipulator, EXPECTED_SMALI);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }
    }

    public static class MethodUnreflectionWithPrivateStaticMethod {

        private static final java.lang.reflect.Method METHOD =
                getMethod(CLASS, "privateStaticFourParameters", int.class, int.class, int.class, int.class);

        @Test
        public void optimizesToExpectedLinesWith3LocalsAnd0Available() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0, "I", 2,
                            new Object[] { 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] expectedLines = new String[] { "nop", "const/4 r3, 0x0", "aget-object r3, r2, r3",
                                                    "check-cast r3, Ljava/lang/Integer;", "const/4 r4, 0x1",
                                                    "aget-object r4, r2, r4", "check-cast r4, Ljava/lang/Integer;",
                                                    "const/4 r5, 0x2", "aget-object r5, r2, r5",
                                                    "check-cast r5, Ljava/lang/Integer;", "const/4 r6, 0x3",
                                                    "aget-object r6, r2, r6", "check-cast r6, Ljava/lang/Integer;",
                                                    "invoke-static {r3, r4, r5, r6}, Lunreflection_strategy_test;" +
                                                    "->privateStaticFourParameters(IIII)V",
                                                    "invoke-static {r0, r1, r2}, Lunreflection_strategy_test;" +
                                                    "->useRegisters(Ljava/lang/Object;Ljava/lang/Object;" +
                                                    "Ljava/lang/Object;)V", "return-void", };

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 7);
        }

        @Test
        public void optimizesToExpectedLinesWith7ContiguousAvailable() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0,
                            "I", 2, new Object[] { 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] expectedLines = new String[] { "nop", "const/4 r0, 0x0", "aget-object r0, r2, r0",
                                                    "check-cast r0, Ljava/lang/Integer;", "const/4 r1, 0x1",
                                                    "aget-object r1, r2, r1", "check-cast r1, Ljava/lang/Integer;",
                                                    "const/4 r3, 0x2", "aget-object r3, r2, r3",
                                                    "check-cast r3, Ljava/lang/Integer;", "const/4 r4, 0x3",
                                                    "aget-object r4, r2, r4", "check-cast r4, Ljava/lang/Integer;",
                                                    "invoke-static {r0, r1, r3, r4}, Lunreflection_strategy_test;" +
                                                    "->privateStaticFourParameters(IIII)V", "move-result-object r0",
                                                    "return-void", };

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }
    }

    public static class MethodUnreflectionWithPublicInstanceMethod {

        private static final String[] EXPECTED_SHARED_SMALI =
                new String[] { "nop", "move-object/16 r3, r1", "const/4 r4, 0x0", "aget-object r4, r2, r4",
                               "check-cast r4, Ljava/lang/Integer;", "const/4 r5, 0x1", "aget-object r5, r2, r5",
                               "check-cast r5, Ljava/lang/Integer;", "const/4 r6, 0x2", "aget-object r6, r2, r6",
                               "check-cast r6, Ljava/lang/Integer;", "const/4 r7, 0x3", "aget-object r7, r2, r7",
                               "check-cast r7, Ljava/lang/Integer;", "const/4 r8, 0x4", "aget-object r8, r2, r8",
                               "check-cast r8, Ljava/lang/Integer;", "const/4 r9, 0x5", "aget-object r9, r2, r9",
                               "check-cast r9, Ljava/lang/Integer;",
                               "invoke-virtual/range {r3 .. r9}, Lunreflection_strategy_test;" +
                               "->publicInstanceSixParameters(IIIIII)V", };

        private static final java.lang.reflect.Method METHOD = getMethod(CLASS, "publicInstanceSixParameters",
                new Class<?>[] { int.class, int.class, int.class, int.class, int.class, int.class, });

        @Test
        public void optimizesToExpectedLinesWith3LocalsAnd0Available() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0, "I", 2,
                            new Object[] { 6, 5, 4, 3, 2, 1 }, "[Ljava/lang/Object;");
            String[] endLines = new String[] {
                    "invoke-static {r0, r1, r2}, Lunreflection_strategy_test;->useRegisters(Ljava/lang/Object;" +
                    "Ljava/lang/Object;Ljava/lang/Object;)V", "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 10);
        }

        @Test
        public void optimizesToExpectedLinesWith7ContiguousAvailable() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0,
                            "I", 2, new Object[] { 6, 5, 4, 3, 2, 1 }, "Ljava/lang/Object;");
            String[] endLines = new String[] { "move-result-object r0", "return-void", };
            String[] expectedLines = ObjectArrays.concat(EXPECTED_SHARED_SMALI, endLines, String.class);

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }

    }

    public static class MethodUnreflectionWithZeroParameters {

        private static final java.lang.reflect.Method METHOD = getMethod(System.class, "gc", new Class<?>[0]);

        @Test
        public void methodWithNoParametersOptimizesEvenWithNoRegistersAvailable() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0, "I", 2, 0,
                            "I");
            String[] expectedLines = new String[] { "nop", "invoke-static {}, Ljava/lang/System;->gc()V",
                                                    "invoke-static {r0, r1, r2}, Lunreflection_strategy_test;" +
                                                    "->useRegisters(Ljava/lang/Object;Ljava/lang/Object;" +
                                                    "Ljava/lang/Object;)V", "return-void", };

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_3_LOCALS_AND_0_AVAILABLE, 3);
        }

        @Test
        public void methodWithNoParametersOptimizesWithManyRegistersAvailable() {
            ExecutionGraphManipulator manipulator =
                    getOptimizedGraph(METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 0, METHOD, METHOD_TYPE, 1, 0,
                            "I", 2, null, "Ljava/lang/Object;");
            String[] expectedLines =
                    new String[] { "nop", "invoke-static {}, Ljava/lang/System;->gc()V", "move-result-object r0",
                                   "return-void", };

            testSmali(manipulator, expectedLines);
            testRegisterCount(manipulator, METHOD_WITH_10_LOCALS_AND_7_CONTIGUOUS_AVAILABLE, 10);
        }
    }

    public static class ProtectedMethods {

        @Test
        public void instanceGetOpcodes() {
            boolean isStatic = false;
            assertEquals(Opcode.IGET, UnreflectionStrategy.getGetOpcode("I", isStatic));
            assertEquals(Opcode.IGET_BOOLEAN, UnreflectionStrategy.getGetOpcode("Z", isStatic));
            assertEquals(Opcode.IGET_BYTE, UnreflectionStrategy.getGetOpcode("B", isStatic));
            assertEquals(Opcode.IGET_CHAR, UnreflectionStrategy.getGetOpcode("C", isStatic));
            assertEquals(Opcode.IGET_OBJECT, UnreflectionStrategy.getGetOpcode("Ljava/lang/Object;", isStatic));
            assertEquals(Opcode.IGET_SHORT, UnreflectionStrategy.getGetOpcode("S", isStatic));
            assertEquals(Opcode.IGET_WIDE, UnreflectionStrategy.getGetOpcode("J", isStatic));
            assertEquals(Opcode.IGET_WIDE, UnreflectionStrategy.getGetOpcode("D", isStatic));
        }

        @Test
        public void staticGetOpcodes() {
            boolean isStatic = true;
            assertEquals(Opcode.SGET, UnreflectionStrategy.getGetOpcode("I", isStatic));
            assertEquals(Opcode.SGET_BOOLEAN, UnreflectionStrategy.getGetOpcode("Z", isStatic));
            assertEquals(Opcode.SGET_BYTE, UnreflectionStrategy.getGetOpcode("B", isStatic));
            assertEquals(Opcode.SGET_CHAR, UnreflectionStrategy.getGetOpcode("C", isStatic));
            assertEquals(Opcode.SGET_OBJECT, UnreflectionStrategy.getGetOpcode("Ljava/lang/Object;", isStatic));
            assertEquals(Opcode.SGET_SHORT, UnreflectionStrategy.getGetOpcode("S", isStatic));
            assertEquals(Opcode.SGET_WIDE, UnreflectionStrategy.getGetOpcode("J", isStatic));
            assertEquals(Opcode.SGET_WIDE, UnreflectionStrategy.getGetOpcode("D", isStatic));
        }

    }

}
