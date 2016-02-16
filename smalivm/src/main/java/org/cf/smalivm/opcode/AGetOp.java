package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AGetOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(AGetOp.class.getSimpleName());

    private final int valueRegister;
    private final int arrayRegister;
    private final int indexRegister;

    AGetOp(MethodLocation location, MethodLocation child, int valueRegister, int arrayRegister, int indexRegister) {
        super(location, child);

        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;

        addException(new VirtualException(NullPointerException.class));
        addException(new VirtualException(ArrayIndexOutOfBoundsException.class));
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        HeapItem getItem;
        if (arrayItem.isUnknown()) {
            String innerType = getUnknownArrayInnerType(arrayItem);
            getItem = HeapItem.newUnknown(innerType);
        } else {
            Object array = arrayItem.getValue();
            if (indexItem.isUnknown()) {
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                getItem = HeapItem.newUnknown(innerType);
            } else {
                // All values known, so exceptions are deterministic.
                node.clearExceptions();

                if (null == array) {
                    VirtualException exception = new VirtualException(NullPointerException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                }

                int index = indexItem.getIntegerValue();
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                if (index >= Array.getLength(array)) {
                    VirtualException exception = new VirtualException(ArrayIndexOutOfBoundsException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                } else {
                    Object value = Array.get(array, index);
                    getItem = new HeapItem(value, innerType);
                    // node.clearExceptions();
                }
            }
        }
        mState.assignRegister(valueRegister, getItem);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

    private static String getUnknownArrayInnerType(HeapItem array) {
        String outerType = array.getType();
        String result = null;
        if ("?".equals(outerType)) {
            result = "?";
        } else {
            result = outerType.replaceFirst("\\[", "");
        }

        return result;
    }

}
