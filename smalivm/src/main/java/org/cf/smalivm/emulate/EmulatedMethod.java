package org.cf.smalivm.emulate;

import java.util.Set;

import org.cf.smalivm.SideEffect.Level;
import org.cf.smalivm.VirtualException;

public interface EmulatedMethod {

    Level getSideEffectLevel();

    Set<VirtualException> getExceptions();

}
