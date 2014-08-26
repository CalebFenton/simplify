package org.cf.smalivm.emulate;

import org.cf.smalivm.context.MethodContext;

public interface EmulatedMethod {

    public void execute(MethodContext mctx, int[] parameterRegisters);

}
