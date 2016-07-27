package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.ClassNameUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;

@RunWith(Enclosed.class)
public class CheckCastOpTest {

    private static final String CLASS_NAME = "Lcheck_cast_test;";

    private VMState expected;
    private VMState initial;

    private static void testException(String methodDescriptor, Class<?> exceptionClass, String exceptionMessage,
                                      VMState initial) {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, methodDescriptor, initial);

        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(exceptionClass, item.getValue().getClass());
        Assert.assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());
        Assert.assertEquals(exceptionMessage, ((Throwable) item.getValue()).getMessage());

        assertFalse("Should not reach next instruction in non-exception execution path", graph.wasAddressReached(1));

        MethodState mState = graph.getNodePile(0).get(0).getContext().getMethodState();
        Assert.assertEquals(0, mState.getRegistersAssigned().length);
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
        expected = initial;

        VMTester.test(CLASS_NAME, "castToObject()V", initial, expected);
    }

    @Test
    public void objectNotOfCastTypeThrowsClassCastException() {
        initial.setRegisters(0, 0x42, "Ljava/lang/Integer;");
        testException("castToStringWithCatch()V", ClassCastException.class,
                "java.lang.Integer cannot be cast to java.lang.String", initial);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
