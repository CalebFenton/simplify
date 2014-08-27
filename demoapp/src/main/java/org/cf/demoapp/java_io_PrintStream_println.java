package org.cf.demoapp;

import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.emulate.EmulatedMethod;

public class java_io_PrintStream_println implements EmulatedMethod {

    @Override
    public void execute(MethodContext mctx) {
        // Virtual method, register 0 is System.out (or something else)
        Object value = mctx.getParameter(1);
        String valueStr = (String) value;

        // Actually print out any println's executed.
        System.out.println(valueStr);
    }

}
