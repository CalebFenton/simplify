package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APutOp extends MethodStateOp {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(APutOp.class.getSimpleName());

    static APutOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction23x instr = (Instruction23x) instruction;
        int valueRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();
        int indexRegister = instr.getRegisterC();

        return new APutOp(address, opName, childAddress, valueRegister, arrayRegister, indexRegister);
    }

    private final int arrayRegister;
    private final int indexRegister;

    private final int valueRegister;

    public APutOp(int address, String opName, int childAddress, int valueRegister, int arrayRegister, int indexRegister) {
        super(address, opName, childAddress);

        this.valueRegister = valueRegister;
        this.arrayRegister = arrayRegister;
        this.indexRegister = indexRegister;
    }

    @Override
    public int[] execute(MethodState mState) {
        Object value = mState.readRegister(valueRegister);
        Object array = mState.readRegister(arrayRegister);
        Object indexValue = mState.readRegister(indexRegister);

        // TODO: https://github.com/CalebFenton/simplify/issues/21
        if (array instanceof UnknownValue) {
            // Do nothing. :(
        } else {
            if ((value instanceof UnknownValue) || (indexValue instanceof UnknownValue)) {
                String type = array.getClass().getName();
                array = new UnknownValue(type);
            } else {
                if (value instanceof Number) {
                    if (getName().endsWith("-wide")) {
                        value = (long) value;
                    } else if (getName().endsWith("-boolean")) {
                        value = ((int) value == 1 ? true : false);
                    } else {
                        Integer intValue = Utils.getIntegerValue(value);
                        if (getName().endsWith("-byte")) {
                            value = intValue.byteValue();
                        } else if (getName().endsWith("-char")) {
                            value = (char) intValue.intValue();
                        } else if (getName().endsWith("-short")) {
                            value = intValue.shortValue();
                        }
                    }
                }

                int index = (int) indexValue;
                Array.set(array, index, value);
            }
        }

        // In most cases, register assignment means the old value was blown away. The optimizer handles assignments for
        // this op specially.
        mState.assignRegister(arrayRegister, array);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
