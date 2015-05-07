package org.cf.smalivm.opcode;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import org.cf.smalivm.VirtualException;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.SmaliClassUtils;
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

        List<String> exceptionNames = SmaliClassUtils.javaClassToSmali(NullPointerException.class,
                        ArrayIndexOutOfBoundsException.class);
        List<VirtualException> exceptions = new LinkedList<VirtualException>();
        for (String exceptionName : exceptionNames) {
            exceptions.add(new VirtualException(exceptionName));
        }
        setExceptions(exceptions);
    }

    @Override
    public int[] execute(MethodState mState) {
        HeapItem arrayItem = mState.readRegister(arrayRegister);
        HeapItem indexItem = mState.readRegister(indexRegister);

        HeapItem getItem;
        if (arrayItem.isUnknown()) {
            String innerType = getUnknownArrayInnerType(arrayItem);
            getItem = HeapItem.newUnknown(innerType);
        } else {
            Object array = arrayItem.getValue();
            if (indexItem.isUnknown()) {
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                getItem = HeapItem.newUnknown(innerType);
            } else {
                // All values known, so exceptions are deterministic.
                setNoExceptions();

                if (null == array) {
                    VirtualException vex = new VirtualException(
                                    SmaliClassUtils.javaClassToSmali(NullPointerException.class));
                    setException(vex);
                    setNoChildren();

                    return getChildren();
                }

                int index = indexItem.getIntegerValue();
                String innerType = arrayItem.getType().replaceFirst("\\[", "");
                if (index >= Array.getLength(array)) {
                    VirtualException vex = new VirtualException(
                                    SmaliClassUtils.javaClassToSmali(ArrayIndexOutOfBoundsException.class));
                    setException(vex);
                    setNoChildren();

                    return getChildren();
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
