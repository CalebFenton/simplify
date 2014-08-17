package simplifier.vm.op_handler;

import java.lang.reflect.Array;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.ClassUtils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.ArrayPayload;

import simplifier.Main;
import simplifier.vm.context.MethodContext;
import simplifier.vm.type.UnknownValue;

public class FillArrayDataPayloadOp extends Op {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    static FillArrayDataPayloadOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;

        ArrayPayload instr = (ArrayPayload) instruction;

        return new FillArrayDataPayloadOp(address, opName, instr.getElementWidth(), instr.getArrayElements());
    }

    private final List<Number> arrayElements;
    private final int elementWidth;

    private FillArrayDataPayloadOp(int address, String opName, int elementWidth, List<Number> arrayElements) {
        super(address, opName, 0); // childAddress / returnAddress not known until runtime

        this.elementWidth = elementWidth;
        this.arrayElements = arrayElements;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        MethodContext parent = mctx.getParent();
        int register = parent.getRegistersAssigned().get(0);
        Object array = mctx.readRegister(register);
        if (!(array instanceof UnknownValue)) {
            Class<?> expectedClass = array.getClass().getComponentType();
            for (int i = 0; i < arrayElements.size(); i++) {
                Number number = arrayElements.get(i);
                Object value = getProperValue(number, expectedClass);
                Array.set(array, i, value);
            }
            mctx.assignRegister(register, array);
        }

        int returnAddress = mctx.getParent().getPseudoInstructionReturnAddress();
        return new int[] { returnAddress };
    }

    private static Object getProperValue(Number number, Class<?> expectedClass) {
        Class<?> klazz = ClassUtils.wrapperToPrimitive(number.getClass());
        Object value = null;

        // Dexlib will only ever make byte (t), int, long (l), or short (s)
        if (klazz == byte.class) {
            value = number.byteValue();
            if (expectedClass == boolean.class) {
                value = (byte) value == 1 ? true : false;
            }
        } else if (klazz == short.class) {
            value = number.shortValue();
            if (expectedClass == char.class) {
                value = (char) number.shortValue();
            }
        } else if (klazz == int.class) {
            if (expectedClass == int.class) {
                value = number.intValue();
            } else if (expectedClass == float.class) {
                value = Float.intBitsToFloat(number.intValue());
            }
        } else if (klazz == long.class) {
            value = number.longValue();
            if (expectedClass == long.class) {
                value = number.longValue();
            } else if (expectedClass == double.class) {
                value = Double.longBitsToDouble(number.longValue());
            }
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(".array-data ");
        sb.append(elementWidth);
        for (Number number : arrayElements) {
            sb.append("\n\t").append(number.toString());
        }
        sb.append("\n.end array-data");

        return sb.toString();
    }
}
