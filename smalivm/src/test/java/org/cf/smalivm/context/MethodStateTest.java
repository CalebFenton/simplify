package org.cf.smalivm.context;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cf.smalivm.VMTester;
import org.junit.jupiter.api.Test;

public class MethodStateTest {

    private static final String CLASS_NAME = "Lsimple_loop;";

    @Test
    public void hasExpectedToString() {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "simpleLoop()V");

        ExecutionNode node = graph.getRoot();
        MethodState state = node.getContext().getMethodState();
        String expected = "params: 0, locals: 2\nv0: type=I, value=0";

        assertEquals(expected, state.toString());
    }

    @Test
    public void toStringHandlesNullRegisterValuesWhenPeekingAllRegisters() {
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "simpleLoop()V");

        ExecutionNode node = graph.getRoot();
        MethodState state = node.getContext().getMethodState();
        String expected = "params: 0, locals: 2\nv0: type=I, value=0\nv1: null";

        assertEquals(expected, state.toString(false));
    }

}
