package org.cf.simplify.strategy;

import static org.junit.Assert.assertEquals;
import gnu.trove.map.TIntObjectMap;

import org.cf.simplify.MethodBackedGraph;
import org.cf.simplify.OptimizerTester;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Enclosed.class)
public class TestPeepholeStrategy {

    private static final String CLASS_NAME = "Lpeephole_strategy_test;";

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TestPeepholeStrategy.class.getSimpleName());

    private static MethodBackedGraph getOptimizedGraph(String methodName, Object... args) {
        TIntObjectMap<Object> initial = VMTester.buildRegisterState(args);
        MethodBackedGraph mbgraph = OptimizerTester.getMethodBackedGraph(CLASS_NAME, methodName, initial);
        PeepholeStrategy strategy = new PeepholeStrategy(mbgraph);
        strategy.perform();

        return mbgraph;
    }

    public static class TestPeepClassForName {
        private static final int ADDRESS = 0;
        private static final String METHOD_NAME = "ClassForName()V";

        private void testForExpectedInstruction(Object register0, String expectedClassName) {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_NAME, 0, register0);

            BuilderInstruction21c instruction = (BuilderInstruction21c) mbgraph.getInstruction(ADDRESS);
            assertEquals(Opcode.CONST_CLASS, instruction.getOpcode());
            assertEquals(0, instruction.getRegisterA());

            String actualClassName = ReferenceUtil.getReferenceString(instruction.getReference());
            assertEquals(expectedClassName, actualClassName);
        }

        @Test
        public void testInvokeClassForNameForImaginaryClassIsReplaced() {
            testForExpectedInstruction("com.funky.unknown.class", "Lcom/funky/unknown/class;");
        }

        @Test
        public void testInvokeClassForNameForKnownClassIsReplaced() {
            testForExpectedInstruction("java.lang.String", "Ljava/lang/String;");
        }

        @Test
        public void testInvokeClassForNameForLocalClassIsReplaced() {
            testForExpectedInstruction("peephole_strategy_test", "Lpeephole_strategy_test;");
        }

        @Test
        public void testInvokeClassForNameForUnknownValueIsNotReplaced() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_NAME, 0, new UnknownValue("Ljava/lang/String;"));
            Instruction35c instruction = (Instruction35c) mbgraph.getInstruction(ADDRESS);
            String methodDescriptor = ReferenceUtil.getMethodDescriptor((MethodReference) instruction.getReference());

            assertEquals("Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;", methodDescriptor);
        }
    }

    public static class TestStringInit {
        private static final String ZENSUNNI_POEM = "Sand keeps the skin clean, and the mind.";
        private static final String METHOD_NAME = "StringInit()V";
        private static final int ADDRESS = 0;

        private void testForExpectedInstruction(Object register1, String expectedConstant) {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_NAME, 0, new UninitializedInstance(
                            "Ljava/lang/String;"), 1, register1);

            BuilderInstruction21c instruction = (BuilderInstruction21c) mbgraph.getInstruction(ADDRESS);
            assertEquals(Opcode.CONST_STRING, instruction.getOpcode());
            assertEquals(0, instruction.getRegisterA());

            String actualConstant = ((StringReference) instruction.getReference()).getString();
            assertEquals(expectedConstant, actualConstant);
        }

        @Test
        public void testStringInitWithKnownStringIsReplaced() {
            testForExpectedInstruction(ZENSUNNI_POEM.getBytes(), ZENSUNNI_POEM);
        }

        @Test
        public void testStringInitWithUnknownValueIsNotReplaced() {
            MethodBackedGraph mbgraph = getOptimizedGraph(METHOD_NAME, 0, new UnknownValue("[B"));
            Instruction35c instruction = (Instruction35c) mbgraph.getInstruction(ADDRESS);
            String methodDescriptor = ReferenceUtil.getMethodDescriptor((MethodReference) instruction.getReference());

            assertEquals("Ljava/lang/String;-><init>([B)V", methodDescriptor);
        }
    }

}
