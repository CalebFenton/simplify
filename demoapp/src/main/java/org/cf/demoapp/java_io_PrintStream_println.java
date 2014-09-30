package org.cf.demoapp;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.emulate.EmulatedMethod;

public class java_io_PrintStream_println implements EmulatedMethod {

    @Override
    public void execute(MethodState mState) {
        // Virtual method, register 0 is System.out (or something else)
        Object value = mState.peekParameter(1);
        String valueStr = (String) value;

        // Actually print out any println's executed.
        System.out.println(valueStr);
    }

    public SideEffect.Level getSideEffectType() {
        // Do not optimize this away.
        return SideEffect.Level.STRONG;
    }

}
