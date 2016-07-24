package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SideEffect.Level;

import java.util.HashSet;
import java.util.Set;

public abstract class EmulatedMethod {

    protected final Set<Throwable> exceptions;
    protected SideEffect.Level level;

    EmulatedMethod() {
        exceptions = new HashSet<>();
        level = SideEffect.Level.NONE;
    }

    public Level getSideEffectLevel() {
        return level;
    }

    protected void setSideEffectLevel(Level level) {
        this.level = level;
    }

    public Set<Throwable> getExceptions() {
        return exceptions;
    }

    protected void setException(Throwable exception) {
        exceptions.add(exception);
    }

}
