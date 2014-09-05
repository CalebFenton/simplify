package org.cf.smalivm.opcode;

import java.lang.reflect.Array;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class AGetOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(AGetOp.class.getSimpleName());

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
    public int[] execute(MethodContext mctx) {
        Object array = mctx.readRegister(arrayRegister);
        Object indexValue = mctx.readRegister(indexRegister);

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

        mctx.assignRegister(valueRegister, value);

        return getPossibleChildren();
    }

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
