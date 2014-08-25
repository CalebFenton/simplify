package org.cf.smalivm.op_handler;

import java.util.logging.Logger;

import org.cf.smalivm.context.MethodContext;
import org.jf.dexlib2.iface.instruction.Instruction;

public class ReturnVoidOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ReturnVoidOp.class.getSimpleName());

    static ReturnVoidOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        return new ReturnVoidOp(address, opName);
    }

    ReturnVoidOp(int address, String opName) {
        super(address, opName, new int[0]);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        return sb.toString();
    }

}
