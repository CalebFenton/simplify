package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;

public interface ExecutionContextMethod extends EmulatedMethod {

    public void execute(VirtualMachine vm, ExecutionContext ectx) throws Exception;

}
