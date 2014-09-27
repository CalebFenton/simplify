package org.cf.smalivm.opcode;

import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.UnknownValue;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryMathOp extends MethodStateOp {

    private static final Logger log = LoggerFactory.getLogger(BinaryMathOp.class.getSimpleName());

    private static enum MathOperandType {
        INT,
        LONG,
        FLOAT,
        DOUBLE,
    };

    private static enum MathOperator {
        ADD,
        SUB,
        MUL,
        DIV,
        REM,
        AND,
        OR,
        XOR,
        SHR,
        SHL,
        USHR,
        RSUB,
    };

    private static Object doDoubleOperation(MathOperator mathOperator, Double lhs, Double rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doFloatOperation(MathOperator mathOperator, Float lhs, Float rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doIntegerOperation(MathOperator mathOperator, Integer lhs, Integer rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case AND:
            result = lhs & rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case OR:
            result = lhs | rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case RSUB:
            result = rhs - lhs;
            break;
        case SHL:
            result = lhs << (rhs & 0x1f);
            break;
        case SHR:
            result = lhs >> (rhs & 0x1f);
            break;
        case SUB:
            result = lhs - rhs;
            break;
        case USHR:
            result = lhs >>> (rhs & 0x1f);
            break;
        case XOR:
            result = lhs ^ rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static Object doLongOperation(MathOperator mathOperator, Long lhs, Long rhs) {
        Object result = null;
        switch (mathOperator) {
        case ADD:
            result = lhs + rhs;
            break;
        case AND:
            result = lhs & rhs;
            break;
        case DIV:
            result = lhs / rhs;
            break;
        case MUL:
            result = lhs * rhs;
            break;
        case OR:
            result = lhs | rhs;
            break;
        case REM:
            result = lhs % rhs;
            break;
        case SHL:
            result = lhs << rhs;
            break;
        case SHR:
            result = lhs >> rhs;
            break;
        case SUB:
            result = lhs - rhs;
            break;
        case USHR:
            result = lhs >>> rhs;
            break;
        case XOR:
            result = lhs ^ rhs;
            break;
        default:
            break;
        }

        return result;
    }

    private static MathOperator getMathOp(String opName) {
        MathOperator result = null;
        if (opName.startsWith("add")) {
            result = MathOperator.ADD;
        } else if (opName.startsWith("sub")) {
            result = MathOperator.SUB;
        } else if (opName.startsWith("mul")) {
            result = MathOperator.MUL;
        } else if (opName.startsWith("div")) {
            result = MathOperator.DIV;
        } else if (opName.startsWith("rem")) {
            result = MathOperator.REM;
        } else if (opName.startsWith("and")) {
            result = MathOperator.AND;
        } else if (opName.startsWith("or")) {
            result = MathOperator.OR;
        } else if (opName.startsWith("xor")) {
            result = MathOperator.XOR;
        } else if (opName.startsWith("shl")) {
            result = MathOperator.SHL;
        } else if (opName.startsWith("shr")) {
            result = MathOperator.SHR;
        } else if (opName.startsWith("ushr")) {
            result = MathOperator.USHR;
        } else if (opName.startsWith("rsub")) {
            result = MathOperator.RSUB;
        }

        return result;
    }

    private static MathOperandType getMathOperandType(String opName) {
        MathOperandType result = null;
        if (opName.contains("-int")) {
            result = MathOperandType.INT;
        } else if (opName.contains("-double")) {
            result = MathOperandType.DOUBLE;
        } else if (opName.contains("-float")) {
            result = MathOperandType.FLOAT;
        } else if (opName.contains("-long")) {
            result = MathOperandType.LONG;
        }

        return result;
    }

    static BinaryMathOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int childAddress = address + instruction.getCodeUnits();
        TwoRegisterInstruction instr = (TwoRegisterInstruction) instruction;
        int destRegister = instr.getRegisterA();
        int arg1Register = instr.getRegisterB();

        BinaryMathOp result = null;
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            int arg2Register = ((Instruction23x) instruction).getRegisterC();
            result = new BinaryMathOp(address, opName, childAddress, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            arg1Register = instr.getRegisterA();
            int arg2Register = ((Instruction12x) instruction).getRegisterB();
            result = new BinaryMathOp(address, opName, childAddress, destRegister, arg1Register, arg2Register, false);
        } else if (instruction instanceof NarrowLiteralInstruction) {
            // Instruction22b - add-int/lit8 vAA, vBB, #CC
            // Instruction22s - add-int/lit16 vAA, vBB, #CCCC
            int arg2Literal = ((NarrowLiteralInstruction) instruction).getNarrowLiteral();
            result = new BinaryMathOp(address, opName, childAddress, destRegister, arg1Register, arg2Literal, true);
        }

        return result;
    }

    private final MathOperator mathOperator;
    private final MathOperandType mathOperandType;
    private final int destRegister;
    private final int arg1Register;
    private int arg2Register;
    private int narrowLiteral;
    private boolean hasLiteral;

    private BinaryMathOp(int address, String opName, int childAddress, int destRegister, int arg1Register) {
        super(address, opName, childAddress);

        this.destRegister = destRegister;
        this.arg1Register = arg1Register;
        mathOperator = getMathOp(opName);
        mathOperandType = getMathOperandType(opName);
    }

    private BinaryMathOp(int address, String opName, int childAddress, int destRegister, int arg1Register,
                    int otherValue, boolean hasLiteral) {
        this(address, opName, childAddress, destRegister, arg1Register);

        this.hasLiteral = hasLiteral;
        if (hasLiteral) {
            narrowLiteral = otherValue;
        } else {
            arg2Register = otherValue;
        }
    }

    @Override
    public int[] execute(MethodState mctx) {
        Object lhs = mctx.readRegister(arg1Register);
        Object rhs = null;
        if (hasLiteral) {
            rhs = narrowLiteral;
        } else {
            rhs = mctx.readRegister(arg2Register);
        }

        Object result;
        if ((lhs instanceof UnknownValue) || (rhs instanceof UnknownValue)) {
            result = new UnknownValue("I");
        } else {
            result = getResult(lhs, rhs);
            if (result == null) {
                log.warn("Null result in binary math. Not possibruuu!");
            }
        }
        mctx.assignRegister(destRegister, result);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());
        sb.append(" r").append(destRegister).append(", ").append("r").append(arg1Register);
        if (hasLiteral) {
            sb.append(", 0x").append(Integer.toHexString(narrowLiteral));
        } else if (!getOpName().endsWith("/2addr")) {
            sb.append(", r").append(arg2Register);
        }

        return sb.toString();
    }

    private Object massageIntoInt(Object value) {
        Object result;
        if (value instanceof Character) {
            result = (int) (char) value;
        } else if (value instanceof Byte) {
            result = ((Byte) value).intValue();
        } else {
            result = value;
        }

        return result;
    }

    private Object getResult(Object lhs, Object rhs) {
        Object result = null;
        switch (mathOperandType) {
        case INT:
            lhs = massageIntoInt(lhs);
            rhs = massageIntoInt(rhs);
            result = doIntegerOperation(mathOperator, (Integer) lhs, (Integer) rhs);
            break;
        case LONG:
            result = doLongOperation(mathOperator, (Long) lhs, (Long) rhs);
            break;
        case FLOAT:
            result = doFloatOperation(mathOperator, (Float) lhs, (Float) rhs);
            break;
        case DOUBLE:
            result = doDoubleOperation(mathOperator, (Double) lhs, (Double) rhs);
            break;
        }

        return result;
    }

}
