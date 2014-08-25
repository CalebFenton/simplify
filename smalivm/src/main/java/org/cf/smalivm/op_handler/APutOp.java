package org.cf.smalivm.op_handler;

import java.lang.reflect.Array;
import java.util.logging.Logger;

import org.cf.smalivm.context.MethodContext;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

public class APutOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(APutOp.class.getSimpleName());

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
    public int[] execute(MethodContext mctx) {
        Object value = mctx.readRegister(valueRegister);
        Object array = mctx.readRegister(arrayRegister);
        Object indexValue = mctx.readRegister(indexRegister);

        // TODO: Adding unknown elements to arrays is very pessimistic. If the value is not known, the entire array
        // becomes unknown. It's much better (though much harder) to keep track of individual unknown elements. This
        // requires more robust and complex array handling. After this thing moves past proof of concept, please fix,
        // future me.
        if (array instanceof UnknownValue) {
            // Do nothing. :(
        } else {
            if ((value instanceof UnknownValue) || (indexValue instanceof UnknownValue)) {
                String type = array.getClass().getName();
                array = new UnknownValue(type);
            } else {
                if (getOpName().endsWith("-wide")) {
                    value = (long) value;
                } else if (getOpName().endsWith("-boolean")) {
                    value = ((int) value == 1 ? true : false);
                } else if (getOpName().endsWith("-byte")) {
                    value = (byte) value;
                } else if (getOpName().endsWith("-char")) {
                    value = (char) ((int) value);
                } else if (getOpName().endsWith("-short")) {
                    value = (short) ((int) value);
                }

                int index = (int) indexValue;
                Array.set(array, index, value);
            }
        }

        // Let the optimizer know the array was modified.
        mctx.assignRegister(arrayRegister, array);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(valueRegister).append(", r").append(arrayRegister).append(", r").append(indexRegister);

        return sb.toString();
    }

}
