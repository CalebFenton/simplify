package org.cf.smalivm.opcode;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;

public abstract class Op {

    private final MethodLocation location;
    private final MethodLocation[] children;
    private final Set<VirtualException> exceptions;

    Op(MethodLocation location, MethodLocation child) {
        this(location, new MethodLocation[] { child });
    }

    Op(MethodLocation location, MethodLocation[] children) {
        this.location = location;
        this.children = children;
        exceptions = new HashSet<VirtualException>();
    }

    public final int getAddress() {
        return location.getCodeAddress();
    }

    public final MethodLocation[] getChildren() {
        return children;
    }

    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    public final BuilderInstruction getInstruction() {
        return (BuilderInstruction) location.getInstruction();
    }

    public final MethodLocation getLocation() {
        return location;
    }

    public final String getName() {
        return getInstruction().getOpcode().name;
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

    @Override
    public abstract String toString();

    void addException(VirtualException exception) {
        exceptions.add(exception);
    }

}
