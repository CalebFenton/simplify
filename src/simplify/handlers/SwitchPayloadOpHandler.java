package simplify.handlers;

import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;

import simplify.Main;
import simplify.vm.MethodContext;
import simplify.vm.types.UnknownValue;

public class SwitchPayloadOpHandler extends OpHandler {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static enum SwitchType {
        PACKED,
        SPARSE
    };

    static SwitchPayloadOpHandler create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        SwitchType switchType = null;
        if (opName.startsWith("packed-")) {
            switchType = SwitchType.PACKED;
        } else {
            switchType = SwitchType.SPARSE;
        }

        SwitchPayload instr = (SwitchPayload) instruction;

        return new SwitchPayloadOpHandler(address, opName, switchType, instr.getSwitchElements());
    }

    private final int address;
    private final String opName;
    private final SwitchType switchType;
    private final List<? extends SwitchElement> switchElements;

    private SwitchPayloadOpHandler(int address, String opName, SwitchType switchType,
                    List<? extends SwitchElement> switchElements) {
        this.address = address;
        this.opName = opName;
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

        // TODO: Not sure what happens if target is not found. Maybe it continues at next instruction after
        // original call?
        log.warning("Switch payload couldn't find target. Didn't know this could happen!");

        return new int[0];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(opName);

        sb.append(" [");
        for (SwitchElement element : switchElements) {
            sb.append(element.getKey()).append(" -> #").append(element.getOffset()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }

    @Override
    public int[] getPossibleChildren() {
        int[] result = new int[switchElements.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = switchElements.get(i).getOffset();
        }

        return result;
    }

    @Override
    public int getAddress() {
        return address;
    }

}
