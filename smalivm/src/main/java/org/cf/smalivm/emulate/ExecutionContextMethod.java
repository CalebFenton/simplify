package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;

public interface ExecutionContextMethod extends EmulatedMethod {

    void execute(VirtualMachine vm, ExecutionContext context);

}
