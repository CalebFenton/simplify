package org.cf.smalivm.opcode;

import java.util.HashSet;
import java.util.Set;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualException;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.MethodLocation;

public abstract class Op {

    private final Set<VirtualException> exceptions;

    private BuilderInstruction instruction;
    private BuilderInstruction[] children;

    Op(BuilderInstruction instruction, BuilderInstruction child) {
        this(instruction, new BuilderInstruction[] { child });
    }

    Op(BuilderInstruction instruction, BuilderInstruction[] children) {
        this.instruction = instruction;
        this.children = children;
        exceptions = new HashSet<VirtualException>();
    }

    public final int getAddress() {
        return instruction.getLocation().getCodeAddress();
    }

    public final BuilderInstruction[] getChildren() {
        return children;
    }

    public Set<VirtualException> getExceptions() {
        return exceptions;
    }

    public final BuilderInstruction getInstruction() {
        return instruction;
    }

    public final MethodLocation getLocation() {
        return instruction.getLocation();
    }

    public final String getName() {
        return instruction.getOpcode().name;
    }

    public void setChildren(BuilderInstruction... children) {
        this.children = children;
    }

    public void setInstruction(BuilderInstruction instruction) {
        this.instruction = instruction;
    }

    public SideEffect.Level sideEffectLevel() {
        return SideEffect.Level.NONE;
    }

    @Override
    public abstract String toString();

    void addException(VirtualException exception) {
        exceptions.add(exception);
    }

}
