package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnVoidOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReturnVoidOp.class.getSimpleName());

    ReturnVoidOp(MethodLocation location) {
        super(location);
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        //
    }

    @Override
    public String toString() {
        return getName();
    }

}
