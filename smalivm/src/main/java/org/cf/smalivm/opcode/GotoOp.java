package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.BuilderInstruction;

public class GotoOp extends MethodStateOp {

    GotoOp(BuilderInstruction instruction, BuilderInstruction childInstruction) {
        super(instruction, childInstruction);
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // https://xkcd.com/292/
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        int childAddress = getChildren()[0].getLocation().getCodeAddress();
        sb.append(" #").append(childAddress);

        return sb.toString();
    }

}
