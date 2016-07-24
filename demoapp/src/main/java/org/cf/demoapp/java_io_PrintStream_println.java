package org.cf.demoapp;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodStateMethod;
import org.cf.smalivm.emulate.UnknownValuesMethod;
import org.cf.smalivm.opcode.Op;

class java_io_PrintStream_println extends MethodStateMethod implements UnknownValuesMethod {

    java_io_PrintStream_println() {
        super();
        level = SideEffect.Level.STRONG;
    }

    @Override
    protected void execute(VirtualMachine vm, Op op, MethodState mState) {
        // Virtual method, register 0 is System.out (or something else)
        HeapItem item = mState.peekParameter(1);
        Object value = item.getValue();
        String valueStr = (String) value;

        // Actually print out any println's executed.
        System.out.println(valueStr);
    }

}
