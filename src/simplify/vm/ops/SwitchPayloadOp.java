package simplify.vm.ops;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.types.UnknownValue;

public class SwitchPayloadOp extends Op {

    private static enum SwitchType {
        PACKED,
        SPARSE
    }

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static int[] determineChildren(List<? extends SwitchElement> switchElements) {
        int[] result = new int[switchElements.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = switchElements.get(i).getOffset();
        }

        return result;
    }

    static SwitchPayloadOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        SwitchType switchType = null;
        if (opName.startsWith("packed-")) {
            switchType = SwitchType.PACKED;
        } else {
            switchType = SwitchType.SPARSE;
        }

        SwitchPayload instr = (SwitchPayload) instruction;

        return new SwitchPayloadOp(address, opName, switchType, instr.getSwitchElements());
    }

    private final List<? extends SwitchElement> switchElements;
    private final SwitchType switchType;

    private SwitchPayloadOp(int address, String opName, SwitchType switchType,
                    List<? extends SwitchElement> switchElements) {
        super(address, opName, determineChildren(switchElements));

        this.switchType = switchType;
        this.switchElements = switchElements;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object targetValue = mctx.readResultRegister();

        if (targetValue instanceof UnknownValue) {
            return getPossibleChildren();
        }

        int targetKey = (Integer) targetValue;
        for (SwitchElement element : switchElements) {
            if (element.getKey() == targetKey) {
                return new int[] { element.getOffset() };
            }
        }

        // Branch target is unspecified. Switch ops are CAN_CONTINUE, so do that.
        return new int[] { mctx.getPseudoInstructionReturnAddress() };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" [");
        for (SwitchElement element : switchElements) {
            sb.append(element.getKey()).append(" -> #").append(element.getOffset()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }

}
