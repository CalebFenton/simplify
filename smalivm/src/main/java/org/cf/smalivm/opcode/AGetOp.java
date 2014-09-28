package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AGetOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AGetOp.class.getSimpleName());

    private static String getUnknownArrayInnerType(UnknownValue array) {
        String outerType = array.getType();
        String result = null;
        if (outerType.equals("?")) {
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

    private final int arrayRegister;
    private final int indexRegister;

    private final int valueRegister;

    public AGetOp(int address, String opName, int childAddress, int valueRegister, int arrayRegister, int indexRegister) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object array = mState.readRegister(arrayRegister);
        Object indexValue = mState.readRegister(indexRegister);

        Object value = null;
        if (array instanceof UnknownValue) {
            String innerType = getUnknownArrayInnerType((UnknownValue) array);
            value = new UnknownValue(innerType);
        } else {
            if (indexValue instanceof UnknownValue) {
                String innerType = array.getClass().getName().replaceFirst("\\[", "");
                value = new UnknownValue(innerType);
            } else {
                int index = (int) indexValue;
                value = Array.get(array, index);
            }
        }

        mState.assignRegister(valueRegister, value);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
