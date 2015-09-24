package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.jf.dexlib2.builder.BuilderInstruction;

public abstract class ExecutionContextOp extends Op {

    ExecutionContextOp(BuilderInstruction instruction, BuilderInstruction child) {
        super(instruction, child);
    }

    ExecutionContextOp(BuilderInstruction instruction, BuilderInstruction[] children) {
        super(instruction, children);
    }

    public abstract void execute(ExecutionNode executionNode, ExecutionContext ectx);

}
