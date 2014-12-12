package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect.Level;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;

public interface EmulatedMethod {

    public void execute(VirtualMachine vm, MethodState mState) throws Exception;

    public Level getSideEffectLevel();

}
