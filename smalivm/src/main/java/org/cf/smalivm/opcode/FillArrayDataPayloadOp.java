package org.cf.smalivm.opcode;

import org.apache.commons.lang3.ClassUtils;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.List;

public class FillArrayDataPayloadOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(FillArrayDataPayloadOp.class.getSimpleName());

    private static Object getProperValue(Number number, Class<?> expectedClass) {
        Class<?> klazz = ClassUtils.wrapperToPrimitive(number.getClass());
        Object value = null;

        // Dexlib will only ever make byte (t), int, long (l), or short (s)
        if (klazz == byte.class) {
            value = number.byteValue();
        } else if (klazz == short.class) {
            value = number.shortValue();
        } else if (klazz == int.class) {
            value = number.intValue();
        } else if (klazz == long.class) {
            value = number.longValue();
        }

        if (expectedClass == boolean.class) {
            value = (byte) value == 1;
        } else if (expectedClass == char.class) {
            value = (char) number.shortValue();
        } else if (expectedClass == short.class) {
            value = number.shortValue();
        } else if (expectedClass == int.class) {
            value = number.intValue();
        } else if (expectedClass == long.class) {
            value = number.longValue();
        } else if (expectedClass == float.class) {
            value = Float.intBitsToFloat(number.intValue());
        } else if (expectedClass == double.class) {
            value = Double.longBitsToDouble(number.longValue());
        }

        return value;
    }

    private final List<Number> arrayElements;
    private final int elementWidth;

    FillArrayDataPayloadOp(MethodLocation location, int elementWidth, List<Number> arrayElements) {
        // childAddress / returnAddress not known until runtime
        super(location);

        this.elementWidth = elementWidth;
        this.arrayElements = arrayElements;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        MethodState parent = mState.getParent();
        int targetRegister = parent.getRegistersAssigned()[0];
        // Peek rather than read. This pseudo-instruction shouldn't count as an actual usage for the optimizer.
        HeapItem arrayItem = mState.peekRegister(targetRegister);
        if (!arrayItem.isUnknown()) {
            Object array = arrayItem.getValue();
            Class<?> expectedClass = array.getClass().getComponentType();
            for (int i = 0; i < arrayElements.size(); i++) {
                Number number = arrayElements.get(i);
                Object value = getProperValue(number, expectedClass);
                Array.set(array, i, value);
            }
            // Poke rather than assign for the optimizer.
            mState.pokeRegister(targetRegister, arrayItem);
        }

        MethodLocation returnLocation = mState.getParent().getPseudoInstructionReturnInstruction();
        node.setChildLocations(returnLocation);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(".array-data ");
        sb.append(elementWidth);
        for (Number number : arrayElements) {
            sb.append("\n\t").append(number.toString());
        }
        sb.append("\n.end array-data");

        return sb.toString();
    }

}
