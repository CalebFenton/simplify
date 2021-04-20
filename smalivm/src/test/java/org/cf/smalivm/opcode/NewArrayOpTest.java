//package org.cf.smalivm.opcode;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//import java.lang.reflect.Array;
//import org.cf.smalivm.VMState;
//import org.cf.smalivm.VMTester;
//import org.cf.smalivm.context.ExecutionGraph;
//import org.cf.smalivm.context.HeapItem;
//import org.cf.smalivm.context.MethodState;
//import org.cf.smalivm.type.UnknownValue;
//import org.cf.util.ClassNameUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class NewArrayOpTest {
//
//    private static final String CLASS_NAME = "Lnew_array_test;";
//
//    private VMState expected;
//    private VMState initial;
//
//    private static void testException(String methodDescriptor, Class<?> exceptionClass, String exceptionMessage,
//                                      VMState initial) {
//        ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial);
//
//        HeapItem item = graph.getTerminatingRegisterConsensus(0);
//        assertEquals(exceptionClass, item.getValue().getClass());
//        assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
//        assertEquals(exceptionMessage, ((Throwable) item.getValue()).getMessage());
//
//        assertFalse(graph.wasAddressReached(1), "Should not reach next instruction in non-exception execution path");
//
//        MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
//        assertEquals(0, mState.getRegistersAssigned().length);
//    }
//
//    @Test
//    public void canCreate2DIntegerArray() {
//        int length = 3;
//        initial.setRegisters(0, length, "I");
//        expected.setRegisters(0, new int[length][], "[[I");
//
//        VMTester.test(CLASS_NAME, "create2DIntegerArray()V", initial, expected);
//    }
//
//    @Test
//    public void canCreate2DLocalInstanceArray() throws ClassNotFoundException {
//        int length = 5;
//        initial.setRegisters(0, length, "I");
//        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "create2DLocalInstanceArray()V", initial);
//        HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
//
//        assertEquals("[[" + CLASS_NAME, consensus.getType());
//        assertEquals(length, Array.getLength(consensus.getValue()));
//        Class<?> actualClass = consensus.getValue().getClass();
//        assertEquals("[[" + CLASS_NAME, actualClass.getName());
//    }
//
//    @Test
//    public void canCreateIntegerArray() {
//        int length = 1;
//        initial.setRegisters(0, length, "I");
//        expected.setRegisters(0, new int[length], "[I");
//
//        VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
//    }
//
//    @Test
//    public void canCreateIntegerArrayWithShortTypeLengthValue() {
//        Short length = 1;
//        initial.setRegisters(0, length, "S");
//        expected.setRegisters(0, new int[length.intValue()], "[I");
//
//        VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
//    }
//
//    @Test
//    public void canCreateIntegerArrayWithUnkonwnLengthValue() {
//        Object length = new UnknownValue();
//        initial.setRegisters(0, length, "I");
//        expected.setRegisters(0, length, "[I");
//
//        VMTester.test(CLASS_NAME, "createIntegerArray()V", initial, expected);
//    }
//
//    @Test
//    public void canCreateLocalInstanceArray() {
//        int length = 1;
//        initial.setRegisters(0, length, "I");
//        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "createLocalInstanceArray()V", initial);
//        HeapItem consensus = graph.getTerminatingRegisterConsensus(0);
//
//        assertEquals("[" + CLASS_NAME, consensus.getType());
//        assertEquals(length, Array.getLength(consensus.getValue()));
//        Class<?> actualClass = consensus.getValue().getClass();
//        assertEquals("[" + CLASS_NAME, actualClass.getName());
//    }
//
//    @Test
//    public void nonExistentClassNameThrowsException() {
//        initial.setRegisters(0, 1, "I");
//        testException("createNonExistentArrayClass()V", ClassNotFoundException.class, "does.not.exist", initial);
//    }
//
//    @BeforeEach
//    public void setUp() {
//        expected = new VMState();
//        initial = new VMState();
//    }
//
//}
