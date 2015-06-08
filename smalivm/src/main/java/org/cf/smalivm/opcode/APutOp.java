package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.ClassManager;
import org.cf.smalivm.VirtualException;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.util.SmaliClassUtils;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APutOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(APutOp.class.getSimpleName());

    static APutOp create(Instruction instruction, int address, VirtualMachine vm) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int putRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(address, opName, childAddress, putRegister, arrayRegister, indexRegister,
                        vm.getClassManager());
    }

    private final int arrayRegister;
    private final int indexRegister;
    private final int valueRegister;
    private final ClassManager classManager;

    public APutOp(int address, String opName, int childAddress, int putRegister, int arrayRegister, int indexRegister,
                    ClassManager classManager) {
        super(address, opName, childAddress);

        this.valueRegister = putRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
        this.classManager = classManager;

        addException(new VirtualException(ArrayIndexOutOfBoundsException.class));
        addException(new VirtualException(NullPointerException.class));
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem valueItem = mState.readRegister(valueRegister);
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        boolean throwsStoreException = throwsArrayStoreException(classManager, arrayItem.getType(), valueItem.getType());
        if (throwsStoreException) {
            String storeType = SmaliClassUtils.smaliClassToJava(valueItem.getType());
            node.setException(new VirtualException(ArrayStoreException.class, storeType));
            node.clearChildAddresses();
            return;
        }

        // TODO: https://github.com/CalebFenton/simplify/issues/21
        // TODO: exceptions should probably have string context
        // TODO: for all exception handling ops, if ambiguous, need to throw all possible exceptions
        if (arrayItem.isUnknown()) {
            // Do nothing.
        } else {
            if ((valueItem.isUnknown()) || (indexItem.isUnknown())) {
                String type = arrayItem.getType();
                arrayItem = HeapItem.newUnknown(type);
            } else {
                Object array = arrayItem.getValue();
                if (null == array) {
                    node.setException(new VirtualException(NullPointerException.class));
                    node.clearChildAddresses();
                    return;
                }

                int index = indexItem.getIntegerValue();
                if (index >= Array.getLength(array)) {
                    node.setException(new VirtualException(ArrayIndexOutOfBoundsException.class));
                    node.clearChildAddresses();
                    return;
                } else {
                    Object value = castValue(getName(), valueItem.getValue());
                    Array.set(array, index, value);
                    node.clearExceptions();
                }
            }
        }

        // In most cases, register assignment means the old value was blown away.
        // The optimizer handles assignments for this op specially.
        mState.assignRegister(arrayRegister, arrayItem);
    }

    private static boolean throwsArrayStoreException(ClassManager classManager, String arrayType, String valueType) {
        String arrayComponentType = SmaliClassUtils.getComponentType(arrayType);
        // These types are all represented identically in bytecode: Z B C S I
        if (isOverloadedPrimitiveType(valueType) && isOverloadedPrimitiveType(arrayComponentType)) {
            // TODO: figure out what dalvik actually does when you try to aput 0x2 into [B
            // also try to find other edge cases, like Integer.MAX_VALUE in [S
            return false;
        }

        try {
            return !classManager.isInstance(valueType, arrayComponentType);
        } catch (UnknownAncestors e) {
            if (log.isWarnEnabled()) {
                log.warn("Unknown ancestors for either {} or {}", valueType, arrayComponentType);
            }
            return true;
        }
    }

    private static boolean isOverloadedPrimitiveType(String type) {
        return (SmaliClassUtils.isPrimitiveType(type) && !("F".equals(type) || "D".equals(type) || "J".equals(type)));
    }

    private static Object castValue(String opName, Object value) {
        if (value instanceof Number) {
            if (opName.endsWith("-wide")) {
                // No need to cast anything
            } else if (opName.endsWith("-boolean")) {
                // Booleans are represented by integer literals, so need to convert
                Integer intValue = Utils.getIntegerValue(value);
                value = (intValue == 1 ? true : false);
            } else {
                Integer intValue = Utils.getIntegerValue(value);
                if (opName.endsWith("-byte")) {
                    value = intValue.byteValue();
                } else if (opName.endsWith("-char")) {
                    // Characters, like boolean, are represented by integers
                    value = (char) intValue.intValue();
                } else if (opName.endsWith("-short")) {
                    value = intValue.shortValue();
                }
            }
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
