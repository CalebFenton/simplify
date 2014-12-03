package org.cf.smalivm.opcode;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;

public class SwitchPayloadOp extends MethodStateOp {

    private static enum SwitchType {
        PACKED,
        SPARSE
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SwitchPayloadOp.class.getSimpleName());

    private static final int SWITCH_OP_CODE_UNITS = 3;

    private static int[] determineChildren(List<? extends SwitchElement> switchElements) {
        TIntSet children = new TIntHashSet(switchElements.size() + 1);
        // Switch ops are CAN_CONTINUE and may "fall through". Add immediate op.
        children.add(SWITCH_OP_CODE_UNITS);
        for (int i = 0; i < switchElements.size(); i++) {
            int offset = switchElements.get(i).getOffset();
            children.add(offset);
        }

        return children.toArray();
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
        List<? extends SwitchElement> switchElements = instr.getSwitchElements();

        return new SwitchPayloadOp(address, opName, switchType, switchElements);
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
    public int[] execute(MethodState mState) {
        Object targetValue = mState.readResultRegister();
        // Pseudo points to instruction *after* switch op.
        int switchOpAddress = mState.getPseudoInstructionReturnAddress() - SWITCH_OP_CODE_UNITS;

        if (targetValue instanceof UnknownValue) {
            int[] children = getTargetAddresses(switchOpAddress, getPossibleChildren());

            return children;
        }

        int targetKey = (Integer) targetValue;
        for (SwitchElement element : switchElements) {
            if (element.getKey() == targetKey) {
                int targetAddress = getTargetAddress(switchOpAddress, element.getOffset());

                return new int[] { targetAddress };
            }
        }

        // Branch target is unspecified. Continue to next op.
        return new int[] { mState.getPseudoInstructionReturnAddress() };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" [");
        for (SwitchElement element : switchElements) {
            sb.append(element.getKey()).append(" -> #").append(element.getOffset()).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }

    private int getTargetAddress(int switchOpAddress, int offset) {
        // Offsets are from switch op's address.
        return switchOpAddress + offset;
    }

    private int[] getTargetAddresses(int switchOpAddress, int[] offsets) {
        int[] result = new int[offsets.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getTargetAddress(switchOpAddress, offsets[i]);
        }

        return result;
    }

}
