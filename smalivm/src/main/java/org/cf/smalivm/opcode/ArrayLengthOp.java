package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayLengthOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(ArrayLengthOp.class.getSimpleName());

    private final int arrayRegister;
    private final int destRegister;

    ArrayLengthOp(MethodLocation location, MethodLocation child, int valueRegister, int arrayRegister) {
        super(location, child);

        destRegister = valueRegister;
        this.arrayRegister = arrayRegister;

        addException(new VirtualException(NullPointerException.class, "Attempt to get length of null array"));
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        Object array = arrayItem.getValue();
        Object lengthValue = null;
        if (arrayItem.isUnknown()) {
            lengthValue = new UnknownValue();
        } else if (array != null && array.getClass().isArray()) {
            lengthValue = Array.getLength(array);
            node.clearExceptions();
        } else {
            if (array == null) {
                node.setExceptions(getExceptions());
                node.clearChildren();
                return;
            } else {
                // Won't pass verifier if it's not an array type. Probably our fault, so error.
                if (log.isErrorEnabled()) {
                    log.error("Unexpected non-array class: {}, {}", array.getClass(), array);
                }
            }
        }
        mState.assignRegister(destRegister, lengthValue, "I");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(arrayRegister);

        return sb.toString();
    }

}
