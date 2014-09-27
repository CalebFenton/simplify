package org.cf.smalivm.opcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;

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
    public int[] execute(MethodState mctx) {
        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        return sb.toString();
    }

}
