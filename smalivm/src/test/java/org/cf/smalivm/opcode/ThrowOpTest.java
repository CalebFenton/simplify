package org.cf.smalivm.opcode;

import org.cf.smalivm.VMState;
import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.context.MethodState;
import org.junit.Before;
import org.junit.Test;

public class ThrowOpTest {

    private static final String CLASS_NAME = "Lthrow_test;";

    private VMState expected;
    private VMState initial;

    @Test
    public void canThrowNullPointerException() {
        String exceptionClass = "Ljava/lang/NullPointerException;";
        VirtualException exception = new VirtualException(exceptionClass, null);
        expected.setRegisters(MethodState.ThrowRegister, exception, exceptionClass, 0, exception, exceptionClass);

        VMTester.test(CLASS_NAME, "throwNullPointerException()V", expected);
    }

    @Before
    public void setUp() {
        expected = new VMState();
        initial = new VMState();
    }

}
