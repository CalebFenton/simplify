//package org.cf.smalivm.opcode;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import org.cf.smalivm.VMState;
//import org.cf.smalivm.VMTester;
//import org.cf.smalivm.context.ExecutionGraph;
//import org.cf.smalivm.context.HeapItem;
//import org.cf.smalivm.context.MethodState;
//import org.cf.util.ClassNameUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class ThrowOpTest {
//
//    private static final String CLASS_NAME = "Lthrow_test;";
//
//    private VMState expected;
//    private VMState initial;
//
//    @BeforeEach
//    public void setUp() {
//        expected = new VMState();
//        initial = new VMState();
//    }
//
//    @Test
//    public void canThrowNullPointerException() {
//        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "throwNullPointerException()V", initial);
//
//        Class<?> exceptionClass = NullPointerException.class;
//        HeapItem item = graph.getTerminatingRegisterConsensus(0);
//        assertEquals(exceptionClass, item.getValue().getClass());
//        assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
//
//        HeapItem throwItem = graph.getTerminatingRegisterConsensus(MethodState.ThrowRegister);
//        assertEquals(item, throwItem);
//
//        VMTester.test(CLASS_NAME, "throwNullPointerException()V", expected);
//    }
//
//}
