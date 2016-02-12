package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.type.UninitializedInstance;
import org.junit.Before;
import org.junit.Test;

public class NewInstanceOpTest {

    private static final String CLASS_NAME = "Lnew_instance_test;";
    private static final String CLASS_NAME_BINARY = "new_instance_test";

    private VMState expected;
    private VMState initial;

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

    @Test
    public void canCreateLocalInstance() throws ClassNotFoundException {
        initial.setRegisters(0, 1, "I");
        expected.setRegisters(0, new UninitializedInstance(CLASS_NAME), CLASS_NAME);

        VMTester.test(CLASS_NAME, "newLocalInstance()V", initial, expected);
    }

    @Test
    public void canCreateNonLocalInstance() {
        initial.setRegisters(0, 1, "I");
        expected.setRegisters(0, new UninitializedInstance("Ljava/lang/Integer;"), "Ljava/lang/Integer;");

        VMTester.test(CLASS_NAME, "newNonLocalInstance()V", initial, expected);
    }

}
