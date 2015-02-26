package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AGetOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(AGetOp.class.getSimpleName());

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

    static AGetOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int valueRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new AGetOp(address, opName, childAddress, valueRegister, arrayRegister, indexRegister);
    }

    private final int valueRegister;
    private final int arrayRegister;
    private final int indexRegister;

    public AGetOp(int address, String opName, int childAddress, int valueRegister, int arrayRegister, int indexRegister) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
    }

    @Override
    public int[] execute(MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        HeapItem getItem;
        if (arrayItem.isUnknown()) {
            String innerType = getUnknownArrayInnerType(arrayItem);
            getItem = new HeapItem(new UnknownValue(), innerType);
        } else {
            Object array = arrayItem.getValue();
            if (indexItem.isUnknown()) {
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                getItem = HeapItem.newUnknown(innerType);
            } else {
                int index = indexItem.getIntegerValue();
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                if (index >= Array.getLength(array)) {
                    getItem = HeapItem.newUnknown(innerType);
                    // TODO: throw an exception and return nothing
                    if (log.isWarnEnabled()) {
                        log.warn("Array index out of bounds @" + getAddress() + " for " + toString());
                    }
                } else {
                    Object value = Array.get(array, index);
                    getItem = new HeapItem(value, innerType);
                }
            }
        }
        mState.assignRegister(valueRegister, getItem);

        return getChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
