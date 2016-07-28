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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ThrowOpTest {

    private static final String CLASS_NAME = "Lthrow_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void canThrowNullPointerException() {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "throwNullPointerException()V", initial);

        Class<?> exceptionClass = NullPointerException.class;
        HeapItem item = graph.getTerminatingRegisterConsensus(0);
        Assert.assertEquals(exceptionClass, item.getValue().getClass());
        Assert.assertEquals(ClassNameUtils.toInternal(exceptionClass), item.getType());

        HeapItem throwItem = graph.getTerminatingRegisterConsensus(MethodState.ThrowRegister);
        assertEquals(item, throwItem);

        VMTester.test(CLASS_NAME, "throwNullPointerException()V", expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
