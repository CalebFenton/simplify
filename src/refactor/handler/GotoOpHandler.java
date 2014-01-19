package refactor.handler;

import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;

import refactor.vm.MethodContext;

public class GotoOpHandler extends OpHandler {

    static GotoOpHandler create(Instruction instruction, int index) {
        String opName = instruction.getOpcode().name;

        int codeAddress = ((BuilderInstruction) instruction).getLocation().getCodeAddress();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = codeAddress + branchOffset;

        return new GotoOpHandler(index, opName, targetAddress);
    }

    private final int index;
    private final String opName;
    private final int targetAddress;

    private GotoOpHandler(int index, String opName, int targetAddress) {
        this.index = index;
        this.opName = opName;
        this.targetAddress = targetAddress;
    }

    @Override
    public int[] execute(MethodContext mectx) {
        return new int[] { targetAddress };
    }

    @Override
    public int getIndex() {
        return index;
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
