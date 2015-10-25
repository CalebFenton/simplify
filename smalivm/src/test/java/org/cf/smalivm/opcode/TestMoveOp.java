package org.cf.smalivm.opcode;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import gnu.trove.map.TIntObjectMap;

import java.util.Arrays;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Test;

public class TestMoveOp {

    private static final String CLASS_NAME = "Lmove_test;";

    @Test
    public void testMoveException() {
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, new UnknownValue(), "Ljava/lang/Exception;");

        VMTester.testMethodState(CLASS_NAME, "TestMoveException()V", expected);
    }

    @Test
    public void testMoveRegisterObject() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, new Object(), "Ljava/lang/Object;");

        // Must invoke VM directly to ensure reference identity
        ExecutionGraph graph = VMTester.execute(CLASS_NAME, "TestMoveRegisterObject()V", initial);
        int[] addresses = graph.getConnectedTerminatingAddresses();
        assertTrue("Should terminate when expected: " + addresses + " == {1}",
                        Arrays.equals(addresses, new int[] { 1 }));

        HeapItem register0 = graph.getRegisterConsensus(1, 0);
        HeapItem register1 = graph.getRegisterConsensus(1, 1);

        assertSame(register0, register1);
        assertTrue(register0 instanceof Object);
    }

    @Test
    public void testMoveRegisterPrimitive() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(0, 42, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 42, "I", 1, 42, "I");

        VMTester.testMethodState(CLASS_NAME, "TestMoveRegisterPrimitive()V", initial, expected);
    }

    @Test
    public void testMoveResult() {
        TIntObjectMap<HeapItem> initial = VMTester.buildRegisterState(MethodState.ResultRegister, 42, "I");
        TIntObjectMap<HeapItem> expected = VMTester.buildRegisterState(0, 42, "I");

        VMTester.testMethodState(CLASS_NAME, "TestMoveResult()V", initial, expected);
    }

}
