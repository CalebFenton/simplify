package org.cf.demoapp;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.MethodStateMethod;
import org.cf.smalivm.emulate.UnknownValuesMethod;

public class java_io_PrintStream_println implements MethodStateMethod, UnknownValuesMethod {

    @Override
    public void execute(VirtualMachine vm, MethodState mState) {
        // Virtual method, register 0 is System.out (or something else)
        HeapItem item = mState.peekParameter(1);
        Object value = item.getValue();
        String valueStr = (String) value;

        // Actually print out any println's executed.
        System.out.println(valueStr);
    }

    public SideEffect.Level getSideEffectLevel() {
        // Do not optimize this away.
        return SideEffect.Level.STRONG;
    }

    @Override
    public Set<VirtualException> getExceptions() {
        return new HashSet<VirtualException>();
    }

}
