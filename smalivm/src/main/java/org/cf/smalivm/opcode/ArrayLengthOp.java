package org.cf.smalivm.opcode;

import java.lang.reflect.Array;

import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayLengthOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(ArrayLengthOp.class.getSimpleName());

    static ArrayLengthOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction12x instr = (Instruction12x) instruction;
        int destRegister = instr.getRegisterA();
        int arrayRegister = instr.getRegisterB();

        return new ArrayLengthOp(address, opName, childAddress, destRegister, arrayRegister);
    }

    private final int arrayRegister;
    private final int destRegister;

    public ArrayLengthOp(int address, String opName, int childAddress, int valueRegister, int arrayRegister) {
        super(address, opName, childAddress);

        this.destRegister = valueRegister;
        this.arrayRegister = arrayRegister;
    }

    @Override
    public void execute(ExecutionNode node, MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        Object array = arrayItem.getValue();
        Object lengthValue = null;
        if (arrayItem.isUnknown()) {
            lengthValue = new UnknownValue();
        } else if ((array != null) && array.getClass().isArray()) {
            lengthValue = Array.getLength(array);
        } else {
            if (log.isWarnEnabled()) {
                if (array == null) {
                    log.warn("Unexpected null array for array-length");
                } else {
                    log.warn("Unexpected non-array class: " + array.getClass() + ", " + array);
                }
            }
        }
        mState.assignRegister(destRegister, lengthValue, "I");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(arrayRegister);

        return sb.toString();
    }

}
