package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.SideEffect.Level;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm2.ExecutionState;
import org.cf.smalivm2.VirtualMachine2;

import java.util.HashSet;
import java.util.Set;

import kotlin.Pair;

public abstract class EmulatedMethod {

    protected final Set<Pair<Class<?>, String>> exceptions;
    protected SideEffect.Level level;

    EmulatedMethod() {
        exceptions = new HashSet<>();
        level = SideEffect.Level.NONE;
    }

    protected abstract void execute(VirtualMachine2 vm, Op op, ExecutionState state);

    public Level getSideEffectLevel() {
        return level;
    }

    protected void setSideEffectLevel(Level level) {
        this.level = level;
    }

    public Set<Pair<Class<?>, String>> getExceptions() {
        return exceptions;
    }

    void setException(Class<?> klazz, String className) {
        exceptions.add(new Pair(klazz, className));
    }
}
