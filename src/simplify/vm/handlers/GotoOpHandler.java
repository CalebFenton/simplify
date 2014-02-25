package simplify.vm.handlers;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;

import simplify.vm.MethodContext;

public class GotoOpHandler extends OpHandler {

    static GotoOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = address + branchOffset;

        return new GotoOpHandler(address, opName, targetAddress);
    }

    private GotoOpHandler(int address, String opName, int targetAddress) {
        super(address, opName, targetAddress);
    }

    @Override
    public int[] execute(MethodContext mctx) {
        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" #").append(getPossibleChildren()[0]);

        return sb.toString();
    }

}
