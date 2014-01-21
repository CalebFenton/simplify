package simplify.handlers;

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

    private final int address;
    private final String opName;
    private final int targetAddress;

    private GotoOpHandler(int address, String opName, int targetAddress) {
        this.address = address;
        this.opName = opName;
        this.targetAddress = targetAddress;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        return new int[] { targetAddress };
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int[] getPossibleChildren() {
        return new int[] { targetAddress };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" #").append(targetAddress);

        return sb.toString();
    }

}
