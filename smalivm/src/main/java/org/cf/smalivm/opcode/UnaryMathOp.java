package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;

public class UnaryMathOp extends MethodStateOp {

    private static String getDestinationTypeName(String opName) {
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
        Object value = mState.readRegister(srcRegister);
        Object newValue = null;
        if (value instanceof UnknownValue) {
            String destType = getDestinationTypeName(getName());
            newValue = new UnknownValue(destType);
        } else {
            newValue = perform(value, getName());
        }

        mState.assignRegister(destRegister, newValue);

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
        Object result = null;
        if (opName.startsWith("double")) {
            Double typedValue = (Double) value;
            if (opName.endsWith("float")) {
                result = typedValue.floatValue();
            } else if (opName.endsWith("int")) {
                result = typedValue.intValue();
            } else if (opName.endsWith("long")) {
                result = typedValue.longValue();
            }
        } else if (opName.startsWith("float")) {
            Float typedValue = (Float) value;
            if (opName.endsWith("double")) {
                result = typedValue.doubleValue();
            } else if (opName.endsWith("int")) {
                result = typedValue.intValue();
            } else if (opName.endsWith("long")) {
                result = typedValue.longValue();
            }
        } else if (opName.startsWith("long")) {
            Long typedValue = (Long) value;
            if (opName.endsWith("double")) {
                result = typedValue.doubleValue();
            } else if (opName.endsWith("int")) {
                result = typedValue.intValue();
            } else if (opName.endsWith("float")) {
                result = typedValue.floatValue();
            }
        } else if (opName.startsWith("int")) {
	    // 'Short's can actually be passed to 'int' functions
	    // which will cause exceptions unless we grab the intValue
	    // from them first
	    Integer typedValue = null;
	    if (value instanceof Short) {
		typedValue = ((Short) value).intValue();
	    } else {
		typedValue = (Integer) value;
	    }

            if (opName.endsWith("byte")) {
                result = typedValue.byteValue();
            } else if (opName.endsWith("char")) {
                result = (char) typedValue.intValue();
            } else if (opName.endsWith("short")) {
                result = typedValue.shortValue();
            } else if (opName.endsWith("double")) {
                result = typedValue.doubleValue();
            } else if (opName.endsWith("long")) {
                result = typedValue.longValue();
            } else if (opName.endsWith("float")) {
                result = typedValue.floatValue();
            }
        } else if (opName.startsWith("neg")) {
            if (opName.endsWith("double")) {
                Double typedValue = (Double) value;
                result = -typedValue;
            } else if (opName.endsWith("float")) {
                Float typedValue = (Float) value;
                result = -typedValue;
            } else if (opName.endsWith("int")) {
                Integer typedValue = (Integer) value;
                result = -typedValue;
            } else if (opName.endsWith("long")) {
                Long typedValue = (Long) value;
                result = -typedValue;
            }
        } else if (opName.startsWith("not")) {
            if (opName.endsWith("int")) {
                Integer typedValue = (Integer) value;
                result = ~typedValue;
            } else if (opName.endsWith("long")) {
                Long typedValue = (Long) value;
                result = ~typedValue;
            }
        }

        return result;
    }

}
