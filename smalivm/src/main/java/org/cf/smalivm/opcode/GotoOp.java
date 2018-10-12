package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;

public class GotoOp extends MethodStateOp {

    GotoOp(MethodLocation location, MethodLocation childInstruction) {
        super(location, childInstruction);
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // https://xkcd.com/292/
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        int childAddress = getChildren()[0].getCodeAddress();
        sb.append(" :addr_").append(childAddress);

        return sb.toString();
    }

}
