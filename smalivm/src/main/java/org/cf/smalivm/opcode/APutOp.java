package org.cf.smalivm.opcode;

import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualArray;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualType;
import org.cf.util.ClassNameUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.builder.MethodLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;

public class APutOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(APutOp.class.getSimpleName());

    private final int valueRegister;
    private final int arrayRegister;
    private final int indexRegister;
    private final ClassManager classManager;
    private final ExceptionFactory exceptionFactory;

    APutOp(MethodLocation location, MethodLocation child, int putRegister, int arrayRegister, int indexRegister,
           ClassManager classManager, ExceptionFactory exceptionFactory) {
        super(location, child);
        valueRegister = putRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
        this.classManager = classManager;
        this.exceptionFactory = exceptionFactory;

        addException(exceptionFactory.build(this, NullPointerException.class));
        addException(exceptionFactory.build(this, ArrayIndexOutOfBoundsException.class));
    }

    private static Object castValue(String opName, Object value) {
        if (value instanceof Number) {
            if (opName.endsWith("-boolean")) {
                // Booleans are represented by integer literals; need to convert
                Integer intValue = Utils.getIntegerValue(value);
                value = intValue == 1;
            } else {
                Integer intValue = Utils.getIntegerValue(value);
                if (opName.endsWith("-byte")) {
                    value = intValue.byteValue();
                } else if (opName.endsWith("-char")) {
                    // Characters, like boolean, are represented by integers
                    value = (char) intValue.intValue();
                } else if (opName.endsWith("-short")) {
                    value = intValue.shortValue();
                } else if (opName.endsWith("-object") && intValue == 0) {
                    // const/4 v0, 0x0 is null
                    value = null;
                }
            }
        }

        return value;
    }

    private static boolean isOverloadedPrimitiveType(String type) {
        return ClassNameUtils.isPrimitive(type) &&
               !(CommonTypes.FLOAT.equals(type) || CommonTypes.DOUBLE.equals(type) || CommonTypes.LONG.equals(type));
    }

    private static boolean throwsArrayStoreException(HeapItem arrayItem, HeapItem valueItem,
                                                     ClassManager classManager) {
        VirtualType valueType = classManager.getVirtualType(valueItem.getType());
        VirtualType arrayTypeType = classManager.getVirtualType(arrayItem.getType());
        if (arrayTypeType instanceof VirtualClass && arrayTypeType.getName().equals(CommonTypes.OBJECT)) {
            Exception e = new Exception("APutOp");
            log.warn("Attempting to store item in array of type java.lang.Object. Not enough type information to know if this may throw an exception.", e);
            return false;
        }
        VirtualArray arrayType = (VirtualArray) arrayTypeType;
        VirtualType arrayComponentType = arrayType.getComponentType();

        if (valueType.instanceOf(arrayComponentType)) {
            return false;
        }

        // Types: Z B C S I are all represented identically in bytecode (e.g. const/4)
        String valueTypeName = valueType.getName();
        String arrayComponentTypeName = arrayComponentType.getName();
        if (isOverloadedPrimitiveType(valueTypeName) && isOverloadedPrimitiveType(arrayComponentTypeName)) {
            // Trying to store something that looks like one type into another type, but they're compatible.
            // For example, a method returns Z, which is actually either 0x1 or 0x0 and is then storing it in an int
            // array.
            // TODO: figure out what dalvik actually does when you try to aput 0x2 into [B
            // also try to find other edge cases, like Integer.MAX_VALUE in [S
            return false;
        }

        // Trying to store a known value of 0 and a primitive type of integer into an array of object reference.
        // This is how Dalvik does null.
        // TODO: see what happens when Dalvik makes a Z and uses as null, might want to consider other types
        boolean storingNull =
                (!valueItem.isUnknown() && valueTypeName.equals(CommonTypes.INTEGER) && valueItem.asInteger() == 0 &&
                 !ClassNameUtils.isPrimitive(arrayComponentTypeName));

        return !storingNull;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem valueItem = mState.readRegister(valueRegister);
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        boolean throwsStoreException = throwsArrayStoreException(arrayItem, valueItem, classManager);
        if (throwsStoreException) {
            String storeType = ClassNameUtils.internalToBinary(valueItem.getType());
            Throwable exception = exceptionFactory.build(this, ArrayStoreException.class, storeType);
            node.setException(exception);
            node.clearChildren();
            return;
        }

        if (arrayItem.isUnknown()) {
            // Do nothing.
        } else {
            if (valueItem.isUnknown() || indexItem.isUnknown()) {
                String type = arrayItem.getType();
                arrayItem = HeapItem.newUnknown(type);
            } else {
                Object array = arrayItem.getValue();
                if (null == array) {
                    Throwable exception = exceptionFactory.build(this, NullPointerException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                }

                int index = indexItem.asInteger();
                if (index >= Array.getLength(array)) {
                    Throwable exception = exceptionFactory.build(this, ArrayIndexOutOfBoundsException.class);
                    node.setException(exception);
                    node.clearChildren();
                    return;
                } else {
                    Object value = castValue(getName(), valueItem.getValue());
                    Array.set(array, index, value);
                    node.clearExceptions();
                }
            }
        }

        mState.assignRegister(arrayRegister, arrayItem);
    }

    @Override
    public String toString() {
        return getName() + " r" + valueRegister + ", r" + arrayRegister + ", r" + indexRegister;
    }

}
