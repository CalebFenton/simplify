package org.cf.smalivm.opcode;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewArrayOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(NewArrayOp.class.getSimpleName());

    private final int destRegister;
    private final int lengthRegister;

    private final String arrayType;
    private final ClassLoader classLoader;

    NewArrayOp(MethodLocation location, MethodLocation child, int destRegister, int lengthRegister, String arrayType,
               ClassLoader classLoader) {
        super(location, child);
        this.destRegister = destRegister;
        this.lengthRegister = lengthRegister;
        this.arrayType = arrayType;
        this.classLoader = classLoader;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem lengthItem = mState.readRegister(lengthRegister);
        Object instance = buildInstance(lengthItem);
        if (instance instanceof Throwable) {
            node.setException((Throwable) instance);
            node.clearChildren();
            return;
        } else {
            node.clearExceptions();
        }

        mState.assignRegister(destRegister, instance, arrayType);
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", r" + lengthRegister + ", " + arrayType;
    }

    private Object buildInstance(HeapItem lengthItem) {
        Object instance;
        if (lengthItem.isUnknown()) {
            instance = new UnknownValue();
        } else {
            int length = lengthItem.asInteger();
            try {
                // Dalvik does not statically initialize classes with new-array
                instance = Utils.buildArray(arrayType, length, classLoader);
            } catch (ClassNotFoundException e) {
                return e;
            }
        }

        return instance;
    }

    public int getDestRegister() {
        return destRegister;
    }

    public int getLengthRegister() {
        return lengthRegister;
    }

    public String getArrayType() {
        return arrayType;
    }

}
