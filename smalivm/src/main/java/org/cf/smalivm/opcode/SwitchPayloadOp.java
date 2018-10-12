package org.cf.smalivm.opcode;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SwitchPayloadOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory
            .getLogger(SwitchPayloadOp.class.getSimpleName());

    private static final int SWITCH_OP_CODE_UNITS = 3;

    private final TIntObjectMap<MethodLocation> addressToLocation;
    private final TIntIntMap targetKeyToOffset;

    SwitchPayloadOp(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation,
                    TIntIntMap targetKeyToOffset) {
        // Don't know children until we know the pseudo return instruction, only branch offsets
        super(location);

        this.targetKeyToOffset = targetKeyToOffset;
        this.addressToLocation = addressToLocation;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // Pseudo points to instruction *after* switch op.
        MethodLocation returnLocation = mState.getPseudoInstructionReturnInstruction();
        int branchFromAddress = returnLocation.getCodeAddress() - SWITCH_OP_CODE_UNITS;

        HeapItem targetItem = mState.readResultRegister();
        if (targetItem.isUnknown()) {
            List<MethodLocation> childList = getTargets(branchFromAddress, targetKeyToOffset);
            childList.add(returnLocation);
            MethodLocation[] children = childList.toArray(new MethodLocation[childList.size()]);
            node.setChildLocations(children);
            return;
        }

        int targetKey = Utils.getIntegerValue(targetItem.getValue());
        if (targetKeyToOffset.containsKey(targetKey)) {
            int targetOffset = branchFromAddress + targetKeyToOffset.get(targetKey);
            MethodLocation child = addressToLocation.get(targetOffset);
            node.setChildLocations(child);
            return;
        }

        // Branch target is unspecified. Continue to next op.
        node.setChildLocations(returnLocation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" [");
        int[] keys = targetKeyToOffset.keys();
        Arrays.sort(keys);
        for (int key : keys) {
            int offset = targetKeyToOffset.get(key);
            sb.append(key).append(" -> :addr_").append(offset).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');

        return sb.toString();
    }

    private List<MethodLocation> getTargets(int branchFromAddress, TIntIntMap targetKeyToOffset) {
        int[] offsets = targetKeyToOffset.values();
        List<MethodLocation> targets = new LinkedList<>();
        for (int offset : offsets) {
            int targetOffset = branchFromAddress + offset;
            targets.add(addressToLocation.get(targetOffset));
        }

        return targets;
    }
}
