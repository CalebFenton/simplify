package org.cf.smalivm;

import org.junit.Before;
import org.junit.Test;

public class TestHashCode {

    private static final String CLASS_NAME = "Lhash_code;";

    private VirtualMachine vm;

    @Before
    public void setupVM() throws Exception {
        vm = VMTester.spawnVM();
    }

    @Test
    public void canCallHashCodeOfDynamicallyGeneratedClass() throws VirtualMachineException {
        String methodName = "createInstance()V";
        vm.execute(CLASS_NAME, methodName);
    }

}
