package org.cf.smalivm.opcode;

import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;

public class UnaryMathOp extends MethodStateOp {

    private static String getResultTypeName(String opName) {
        String[] parts = opName.split("-");
        String type = parts[parts.length - 1];
        switch (type) {
        case "int":
            return "I";
        case "long":
            return "J";
        case "float":
            return "F";
        case "byte":
            return "B";
        case "char":
            return "C";
        case "double":
            return "D";
        case "short":
            return "S";
        }

        return null;
    }

    static UnaryMathOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();

        Instruction12x instr = (Instruction12x) instruction;
        int destRegister = instr.getRegisterA();
        int srcRegister = instr.getRegisterB();
        return new UnaryMathOp(address, opName, childAddress, destRegister, srcRegister);
    }

    private final int destRegister;
    private final int srcRegister;

    private UnaryMathOp(int address, String opName, int childAddress, int destRegister, int srcRegister) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.srcRegister = srcRegister;
    }

    @Override
    public int[] execute(MethodState mState) {
        HeapItem item = mState.readRegister(srcRegister);
        HeapItem resultItem;
        String type = getResultTypeName(getName());
        if (item.isUnknown()) {
            resultItem = HeapItem.newUnknown(type);
        } else {
            Object resultValue = perform(item.getValue(), getName());
            resultItem = new HeapItem(resultValue, type);
        }
        mState.assignRegister(destRegister, resultItem);

        return this.getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getName());
        sb.append(" r").append(destRegister).append(", r").append(srcRegister);

        return sb.toString();
    }

    private Object perform(Object value, String opName) {
        // Stupid, but simple. Deals.
        Object resultValue = null;
        if (opName.startsWith("double")) {
            Double typedValue = Utils.getDoubleValue(value);
            if (opName.endsWith("float")) {
                resultValue = typedValue.floatValue();
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.intValue();
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.longValue();
            }
        } else if (opName.startsWith("float")) {
            Float typedValue = Utils.getFloatValue(value);
            if (opName.endsWith("double")) {
                resultValue = typedValue.doubleValue();
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.intValue();
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.longValue();
            }
        } else if (opName.startsWith("long")) {
            Long typedValue = Utils.getLongValue(value);
            if (opName.endsWith("double")) {
                resultValue = typedValue.doubleValue();
            } else if (opName.endsWith("int")) {
                resultValue = typedValue.intValue();
            } else if (opName.endsWith("float")) {
                resultValue = typedValue.floatValue();
            }
        } else if (opName.startsWith("int")) {
            // Could be something other than an int, such as short
            Integer typedValue = Utils.getIntegerValue(value);
            if (opName.endsWith("byte")) {
                resultValue = typedValue.byteValue();
            } else if (opName.endsWith("char")) {
                resultValue = (char) typedValue.intValue();
            } else if (opName.endsWith("short")) {
                resultValue = typedValue.shortValue();
            } else if (opName.endsWith("double")) {
                resultValue = typedValue.doubleValue();
            } else if (opName.endsWith("long")) {
                resultValue = typedValue.longValue();
            } else if (opName.endsWith("float")) {
                resultValue = typedValue.floatValue();
            }
        } else if (opName.startsWith("neg")) {
            if (opName.endsWith("double")) {
                Double typedValue = Utils.getDoubleValue(value);
                resultValue = -typedValue;
            } else if (opName.endsWith("float")) {
                Float typedValue = Utils.getFloatValue(value);
                resultValue = -typedValue;
            } else if (opName.endsWith("int")) {
                Integer typedValue = Utils.getIntegerValue(value);
                resultValue = -typedValue;
            } else if (opName.endsWith("long")) {
                Long typedValue = Utils.getLongValue(value);
                resultValue = -typedValue;
            }
        } else if (opName.startsWith("not")) {
            if (opName.endsWith("int")) {
                Integer typedValue = Utils.getIntegerValue(value);
                resultValue = ~typedValue;
            } else if (opName.endsWith("long")) {
                Long typedValue = Utils.getLongValue(value);
                resultValue = ~typedValue;
            }
        }

        return resultValue;
    }

}
