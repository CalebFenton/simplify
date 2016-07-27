package org.cf.smalivm.opcode;

import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;

public class ArrayLengthOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(ArrayLengthOp.class.getSimpleName());

    private final int arrayRegister;
    private final int destRegister;

    ArrayLengthOp(MethodLocation location, MethodLocation child, int destRegister, int arrayRegister,
                  ExceptionFactory exceptionFactory) {
        super(location, child);
        this.destRegister = destRegister;
        this.arrayRegister = arrayRegister;

        addException(exceptionFactory.build(this, NullPointerException.class, "Attempt to get length of null array"));
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        Object array = arrayItem.getValue();
        Object lengthValue = null;
        if (arrayItem.isUnknown()) {
            lengthValue = new UnknownValue();
        } else if (array == null) {
            node.clearChildren();
            return;
        } else if (array.getClass().isArray()) {
            lengthValue = Array.getLength(array);
            node.clearExceptions();
        } else {
            // Won't pass DEX verifier if it's not an array class. Probably our fault, so error.
            if (log.isErrorEnabled()) {
                log.error("Unexpected non-array class: {}, {}", array.getClass(), array);
            }
        }
        mState.assignRegister(destRegister, lengthValue, CommonTypes.INTEGER);
    }

    @Override
    public String toString() {
        return getName() + " r" + destRegister + ", r" + arrayRegister;
    }

}
