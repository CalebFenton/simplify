package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReturnVoidOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ReturnVoidOp.class.getSimpleName());

    static ReturnVoidOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        return new ReturnVoidOp(address, opName);
    }

    ReturnVoidOp(int address, String opName) {
        super(address, opName, new int[0]);
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
