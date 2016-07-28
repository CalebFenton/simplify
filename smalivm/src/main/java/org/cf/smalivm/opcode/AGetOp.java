package org.cf.smalivm.opcode;

import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;

public class AGetOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(AGetOp.class.getSimpleName());

    private final int valueRegister;
    private final int arrayRegister;
    private final int indexRegister;
    private final ExceptionFactory exceptionFactory;

    AGetOp(MethodLocation location, MethodLocation child, int valueRegister, int arrayRegister, int indexRegister,
           ExceptionFactory exceptionFactory) {
        super(location, child);
        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
        this.exceptionFactory = exceptionFactory;

        addException(exceptionFactory.build(this, NullPointerException.class));
        addException(exceptionFactory.build(this, ArrayIndexOutOfBoundsException.class));
    }

    private static String getUnknownArrayInnerType(HeapItem array) {
        String outerType = array.getType();
        String result;
        if (CommonTypes.UNKNOWN.equals(outerType)) {
            result = CommonTypes.UNKNOWN;
        } else {
            result = outerType.replaceFirst("\\[", "");
        }

        return result;
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
                    Throwable exception = exceptionFactory.build(this, NullPointerException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                }

                int index = indexItem.asInteger();
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                if (index >= Array.getLength(array)) {
                    Throwable exception = exceptionFactory.build(this, ArrayIndexOutOfBoundsException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                } else {
                    Object value = Array.get(array, index);
                    getItem = new HeapItem(value, innerType);
                    node.clearExceptions();
                }
            }
        }
        mState.assignRegister(valueRegister, getItem);
    }

    @Override
    public String toString() {
        return getName() + " r" + valueRegister + ", r" + arrayRegister + ", r" + indexRegister;
    }

}
