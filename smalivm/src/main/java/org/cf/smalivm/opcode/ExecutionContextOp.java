package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionNode;
import org.jf.dexlib2.builder.MethodLocation;

public abstract class ExecutionContextOp extends Op {

    ExecutionContextOp(MethodLocation location, MethodLocation child) {
        super(location, child);
    }

    ExecutionContextOp(MethodLocation location, MethodLocation[] children) {
        super(location, children);
    }

    public abstract void execute(ExecutionNode executionNode, ExecutionContext context);

}
