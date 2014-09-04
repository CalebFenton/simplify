package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect.Type;
import org.cf.smalivm.context.MethodContext;

public interface EmulatedMethod {

    public void execute(MethodContext mctx);

    public Type getSideEffectType();

}
