package org.cf.smalivm.opcode;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;

import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchPayloadOp extends MethodStateOp {

    static enum SwitchType {
        PACKED, SPARSE
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SwitchPayloadOp.class.getSimpleName());

    private static final int SWITCH_OP_CODE_UNITS = 3;
    private final TIntObjectMap<BuilderInstruction> addressToInstruction;
    private final TIntIntMap targetKeyToOffset;

    SwitchPayloadOp(BuilderInstruction instruction, TIntObjectMap<BuilderInstruction> addressToInstruction,
                    TIntIntMap targetKeyToOffset) {
        // Don't know children until we know the pseudo return instruction, only branch offsets
        super(instruction);

        this.targetKeyToOffset = targetKeyToOffset;
        this.addressToInstruction = addressToInstruction;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        // Pseudo points to instruction *after* switch op.
        BuilderInstruction returnInstruction = mState.getPseudoInstructionReturnInstruction();
        int branchFromAddress = returnInstruction.getLocation().getCodeAddress() - SWITCH_OP_CODE_UNITS;

        HeapItem targetItem = mState.readResultRegister();
        if (targetItem.isUnknown()) {
            List<BuilderInstruction> childList = getTargets(branchFromAddress, targetKeyToOffset);
            childList.add(returnInstruction);
            BuilderInstruction[] children = childList.toArray(new BuilderInstruction[childList.size()]);
            node.setChildren(children);
            return;
        }

        int targetKey = Utils.getIntegerValue(targetItem.getValue());
        if (targetKeyToOffset.containsKey(targetKey)) {
            int targetOffset = branchFromAddress + targetKeyToOffset.get(targetKey);
            BuilderInstruction child = addressToInstruction.get(targetOffset);
            node.setChildren(child);
            return;
        }

        // Branch target is unspecified. Continue to next op.
        node.setChildren(returnInstruction);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" [");
        for (int key : targetKeyToOffset.keys()) {
            int offset = targetKeyToOffset.get(key);
            sb.append(key).append(" -> #").append(offset).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append(']');

        return sb.toString();
    }

    private List<BuilderInstruction> getTargets(int branchFromAddress, TIntIntMap targetKeyToOffset) {
        int[] offsets = targetKeyToOffset.values();
        List<BuilderInstruction> targets = new LinkedList<BuilderInstruction>();
        for (int i = 0; i < offsets.length; i++) {
            int targetOffset = branchFromAddress + offsets[i];
            targets.add(addressToInstruction.get(targetOffset));
        }

        return targets;
    }
}
