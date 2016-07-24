package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.opcode.Op;

public abstract class ExecutionContextMethod extends EmulatedMethod {

    protected abstract void execute(VirtualMachine vm, Op op, ExecutionContext context);

}
