package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrayOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(NewArrayOp.class.getSimpleName());

    private final int destRegister;
    private final int lengthRegister;
    private final boolean useLocalClass;
    private final String arrayType;

    NewArrayOp(MethodLocation location, MethodLocation child, int destRegister, int lengthRegister, String arrayType,
                    boolean useLocalClass) {
        super(location, child);

        this.destRegister = destRegister;
        this.lengthRegister = lengthRegister;
        this.arrayType = arrayType;
        this.useLocalClass = useLocalClass;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem lengthItem = mState.readRegister(lengthRegister);
        HeapItem instanceItem;
        if (lengthItem.isUnknown()) {
            instanceItem = HeapItem.newUnknown(arrayType);
        } else {
            int length = lengthItem.getIntegerValue();
            try {
                // Dalvik does not statically initialize classes with new-array
                Object instance = Utils.buildArray(arrayType, length, useLocalClass);
                instanceItem = new HeapItem(instance, arrayType);
            } catch (ClassNotFoundException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Couldn't find class: {}, using unknown @{}", arrayType, toString());
                }
                instanceItem = HeapItem.newUnknown(arrayType);
            }
        }
        mState.assignRegister(destRegister, instanceItem);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(lengthRegister).append(", ").append(arrayType);

        return sb.toString();
    }

}
