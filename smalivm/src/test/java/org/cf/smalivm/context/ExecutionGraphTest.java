package org.cf.smalivm.context;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class ExecutionGraphTest {

    private static final String CLASS_NAME = "Lexecution_graph;";

    private VirtualMachine vm;
    private VirtualClass virtualClass;

    @Before
    public void setUp() {
        vm = VMTester.spawnVM();
        virtualClass = vm.getClassManager().getVirtualClass(CLASS_NAME);
    }

    @Test
    public void hasExpectedTerminatingAddresses() {
        String methodDescriptor = "terminatingAddresses()V";
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        ExecutionGraph graph = vm.spawnInstructionGraph(method);

        int[] expected = new int[] { 6, 7, 8, 9, 10, 12 };
        int[] actual = graph.getTerminatingAddresses();

        assertArrayEquals(expected, actual);
    }

}
