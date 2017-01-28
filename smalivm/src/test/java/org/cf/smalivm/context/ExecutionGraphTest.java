package org.cf.smalivm.context;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ExecutionGraphTest {

    private static final String CLASS_NAME = "Lexecution_graph;";

    private VirtualMachine vm;
    private VirtualClass virtualClass;
    private VMState initial;

    @Before
    public void setUp() {
        vm = VMTester.spawnVM();
        virtualClass = vm.getClassManager().getVirtualClass(CLASS_NAME);
        initial = new VMState();
    }

    @Test
    public void hasExpectedTerminatingAddresses() {
        String methodDescriptor = "terminatingAddresses()V";
        VirtualMethod method = virtualClass.getMethod(methodDescriptor);
        ExecutionGraph graph = vm.spawnInstructionGraph(method);

        int[] expected = new int[]{ 6, 7, 8, 9, 10, 12 };
        int[] actual = graph.getTerminatingAddresses();

        assertArrayEquals(expected, actual);
    }

    @Test
    public void determinesCorrectConsensusTypeForTypesInSameHierarchy() {
        String methodDescriptor = "returnsObjectOrString()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");

        ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);

        assertEquals(CommonTypes.OBJECT, item.getType());
    }

    @Test
    public void determinesCorrectConsensusTypeForTypesInSameHierarchyWithNull() {
        String methodDescriptor = "returnsObjectOrStringOrNull()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");

        ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);

        assertEquals(CommonTypes.OBJECT, item.getType());
    }

    @Test
    public void determinesUnknownConsensusTypeForAmbiguousTypes() {
        String methodDescriptor = "returnsStringOrInteger()Ljava/lang/Object;";
        initial.setRegister(0, new UnknownValue(), "I");

        ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, methodDescriptor, initial);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);

        assertEquals(CommonTypes.UNKNOWN, item.getType());
    }

}
