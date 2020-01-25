package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.ClassNameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CheckCastOpTest {

    private static final String CLASS_NAME = "Lcheck_cast_test;";

    private VMState expected;
    private VMState initial;

    private static void testException(String methodDescriptor, Class<?> exceptionClass, String exceptionMessage,
                                      VMState initial) {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial);

        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        assertEquals(exceptionClass, item.getValue().getClass());
        assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
        assertEquals(exceptionMessage, ((Throwable) item.getValue()).getMessage());

        assertFalse(graph.wasAddressReached(1), "Should not reach next instruction in non-exception execution path");

        MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
        assertEquals(0, mState.getRegistersAssigned().length);
    }

    @Test
    public void canCastNullWithObjectTypeToString() {
        initial.setRegisters(0, null, "Ljava/lang/Object;");
        expected.setRegisters(0, null, "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "castToString()V", initial, expected);
    }

    @Test
    public void canCastStringWithObjectTypeToString() {
        initial.setRegisters(0, "great maker", "Ljava/lang/Object;");
        expected.setRegisters(0, "great maker", "Ljava/lang/String;");

        VMTester.test(CLASS_NAME, "castToString()V", initial, expected);
    }

    @Test
    public void canCastStringWithStringTypeToObject() {
        initial.setRegisters(0, "great maker", "Ljava/lang/String;");
        expected.setRegisters(0, "great maker", "Ljava/lang/Object;");

        VMTester.test(CLASS_NAME, "castToObject()V", initial, expected);
    }

    @Test
    public void objectNotOfCastTypeThrowsClassCastException() {
        initial.setRegisters(0, 0x42, "Ljava/lang/Integer;");
        testException("castToStringWithCatch()V", ClassCastException.class,
                "java.lang.Integer cannot be cast to java.lang.String", initial);
    }

    @BeforeEach
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
