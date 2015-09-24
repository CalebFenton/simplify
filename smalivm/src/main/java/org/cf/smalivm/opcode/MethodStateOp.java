package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;

public abstract class MethodStateOp extends Op {

    MethodStateOp(BuilderInstruction instruction) {
        super(instruction, new BuilderInstruction[0]);
    }

    MethodStateOp(BuilderInstruction instruction, BuilderInstruction child) {
        super(instruction, child);
    }

    MethodStateOp(BuilderInstruction instruction, BuilderInstruction[] children) {
        super(instruction, children);
    }

    public abstract void execute(ExecutionNode executionNode, MethodState mState);

}
