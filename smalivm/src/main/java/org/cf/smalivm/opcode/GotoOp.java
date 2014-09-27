package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;

public class GotoOp extends MethodStateOp {

    static GotoOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;

        return new GotoOp(address, opName, targetAddress);
    }

    private GotoOp(int address, String opName, int targetAddress) {
        super(address, opName, targetAddress);
    }

    @Override
    public int[] execute(MethodState mState) {
        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" #").append(getPossibleChildren()[0]);

        return sb.toString();
    }

}
