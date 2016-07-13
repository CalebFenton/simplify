package org.cf.smalivm.context;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ExecutionGraphTest {

    private static final String CLASS_NAME = "Lexecution_graph;";

    @Test
    public void hasExpectedTerminatingAddresses() {
        VirtualMachine vm = VMTester.spawnVM();
        String methodDescriptor = "terminatingAddresses()V";
        ExecutionGraph graph = vm.spawnInstructionGraph(CLASS_NAME, methodDescriptor);

        int[] expected = new int[]{6, 7, 8, 9, 10, 12};
        int[] actual = graph.getTerminatingAddresses();

        assertArrayEquals(expected, actual);
    }

}
