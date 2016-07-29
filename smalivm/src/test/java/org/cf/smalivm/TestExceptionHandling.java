package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.Instance;
import org.cf.smalivm.type.UninitializedInstance;
import org.cf.smalivm.type.UnknownValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestExceptionHandling {

    private static final String CLASS_NAME = "Lorg/cf/test/ExceptionalCode;";
    private static final String EXCEPTION_CLASS_NAME = "Lorg/cf/test/CustomException;";
    private VirtualMachine vm;

    @Before
    public void setupVM() {
        vm = VMTester.spawnVM();
    }

    @Test
    public void unsafeExceptionIsNotInstantiated() throws VirtualMachineException {
        String methodName = "createAndThrowException()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);

        assertEquals(EXCEPTION_CLASS_NAME, item.getType());
        assertEquals(UninitializedInstance.class, item.getValue().getClass());
        Instance instance = (Instance) item.getValue();
        assertEquals(EXCEPTION_CLASS_NAME, instance.getType().getName());
    }

    @Test
    public void unsafeExceptionIsNotThrown() throws VirtualMachineException {
        String methodName = "callsExceptionalMethod()V";
        ExecutionGraph graph = vm.execute(CLASS_NAME + "->" + methodName);
        HeapItem item = graph.getTerminatingRegisterConsensus(0);

        assertNull(item);
//        assertEquals(EXCEPTION_CLASS_NAME, item.getType());
//        assertEquals(UninitializedInstance.class, item.getValue().getClass());
//        Instance instance = (Instance) item.getValue();
//        assertEquals(EXCEPTION_CLASS_NAME, instance.getType().getName());

//        System.out.println(Arrays.toString(graph.getAddresses()));
//        for ( int address : graph.getAddresses() ) {
//            System.out.println("visited " + address + " ? " + graph.wasAddressReached(address));
//        }
    }

}
