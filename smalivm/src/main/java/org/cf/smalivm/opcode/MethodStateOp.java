package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public abstract class MethodStateOp extends Op {

    MethodStateOp(MethodLocation location) {
        super(location, new MethodLocation[0]);
    }

    MethodStateOp(MethodLocation location, MethodLocation child) {
        super(location, child);
    }

    MethodStateOp(MethodLocation location, MethodLocation[] children) {
        super(location, children);
    }

    public abstract void execute(ExecutionNode executionNode, MethodState mState);

}
