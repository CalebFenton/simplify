package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APutOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(APutOp.class.getSimpleName());

    static APutOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int putRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(address, opName, childAddress, putRegister, arrayRegister, indexRegister);
    }

    private final int arrayRegister;
    private final int indexRegister;
    private final int putRegister;

    public APutOp(int address, String opName, int childAddress, int putRegister, int arrayRegister, int indexRegister) {
        super(address, opName, childAddress);

        this.putRegister = putRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
    }

    @Override
    public int[] execute(MethodState mState) {
        HeapItem putItem = mState.readRegister(putRegister);
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        // TODO: https://github.com/CalebFenton/simplify/issues/21
        if (arrayItem.isUnknown()) {
            // Do nothing. :(
        } else {
            if ((putItem.isUnknown()) || (indexItem.isUnknown())) {
                String type = arrayItem.getType();
                arrayItem = HeapItem.newUnknown(type);
            } else {
                Object putValue = putItem.getValue();
                if (putValue instanceof Number) {
                    if (getName().endsWith("-wide")) {
                        // No need to cast anything
                    } else if (getName().endsWith("-boolean")) {
                        // Booleans are represented by integer literals, so need to convert
                        Integer intValue = Utils.getIntegerValue(putValue);
                        putValue = (intValue == 1 ? true : false);
                    } else {
                        Integer intValue = Utils.getIntegerValue(putValue);
                        if (getName().endsWith("-byte")) {
                            putValue = intValue.byteValue();
                        } else if (getName().endsWith("-char")) {
                            // Characters, like boolean, are represented by integers
                            putValue = (char) intValue.intValue();
                        } else if (getName().endsWith("-short")) {
                            putValue = intValue.shortValue();
                        }
                    }
                }

                int index = indexItem.getIntegerValue();
                Object array = arrayItem.getValue();
                if (index >= Array.getLength(array)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Array index out of bounds @" + getAddress() + " for " + toString());
                    }
                    arrayItem = HeapItem.newUnknown(arrayItem.getType());
                } else {
                    Array.set(array, index, putValue);
                }
            }
        }

        // In most cases, register assignment means the old value was blown away. The optimizer handles assignments for
        // this op specially.
        mState.assignRegister(arrayRegister, arrayItem);

        return getChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(putRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
