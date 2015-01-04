package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect.Level;

public interface EmulatedMethod {

    public Level getSideEffectLevel();

}
