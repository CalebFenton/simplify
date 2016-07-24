package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.opcode.Op;

public abstract class MethodStateMethod extends EmulatedMethod {

    protected abstract void execute(VirtualMachine vm, Op op, MethodState mState);

}
