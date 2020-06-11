package org.cf.demoapp;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodStateMethod;
import org.cf.smalivm.emulate.UnknownValuesMethod;
import org.cf.smalivm.opcode.Op;

public class java_io_PrintStream_println extends MethodStateMethod implements UnknownValuesMethod {
    public java_io_PrintStream_println() {
        super();
        // Writing bytes over any kind of IO affects state outside of the VM.
        // Set the side effect level so the optimizer knows not to remove this method call.
        level = SideEffect.Level.STRONG;
    }

    @Override
    protected void execute(VirtualMachine vm, Op op, MethodState mState) {
        // This is a virtual method, so register 0 contains a reference to an instance of Ljava/io/PrintStream;
        // Register 1 should have the string to print.
        HeapItem item = mState.peekParameter(1);
        String value = (String) item.getValue();
        System.out.println(value);
    }
}
