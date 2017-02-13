package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public class MonitorEnterOp extends MethodStateOp {

    private final int destRegister;

    MonitorEnterOp(MethodLocation location, MethodLocation child, int destRegister) {
        super(location, child);
        this.destRegister = destRegister;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister;
    }
}
